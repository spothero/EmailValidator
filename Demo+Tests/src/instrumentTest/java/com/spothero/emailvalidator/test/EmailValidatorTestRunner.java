package com.spothero.emailvalidator.test;

import android.test.InstrumentationTestRunner;
import android.test.InstrumentationTestSuite;

import junit.framework.TestSuite;

public class EmailValidatorTestRunner extends InstrumentationTestRunner {

	@Override
	public TestSuite getAllTests() {
		InstrumentationTestSuite suite = new InstrumentationTestSuite(this);
		suite.addTestSuite(EmailValidatorTestCase.class);
		return suite;
	}

	@Override
	public ClassLoader getLoader() {
		return EmailValidatorTestRunner.class.getClassLoader();
	}

}
