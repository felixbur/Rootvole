package com.tlabs.rootvole;

import java.util.Iterator;
import java.util.Vector;

import com.felix.util.StringUtil;

/**
 * An input string represents an input for the parser.
 * 
 * @author burkhardt.felix
 * 
 */
public class InputString {
	private String _inputString = null, _alphaNumOnly = null;
	private Vector<MultiStringWord> _depth1res = null, _depth2res = null,
			_depth3res = null, _depth4res = null, _lastRes = null;
	private String[] _stopWords, _inputStringArray = null;
	private int _wordNum = 0;

	/**
	 * Constructor.
	 * 
	 * @param inputString
	 *            The complete input String.
	 * @param stopWords
	 *            Set of words to be removed before processing, can be null.
	 */
	public InputString(String inputString, String[] stopWords) {
		super();
		_stopWords = stopWords;
		if (stopWords != null) {
			_inputStringArray = StringUtil.removeStopwords(inputString,
					_stopWords);
			_inputString = StringUtil.arrayToString(_inputStringArray);
		} else {
			this._inputStringArray = StringUtil.stringToArray(inputString);
			_inputString = inputString;
		}
		_wordNum = _inputStringArray.length;
		// _alphaNumOnly =
		// StringUtil.removeNonAlpanumericCharacters(_inputString);
	}

	public String getInput() {
		return _inputString;
	}

	public Vector<String> getReplacedWords() {
		Vector<String> retVec = new Vector<String>();
		for (Iterator iterator = _lastRes.iterator(); iterator.hasNext();) {
			MultiStringWord multiStringWord = (MultiStringWord) iterator.next();
			if (multiStringWord.is_replaced()) {
				retVec.add(multiStringWord.get_replacement());
			}
		}
		return retVec;
	}

	public void newInput(String inputString) {
		if (_stopWords != null) {
			_inputStringArray = StringUtil.removeStopwords(inputString,
					_stopWords);
			_inputString = StringUtil.arrayToString(_inputStringArray);
		} else {
			this._inputStringArray = StringUtil.stringToArray(inputString);
			_inputString = inputString;
		}
		_wordNum = _inputStringArray.length;

	}

	/**
	 * Constructor.
	 * 
	 * @param inputStringArray
	 *            Array of string to be used as input.
	 * @param stopWords
	 *            Set of stopwords to be removed before processing.
	 */
	public InputString(String[] inputStringArray, String[] stopWords) {
		super();
		_stopWords = stopWords;
		if (stopWords != null) {
			_inputStringArray = StringUtil.removeStopwords(inputStringArray,
					_stopWords);
			_inputString = StringUtil.arrayToString(_inputStringArray);
		} else {
			this._inputStringArray = inputStringArray;
			_inputString = StringUtil.arrayToString(inputStringArray);
		}
		_wordNum = _inputStringArray.length;
		// _alphaNumOnly =
		// StringUtil.removeNonAlpanumericCharacters(_inputString);
	}

	public int getWordNum() {
		return _wordNum;
	}

	public String[] getWords() {
		return _inputStringArray;
	}

