/**
 * 
 */
package com.ach.editor.model;

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

    private int selectedRow;

    private List<ModelListener> subscribers;

    private String title;

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

    public int getSelectedRow() {
        return selectedRow;
    }

    public String getTitle() {
        return title;
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

    public void setSelectedRow(int selectedRow) {
        LOG.debug("setSelectedRow({})", selectedRow);
        this.selectedRow = selectedRow;
        for (ModelListener subscriber : subscribers) {
            subscriber.onSetSelectedRow();
        }
    }

    public void setTitle(String title) {
        LOG.debug("setTitle({})", title);

        this.title = title;
        for (ModelListener s : subscribers) {
            s.onSetTitle();
        }
    }

}
