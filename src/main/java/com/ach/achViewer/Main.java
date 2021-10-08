package com.ach.achViewer;

import com.ach.domain.ACHDocument;
import com.ach.editor.controller.ACHEditorController;
import com.ach.editor.model.ACHEditorModel;
import com.ach.editor.view.ACHEditorView;
import com.ach.editor.view.IOWorld;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(ACHEditorController.class);

    public static void main(String args[]) {

        if (args.length > 0 && args[0].equals("--validate")) {
            ACHDocument achFile;

            try {
                if (args.length > 1) {
                    final String filename = args[1];
                    achFile = FileSystemIOWorld.fromIs(new FileInputStream(new File(filename)));
                } else {
                    achFile = FileSystemIOWorld.fromIs(System.in);
                }
            } catch (Exception ex) {
                System.err.println("Failed to read ACH File");
                System.err.println(ex.getMessage());
                ex.printStackTrace(System.err);
                System.exit(2);
                return;
            }

            validate(achFile);
        } else {
            Main.runWithoutValidation(args);
        }
    }

    private static void validate(ACHDocument achFile) {
        Vector<String> messages = achFile.validate();

        if (messages.isEmpty()) {
            LOG.info("ACH File Validated");
            System.exit(0);
        } else {
            LOG.info("ACH File Validation Failed");
            for (String message : messages) {
                System.err.println("Error: " + message);
            }
            System.exit(1);
        }
    }

    static ArrayList<String> getLines(final InputStream is) throws IOException {
		InputStreamReader inputStreamReader = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line;
        ArrayList<String> lines = new ArrayList<>();
        while ((line = reader.readLine()) != null && line.length() != 0) {
            lines.add(line);
        }
		return lines;
	}

    /**
     * @param args
     *            the command line arguments
     */
    public static void runWithoutValidation(String args[]) {
    	final ACHEditorModel model = new ACHEditorModel();
        final ACHEditorView view = new ACHEditorView(model);
    	final ACHEditorController controller = new ACHEditorController(model, view, new FileSystemIOWorld());
    	try {
    		view.setVisible(true);
    		if(args.length>0) {
    		    final String filename = args[0];
    		    final File file = new File(filename);
                controller.loadDocumentFromFile(file);
                model.setOutputFile(file);
    		}
    	} catch (Exception ex) {
    		System.err.println(ex.getMessage());
    		ex.printStackTrace();
    		if (view != null) {
    			view.dispose();
    		}
    	}
    }

}
