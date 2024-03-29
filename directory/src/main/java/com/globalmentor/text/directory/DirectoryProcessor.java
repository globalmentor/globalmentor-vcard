/*
 * Copyright © 1996-2011 GlobalMentor, Inc. <https://www.globalmentor.com/>
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

package com.globalmentor.text.directory;

import java.io.*;
import java.util.*;

import static com.globalmentor.io.ReaderParser.*;
import static com.globalmentor.text.ABNF.*;
import static com.globalmentor.text.directory.Directory.*;

import com.globalmentor.io.*;
import com.globalmentor.java.Characters;
import com.globalmentor.model.LocaledText;
import com.globalmentor.model.NameValuePair;

/**
 * Class that can process a directory of type <code>text/directory</code> as defined in <a href="https://www.ietf.org/rfc/rfc2425.txt">RFC 2425</a>, "A MIME
 * Content-Type for Directory Information".
 * <p>
 * This processor makes the following decisions for ambiguities in the specification:
 * </p>
 * <ul>
 * <li>Every content line, including the last, is required to end in CRLF.</li>
 * <li>Lines containing only whitespace are ignored.</li>
 * </ul>
 * @author Garret Wilson
 * @see ValueFactory
 * @see Profile
 * @see PredefinedProfile
 */
public class DirectoryProcessor {

	/** The profile for the predefined types. */
	private final PredefinedProfile predefinedProfile = new PredefinedProfile();

	/** @return The profile for the predefined types. */
	protected PredefinedProfile getPredefinedProfile() {
		return predefinedProfile;
	}

	/** A map of profiles keyed to the lowercase version of the profile name. */
	private final Map<String, Profile> profileMap = new HashMap<String, Profile>();

	/**
	 * Registers a profile.
	 * @param profileName The name of the profile.
	 * @param profile The profile to be registered with this profile name.
	 */
	public void registerProfile(final String profileName, final Profile profile) {
		profileMap.put(profileName.toLowerCase(), profile); //put the profile in the map, keyed to the lowercase version of the profile name
	}

	/**
	 * Retrieves a profile for the given profile name.
	 * @param profileName The name of the profile to return, or <code>null</code> if the predefined profile should be returned.
	 * @return A profile for this profile name, or <code>null</code> if there is no profile registered for this profile name.
	 * @see #getPredefinedProfile
	 */
	protected Profile getProfile(final String profileName) {
		return profileName != null ? profileMap.get(profileName.toLowerCase()) : getPredefinedProfile(); //get the profile keyed to the lowercase version of the profile name, or return the predefined profile if null was passed
	}

	/** A map of value factories keyed to the lowercase version of the value type. */
	private final Map<String, ValueFactory> valueFactoryMap = new HashMap<String, ValueFactory>();

	/**
	 * Registers a value factory by value type.
	 * @param valueType The value type for which this value factory can create values.
	 * @param valueFactory The value factory to be registered with this value type.
	 */
	public void registerValueFactory(final String valueType, final ValueFactory valueFactory) {
		valueFactoryMap.put(valueType.toLowerCase(), valueFactory); //put the value factory in the map, keyed to the lowercase version of the type
	}

	/**
	 * Retrieves a value factory to create values for the given value type.
	 * @param valueType The value type for which a value factory should be returned.
	 * @return A value factory for this value type, or <code>null</code> if there is no value factory registered for this value type.
	 */
	protected ValueFactory getValueFactory(final String valueType) {
		return valueFactoryMap.get(valueType.toLowerCase()); //get the value factory keyed to the lowercase version of this value type
	}

	/** The profile last encountered in a "profile:" type content line. */
	private String defaultProfile = null;

	/** Whether the default profile was the last profile encountered. */
	private boolean useDefaultProfile = false;

	/**
	 * The stack of profiles encountered in a "begin:"/"end:" blocks; created before processing and released afterwards.
	 */
	private LinkedList<String> profileStack = null;

