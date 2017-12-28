/**
 * 
 */
package com.ach.editor.controller;

import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JPopupMenu;

import org.apache.commons.lang3.StringUtils;

import com.ach.achViewer.ACHAddendaDialog;
import com.ach.achViewer.ACHBatchControlDialog;
import com.ach.achViewer.ACHBatchHeaderDialog;
import com.ach.achViewer.ACHEntryDetailDialog;
import com.ach.achViewer.ACHFileControlDialog;
import com.ach.achViewer.ACHFileHeaderDialog;
import com.ach.achViewer.Main;
import com.ach.domain.ACHBatch;
import com.ach.domain.ACHEntry;
import com.ach.domain.ACHDocument;
import com.ach.domain.ACHRecord;
import com.ach.domain.ACHRecordAddenda;
import com.ach.domain.ACHRecordBatchControl;
import com.ach.domain.ACHRecordBatchHeader;
import com.ach.domain.ACHRecordEntryDetail;
import com.ach.domain.ACHRecordFileControl;
import com.ach.editor.model.ACHEditorModel;
import com.ach.editor.view.ACHEditorView;
import com.ach.editor.view.ACHEditorViewListener;
import com.ach.parser.IOUtils;

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

    @Override
    public void addAchAddenda() {
        final ACHDocument achFile = model.getAchFile();
        int selected = view.clickedIndex();
        if (selected < 0) {
            view.showError("Cannot perform request", "No items selected ... cannot add addenda");
            return;
        }
        ACHRecord achRecord = view.getRow(selected);
        if (achRecord.isEntryDetailType() || achRecord.isAddendaType()) {
            Integer[] position = view.getPositions(selected);
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
            model.setSelectedRow(selected);
        } else {
            view.showError("Cannot perform requested function", "Cannot add addenda after this row");
            return;
        }
    }

    @Override
    public void addAchBatch() {
        final ACHDocument achFile = model.getAchFile();
        int[] selected = view.getSelectedRows();
        if (selected.length < 1) {
            view.showError("Cannot perform request", "No items selected ... cannot add entry detail");
            return;
        }

        final int selectedIdx = selected[0];
        ACHRecord achRecord = view.getRow(selectedIdx);
        if (achRecord.isFileHeaderType() || achRecord.isBatchHeaderType() || achRecord.isBatchControlType()) {

            // Build the ACHBatch type
            ACHBatch achBatch = new ACHBatch();
            achBatch.setBatchHeader(new ACHRecordBatchHeader());
            achBatch.setBatchControl(new ACHRecordBatchControl());
            // add one entry rec
            achBatch.addEntryRecs(new ACHEntry());
            achBatch.getEntryRecs().get(0).setEntryDetail(new ACHRecordEntryDetail());

            // Add to achFile
            Integer[] position = view.getPositions(selectedIdx);
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
            model.setSelectedRow(selectedIdx);

        } else {
            view.showError("Cannot perform requested function", "Cannot add entry detail after this row");
            return;
        }
    }

    @Override
    public void addAchEntryDetail() {
        final ACHDocument achFile = model.getAchFile();
        int[] selected = view.getSelectedRows();
        if (selected.length < 1) {
            view.showError("Cannot perform request", "No items selected ... cannot add entry detail");
            return;
        }
        final int index = selected[0];
        ACHRecord achRecord = view.getRow(index);
        if (achRecord.isEntryDetailType() || achRecord.isAddendaType() || achRecord.isBatchHeaderType()) {
            Integer[] position = view.getPositions(index);
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
            model.setSelectedRow(index);
        } else {
            view.showError("Cannot perform requested function", "Cannot add entry detail after this row");
            return;
        }
    }

    private ACHRecord clickedRecord() {
        int itemAtMouse = view.clickedIndex();
        ACHRecord achRecord = view.getRow(itemAtMouse);
        return achRecord;
    }

    public void deleteAchAddenda() {
        final ACHDocument achFile = model.getAchFile();
        int[] selected = view.getSelectedRows();
        if (selected.length < 1) {
            view.showError("Cannot perform request", "No items selected ... cannot delete entry detail");
            return;
        }
        for (int i = 0; i < selected.length; i++) {
            ACHRecord achRecord = view.getRow(selected[i]);
            if (achRecord.isAddendaType()) {
            } else {
                view.showError("Cannot perform requested function",
                        "Cannot delete addenda records -- non-entry/addenda rows " + "in selection list");
                return;
            }
        }
        // Remove them backwards to positions don't shift on us
        Integer[] position = new Integer[0];
        for (int i = selected.length - 1; i >= 0; i--) {
            final int index = selected[i];
            position = view.getPositions(index);
            if (position.length != 3) {
                // problem -- this can only occur if there is a mismatch
                // between positions and jListAchDataAchRecords
                view.showError("Cannot perform requested function",
                        "Cannot delete addenda -- row is not an addenda row");
                return;
            } else {
                achFile.getBatches().get(position[0]).getEntryRecs().get(position[1]).getAddendaRecs()
                        .remove(position[2].intValue());
            }
        }
        // position has the first addenda that was deleted ... we need it to
        // know where the
        // entry is
        if (position.length > 0) {

            // Make sure entry record has the addenda indicator set if there are
            // addenda recs left
            achFile.getBatches().get(position[0]).getEntryRecs().get(position[1]).getEntryDetail().setAddendaRecordInd(
                    achFile.getBatches().get(position[0]).getEntryRecs().get(position[1]).getAddendaRecs().size() > 0
                            ? "1" : "0");
            // Resequence all addenda records -- this won't do anything
            // if all addenda records were deleted
            Vector<ACHRecordAddenda> achAddendas = achFile.getBatches().get(position[0]).getEntryRecs().get(position[1])
                    .getAddendaRecs();
            for (int i = 0; i < achAddendas.size(); i++) {
                achAddendas.get(i).setAddendaSeqNbr(String.valueOf(i + 1));
            }
            achFile.getBatches().get(position[0]).getEntryRecs().get(position[1]).setAddendaRecs(achAddendas);
        }

        model.setAchFileDirty(true);
        view.clearJListAchDataAchRecords();
        view.loadAchDataRecords();
        final int index = selected[0];
        model.setSelectedRow(index);
    }

    /**
     * @param itemAtMouse
     */
    private void deleteAchRecord(int selectRow) {
        int[] selected = view.getSelectedRows();
        if (selected.length > 1) {
            int addendaCount = 0;
            int entryCount = 0;
            int batchHeaderCount = 0;
            int batchControlCount = 0;
            int fileHeaderCount = 0;
            int fileControlCount = 0;
            for (int i = 0; i < selected.length; i++) {
                ACHRecord achRecord = view.getRow(selected[i]);
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
                int selection = view.askDeleteCancel("Deleting multiple Addenda records",
                        "This will delete multiple Addenda records. Continue with delete??");
                if (selection == 0) {
                    deleteAchAddenda();
                } else {
                    return;
                }
            }
        } else {
            ACHRecord achRecord = view.getRow(selectRow);
            if (achRecord.isBatchHeaderType() || achRecord.isBatchControlType()) {
                onDeleteAchBatch();
            } else if (achRecord.isEntryDetailType()) {
                onDeleteAchEntryDetail();
            } else if (achRecord.isAddendaType()) {
                deleteAchAddenda();
            }
        }
    }

    public void editAchAddenda(int batchPosition, int entryPosition, int addendaPosition, int selectRow) {
        final ACHDocument achFile = model.getAchFile();
        ACHAddendaDialog dialog = new ACHAddendaDialog(new javax.swing.JFrame(), true, achFile.getBatches()
                .get(batchPosition).getEntryRecs().get(entryPosition).getAddendaRecs().get(addendaPosition));
        dialog.setVisible(true);
        if (dialog.getButtonSelected() == ACHAddendaDialog.SAVE_BUTTON) {
            achFile.getBatches().get(batchPosition).getEntryRecs().get(entryPosition).getAddendaRecs()
                    .set(addendaPosition, dialog.getAchRecord());
            view.putRow(selectRow, dialog.getAchRecord());
            model.setAchFileDirty(true);
        }
    }

    public void editAchBatchControl(int position, int selectRow) {
        final ACHDocument achFile = model.getAchFile();
        ACHBatchControlDialog dialog = new ACHBatchControlDialog(new javax.swing.JFrame(), true,
                achFile.getBatches().get(position).getBatchControl());
        dialog.setVisible(true);
        if (dialog.getButtonSelected() == ACHBatchControlDialog.SAVE_BUTTON) {
            achFile.getBatches().get(position).setBatchControl(dialog.getAchRecord());
            view.putRow(selectRow, dialog.getAchRecord());
            model.setAchFileDirty(true);
        }
    }

    public void editAchBatchHeader(int position, int selectRow) {
        final ACHDocument achFile = model.getAchFile();

        final ACHRecordBatchHeader batchHeader = achFile.getBatches().get(position).getBatchHeader();
        ACHBatchHeaderDialog dialog = new ACHBatchHeaderDialog(new javax.swing.JFrame(), true, batchHeader);
        dialog.setVisible(true);
        if (dialog.getButtonSelected() == ACHBatchHeaderDialog.SAVE_BUTTON) {
            achFile.getBatches().get(position).setBatchHeader(dialog.getAchRecord());
            view.putRow(selectRow, dialog.getAchRecord());
            model.setAchFileDirty(true);
        }

    }

    public void editAchEntryDetail(int batchPosition, int entryPosition, int selectRow) {
        final ACHDocument achFile = model.getAchFile();
        ACHEntryDetailDialog dialog = new ACHEntryDetailDialog(new javax.swing.JFrame(), true,
                achFile.getBatches().get(batchPosition).getEntryRecs().get(entryPosition).getEntryDetail());
        dialog.setVisible(true);
        if (dialog.getButtonSelected() == ACHEntryDetailDialog.SAVE_BUTTON) {
            achFile.getBatches().get(batchPosition).getEntryRecs().get(entryPosition)
                    .setEntryDetail(dialog.getAchRecord());
            view.putRow(selectRow, dialog.getAchRecord());
            model.setAchFileDirty(true);
        }
    }

    public void editAchFileControl(int idx) {
        final ACHDocument achFile = model.getAchFile();

        ACHFileControlDialog dialog = new ACHFileControlDialog(new javax.swing.JFrame(), true,
                achFile.getFileControl());
        dialog.setVisible(true);
        if (dialog.getButtonSelected() == ACHFileControlDialog.SAVE_BUTTON) {
            final ACHRecordFileControl achRecord = dialog.getAchRecord();
            achFile.setFileControl(achRecord);
            view.putRow(idx, achRecord);
            model.setAchFileDirty(true);
            model.setAchFile(achFile);
        }
    }

    public void editAchFileHeader(int selectRow) {
        final ACHDocument achFile = model.getAchFile();
        ACHFileHeaderDialog dialog = new ACHFileHeaderDialog(new javax.swing.JFrame(), true, achFile.getFileHeader());
        dialog.setVisible(true);
        if (dialog.getButtonSelected() == ACHFileHeaderDialog.SAVE_BUTTON) {
            achFile.setFileHeader(dialog.getAchRecord());
            view.putRow(selectRow, dialog.getAchRecord());
            model.setAchFileDirty(true);
        }
        model.setAchFile(achFile);
    }

    public void editAchRecord(int selectRow) {
        char recordType = view.getRow(selectRow).getRecordTypeCode();
        Integer[] position = view.getPositions(selectRow);
        if (recordType == ACHRecord.FILE_HEADER_TYPE) {
            editAchFileHeader(selectRow);
        } else if (recordType == ACHRecord.FILE_CONTROL_TYPE) {
            editAchFileControl(selectRow);
        } else if (position.length == 1) {
            if (recordType == ACHRecord.BATCH_HEADER_TYPE) {
                editAchBatchHeader(position[0], selectRow);
            } else {
                editAchBatchControl(position[0], selectRow);
            }
        } else if (position.length == 2) {
            editAchEntryDetail(position[0], position[1], selectRow);
        } else {
            editAchAddenda(position[0], position[1], position[2], selectRow);
        }
    }

    public void loadAchData(File file) {
        view.setCursorWait();
        try {
            model.setAchFile(Main.parseFile(file));
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            view.showMessage(ex.getMessage());
        } finally {
            view.setCursorDefault();
        }
        Vector<String> errorMessages = model.getAchFile().getErrorMessages();
        if (errorMessages.size() == 0) {
            view.showMessage("File loaded without error");
        } else {
            final String msg = StringUtils.join(errorMessages, System.getProperty("line.separator", "\r\n"));
            view.showMessage(msg);
        }
        model.setAchFileDirty(false);
    }

    @Override
    public void onDeleteAchBatch() {
        final ACHDocument achFile = model.getAchFile();
        int[] selected = view.getSelectedRows();
        if (selected.length < 1) {
            view.showError("Cannot perform request", "No items selected ... cannot delete");
            return;
        }
        for (int i = 0; i < selected.length; i++) {
            ACHRecord achRecord = view.getRow(selected[i]);
            if (achRecord.isFileHeaderType() || achRecord.isFileControlType()) {
                view.showError("Cannot perform requested function",
                        "Cannot delete file header/control rows in selection list");
                return;
            }
        }
        // Remove them backwards to positions don't shift on us
        for (int i = selected.length - 1; i >= 0; i--) {
            Integer[] position = view.getPositions(selected[i]);
            if (position.length != 1) {
                // find batch headers -- skipp entry and addenda
            } else {
                ACHRecord achRecord = view.getRow(selected[i]);
                // only delete items that match the headers
                if (achRecord.isBatchHeaderType()) {
                    achFile.getBatches().remove(position[0].intValue());
                }
            }
        }
        model.setAchFileDirty(true);
        view.clearJListAchDataAchRecords();
        view.loadAchDataRecords();
        model.setSelectedRow(selected[0]);
    }

    public void onDeleteAchBatch(ACHEditorController achEditorController) {
        final ACHDocument achFile = model.getAchFile();
        int[] selected = view.getSelectedRows();
        if (selected.length < 1) {
            view.showError("Cannot perform request", "No items selected ... cannot delete");
            return;
        }
        for (int i = 0; i < selected.length; i++) {
            ACHRecord achRecord = view.getRow(selected[i]);
            if (achRecord.isFileHeaderType() || achRecord.isFileControlType()) {
                final String message = "Cannot delete file header/control rows " + "in selection list";
                final String title = "Cannot perform requested function";
                view.showError(message, title);
                return;
            }
        }
        // Remove them backwards to positions don't shift on us
        for (int i = selected.length - 1; i >= 0; i--) {
            Integer[] position = view.getPositions(selected[i]);
            if (position.length != 1) {
                // find batch headers -- skipp entry and addenda
            } else {
                ACHRecord achRecord = view.getRow(selected[i]);
                // only delete items that match the headers
                if (achRecord.isBatchHeaderType()) {
                    achFile.getBatches().remove(position[0].intValue());
                }
            }
        }
        model.setAchFileDirty(true);
        view.clearJListAchDataAchRecords();
        view.loadAchDataRecords();
        model.setSelectedRow(selected[0]);
    }

    @Override
    public void onDeleteAchEntryDetail() {
        final ACHDocument achFile = model.getAchFile();
        int[] selected = view.getSelectedRows();
        if (selected.length < 1) {
            view.showError("Cannot perform request", "No items selected ... cannot delete entry detail");
            return;
        }
        for (int i = 0; i < selected.length; i++) {
            ACHRecord achRecord = view.getRow(selected[i]);
            if (achRecord.isEntryDetailType() || achRecord.isAddendaType()) {
            } else {
                view.showError("Cannot perform requested function",
                        "Cannot delete entry detail -- non-entry/addenda rows in selection list");
                return;
            }
        }
        // Remove them backwards to positions don't shift on us
        for (int i = selected.length - 1; i >= 0; i--) {
            Integer[] position = view.getPositions(selected[i]);
            if (position.length != 2) {
                // problem -- this can only occur if there is a mismatch
                // between positions and jListAchDataAchRecords
                view.showError("Cannot perform requested function",
                        "Cannot delete entry detail -- row is not an entry row");
                return;
            } else {
                achFile.getBatches().get(position[0]).getEntryRecs().remove(position[1].intValue());
            }
        }
        model.setAchFileDirty(true);
        view.clearJListAchDataAchRecords();
        view.loadAchDataRecords();
        model.setSelectedRow(selected[0]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ach.editor.view.ACHEditorViewListener#exitProgram()
     */
    @Override
    public void onExitProgram() {
        final ACHDocument achFile = model.getAchFile();
        if (model.isAchFileDirty()) {
            final String title = "ACH File has changed";
            final String message = "ACH File has been changed? What would you like to do.";
            int selection = view.askSaveExitCancel(title, message);
            if (selection == 2) {
                // Selected cancel
                return;
            } else if (selection == 0) {
                try {
                    // achFile.setFedFile(view.jCheckBoxMenuFedFile.isSelected());
                    if (!saveFile()) {
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
    public void onFileNew() {
        if (model.isAchFileDirty()) {
            int selection = view.askSaveContinueCancel("ACH File has changed",
                    "ACH File has been changed. What would you like to do.");
            if (selection == 2) {
                // Selected cancel
                return;
            } else if (selection == 0) {
                try {
                    // achFile.setFedFile(view.jCheckBoxMenuFedFile.isSelected());
                    if (!saveFile()) {
                        return;
                    }
                } catch (Exception ex) {
                    view.showError("Error saving file", "Failure saving file -- \n" + ex.getMessage());
                    return;
                }
            }
        }
        {
            final ACHDocument brandNewAchFile = new ACHDocument();
            brandNewAchFile.addBatch(new ACHBatch());
            brandNewAchFile.recalculate();
            model.setAchFile(brandNewAchFile);
            // Update display with new data
            model.setAchFileDirty(false); // Don't care if these records get
                                          // lost
        }
    }

    @Override
    public void onFileOpen() {
        final String filePath = view.jLabelAchInfoFileName.getText();
        if (model.isAchFileDirty()) {
            int selection = view.askSaveContinueCancel("ACH File has changed",
                    "ACH File has been changed. What would you like to do.");
            if (selection == 2) {
                // Selected cancel
                return;
            } else if (selection == 0) {
                try {
                    ACHDocument achFile = model.getAchFile();
                    // achFile.setFedFile(view.jCheckBoxMenuFedFile.isSelected());
                    model.setAchFile(achFile);
                    if (!IOUtils.save(filePath, achFile)) {
                        return;
                    }
                } catch (Exception ex) {
                    view.showError("Error saving file", "Failure saving file -- \n" + ex.getMessage());
                    return;
                }
            }
        }

        JFileChooser chooser = new JFileChooser(new File(filePath).getParent());
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setApproveButtonText("Open");

        int returnVal = chooser.showOpenDialog(view.getContentPane());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            view.jLabelAchInfoFileName.setText(chooser.getSelectedFile().getAbsolutePath());

            loadAchData(chooser.getSelectedFile());
        }
    }

    @Override
    public void onFileSave() {
        try {
            // achFile.setFedFile(view.jCheckBoxMenuFedFile.isSelected());
            if (saveFile()) {
                model.setAchFileDirty(false);
            }
        } catch (Exception ex) {
            view.showError("Error writing file", "Unable to save ACH data to fileName. Reason: " + ex.getMessage());
        }
    }

    @Override
    public void onFileSaveAs() {
        final ACHDocument achFile = model.getAchFile();

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
                // achFile.setFedFile(view.jCheckBoxMenuFedFile.isSelected());
                if (saveFile()) {
                    model.setAchFileDirty(false);
                }
            } catch (Exception ex) {
                view.showError("Error writing file", "Unable to save ACH data to fileName. Reason: " + ex.getMessage());
            }
        }
    }

    @Override
    public void onIsFedFile() {
        ACHDocument achFile = model.getAchFile();
        achFile.setFedFile(!achFile.isFedFile());
        model.setAchFile(achFile);
    }

    @Override
    public void onItemMultipleDelete() {
        int itemAtMouse = view.clickedIndex();
        deleteAchRecord(itemAtMouse);
    }

    @Override
    public void onItemPopupAddendaCopy() {
        int[] selected = view.getSelectedRows();
        view.copy(selected);
    }

    @Override
    public void onItemPopupAddendaDelete() {
        int itemAtMouse = view.clickedIndex();
        deleteAchRecord(itemAtMouse);
    }

    @Override
    public void onItemPopupAddendaPaste() {
        // TODO Auto-generated method stub
        throw new RuntimeException("not implemented");
    }

    @Override
    public void onItemPopupBatchCopy() {
        int[] selected = view.getSelectedRows();
        view.copy(selected);
    }

    @Override
    public void onItemPopupBatchEditBatch() {
        int itemAtMouse = view.clickedIndex();
        editAchRecord(itemAtMouse);
    }

    @Override
    public void onItemPopupBatchPaste() {
        // TODO Auto-generated method stub
        throw new RuntimeException("not implemented");
    }

    @Override
    public void onItemPopupEntryCopy() {
        int[] selected = view.getSelectedRows();
        view.copy(selected);
    }

    @Override
    public void onItemPopupEntryEditEntry() {
        int itemAtMouse = view.clickedIndex();
        editAchRecord(itemAtMouse);
    }

    @Override
    public void onItemPopupEntryPaste() {
        // TODO Auto-generated method stub
        throw new RuntimeException("not implemented");
    }

    @Override
    public void onItemPopupFileEdit() {
        int itemAtMouse = view.clickedIndex();
        editAchRecord(itemAtMouse);
    }

    @Override
    public void onItemPopupMultipleCopy() {
        int[] selected = view.getSelectedRows();
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
    public void onItemToolsRecalculate() {
        final ACHDocument achFile = model.getAchFile();

        view.setCursorWait();
        if (!achFile.recalculate()) {
            view.showMessage("Unable to fully recalculate ... run Validate tool");
        }
        view.clearJListAchDataAchRecords();
        view.loadAchDataRecords();
        model.setAchFile(achFile);
        model.setAchFileDirty(true);
        view.setCursorDefault();
    }

    @Override
    public void onItemToolsReverse() {
        final ACHDocument achFile = model.getAchFile();
        achFile.reverse();
        view.clearJListAchDataAchRecords();
        model.setAchFile(achFile);
        view.loadAchDataRecords();
        model.setAchFileDirty(true);
    }

    @Override
    public void onItemToolsValidate() {
        final ACHDocument achFile = model.getAchFile();
        view.setCursorWait();
        Vector<String> messages = achFile.validate();
        view.setCursorDefault();

        if (messages.size() > 0) {
            final String msg = StringUtils.join(messages, "\n");
            view.showMessage(msg);
        }
    }

    @Override
    public void onListClick(int itemAtMouse, int clickCount, int button) {// GEN-FIRST:event_jListAchDataAchRecordsMouseClicked
        int[] selected = view.getSelectedRows();
        boolean found = false;
        for (int i = 0; i < selected.length && (!found); i++) {
            if (itemAtMouse == selected[i]) {
                found = true;
            }
        }
        if (!found) {
            model.setSelectedRow(itemAtMouse);
            selected = view.getSelectedRows();
        }

        if (selected.length < 1) {
            return;
        }
        if (clickCount == 2 && button == MouseEvent.BUTTON1) {
            editAchRecord(selected[0]);
            return;
        }

        if (clickCount == 1 && button == MouseEvent.BUTTON3) {
            processRightClick(selected);
            return;
        }

    }

    public void processRightClick(int[] selected) {
        ACHRecord achRecord = clickedRecord();
        JPopupMenu dialog;
        if (selected.length == 1) {
            if (achRecord.isEntryDetailType()) {
                dialog = view.jPopupMenuEntry;
            } else if (achRecord.isAddendaType()) {
                dialog = view.jPopupMenuAddenda;
            } else if (achRecord.isBatchControlType() || achRecord.isBatchHeaderType()) {
                dialog = view.jPopupMenuBatch;
            } else if (achRecord.isFileControlType() || achRecord.isFileHeaderType()) {
                dialog = view.jPopupMenuFile;
            } else {
                throw new RuntimeException("not supported");
            }
        } else {
            dialog = view.jPopupMenuMultipleSelection;
        }
        view.showDialog(dialog);
    }

    /**
     * @return
     */
    private boolean saveFile() {
        final ACHDocument achFile = model.getAchFile();
        try {
            return IOUtils.save(view.jLabelAchInfoFileName.getText(), achFile);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
}
