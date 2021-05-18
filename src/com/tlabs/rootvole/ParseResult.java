package com.tlabs.rootvole;

import java.util.Collections;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.felix.util.StringUtil;

/**
 * Class for the result of a parsing process. This is mainly a set of entities
 * and values.
 * 
 * @author burkhardt.felix
 * 
 */
public class ParseResult {
	private Vector<Entity> _entities;
	private Vector<Value> _values;
	private String _input = null, _parserID = "", _parserVersion = "",
			_rest = "", _origString = "";
	private String[] _restArray = null;

	/**
	 * Constructor.
	 * 
	 * @param input
	 *            The input string.
	 * @param parserId
	 *            The id string for the parser.
	 * @param parserVersion
	 *            The version string for the parser.
	 */
	public ParseResult(String input, String parserId, String parserVersion) {
		super();
		_parserID = parserId;
		_parserVersion = parserVersion;
		this._input = input;
		_entities = new Vector<Entity>();
		_values = new Vector<Value>();
	}

	/**
	 * Constructor from a Json object.
	 * 
	 * @param jsonObject
	 *            The json object.
	 */
	public ParseResult(JSONObject jsonObject) {
		try {
			_entities = new Vector<Entity>();
			_values = new Vector<Value>();
			JSONArray outputAll = jsonObject
					.getJSONArray(Constants.STRING_PARSEROUTPUT);
			for (int i = 0; i < outputAll.length(); i++) {
				JSONObject jo = outputAll.getJSONObject(i);
				if (jo.has(Constants.STRING_ID)) {
					_parserID = jo.getString(Constants.STRING_ID);
				} else if (jo.has(Constants.STRING_PARSERVERSION)) {
					_parserVersion = jo
							.getString(Constants.STRING_PARSERVERSION);
				} else if (jo.has(Constants.STRING_INPUT)) {
					_input = jo.getString(Constants.STRING_INPUT);
				} else if (jo.has(Constants.STRING_PARSERESULT)) {
					JSONObject outputObj = jo
							.getJSONObject(Constants.STRING_PARSERESULT);
					try {
						JSONArray valArray = outputObj
								.getJSONArray(Constants.STRING_VALUES);
						for (int j = 0; j < valArray.length(); j++) {
							JSONObject valObj = valArray.getJSONObject(j);
							Value val = new Value(
									valObj.getJSONArray(Constants.STRING_VALUE));
							addValue(val);
						}
					} catch (JSONException jse) {
						// no values
					}
					try {
						JSONArray entArray = outputObj
								.getJSONArray(Constants.STRING_ENTITIES);
						for (int k = 0; k < entArray.length(); k++) {
							JSONObject entObj = entArray.getJSONObject(k);
							Entity ent = new Entity(
									entObj.getJSONArray(Constants.STRING_ENTITY));
							addEntity(ent);
						}
					} catch (JSONException jse) {
						// no entities
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add an entity to the result set.
	 * 
	 * @param entity
	 *            The entity.
	 */
	public void addEntity(Entity entity) {
		_entities.add(entity);
	}

	public void setRestArray(String[] a) {
		_restArray = a;
	}

	/**
	 * Get all entities.
	 * 
	 * @return The entities.
	 */
	public Vector<Entity> getEntities() {
		return _entities;
	}

	public boolean isIdContained(String id) {
		for (Entity e : _entities)
			if (e.get_id().compareTo(id) == 0)
				return true;
		return false;
	}

	/**
	 * Remove results in entities that are part of larger entity results, e.g.
	 * "alfa" and "romeo" for "alfa romeo"
	 */
	public void checkDoubles() {
		int cand_num = _entities.size();
		if (cand_num == 1)
			return;
		Vector<Entity> newEntities = new Vector<Entity>();
		int lastOffset = 0;
		sortEnties();
		for (int i = 0; i < cand_num; i++) {
			Entity act = _entities.elementAt(i);
			if (i == 0) {
				// newEntities.add(act);
				int firstOffset = act.getValue().get_offset();
				for (int k = 0; k < firstOffset; k++) {
					_rest += _restArray[k] + " ";
				}
				lastOffset = act.getValue().get_end();
			}
			boolean found = false;
			for (int j = 0; j < i; j++) {
				Entity pre = _entities.elementAt(j);
				if (act.isSmallerPartOf(pre)) {
					found = true;
					break;
				}
			}
			if (!found) {
				newEntities.add(act);
				int firstOffset = act.getValue().get_offset();
				for (int k = lastOffset; k < firstOffset; k++) {
					_rest += _restArray[k] + " ";
				}
				lastOffset = act.getValue().get_end();
			}
		}
		for (int k = lastOffset; k < _restArray.length; k++) {
			_rest += _restArray[k] + " ";
		}
		_entities = newEntities;
	}

	private void sortEnties() {
		for (int i = _entities.size(); i > 1; i--) {
			for (int j = 0; j < i - 1; j++) {
				Entity act = (Entity) _entities.elementAt(j);
				Entity next = (Entity) _entities.elementAt(j + 1);
				int offsetAct = act.getValue().get_offset();
				int offsetNext = next.getValue().get_offset();
				if (offsetAct > offsetNext) {
					Collections.swap(_entities, j, j + 1);
				} else if (offsetAct == offsetNext) {
					int endAct = act.getValue().get_end();
					int endNext = next.getValue().get_end();
					if (endNext > endAct) {
						Collections.swap(_entities, j, j + 1);
					}
				}
			}
		}
	}

	/**
	 * Remove results in entities which ids are already present
	 */
	public void checkDoubleIDs() {
		Vector<Entity> newEntities = new Vector<Entity>();
		for (int i = _entities.size() - 1; i >= 0; i--) {
			Entity act = _entities.elementAt(i);
			boolean found = false;
			for (int j = 0; j < i; j++) {
				Entity pre = _entities.elementAt(j);
				if (pre.get_id().compareTo(act.get_id()) == 0) {
					found = true;
				}
			}
			if (!found) {
				newEntities.add(act);
			}
		}
		_entities = newEntities;
	}

	/**
	 * Get all numerical values.
	 * 
	 * @return The values.
	 */
	public Vector<Value> getValues() {
		return _values;
	}

	/**
	 * Add a values to the results set.
	 * 
	 * @param value
	 *            The value.
	 */
	public void addValue(Value value) {
		_values.add(value);
	}

	/**
	 * Return String with not interpreted words.
	 * 
	 * @return Not interpreted words;
	 */
	public String getRest() {
		return _rest.trim();
	}

	public String getOrigString() {
		return _origString;
	}

	public void setOrigString(String _origString) {
		this._origString = _origString;
	}

	/**
	 * Get a string representation of this result.
	 */
	public String toString() {
		StringBuffer retBuf = new StringBuffer();
		retBuf.append("String " + _input);
		if (StringUtil.isFilled(_origString)) {
			retBuf.append(" (" + _origString + ")");
		}
		retBuf.append(", got " + _entities.size() + " entities and "
				+ _values.size() + " values, (rest: " + _rest + ") : ");
		for (Entity entity : _entities) {
			retBuf.append(entity.toString() + " ");
		}
		for (Value value : _values) {
			retBuf.append(value.toString() + " ");
		}
		return retBuf.toString();
	}

	public String printMatchedEntities() {
		StringBuffer retBuf = new StringBuffer();
		for (Entity entity : _entities) {
			retBuf.append(entity.toString() + " ");
		}
		return retBuf.toString().trim();
	}

	/**
	 * Transform into JSon object.
	 * 
	 * @return The JSon object.
	 */
	public JSONObject toJsonObject() {
		JSONArray array = new JSONArray();
		try {
			array.put(new JSONObject().put(Constants.STRING_ID, _parserID));
			array.put(new JSONObject().put(Constants.STRING_PARSERVERSION,
					_parserVersion));
			array.put(new JSONObject().put(Constants.STRING_INPUT, _input));
			JSONObject parseResult = new JSONObject();
			JSONArray entities = new JSONArray();
			for (Entity entity : _entities) {
				entities.put(new JSONObject().put(Constants.STRING_ENTITY,
						entity.toJsonArray()));
			}
			parseResult.put(Constants.STRING_ENTITIES, entities);
			JSONArray values = new JSONArray();
			for (Value value : _values) {
				values.put(new JSONObject().put(Constants.STRING_VALUE,
						value.toJsonArray()));
			}
			parseResult.put(Constants.STRING_VALUES, values);
			array.put(new JSONObject().put(Constants.STRING_PARSERESULT,
					parseResult));
			return new JSONObject().put(Constants.STRING_PARSEROUTPUT, array);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
