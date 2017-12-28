/**
 * 
 */
package com.ach.editor.model;

/**
 * @author ilyakharlamov
 *
 */
public interface ModelListener {

    void onSetFileDirty();

    void onSetSelectedRow();

    void onSetAchFile();

    void onSetOutputFile();

}
