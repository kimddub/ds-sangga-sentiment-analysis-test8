package com.example.demo.service;

import java.util.List;
import java.util.Map;

public interface APIService {
	
	public List<Map<String, Object>> getKeywordSentimentData(String keyword);

	public Map<String, Object> getTotalData();
	
	public Map<String, Object> getKeywordTotalData(String keyword);

	public List<Map<String, Object>> getLocalData();

	public List<Map<String, Object>> getKeywordInfo(String keyword);

	public List<Map<String, Object>> getCategoryData();

	public List<Map<String, Object>> getTotalCompareData(String keyword);

	public List<Map<String, Object>> getKeywordRank();

	public Integer checkUniqueKeyword(String keyword);


}
