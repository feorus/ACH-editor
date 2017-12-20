package com.ach.parser;

import org.junit.Test;

import com.ach.domain.ACHFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static org.junit.Assert.*;

public class ACHFileParserTest {
	
	List<String> nachaLines(String...lines) {
		return Arrays.asList(lines);
	}
	
    @Test
    public void singleLineNachaFile() throws Exception {
        List<String> nacha_data = nachaLines("101  9100001912737206971506161208A094101WELLSFARGO             COMPANYONE1"
            ,"5225COMANAGELLC     ACH SETTLEMENT      1234567890CCDPAYMENT         150616   1001237370000001"
            ,"6271221052785005486880       0000082100               PERSON 1                0001237370000001"
            ,"822500000100122105270000000821000000000000001234567890                         001237370000001"
            ,"9000001000001000000010012210527000000082100000000000000"
        );

        ACHFile achFile = ACHFileParser.fromLines(nacha_data);
        Vector<String> messages = achFile.validate();

        assertTrue(messages.isEmpty());
    }

    @Test
    public void testLargerNachaFile() throws Exception {
    	List<String> nacha_data = nachaLines("101  9100001912737206971506161217A094101WELLSFARGO             COMPANYONE1"
            ,"5200COMANAGELLC     ACH SETTLEMENT      1234567890CCDPAYMENT         150616   1001237370000001"
            ,"6271221052785005486880       0000082100               PERSON 1                0001237370000001"
            ,"6271221052786886896684       0000864107               PERSON 1                0001237370000002"
            ,"6223221747951228713          0000220000               PERSON 2                0001237370000003"
            ,"622122100024785323353        0000020125               PERSON 3                0001237370000004"
            ,"820000000400688485350000009462070000002401251234567890                         001237370000001"
            ,"5220COMANAGELLC     ACH SETTLEMENT      1234567890PPDPAYMENT         150616   1001237370000002"
            ,"6221221052789886521146       0000101832               PERSONS 4               0001237370000001"
            ,"6221221052789886521146       0000069863               PERSONS 4               0001237370000002"
            ,"822000000200244210540000000000000000001716951234567890                         001237370000002"
            ,"9000002000002000000060093269589000000946207000000411820"
        );

        ACHFile achFile = ACHFileParser.fromLines(nacha_data);
        Vector<String> messages = achFile.validate();

        assertTrue(messages.isEmpty());
    }

    @Test
    public void testInvalidFileShouldHaveErrors() throws Exception {
        List<String> nacha_data = nachaLines("101  9100001912737206971506161217A094101WELLSFARGO             COMPANYONE1"
            ,"5200COMANAGELLC     ACH SETTLEMENT      1234567890CCDPAYMENT         150616   1001237370000001"
            ,"6271221052785005486880       0000082100               PERSON 1                0001237370000001"
            ,"6271221052786886896684       0000864107               PERSON 1                0001237370000002"
            ,"6223221747951228713          0000220000               PERSON 2                0001237370000003"
            ,"622122100024785323353        0000020125               PERSON 3                0001237370000004"
            ,"820000000400688485350000009462070000002401251234567890                         001237370000001"
            ,"5220COMANAGELLC     ACH SETTLEMENT      1234567890PPDPAYMENT         150616   1001237370000002"
            ,"6221221052789886521146       0000101832               PERSON 4                0001237370000001"
            ,"6221221052789886521146       0000069863               PERSON 4                0001237370000002"
            ,"822000000200244210540000000000000000001716951234567890                         001237370000002"
            ,"9000002000001000000060093269589000000946206000000411820"
        );

        ACHFile achFile = ACHFileParser.fromLines(nacha_data);
        Vector<String> messages = achFile.validate();

        assertFalse(messages.isEmpty());
    }
}