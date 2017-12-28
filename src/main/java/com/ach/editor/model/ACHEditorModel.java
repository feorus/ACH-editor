/**
 * 
 */
package com.ach.editor.model;

import java.util.ArrayList;
import java.util.List;

import com.ach.domain.ACHFile;

/**
 * @author ilyakharlamov
 *
 */
public class ACHEditorModel {
    public ACHFile getAchFile() {
        return achFile;
    }

    public void setAchFile(ACHFile achFile) {
        this.achFile = achFile;
    }



    private boolean achFileDirty;

    private String title;

    private ACHFile achFile;

    private List<ModelListener> subscribers;

    public ACHEditorModel() {
        super();
        this.subscribers = new ArrayList<>();
    }

    public boolean isAchFileDirty() {
        return achFileDirty;
    }

    public void setAchFileDirty(boolean achFileDirty) {
        this.achFileDirty = achFileDirty;
        for (ModelListener subscriber: subscribers) {
            subscriber.onSetFileDirty();
        }
    }

    /**
     * @param fileName
     */
    public void setTitle(String title) {
       this.title = title;
       for (ModelListener s: subscribers) {
           s.onSetTitle();
       }
    }

    public String getTitle() {
        return title;
    }

    /**
     * 
     */
    public void addSubscriber(ModelListener subscriber) {
        this.subscribers.add(subscriber);
    }

}
