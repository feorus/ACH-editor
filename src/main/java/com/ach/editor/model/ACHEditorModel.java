/**
 * 
 */
package com.ach.editor.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.ach.domain.ACHBatch;
import com.ach.domain.ACHEntry;
import com.ach.domain.ACHRecordAddenda;
import com.ach.editor.view.RecordAndPositions;
import org.slf4j.Logger;

import com.ach.domain.ACHDocument;

/**
 * @author ilyakharlamov
 *
 */
public class ACHEditorModel {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(ACHEditorModel.class);

    private ACHDocument achFile;

    private boolean achFileDirty;

    private File outputFile;

    private int selectedRow;

    private List<ModelListener> subscribers;

    public ACHEditorModel() {
        super();
        this.subscribers = new ArrayList<>();
    }

    public List<RecordAndPositions> getAchRecords() {
        List<RecordAndPositions> items = new ArrayList<>();

        items.add(new RecordAndPositions(achFile.getFileHeader(), new Integer[0]));

        Vector<ACHBatch> achBatches = achFile.getBatches();
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
        items.add(new RecordAndPositions(achFile.getFileControl(),new Integer[0]));
        return items;
    }

    public void addSubscriber(ModelListener subscriber) {
        this.subscribers.add(subscriber);
    }

    public ACHDocument getAchFile() {
        return achFile;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public boolean isAchFileDirty() {
        return achFileDirty;
    }

    public void setAchDocument(ACHDocument achFile) {
        LOG.debug("setAchFile({})", achFile);
        this.achFile = achFile;
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

    public void setSelectedRow(int selectedRow) {
        LOG.debug("setSelectedRow({})", selectedRow);
        this.selectedRow = selectedRow;
        for (ModelListener s : subscribers) {
            s.onSetSelectedRow();
        }
    }

}
