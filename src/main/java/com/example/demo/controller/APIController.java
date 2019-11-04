package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.service.APIService;


@RequestMapping("/api")
@Controller
public class APIController {
	@Autowired
	APIService apiSerivce;
	
	@RequestMapping("/search")
	public String showSearch(String keyword, Model model) {
		
		if (keyword == null || keyword.trim().length() == 0) {
			
			model.addAttribute("alertMsg", "분석할 키워드가 없습니다.");
			model.addAttribute("historyBack", "true");
			
			return "common/redirect";
		}
		
		keyword.trim();
		
		List<Map<String,Object>> keywordInfo = apiSerivce.getKeywordInfo(keyword);
		
		// 입력한 키워드가 존재하지 않는 경우
		if (keywordInfo == null || keywordInfo.size() == 0) {
			model.addAttribute("alertMsg", "'" + keyword + "' 에 대한 분석 결과가 없습니다.");
			model.addAttribute("historyBack", "true");
			
			return "common/redirect";
		}

		// 입력한 키워드 id가 여러곳인 경우
		if (keywordInfo.size() > 1) {
			// 주소가 다른 업체들인지 확인
			boolean uniqueSangga = apiSerivce.checkUniqueKeyword(keyword) > 1 ? false:true;
			
			if (uniqueSangga) {
//				model.addAttribute("multipleCategory", "true");
//				model.addAttribute("keyword", keyword);
//				model.addAttribute("keywordInfo", keywordInfo.get(0));
//				
//				return "api/search";
				

				model.addAttribute("alertMsg",  "'" + keyword + "' 의 카테고리가 1개 이상입니다.");
				model.addAttribute("historyBack", "true");
				
				return "common/redirect";
			}
			
			// 하나의 업체를 선택해 업체 id로 접근
			
			// 카테고리가 여러개인 것이라면 모두 합쳐서 접근
			
			model.addAttribute("alertMsg",  "'" + keyword + "' 와 일치하는 업체가 1개 이상입니다.");
			model.addAttribute("historyBack", "true");
			
			return "common/redirect";
		}
		

		// 입력한 키워드의 자동완성 키워드가 있는 경우??
		
		model.addAttribute("keyword", keyword);
		model.addAttribute("keywordInfo", keywordInfo.get(0));
		
		return "api/search";
	}
	
	@RequestMapping("/get/json/keywordRank")
	@ResponseBody
	public JSONArray getJSONKeywordRank() {
		
		
		// 고유 id가 여러개인 키워드인지 검사해보고 선택하게 하기?
		
		if (true) {
			// 월별 키워드 감성어 분석 결과 모드
			List<Map<String,Object>> dataList = apiSerivce.getKeywordRank();

			JSONArray resultArr = new JSONArray();
			
			for (Map<String,Object> data:dataList) {

				JSONObject jsonObject = new JSONObject();
				
				for( Map.Entry<String, Object> entry : data.entrySet() ) {

					String key = entry.getKey();
					
					Object value = entry.getValue();
					
		            jsonObject.put(key, value);
		            
		        }
				resultArr.add(jsonObject);
			}
			
			return resultArr;
		}
				
		return null;
	}
	
	@RequestMapping("/get/json/keywordMonthly")
	@ResponseBody
	public JSONArray getJSONKeywordMonthlyData(String keyword) {
		
	
		if (keyword == null || keyword == "") {
			
			return null;
		} else {

			keyword = keyword.trim();
		}
		
		if (true) {
			List<Map<String,Object>> dataList = apiSerivce.getKeywordSentimentData(keyword);

			JSONArray resultArr = new JSONArray();
			
			for (Map<String,Object> data:dataList) {

				JSONObject jsonObject = new JSONObject();
				
				for( Map.Entry<String, Object> entry : data.entrySet() ) {

					String key = entry.getKey();
					
					Object value = entry.getValue();
					
		            jsonObject.put(key, value);
		            
		        }
				resultArr.add(jsonObject);
			}
			
			return resultArr;
		}
				
		return null;
	}
	
