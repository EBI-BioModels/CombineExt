/**
 * Copyright © 2014-2015:
 * - Martin Peters <martin@freakybytes.net>
 * - Martin Scharm <martin@binfalse.de>
 * 
 * This file is part of the CombineExt library.
 * 
 * CombineExt is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * CombineExt is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with CombineExt. If not, see <http://www.gnu.org/licenses/>.
 */
package de.unirostock.sems.cbext.recognizer;

import java.io.File;
import java.net.URI;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.cbext.FormatRecognizer;
import de.unirostock.sems.cbext.Formatizer;



/**
 * The Class SbmlFormatizer to recognize sbml files.
 */
public class SbmlRecognizer
	extends FormatRecognizer
{
	
	/** priority for this format recognizer */
	protected static int 			priority			= 100;
	
	/**
	 * Sets the priority of this format recognizer and triggers a resort of all
	 * format recognizers.
	 * 
	 * The higher the priority, the earlier this recognizer gets called.
	 * The first recognizer, which is able to identify a file, determines it's
	 * format.
	 * Setting a negative priority will be ignored.
	 * Default recognizers have a priority of 100.
	 * 
	 * @param newPriority
	 */
	public static void setPriority (int newPriority) {
		
		// no negative priorities!
		if( priority < 0 )
			return;
		
		priority = newPriority;
		Formatizer.resortRecognizers();
	}

	/* (non-Javadoc)
	 * @see de.unirostock.sems.cbext.FormatRecognizer#getPriority()
	 */
	@Override
	public int getPriority ()
	{
		return priority;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unirostock.sems.cbext.FormatParser#checkFormat(java.io.File,
	 * java.lang.String)
	 */
	@Override
	public URI getFormatByParsing (File file, String mimeType)
	{
		
		// mime type check
		if (mimeType == null || mimeType.equals ("application/xml") == false)
			return null;
		
		try
		{
			SBMLDocument doc = SBMLReader.read (file);
			return buildUri (IDENTIFIERS_BASE, "sbml.level-" + doc.getLevel ()
				+ ".version-" + doc.getVersion ());
		}
		catch (Exception e)
		{
			LOGGER.info (e, "file ", file, " seems to be no sbml file..");
		}
		
		// no format could be guessed
		return null;
	}
	
	
	@Override
	public URI getFormatFromMime (String mime)
	{
		// we cannot decide from just a mime type
		return null;
	}
	
	
	@Override
	public URI getFormatFromExtension (String extension)
	{
		if (extension != null && extension != null && extension.equals ("sbml"))
			return buildUri (IDENTIFIERS_BASE, "sbml");
		return null;
	}
	
}
