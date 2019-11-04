package com.example.demo.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface KeywordDao {

	public void insert(List<Map<String, Object>> data);

	public void insertToMonthlyTable(List<Map<String, Object>> data2);

	public List<Map<String, Object>> select();

	public void truncate();

	public void truncateToMonthlyTable();

	public List<Map<String, Object>> selectMonthlyData();

	public List<Map<String, Object>> selectTotal();



}
