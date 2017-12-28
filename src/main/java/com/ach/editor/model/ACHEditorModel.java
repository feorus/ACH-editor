/**
 * 
 */
package com.ach.editor.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    public void setAchFile(ACHDocument achFile) {
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
