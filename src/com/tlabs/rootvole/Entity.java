package com.tlabs.rootvole;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An entity is a single value that might be present or not, could be several
 * words, e.g. "alpha romeo". Can be matched by a vocabulary
 */
public class Entity {
	String _vocabId = null, _id = null;
	MultiStringWord _value = null;
	boolean _isSingleSlot = false;

	/**
	 * Create a new entity object.
	 * 
	 * @param _vocabId
	 *            The id of the vocabulary it belongs to.
	 */
	public Entity(String _vocabId) {
		super();
		this._vocabId = _vocabId;
	}

	/**
	 * Test if this entity stems from a single slot vocabulary, i.e. a vocab
	 * that should only deliver maximum one value.
	 * 
	 * @return
	 */
	public boolean isSingleSlot() {
		return _isSingleSlot;
	}

	/**
	 * Create an entity object from a JSon array (compatible to the toJsonObject
	 * method).
	 * 
	 * @param array
	 *            The JSon array.
	 */
	protected Entity(JSONArray array) {
		super();
		try {
			for (int i = 0; i < array.length(); i++) {
				JSONObject testObj = array.getJSONObject(i);
				if (testObj.has(Constants.STRING_VOCAB)) {
					_vocabId = testObj.getString(Constants.STRING_VOCAB);
				} else if (testObj.has(Constants.STRING_VALUE)) {
					String value = testObj.getString(Constants.STRING_VALUE);
					_value = new MultiStringWord(value);

				} else if (testObj.has(Constants.STRING_ID)) {
					_id = testObj.getString(Constants.STRING_ID);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public boolean isSameWord(Entity other) {
		if (_value.isSameWord(other.getValue()))
			return true;
		return false;
	}

	/**
	 * Get the id for this entity.
	 * 
	 * @return the id or null of not set.
	 */
	public String get_id() {
		return _id;
	}

	/**
	 * Set the id.
	 * 
	 * @param _id
	 *            The id.
	 */
	public void setId(String _id) {
		this._id = _id;
	}

	/**
	 * Get the vocabulary id set in the constructor.
	 * 
	 * @return The vocabulary id.
	 */
	public String getVocabId() {
		return _vocabId;
	}

	/**
	 * Test if this entity belongs to a specific vocabulary.
	 * 
	 * @param vocabId
	 * @return
	 */
	public boolean isOfVocab(String vocabId) {
		if (_vocabId.compareTo(vocabId) == 0)
			return true;
		return false;
	}

	/**
	 * Get the value, i.e. a String might contain several sub words separated by
	 * blanks.
	 * 
	 * @return The value.
	 */
	public MultiStringWord getValue() {
		return _value;
	}

	/**
	 * Set the value string.
	 * 
	 * @param _value
	 *            The value.
	 */
	public void setValue(MultiStringWord _value) {
		this._value = _value;
	}

	/**
	 * Get a string representation of this object.
	 * 
	 */
	public String toString() {
		String id = "";
		if (_id.compareTo(_value.get_word()) != 0) {
			id = " (" + _id + ")";
		}
		return "[" + _vocabId + ": " + _value.get_word() + id + "]";
	}

	/**
	 * Represent this object as a Json array containing the values for all
	 * fields.
	 * 
	 * @return The JSon array.
	 */
	protected JSONArray toJsonArray() {
		try {
			JSONObject vocab = new JSONObject().put(Constants.STRING_VOCAB,
					_vocabId);
			JSONObject value = new JSONObject().put(Constants.STRING_VALUE, "");
			if (_value != null)
				value = new JSONObject().put(Constants.STRING_VALUE,
						_value.get_word());
			JSONObject id = new JSONObject().put(Constants.STRING_ID, _id);
			JSONArray array = new JSONArray();
			array.put(vocab);
			array.put(value);
			array.put(id);
			return array;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Test if this entity is a substring of another entity.
	 * 
	 * @param other
	 *            The other entity.
	 * @return True e.g. for this = "alfa romeo" and other = "no alfa romeo"
	 */
	protected boolean isTotallyContained(Entity other) {
		return _value.isTotallyContainted(other.getValue());
	}

	protected boolean isSmallerPartOf(Entity other) {
		return _value.isSmallerPartOf(other.getValue());
	}
	protected void setSingleSlot(boolean isSingleSlot) {
		this._isSingleSlot = isSingleSlot;
	}

	protected void appendVocabId(String postfix) {
		_vocabId += postfix;
	}

	protected void prependVocabId(String prefix) {
		_vocabId = prefix + _vocabId;
	}


}
