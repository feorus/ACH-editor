/**
 * 
 */
package com.ach.achViewer;

import com.ach.achViewer.model.ACHEditorModel;

/**
 * @author ilyakharlamov
 *
 */
public class ACHEditorController {

    private final ACHEditorModel model;
    private final ACHEditorView view;

    /**
     * @param model
     * @param viewer
     */
    public ACHEditorController(ACHEditorModel model, ACHEditorView view) {
        this.model = model;
        this.view = view;
    }

}
