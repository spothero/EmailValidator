/*
 * EmailValidatorTestCase
 *
 * Created by erickuck on 10/21/2013
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

package com.spothero.emailvalidator.test;

import android.test.AndroidTestCase;

import com.spothero.emailvalidator.EmailValidationResult;
import com.spothero.emailvalidator.EmailValidator;

import junit.framework.Assert;

public class EmailValidatorTestCase extends AndroidTestCase {

	public void testLevenshteinDistance() throws Throwable {
		LevenshteinDistanceTestModel[] tests = new LevenshteinDistanceTestModel[] {
				new LevenshteinDistanceTestModel("kitten", "sitting", 3),
				new LevenshteinDistanceTestModel("testing", "lev", 6),
				new LevenshteinDistanceTestModel("book", "back", 2),
				new LevenshteinDistanceTestModel("spot", "hero", 4),
				new LevenshteinDistanceTestModel("parking", "rules", 6),
				new LevenshteinDistanceTestModel("lame", "same", 1),
				new LevenshteinDistanceTestModel("same", "same", 0)
		};

		for (LevenshteinDistanceTestModel test : tests) {
			Assert.assertEquals(EmailValidator.getLevenshteinDistance(test.stringA.toCharArray(), test.stringB.toCharArray()), test.distance);
			Assert.assertEquals(EmailValidator.getLevenshteinDistance(test.stringB.toCharArray(), test.stringA.toCharArray()), test.distance);
		}
	}

	public void testSyntaxValidator() throws Throwable {
		ValidatorTestModel[] tests = new ValidatorTestModel[] {
				new ValidatorTestModel("test@email.com"),
				new ValidatorTestModel("test+-.test@email.com"),
				new ValidatorTestModel("test@.com", EmailValidationResult.ValidationError.INVALID_SYNTAX),
				new ValidatorTestModel("test.com", EmailValidationResult.ValidationError.INVALID_SYNTAX),
				new ValidatorTestModel("test@com", EmailValidationResult.ValidationError.INVALID_SYNTAX),
				new ValidatorTestModel("test@email.c", EmailValidationResult.ValidationError.INVALID_TLD),
				new ValidatorTestModel("test@email+.com", EmailValidationResult.ValidationError.INVALID_DOMAIN),
				new ValidatorTestModel("test&*\"@email.com", EmailValidationResult.ValidationError.INVALID_USERNAME)
		};

		for (ValidatorTestModel test : tests) {
			assertEquals(EmailValidator.validateSyntax(test.emailAddress), test.validationError);
		}
	}

	public void testEmailSuggestions() throws Throwable {
		ValidatorTestModel[] tests = new ValidatorTestModel[] {
				new ValidatorTestModel("test@gmail.com"),
				new ValidatorTestModel("test@yahoo.co.uk"),
				new ValidatorTestModel("test@googlemail.com"),
				new ValidatorTestModel("test@gamil.con", "test@gmail.com"),
				new ValidatorTestModel("test@yaho.com.uk", "test@yahoo.co.uk"),
				new ValidatorTestModel("test@yahooo.co.uk", "test@yahoo.co.uk"),
				new ValidatorTestModel("test@goglemail.coj", "test@googlemail.com"),
				new ValidatorTestModel("test@goglemail.com", "test@googlemail.com")
		};

		for (ValidatorTestModel test : tests) {
			assertEquals(EmailValidator.getAutocorrectSuggestion(test.emailAddress), test.suggestion);
		}
	}

}
