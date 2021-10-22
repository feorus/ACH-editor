package com.ach.editor.view;

import com.ach.editor.model.ACHEditorModel;
import com.ach.editor.model.ModelListener;

import javax.swing.*;
import javax.swing.event.ListDataListener;

class AchRecordsModel extends AbstractListModel {
    private final ACHEditorModel model;

    public AchRecordsModel(ACHEditorModel model) {
        this.model = model;
    }

    @Override
    public int getSize() {
        return model.getAchRecords().size();
    }

    @Override
    public Object getElementAt(int index) {
        return model.getAchRecords().get(index).getAchRecord();
    }

    @Override
    public void addListDataListener(ListDataListener l) {
    }

    @Override
    public void removeListDataListener(ListDataListener l) {

    }
}
