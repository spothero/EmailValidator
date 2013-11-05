/*
 * EmailValidationEditText
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
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

public class EmailValidationEditText extends EditText {

	private final EmailValidationOnFocusChangeListener mOnFocusChangeListener = new EmailValidationOnFocusChangeListener();
	private final Map<EmailValidationResult.ValidationError, String> mErrorMessages = new HashMap<EmailValidationResult.ValidationError, String>();
	private final int[] mLocationArray = new int[2];
	private final AutocorrectSuggestionPopup.OnDismissListener mOnDismissListener = new AutocorrectSuggestionPopup.OnDismissListener() {
		@Override
		public void onDismiss(boolean suggestionAccepted) {
			if (suggestionAccepted) {
				setText(mAutocorrectSuggestionPopup.getSuggestion());
			}
		}
	};
	private final ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
		@Override
		public void onGlobalLayout() {
			Rect displayFrame = new Rect();
			getWindowVisibleDisplayFrame(displayFrame);
			getLocationOnScreen(mLocationArray);

			if (mAutocorrectSuggestionPopup.isShowing()) {
				mAutocorrectSuggestionPopup.update(EmailValidationEditText.this, mLocationArray);
			}

			if (mNeedsPopupDisplay) {
				mAutocorrectSuggestionPopup.showFromView(EmailValidationEditText.this, mPopupTitle, mPopupSuggestion);
				mNeedsPopupDisplay = false;
			}
		}
	};
	private final ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
		@Override
		public void onScrollChanged() {
			if (mAutocorrectSuggestionPopup.isShowing()) {
				getLocationOnScreen(mLocationArray);
				mAutocorrectSuggestionPopup.update(EmailValidationEditText.this, mLocationArray);
			}
		}
	};

	private AutocorrectSuggestionPopup mAutocorrectSuggestionPopup;
	private String mDefaultErrorMessage;
	private String mMessageForSuggestion;
	private boolean mNeedsPopupDisplay;
	private String mPopupTitle;
	private String mPopupSuggestion;

	public EmailValidationEditText(Context context) {
		super(context);
		commonInit(context, null);
	}

	public EmailValidationEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		commonInit(context, attrs);
	}

	public EmailValidationEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		commonInit(context, attrs);
	}

	private void commonInit(Context context, AttributeSet attrs) {
		setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		setImeOptions(getImeOptions());
		setOnFocusChangeListener(mOnFocusChangeListener);
		mAutocorrectSuggestionPopup = new AutocorrectSuggestionPopup(getContext());
		mAutocorrectSuggestionPopup.setOnDismissListener(mOnDismissListener);
		mDefaultErrorMessage = "Please enter a valid email address";
		mMessageForSuggestion = "Did you mean";
		ViewTreeObserver viewTreeObserver = getViewTreeObserver();
		viewTreeObserver.addOnGlobalLayoutListener(mOnGlobalLayoutListener);
		viewTreeObserver.addOnScrollChangedListener(mOnScrollChangedListener);

		if (attrs != null) {
			TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EmailValidationEditText, 0, 0);
			if (a.hasValue(R.styleable.EmailValidationEditText_popupBorderColor)) {
				mAutocorrectSuggestionPopup.setBorderColor(a.getColor(R.styleable.EmailValidationEditText_popupBorderColor, 0));
			}
			if (a.hasValue(R.styleable.EmailValidationEditText_popupFillColor)) {
				mAutocorrectSuggestionPopup.setFillColor(a.getColor(R.styleable.EmailValidationEditText_popupFillColor, 0));
			}
			if (a.hasValue(R.styleable.EmailValidationEditText_popupTitleColor)) {
				mAutocorrectSuggestionPopup.setTitleColor(a.getColor(R.styleable.EmailValidationEditText_popupTitleColor, 0));
			}
			if (a.hasValue(R.styleable.EmailValidationEditText_popupSuggestionColor)) {
				mAutocorrectSuggestionPopup.setSuggestionColor(a.getColor(R.styleable.EmailValidationEditText_popupSuggestionColor, 0));
			}
			if (a.hasValue(R.styleable.EmailValidationEditText_popupDismissButtonColor)) {
				mAutocorrectSuggestionPopup.setDismissButtonColor(a.getColor(R.styleable.EmailValidationEditText_popupDismissButtonColor, 0));
			}
			if (a.hasValue(R.styleable.EmailValidationEditText_defaultErrorMessage)) {
				mDefaultErrorMessage = a.getString(R.styleable.EmailValidationEditText_defaultErrorMessage);
			}
			if (a.hasValue(R.styleable.EmailValidationEditText_suggestionTitle)) {
				mMessageForSuggestion = a.getString(R.styleable.EmailValidationEditText_suggestionTitle);
			}
			if (a.hasValue(R.styleable.EmailValidationEditText_invalidSyntaxError)) {
				mErrorMessages.put(EmailValidationResult.ValidationError.INVALID_SYNTAX, a.getString(R.styleable.EmailValidationEditText_invalidSyntaxError));
			}
			if (a.hasValue(R.styleable.EmailValidationEditText_invalidUsernameError)) {
				mErrorMessages.put(EmailValidationResult.ValidationError.INVALID_USERNAME, a.getString(R.styleable.EmailValidationEditText_invalidUsernameError));
			}
			if (a.hasValue(R.styleable.EmailValidationEditText_invalidDomainError)) {
				mErrorMessages.put(EmailValidationResult.ValidationError.INVALID_DOMAIN, a.getString(R.styleable.EmailValidationEditText_invalidDomainError));
			}
			if (a.hasValue(R.styleable.EmailValidationEditText_invalidTLDError)) {
				mErrorMessages.put(EmailValidationResult.ValidationError.INVALID_TLD, a.getString(R.styleable.EmailValidationEditText_invalidTLDError));
			}

			a.recycle();
		}
	}

	@Override
	public void setOnFocusChangeListener(OnFocusChangeListener l) {
		if (l == mOnFocusChangeListener) {
			super.setOnFocusChangeListener(l);
		} else {
			mOnFocusChangeListener.setSubOnFocusChangeListener(l);
		}
	}

	/**
	 * Sets the error message that will appear for errors that haven't had specific messages applied to them using the {@link #setMessageForError(int, com.spothero.emailvalidator.EmailValidationResult.ValidationError)} method
	 * @param resId A string resource ID
	 */
	public void setDefaultErrorMessage(int resId) {
		setDefaultErrorMessage(getContext().getString(resId));
	}

	/**
	 * Sets the error message that will appear for errors that haven't had specific messages applied to them using the {@link #setMessageForError(int, com.spothero.emailvalidator.EmailValidationResult.ValidationError)} method
	 * @param defaultErrorMessage A String
	 */
	public void setDefaultErrorMessage(String defaultErrorMessage) {
		mDefaultErrorMessage = defaultErrorMessage;
	}

	/**
	 * Sets the message that will be displayed above typo suggestions
	 * @param resId A string resource ID
	 */
	public void setMessageForSuggestion(int resId) {
		setMessageForSuggestion(getContext().getString(resId));
	}

	/**
	 * Sets the message that will be displayed above typo suggestions
	 * @param messageForSuggestion A String
	 */
	public void setMessageForSuggestion(String messageForSuggestion) {
		mMessageForSuggestion = messageForSuggestion;
	}

	/**
	 * Sets the message that will be shown when the given validation error occurs
	 * @param resId A string resource ID
	 * @param error The error for which the error message will be displayed
	 */
	public void setMessageForError(int resId, EmailValidationResult.ValidationError error) {
		setMessageForError(getContext().getString(resId), error);
	}

	/**
	 * Sets the message that will be shown when the given validation error occurs
	 * @param message A String
	 * @param error The error for which the error message will be displayed
	 */
	public void setMessageForError(String message, EmailValidationResult.ValidationError error) {
		mErrorMessages.put(error, message);
	}

	@Override
	public void setImeOptions(int imeOptions) {
		super.setImeOptions(imeOptions | EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);
	}

	/**
	 * Validates the input and displays error messages or typo suggestions if needed
	 */
	public void validateInput() {
		String input = getText().toString();
		if (input.length() > 0) {
			EmailValidationResult result = EmailValidator.validateAndAutocorrect(input);

			if (!result.passedValidation) {
				String errorMessage;
				if (mErrorMessages.containsKey(result.validationError)) {
					errorMessage = mErrorMessages.get(result.validationError);
				} else {
					errorMessage = mDefaultErrorMessage;
				}
				mAutocorrectSuggestionPopup.showFromView(this, errorMessage, null);
			} else if (result.autocorrectSuggestion != null) {
				mAutocorrectSuggestionPopup.showFromView(this, mMessageForSuggestion, result.autocorrectSuggestion);
			}
		}
	}

	/**
	 * Dismisses any error/typo suggestion popups that are showing
	 */
	public void dismissPopup() {
		if (mAutocorrectSuggestionPopup.isShowing()) {
			mAutocorrectSuggestionPopup.dismiss();
		}
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState ss = new SavedState(superState);
		ss.popupIsShowing = mAutocorrectSuggestionPopup.isShowing();
		ss.popupTitle = mAutocorrectSuggestionPopup.getTitle();
		ss.popupSuggestion = mAutocorrectSuggestionPopup.getSuggestion();
		return ss;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if ((state instanceof SavedState)) {
			SavedState savedState = (SavedState)state;
			super.onRestoreInstanceState(savedState.getSuperState());

			if (savedState.popupIsShowing) {
				mNeedsPopupDisplay = true;
				mPopupTitle = savedState.popupTitle;
				mPopupSuggestion = savedState.popupSuggestion;
			}
		} else {
			super.onRestoreInstanceState(state);
		}
	}

	static class SavedState extends BaseSavedState {
		boolean popupIsShowing;
		String popupTitle;
		String popupSuggestion;

		SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			popupIsShowing = in.readInt() == 1;
			if (popupIsShowing) {
				popupTitle = in.readString();
				popupSuggestion = in.readString();

				if (popupTitle.equals("")) {
					popupTitle = null;
				}
				if (popupSuggestion.equals("")) {
					popupSuggestion = null;
				}
			}
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeInt(popupIsShowing ? 1 : 0);
			if (popupTitle != null) {
				out.writeString(popupTitle);
			} else {
				out.writeString("");
			}

			if (popupSuggestion != null) {
				out.writeString(popupSuggestion);
			} else {
				out.writeString("");
			}
		}

		public static final Parcelable.Creator<SavedState> CREATOR =
				new Parcelable.Creator<SavedState>() {
					public SavedState createFromParcel(Parcel in) {
						return new SavedState(in);
					}
					public SavedState[] newArray(int size) {
						return new SavedState[size];
					}
				};
	}

	private class EmailValidationOnFocusChangeListener implements OnFocusChangeListener {
		private OnFocusChangeListener mSubOnFocusChangeListener;

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (mSubOnFocusChangeListener != null) {
				mSubOnFocusChangeListener.onFocusChange(v, hasFocus);
			}
			if (!hasFocus) {
				validateInput();
			} else {
				dismissPopup();
			}
		}

		public void setSubOnFocusChangeListener(OnFocusChangeListener subOnFocusChangeListener) {
			mSubOnFocusChangeListener = subOnFocusChangeListener;
		}
	}
}
