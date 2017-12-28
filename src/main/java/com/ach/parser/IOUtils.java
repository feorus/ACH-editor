/**
 * 
 */
package com.ach.parser;

import java.io.File;
import java.io.FileWriter;
import java.util.Vector;

import com.ach.domain.ACHBatch;
import com.ach.domain.ACHDocument;
import com.ach.domain.ACHEntry;
import com.ach.domain.ACHRecordAddenda;

/**
 * @author ilyakharlamov
 *
 */
public class IOUtils {

    public static boolean save(String filename, ACHDocument achDocument) throws Exception {
    
        // Create file for writing
        FileWriter fileWriter = new FileWriter(new File(filename));
        String record = "";
        int rowCount = 0;
    
        String recDelimiter = System.getProperty("line.separator", "\r\n");
        if (achDocument.isFedFile()) {
            recDelimiter = "";
        }
    
        // Write ACH File Header
        record = achDocument.getFileHeader().toString();
        fileWriter.write(record + recDelimiter);
        rowCount++;
    
        Vector<ACHBatch> batches = achDocument.getBatches();
        // Output each ACH Batch
        for (int i = 0; i < batches.size(); i++) {
            // Write ACH Batch Header
            record = batches.get(i).getBatchHeader().toString();
            fileWriter.write(record + recDelimiter);
            rowCount++;
    
            // Output each entry
            Vector<ACHEntry> achEntries = batches.get(i).getEntryRecs();
            for (int j = 0; j < achEntries.size(); j++) {
                // Output entry detail
                record = achEntries.get(j).getEntryDetail().toString();
                fileWriter.write(record + recDelimiter);
                rowCount++;
                // Output each addenda
                Vector<ACHRecordAddenda> achAddendas = achEntries.get(j)
                        .getAddendaRecs();
                for (int k = 0; k < achAddendas.size(); k++) {
                    // output addenda
                    record = achAddendas.get(k).toString();
                    fileWriter.write(record + recDelimiter);
                    rowCount++;
                }
            }
            // Output bach control
            record = batches.get(i).getBatchControl().toString();
            fileWriter.write(record + recDelimiter);
            rowCount++;
        }
        // output file control
        record = achDocument.getFileControl().toString();
        fileWriter.write(record + recDelimiter);
        rowCount++;
    
        try {
            int blockSize = Integer.parseInt(achDocument.getFileHeader()
                    .getBlockingFactor());
            int neededRows = blockSize - (rowCount % blockSize);
            if (neededRows > 0) {
                int recordSize = Integer.parseInt(achDocument.getFileHeader()
                        .getRecordSize());
                StringBuffer outputRecord = new StringBuffer("");
                for (int i = 0; i < recordSize; i++) {
                    outputRecord.append("9");
                }
                for (int i = 0; i < neededRows; i++) {
                    fileWriter.write(outputRecord.toString() + recDelimiter);
                }
            }
        } catch (Exception ex) {
            System.err.println("Unable to output trailing control records -- "
                    + ex.getMessage());
            ex.printStackTrace();
        }
    
        fileWriter.flush();
        fileWriter.close();
    
        return true;
    }

}
