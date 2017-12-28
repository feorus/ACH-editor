/**
 * 
 */
package com.ach.editor.controller;

import java.io.File;

import com.ach.domain.ACHDocument;

/**
 * @author ilyakharlamov
 *
 */
public interface IOWorld {

    /**
     * @param outputFile
     * @param achFile
     * @return
     */
    boolean save(File outputFile, ACHDocument achFile);

    ACHDocument load(File file);

}
