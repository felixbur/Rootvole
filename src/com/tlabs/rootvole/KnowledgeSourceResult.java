package com.tlabs.rootvole;

public class KnowledgeSourceResult {
	private String _knowledgeSourceName = "";
	private String _knowledgeSourceAnswer = "";
	public KnowledgeSourceResult(String _knowledgesourceName,
			String _knowledgesourceAnswer) {
		super();
		this._knowledgeSourceName = _knowledgesourceName;
		this._knowledgeSourceAnswer = _knowledgesourceAnswer;
	}
	public String getKnowledgeSourceName() {
		return _knowledgeSourceName;
	}
	public String getKnowledgeSourceAnswer() {
		return _knowledgeSourceAnswer;
	}
	
}
