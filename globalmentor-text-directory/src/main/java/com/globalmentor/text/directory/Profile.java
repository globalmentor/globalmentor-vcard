/*
 * Copyright © 1996-2011 GlobalMentor, Inc. <http://www.globalmentor.com/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.globalmentor.text.directory;

import java.util.*;

import com.globalmentor.model.NameValuePair;

/**
 * Class that knows how to give profile-specific information for types within a profile of a <code>text/directory</code> as defined in <a
 * href="http://www.ietf.org/rfc/rfc2425.txt">RFC 2425</a>, "A MIME Content-Type for Directory Information".
 * <p>
 * A profile may return <code>null</code> from {@link #getValueType(String, String, String, List)}) if if it wishes to do custom creation in
 * {@link ValueFactory#createValues(String, String, String, List, String, java.io.Reader)}, providing it has been registered with the particular type
 * name.
 * @author Garret Wilson
 */
public interface Profile {

	/**
	 * Determines the value type of the given content line value.
	 * @param profile The profile of this content line, or <code>null</code> if there is no profile.
	 * @param group The group specification, or <code>null</code> if there is no group.
	 * @param name The name of the information.
	 * @param paramList The list of parameters; a <code>null</code> value indicates that the name/value pair contained only a name.
	 * @return The value type of the content line, or <code>null</code> if the value type cannot be determined.
	 */
	public String getValueType(final String profile, final String group, final String name, final List<NameValuePair<String, String>> paramList);

	/**
	 * Creates a directory from the given content lines. Unrecognized or unusable content lines within the directory object will be saved as literal content lines
	 * so that their information will be preserved.
	 * @param contentLines The content lines that make up the directory.
	 * @return A directory object representing the directory, or <code>null</code> if this profile cannot create a directory from the given information.
	 */
	public Directory createDirectory(final ContentLine[] contentLines);
}
