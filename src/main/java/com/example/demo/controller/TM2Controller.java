package com.example.demo.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.service.TM2Service;

@RequestMapping("/tm2")
@Controller
@EnableAsync
public class TM2Controller {
	@Autowired
	TM2Service tm2Serivce;
	@Value("${custom.tm2Url}")
	String tm2Url;
	
	@RequestMapping("test")
	@ResponseBody
	public String test() {
		return "test";
	}
	
	@Scheduled(cron = "0 1 0 * * *")
	@RequestMapping("/collectEveryday")
	public void collectTM2DataEveryday() { //뷰창 필요없음 //어제에 대해서 실행하는 애
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -1);
		
		Date yesterday = cal.getTime();
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
		String dateUrl = dateFormatter.format(yesterday);
		
		
		Map<String, Object> rs = tm2Serivce.collectSanggaData(dateUrl,dateUrl);
		
		// 시행결과 기록에 남길 때
		
		// 시행결과 뷰로 띄울 때
//		String resultCode = (String)rs.get("resultCode");
//		String alertMsg = (String)rs.get("alertMsg");
//		
//		if (resultCode.startsWith("F")) {
//			model.addAttribute("historyBack", "true");
//			model.addAttribute("alertMsg", alertMsg);
//			
//			return "common/redirect";
//		}
//		
//		model.addAttribute("redirectUrl", "../home/main");
//		model.addAttribute("alertMsg", alertMsg);
//		
//		return "common/redirect";
	}
	
	
	// 입력받는 뷰창 만들까?
	@RequestMapping("/collectPeriod")
	public void collectTM2DataPeriod() throws ParseException {
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");

		Date startDate = dateFormatter.parse("20190101");
		Date endDate = new Date();
		
		List<Map<String, Object>> separatedPeriodList = getSeparationPeriod(startDate,endDate);
		
		for(Map<String, Object> period:separatedPeriodList) {
			
			String startUrl = (String)period.get("start");
			String endUrl = (String)period.get("end");
			
			tm2Serivce.collectSanggaData(startUrl,endUrl);
			
//			Map<String, Object> rs = tm2Serivce.collectSanggaData(startUrl,endUrl);
//			
//			List<String> result = (List)rs.get("wrongRequest");
//			String alertMsg = (String)rs.get("alertMsg");
//			
//			System.out.println("==wrong tm2 URL request==");
//			System.out.println(result);
//			
//			Log.info(alertMsg);
		}
		
//		model.addAttribute("redirectUrl", "../home/main");
//		model.addAttribute("alertMsg", "console 창에서 수집결과를 확인하세요");
//		
//		return "common/redirect";
	}

	private List<Map<String,Object>> getSeparationPeriod(Date startDate, Date endDate) {
		
		List<Map<String, Object>> separatedPeriodList = new ArrayList<>();
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");

		Date pointDate = startDate;
		
		Calendar cal = Calendar.getInstance();
		
		// 중간 날짜가 마지막 날짜보다 빠르고, 앞의 달일때만 진행 (같은 달은 따로)
		while ( pointDate.compareTo(endDate) < 0 && pointDate.getMonth() < endDate.getMonth() )  {
			
			Map<String,Object> separatedPeriod = new HashMap<>();
			
			String start = dateFormatter.format(pointDate);
			separatedPeriod.put("start", start);

			cal.setTime(pointDate);
			cal.add(Calendar.MONTH, 1);
			cal.add(Calendar.DATE, -1);
			pointDate = cal.getTime();

			String end = dateFormatter.format(pointDate);
			separatedPeriod.put("end", end);
			
			cal.add(Calendar.DATE, 1);
			pointDate = cal.getTime();
			
			separatedPeriodList.add(separatedPeriod);
		}

		
		if (pointDate.compareTo(endDate) < 1) {
			Map<String,Object> separatedPeriod = new HashMap<>();
			
			String start = dateFormatter.format(pointDate);
			separatedPeriod.put("start", start);
			
			String end = dateFormatter.format(endDate);
			separatedPeriod.put("end", end);

			separatedPeriodList.add(separatedPeriod);
		}
		
		return separatedPeriodList;
	}
	
	
	
	
	

	/*
	// tm2 실사이트로 조회 요청하는 뷰
	@RequestMapping("/select")
	public String showSelect() {
		return "keywordManager/select";
	}
	
	// 데이터 exclude 저장
	@RequestMapping("/saveExclude")
	@ResponseBody
	public String saveExclude(int keywordId, String sns, String exclude) {
		
		sns = sns.trim().substring(0,1).toUpperCase();
		
		tm2Serivce.saveExclude(keywordId, sns, exclude);
		
		return sns + ": " +  exclude + " 저장";
	}
	
	// tm2 실사이트 조회 결과 가져옴 (exclude없이) -> exclude 찾기 위한 시스템
	@RequestMapping("/printAllKeywordPost")
	@ResponseBody
	public List<Map<String,Object>> test(String keywordId) {
		
        int id = Integer.parseInt(keywordId);
        
        String include = tm2Serivce.getInclude(id);
        String keyword = tm2Serivce.getKeyword(id);
        

		System.out.println(keyword + " 데이터 tm2 조회");
        
        System.out.println(include);
          
        
        if (keywordId == null) {
        	return null;
        }
        
        String[] sources = {"blog","insta","news","twitter"};

		List<Map<String,Object>> data = new ArrayList<>();
        int rowPerPage = 20; 

        for (String source:sources) {

	        int pageNum = 1;     
	        
			// url 셋팅
			String tm2Url = setUrlOfKeywordDocuments(keyword,include,source,rowPerPage,pageNum);
			System.out.println(tm2Url);
			// tm2 API 조회
			String dataStr = readURL(tm2Url);
			
			System.out.println(dataStr);
	
			if (dataStr == null) {
				return null;
			}
			
			// 데이터 
			data.addAll(getDocumentJson(keyword,source,dataStr));
			
			
			// 다음 페이지 데이터
	        Long totalCnt = getTotalCnt(keyword,source,dataStr);
	
	        while(totalCnt > pageNum*rowPerPage) {
	        	pageNum++;
	        	
	        	// url 셋팅
	    		tm2Url = setUrlOfKeywordDocuments(keyword,include,source,rowPerPage,pageNum);
	    		// tm2 API 조회
	    		dataStr = readURL(tm2Url);
	    		// 데이터 
	    		data.addAll(getDocumentJson(keyword,source,dataStr));
	    		
	    		
	        }
        }
				
		return data;
	}
	
	@RequestMapping("/getTotalCnt")
	private Long getTotalCnt(String keyword, String source, String dataStr) {
		
		List<Map<String,Object>> JsonData = new ArrayList<>();
		
		String sourceType = (source).trim().substring(0,1).toUpperCase();
		
		Long totalCnt = null;
		
		try{
			
			// Json parser를 만들어 만들어진 문자열 데이터를 객체화 합니다. 
			JSONParser parser = new JSONParser(); 
			JSONObject obj = (JSONObject) parser.parse(dataStr); 

			totalCnt = (Long)obj.get("totalCnt");
			
		}catch(Exception e){ 
			System.out.println(e.getMessage()); 
			
		}
		
		return totalCnt;
	}
	
	
	
	private List<Map<String,Object>> getDocumentJson(String keyword, String source, String dataStr) {
		
		List<Map<String,Object>> JsonData = new ArrayList<>();
		
		String sourceType = (source).trim().substring(0,1).toUpperCase();
		
		
		try{
			
			// Json parser를 만들어 만들어진 문자열 데이터를 객체화 합니다. 
			JSONParser parser = new JSONParser(); 
			JSONObject obj = (JSONObject) parser.parse(dataStr); 

			JSONArray documentList = (JSONArray) obj.get("documentList"); 
			
			JSONObject document; 
			String title = ""; 
			String content = ""; 
			String url = "";
			String writerName = "";
			
			// parse_item은 배열형태이기 때문에 하나씩 데이터를 하나씩 가져올때 사용합니다. 
			// 필요한 데이터만 가져오려고합니다. 
			for(int i = 0 ; i < documentList.size(); i++) { 
				document = (JSONObject) documentList.get(i); 
				
				title = (String)document.get("title");
				content = (String)document.get("content");
				url = (String)document.get("url");
				writerName = (String)document.get("writerName");
				content = content.replaceAll("\n","</br>");
				JsonData.add(Maps.of("title",title,"content",content,"url",url,"writerName",writerName));
				
			} 
			
		}catch(Exception e){ 
			System.out.println(e.getMessage()); 
		}
		
		return JsonData;
	}
	
	private String setUrlOfKeywordDocuments(String keyword, String include, String source, int rowPerPage, int pageNum) {
		
		// period >> 0 : 일별, 1 : 주별,2 : 월별, 3 : 분기,4 : 반기, 5 : 연간
		 
		String urlKeyword = "&keyword=(" + URLEncoder.encode(keyword) +")%26%26(" + URLEncoder.encode(include) + ")";
		String urlBasicTm2Condition = "lang=ko&command=GetKeywordDocuments";
		String urlSource = "&source=" + source; // insta, blog, twitter, news ...
		String urlStartDate = "&startDate=" + "20190101";
		String urlEndDate = "&endDate=" + "20191029";
		String urlRowPerPage = "&rowPerPage=" + rowPerPage;
		String urlPageNum = "&pageNum=" + pageNum;
		
		//String urlPeriod = "&period=" + "0";
		
		String keywordUrl = tm2Url + urlBasicTm2Condition
									+ urlKeyword
									+ urlSource
									+ urlStartDate
									+ urlEndDate
									+ urlRowPerPage
									+ urlPageNum;
		
		return keywordUrl;
	}
	*/
}