	/**
	 * Get all string combinations for a specific context depth for this input.
	 * 
	 * @param depth
	 *            The depth, e.g. "2" for search on maximal 2-word string.
	 * @return The vector of sub strings, e.g. "alfa", "alfa romeo", "romeo" for
	 *         "alfa romeo"
	 */
	public Vector<MultiStringWord> getCombinations(int depth) {
		if (depth == 1) {
			if (_depth1res == null) {
				_depth1res = new Vector<MultiStringWord>();
				// _depth1res.add(new MultiStringWord(_inputString, 0,
				// _wordNum));
				for (int i = 0; i < _wordNum; i++) {
					_depth1res.add(new MultiStringWord(_inputStringArray[i], i,
							1));
				}
			}
			_lastRes = _depth1res;
			return _depth1res;
		} else if (depth == 2) {
			if (_depth2res == null) {
				_depth2res = new Vector<MultiStringWord>();
				// _depth2res.add(new MultiStringWord(_inputString, 0,
				// _wordNum));
				String act = null;
				String pre = null;
				for (int i = 0; i < _wordNum; i++) {
					act = _inputStringArray[i];
					_depth2res.add(new MultiStringWord(act, i, 1));
					if (pre != null) {
						_depth2res.add(new MultiStringWord(pre + " " + act,
								i - 1, 2));
					}
					pre = act;
				}
			}
			_lastRes = _depth2res;
			return _depth2res;
		} else if (depth == 3) {
			if (_depth3res == null) {
				_depth3res = new Vector<MultiStringWord>();
				// _depth3res.add(new MultiStringWord(_inputString, 0,
				// _wordNum));
				String act = null, pre = null, prepre = null;
				for (int i = 0; i < _wordNum; i++) {
					act = _inputStringArray[i];
					_depth3res.add(new MultiStringWord(act, i, 1));
					if (prepre != null) {
						// _depth3res.add(new MultiStringWord(prepre + " " +
						// pre,
						// i - 2, 2));
						_depth3res.add(new MultiStringWord(prepre + " " + pre
								+ " " + act, i - 2, 3));
					}
					if (pre != null) {
						_depth3res.add(new MultiStringWord(pre + " " + act,
								i - 1, 2));
						prepre = pre;
					}
					pre = act;
				}
			}
			_lastRes = _depth3res;
			return _depth3res;
		} else if (depth == 4) {
			if (_depth4res == null) {
				_depth4res = new Vector<MultiStringWord>();
				// _depth4res.add(new MultiStringWord(_inputString, 0,
				// _wordNum));
				String act = null, pre = null, prepre = null, preprepre = null;
				for (int i = 0; i < _wordNum; i++) {
					act = _inputStringArray[i];
					_depth4res.add(new MultiStringWord(act, i, 1));
					if (preprepre != null) {
						// _depth4res.add(new MultiStringWord(preprepre + " "
						// + prepre, i - 3, 4));
						// _depth4res.add(new MultiStringWord(preprepre + " "
						// + prepre + " " + pre, i - 3, 4));
						_depth4res.add(new MultiStringWord(preprepre + " "
								+ prepre + " " + pre + " " + act, i - 3, 4));
					}
					if (prepre != null) {
						// _depth4res.add(new MultiStringWord(prepre + " " +
						// pre,
						// i - 2, 2));
						_depth4res.add(new MultiStringWord(prepre + " " + pre
								+ " " + act, i - 2, 3));
						preprepre = prepre;
					}
					if (pre != null) {
						_depth4res.add(new MultiStringWord(pre + " " + act,
								i - 1, 2));
						prepre = pre;
					}
					pre = act;
				}
			}
			_lastRes = _depth4res;
			return _depth4res;
		}
		return null;
	}

	public Vector<MultiStringWord> filterMultistrings(int len, int start,
			int end) {
		Vector<MultiStringWord> retVec = new Vector<MultiStringWord>();
		for (Iterator iterator = _lastRes.iterator(); iterator.hasNext();) {
			MultiStringWord multiStringWord = (MultiStringWord) iterator.next();
			if (multiStringWord.get_length() == len
					&& multiStringWord.get_offset() <= end
					&& multiStringWord.get_offset() >= start) {
				retVec.add(multiStringWord);
			}
		}
		return retVec;
	}

	/**
	 * Used for testing.
	 * 
	 * @param args
	 *            Nothing.
	 */
	public static void main(String[] args) {
		String[] sw = new String[] { "das", "ist" };
		// InputString inputString = new InputString(
		// "das ist mein toller testsatz das huhu &", sw);
		InputString inputString = new InputString("foo golf bar", sw);
		Vector<MultiStringWord> test = inputString.getCombinations(4);
		for (MultiStringWord t : test)
			System.out.println(t.get_word() + ": " + t.get_offset() + " "
					+ t.get_length());
	}
}
