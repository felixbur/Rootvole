package com.tlabs.rootvole;

import com.felix.util.Stack;
import com.felix.util.StringUtil;

/**
 * A value is a numeric entity, might have a constraint, a value and a unit,
 * e.g. "max. 3000 euro"
 * 
 * @author burkhardt.felix
 * 
 */
public class ValueDescription {
	private String _id;
	private String _featureName;
	private VocabEntry _units;
	private VocabEntry _maxConstraints;
	private VocabEntry _minConstraints;
	private VocabEntry _intervalWords;
	private boolean _isPostfix = true, _isDouble = false;

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            The id for the descriptor, e.g. "euro"
	 * @param isPostfix
	 *            If the unit is postfix or prefix notation, e.g. "true" for
	 *            "300 euro" but "false" for "baujahr 2010"
	 * @param isDouble
	 *            If the values are doubles or integers.
	 */
	public ValueDescription(String id, boolean isPostfix, boolean isDouble) {
		_id = id;
		_isPostfix = isPostfix;
		_isDouble = isDouble;
	}

	/**
	 * Search for a value.
	 * 
	 * @param test
	 *            The string to test, e.g. "remeo max 200 euro black".
	 * @return The result and the strings not used, e.g. "max 200 euro" and
	 *         "romeo black".
	 */
	public ValueResult searchValue(String test) {
		String[] words = StringUtil.stringToArray(test);
		return searchValue(words);
	}

	/**
	 * Search for a value.
	 * 
	 * @param test
	 *            the string array to search in.
	 * @return The result and the strings not used, e.g. "max 200 euro" and
	 *         "romeo black".
	 */
	public ValueResult searchValue(String[] test) {
		if (_isPostfix) {
			return searchValuePostfix(test);
		}
		return searchValuePrefix(test);
	}

	private ValueResult searchValuePostfix(String[] test) {
		int wordNum = test.length;
		boolean found = false;
		String testVal = null, testConstraint = null;
		Value resultVal = null;
		Stack<String> restStack = new Stack<String>();
		if (wordNum < 2) {
			return null;
		}
		for (int i = 0; i < wordNum; i++) {
			String w = test[i];
			if (_units.isSynonym(w)) {
				if (i >= 1) {
					testVal = test[i - 1];
					try {
						resultVal = tryValue(testVal);
//								
//								new Value(_id, _featureName, _isDouble);
//						if (_isDouble) {
//							resultVal.set_value(Double.parseDouble(testVal));
//						} else {
//							resultVal.set_value(Integer.parseInt(testVal
//									.replace(".", "")));
//						}
//						resultVal.set_unit(_units.get_key());

						found = true;
						restStack.pop();
						if (i >= 2) {
							testConstraint = test[i - 2];
							if (_maxConstraints.isSynonym(testConstraint)) {
								resultVal.set_isMax(true);
								restStack.pop();
							} else if (_minConstraints
									.isSynonym(testConstraint)) {
								resultVal.set_isMin(true);
								restStack.pop();
							} else {
							}
						}
						if (i >= 3 && _intervalWords != null) {
							// check for double values, e.g.
							// "between x and y dollar"
							String testBetweenWord = test[i - 2];
							if (_intervalWords.isSynonym(testBetweenWord)) {
								String testVallower = test[i - 3];
								if (_isDouble) {
									resultVal.setValueLowerDouble(Double
											.parseDouble(testVallower));
								} else {
									resultVal.setValueLowerInt(Integer
											.parseInt(testVallower.replace(".",
													"")));
								}
								restStack.pop();
							}
						}
					} catch (NumberFormatException e) {
						// numberformatException accepted
					}
				}
			} else {
				restStack.push(w);
			}
		}
		if (!found) {
			return null;
		}
		return new ValueResult(resultVal, restStack.toStringArrayReverse());
	}

