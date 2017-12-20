package com.ach.parser;

import java.util.List;

import com.ach.domain.ACHFile;

public interface AchFileParser {

	ACHFile fromLines(List<String> lines) throws AchParserException;

}
