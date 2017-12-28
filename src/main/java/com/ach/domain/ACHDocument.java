package com.ach.domain;

import java.util.Vector;

public class ACHDocument {

    private ACHRecordFileHeader fileHeader = null;

    private ACHRecordFileControl fileControl = null;

    // Designates whether the file is flat or contains carriage returns
    private boolean isFedFile = false;

    private Vector<ACHBatch> batches = new Vector<ACHBatch>(10, 10);

    private Vector<String> errorMessages = new Vector<String>(10, 10);

    private class BatchTotals {

        public long recordCount = 0;

        public long batchCount = 0;

        public long entryAddendaCount = 0;

        public long debitDollarAmount = 0;

        public long creditDollarAmount = 0;

        public long entryHash = 0;

    }

    public ACHDocument() {
        setFileHeader(new ACHRecordFileHeader());
        setFileControl(new ACHRecordFileControl());
        batches = new Vector<ACHBatch>(10, 10);
    }

    public BatchTotals getBatchTotals() throws Exception {
        BatchTotals retValue = new BatchTotals();
        try {
            retValue.batchCount = getBatches().size();
            for (int i = 0; i < getBatches().size(); i++) {
                // Add the number of entry/addenda counts plus two for batch
                // header and control for the nbr of records. Needed to calc
                // block count
                retValue.recordCount += Long.parseLong(getBatches().get(i)
                        .getBatchControl().getEntryAddendaCount()) + 2;

                retValue.entryAddendaCount += Long.parseLong(getBatches()
                        .get(i).getBatchControl().getEntryAddendaCount());
                retValue.entryHash += Long.parseLong(getBatches().get(i)
                        .getBatchControl().getEntryHash());
                retValue.creditDollarAmount += Long.parseLong(getBatches().get(
                        i).getBatchControl().getTotCreditDollarAmt());
                retValue.debitDollarAmount += Long.parseLong(getBatches()
                        .get(i).getBatchControl().getTotDebitDollarAmt());
            }
            // Restrict hash to the first 10 characters

            retValue.entryHash = Long.parseLong(ACHRecord.formatACHDecimal(
                    String.valueOf(retValue.entryHash), "0000000000"));
        } catch (Exception ex) {
            throw ex;
        }
        // Add two more for file header and control
        retValue.recordCount += 2;
        return retValue;
    }

    public boolean recalculate() {

        boolean retvalue = true;
        try {

            // Recalc all batches
            for (int i = 0; i < getBatches().size() || !retvalue; i++) {
                retvalue = getBatches().get(i).recalc();
            }

            if (!retvalue) {
                // unable to calculcate batches for some reason
                // should run validate to get a list of errors
                return false;
            }

            BatchTotals totals = getBatchTotals();
            long recsPerBlock = Long.parseLong(fileHeader.getBlockingFactor());
            long calcBlockCount = totals.recordCount / recsPerBlock;
            if ((totals.recordCount % recsPerBlock) > 0) {
                calcBlockCount++;
            }

            fileControl.setEntryAddendaCount(String
                    .valueOf(totals.entryAddendaCount));
            fileControl.setTotDebitDollarAmt(String
                    .valueOf(totals.debitDollarAmount));
            fileControl.setTotCreditDollarAmt(String
                    .valueOf(totals.creditDollarAmount));
            fileControl.setEntryHash(String.valueOf(totals.entryHash));
            fileControl.setBatchCount(String.valueOf(totals.batchCount));
            fileControl.setBlockCount(String.valueOf(calcBlockCount));

        } catch (Exception ex) {
            ex.printStackTrace();
            retvalue = false;
        }
        return retvalue;
    }

    public boolean reverse() {

        // Reverse all batches
        for (int i = 0; i < getBatches().size(); i++) {
            getBatches().get(i).reverse();
        }

        return recalculate();
    }

