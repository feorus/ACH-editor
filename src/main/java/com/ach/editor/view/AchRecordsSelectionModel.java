package com.ach.editor.view;

import com.ach.editor.model.ACHEditorModel;
import javafx.scene.control.SelectionMode;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;

public class AchRecordsSelectionModel extends DefaultListSelectionModel{
    private final ACHEditorModel model;

    public AchRecordsSelectionModel(ACHEditorModel model) {
        this.model = model;
    }
}