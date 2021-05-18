package com.tlabs.rootvole;

import java.util.Random;

import com.felix.util.StringUtil;
import com.felix.util.Util;

/**
 * A vocabEntry has a key and an optional set of synonyms.
 * 
 * @author burkhardt.felix
 * 
 */
public class VocabEntry {
	private String _key = "";
	private String[] _synonyms;
	private boolean _hasSynonyms = false;

	/**
	 * Constructor.
	 * 
	 * @param _key
	 *            The key value.
	 * @param _synonyms
	 *            The synonyms.
	 * @param _hasSynonyms
	 *            True if synonyms are used.
	 */
	public VocabEntry(String _key, String[] _synonyms, boolean _hasSynonyms) {
		super();
		this._key = _key;
		this._synonyms = _synonyms;
		this._hasSynonyms = _hasSynonyms;
	}

	/**
	 * Constructor with a set of words, first word is key.
	 * 
	 * @param words
	 *            E.g. "key synonym1 synonym2"
	 */
	public VocabEntry(String[] words) {
		_key = words[0];
		int wordNum = words.length;
		if (wordNum > 1) {
			_hasSynonyms = true;
			_synonyms = words;
		}
	}

	/**
	 * Test if a word belongs to the synonyms.
	 * 
	 * @param test
	 *            The test word, e.g. "ente"
	 * @return The result, e.g. "true" for "2cv, ente"
	 */
	public boolean isSynonym(String test) {
		if (_hasSynonyms)
			return StringUtil.isStringInArray(test, _synonyms);
		else {
			if (_key.compareTo(test) == 0)
				return true;
			return false;
		}
	}

	/**
	 * Add all synonyms from another vocab entry.
	 * 
	 * @param ve
	 *            The other Vocab Entry.
	 */
	public void addSynonyms(VocabEntry ve) {
		if (_hasSynonyms) {
			if (ve.is_hasSynonyms()) {
				_synonyms = StringUtil.unifyStringArrays(_synonyms,
						ve.get_synonyms());
			}
		} else {
			if (ve.is_hasSynonyms()) {
				this._synonyms = ve.get_synonyms();
				_hasSynonyms = true;
			}
		}
	}

	/**
	 * Test of it has the same key string.
	 * 
	 * @param ve
	 *            The other VocabEntry.
	 * @return True if key string compares to the other one with 0.
	 */
	public boolean isSameKey(VocabEntry ve) {
		if (_key.compareTo(ve.get_key()) == 0)
			return true;
		return false;
	}

	/**
	 * Get the key value.
	 * 
	 * @return The key value.
	 */
	public String get_key() {
		return _key;
	}

	/**
	 * Get all synonyms.
	 * 
	 * @return The synonyms including id.
	 */
	public String[] get_synonyms() {
		return _synonyms;
	}

	/**
	 * Get the synonyms without the id.
	 * 
	 * @return The synonyms.
	 */
	public String[] get_synonymsWithoutId() {
		return (String[]) Util.subStringArray(_synonyms, 1, _synonyms.length);
	}

	/**
	 * Get a random word from the set of synonyms. omitting the id.
	 * 
	 * @return A random synonym.
	 */
	public String getRandomSynonymWithouID() {
		String[] syns = get_synonymsWithoutId();
		int len = syns.length;
		int id = new Random().nextInt(len);
		return syns[id];
	}

	/**
	 * Test if synonyms are used.
	 * 
	 * @return True if synonyms are used.
	 */
	public boolean is_hasSynonyms() {
		return _hasSynonyms;
	}

	/**
	 * Return a representation suitable for writing in a vocabulary.
	 * 
	 * @param separator
	 * @return
	 */
	public String toVocabularyString(String separator) {
		String ret = "";
		if (_synonyms != null) {
			if (_synonyms[0].compareTo(_key) != 0) {
				ret += _key + separator;
			}
			for (String s : _synonyms) {
				ret += s + separator;
			}
		} else {
			return _key;
		}
		return ret.substring(0, ret.length() - 1);
	}
}
