package com.ach.parser;

import java.util.List;

import com.ach.domain.ACHDocument;

public interface AchFileParser {

	ACHDocument fromLines(List<String> lines) throws AchParserException;

}