	/**
	 * Sets the profile to be used for subsequent content lines. If in the middle of a profile "begin:"/"end:" block, the profile of that block will be suspended
	 * until the block ends or another block begins.
	 * @param profile The new profile of the directory.
	 */
	protected void setProfile(final String profile) {
		defaultProfile = profile; //save the profile
		useDefaultProfile = true; //show that we should use the default profile
	}

	/**
	 * @return The current profile, either the last set profile, the profile of the current "begin:"/"end:" block, or <code>null</code> if there is no profile, in
	 *         that order.
	 */
	protected String getProfile() {
		if(useDefaultProfile && defaultProfile != null) { //if we should use the default profile and there is a profile set
			return defaultProfile; //return the last set profile
		} else if(profileStack.size() > 0) { //if we're in a profile "begin:"/"end:" block
			return profileStack.getLast(); //return the profile of the current block
		} else { //if no profile is set, and we're not in a profile "begin:"/"end:" block
			return defaultProfile; //if there's no profile "begin:"/"end:" block, we'll have to use the default profile, even if it is null
		}
	}

	/**
	 * Pushes the given profile on the stack, and removes the set profile, if any. Suspends the currently set profile, if any.
	 * @param profile The profile of the new "begin:"/"end:" block block.
	 */
	protected void pushProfile(final String profile) {
		profileStack.addLast(profile); //push the profile onto the stack
		useDefaultProfile = false; //suspend use of the default profile
	}

	/**
	 * Removes the profile from the top of the stack. Suspends the currently set profile, if any.
	 * @return The profile from the top of the stack.
	 * @throws NoSuchElementException Thrown if there are no more profiles on the stack.
	 */
	protected String popProfile() {
		useDefaultProfile = false; //suspend use of the default profile
		return profileStack.removeLast(); //pop the profile from the stack
	}

	/**
	 * Default constructor. This class automatically registers the predefined profile as a value factory for standard value types.
	 */
	public DirectoryProcessor() {
		//register the predefined profile as a value factory for the standard value types
		registerValueFactory(URI_VALUE_TYPE, getPredefinedProfile());
		registerValueFactory(TEXT_VALUE_TYPE, getPredefinedProfile());
		registerValueFactory(DATE_VALUE_TYPE, getPredefinedProfile());
		registerValueFactory(TIME_VALUE_TYPE, getPredefinedProfile());
		registerValueFactory(DATE_TIME_VALUE_TYPE, getPredefinedProfile());
		registerValueFactory(INTEGER_VALUE_TYPE, getPredefinedProfile());
		registerValueFactory(BOOLEAN_VALUE_TYPE, getPredefinedProfile());
		registerValueFactory(FLOAT_VALUE_TYPE, getPredefinedProfile());
	}

	/**
	 * The delimiter characters separating the main components of a content line with no group provided (';', ':', CR, and LF).
	 */
	protected static final Characters GROUPLESS_CONTENT_LINE_DELIMITER_CHARACTERS = Characters.of(PARAM_SEPARATOR_CHAR, NAME_VALUE_SEPARATOR_CHAR, CR, LF);

	/**
	 * The delimiter characters separating the main components of a content line ('.', ';', ':', CR, and LF).
	 */
	protected static final Characters CONTENT_LINE_DELIMITER_CHARACTERS = GROUPLESS_CONTENT_LINE_DELIMITER_CHARACTERS.add(GROUP_NAME_SEPARATOR_CHAR);

	/**
	 * Processes the content lines from a directory of type <code>text/directory</code>.
	 * @param reader The reader that contains the lines of the directory.
	 * @param sourceObject The source of the data (e.g. a <code>String</code>, <code>File</code>, <code>URL</code>, or <code>URI</code>).
	 * @return An object representing the directory.
	 * @throws IOException Thrown if there is an error reading the directory.
	 * @throws ParseIOException Thrown if there is a an error interpreting the directory.
	 */
	public Directory processDirectory(final Reader reader, final Object sourceObject) throws IOException, ParseIOException { //TODO remove sourceObject
		return processDirectory(new LineUnfoldReader(reader)); //create a new line unfold parse reader and use that to process the directory TODO see if the reader is already a LineUnfoldParseReader
	}

