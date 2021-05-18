package com.tlabs.rootvole;

import com.felix.util.StringUtil;

/**
 * A multi string word is a word that is part of a longer (containing) string.
 * 
 * @author burkhardt.felix
 * 
 */
public class MultiStringWord {
	private String _word, _replacement;
	private int _offset, _length, _end;
	private boolean _replaced;

	/**
	 * Construct with all parameters.
	 * 
	 * @param word
	 *            The word, might contain whitespace.
	 * @param offset
	 *            The offset in the containing string.
	 * @param length
	 *            The number of blank separated words.
	 */
	public MultiStringWord(String word, int offset, int length) {
		_word = word;
		_offset = offset;
		_length = length;
		_end = offset + length;
	}

	public String get_replacement() {
		return _replacement;
	}

	public void set_replacement(String _replacement) {
		this._replacement = _replacement;
		this._replaced = true;
	}

	public boolean is_replaced() {
		return _replaced;
	}

	public void set_replaced() {
		_replacement = _word;
		this._replaced = true;
	}

	/**
	 * Constructor with offset 0 and length=number of sub words.
	 * 
	 * @param word
	 *            The string, e.g. "alpha romeo"
	 */
	public MultiStringWord(String word) {
		_word = word;
		_offset = 0;
		_length = StringUtil.numWords(_word);
		_end = _length;
	}

	/**
	 * Get the word.
	 * 
	 * @return The word.
	 */
	public String get_word() {
		return _word;
	}

	public String toString() {
		return "word: "+_word+", offset: "+_offset+", len: "+_length+", end: "+_end	;
	}

	/**
	 * Get the offset.
	 * 
	 * @return The offset.
	 */
	public int get_offset() {
		return _offset;
	}

	/**
	 * Get the number of subwords, e.g. 2 for "alpha romeo".
	 * 
	 * @return The length.
	 */
	public int get_length() {
		return _length;
	}

	/**
	 * Get the end index in a containing string.
	 * 
	 * @return The end index.
	 */
	public int get_end() {
		return _end;
	}

	public boolean isSameWord(MultiStringWord other) {
		if (_word.compareTo(other.get_word()) == 0)
			return true;
		return false;
	}

	/**
	 * Test if this multi word string is totally inside another String, also if
	 * it's the same String.
	 * 
	 * @param other
	 *            The other string, e.g. "no alpha romeo"
	 * @return True if totally contained.
	 * 
	 *         || (other.get_offset() >= _offset && other.get_end() <= _end
	 */
	public boolean isTotallyContainted(MultiStringWord other) {
		if ((other.get_offset() <= _offset && other.get_end() >= _end)) {
			return true;
		}
		return false;
	}

	/**
	 * Test if a String inside a part of another String
	 * 
	 * @param other
	 *            "alpha romeo"
	 * @return The result, e.g. true for "alpha" but false for "alpha romeo"
	 */
	public boolean isSmallerPartOf(MultiStringWord other) {
		if ((other.get_offset() < _offset && other.get_end() >= _end)
				|| (other.get_offset() <= _offset && other.get_end() > _end)) {
			return true;
		}
		return false;
	}

}
