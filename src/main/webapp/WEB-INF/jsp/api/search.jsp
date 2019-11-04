<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ include file="../part/header.jspf"%>

<style>
	#keywordMonthlyChart {
	  width: 100%;
	  height: 500px;
	}
	
	#keywordTotalChart {
	  width: 100%;
	  height:250px;
	}
	
	#totalCompareChart {
	  width: 100%;
	  height:250px;
	}
	
	.data-box > .data {
		margin:50px auto;
		width:50%;
		min-height:300px;
	}
	
	.search-info {
		margin:50px auto;
	}
	
	#keyword {
		background-color:#003366;
		color:white;
		font-size:20px;
		font-weight:bold;
		padding:5px 20px;
		border-radius:20px;
	}

</style>

<!-- Chart code -->
<script>
	am4core.ready(function() {
		getKeywordMonthlyData();
		getKeywordTotalData();
		getTotalCompareData();
	
	});
	
	function getKeywordMonthlyData() {

		if ('${multipleCategory}' == 'true') {

		}
		
		$.post(
			"/api/get/json/keywordMonthly",
			{"keyword":'${keyword}'},
			function(data) {
				drawKeywordMonthlyChart(data);
			},
			"json"
		).fail(function() {
			alert('키워드 월별 긍부정 수치를 가져오지 못했습니다');
		});

	}

	function getKeywordTotalData() {
		
		$.post(
			"/api/get/json/keywordTotal",
			{"keyword":'${keyword}'},
			function(data) {
				drawKeywordTotalChart(data);
			},
			"json"
		).fail(function() {
			alert('키워드 통합 긍부정 수치를 가져오지 못했습니다');
		});

	}

	function getTotalCompareData() {
		
		$.post(
			"/api/get/json/totalCompare",
			{"keyword":'${keyword}'},
			function(data) {
				drawTotalCompareChart(data);
			},
			"json"
		).fail(function() {
			alert('세종 평균 긍부정 수치 비교를 가져오지 못했습니다');
		});

	}

	function drawKeywordMonthlyChart(jsonData) {
		// Themes begin
		am4core.useTheme(am4themes_material);
		am4core.useTheme(am4themes_animated);
		// Themes end

		// Create chart instance
		var chart = am4core.create("keywordMonthlyChart", am4charts.XYChart);
		chart.paddingRight = 20;

		// Add data
		chart.data = jsonData;
		//month, score
//	 		[{
//	 		  "month": "1950",
//	 		  "score": -0.307
//	 		}, {
//	 		  "month": "1951",
//	 		  "score": -0.168
//	 		}, {
//	 		  "month": "1952",
//	 		  "score": -0.073
//	 		}];

		// Create axes
		var categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
		categoryAxis.dataFields.category = "month";
		categoryAxis.renderer.minGridDistance = 50;
		categoryAxis.renderer.grid.template.location = 0.5;
		categoryAxis.startLocation = 0.5;
		categoryAxis.endLocation = 0.5;

		// Pre zoom
		chart.events.on("datavalidated", function () {
		  categoryAxis.zoomToIndexes(Math.round(chart.data.length * 0.4), Math.round(chart.data.length * 0.55));
		});

		// Create value axis
		var valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
		valueAxis.baseValue = 50;

		// Create series
		var series = chart.series.push(new am4charts.LineSeries());
		series.dataFields.valueY = "score";
		series.dataFields.categoryX = "month";
		series.strokeWidth = 5;
		series.tensionX = 0.77;

		var range = valueAxis.createSeriesRange(series);
		range.value = 49.5;
		range.endValue = 101;
		range.contents.stroke = am4core.color("skyblue");
		range.contents.fill = range.contents.stroke;

		// Add scrollbar
		var scrollbarX = new am4charts.XYChartScrollbar();
		scrollbarX.series.push(series);
		chart.scrollbarX = scrollbarX;

		chart.cursor = new am4charts.XYCursor();
	}

	function drawKeywordTotalChart(jsonData) {
		// Themes begin
// 		am4core.useTheme(am4themes_animated);
// 		am4core.useTheme(am4themes_dataviz); 
		// Themes end

		var chart = am4core.create("keywordTotalChart", am4charts.PieChart);
		chart.hiddenState.properties.opacity = 0; // this creates initial fade-in
		
		chart.data = jsonData
		//positive, negative
		
		chart.radius = am4core.percent(70);
		chart.innerRadius = am4core.percent(40);
		chart.startAngle = 180;
		chart.endAngle = 360;  

		var series = chart.series.push(new am4charts.PieSeries());
		series.dataFields.value = "count";
		series.dataFields.category = "sentiment";

		series.slices.template.cornerRadius = 10;
		series.slices.template.innerCornerRadius = 7;
		series.slices.template.draggable = true;
		series.slices.template.inert = true;
		series.alignLabels = false;

		series.hiddenState.properties.startAngle = 90;
		series.hiddenState.properties.endAngle = 90;

		chart.legend = new am4charts.Legend();
	}
	
	function drawTotalCompareChart(jsonData) {
		// Themes begin
		am4core.useTheme(am4themes_animated);
		// Themes end

		// Create chart instance
		var chart = am4core.create("totalCompareChart", am4charts.XYChart);
		chart.scrollbarX = new am4core.Scrollbar();

		// Add data
		chart.data = jsonData;

		// Create axes
		var categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
		categoryAxis.dataFields.category = "keyword";
		categoryAxis.renderer.grid.template.location = 0;
		categoryAxis.renderer.minGridDistance = 30;
		categoryAxis.renderer.labels.template.horizontalCenter = "right";
		categoryAxis.renderer.labels.template.verticalCenter = "middle";
		categoryAxis.tooltip.disabled = true;
		categoryAxis.renderer.minHeight = 0;

		var valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
		valueAxis.renderer.minWidth = 50;

		// Create series
		var series = chart.series.push(new am4charts.ColumnSeries());
		series.sequencedInterpolation = true;
		series.dataFields.valueY = "score";
		series.dataFields.categoryX = "keyword";
		series.tooltipText = "[{categoryX}: bold]{valueY}[/]";
		series.columns.template.strokeWidth = 0;

		series.tooltip.pointerOrientation = "vertical";

		series.columns.template.column.cornerRadiusTopLeft = 10;
		series.columns.template.column.cornerRadiusTopRight = 10;
		series.columns.template.column.fillOpacity = 0.8;

		// on hover, make corner radiuses bigger
		var hoverState = series.columns.template.column.states.create("hover");
		hoverState.properties.cornerRadiusTopLeft = 0;
		hoverState.properties.cornerRadiusTopRight = 0;
		hoverState.properties.fillOpacity = 1;

		series.columns.template.adapter.add("fill", function(fill, target) {
		  return chart.colors.getIndex(target.dataItem.index);
		});

		// Cursor
		chart.cursor = new am4charts.XYCursor();

	}
</script>

	<section class="search-info">
		<sapn id="keyword">${keyword}(${keywordInfo.sentimentCount})</sapn>
		<p>연관감성어: ${keywordInfo.sentimentCount}개</p>
		<p>${keywordInfo.address} | ${keywordInfo.hdName}</p>
		<p>${keywordInfo.category}</p>
	</section>
	
	<section id="keyword-chart" class="chart-section">
		
		<div class="row">
			<div class="half-section cell">
				<h1>'${keyword}' 긍부정</h1>
				<div id="keywordTotalChart">
				</div>
			</div>
			
			<div class="half-section cell-right">
				<h1>세종시 업체 평균과 긍부정 지수 비교</h1>
				<div id="totalCompareChart">
				</div>
			</div>
		</div>
		
		<div class="whole-section">
			
			<h1>월별 '${keyword}' 긍부정 추이</h1>
		
			<div id="keywordMonthlyChart"></div>
		</div>
		
	</section>


<%@ include file="../part/footer.jspf"%>