	/**
	 * Processes the content lines from a directory of type <code>text/directory</code>. The first profile encountered that can create a directory object will be
	 * used to create the directory object. Otherwise, the predefined profile will be used to create a default directory object containing the content lines.
	 * @param reader The reader that contains the lines of the directory.
	 * @return An object representing the directory.
	 * @throws IOException Thrown if there is an error reading the directory.
	 * @throws ParseIOException Thrown if there is a an error interpreting the directory.
	 */
	public Directory processDirectory(final Reader reader) throws IOException, ParseIOException {
		final Set<String> checkedProfileNameSet = new HashSet<String>(); //create a set to store the profile names we check
		final ContentLine[] contentLines = processContentLines(reader); //process the content lines
		for(int i = 0; i < contentLines.length; ++i) { //look at each content line
			final String profileName = contentLines[i].getProfile(); //get this line's profile name
			if(profileName != null && !checkedProfileNameSet.contains(profileName)) { //if this content line is in a profile that we haven't checked
				final Profile profile = getProfile(profileName); //see if we have a profile object for this profile
				if(profile != null) { //if we have a profile object registered
					final Directory directory = profile.createDirectory(contentLines); //ask this profile to create a directory
					if(directory != null) { //if the profile created a directory
						return directory; //return the directory the profile created
					}

				}
				checkedProfileNameSet.add(profileName); //show that we've checked this profile 				
			}
		}
		return getPredefinedProfile().createDirectory(contentLines); //if none of the profiles can create a directory, ask the predefined profile to create a directory
	}

	/**
	 * Retrieves content lines from a directory of type <code>text/directory</code>.
	 * @param reader The reader that contains the lines of the directory.
	 * @return An array of content lines in the directory.
	 * @throws IOException Thrown if there is an error reading the directory.
	 * @throws ParseIOException Thrown if there is a an error interpreting the directory.
	 */
	public ContentLine[] processContentLines(final Reader reader) throws IOException, ParseIOException {
		profileStack = new LinkedList<String>(); //create a new profile stack
		defaultProfile = null; //show that there is no default profile
		useDefaultProfile = false; //don't use the default profile
		final List<ContentLine> contentLineList = new ArrayList<ContentLine>(); //create an array in which to told the content lines
		ContentLine[] contentLines;
		while((contentLines = processContentLine(reader)) != null) { //process one or more lines of contents, all of which should have the same type, while we haven't reached the end of the reader
			for(final ContentLine contentLine : contentLines) { //look at each line of content
				//TODO del Log.trace("just processed content line: ", contentLine);	//TODO del
				final String typeName = contentLine.getName(); //get the type
				/*TODO del when works
									if(NAME_TYPE.equalsIgnoreCase(typeName)) {	//if this is NAME
										if(directory.getName()==null) {	//if the directory does not yet have a name
											directory.setName((String)contentLine.getValue());	//get the directory name
										}
									}
				*/
				if(PROFILE_TYPE.equalsIgnoreCase(typeName)) { //if this is PROFILE
					final String profile = ((LocaledText)contentLine.getValue()).getText(); //get the profile
					contentLine.setProfile(profile); //a profile type should have the same profile as the one it sets
					setProfile(profile); //set the profile to the new profile
				} else if(BEGIN_TYPE.equalsIgnoreCase(typeName)) { //if this is BEGIN:xxx
					final String profile = ((LocaledText)contentLine.getValue()).getText(); //get the profile
					contentLine.setProfile(profile); //a beginning profile type should have the same profile as the one it sets
					pushProfile(profile); //push the new profile
				} else if(END_TYPE.equalsIgnoreCase(typeName)) { //if this is END:xxx
					final String profile = ((LocaledText)contentLine.getValue()).getText(); //get the profile
					contentLine.setProfile(profile); //an ending profile type should have the same profile to which it refers
					try {
						final String oldProfile = popProfile(); //pop the profile from the stack
						//TODO make sure the old profile is what we expect
					} catch(final NoSuchElementException noSuchElementException) { //if there are no more profiles on the stack
						throw new ParseIOException(reader, "Profile \"" + profile + "\" END without BEGIN."); //throw an error indicating that there was no beginning to the profile
					}
				}
				contentLineList.add(contentLine); //add this content line to the list of content lines
			}
		}
		profileStack = null; //release the profile stack
		defaultProfile = null; //show that there is no default profile
		useDefaultProfile = false; //don't use the default profile
		return contentLineList.toArray(new ContentLine[contentLineList.size()]); //return the content lines we processed				
	}

