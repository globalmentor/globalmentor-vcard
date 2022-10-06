/*
 * Copyright © 1996-2012 GlobalMentor, Inc. <https://www.globalmentor.com/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.globalmentor.text.directory.vcard;

import java.io.IOException;
import java.util.*;

import static java.util.Objects.*;

import static com.globalmentor.java.Conditions.*;
import static com.globalmentor.text.TextFormatter.*;

import com.globalmentor.model.DefaultLocaleable;

/**
 * An object representing the "N" type of a vCard <code>text/directory</code> profile as defined in <a href="https://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
 * "vCard MIME Directory Profile".
 * @author Garret Wilson
 */
public class Name extends DefaultLocaleable {

	/** The family names. */
	private String[] familyNames;

	/** @return The family names. */
	public String[] getFamilyNames() {
		return familyNames;
	}

	/** @return The last family name, or <code>null</code> if there are no family names. */
	public String getFamilyName() {
		return familyNames.length > 0 ? familyNames[familyNames.length - 1] : null;
	}

	/**
	 * Sets the family names.
	 * @param familyNames The family names.
	 * @throws NullPointerException if the provided family names is <code>null</code>.
	 */
	public void setFamilyNames(final String... familyNames) {
		this.familyNames = requireNonNull(familyNames, "Family names cannot be null.");
	}

	/** The given names. */
	private String[] givenNames;

	/** @return The given names. */
	public String[] getGivenNames() {
		return givenNames;
	}

	/** @return The first given name, or <code>null</code> if there are no given names. */
	public String getGivenName() {
		return givenNames.length > 0 ? givenNames[0] : null;
	}

	/**
	 * Sets the given names.
	 * @param givenNames The given names.
	 * @throws NullPointerException if the provided given names is <code>null</code>.
	 */
	public void setGivenNames(final String... givenNames) {
		this.givenNames = requireNonNull(givenNames, "Given names cannot be null.");
	}

	/** The additional names. */
	private String[] additionalNames;

	/** @return The additional names. */
	public String[] getAdditionalNames() {
		return additionalNames;
	}

	/** @return The first additional name, or <code>null</code> if there are no additional names. */
	public String getAdditionalName() {
		return additionalNames.length > 0 ? additionalNames[0] : null;
	}

	/**
	 * Sets the additional names.
	 * @param additionalNames The additional names.
	 * @throws NullPointerException if the provided additional names is <code>null</code>.
	 */
	public void setAdditionalNames(final String... additionalNames) {
		this.additionalNames = requireNonNull(additionalNames, "Additional names cannot be null");
	}

	/** The honorific prefixes. */
	private String[] honorificPrefixes;

	/** @return The honorific prefixes. */
	public String[] getHonorificPrefixes() {
		return honorificPrefixes;
	}

	/** @return The first honorific prefix, or <code>null</code> if there are no honorific prefixes. */
	public String getHonorificPrefix() {
		return honorificPrefixes.length > 0 ? honorificPrefixes[0] : null;
	}

	/**
	 * Sets the honorific prefixes.
	 * @param honorificPrefixes The honorific prefixes.
	 * @throws NullPointerException if the provided honorific prefixes is <code>null</code>.
	 */
	public void setHonorificPrefixes(final String... honorificPrefixes) {
		this.honorificPrefixes = requireNonNull(honorificPrefixes, "Honorific prefixes cannot be null.");
	}

	/** The honorific suffixes. */
	private String[] honorificSuffixes;

	/** @return The honorific suffixes. */
	public String[] getHonorificSuffixes() {
		return honorificSuffixes;
	}

	/** @return The first honorific suffix, or <code>null</code> if there are no honorific suffixes. */
	public String getHonorificSuffix() {
		return honorificSuffixes.length > 0 ? honorificSuffixes[0] : null;
	}

	/**
	 * Sets the honorific suffixes.
	 * @param honorificSuffixes The honorific suffixes.
	 * @throws NullPointerException if the provided honorific suffixes is <code>null</code>.
	 */
	public void setHonorificSuffixes(final String... honorificSuffixes) {
		this.honorificSuffixes = requireNonNull(honorificSuffixes, "Honorific suffixes cannot be null.");
	}

	/**
	 * Name constructor.
	 * @param familyNames The family names.
	 * @param givenNames The given names.
	 */
	public Name(final String[] familyNames, final String[] givenNames) {
		this(familyNames, givenNames, new String[] {}); //construct the name with no additional names
	}

	/**
	 * Additional constructor.
	 * @param familyNames The family names.
	 * @param givenNames The given names.
	 * @param additionalNames The additional names.
	 */
	public Name(final String[] familyNames, final String[] givenNames, final String[] additionalNames) {
		this(familyNames, givenNames, additionalNames, new String[] {}, new String[] {}); //construct the name with no prefixes or suffixes
	}

	/**
	 * Full name constructor.
	 * @param familyNames The family names.
	 * @param givenNames The given names.
	 * @param additionalNames The additional names.
	 * @param honorificPrefixes The honorific prefixes.
	 * @param honorificSuffixes The honorific suffixes.
	 */
	public Name(final String[] familyNames, final String[] givenNames, final String[] additionalNames, final String[] honorificPrefixes,
			final String[] honorificSuffixes) {
		this(familyNames, givenNames, additionalNames, honorificPrefixes, honorificSuffixes, null); //construct the name with no locale	
	}

