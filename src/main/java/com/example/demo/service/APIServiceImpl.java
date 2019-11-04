package com.example.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.TM2Dao;

@Service
public class APIServiceImpl implements APIService{
	@Autowired
	TM2Dao tm2Dao;

	public List<Map<String, Object>> getKeywordSentimentData(String keyword) {
		return tm2Dao.getKeywordSentimentData(keyword);
	}
	
	public Map<String, Object> getTotalData() {
		return tm2Dao.getTotalData();
	}

	public Map<String, Object> getKeywordTotalData(String keyword) {

		return tm2Dao.getKeywordTotalData(keyword);
	}

	public List<Map<String, Object>> getLocalData() {
		return tm2Dao.getLocalData();
	}
	
	public List<Map<String, Object>> getKeywordInfo(String keyword) {
		return tm2Dao.getKeywordInfo(keyword);
	}

	public List<Map<String, Object>> getCategoryData() {
		return tm2Dao.getCategoryData();
	}
	
	public List<Map<String, Object>> getTotalCompareData(String keyword) {
		return tm2Dao.getTotalCompareData(keyword);
	}
	
	public List<Map<String, Object>> getKeywordRank() {
		return tm2Dao.getKeywordRank();
	}
	
	public Integer checkUniqueKeyword(String keyword) {
		return tm2Dao.checkUniqueKeyword(keyword);
	}
}