    public Vector<String> validate() {

        Vector<String> retvalue = new Vector<String>(10, 10);
        try {

            // Get all batch errors
            for (int i = 0; i < getBatches().size(); i++) {
                Vector<String> batchMessages = getBatches().get(i).validate();
                retvalue.addAll(batchMessages);
            }

            long entryAddendaCount = Long.parseLong(fileControl
                    .getEntryAddendaCount());
            long entryDebitDollarAmount = Long.parseLong(fileControl
                    .getTotDebitDollarAmt());
            long entryCreditDollarAmount = Long.parseLong(fileControl
                    .getTotCreditDollarAmt());
            long entryHash = Long.parseLong(fileControl.getEntryHash());
            long batchCount = Long.parseLong(fileControl.getBatchCount());
            long blockCount = Long.parseLong(fileControl.getBlockCount());

            BatchTotals totals = new BatchTotals();
            try {
                totals = getBatchTotals();
            } catch (Exception ex) {
                retvalue.add("Error calculating batch totals");
            }
            long recsPerBlock = Long.parseLong(fileHeader.getBlockingFactor());
            long calcBlockCount = totals.recordCount / recsPerBlock;
            if ((totals.recordCount % recsPerBlock) > 0) {
                calcBlockCount++;
            }

            if (totals.entryAddendaCount != entryAddendaCount) {
                retvalue.add("File count is out of balance -- file: "
                        + entryAddendaCount + "  Calced: "
                        + totals.entryAddendaCount);
                ;
            }
            if (entryDebitDollarAmount != totals.debitDollarAmount) {
                retvalue.add("File debits are out of balance -- file: "
                        + entryDebitDollarAmount + "  Calced: "
                        + totals.debitDollarAmount);
                ;
            }
            if (entryCreditDollarAmount != totals.creditDollarAmount) {
                retvalue.add("File credits are out of balance -- file: "
                        + entryCreditDollarAmount + "  Calced: "
                        + totals.creditDollarAmount);
                ;
            }
            if (entryHash != totals.entryHash) {
                retvalue.add("File hash is out of balance -- file: "
                        + entryHash + "  Calced: " + totals.entryHash);
                ;
            }
            if (batchCount != totals.batchCount) {
                retvalue.add("File batch count is out of balance -- file: "
                        + batchCount + "  Calced: " + totals.batchCount);
                ;
            }

            if (blockCount != calcBlockCount) {
                retvalue.add("File block count is out of balance -- file: "
                        + blockCount + "  Calced: " + calcBlockCount);
                ;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            retvalue.add("Error processing batch : " + ex.getMessage());
        }
        return retvalue;
    }

    /**
     * @return the batches
     */
    public synchronized Vector<ACHBatch> getBatches() {
        return batches;
    }

    /**
     * @param batches the batches to set
     */
    public synchronized void setBatches(Vector<ACHBatch> batches) {
        this.batches = batches;
    }

    /**
     * @param achBatch the batch to add
     */
    public synchronized void addBatch(ACHBatch achBatch) {
        this.batches.add(achBatch);
    }

    /**
     * @return the fileControl
     */
    public synchronized ACHRecordFileControl getFileControl() {
        return fileControl;
    }

    /**
     * @param fileControl the fileControl to set
     */
    public synchronized void setFileControl(ACHRecordFileControl fileControl) {
        this.fileControl = fileControl;
    }

    /**
     * @return the fileHeader
     */
    public synchronized ACHRecordFileHeader getFileHeader() {
        return fileHeader;
    }

    /**
     * @param fileHeader the fileHeader to set
     */
    public synchronized void setFileHeader(ACHRecordFileHeader fileHeader) {
        this.fileHeader = fileHeader;
    }

    /**
     * @param isFedFile the isFedFile to set
     */
    public void setFedFile(boolean isFedFile) {
        this.isFedFile = isFedFile;
    }

    /**
     * @return the isFedFile
     */
    public boolean isFedFile() {
        return isFedFile;
    }

    /**
     * @return Returns the errorMessages.
     */
    public Vector<String> getErrorMessages() {
        return errorMessages;
    }

}
