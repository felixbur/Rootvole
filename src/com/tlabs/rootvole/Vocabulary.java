package com.tlabs.rootvole;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import com.felix.util.FileUtil;
import com.felix.util.StringUtil;

/**
 * A vocabulary is a set of words consisting of key and synonyms.
 * 
 * the input format is <id>,<synonym1>,...,<synonym N>
 * 
 * @author burkhardt.felix
 * 
 */
public class Vocabulary {
	private Vector<VocabEntry> _vocab;
	private boolean _withSynonyms = false, _includeIDinSearch = true,
			_multipleIds = false;
	private HashMap<String, String> _vocabHashMap;
	private HashMap<String, Vector> _vocabMultipleIDsHashMap;
	private String _id = null,
			_vocabSeparator = Constants.VOCAB_DEFAULT_SEPARATOR;
	private String[] _vocabArray = null;
	private String[] _forbiddenEntries = null;

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            The id for this vocabulary, e.g. "colors".
	 */
	public Vocabulary(String id) {
		_id = id;
	}

	/**
	 * Set the string to separate id and synonms in vocabulary
	 * 
	 * @param separator
	 */
	public void setVocabSeparator(String separator) {
		_vocabSeparator = separator;
	}

	/**
	 * Simple constructor.
	 * 
	 * @param id
	 *            The id.
	 * @param elements
	 *            A vector of strings for the elements, without synonyms.
	 */
	public Vocabulary(String id, Vector<String> elements) {
		_id = id;
		loadVocabFromVectorWithoutSynonyms(elements);
	}

	/**
	 * Set a set of tokens that should NOT be used as ids or synonyms in this
	 * vocab, i.e. rejected when tried to load.
	 * 
	 * @param e
	 */
	public void setForbiddenEntries(String[] e) {
		_forbiddenEntries = e;
	}

	/**
	 * Simple constructor.
	 * 
	 * @param id
	 *            The id.
	 * @param elements
	 *            A string array for the elements without synonyms.
	 */
	public Vocabulary(String id, String[] elements) {
		_id = id;
		_vocab = new Vector<VocabEntry>();
		_vocabHashMap = new HashMap<String, String>();
		for (String element : elements) {
			VocabEntry ie = new VocabEntry(element, null, false);
			_vocab.add(ie);
			_vocabHashMap.put(element, element);
		}
	}

	public boolean is_includeIDinSearch() {
		return _includeIDinSearch;
	}

	/**
	 * Set the ability to have several IDs for the same values, i.e. values are
	 * ambigous.
	 * 
	 * @param hasMultipleIDs
	 * @return
	 */
	public void setMultipleIDs(boolean hasMultipleIDs) {
		_multipleIds = hasMultipleIDs;
	}

	/**
	 * If the vocabulary returns multiple ids per entry.
	 * 
	 * @return
	 */
	public boolean isMultiplIDs() {
		return _multipleIds;
	}

	/**
	 * Set whether the id is included in the search.
	 * 
	 * @param _includeIDinSearch
	 */
	public void set_includeIDinSearch(boolean _includeIDinSearch) {
		this._includeIDinSearch = _includeIDinSearch;
	}

	/**
	 * Get th ID String.
	 * 
	 * @return
	 */
	public String getId() {
		return _id;
	}

	/**
	 * Get the vocabulary as an array of Strings
	 * 
	 * @return
	 */
	public String[] getVocabAsArray() {
		if (_vocabArray == null)
			fillVocabArray();
		return _vocabArray;
	}

	/**
	 * Load the vocabulary from a Vector of Strings.
	 * 
	 * @param elements
	 * @param withSynonyms
	 */
	public void loadVocabFromVector(Vector<String> elements,
			boolean withSynonyms) {
		set_withSynonyms(withSynonyms);
		if (withSynonyms)
			loadVocabFromVectorWithSynonyms(elements);
		else
			loadVocabFromVectorWithoutSynonyms(elements);

	}

	/**
	 * Load the vocabulary from a file.
	 * 
	 * @param filepath
	 * @param withSynonyms
	 * @throws Exception
	 */
	public void loadVocabFromFile(String filepath, boolean withSynonyms)
			throws Exception {
		Vector<String> elements = FileUtil.getFileLines(filepath);
		set_withSynonyms(withSynonyms);
		if (withSynonyms)
			loadVocabFromVectorWithSynonyms(elements);
		else
			loadVocabFromVectorWithoutSynonyms(elements);

	}

	/**
	 * Test whether some word is in the vocabulary and return the ID-word. For
	 * single IDs.
	 * 
	 * @param s
	 *            The String to test.
	 * @return The ID-word (e.g. "vw" for "volkswagen" in vocab "vw,volkswagen")
	 */
	public String getIDFromVocab(String s) {
		if (_multipleIds) {
			Vector<String> resVec = getIDsFromVocab(s);
			if (resVec != null)
				return resVec.firstElement();
			else
				return null;
		}

		return _vocabHashMap.get(s);
	}