	/**
	 * Retrieves one or more content lines from a directory, all of which will have the same type name. If the parsed content line has multiple values, a new
	 * identical content line will be created for to contain each value, differing only in the value. If the content line has only whitespace, an empty array will
	 * be returned. If there are no content lines before the end of the reader is reached, <code>null</code> will be returned.
	 * @param reader The reader that contains the lines of the directory.
	 * @return A one or more content lines from the directory (an empty array if the line contained only whitespace), or <code>null</code> if there are no more
	 *         content lines.
	 * @throws IOException Thrown if there is an error reading the directory.
	 * @throws ParseIOException Thrown if there is a an error interpreting the directory.
	 */
	public ContentLine[] processContentLine(final Reader reader) throws IOException, ParseIOException {
		String profile = getProfile(); //get the current profile, if there is one
		String group = null; //we'll store the group here
		String name = null; //we'll store the name here
		List<NameValuePair<String, String>> paramList = null; //we'll store parameters here, if we have any

		String token = readUntil(reader, CONTENT_LINE_DELIMITER_CHARACTERS); //read the next line token; don't throw an exception if the end of the file is reached, because this could be an empty line
		final int c = reader.read(); //get the delimiter character we encountered
		if(c < 0) { //if we reached the end of the file
			if(token.trim().length() > 0) { //if there is non-whitespace content before the end of the line, but none of the other delimiters we expect, there's a syntax error in the line
				throw new ParseEOFException(reader); //show that we didn't expect to run out of data here
			} else { //if this was an empty line
				if(token.isEmpty()) { //if no characters were read before the end of the line
					return null; //the end of the reader was reached
				} else { //if only whitespace was read
					return new ContentLine[0]; //return an empty content line indicator
				}
			}
		}
		char character = (char)c;
		if(character == GROUP_NAME_SEPARATOR_CHAR) { //if we just read a group
			//TODO check the syntax of the group
			group = token; //save the group we read
			//TODO del Log.trace("found group: ", group);
			token = readUntilRequired(reader, GROUPLESS_CONTENT_LINE_DELIMITER_CHARACTERS); //read the next line token after the group, which should be the name
			character = readRequired(reader); //get the delimiter character we encountered, and fall through to checking the name
		}
		switch(character) { //see which delimiter character we encountered
			case PARAM_SEPARATOR_CHAR: //if we just read a parameter separator
			case NAME_VALUE_SEPARATOR_CHAR: //if we just read the character separates the name from the value
				//TODO check the name
				name = token; //this is the name
				//		TODO del Log.trace("found name: ", name);
				if(character == PARAM_SEPARATOR_CHAR) { //if this was the character separating the name from parameters, read the parameters
					paramList = processParameters(reader); //process the parameters
					confirm(reader, NAME_VALUE_SEPARATOR_CHAR); //read the ':' that we expect to come after the parameters
					//					reader.readExpectedChar(NAME_VALUE_SEPARATOR_CHAR); //read the ':' that we expect to come after the parameters
				} else { //if there were no parameters
					paramList = new ArrayList<NameValuePair<String, String>>(); //create an empty list, since we didn't read any parameters
				}
				//		TODO del Log.trace("ready to process value");
				final Object[] values = processValue(profile, group, name, paramList, reader); //process the value and get an object that represents the object
				check(reader, CRLF); //there should always be a CRLF after the value
				final ContentLine[] contentLines = new ContentLine[values.length]; //create an array of content lines that we'll fill with new content lines
				for(int i = 0; i < values.length; ++i) { //look at each value
					contentLines[i] = new ContentLine(profile, group, name, new ArrayList<NameValuePair<String, String>>(paramList), values[i]); //create a content line with this value, making a copy of the parameter list
				}
				return contentLines; //return the array of content lines we created and filled
			case CR: //if we just read a carriage return
				if(token.trim().length() > 0) { //if there is content before the CRLF, but none of the other delimiters we expect, there's a syntax error in the line
					throw new ParseUnexpectedDataException(reader, Characters.of(PARAM_SEPARATOR_CHAR, NAME_VALUE_SEPARATOR_CHAR), character); //show that we didn't expect this character here
				}
				check(reader, LF); //there should always be an LF after a CR
				return new ContentLine[0]; //return an empty content line indicator
			case LF: //if we see an LF before a CR
			default: //if we read anything else (there shouldn't be anything else unless there is a logic error)					
				throw new ParseUnexpectedDataException(reader, Characters.of(PARAM_SEPARATOR_CHAR, NAME_VALUE_SEPARATOR_CHAR), character); //show that we didn't expect this character here
		}
	}

