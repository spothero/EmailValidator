package com.spothero.emailvalidator.test;

import com.spothero.emailvalidator.EmailValidationResult;

public class ValidatorTestModel {

	public String emailAddress;
	public String suggestion;
	public EmailValidationResult.ValidationError validationError;

	public ValidatorTestModel(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public ValidatorTestModel(String emailAddress, EmailValidationResult.ValidationError validationError) {
		this.emailAddress = emailAddress;
		this.validationError = validationError;
	}

	public ValidatorTestModel(String emailAddress, String suggestion) {
		this.emailAddress = emailAddress;
		this.suggestion = suggestion;
	}
}
