package com.ach.editor.view;

import com.ach.editor.model.ACHEditorModel;

import javax.swing.*;

public class AchRecordsSelectionModel extends DefaultListSelectionModel{
    private final ACHEditorModel model;

    public AchRecordsSelectionModel(ACHEditorModel model) {
        this.model = model;
    }
}