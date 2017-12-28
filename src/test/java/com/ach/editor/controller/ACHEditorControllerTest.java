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
import com.ach.editor.view.DoYouWantToSaveTheChangesDialogOptions;

/**
 * @author ilyakharlamov
 *
 */
public class ACHEditorControllerTest {
    ACHEditorController controller;
    ACHEditorModel model;
    ACHEditorView view;
    IOWorld ioWorld;
    
    File file;
    ACHDocument achDocument;
    
    @Before
    public void setUp() {
        view = Mockito.mock(ACHEditorView.class);
        ioWorld = Mockito.mock(IOWorld.class);
        model = new ACHEditorModel();
        controller = new ACHEditorController(model, view, ioWorld);
        file = Mockito.mock(File.class);
        achDocument = new ACHDocument();
        Mockito.when(ioWorld.load(file)).thenReturn(achDocument);
    }

    @Test
    public void when_file_modified_then_ask_confirmation_dialog() {
        controller.loadDocumentFromFile(file);
        controller.onIsFedFile();
        controller.onFileNew();
        Mockito.verify(view, Mockito.times(1)).askDoYouWantSaveChanges();
    }
    
    @Test
    public void when_batch_modified_then_ask_confirmatino_dialog() {
        file = Mockito.mock(File.class);
        achDocument = new ACHDocument();
        Mockito.when(ioWorld.load(file)).thenReturn(achDocument);
        controller.loadDocumentFromFile(file);
        controller.onIsFedFile();
        controller.onExitProgram();
        Mockito.verify(view, Mockito.times(1)).askDoYouWantSaveChanges();
    }
    
    @Test
    public void when_modified_and_answered_save_then_io_saved_and_new_document() {
        Mockito.when(view.askDoYouWantSaveChanges()).thenReturn(DoYouWantToSaveTheChangesDialogOptions.SAVE);
        controller.loadDocumentFromFile(file);
        assertEquals(achDocument, model.getAchFile());
        controller.onIsFedFile();
        controller.onFileNew();
        Mockito.verify(view, Mockito.times(1)).askDoYouWantSaveChanges();
        Mockito.verify(ioWorld, Mockito.times(1)).save(file, achDocument);
        assertNotEquals(achDocument, model.getAchFile());
    }
    
    @Test
    public void when_modified_and_answered_dontsave_then() {
        Mockito.when(view.askDoYouWantSaveChanges()).thenReturn(DoYouWantToSaveTheChangesDialogOptions.DONT_SAVE);
        controller.loadDocumentFromFile(file);
        assertEquals(achDocument, model.getAchFile());
        controller.onIsFedFile();
        controller.onFileNew();
        Mockito.verify(view, Mockito.times(1)).askDoYouWantSaveChanges();
        Mockito.verify(ioWorld, Mockito.never()).save(file, achDocument);
        assertNotEquals(achDocument, model.getAchFile());
    }
    
    @Test
    public void when_modified_and_answered_cancel_then_no_io_called_and_document_is_the_same() {
        Mockito.when(view.askDoYouWantSaveChanges()).thenReturn(DoYouWantToSaveTheChangesDialogOptions.CANCEL);
        controller.loadDocumentFromFile(file);
        assertEquals(achDocument, model.getAchFile());
        controller.onIsFedFile();
        controller.onFileNew();
        Mockito.verify(view, Mockito.times(1)).askDoYouWantSaveChanges();
        Mockito.verify(ioWorld, Mockito.never()).save(file, achDocument);
        assertEquals(achDocument, model.getAchFile());
    }
    
    @Test
    public void when_not_modified_and_new_file_then_file_shoulde_be_new () {
        Mockito.when(view.askDoYouWantSaveChanges()).thenReturn(DoYouWantToSaveTheChangesDialogOptions.CANCEL);
        controller.loadDocumentFromFile(file);
        assertEquals(achDocument, model.getAchFile());
        controller.onFileNew();
        Mockito.verify(view, Mockito.never()).askDoYouWantSaveChanges();
        assertNotEquals(achDocument, model.getAchFile());
    }

}
