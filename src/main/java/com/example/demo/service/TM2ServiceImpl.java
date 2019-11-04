package com.example.demo.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.groovy.util.Maps;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.demo.dao.TM2Dao;

@Service
public class TM2ServiceImpl implements TM2Service{
	@Autowired
	TM2Dao tm2Dao;
	@Value("${custom.tm2Url}")
	String tm2Url;
	
	public List<Map<String, Object>> getSanggaMasterList() {
		return tm2Dao.getSanggaMasterList();
	}

	public Map<String, Object> getSanggaInfo(int id) {
		return tm2Dao.getSanggaInfo(id);
	}
	
	public void addSentimentData(List<Map<String, Object>> data) {
		tm2Dao.insertSentimentData(data);
	}
	
	public void saveExclude(int keywordId, String sns, String exclude) {
		tm2Dao.updateExclude(keywordId, sns, exclude);
	}

	@Async
	public Map<String, Object> collectSanggaData(String startUrl, String endUrl) {
		
		List<String> wrongRequest = new ArrayList<>();
		
		Map<String, Object> rs = new HashMap<>();
		
		List<Map<String,Object>> sanggaMasterList = getSanggaMasterList();
		
		for (Map<String,Object> sanggaMaster:sanggaMasterList) {
			
			String keyword = (String)sanggaMaster.get("keyword");
			Integer keywordId = (Integer)sanggaMaster.get("keywordId");
			String sns = (String)sanggaMaster.get("sns");
			String include = (String)sanggaMaster.get("include");
			String exclude = (String)sanggaMaster.get("exclude");
			
			System.out.println("==" + keyword + "(" + sns + ")==");
			System.out.println("- 조회기간: " + startUrl + " ~ " + endUrl);
			
			// tm2 URL 생성
			String tm2Url = setUrlOfAssociationTransitionBySentiment(keyword, sns,include,exclude, startUrl, endUrl);
	
			// tm2 API 조회
			String dataStr = readURL(tm2Url);

			if (dataStr == null) {
				wrongRequest.add(tm2Url);
				continue;
			}
			
			Map<String, Object> sanggaInfo = getSanggaInfo(keywordId);
			
			// 분석 결과 데이터 형태로 만듬 
			List<Map<String,Object>> data = getSanggaAnalysisData(keyword,keywordId,sns,sanggaInfo,dataStr);

			if (data != null && data.size() != 0) {
				System.out.println("데이터 " + data);
				addSentimentData(data);
			}
			
		}

		System.out.println("==" + startUrl + " ~ " + endUrl + " 기간 데이터 수집 완료==");

		rs.put("wrongRequest", wrongRequest);
		rs.put("alertMsg", "감성 분석 및 저장 완료");
		
		return rs;
	}
	
	@Async
	private String setUrlOfAssociationTransitionBySentiment(String keyword, String sns, String include, String exclude, String startDate, String endDate) {
		
		String source = "";
		
		if (sns.equals("B")) {
			source = "blog";
			
		} else if (sns.equals("I")) {
			source = "insta";
			
		} else if (sns.equals("N")) {
			source = "news";
			
		} else if (sns.equals("T")) {
			source = "twitter";
			
		} 
		
		
		// period >> 0 : 일별, 1 : 주별,2 : 월별, 3 : 분기,4 : 반기, 5 : 연간
		 
		String urlKeyword = "&keyword=" + URLEncoder.encode(keyword);
		String urlKeywordFilterListPlus = "&keywordFilterList[]=" + URLEncoder.encode("+" + include);
		String urlKeywordFilterListMinus = "&keywordFilterList[]=" + URLEncoder.encode("-" + exclude);
		String urlBasicTm2Condition = "lang=ko&command=GetAssociationTransitionBySentiment&topN=1&cutOffFrequencyMin=0&cutOffFrequencyMax=0&start_weekday=SUNDAY&categorySetName=SM";
		String urlSource = "&source=" + source; // insta, blog, twitter, news ...
		String urlStartDate = "&startDate=" + startDate;
		String urlEndDate = "&endDate=" + endDate;
//		String urlRowPerPage = "&rowPerPage=" + rowPerPage;
//		String urlPageNum = "&pageNum=" + pageNum;
		
		String urlPeriod = "&period=" + "0";
		
		String keywordUrl = tm2Url + urlBasicTm2Condition
									+ urlKeyword
									+ urlKeywordFilterListPlus
									+ urlKeywordFilterListMinus
									+ urlSource
									+ urlStartDate 
									+ urlEndDate
									+ urlPeriod;
		
		return keywordUrl;
	}
	
