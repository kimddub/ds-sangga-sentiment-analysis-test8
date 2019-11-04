package com.example.demo.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TM2Dao {

	public List<Map<String, Object>> getSanggaMasterList();
	
	public Map<String, Object> getSanggaInfo(int id);
	
	public void insertSentimentData(List<Map<String, Object>> data);
	
	public List<Map<String, Object>> getKeywordSentimentData(String keyword);

	public List<Map<String, Object>> getLocalData();
	
	public Map<String, Object> getTotalData();
	
	public Map<String, Object> getKeywordTotalData(String keyword);
	
	public void updateExclude(int keywordId, String sns, String exclude);
	
	public String getInclude(int keyword);
	
	public String getExclude(int keywordId,String sns);

	public String getKeyword(int keyword);

	public Integer[] getKeywordIdList();

	public List<Map<String, Object>> getKeywordInfo(String keyword);

	public List<Map<String, Object>> getCategoryData();

	public List<Map<String, Object>> getTotalCompareData(String keyword);

	public List<Map<String, Object>> getKeywordRank();

	public Integer checkUniqueKeyword(String keyword);




}
