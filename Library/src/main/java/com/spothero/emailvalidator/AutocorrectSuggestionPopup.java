/*
 * AutocorrectSuggestionPopup
 *
 * Created by erickuck on 10/18/2013
 * Copyright (C) 2013 SpotHero
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.spothero.emailvalidator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class AutocorrectSuggestionPopup {

	public interface OnDismissListener {
		public void onDismiss(boolean suggestionAccepted);
	}

	public static final String TAG = "EmailValidator";

	private Context mContext;
	private WindowManager mWindowManager;
	private String mSuggestion;
	private String mTitle;
	private int mAnchorX;
	private int mAnchorY;
	private boolean mIsShowing;
	private OnDismissListener mOnDismissListener;
	private final SuggestionPopupView mSuggestionPopupView;
	private final OnDismissListener mLocalOnDismissListener = new OnDismissListener() {
		@Override
		public void onDismiss(boolean suggestionAccepted) {
			if (mOnDismissListener != null) {
				dismiss();
				mOnDismissListener.onDismiss(suggestionAccepted);
			}
		}
	};

	public AutocorrectSuggestionPopup(Context context) {
		mContext = context;
		mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		mSuggestionPopupView = new SuggestionPopupView(context);
		mSuggestionPopupView.setOnDismissListener(mLocalOnDismissListener);
	}

	public boolean isShowing() {
		return mIsShowing;
	}

	public void update(View anchor, int[] location) {
		if (!mIsShowing) {
			return;
		}

		if (mAnchorX != location[0] || mAnchorY != location[1]) {
			mAnchorX = location[0];
			mAnchorY = location[1];

			int x = getXFromAnchor(anchor);
			int y = getYFromAnchor();

			WindowManager.LayoutParams p = (WindowManager.LayoutParams)mSuggestionPopupView.getLayoutParams();
			if (p.x != x || p.y != y) {
				p.x = x;
				p.y = y;
				mWindowManager.updateViewLayout(mSuggestionPopupView, p);
			}
		}
	}

	public void showFromView(View anchor, String title, String suggestion) {
		if (mIsShowing) {
			dismiss();
		}

		mIsShowing = true;

		mTitle = title;
		mSuggestion = suggestion;
		mSuggestionPopupView.setText(title, suggestion);
		mSuggestionPopupView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		int[] location = new int[2];
		anchor.getLocationOnScreen(location);

		mAnchorX = location[0];
		mAnchorY = location[1];

		WindowManager.LayoutParams p = new WindowManager.LayoutParams();
		p.packageName = mContext.getPackageName();
		p.gravity = Gravity.LEFT | Gravity.TOP;
		p.width = WindowManager.LayoutParams.WRAP_CONTENT;
		p.height = WindowManager.LayoutParams.WRAP_CONTENT;
		p.format = PixelFormat.TRANSLUCENT;
		p.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
		p.token = anchor.getWindowToken();
		p.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED;
		p.setTitle("PopupWindow:" + Integer.toHexString(hashCode()));
		p.windowAnimations = android.R.style.Animation_Dialog;
		p.gravity = Gravity.TOP | Gravity.LEFT;
		p.x = getXFromAnchor(anchor);
		p.y = getYFromAnchor();

		p.flags &= ~(
				WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES |
						WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
						WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
						WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
						WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
						WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		p.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |  WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

		mWindowManager.addView(mSuggestionPopupView, p);
	}

	public void dismiss() {
		if (mIsShowing) {
			mIsShowing = false;

			try {
				mWindowManager.removeViewImmediate(mSuggestionPopupView);
			} catch (Exception e) {
				Log.e(TAG, "Tried to dismiss invalid popup");
			}
		}
	}

	private int getXFromAnchor(View anchor) {
		return mAnchorX + anchor.getMeasuredWidth() / 2 - mSuggestionPopupView.getMeasuredWidth() / 2;
	}

	private int getYFromAnchor() {
		return mAnchorY - mSuggestionPopupView.getMeasuredHeight() + mSuggestionPopupView.getArrowHeight() / 2;
	}

	public void setOnDismissListener(OnDismissListener onDismissListener) {
		mOnDismissListener = onDismissListener;
	}

	public String getTitle() {
		return mTitle;
	}

	public String getSuggestion() {
		return mSuggestion;
	}

	public void setBorderColor(int borderColor) {
		mSuggestionPopupView.setBorderColor(borderColor);
	}

	public void setFillColor(int fillColor) {
		mSuggestionPopupView.setFillColor(fillColor);
	}

	public void setTitleColor(int titleColor) {
		mSuggestionPopupView.setTitleColor(titleColor);
	}

	public void setSuggestionColor(int suggestionColor) {
		mSuggestionPopupView.setSuggestionColor(suggestionColor);
	}

	public void setDismissButtonColor(int dismissButtonColor) {
		mSuggestionPopupView.setDismissButtonColor(dismissButtonColor);
	}

	private static class SuggestionPopupView extends View {
		private static final int ARROW_HEIGHT = 16;
		private static final int ARROW_WIDTH = 14;
		private static final int DISMISS_WIDTH = 40;
		private static final int MIN_HEIGHT = 36 + ARROW_HEIGHT;

		private final Paint mPaint;
		private final Paint mTitlePaint;
		private final Paint mSuggestionPaint;
		private final Rect mTitleRect;
		private final Rect mSuggestionRect;
		private final RectF mContentRect;
		private final int mDismissButtonWidth;
		private final int mArrowHeight;
		private final int mArrowWidth;
		private final int mPadding;
		private final Path mPath;
		private final float mDensity;
		private final float mBorderWidth;
		private final int mMinHeight;
		private String mTitle;
		private String mSuggestion;
		private OnDismissListener mOnDismissListener;

		private int mBorderColor;
		private int mFillColor;
		private int mDismissButtonColor;

		private SuggestionPopupView(Context context) {
			super(context);

			mDensity = context.getResources().getDisplayMetrics().density;

			mTitleRect = new Rect();
			mSuggestionRect = new Rect();
			mContentRect = new RectF();
			mPath = new Path();

			mPaint = new Paint();
			mPaint.setStrokeJoin(Paint.Join.MITER);
			mPaint.setStrokeCap(Paint.Cap.SQUARE);
			mPaint.setAntiAlias(true);

			mTitlePaint = new TextPaint();
			mTitlePaint.setTextSize((int)(mDensity * 16));
			mTitlePaint.setColor(Color.BLACK);
			mTitlePaint.setTextAlign(Paint.Align.CENTER);

			mSuggestionPaint = new TextPaint();
			mSuggestionPaint.setTextSize((int) (mDensity * 16));
			mSuggestionPaint.setColor(0xFF7F7FFF);

			mArrowHeight = (int)(ARROW_HEIGHT * mDensity);
			mArrowWidth = (int)(ARROW_WIDTH * mDensity);
			mDismissButtonWidth = (int)(DISMISS_WIDTH * mDensity);
			mPadding = (int)(8 * mDensity);
			mBorderWidth = (int)(2 * mDensity);
			mMinHeight = (int)(MIN_HEIGHT * mDensity);

			mBorderColor = Color.RED;
			mFillColor = Color.WHITE;
			mDismissButtonColor = 0xFFAAAAAA;
		}

		public void setBorderColor(int borderColor) {
			mBorderColor = borderColor;
		}

		public void setFillColor(int fillColor) {
			mFillColor = fillColor;
		}

		public void setTitleColor(int titleColor) {
			mTitlePaint.setColor(titleColor);
		}

		public void setSuggestionColor(int suggestionColor) {
			mSuggestionPaint.setColor(suggestionColor);
		}

		public void setDismissButtonColor(int dismissButtonColor) {
			mDismissButtonColor = dismissButtonColor;
		}

		public void setText(String title, String suggestion) {
			mTitle = title;
			mSuggestion = suggestion;

			mTitlePaint.getTextBounds(title, 0, title.length(), mTitleRect);
			if (suggestion != null) {
				mSuggestionPaint.getTextBounds(suggestion, 0, suggestion.length(), mSuggestionRect);
			} else {
				mSuggestionRect.set(0, 0, 0, 0);
			}
		}

		public void setOnDismissListener(OnDismissListener onDismissListener) {
			mOnDismissListener = onDismissListener;
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int width = mDismissButtonWidth + Math.max(mTitleRect.width(), mSuggestionRect.width()) + mPadding * 2 + (int)(mBorderWidth * 2);
			int height = Math.max(mMinHeight, mTitleRect.height() + mSuggestionRect.height() + mPadding * 2 + mArrowHeight) + (int)(mBorderWidth * 2);

			mContentRect.set(mBorderWidth, mBorderWidth, width - mBorderWidth, height - mArrowHeight - mBorderWidth);

			setMeasuredDimension(width, height);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			mPath.reset();
			mPath.moveTo((mContentRect.right - mArrowWidth) / 2, mContentRect.bottom);
			mPath.lineTo(mContentRect.centerX(), getMeasuredHeight() - mBorderWidth);
			mPath.lineTo((mContentRect.right + mArrowWidth) / 2, mContentRect.bottom);
			mPath.lineTo(mContentRect.right, mContentRect.bottom);
			mPath.lineTo(mContentRect.right, mContentRect.top);
			mPath.lineTo(mContentRect.left, mContentRect.top);
			mPath.lineTo(mContentRect.left, mContentRect.bottom);
			mPath.close();

			mPaint.setColor(mFillColor);
			mPaint.setStyle(Paint.Style.FILL);
			canvas.drawPath(mPath, mPaint);

			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth((int)(1 * mDensity));

			mPaint.setColor(mDismissButtonColor);
			canvas.drawLine(mContentRect.right - mDismissButtonWidth, mContentRect.top, mContentRect.right - mDismissButtonWidth, mContentRect.bottom, mPaint);

			mPaint.setStrokeWidth(mBorderWidth);
			mPaint.setColor(mBorderColor);
			canvas.drawPath(mPath, mPaint);

			if (mSuggestion == null) {
				canvas.drawText(mTitle, (mContentRect.width() - mDismissButtonWidth) / 2, mContentRect.top + (mContentRect.height() + mTitleRect.height()) / 2, mTitlePaint);
			} else {
				canvas.drawText(mTitle, (mContentRect.width() - mDismissButtonWidth) / 2, mContentRect.top + mPadding + mTitleRect.height() - mTitlePaint.descent(), mTitlePaint);
				canvas.drawText(mSuggestion, mPadding, mContentRect.top + mPadding + mTitleRect.height() + mSuggestionRect.height() - mSuggestionPaint.descent(), mSuggestionPaint);
			}

			mPaint.setStrokeWidth((int) (mDensity * 4));
			mPaint.setColor(mDismissButtonColor);

			final int xSize = mDismissButtonWidth / 3;
			final int xLeft = (int)(mContentRect.right - mDismissButtonWidth + (mDismissButtonWidth - xSize) / 2 - mBorderWidth / 4);
			final int xTop = (int)((mContentRect.top + mContentRect.height() - xSize) / 2);
			canvas.drawLine(xLeft, xTop, xLeft + xSize, xTop + xSize, mPaint);
			canvas.drawLine(xLeft, xTop + xSize, xLeft + xSize, xTop, mPaint);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
				float x = event.getX();
				float y = event.getY();

				if (y >= 0 && y < mContentRect.height() && x >= 0 && x < mContentRect.width()) {
					if (mOnDismissListener != null) {
						if (x < getMeasuredWidth() - mDismissButtonWidth) {
							if (mSuggestion != null) {
								mOnDismissListener.onDismiss(true);
							}
						} else {
							mOnDismissListener.onDismiss(false);
						}
					}
					return true;
				} else {
					return false;
				}
			} else {
				return super.onTouchEvent(event);
			}
		}

		public int getArrowHeight() {
			return mArrowHeight;
		}
	}
}
