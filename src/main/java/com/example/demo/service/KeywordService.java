package com.example.demo.service;

import java.util.List;
import java.util.Map;

public interface KeywordService {
	public void resetBeforeData();

	public void addData(List<Map<String, Object>> data);
	
	public void addDataToMonthlyTable(List<Map<String, Object>> data2);

	public List<Map<String, Object>> getData();

	public List<Map<String, Object>> getMonthlyData();

	public List<Map<String, Object>> getTotalData();



}
