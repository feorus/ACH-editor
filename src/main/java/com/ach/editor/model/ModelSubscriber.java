/**
 * 
 */
package com.ach.editor.model;

/**
 * @author ilyakharlamov
 *
 */
public interface ModelSubscriber {

    void onSetFileDirty();

    void onSetTitle();

}
