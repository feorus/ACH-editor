/**
 * 
 */
package com.ach.editor.view;

/**
 * @author ilyakharlamov
 *
 */
public enum DoYouWantToSaveTheChangesDialogOptions {

    SAVE("Save"),
    CANCEL("Cancel"),
    DONT_SAVE("Don't Save");
    
    
    private final String label;

    private DoYouWantToSaveTheChangesDialogOptions(String label) {
        this.label = label;
    }

}