	@RequestMapping("/get/json/keywordTotal")
	@ResponseBody
	public List<JSONObject> getJSONKeywordTotalData(@RequestParam Map<String,Object> param) {
		
		
		String keyword = (String)param.get("keyword");
	
		if (keyword == null || keyword == "") {
			
			return null;
		} else {

			keyword = keyword.trim();
		}
		
		
		// 고유 id가 여러개인 키워드인지 검사해보고 선택하게 하기?
		
		if (true) {
			// 월별 키워드 감성어 분석 결과 모드
			Map<String,Object> data = apiSerivce.getKeywordTotalData(keyword);

			// 출력할 데이터들을 담을 리스트
			List<JSONObject> result = new ArrayList<>();
				
			for( Map.Entry<String, Object> entry : data.entrySet() ) {
				JSONObject jsonObject = new JSONObject();
				
				String key = entry.getKey();
				
				if (key.equals("positive")) {
					key = "긍정";
				} else {
					key = "부정";
				}

	            jsonObject.put("sentiment", key);
	            jsonObject.put("count", entry.getValue());
	            
		        result.add(jsonObject);
	        }
			
			return result;
		}
				
		return null;
	}
	
	@RequestMapping("/get/json/totalCompare")
	@ResponseBody
	public JSONArray getJSONTotalCompareData(String keyword) {
		
	
		if (keyword == null || keyword == "") {
			
			return null;
		} else {

			keyword = keyword.trim();
		}
		
		// 고유 id가 여러개인 키워드인지 검사해보고 선택하게 하기?
		
		if (true) {
			// 월별 키워드 감성어 분석 결과 모드
			List<Map<String, Object>> dataList = apiSerivce.getTotalCompareData(keyword);

			JSONArray resultArr = new JSONArray();
			
			for (Map<String,Object> data:dataList) {

				JSONObject jsonObject = new JSONObject();
				
				for( Map.Entry<String, Object> entry : data.entrySet() ) {

					String key = entry.getKey();
					
					Object value = entry.getValue();
					
		            jsonObject.put(key, value);
		            
		        }
				resultArr.add(jsonObject);
			}
			
			return resultArr;
		}
				
		return null;
	}
	
	@RequestMapping("/get/json/local")
	@ResponseBody
	public JSONArray getJSONLocalData() {
		
		if (true) {
			List<Map<String,Object>> dataList = apiSerivce.getLocalData(); 

			JSONArray resultArr = new JSONArray();
			
			for (Map<String,Object> data:dataList) {

				JSONObject jsonObject = new JSONObject();
				
				for( Map.Entry<String, Object> entry : data.entrySet() ) {

					String key = entry.getKey();
					
					Object value = entry.getValue();
					
		            jsonObject.put(key, value);
		            
		        }
				resultArr.add(jsonObject);
			}
			
			return resultArr;
		}
				
		return null;
	}
	
	@RequestMapping("/get/json/category")
	@ResponseBody
	public JSONArray getJSONCategoryData() {
		
		if (true) {
			List<Map<String,Object>> dataList = apiSerivce.getCategoryData(); 

			JSONArray resultArr = new JSONArray();
			
			for (Map<String,Object> data:dataList) {

				JSONObject jsonObject = new JSONObject();
				
				for( Map.Entry<String, Object> entry : data.entrySet() ) {

					String key = entry.getKey();
					
					Object value = entry.getValue();
					
		            jsonObject.put(key, value);
		            
		        }
				resultArr.add(jsonObject);
			}
			
			return resultArr;
		}
				
		return null;
	}
	
	@RequestMapping("/get/json/total")
	@ResponseBody
	public List<JSONObject> getJSONTotalData() {
		
		
		// 고유 id가 여러개인 키워드인지 검사해보고 선택하게 하기?
		
		if (true) {
			// 월별 키워드 감성어 분석 결과 모드
			Map<String,Object> data = apiSerivce.getTotalData();

			// 출력할 데이터들을 담을 리스트
			List<JSONObject> result = new ArrayList<>();
				
			for( Map.Entry<String, Object> entry : data.entrySet() ) {
				JSONObject jsonObject = new JSONObject();
				
				String key = entry.getKey();
				
				if (key.equals("positive")) {
					key = "긍정";
				} else {
					key = "부정";
				}

	            jsonObject.put("sentiment", key);
	            jsonObject.put("count", entry.getValue());
	            
		        result.add(jsonObject);
	        }
			
			return result;
		}
				
		return null;
	}
}
