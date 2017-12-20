package com.ach.achViewer;

import com.ach.domain.ACHFile;
import com.ach.parser.ACHViewerFileParser;
import com.ach.parser.AchParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author frank
 */
public class Main {

    public static void main(String args[]) {

        if (args.length > 0 && args[0].equals("--validate")) {
            ACHFile achFile;

            try {
                if (args.length > 1) {
                    achFile = loadACHFileFromFilename(args[1]);
                } else {
                    achFile = loadACHFileFromStdin();
                }
            } catch (Exception ex) {
                System.err.println("Failed to read ACH File");
                System.err.println(ex.getMessage());
                ex.printStackTrace(System.err);
                System.exit(2);
                return;
            }

            Vector<String> messages = achFile.validate();

            if (messages.isEmpty()) {
                System.out.println("ACH File Validated");
                System.exit(0);
            } else {
                System.out.println("ACH File Validation Failed");
                for (String message : messages) {
                    System.err.println("Error: " + message);
                }
                System.exit(1);
            }
        } else {
            ACHViewer.main(args);
        }
    }

    public static ACHFile loadACHFileFromFilename(String filename) throws AchParserException {
        try {
			return fromIs(new FileInputStream(new File(filename)));
		} catch (FileNotFoundException e) {
			throw new AchParserException(e);
		}
    }

    public static ACHFile loadACHFileFromStdin() throws AchParserException {
		return fromIs(System.in);
    }

	private static ACHFile fromIs(final InputStream is) throws AchParserException {
		try {
			ArrayList<String> lines = getLines(is);
			ACHViewerFileParser parser = new ACHViewerFileParser();
			return parser.fromLines(lines);
		} catch (IOException e) {
			throw new AchParserException(e);
		}
	}

	private static ArrayList<String> getLines(final InputStream is) throws IOException {
		InputStreamReader inputStreamReader = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line;
        ArrayList<String> lines = new ArrayList<>();
        while ((line = reader.readLine()) != null && line.length() != 0) {
            lines.add(line);
        }
		return lines;
	}

}
