/**
 * 
 */
package com.ach.achViewer.model;

/**
 * @author ilyakharlamov
 *
 */
public interface ModelSubscriber {

    void onFileDirty();

    void onSetTitle();

}
