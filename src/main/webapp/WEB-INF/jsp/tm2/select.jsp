<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>

<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<link rel="stylesheet" href="/css/lib/lib.css">

<script>
	function saveExclude() {
		var keywordId = $('#keywordId').val();
		var sns = $('#sns').val().trim();
		var exclude = $('#exclude').val().trim();

		$.post(
			'/keywordManager/saveExclude',
			{
				"keywordId":keywordId,
				"sns":sns,
				"exclude":exclude
			},
			function(data) {
				alert(data);

			},
			"html"
		).fail(function() {
		    alert("저장 실패");
		});
	}
	
	function selectPost() {
		var keywordId = $('#keywordId').val();
// 		var source = $('#source').val().trim();

		$.post(
			'/keywordManager/printAllKeywordPost',
			{
				"keywordId":keywordId
// 				,
// 				"source":source
			},
			function(data) {
				alert("조회");

// 				alert(data.length);

				$('#post-box').empty();
				$('#post-box').append('<h2> total: ' + data.length + '</h2>');
				
				$.each(data,function(key,list) {
					var title = "";
					var url = "";
					var writerName = "";
					var content = "";


					$.each(list,function(key,value) {

						if (key == "title") {
							title += value;
						} else if (key == "url") {
							url += value;
						} else if (key == "writerName") {
							writerName += value;
						} else {
							content += value;
						}

					});
					$('#post-box').append(
							`<div class="post">
								<h3>` + title + `</h3>
								<div>작성자: ` + writerName + `</div> 
								<div>출처: <a href="` + url + `" target="_blank">` + url + `</a></div>
								<div> ` + content + `</div> 
							</div>`);
				});
			},
			"json"
		).fail(function(jqXHR) {
		    alert("null");
		});
		
	}

</script>

<body>
	<div style="position:fixed; background-color:white; height:50px;">
		<section class="row">
			<div class="cell">
				<label for="keyword">키워드 ID:</label>
				<input id="keywordId" type="text" name="keywordId">
				<!-- <label for="source">매체:</label> -->
				<!-- <input id="source" type="text" name="source">  -->
				
				<button onclick="selectPost();">조회</button>
			</div>
	
			<div class="cell-right">			
				<label for=sns>source :</label>
				<input id="sns" type="text" name="sns">
				
				<label for="exclude">exclude :</label>
				<input id="exclude" type="text" name="exclude" style="width:300px;">
		
				<button onclick="saveExclude();">저장</button>
			</div>
		</section>
	</div>

	<div id="post-box" style="padding-top:50px;">
	
	</div>

</body>
</html>