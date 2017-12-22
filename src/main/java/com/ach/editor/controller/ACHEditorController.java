/**
 * 
 */
package com.ach.editor.controller;

import java.awt.Cursor;
import java.io.File;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.ach.achViewer.ACHEditorView;
import com.ach.achViewer.Main;
import com.ach.achViewer.model.ACHEditorModel;

/**
 * @author ilyakharlamov
 *
 */
public class ACHEditorController {

    private final ACHEditorModel model;
    private final ACHEditorView view;

    /**
     * @param model
     * @param viewer
     */
    public ACHEditorController(ACHEditorModel model, ACHEditorView view) {
        this.model = model;
        this.view = view;
        registerView();
    }

    /**
     * 
     */
    private void registerView() {
        view.jMenuItemFileOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFileOpenActionPerformed(evt);
            }
        });
    }

    /**
     * @param string
     */
    public void loadFile(String fileName) {
        loadAchData(fileName);
        model.setTitle(fileName);
    }

    public void loadAchData(String fileName) {
    
    	Cursor currentCursor = view.getCursor();
    	if (currentCursor.getType() == Cursor.DEFAULT_CURSOR) {
    		view.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    	}
    	try {
    		view.setAchFile(Main.parseFile(new File(fileName)));
    		view.loadAchInformation();
    		view.clearJListAchDataAchRecords();
    		view.loadAchDataRecords();
    	} catch (Exception ex) {
    		System.err.println(ex.getMessage());
    		ex.printStackTrace();
    		view.showMessage(ex.getMessage());
    	}
    	Vector<String> errorMessages = view.getAchFile().getErrorMessages();
    	if (errorMessages.size() == 0) {
            view.showMessage("File loaded without error");
    	} else {
    		StringBuffer errorMessage = new StringBuffer("");
    		for (int i = 0; i < errorMessages.size(); i++) {
    			errorMessage.append(errorMessages.get(i)
    					+ System.getProperty("line.separator", "\r\n"));
    		}
    		view.showMessage(errorMessage.toString());
    	}
    	ACHEditorView.setAchFileDirty(model, view, false);
    	if (currentCursor.getType() == Cursor.DEFAULT_CURSOR) {
    		view.setCursor(new Cursor(currentCursor.getType()));
    	}
    }
    
    private void jMenuItemFileOpenActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuFileOpenActionPerformed

        if (view.isAchFileDirty()) {
            Object[] options = { "Save", "Continue", "Cancel" };
            int selection = JOptionPane.showOptionDialog(view,
                    "ACH File has been changed. What would you like to do.",
                    "ACH File has changed", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (selection == 2) {
                // Selected cancel
                return;
            } else if (selection == 0) {
                try {
                    view.getAchFile().setFedFile(view.jCheckBoxMenuFedFile.isSelected());
                    if (!view.getAchFile().save(view.jLabelAchInfoFileName.getText())) {
                        return;
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(view,
                            "Failure saving file -- \n" + ex.getMessage(),
                            "Error saving file", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        JFileChooser chooser = new JFileChooser(new File(view.jLabelAchInfoFileName
                .getText()).getParent());
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setApproveButtonText("Open");

        int returnVal = chooser.showOpenDialog(view.getContentPane());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            view.jLabelAchInfoFileName.setText(chooser.getSelectedFile()
                    .getAbsolutePath());

            loadAchData(chooser.getSelectedFile().getAbsolutePath());
        }
    }// GEN-LAST:event_jMenuFileOpenActionPerformed

}
