package com.tlabs.rootvole;

import org.json.JSONArray;
import org.json.JSONObject;

import com.felix.util.NumberFormat;

/**
 * A value is a numeric entity, might have a constraint, a value and a unit,
 * e.g. "max. 3000 euro"
 * 
 * @author burkhardt.felix
 * 
 */
public class Value {
	private String _valuesId = null;
	private String _featureName = null; 
	private String _unit = null;
	private boolean _isMax = false;
	private boolean _isMin = false;
	private double _valueDouble = 0;
	private int _valueInt = 0;
	private double _valueLowerDouble = 0;
	private int _valueLowerInt = 0;
	private boolean _hasLowerBound = false;
	private boolean _isDouble = false;

	/**
	 * Constructor.
	 * 
	 * @param _valuesId
	 *            The id of the values descriptor.
	 * @param isDouble
	 *            If true, the numerical values are treated as doubles, else as
	 *            integers.
	 */
	public Value(String _valuesId, String featureName, boolean isDouble) {
		super();
		this._valuesId = _valuesId;
		this._featureName = featureName;
		_isDouble = isDouble;
	}

	/**
	 * Constructor from a JSon array.
	 * 
	 * @param array
	 *            The JSon array.
	 */
	public Value(JSONArray array) {
		super();
		try {
			for (int i = 0; i < array.length(); i++) {
				JSONObject testObj = array.getJSONObject(i);
				if (testObj.has(Constants.STRING_ID)) {
					_valuesId = testObj.getString(Constants.STRING_ID);
				} else if (testObj.has(Constants.STRING_ISDOUBLE)) {
					_isDouble = testObj.getBoolean(Constants.STRING_ISDOUBLE);
				} else if (testObj.has(Constants.STRING_VALUE)) {
					if (_isDouble) {
						_valueDouble = testObj
								.getDouble(Constants.STRING_VALUE);
					} else {
						_valueInt = testObj.getInt(Constants.STRING_VALUE);
					}
				} else if (testObj.has(Constants.STRING_UNIT)) {
					_unit = testObj.getString(Constants.STRING_UNIT);
				} else if (testObj.has(Constants.STRING_ISMAX)) {
					_isMax = testObj.getBoolean(Constants.STRING_ISMAX);
				} else if (testObj.has(Constants.STRING_ISMIN)) {
					_isMin = testObj.getBoolean(Constants.STRING_ISMIN);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean hasLowerBound() {
		return _hasLowerBound;
	}

	public double getValueLowerDouble() {
		return _valueLowerDouble;
	}

	public void setValueLowerDouble(double valueLowerDouble) {
		_hasLowerBound = true;
		if (isDouble()) {
			this._valueLowerDouble = valueLowerDouble;
		} else {
			this._valueLowerInt = (int) valueLowerDouble;
		}
	}

	public int getValueLowerInt() {
		return _valueLowerInt;
	}

	public void setValueLowerInt(int value) {
		_hasLowerBound = true;
		if (isDouble()) {
			this._valueLowerDouble = value;
		} else {
			this._valueLowerInt = value;
		}
	}

	/**
	 * Get the value descriptor id.
	 * 
	 * @return The id of the values descriptor, e.g. "price"
	 */
	public String get_valuesId() {
		return _valuesId;
	}

	/**
	 * Get the unit string.
	 * 
	 * @return The unit string, e.g. "euro".
	 */
	public String get_unit() {
		return _unit;
	}

	/**
	 * Set the unit string.
	 * 
	 * @param unit
	 *            The unit string, e.g. "euro".
	 */
	public void set_unit(String _unit) {
		this._unit = _unit;
	}

	public String getFeatureName() {
		return _featureName;
	}

	public void setFeatureName(String featureName) {
		this._featureName = featureName;
	}

	/**
	 * Test if this is a maximal value.
	 * 
	 * @return True if is maximal value.
	 */
	public boolean is_isMax() {
		return _isMax;
	}

	/**
	 * Test if values is double.
	 * 
	 * @return True if value is double.
	 */
	public boolean isDouble() {
		return _isDouble;
	}

	/**
	 * Test if value is integer value.
	 * 
	 * @return True if not double value.
	 */
	public boolean isInteger() {
		return !_isDouble;
	}

	/**
	 * Set true if this is a maximal value.
	 * 
	 * @param _isMmax
	 *            The boolean.
	 */
	public void set_isMax(boolean _isMmax) {
		this._isMax = _isMmax;
	}

	/**
	 * Rename the value identifcator.
	 * 
	 * @param newId
	 */
	public void setValuesId(String newId) {
		_valuesId = newId;
	}

	public void appendValuesId(String postfix) {
		_valuesId += postfix;
		if(_featureName != null) {
			_featureName += postfix;			
		}
	}

	public void prependValuesId(String prefix) {
		_valuesId = prefix + _valuesId;
		if(_featureName != null) {
			_featureName = prefix + _featureName;			
		}
	}

	/**
	 * Test if this is a minimal value.
	 * 
	 * @return True if is minimal value.
	 */
	public boolean is_isMin() {
		return _isMin;
	}

	/**
	 * Set true if this is a minimal value.
	 * 
	 * @return The boolean.
	 */
	public void set_isMin(boolean _isMin) {
		this._isMin = _isMin;
	}

	/**
	 * Get the value.
	 * 
	 * @return The value, e.g. "234" or "2.54"
	 */
	public double get_value() {
		if (_isDouble)
			return _valueDouble;
		else {
			return _valueInt;
		}
	}

	public String get_valueString() {
		if (_isDouble)
			return String.valueOf(_valueDouble);
		else {
			return NumberFormat.add1000SepMark(_valueInt);
		}
	}

	/**
	 * Force this value, cast if neccessary.
	 * 
	 * @return The value as integer.
	 */
	public int get_integerValue() {
		if (_isDouble)
			return (int) _valueDouble;
		else {
			return _valueInt;
		}
	}

	/**
	 * Set the double value.
	 * 
	 * @param value
	 *            The value.
	 */
	public void set_value(double value) {
		if (isDouble()) {
			this._valueDouble = value;
		} else {
			_valueInt = (int) value;
		}
	}

	/**
	 * Set the integer value.
	 * 
	 * @param value
	 *            The value.
	 */
	public void set_value(int value) {
		if (isDouble()) {
			this._valueDouble = value;
		} else {
			_valueInt = value;
		}
	}

	/**
	 * Get a string representation for this instance.
	 */
	public String toString() {
		String unit = "";
		if (_unit != null) {
			unit = " " + _unit;
		}
		String max = "";
		if (_isMax) {
			max = "max ";
		}
		String min = "";
		if (_isMin) {
			min = "min ";
		}
		String value = isDouble() ? String.valueOf(_valueDouble) : NumberFormat
				.add1000SepMark(_valueInt);
		return "[" + _valuesId + ": " + max + min + value + unit + "]";
	}

	/**
	 * Get a JSon array containing all fields.
	 * 
	 * @return The JSon array.
	 */
	public JSONArray toJsonArray() {
		try {
			JSONObject valuesId = new JSONObject().put(Constants.STRING_ID,
					_valuesId);
			JSONObject value = new JSONObject().put(Constants.STRING_VALUE,
					get_valueString());
			JSONObject unit = new JSONObject().put(Constants.STRING_UNIT,
					get_unit());
			JSONObject isMin = new JSONObject().put(Constants.STRING_ISMIN,
					String.valueOf(_isMin));
			JSONObject isMax = new JSONObject().put(Constants.STRING_ISMAX,
					String.valueOf(_isMax));
			JSONObject isDouble = new JSONObject().put(
					Constants.STRING_ISDOUBLE, String.valueOf(_isDouble));
			JSONArray array = new JSONArray();
			array.put(valuesId);
			array.put(isDouble);
			array.put(value);
			array.put(unit);
			array.put(isMin);
			array.put(isMax);
			return array;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