	private ValueResult searchValuePrefix(String[] test) {
		int wordNum = test.length;
		boolean found = false;
		String testVal = null, testConstraint = null;
		Value resultVal = null;
		Stack<String> restStack = new Stack<String>();
		if (wordNum < 2) {
			return null;
		}
		for (int i = 0; i < wordNum; i++) {
			String w = test[i];
			try {
				if (_units.isSynonym(w)) {
					if (i < wordNum - 1) {
						testVal = test[i + 1];
						if (_maxConstraints.isSynonym(testVal)) {
							if (i < wordNum - 2) {
								testVal = test[i + 2];
								resultVal = tryValue(testVal);
								i++;
							}
							resultVal.set_isMax(true);
							i++;
							found = true;
						} else if (_minConstraints.isSynonym(testVal)) {
							if (i < wordNum - 2) {
								testVal = test[i + 2];
								resultVal = tryValue(testVal);
								i++;
							}
							resultVal.set_isMin(true);
							i++;
							found = true;
						} else {
							resultVal = tryValue(testVal);
							found = true;
							if (i >= 1) {
								testConstraint = test[i - 1];
								if (_maxConstraints.isSynonym(testConstraint)) {
									resultVal.set_isMax(true);
									restStack.pop();
								} else if (_minConstraints
										.isSynonym(testConstraint)) {
									resultVal.set_isMin(true);
									restStack.pop();
								} else {
								}
							}
							i++;
						}
					}
				} else {
					restStack.push(w);
				}
			} catch (NumberFormatException e) {
				// TODO: handle exception
			}
		}
		if (!found) {
			return null;
		}
		return new ValueResult(resultVal, restStack.toStringArrayReverse());
	}

	private Value tryValue(String testVal) {
		Value resultVal;
		resultVal = new Value(_id, _featureName, _isDouble);
		if (_isDouble) {
			resultVal.set_value(Double.parseDouble(testVal));
		} else {
			resultVal.set_value(Integer.parseInt(testVal
					.replace(".", "")));
		}
		resultVal.set_unit(_units.get_key());
		return resultVal;
	}

	public String getFeatureName() {
		return _featureName;
	}

	public void setFeatureName(String featureName) {
		this._featureName = featureName;
	}

	/**
	 * Get the id for this value, e.g. "price".
	 * 
	 * @return The id.
	 */
	public String get_id() {
		return _id;
	}

	/**
	 * The unit strings as a vocabulary entry (might have synonyms).
	 * 
	 * @return THe unit, e.g. "baujahr", "bj".
	 */
	public VocabEntry get_units() {
		return _units;
	}

	/**
	 * Set the units.
	 * 
	 * @param units
	 *            The units.
	 */
	public void set_units(VocabEntry _units) {
		this._units = _units;
	}

	public VocabEntry get_betweenWords() {
		return _intervalWords;
	}

	public void set_betweenWords(VocabEntry _betweenWords) {
		this._intervalWords = _betweenWords;
	}

	/**
	 * Get the words for the maximum.
	 * 
	 * @return The words, e.g. "max".
	 */
	public VocabEntry get_maxConstraints() {
		return _maxConstraints;
	}

	/**
	 * Set the maximum words, might have synonyms.
	 * 
	 * @param _maxConstraints
	 *            The maximum words, e.g. "max".
	 */
	public void set_maxConstraints(VocabEntry _maxConstraints) {
		this._maxConstraints = _maxConstraints;
	}

	/**
	 * Get the minimum words.
	 * 
	 * @return The minimum words, e.g. "min".
	 */
	public VocabEntry get_minConstraints() {
		return _minConstraints;
	}

	/**
	 * Set the minimum words.
	 * 
	 * @param _minConstraints
	 *            The minimum words, e.g. "min".
	 */
	public void set_minConstraints(VocabEntry _minConstraints) {
		this._minConstraints = _minConstraints;
	}

	/**
	 * Inner class to repersent the result and all words are were not used.
	 * 
	 * @author burkhardt.felix
	 * 
	 */
	public class ValueResult {
		private Value _value = null;
		private String[] _restString = null;

		public ValueResult(Value _value, String[] _restString) {
			super();
			this._value = _value;
			this._restString = _restString;
		}

		public Value get_value() {
			return _value;
		}

		public String[] get_restString() {
			return _restString;
		}

	}

}
