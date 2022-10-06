/*
 * Copyright © 2011 GlobalMentor, Inc. <http://www.globalmentor.com/>
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

package com.globalmentor.text.directory.vcard.cat;

import static java.lang.String.*;
import static java.util.stream.Collectors.*;

import java.io.*;
import java.util.List;

import javax.annotation.*;

import org.slf4j.event.Level;

import com.globalmentor.application.*;
import com.globalmentor.io.Files;
import com.globalmentor.text.directory.vcard.*;

import io.clogr.Clogged;
import picocli.CommandLine.*;

/**
 * Application and routines for concatenating the contents of one or more VCards into a single output.
 * <p>
 * One benefit of concatenating, if even for a single input file, is that the file contents will be normalized, compensating for variations in the output of
 * various producers.
 * </p>
 * @author Garret Wilson
 */
@Command(name = "vcardcat", description = "Concatenates the contents of one or more vCards into a single output.")
public class VCardCat extends BaseCliApplication implements Clogged {

	@Option(names = {"--out", "-o"})
	private File outputFile;

	@Parameters(paramLabel = "<file>", description = "One or more input files, or a single file glob.", arity = "1..*")
	private List<File> inputFiles;

	/**
	 * Constructor.
	 * @param args The command line arguments.
	 */
	public VCardCat(@Nonnull final String[] args) {
		super(args, Level.INFO);
	}

	/**
	 * Main program entry method.
	 * @param args Program arguments.
	 */
	public static void main(@Nonnull final String[] args) {
		Application.start(new VCardCat(args));
	}

	@Override
	public void run() {
		logAppInfo();

		try {
			final List<File> files = inputFiles.stream().map(File::getAbsoluteFile).collect(toUnmodifiableList()); //TODO switch to `Path.toRealPath(NOFOLLOW_LINKS)`
			final OutputStream outputStream = outputFile != null ? new BufferedOutputStream(new FileOutputStream(outputFile)) : System.out;
			try {
				final VCardIO vcardIO = new VCardIO();
				vcardIO.setSerializationSingleValueNames(VCard.NOTE_TYPE); //combine notes TODO make this optional
				for(final File file : files) { //look at all the specified files
					try {
						//read the card from resources
						final VCard vcard = Files.read(file, vcardIO); //read this VCard
						vcardIO.write(outputStream, null, vcard); //write the VCard to the output
					} catch(final Throwable throwable) {
						throw new RuntimeException(format("Error processing VCard file `%s`: %s.", file, throwable.getMessage()), throwable);
					}
				}
			} finally {
				if(outputFile != null) { //if we opened a special file
					outputStream.close(); //close the file
				}
			}
		} catch(final IOException ioException) {
			throw new UncheckedIOException(ioException.getMessage(), ioException);
		}
	}

}
