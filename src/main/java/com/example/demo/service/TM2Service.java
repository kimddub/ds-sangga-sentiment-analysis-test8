package com.example.demo.service;

import java.util.List;
import java.util.Map;

public interface TM2Service {
	public List<Map<String, Object>> getSanggaMasterList();
	
	public Map<String, Object> getSanggaInfo(int id);
	
	public void addSentimentData(List<Map<String, Object>> data);
	
	public void saveExclude(int keywordId, String sns, String exclude);

	public Map<String, Object> collectSanggaData(String startUrl, String endUrl);





}
