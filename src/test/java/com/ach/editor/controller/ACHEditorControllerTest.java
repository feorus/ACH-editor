/**
 * 
 */
package com.ach.editor.controller;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.ach.domain.ACHDocument;
import com.ach.editor.model.ACHEditorModel;
import com.ach.editor.view.ACHEditorView;

/**
 * @author ilyakharlamov
 *
 */
public class ACHEditorControllerTest {
    ACHEditorController controller;
    ACHEditorModel model;
    ACHEditorView view;
    IOWorld ioWorld;
    
    @Before
    public void setUp() {
        view = Mockito.mock(ACHEditorView.class);
        ioWorld = Mockito.mock(IOWorld.class);
        model = new ACHEditorModel();
        controller = new ACHEditorController(model, view, ioWorld);

    }

    @Test
    public void when_file_modified_then_ask_confirmation_dialog() {
        final File file = Mockito.mock(File.class);
        Mockito.when(ioWorld.load(file)).thenReturn(new ACHDocument());
        controller.loadDocumentFromFile(file);
        controller.onIsFedFile();
        controller.onFileNew();
        Mockito.verify(view, Mockito.times(1)).askDoYouWantSaveChanges(Mockito.anyString(), Mockito.anyString());
    }
    
    @Test
    public void when_batch_modified_then_ask_confirmatino_dialog() {
        final File file = Mockito.mock(File.class);
        Mockito.when(ioWorld.load(file)).thenReturn(new ACHDocument());
        controller.loadDocumentFromFile(file);
        controller.onIsFedFile();
        controller.onExitProgram();
        Mockito.verify(view, Mockito.times(1)).askDoYouWantSaveChanges(Mockito.anyString(), Mockito.anyString());
    }

}