	/**
	 * After reading the parameter name, we expect either a parameter name-value separator ('=') indicating a value, the parameter separator (';') indicating more
	 * parameters, or the line name/value separator (':'), indicating we've finished parameters.
	 */
	protected static final Characters PARAM_NAME_DELIMITER_CHARACTERS = Characters.of(PARAM_NAME_VALUE_SEPARATOR_CHAR, PARAM_SEPARATOR_CHAR,
			NAME_VALUE_SEPARATOR_CHAR);

	/**
	 * After reading the parameter value, we expect either a parameter separator (';') the parameter value separator (',') indicating more values, or the line
	 * name/value separator (':'), indicating we've finished parameters.
	 */
	protected static final Characters PARAM_VALUE_DELIMITER_CHARACTERS = Characters.of(PARAM_SEPARATOR_CHAR, PARAM_VALUE_SEPARATOR_CHAR,
			NAME_VALUE_SEPARATOR_CHAR);

	/**
	 * Retrieves parameters from a line of content from a directory.
	 * <p>
	 * Whatever delimiter ended the value will be left in the reader.
	 * </p>
	 * @param reader The reader that contains the lines of the directory.
	 * @throws IOException Thrown if there is an error reading the directory.
	 * @throws ParseIOException Thrown if there is a an error interpreting the directory.
	 * @return The parameters from the line of content.
	 * @see NameValuePair
	 */
	public List<NameValuePair<String, String>> processParameters(final Reader reader) throws IOException, ParseIOException {
		final List<NameValuePair<String, String>> paramList = new ArrayList<NameValuePair<String, String>>(); //create a list of parameters
		char nextCharacter; //we'll store the last peeked delimiter here each time in the loop
		do { //read each parameter
			//read the parameter name
			final String paramName = readUntilRequired(reader, PARAM_NAME_DELIMITER_CHARACTERS); //get the parameter name, which is usually everything up to the '=' character
			//TODO check the param name for validity
			nextCharacter = peekRequired(reader); //see what delimiter will come next
			if(nextCharacter == PARAM_NAME_VALUE_SEPARATOR_CHAR) { //if there is at least one value waiting
				do { //read the parameter value(s)
					reader.skip(1); //skip the delimiter that got us here
					final String paramValue; //we'll read the value and store it here
					switch(peekRequired(reader)) { //see what character is first in the value
						case DQUOTE: //if the string starts with a quote
							paramValue = readEnclosedRequired(reader, DQUOTE, DQUOTE); //read the value within the quotes 
							break;
						default: //if the string doesn't end with a quote
							paramValue = readUntilRequired(reader, PARAM_VALUE_DELIMITER_CHARACTERS); //read everything until the end of this parameter
							break;
					}
					//			TODO del Log.trace("found param value: ", paramValue);
					//TODO check the parameter value, here
					paramList.add(new NameValuePair<String, String>(paramName, paramValue)); //add this name/value pair to our list of parameters
					nextCharacter = peekRequired(reader); //see what delimiter will come next
				} while(nextCharacter == PARAM_VALUE_SEPARATOR_CHAR); //keep getting parameter values while there are more parameter value separators
			} else { //if there is no '='
				paramList.add(new NameValuePair<String, String>(paramName, null)); //add a name/value pair with null as the value
			}
			if(nextCharacter == PARAM_SEPARATOR_CHAR) { //if the next character is the character that separates multiple parameters
				reader.skip(1); //skip the parameter separator
			}
		} while(nextCharacter != NAME_VALUE_SEPARATOR_CHAR); //keep reading parameters until we get to the '=' that separates the name from the value
		return paramList; //return the list of parameters we filled
	}