	/**
	 * Test whether some word is in the vocabulary and return all IDs. For
	 * multiple IDs.
	 * 
	 * @param s
	 *            The String to test.
	 * @return All possible IDs, (e.g. "2342" and "4234" for "300")
	 */
	public Vector<String> getIDsFromVocab(String s) {
		return _vocabMultipleIDsHashMap.get(s);
	}

	/**
	 * Exhaustive search for all IDs in vocabulary testing all synonyms.
	 * 
	 * @param query
	 * @return
	 */
	public Vector<String> getIDsForVocabSearch(String query) {
		Vector<String> retVec = new Vector<String>();
		for (VocabEntry ve : _vocab) {
			if (ve.isSynonym(query)) {
				retVec.add(ve.get_key());
			}
		}
		return retVec;
	}

	public void loadVocabFromVectorWithoutSynonyms(Vector<String> elements) {
		_vocab = new Vector<VocabEntry>();
		_vocabHashMap = new HashMap<String, String>();
		for (String element : elements) {
			if (StringUtil.isFilled(element)) {
				VocabEntry ie = new VocabEntry(element, null, false);
				if (!checkForbiddenEntries(ie)) {
					_vocab.add(ie);
					_vocabHashMap.put(element, element);
				}
			}
		}
	}

	public void loadVocabFromVectorWithSynonyms(Vector<String> elements) {
		loadVocabFromVectorWithSynonyms(elements, true);
	}

	public void addToVocabFromVectorWithSynonyms(Vector<String> elements) {
		loadVocabFromVectorWithSynonyms(elements, false);
	}

	public void loadVocabFromVectorWithSynonyms(Vector<String> elements,
			boolean initialVocab) {
		if (initialVocab) {
			_vocab = new Vector<VocabEntry>();
			if (_multipleIds) {
				_vocabMultipleIDsHashMap = new HashMap<String, Vector>();
			} else {
				_vocabHashMap = new HashMap<String, String>();
			}
		}
		String eName = "";
		String[] elemDescription;
		try {
			for (String element : elements) {
				if (StringUtil.isFilled(element)) {
					elemDescription = StringUtil.stringToArray(element,
							_vocabSeparator);
					if (elemDescription == null)
						continue;
					if (_includeIDinSearch) {
						String s = elemDescription[0];
						if (_multipleIds) {
							addToMultipleIDHashmap(s, s);
						} else {
							_vocabHashMap.put(s, s);
						}
					}
					// if (elemDescription.length == 1)
					// return;
					int indexStart = 1;
					eName = elemDescription[0];
					VocabEntry ie = new VocabEntry(eName, elemDescription, true);
					if (!checkForbiddenEntries(ie)) {
						for (int i = indexStart; i < elemDescription.length; i++) {
							String s = elemDescription[i];
							if (_multipleIds) {
								addToMultipleIDHashmap(s, eName);
							} else {
								_vocabHashMap.put(s, eName);
							}
						}
						if (!initialVocab) {
							VocabEntry orig = searchSameKey(ie);
							if (orig != null) {
								orig.addSynonyms(ie);
							} else {
								_vocab.add(ie);
							}
						} else {
							_vocab.add(ie);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private VocabEntry searchSameKey(VocabEntry ve) {
		for (VocabEntry v : _vocab) {
			if (v.isSameKey(ve))
				return v;
		}
		return null;
	}

	private boolean checkForbiddenEntries(VocabEntry v) {
		if (_forbiddenEntries == null)
			return false;
		for (String s : _forbiddenEntries) {
			if (v.isSynonym(s))
				return true;
		}
		return false;
	}

	private void addToMultipleIDHashmap(String key, String value) {
		Vector<String> values = _vocabMultipleIDsHashMap.get(key);
		if (values != null) {
			values.add(value);
			_vocabMultipleIDsHashMap.put(key, values);
		} else {
			values = new Vector<String>();
			values.add(value);
			_vocabMultipleIDsHashMap.put(key, values);
		}
	}

	public Vector<String> getSynonyms() {
		Vector<String> ret = new Vector<String>();
		for (VocabEntry ve : _vocab) {
			for (String s : ve.get_synonymsWithoutId()) {
				ret.add(s);
			}
			ret.add(ve.getRandomSynonymWithouID());
		}
		return ret;
	}

	public void printToFile(String filename) throws Exception {
		Vector<String> contents = new Vector<String>();
		for (VocabEntry e : _vocab) {
			contents.add(e.toVocabularyString(_vocabSeparator));
		}
		FileUtil.writeFileContent(filename, contents);
	}

	public void printToPrintStream(PrintStream out) throws Exception {
		for (VocabEntry e : _vocab) {
			out.println(e.toVocabularyString(_vocabSeparator));
		}
	}

	public String getRandomSynonym() {
		int len = _vocab.size();
		int id = new Random().nextInt(len);
		return _vocab.elementAt(id).getRandomSynonymWithouID();
	}

	private void fillVocabArray() {
		_vocabArray = new String[_vocab.size()];
		int i = 0;
		for (VocabEntry ve : _vocab) {
			_vocabArray[i++] = ve.get_key();
		}

	}

	public boolean is_withSynonyms() {
		return _withSynonyms;
	}

	public void set_withSynonyms(boolean _withSynonyms) {
		this._withSynonyms = _withSynonyms;
	}
}
