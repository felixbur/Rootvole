package com.tlabs.rootvole;

import java.util.Vector;

import com.felix.util.Preprocessor;
import com.felix.util.StringUtil;
import com.tlabs.rootvole.ValueDescription.ValueResult;

/**
 * Central class of this library.
 * 
 * @author burkhardt.felix
 * 
 */
public class Parser {
	private Vocabulary _stopwords = null;
	private Vector<Vocabulary> _vocabularies;
	private Vector<ValueDescription> _valueDescriptions;
	private String _id = "", _version = "";
	private Preprocessor _queryPreprocessor = null;
	private boolean _hasStopwords = false, _inputToLower = false;

	/**
	 * Constructor with identifiable string.
	 * 
	 * @param id
	 *            The id string.
	 */
	public Parser(String id) {
		_id = id;
	}

	/**
	 * Add a vocabulary.
	 * 
	 * @param vocabulary
	 *            The vocabulary.
	 */
	public void addVocabulary(Vocabulary vocabulary) {
		if (_vocabularies == null) {
			_vocabularies = new Vector<Vocabulary>();
		}
		_vocabularies.add(vocabulary);
	}

	/**
	 * Set the stopword vocabulary, i.e. words that will be removed before
	 * searching the vocabularies.
	 * 
	 * @param stopwords
	 */
	public void setStopwords(Vocabulary stopwords) {
		_stopwords = stopwords;
		_hasStopwords = true;
	}

	/**
	 * Add a value description instance.
	 * 
	 * @param valueDescription
	 *            The value description.
	 */
	public void addValueDesciptions(ValueDescription valueDescription) {
		if (_valueDescriptions == null) {
			_valueDescriptions = new Vector<ValueDescription>();
		}
		_valueDescriptions.add(valueDescription);
	}

	/**
	 * Get the version String.
	 * 
	 * @return The version or empty if not set.
	 */
	public String get_version() {
		return _version;
	}

	/**
	 * Optionally set a preprocessor that preprocesses input queries.
	 * 
	 * @param qp
	 *            The preprocessor.
	 */
	public void setQueryPreprocessor(Preprocessor qp) {
		_queryPreprocessor = qp;
	}

	/**
	 * Set the version string (optional).
	 * 
	 * @param _version
	 *            The version String.
	 */
	public void set_version(String _version) {
		this._version = _version;
	}

	/**
	 * Parse an input string.
	 * 
	 * @param in
	 *            The input string.
	 * @param contextDepth
	 *            The number of words that are considered to be an identifiable
	 *            vocabulary item, e.g. "2",
	 * @return The result, might be empty if no matches were found.
	 */
	public ParseResult parse(String in, int contextDepth) {
		try {
			ParseResult parseResult = new ParseResult(in, _id, _version);
			if (_queryPreprocessor != null) {
				parseResult.setOrigString(in);
				in = _queryPreprocessor.process(in);
			}
			if (_inputToLower) {
				if (_queryPreprocessor == null) {
					parseResult.setOrigString(in);
				}
				in = in.toLowerCase();
			}
			String[] words = null;
			if (_hasStopwords) {
				words = StringUtil.removeStopwords(in,
						_stopwords.getVocabAsArray());
			} else {
				words = StringUtil.stringToArray(in);
			}
			ValueResult valueResult = null;
			String[] testWords = words;
			if (_valueDescriptions != null) {
				for (ValueDescription valueDescriptor : _valueDescriptions) {
					valueResult = valueDescriptor.searchValue(testWords);
					if (valueResult != null) {
						parseResult.addValue(valueResult.get_value());
						testWords = valueResult.get_restString();
						if (testWords.length == 0)
							break;
					}
				}
			}
			parseResult.setRestArray(testWords);
			InputString inputString = new InputString(testWords, null);
			Vector<MultiStringWord> targetWords = inputString
					.getCombinations(contextDepth);
			if (testWords.length > 0 && _vocabularies != null) {
				for (Vocabulary vocab : _vocabularies) {
					for (MultiStringWord target : targetWords) {
						if (vocab.isMultiplIDs()) {
							Vector<String> ids = vocab.getIDsFromVocab(target
									.get_word());
							if (ids != null) {
								// id = ids.firstElement();
								for (String id : ids) {
									addEntityToParseResult(parseResult, vocab,
											target, id);
								}
							}
						} else {
							String id = vocab.getIDFromVocab(target.get_word());
							if (id != null) {
								addEntityToParseResult(parseResult, vocab,
										target, id);
							}

						}
					}
				}
			}
			parseResult.checkDoubles();
			return parseResult;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void addEntityToParseResult(ParseResult parseResult,
			Vocabulary vocab, MultiStringWord target, String id) {
		Entity entity = new Entity(vocab.getId());
		entity.setId(id);
		entity.setValue(target);
		parseResult.addEntity(entity);
	}

	/**
	 * Get a vocabulary from the set of vocabularies..
	 * 
	 * @param id
	 *            The id of the vocabulary.
	 * @return The vocabulary or null if not found.
	 */
	public Vocabulary getVocab(String id) {
		for (Vocabulary v : _vocabularies) {
			if (v.getId().compareTo(id) == 0)
				return v;
		}
		return null;
	}

	public boolean isInputToLower() {
		return _inputToLower;
	}

	public void setInputToLower(boolean _inputToLower) {
		this._inputToLower = _inputToLower;
	}

}