	/**
	 * Processes the textual representation of a line's value and returns one or more object representing the value, as some value types support multiple values.
	 * <p>
	 * Whatever delimiter ended the value will be left in the reader.
	 * </p>
	 * <p>
	 * When attempting to find a <code>ValueFactory</code> to process a given value, an attempt is made to locate a value factory in this order:
	 * </p>
	 * <ol>
	 * <li>If no explicit value type is given and a profile name is known, the <code>Profile</code> registered for that profile, if any, is asked for the
	 * type.</li>
	 * <li>If no explicit value type is still not known, the predefined profile is asked for the predefined type name.</li>
	 * <li>If a profile name is known and the <code>Profile</code> registered with the profile, if any, implements <code>ValueFactory</code>, it is asked to
	 * create the value object.</li>
	 * <li>If no value object was created, if the value type is known, the <code>ValueFactory</code> registered for the type, if any, is asked to create the value
	 * object.</li>
	 * <li>If no value object was created, a string is returned containing the literal contents of the value.</li>
	 * </ol>
	 * @param profileName The profile of this content line, or <code>null</code> if there is no profile.
	 * @param group The group specification, or <code>null</code> if there is no group.
	 * @param name The name of the information.
	 * @param paramList The list of parameters; a <code>null</code> value indicates that the name/value pair contained only a name.
	 * @param reader The reader that contains the lines of the directory.
	 * @return An array of objects represent the value string.
	 * @throws IOException Thrown if there is an error reading the directory.
	 * @throws ParseIOException Thrown if there is a an error interpreting the directory.
	 * @see NameValuePair
	 */
	protected Object[] processValue(final String profileName, final String group, final String name, final List<NameValuePair<String, String>> paramList,
			final Reader reader) throws IOException, ParseIOException {
		Object[] objects = null; //start out by assuming we can't process the value
		final Profile profile = getProfile(profileName); //see if we have a profile registered with this profile name
		String valueType = getParamValue(paramList, VALUE_PARAM_NAME); //get the value type parameter value
		if(valueType == null) { //if the value type wasn't explicitly given
			if(profile != null) { //if there is a profile for this profile name
				valueType = profile.getValueType(profileName, group, name, paramList); //ask this profile's value factory for the value type
			}
			if(valueType == null && profile != getPredefinedProfile()) { //if we still don't know the type, and we didn't already check the predefined profile 
				valueType = getPredefinedProfile().getValueType(profileName, group, name, paramList); //ask the predefined profile for the value type
			}
		}
		if(profile instanceof ValueFactory) { //if our profile is a value factory, use the profile as a value factory
			objects = ((ValueFactory)profile).createValues(profileName, group, name, paramList, valueType, reader); //create objects for this profile
		}
		if(objects == null && valueType != null) { //if no objects were created, but we know the value type
			final ValueFactory valueFactory = getValueFactory(valueType); //see if we have a value factory registered with this value type
			if(valueFactory != null) { //if there is a value factory for this value type
				objects = valueFactory.createValues(profileName, group, name, paramList, valueType, reader); //create objects for this value type
			}
		}
		if(objects == null) { //if no objects were created
			final String valueString = readUntilRequired(reader, CR); //everything before the carriage return will constitute the value
			objects = new String[] {valueString}; //put the single value string in an array of strings and use that for the value objects
		}
		return objects; //return the value objects we processed
	}

}
