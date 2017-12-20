package com.ach.parser;
import java.util.List;
import java.util.Vector;

import com.ach.domain.ACHBatch;
import com.ach.domain.ACHEntry;
import com.ach.domain.ACHFile;
import com.ach.domain.ACHRecord;
import com.ach.domain.ACHRecordAddenda;
import com.ach.domain.ACHRecordBatchControl;
import com.ach.domain.ACHRecordBatchHeader;
import com.ach.domain.ACHRecordEntryDetail;
import com.ach.domain.ACHRecordFileControl;
import com.ach.domain.ACHRecordFileHeader;

public class ACHViewerFileParser implements AchFileParser {

	@Override
	public ACHFile fromLines(List<String> lines) throws AchParserException {
	    final Vector<String> errorMessages = new Vector<String>(10, 10);

		final ACHFile achFile = new ACHFile();
		boolean foundFileControl = false;
        int rowCount = 0;

        if (lines.isEmpty()) {
            throw new AchParserException("Data is empty");
        }

        if (!lines.get(0).substring(0, 1).equals("1")) {
            throw new AchParserException("Data is not an ACH file.  First character must be a \"1\"");
        }

        ACHBatch achBatch = null;
        ACHEntry achEntry = null;
        int recLength = 94;
        for (String record : lines) {
            rowCount++;
            for (int recStart = 0; recStart < record.length(); recStart += recLength) {
                int endRec = recStart + recLength;
                if (endRec > record.length()) {
                    endRec = record.length();
                }
                ACHRecord achRecord = ACHRecord.parseACHRecord(record
                        .substring(recStart, endRec));
                if (achRecord.isFileHeaderType()) {
                	achFile.setFileHeader((ACHRecordFileHeader) achRecord);
                    recLength = Integer.parseInt(achFile.getFileHeader()
                            .getRecordSize());
                    achEntry = null;
                    achBatch = null;
                } else if (achRecord.isFileControlType()) {
                    if (achEntry != null) {
                        achBatch.addEntryRecs(achEntry);
                        achEntry = null;
                    }
                    if (achBatch != null) {
                    	achFile.addBatch(achBatch);
                        achBatch = null;
                    }
                    if (!foundFileControl) {
                    	achFile.setFileControl((ACHRecordFileControl) achRecord);
                        foundFileControl = true;
                    }
                } else if (achRecord.isBatchHeaderType()) {
                    if (achEntry != null) {
                        achBatch.addEntryRecs(achEntry);
                        achEntry = null;
                    }
                    if (achBatch != null) {
                    	achFile.addBatch(achBatch);
                        achBatch = null;
                    }

                    achBatch = new ACHBatch();
                    achBatch
                            .setBatchHeader((ACHRecordBatchHeader) achRecord);
                } else if (achRecord.isBatchControlType()) {
                    if (achEntry != null) {
                        achBatch.addEntryRecs(achEntry);
                        achEntry = null;
                    }
                    if (achBatch == null) {
                        achBatch = new ACHBatch();
                    }
                    achBatch
                            .setBatchControl((ACHRecordBatchControl) achRecord);
                } else if (achRecord.isEntryDetailType()) {
                    if (achEntry != null) {
                        achBatch.addEntryRecs(achEntry);
                        achEntry = null;
                    }
                    if (achBatch == null) {
                        achBatch = new ACHBatch();
                    }
                    achEntry = new ACHEntry();
                    achEntry
                            .setEntryDetail((ACHRecordEntryDetail) achRecord);
                } else if (achRecord.isAddendaType()) {
                    if (achEntry == null) {
                        achEntry = new ACHEntry();
                    }
                    achEntry.addAddendaRecs((ACHRecordAddenda) achRecord);
                } else {
                    errorMessages.add("Invalid record at row " + rowCount);
                }
            }
        }
        if (rowCount == 1 && errorMessages.size() == 0) {
        	achFile.setFedFile(true);
        }
		return achFile;
	}


}
