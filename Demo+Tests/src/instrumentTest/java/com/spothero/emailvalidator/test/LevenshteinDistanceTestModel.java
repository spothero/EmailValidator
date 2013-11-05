package com.spothero.emailvalidator.test;

public class LevenshteinDistanceTestModel {

	public String stringA;
	public String stringB;
	public int distance;

	public LevenshteinDistanceTestModel(String stringA, String stringB, int distance) {
		this.stringA = stringA;
		this.stringB = stringB;
		this.distance = distance;
	}
}