	/**
	 * Full constructor.
	 * @param familyNames The family names.
	 * @param givenNames The given names.
	 * @param additionalNames The additional names.
	 * @param honorificPrefixes The honorific prefixes.
	 * @param honorificSuffixes The honorific suffixes.
	 * @param locale The locale that represents the language of the text, or <code>null</code> if no language should be indicated.
	 */
	public Name(final String[] familyNames, final String[] givenNames, final String[] additionalNames, final String[] honorificPrefixes,
			final String[] honorificSuffixes, final Locale locale) {
		super(locale); //construct the parent class
		setFamilyNames(familyNames); //set the names
		setGivenNames(givenNames);
		setAdditionalNames(additionalNames);
		setHonorificPrefixes(honorificPrefixes);
		setHonorificSuffixes(honorificSuffixes);
	}

	/**
	 * Single constructor.
	 * @param familyName The family name, or <code>null</code> for no family name.
	 * @param givenName The given name, or <code>null</code> for no given name.
	 */
	public Name(final String familyName, final String givenName) {
		this(familyName, givenName, null); //construct the name with no additional name
	}

	/**
	 * Additional single constructor.
	 * @param familyName The family name, or <code>null</code> for no family name.
	 * @param givenName The given name, or <code>null</code> for no given name.
	 * @param additionalName The additional name, or <code>null</code> for no additional name.
	 */
	public Name(final String familyName, final String givenName, final String additionalName) {
		this(familyName, givenName, additionalName, null, null); //construct the name with no prefix or suffix
	}

	/**
	 * Full name constructor.
	 * @param familyName The family name, or <code>null</code> for no family name.
	 * @param givenName The given name, or <code>null</code> for no given name.
	 * @param additionalName The additional name, or <code>null</code> for no additional name.
	 * @param honorificPrefix The honorific prefix, or <code>null</code> for no honorific prefix.
	 * @param honorificSuffix The honorific suffix, or <code>null</code> for no honorific suffix.
	 */
	public Name(final String familyName, final String givenName, final String additionalName, final String honorificPrefix, final String honorificSuffix) {
		this(familyName, givenName, additionalName, honorificPrefix, honorificSuffix, null); //construct the name with no locale	
	}

	/**
	 * Full single constructor.
	 * @param familyName The family name, or <code>null</code> for no family name.
	 * @param givenName The given name, or <code>null</code> for no given name.
	 * @param additionalName The additional name, or <code>null</code> for no additional name.
	 * @param honorificPrefix The honorific prefix, or <code>null</code> for no honorific prefix.
	 * @param honorificSuffix The honorific suffix, or <code>null</code> for no honorific suffix.
	 * @param locale The locale that represents the language of the text, or <code>null</code> if no language should be indicated.
	 */
	public Name(final String familyName, final String givenName, final String additionalName, final String honorificPrefix, final String honorificSuffix,
			final Locale locale) {
		super(locale); //construct the parent class
		setFamilyNames(familyName); //set the names
		setGivenNames(givenName);
		setAdditionalNames(additionalName);
		setHonorificPrefixes(honorificPrefix);
		setHonorificSuffixes(honorificSuffix);
	}

	/**
	 * Returns a compact form of the complete name. This is normally the given name and the family name, if any. This method never returns <code>null</code>.
	 * @return A compact form of the complete name.
	 * @see #getGivenName()
	 * @see #getFamilyName()
	 */
	public final String getCompleteName() {
		final String givenName = getGivenName(); //get the first and last names
		final String familyName = getFamilyName();
		if(givenName != null || familyName != null) { //if we have a first or last name TODO add an additional name of there is no first or last name
			final StringBuilder stringBuilder = new StringBuilder(); //create a new string builder to hold the complete name we'll construct
			if(givenName != null) { //if there is a first name
				stringBuilder.append(givenName); //append the first name
			}
			if(familyName != null) { //if there is a last name
				if(stringBuilder.length() > 0) { //if there are characters so far
					stringBuilder.append(' '); //separate the components
				}
				stringBuilder.append(familyName); //append the last name
			}
			return stringBuilder.toString(); //return the complete name we constructed
		} else { //if there is no first or last name
			return ""; //return an empty string
		}
	}

	/** @return A string representation of the name. */
	public String toString() {
		try {
			final StringBuilder stringBuilder = new StringBuilder(); //create a new string builder to hold the string we'll construct
			formatList(stringBuilder, ',', (Object[])honorificPrefixes); //append the honorific prefixes, separated by a comma
			if(honorificPrefixes.length > 0 && (givenNames.length > 0 || additionalNames.length > 0 || familyNames.length > 0 || honorificSuffixes.length > 0)) { //if we added information and there is more information following
				stringBuilder.append(' '); //append a space
			}
			formatList(stringBuilder, '/', (Object[])givenNames); //append the given names, separated by a slash
			if(givenNames.length > 0 && (additionalNames.length > 0 || familyNames.length > 0 || honorificSuffixes.length > 0)) { //if we added information and there is more information following
				stringBuilder.append(' '); //append a space
			}
			formatList(stringBuilder, ", ", (Object[])additionalNames); //append the additional names, separated by a comma
			if(additionalNames.length > 0 && (familyNames.length > 0 || honorificSuffixes.length > 0)) { //if we added information and there is more information following
				stringBuilder.append(' '); //append a space
			}
			formatList(stringBuilder, '/', (Object[])familyNames); //append the family names, separated by a slash
			if(stringBuilder.length() > 0 && honorificSuffixes.length > 0) { //if we have any content before the suffixes, and there are suffixes
				stringBuilder.append(','); //add a comma before the suffixes
			}
			if(familyNames.length > 0 && (honorificSuffixes.length > 0)) { //if we added information and there is more information following
				stringBuilder.append(' '); //append a space
			}
			formatList(stringBuilder, ", ", (Object[])honorificSuffixes); //append the honorific suffixes, separated by a comma
			return stringBuilder.toString(); //return the string we constructed
		} catch(final IOException ioException) {
			throw unexpected(ioException);
		}
	}
}
