/**
 * 
 */
package com.ach.editor.view;

import java.awt.event.ActionEvent;

/**
 * @author ilyakharlamov
 *
 */
public interface ACHEditorViewListener {
    void onFileOpen();

    void onFileNew();

    void addAchEntryDetail();

    void onExitProgram();

    void addAchBatch();

    void addAchAddenda();

    void onFileSaveAs();

    void onItemPopupAddendaPaste();

    void onItemPopupBatchPaste();

    void onFileSave();

    void onItemPopupAddendaDelete();

    void onItemPopupMultipleCopy();

    void onItemPopupAddendaCopy();

    void onItemPopupEntryEditEntry();

    void onItemMultipleDelete();

    void onItemPopupFileEdit();

    void onItemToolsValidate();

    void onItemToolsRecalculate();

    void onDeleteAchEntryDetail();

    void onItemPopupEntryPaste();

    void onItemToolsReverse();

    void onItemPopupBatchCopy();

    void onItemPopupEntryCopy();

    void onItemPopupPasteMultiple();

    void onItemPopupBatchEditBatch();

    void onDeleteAchBatch();
}
