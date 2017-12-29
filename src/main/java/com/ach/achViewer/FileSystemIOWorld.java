/**
 * 
 */
package com.ach.achViewer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.ach.domain.ACHDocument;
import com.ach.editor.view.IOWorld;
import com.ach.parser.ACHViewerFileParser;
import com.ach.parser.AchParserException;
import com.ach.parser.IOUtils;

/**
 * @author ilyakharlamov
 *
 */
public class FileSystemIOWorld implements IOWorld {

    @Override
    public boolean save(File outputFile, ACHDocument achDocument) {
        try {
            return IOUtils.save(outputFile, achDocument);
        } catch (Exception e) {
            throw new IOWorldException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.ach.editor.controller.IOWorld#load(java.io.File)
     */
    @Override
    public ACHDocument load(File file) {
        try {
            return FileSystemIOWorld.parseFile(file);
        } catch (AchParserException e) {
            throw new IOWorldException(e);
        }
    }

    static ACHDocument fromIs(final InputStream is) throws AchParserException {
    	try {
    		ArrayList<String> lines = Main.getLines(is);
    		ACHViewerFileParser parser = new ACHViewerFileParser();
    		return parser.fromLines(lines);
    	} catch (IOException e) {
    		throw new AchParserException(e);
    	}
    }

    public static ACHDocument parseFile(File file) throws AchParserException {
        try {
            return FileSystemIOWorld.fromIs(new FileInputStream(file));
    	} catch (FileNotFoundException e) {
    		throw new AchParserException(e);
    	}
    }

}

class IOWorldException extends RuntimeException {

    public IOWorldException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public IOWorldException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public IOWorldException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
}
