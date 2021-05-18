For general processing of voice queries we developed a text parsing library named 'Rootvole' that can be used to match
text with semantic concepts. 
The algorithm was implemented in Java and can be described as a form of a parsing expression grammar, where we generate the expressions to be detected beforehand by regular expressions and store them in a vocabulary.
The central class is the parser class, which is instantiated as a series of vocabularies, simple text lists that describe tokens and synonyms, and value descriptors. 
Value descriptors describe numerical values and are characterized mainly by a unit string. 
Furthermore, the information whether the numerical value is postfix or prefix in relation to the unit must be
stated explicitly, for example “500 euro” vs. “year 2003”. 
Via the methods hasLowerBound and getLowerBound, the parse results for regions can be retrieved, an example would be “200 to 500 euro”. 
Via the methods isMax and isMin, it is possible to determine whether the value is a lower or upper bound, an
example would be “at most 500 euro”. 
The parsing process itself is programmed in a two step process. Firstly, the values are extracted by detecting the unit strings and extracting nearby numbers as values. Secondly, the remaining bag-of-words set, i.e. all possible string groups given a certain context depth, is used to match against the vocabularies. 

Here's a Java source example for Usage:

		parser = new Parser(Constants.PROJECTNAME);
		parser.set_version(Constants.VERSION);
		parser.setInputToLower(true);
		titlePartVec = FileUtil.getFileLines(_config.getPathValue("titlePartVocab"));
		Vocabulary v = makeVocab(TITLE_PARTS, _titlePartVec, null);
		parser.addVocabulary(v);
		ParseResult pr = parser.parse(input.toLowerCase(Locale.GERMANY),
				Constants.PARSER_DEPTH);
		for (Entity ent : pr.getEntities()) {
			if (ent.isOfVocab(ParserManager.TITLE_PARTS)) {
			// do something interesting
			}
		}


There's a section in a paper describing Rootvole:
F. Burkhardt, H.U. Nägeli: Voice Search in Mobile Applications and the Use of Linked Open Data, Proc. Interspeech Lyon, 2013

The library FelixUtil is nedded when you want to compile.