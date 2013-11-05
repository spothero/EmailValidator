/*
 * EmailValidationResult
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

public class EmailValidationResult {

	/**
	 * All possible errors that could be returned for syntax validation
	 */
	public enum ValidationError {
		/**
		 * The email address passed in was blank
		 */
		BLANK_ADDRESS,

		/**
		 * The email address passed in had a completely invalid syntax
		 */
		INVALID_SYNTAX,

		/**
		 * The email address passed in had an invalid username
		 */
		INVALID_USERNAME,

		/**
		 * The email address passed in had an invalid domain
		 */
		INVALID_DOMAIN,

		/**
		 * The email address passed in had an invalid TLD
		 */
		INVALID_TLD
	}

	/**
	 * True if the email address has a valid syntax, false if it does not
	 */
	public boolean passedValidation;

	/**
	 * A specific error for why the email address's syntax was invalid, or null
	 */
	public ValidationError validationError;

	/**
	 * A suggestion to fix a possible typo, or null if one is not available
	 */
	public String autocorrectSuggestion;

	public EmailValidationResult(boolean passed, ValidationError error, String suggestion) {
		passedValidation = passed;
		validationError = error;
		autocorrectSuggestion = suggestion;
	}
}
