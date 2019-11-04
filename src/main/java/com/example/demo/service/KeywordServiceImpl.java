package com.example.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.KeywordDao;

@Service
public class KeywordServiceImpl implements KeywordService {
	@Autowired
	KeywordDao kDao;
	
	public void resetBeforeData() {
		kDao.truncate();
		kDao.truncateToMonthlyTable();
	}
	
	public void addData(List<Map<String, Object>> data) {
		kDao.insert(data);
	}
	
	public void addDataToMonthlyTable(List<Map<String, Object>> data2) {
		kDao.insertToMonthlyTable(data2);
	}
	
	public List<Map<String, Object>> getData() {
		return kDao.select();
	}
	
	public List<Map<String, Object>> getMonthlyData() {
		return kDao.selectMonthlyData();
	}
	
	public List<Map<String, Object>> getTotalData() {
		return kDao.selectTotal();
	}
}