	@Async
	private String readURL(String kewordUrl) {
		
		BufferedReader br = null;
		String DataStr = "";
        String DataLine = "";
		
        try{            
        	// Web to Web
            URL url = new URL(kewordUrl);
            HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
            urlconnection.setRequestMethod("GET");
            br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream(),"UTF-8"));
            
            if ((DataLine = br.readLine()) == null) {
            	return null;
            }
            
            while(DataLine != null) {
            	DataStr = DataStr + DataLine + "\n";
            	DataLine = br.readLine();
            }
            
            br.close();
            
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        
        return DataStr;
	}
	
	@Async
	private List<Map<String,Object>> getSanggaAnalysisData(String keyword, int keywordId, String sns, Map<String,Object> sanggaInfo, String dataStr) {
		
		List<Map<String,Object>> JsonData = new ArrayList<>();
		
		try{
			
			// Json parser를 만들어 만들어진 문자열 데이터를 객체화 합니다. 
			JSONParser parser = new JSONParser(); 
			JSONObject obj = (JSONObject) parser.parse(dataStr); 
			
			// Top레벨 단계인 rows 키를 가지고 데이터를 파싱합니다. 
			//JSONObject parse_rows = (JSONObject) obj.get("rows"); 
			
			// List인 rows의 요소를 받아오기 : 뒤에 [ 로 시작하므로 jsonarray이다 
			JSONArray rows = (JSONArray) obj.get("rows"); 
			
			JSONObject row; 
			JSONObject keywordSentiment;
			String regDate; 
			Long positive;
			Long negative;
			Long neutral;
			
			// parse_item은 배열형태이기 때문에 하나씩 데이터를 하나씩 가져올때 사용합니다. 
			// 필요한 데이터만 가져오려고합니다. 
			for(int i = 0 ; i < rows.size(); i++) { 
				row = (JSONObject) rows.get(i); 
				
				regDate = (String)row.get("date");
				
				keywordSentiment = (JSONObject) row.get(keyword); 
				positive = (Long)keywordSentiment.get("positive");
				negative = (Long)keywordSentiment.get("negative");
				neutral = (Long)keywordSentiment.get("neutral");
				
				Long empty = new Long(0);
				
				if (!positive.equals(empty) || !negative.equals(empty) || !neutral.equals(empty)) {
					System.out.println(regDate + ", " + positive + ", " + negative + ", " + neutral );
					
					Long denominator = positive + negative + neutral;
					
							
					Double sentimentScore = denominator == 0? (double)50 : (double)((1*positive + 0*neutral + (-1)*negative)*100/(denominator) + 100)/2;
					
//					
//					System.out.println(Maps.of("hdName", sanggaInfo.get("hdName"),
//							"hdCode",sanggaInfo.get("hdCode"),
//							"categoryCode",sanggaInfo.get("categoryCode"),
//							"categoryName",sanggaInfo.get("categoryName")));
					JsonData.add(
							Maps.of("sns",sns,
									"keyword",keyword,
									"keywordId",keywordId,
									"hdName", sanggaInfo.get("hdName"),
									"hdCode",sanggaInfo.get("hdCode"),
									"categoryCode",sanggaInfo.get("categoryCode"),
									"categoryName",sanggaInfo.get("categoryName"),
									"positive",positive, 
									"negative",negative, 
									"neutral", neutral,
									"sentimentScore",sentimentScore,
									"regDate",regDate));
					
				}
			} 
			
		}catch(Exception e){ 
			System.out.println(e.getMessage()); 
		}
		
		return JsonData;
	}
}
