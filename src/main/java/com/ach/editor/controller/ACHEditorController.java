/**
 * 
 */
package com.ach.editor.controller;

import java.awt.Cursor;
import java.io.File;
import java.util.Vector;

import javax.swing.JFileChooser;

import org.apache.commons.lang3.StringUtils;

import com.ach.achViewer.Main;
import com.ach.domain.ACHBatch;
import com.ach.domain.ACHEntry;
import com.ach.domain.ACHFile;
import com.ach.domain.ACHRecord;
import com.ach.domain.ACHRecordAddenda;
import com.ach.domain.ACHRecordBatchControl;
import com.ach.domain.ACHRecordBatchHeader;
import com.ach.domain.ACHRecordEntryDetail;
import com.ach.editor.model.ACHEditorModel;
import com.ach.editor.view.ACHEditorView;
import com.ach.editor.view.ACHEditorViewListener;

/**
 * @author ilyakharlamov
 *
 */
public class ACHEditorController implements ACHEditorViewListener {

    private final ACHEditorModel model;
    private final ACHEditorView view;

    /**
     * @param model
     * @param viewer
     */
    public ACHEditorController(ACHEditorModel model, ACHEditorView view) {
        this.model = model;
        this.view = view;
        this.view.registerListener(this);
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
            model.setAchFile(Main.parseFile(new File(fileName)));
            view.loadAchInformation();
            view.clearJListAchDataAchRecords();
            view.loadAchDataRecords();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            view.showMessage(ex.getMessage());
        }
        Vector<String> errorMessages = model.getAchFile().getErrorMessages();
        if (errorMessages.size() == 0) {
            view.showMessage("File loaded without error");
        } else {
            final String msg = StringUtils.join(errorMessages,System.getProperty("line.separator", "\r\n"));
            view.showMessage(msg);
        }
        model.setAchFileDirty(false);
        if (currentCursor.getType() == Cursor.DEFAULT_CURSOR) {
            view.setCursor(new Cursor(currentCursor.getType()));
        }
    }

    @Override
    public void onFileOpen() {
        if (model.isAchFileDirty()) {
            int selection = view.askSaveContinueCancel("ACH File has changed", "ACH File has been changed. What would you like to do.");
            if (selection == 2) {
                // Selected cancel
                return;
            } else if (selection == 0) {
                try {
                    model.getAchFile().setFedFile(view.jCheckBoxMenuFedFile.isSelected());
                    if (!model.getAchFile().save(view.jLabelAchInfoFileName.getText())) {
                        return;
                    }
                } catch (Exception ex) {
                    view.showError("Error saving file", "Failure saving file -- \n" + ex.getMessage());
                    return;
                }
            }
        }

        JFileChooser chooser = new JFileChooser(new File(view.jLabelAchInfoFileName.getText()).getParent());
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setApproveButtonText("Open");

        int returnVal = chooser.showOpenDialog(view.getContentPane());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            view.jLabelAchInfoFileName.setText(chooser.getSelectedFile().getAbsolutePath());

            loadAchData(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    @Override
    public void onFileNew() {
        final ACHFile achFile = model.getAchFile();

        if (model.isAchFileDirty()) {
            int selection = view.askSaveContinueCancel("ACH File has changed", "ACH File has been changed. What would you like to do.");
            if (selection == 2) {
                // Selected cancel
                return;
            } else if (selection == 0) {
                try {
                    achFile.setFedFile(view.jCheckBoxMenuFedFile.isSelected());
                    if (!achFile.save(view.jLabelAchInfoFileName.getText())) {
                        return;
                    }
                } catch (Exception ex) {
                    view.showError("Error saving file", "Failure saving file -- \n" + ex.getMessage());
                    return;
                }
            }
        }
        {
            final ACHFile brandNewAchFile = new ACHFile();
            brandNewAchFile.addBatch(new ACHBatch());
            brandNewAchFile.recalculate();
            model.setAchFile(brandNewAchFile);
            // Update display with new data
            model.setAchFileDirty(false); // Don't care if these records get
                                          // lost
            view.clearJListAchDataAchRecords();
            view.loadAchDataRecords();
        }
    }

    @Override
    public void addAchEntryDetail() {
        final ACHFile achFile = model.getAchFile();
        int[] selected = view.jListAchDataAchRecords.getSelectedIndices();
        if (selected.length < 1) {
            view.showError("Cannot perform request", "No items selected ... cannot add entry detail");
            return;
        }
        ACHRecord achRecord = (ACHRecord) view.jListAchDataAchRecords.getModel().getElementAt(selected[0]);
        if (achRecord.isEntryDetailType() || achRecord.isAddendaType() || achRecord.isBatchHeaderType()) {
            Integer[] position = view.positions.get(selected[0]);
            ACHRecordEntryDetail entryRecord = new ACHRecordEntryDetail();
            ACHEntry achEntry = new ACHEntry();
            achEntry.setEntryDetail(entryRecord);
            if (position.length == 0) {
                // problem -- this can only occur on file headers and file
                // details
                view.showError("Cannot perform requested function", "Cannot add entry detail after this row");
                return;
            } else if (position.length == 1) {
                // Adding after Batch Header
                achFile.getBatches().get(position[0]).getEntryRecs().add(0, achEntry);
            } else {
                achFile.getBatches().get(position[0]).getEntryRecs().add(position[1] + 1, achEntry);
            }
            model.setAchFileDirty(true);
            view.clearJListAchDataAchRecords();
            view.loadAchDataRecords();
            view.jListAchDataAchRecords.setSelectedIndex(selected[0]);
            view.jListAchDataAchRecords.ensureIndexIsVisible(selected[0]);
        } else {
            view.showError("Cannot perform requested function", "Cannot add entry detail after this row");
            return;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ach.editor.view.ACHEditorViewListener#exitProgram()
     */
    @Override
    public void onExitProgram() {
        final ACHFile achFile = model.getAchFile();
        if (model.isAchFileDirty()) {
            final String title = "ACH File has changed";
            final String message = "ACH File has been changed? What would you like to do.";
            int selection = view.askSaveExitCancel(title, message);
            if (selection == 2) {
                // Selected cancel
                return;
            } else if (selection == 0) {
                try {
                    achFile.setFedFile(view.jCheckBoxMenuFedFile.isSelected());
                    if (!achFile.save(view.jLabelAchInfoFileName.getText())) {
                        return;
                    }
                } catch (Exception ex) {
                    view.showError("Error saving file", "Failure saving file -- \n" + ex.getMessage());
                }
            }
        }
        view.dispose();
    }

    @Override
    public void addAchBatch() {
        final ACHFile achFile = model.getAchFile();
        int[] selected = view.jListAchDataAchRecords.getSelectedIndices();
        if (selected.length < 1) {
            view.showError("Cannot perform request", "No items selected ... cannot add entry detail");
            return;
        }
        ACHRecord achRecord = (ACHRecord) view.jListAchDataAchRecords.getModel().getElementAt(selected[0]);
        if (achRecord.isFileHeaderType() || achRecord.isBatchHeaderType() || achRecord.isBatchControlType()) {

            // Build the ACHBatch type
            ACHBatch achBatch = new ACHBatch();
            achBatch.setBatchHeader(new ACHRecordBatchHeader());
            achBatch.setBatchControl(new ACHRecordBatchControl());
            // add one entry rec
            achBatch.addEntryRecs(new ACHEntry());
            achBatch.getEntryRecs().get(0).setEntryDetail(new ACHRecordEntryDetail());

            // Add to achFile
            Integer[] position = view.positions.get(selected[0]);
            if (position.length == 0) {
                // Adding after File Header
                achFile.getBatches().add(0, achBatch);
            } else {
                achFile.getBatches().add(position[0] + 1, achBatch);
            }
            // make sure we have to save
            model.setAchFileDirty(true);

            // update display
            view.clearJListAchDataAchRecords();
            view.loadAchDataRecords();
            view.jListAchDataAchRecords.setSelectedIndex(selected[0]);
            view.jListAchDataAchRecords.ensureIndexIsVisible(selected[0]);
        } else {
            view.showError( "Cannot perform requested function", "Cannot add entry detail after this row");
            return;
        }
    }

    @Override
    public void addAchAddenda() {
        final ACHFile achFile = model.getAchFile();
        int selected = view.jListAchDataAchRecords.locationToIndex(view.mouseClick);
        if (selected < 0) {
            view.showError("Cannot perform request", "No items selected ... cannot add addenda");
            return;
        }
        ACHRecord achRecord = (ACHRecord) view.jListAchDataAchRecords.getModel().getElementAt(selected);
        if (achRecord.isEntryDetailType() || achRecord.isAddendaType()) {
            Integer[] position = view.positions.get(selected);
            if (position.length < 2) {
                view.showError("Cannot perform request", "Cannot add entry detail after this item");
                return;
            }
            // build empty addenda ... don't worry about sequence here, it's
            // fixed
            // later
            ACHRecordAddenda addendaRecord = new ACHRecordAddenda();
            addendaRecord.setEntryDetailSeqNbr(achFile.getBatches().get(position[0]).getEntryRecs().get(position[1])
                    .getEntryDetail().getTraceNumber());

            if (position.length == 2) {
                // Entry record selected ... add as the first addenda
                achFile.getBatches().get(position[0]).getEntryRecs().get(position[1]).getAddendaRecs().add(0,
                        addendaRecord);
            } else {
                // Addenda record selected .. add after it
                achFile.getBatches().get(position[0]).getEntryRecs().get(position[1]).getAddendaRecs()
                        .add(position[2] + 1, addendaRecord);
            }
            // Make sure entry record has the addenda indicator set
            achFile.getBatches().get(position[0]).getEntryRecs().get(position[1]).getEntryDetail()
                    .setAddendaRecordInd("1");
            // Resequence all addenda records
            Vector<ACHRecordAddenda> achAddendas = achFile.getBatches().get(position[0]).getEntryRecs().get(position[1])
                    .getAddendaRecs();
            for (int i = 0; i < achAddendas.size(); i++) {
                achAddendas.get(i).setAddendaSeqNbr(String.valueOf(i + 1));
            }
            achFile.getBatches().get(position[0]).getEntryRecs().get(position[1]).setAddendaRecs(achAddendas);

            // Update display with new data
            model.setAchFileDirty(true);
            view.clearJListAchDataAchRecords();
            view.loadAchDataRecords();
            view.jListAchDataAchRecords.setSelectedIndex(selected);
            view.jListAchDataAchRecords.ensureIndexIsVisible(selected);
        } else {
            view.showError("Cannot perform requested function", "Cannot add addenda after this row");
            return;
        }
    }

    @Override
    public void onFileSaveAs() {
        final ACHFile achFile = model.getAchFile();

        JFileChooser chooser = new JFileChooser(new File(view.jLabelAchInfoFileName.getText()).getParent());
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setApproveButtonText("Save As");

        int returnVal = chooser.showOpenDialog(view.getContentPane());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String fileName = chooser.getSelectedFile().getAbsolutePath();
            if (new File(fileName).exists()) {
                final String message = "File " + fileName + " already exists. Overwrite??";
                final String title = "File already exists";
                int answer = view.askYesNo(message, title);
                if (answer == 1) {
                    return;
                }
            }

            try {
                achFile.setFedFile(view.jCheckBoxMenuFedFile.isSelected());
                if (achFile.save(fileName)) {
                    model.setTitle(fileName);
                    model.setAchFileDirty(false);

                }
            } catch (Exception ex) {
                view.showError("Error writing file", "Unable to save ACH data to fileName. Reason: " + ex.getMessage());
            }
        }
    }

    @Override
    public void onItemPopupAddendaPaste() {
        // TODO Auto-generated method stub
        throw new RuntimeException("not implemented");
    }

    @Override
    public void onItemPopupBatchPaste() {
        // TODO Auto-generated method stub
        throw new RuntimeException("not implemented");
    }

    @Override
    public void onFileSave() {
        final ACHFile achFile = model.getAchFile();
        String fileName = view.jLabelAchInfoFileName.getText();
        try {
            achFile.setFedFile(view.jCheckBoxMenuFedFile.isSelected());
            if (achFile.save(fileName)) {
                model.setTitle(fileName);
                model.setAchFileDirty(false);
            }
        } catch (Exception ex) {
            view.showError("Error writing file", "Unable to save ACH data to fileName. Reason: " + ex.getMessage());
        }
    }

    @Override
    public void onItemPopupAddendaDelete() {
        int itemAtMouse = view.jListAchDataAchRecords.locationToIndex(view.mouseClick);
        deleteAchRecord(itemAtMouse);
    }

    @Override
    public void onItemPopupMultipleCopy() {
        int[] selected = view.jListAchDataAchRecords.getSelectedIndices();
        view.copy(selected);
    }

    @Override
    public void onItemPopupAddendaCopy() {
        int[] selected = view.jListAchDataAchRecords.getSelectedIndices();
        view.copy(selected);
    }

    @Override
    public void onItemPopupEntryEditEntry() {
        int itemAtMouse = view.jListAchDataAchRecords.locationToIndex(view.mouseClick);
        view.editAchRecord(itemAtMouse);
    }

    @Override
    public void onItemMultipleDelete() {
        int itemAtMouse = view.jListAchDataAchRecords.locationToIndex(view.mouseClick);
        deleteAchRecord(itemAtMouse);
    }

    /**
     * @param itemAtMouse
     */
    private void deleteAchRecord(int selectRow) {
        int[] selected = view.jListAchDataAchRecords.getSelectedIndices();
        if (selected.length > 1) {
            int addendaCount = 0;
            int entryCount = 0;
            int batchHeaderCount = 0;
            int batchControlCount = 0;
            int fileHeaderCount = 0;
            int fileControlCount = 0;
            for (int i = 0; i < selected.length; i++) {
                ACHRecord achRecord = ((ACHRecord) view.jListAchDataAchRecords.getModel().getElementAt(selected[i]));
                if (achRecord.isAddendaType()) {
                    addendaCount++;
                } else if (achRecord.isEntryDetailType()) {
                    entryCount++;
                } else if (achRecord.isBatchHeaderType()) {
                    batchHeaderCount++;
                } else if (achRecord.isBatchControlType()) {
                    batchControlCount++;
                } else if (achRecord.isFileHeaderType()) {
                    fileHeaderCount++;
                } else if (achRecord.isFileControlType()) {
                    fileControlCount++;
                }
            }
            // Determine the type of delete from the outside in
            if (fileHeaderCount > 0 || fileControlCount > 0) {
                view.showError("Cannot perform request",
                        "Cannot delete file header or control records. Use 'New' menu item instead");
                return;
            } else if (batchHeaderCount > 0 || batchControlCount > 0) {
                final String title = "Deleting multiple batches";
                final String message = "This will delete multiple batches. Continue with delete??";
                int selection = view.askDeleteCancel(title, message);
                if (selection == 0) {
                    onDeleteAchBatch();
                } else {
                    return;
                }
            } else if (entryCount > 0) {
                final String title = "Deleting multiple entry details";
                final String message = "This will delete multiple entry details. Continue with delete??";
                int selection = view.askDeleteCancel(title, message);
                if (selection == 0) {
                    onDeleteAchEntryDetail();
                } else {
                    return;
                }
            } else if (addendaCount > 0) {
                int selection = view.askDeleteCancel("Deleting multiple Addenda records", "This will delete multiple Addenda records. Continue with delete??");
                if (selection == 0) {
                    view.deleteAchAddenda();
                } else {
                    return;
                }
            }
        } else {
            ACHRecord achRecord = ((ACHRecord) view.jListAchDataAchRecords.getModel().getElementAt(selectRow));
            if (achRecord.isBatchHeaderType() || achRecord.isBatchControlType()) {
                onDeleteAchBatch();
            } else if (achRecord.isEntryDetailType()) {
                onDeleteAchEntryDetail();
            } else if (achRecord.isAddendaType()) {
                view.deleteAchAddenda();
            }
        }
    }

    @Override
    public void onItemPopupFileEdit() {
        int itemAtMouse = view.jListAchDataAchRecords.locationToIndex(view.mouseClick);
        view.editAchRecord(itemAtMouse);
    }

    @Override
    public void onItemToolsValidate() {
        final ACHFile achFile = model.getAchFile();

        Cursor currentCursor = view.getCursor();
        if (currentCursor.getType() == Cursor.DEFAULT_CURSOR) {
            view.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        }
        Vector<String> messages = achFile.validate();
        if (currentCursor.getType() == Cursor.DEFAULT_CURSOR) {
            view.setCursor(new Cursor(currentCursor.getType()));
        }

        if (messages.size() > 0) {
            final String msg = StringUtils.join(messages,"\n");
            view.showMessage(msg);
        }
    }

    @Override
    public void onItemToolsRecalculate() {
        final ACHFile achFile = model.getAchFile();

        Cursor currentCursor = view.getCursor();
        if (currentCursor.getType() == Cursor.DEFAULT_CURSOR) {
            view.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        }
        if (!achFile.recalculate()) {
            view.showMessage("Unable to fully recalculate ... run Validate tool");
        }
        view.clearJListAchDataAchRecords();
        view.loadAchInformation();
        view.loadAchDataRecords();
        model.setAchFileDirty(true);
        if (currentCursor.getType() == Cursor.DEFAULT_CURSOR) {
            view.setCursor(new Cursor(currentCursor.getType()));
        }
    }

    @Override
    public void onDeleteAchEntryDetail() {
        final ACHFile achFile = model.getAchFile();
        int[] selected = view.jListAchDataAchRecords.getSelectedIndices();
        if (selected.length < 1) {
            view.showError("Cannot perform request", "No items selected ... cannot delete entry detail");
            return;
        }
        for (int i = 0; i < selected.length; i++) {
            ACHRecord achRecord = (ACHRecord) view.jListAchDataAchRecords.getModel().getElementAt(selected[i]);
            if (achRecord.isEntryDetailType() || achRecord.isAddendaType()) {
            } else {
                view.showError("Cannot perform requested function", "Cannot delete entry detail -- non-entry/addenda rows in selection list");
                return;
            }
        }
        // Remove them backwards to positions don't shift on us
        for (int i = selected.length - 1; i >= 0; i--) {
            Integer[] position = view.positions.get(selected[i]);
            if (position.length != 2) {
                // problem -- this can only occur if there is a mismatch
                // between positions and jListAchDataAchRecords
                view.showError("Cannot perform requested function", "Cannot delete entry detail -- row is not an entry row");
                return;
            } else {
                achFile.getBatches().get(position[0]).getEntryRecs().remove(position[1].intValue());
            }
        }
        model.setAchFileDirty(true);
        view.clearJListAchDataAchRecords();
        view.loadAchDataRecords();
        view.jListAchDataAchRecords.setSelectedIndex(selected[0]);
        view.jListAchDataAchRecords.ensureIndexIsVisible(selected[0]);
    }

    @Override
    public void onItemPopupEntryPaste() {
        // TODO Auto-generated method stub
        throw new RuntimeException("not implemented");
    }

    @Override
    public void onItemToolsReverse() {
        final ACHFile achFile = model.getAchFile();
        achFile.reverse();
        view.loadAchInformation();
        view.clearJListAchDataAchRecords();
        view.loadAchDataRecords();
        model.setAchFileDirty(true);
    }

    @Override
    public void onItemPopupBatchCopy() {
        int[] selected = view.jListAchDataAchRecords.getSelectedIndices();
        view.copy(selected);
    }

    @Override
    public void onItemPopupEntryCopy() {
        int[] selected = view.jListAchDataAchRecords.getSelectedIndices();
        view.copy(selected);
    }

    @Override
    public void onItemPopupPasteMultiple() {
        Vector<ACHRecord> copySet = view.getClipboard();
        for (int i = 0; i < copySet.size(); i++) {
            System.err.println(copySet.get(i).toString());
        }
    }

    @Override
    public void onItemPopupBatchEditBatch() {
        int itemAtMouse = view.jListAchDataAchRecords.locationToIndex(view.mouseClick);
        view.editAchRecord(itemAtMouse);
    }

    @Override
    public void onDeleteAchBatch() {
        final ACHFile achFile = model.getAchFile();
        int[] selected = view.jListAchDataAchRecords.getSelectedIndices();
        if (selected.length < 1) {
            view.showError("Cannot perform request", "No items selected ... cannot delete");
            return;
        }
        for (int i = 0; i < selected.length; i++) {
            ACHRecord achRecord = (ACHRecord) view.jListAchDataAchRecords.getModel().getElementAt(selected[i]);
            if (achRecord.isFileHeaderType() || achRecord.isFileControlType()) {
                view.showError("Cannot perform requested function",
                        "Cannot delete file header/control rows in selection list");
                return;
            }
        }
        // Remove them backwards to positions don't shift on us
        for (int i = selected.length - 1; i >= 0; i--) {
            Integer[] position = view.positions.get(selected[i]);
            if (position.length != 1) {
                // find batch headers -- skipp entry and addenda
            } else {
                ACHRecord achRecord = (ACHRecord) view.jListAchDataAchRecords.getModel().getElementAt(selected[i]);
                // only delete items that match the headers
                if (achRecord.isBatchHeaderType()) {
                    achFile.getBatches().remove(position[0].intValue());
                }
            }
        }
        model.setAchFileDirty(true);
        view.clearJListAchDataAchRecords();
        view.loadAchDataRecords();
        view.jListAchDataAchRecords.setSelectedIndex(selected[0]);
        view.jListAchDataAchRecords.ensureIndexIsVisible(selected[0]);
    }
}
