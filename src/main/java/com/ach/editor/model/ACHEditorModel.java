/**
 * 
 */
package com.ach.editor.model;

import java.io.File;
import java.util.*;

import com.ach.domain.*;
import com.ach.editor.view.RecordAndPositions;
import org.slf4j.Logger;
import com.google.common.collect.ImmutableList;
/**
 * @author ilyakharlamov
 *
 */
public class ACHEditorModel {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(ACHEditorModel.class);

    private ACHDocument achDocument;

    private boolean achFileDirty;

    private File outputFile;

    private List<ModelListener> subscribers;

    private int[] selectedRows;
    private String searchText = "";

    public ACHEditorModel() {
        super();
        this.subscribers = new ArrayList<>();
        selectedRows = new int[]{};
    }

    public ImmutableList<RecordAndPositions> getAchRecords() {
        if (achDocument == null) {
            LOG.info("no ach records");
            return ImmutableList.of();
        }
        ImmutableList.Builder<RecordAndPositions> items = ImmutableList.<RecordAndPositions>builder();
        items.add(new RecordAndPositions(achDocument.getFileHeader(), new Integer[0]));

        Vector<ACHBatch> achBatches = achDocument.getBatches();
        for (int i = 0; i < achBatches.size(); i++) {
            items.add(new RecordAndPositions(achBatches.get(i).getBatchHeader(), new Integer[] { i }));
            Vector<ACHEntry> achEntries = achBatches.get(i).getEntryRecs();
            for (int j = 0; j < achEntries.size(); j++) {
                items.add(new RecordAndPositions(achEntries.get(j).getEntryDetail(), new Integer[] { i, j }));
                Vector<ACHRecordAddenda> achAddendas = achEntries.get(j)
                        .getAddendaRecs();
                for (int k = 0; k < achAddendas.size(); k++) {
                    items.add(new RecordAndPositions(achAddendas.get(k), new Integer[] { i, j, k }));
                }
            }
            items.add(new RecordAndPositions(achBatches.get(i).getBatchControl(),new Integer[] { i }));
        }
        items.add(new RecordAndPositions(achDocument.getFileControl(),new Integer[0]));
        return items.build();
    }

    public void addSubscriber(ModelListener subscriber) {
        this.subscribers.add(subscriber);
    }

    public ACHDocument getAchDocument() {
        return achDocument;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public boolean isAchFileDirty() {
        return achFileDirty;
    }

    public void setAchDocument(ACHDocument achDocument) {
        LOG.debug("setAchDocument({})", achDocument);
        this.achDocument = achDocument;
        for (ModelListener s : subscribers) {
            s.onSetAchFile();
        }
    }

    public void setAchFileDirty(boolean achFileDirty) {
        LOG.debug("setAchFileDirty({})", achFileDirty);
        this.achFileDirty = achFileDirty;
        for (ModelListener subscriber : subscribers) {
            subscriber.onSetFileDirty();
        }
    }

    public void setOutputFile(File outputFile) {
        LOG.debug("setOutputFile({})", outputFile);
        this.outputFile = outputFile;
        for (ModelListener s : subscribers) {
            s.onSetOutputFile();
        }
    }

    public void setSelectedRows(int[] selectedRows) {
        LOG.debug("setSelectedRows({})", selectedRows);
        this.selectedRows = selectedRows;
        for (ModelListener s : subscribers) {
            s.onSetSelectedRow();
        }
    }

    public int[] getSelectedRows() {
        return this.selectedRows;
    }

    public void setSearchText(String text) {
        this.searchText = text;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setFileHeader(ACHRecordFileHeader fileHeader) {
        achDocument.setFileHeader(fileHeader);
        setAchFileDirty(true);
    }

    public void setBatchHeader(int batchIndex, ACHRecordBatchHeader achBatchHeader) {
        achDocument.getBatches().get(batchIndex).setBatchHeader(achBatchHeader);
        setAchFileDirty(true);
    }

    public void setEntryDetail(int batchIndex, int entryIndex, ACHRecordEntryDetail entryDetail) {
        achDocument.getBatches().get(batchIndex).getEntryRecs().get(entryIndex).setEntryDetail(entryDetail);
        setAchFileDirty(true);
    }

    public void setAddenda(int batchIndex, int entryIndex, int addendaIndex, ACHRecordAddenda achRecord) {
        Vector<ACHRecordAddenda> existingAddendas = achDocument.getBatches().get(batchIndex).getEntryRecs().get(entryIndex).getAddendaRecs();
        existingAddendas.set(addendaIndex, achRecord);
        achDocument.getBatches().get(batchIndex).getEntryRecs().get(entryIndex).setAddendaRecs(existingAddendas);
        setAchFileDirty(true);
    }

    public void setBatchControl(int batchIndex, ACHRecordBatchControl batchControl) {
        achDocument.getBatches().get(batchIndex).setBatchControl(batchControl);
        setAchFileDirty(true);
    }

    public void setFileControl(ACHRecordFileControl fileControl) {
        achDocument.setFileControl(fileControl);
        setAchFileDirty(true);
    }
}
