/*
 * EmailValidator
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

import com.spothero.emailvalidator.EmailValidationResult.ValidationError;

import java.util.Arrays;
import java.util.List;

public class EmailValidator {

	// All TLDs registered with IANA as of Sat Oct 12 07:07:01 2013 UTC (latest list at: http://data.iana.org/TLD/tlds-alpha-by-domain.txt)
	private static final List<String> IANA_REGISTERED_TLDS = Arrays.asList("ac", "ad", "ae", "aero", "af", "ag", "ai", "al", "am", "an", "ao", "aq", "ar", "arpa", "as", "asia", "at", "au", "aw", "ax", "az", "ba", "bb", "bd", "be", "bf", "bg", "bh", "bi", "biz", "bj", "bm", "bn", "bo", "br", "bs", "bt", "bv", "bw", "by", "bz", "ca", "cat", "cc", "cd", "cf", "cg", "ch", "ci", "ck", "cl", "cm", "cn", "co", "com", "coop", "cr", "cu", "cv", "cw", "cx", "cy", "cz", "de", "dj", "dk", "dm", "do", "dz", "ec", "edu", "ee", "eg", "er", "es", "et", "eu", "fi", "fj", "fk", "fm", "fo", "fr", "ga", "gb", "gd", "ge", "gf", "gg", "gh", "gi", "gl", "gm", "gn", "gov", "gp", "gq", "gr", "gs", "gt", "gu", "gw", "gy", "hk", "hm", "hn", "hr", "ht", "hu", "id", "ie", "il", "im", "in", "info", "int", "io", "iq", "ir", "is", "it", "je", "jm", "jo", "jobs", "jp", "ke", "kg", "kh", "ki", "km", "kn", "kp", "kr", "kw", "ky", "kz", "la", "lb", "lc", "li", "lk", "lr", "ls", "lt", "lu", "lv", "ly", "ma", "mc", "md", "me", "mg", "mh", "mil", "mk", "ml", "mm", "mn", "mo", "mobi", "mp", "mq", "mr", "ms", "mt", "mu", "museum", "mv", "mw", "mx", "my", "mz", "na", "name", "nc", "ne", "net", "nf", "ng", "ni", "nl", "no", "np", "nr", "nu", "nz", "om", "org", "pa", "pe", "pf", "pg", "ph", "pk", "pl", "pm", "pn", "post", "pr", "pro", "ps", "pt", "pw", "py", "qa", "re", "ro", "rs", "ru", "rw", "sa", "sb", "sc", "sd", "se", "sg", "sh", "si", "sj", "sk", "sl", "sm", "sn", "so", "sr", "st", "su", "sv", "sx", "sy", "sz", "tc", "td", "tel", "tf", "tg", "th", "tj", "tk", "tl", "tm", "tn", "to", "tp", "tr", "travel", "tt", "tv", "tw", "tz", "ua", "ug", "uk", "us", "uy", "uz", "va", "vc", "ve", "vg", "vi", "vn", "vu", "wf", "ws", "xn", "xxx", "ye", "yt", "za", "zm", "zw");
	private static final List<String> COMMON_TLDS = Arrays.asList("com", "net", "ru", "org", "de", "uk", "jp", "ca", "fr", "au", "br", "us", "info", "cn", "dk", "edu", "gov", "mil", "ch", "it", "nl", "se", "no", "es", "me");
	private static final List<String> COMMON_DOMAINS = Arrays.asList("gmail.com", "googlemail.com", "yahoo.com", "yahoo.co.uk", "yahoo.co.in", "yahoo.ca", "ymail.com", "hotmail.com", "hotmail.co.uk", "msn.com", "live.com", "outlook.com", "comcast.net", "sbcglobal.net", "bellsouth.net", "verizon.net", "earthlink.net", "cox.net", "rediffmail.com", "charter.net", "facebook.com", "mail.com", "gmx.com", "aol.com", "att.net", "mac.com", "rocketmail.com");

	/**
	 * Performs simple validation on the email address. If the validation check passes, a typo check will be performed.
	 *
	 * @param emailAddress The email address to check
	 * @return An {@link com.spothero.emailvalidator.EmailValidationResult} that contains data on if the email address passed validation, any errors that occurred, and a typo suggestion if applicable.
	 *
	 */
	public static EmailValidationResult validateAndAutocorrect(String emailAddress) {
		String[] addressParts = getEmailAddressParts(emailAddress);

		ValidationError validationError = validateSyntax(emailAddress, addressParts);
		if (validationError != null) {
			return new EmailValidationResult(false, validationError, null);
		} else {
			return new EmailValidationResult(true, null, getAutocorrectSuggestion(emailAddress, addressParts));
		}
	}

	private static ValidationError validateSyntax(String emailAddress, String[] addressParts) {
		if (emailAddress == null || emailAddress.length() == 0) {
			return ValidationError.BLANK_ADDRESS;
		}

		if (!emailAddress.matches("^\\b.+@.+\\..+\\b$")) {
			return ValidationError.INVALID_SYNTAX;
		}

		String username = addressParts[0];
		String domain = addressParts[1];
		String tld = addressParts[2];

		if (username.length() == 0 || !username.matches("[A-Z0-9a-z.!#$%&'*+-/=?^_`{|}~]+") || username.startsWith(".") || username.endsWith(".") || username.contains("..")) {
			return ValidationError.INVALID_USERNAME;
		} else if (domain.length() == 0 || !domain.matches("[A-Z0-9a-z.-]+")) {
			return ValidationError.INVALID_DOMAIN;
		} else if (!tld.matches("[A-Za-z][A-Z0-9a-z-]{0,22}[A-Z0-9a-z]")) {
			return ValidationError.INVALID_TLD;
		} else {
			return null;
		}
	}

	/**
	 * Performs basic syntax validation on the email address
	 * @param emailAddress The email address to check
	 * @return A {@link com.spothero.emailvalidator.EmailValidationResult.ValidationError} explaining an error that occurred, or null if the email address passed validation.
	 */
	public static ValidationError validateSyntax(String emailAddress) {
		return validateSyntax(emailAddress, getEmailAddressParts(emailAddress));
	}

	private static String getAutocorrectSuggestion(String emailAddress, String[] addressParts) {
		String username = addressParts[0];
		String domain = addressParts[1];
		String tld = addressParts[2];

		String suggestedTLD = null;
		if (!IANA_REGISTERED_TLDS.contains(tld)) {
			suggestedTLD = getClosestString(tld, COMMON_TLDS, 0.5f);
		}
		if (suggestedTLD == null) {
			suggestedTLD = tld;
		}

		String fullDomain = domain + "." + suggestedTLD;
		String suggestedDomain = null;
		if (!COMMON_DOMAINS.contains(fullDomain)) {
			suggestedDomain = getClosestString(fullDomain, COMMON_DOMAINS, 0.25f);
		}
		if (suggestedDomain == null) {
			suggestedDomain = fullDomain;
		}

		String suggestedEmailAddress = username + "@" + suggestedDomain;
		if (suggestedEmailAddress.equals(emailAddress)) {
			return null;
		} else {
			return suggestedEmailAddress;
		}
	}

	/**
	 * Returns a typo suggestion if one is available
	 * @param emailAddress The email address to check
	 * @return A typo suggestion, or null if one couldn't be found
	 */
	public static String getAutocorrectSuggestion(String emailAddress) {
		String[] addressParts = getEmailAddressParts(emailAddress);

		if (validateSyntax(emailAddress, addressParts) != null) {
			return null;
		} else {
			return getAutocorrectSuggestion(emailAddress, addressParts);
		}
	}

	private static String getClosestString(String string, List<String> list, float tolerance) {
		String closestString = null;
		float closestDistance = Float.MAX_VALUE;

		for (String listString : list) {
			float distance = getLevenshteinDistance(string.toCharArray(), listString.toCharArray());
			if (distance < closestDistance && ((distance / string.length()) < tolerance)) {
				closestDistance = distance;
				closestString = listString;
			}
		}

		return closestString;
	}

	public static int getLevenshteinDistance(char[] a, char[] b) {
		final int aLength = a.length;
		final int bLength = b.length;
		int[][] distance = new int[aLength + 1][bLength + 1];

		for (int i = 0; i <= aLength; i++) {
			distance[i][0] = i;
		}
		for (int j = 1; j <= bLength; j++) {
			distance[0][j] = j;
		}

		for (int i = 1; i <= aLength; i++) {
			for (int j = 1; j <= bLength; j++) {
				boolean match = a[i - 1] == b[j - 1];
				distance[i][j] = Math.min(distance[i - 1][j] + 1, Math.min(distance[i][j - 1] + 1, distance[i - 1][j - 1] + (match ? 0 : 1)));
			}
		}

		return distance[aLength][bLength];
	}

	private static String[] getEmailAddressParts(String emailAddress) {
		String username = "";
		String domain = "";
		String tld = "";

		int atIndex = emailAddress.indexOf('@');
		if (atIndex >= 0) {
			username = emailAddress.substring(0, atIndex);

			int periodIndex = emailAddress.lastIndexOf('.');
			if (periodIndex >= 0 && periodIndex > atIndex) {
				domain = emailAddress.substring(atIndex + 1, periodIndex);
				tld = emailAddress.substring(periodIndex + 1);
			}
		}

		return new String[] { username, domain, tld };
	}
}
