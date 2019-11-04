DROP DATABASE IF EXISTS ds8;

CREATE DATABASE ds8;

USE ds8;

# drop table sangga;
CREATE TABLE sangga(
	id INT(10) PRIMARY KEY,
	place_name VARCHAR(100) NOT NULL,
	category1 VARCHAR(20) NOT NULL,	
	category1_name VARCHAR(100) NOT NULL,	
	category2 VARCHAR(20) NOT NULL,	
	category2_name VARCHAR(100) NOT NULL,	
	category3 VARCHAR(20) NOT NULL,	
	category3_name VARCHAR(100) NOT NULL,	
	address_refined	VARCHAR(4000) NOT NULL,
	hd_emd_name VARCHAR(4000) NOT NULL,	
	hd_emd_code VARCHAR(20) NOT NULL,
	nice_category1_code VARCHAR(20),
	UNIQUE KEY(place_name, category1, category2, category3, address_refined, hd_emd_code) 
); 

# Excel to DB
-- LOAD DATA LOCAL INFILE 'C:\\work\\daumsoft-probation\\T8_1104\\자료\\01.소상공인 업체 마스터\\2019_N07_encoding.csv' 
-- INTO TABLE sangga FIELDS TERMINATED BY ','
-- (id, place_name, category1, category1_name, category2, category2_name, category3, category3_name
-- 	, address_refined, hd_emd_name, hd_emd_code, nice_category1_code); 
-- delete from sangga
-- where id = 0;
-- UPDATE sangga
-- SET nice_category1_code = LEFT(nice_category1_code,3);

SELECT *, CONCAT('"',nice_category1_code,'"')
FROM sangga;


--

# drop table category;
CREATE TABLE category(	
	id INT(10) AUTO_INCREMENT PRIMARY KEY,
	category_code VARCHAR(20) UNIQUE KEY,		
	category_name VARCHAR(100) NOT NULL,	
	upper_category_code VARCHAR(20) NOT NULL,		
	category_level INT(10) NOT NULL,		
	order_numeric INT(10) NOT NULL,		
	nice_category1_code VARCHAR(20) NOT NULL,	
	nice_category1_name VARCHAR(100) NOT NULL
); 

# Excel to DB
-- LOAD DATA LOCAL INFILE 'C:\\work\\daumsoft-probation\\T8_1104\\자료\\02.카테고리 마스터\\category_master_utf8.csv' 
-- INTO TABLE category CHARACTER SET utf8 FIELDS TERMINATED BY ',' 
-- (category_code, category_name, upper_category_code, category_level, order_numeric, nice_category1_code, nice_category1_name);
-- delete from category
-- where id = 1;

SELECT *
FROM category;

--

# drop table sangga_keyword_master;
CREATE TABLE sangga_keyword_master(	
	id INT(10) AUTO_INCREMENT PRIMARY KEY,
	sns_type VARCHAR(1) NOT NULL,		
	keyword VARCHAR(100) NOT NULL,	
	keyword_id INT(10) NOT NULL,
	include VARCHAR(4000),		
	`exclude` VARCHAR(4000)
); 

SELECT COUNT(*)
FROM sangga_keyword_master;

SELECT *
FROM sangga_keyword_master

SELECT *
FROM sangga
WHERE place_name = '경희대석사태권도' AND address_refined = '세종특별자치시 달빛1로 201';

SELECT *
FROM sangga
WHERE place_name = '경희대석사태권도';

SELECT DISTINCT(address_refined)
FROM sangga
WHERE place_name = '경희대석사태권도'


SELECT sns_type, 
	keyword, 
	hd_emd_name, 
	nice_category1_name, 
	positive_count,
	negative_count,
	neutral_count,
	ROUND(AVG(sentiment_score),2) AS `score`,
	DATE_FORMAT(register_date, '%m') AS `month`
FROM sangga_sentiment_analysis
WHERE keyword = '경희대석사태권도' AND keyword_id IN (SELECT id
			FROM sangga
			WHERE place_name = '경희대석사태권도' AND address_refined = '세종특별자치시 달빛1로 201')
GROUP BY `month`

SELECT *
FROM sangga_sentiment_analysis
WHERE keyword = '경희대석사태권도' 

SELECT *
FROM sangga_sentiment_analysis
WHERE keyword = '경희대석사태권도' AND keyword_id IN (SELECT id
			FROM sangga
			WHERE place_name = '경희대석사태권도' AND address_refined = '세종특별자치시 달빛1로 201')

SELECT sns_type, 
	keyword, 
	hd_emd_name, 
	nice_category1_name, 
	positive_count,
	negative_count,
	neutral_count,
	ROUND(AVG(sentiment_score),2) AS `score`,
	DATE_FORMAT(register_date, '%m') AS `month`
FROM sangga_sentiment_analysis
WHERE keyword = '경희대석사태권도' 
GROUP BY `month`


UPDATE sangga_keyword_master
SET include = '(학원||우리들 학원||공부방||과외)&&(세종||세종시||새롬동||한누리대로)'
WHERE keyword = '우리들';

# Excel to DB
-- LOAD DATA LOCAL INFILE 'C:\\work\\daumsoft-probation\\T8_1104\\자료\\N06_keyword_master_encoding.csv' 
-- INTO TABLE sangga_keyword_master CHARACTER SET utf8 FIELDS TERMINATED BY ',' 
-- (sns_type, keyword, keyword_id, include, `exclude`);
-- delete from sangga_keyword_master
-- where id = 1534


--
# drop table sangga_keyword_master_tmp;
CREATE TABLE sangga_keyword_master_tmp(	
	id INT(10) AUTO_INCREMENT PRIMARY KEY,
	sns_type VARCHAR(1) NOT NULL,		
	keyword VARCHAR(100) NOT NULL,	
	keyword_id INT(10) NOT NULL,
	include VARCHAR(4000),		
	`exclude` VARCHAR(4000)
); 

-- LOAD DATA LOCAL INFILE 'C:\\work\\daumsoft-probation\\T8_1104\\자료\\N07_keyword_master_encoding.csv' 
-- INTO TABLE sangga_keyword_master_tmp CHARACTER SET utf8 FIELDS TERMINATED BY ',' 
-- (keyword_id,sns_type, keyword, include, `exclude`);
-- LOAD DATA LOCAL INFILE 'C:\\work\\daumsoft-probation\\T8_1104\\자료\\N03_keyword_master_encoding.csv' 
-- INTO TABLE sangga_keyword_master_tmp CHARACTER SET utf8 FIELDS TERMINATED BY ',' 
-- (sns_type, keyword, keyword_id, include, `exclude`);
-- DELETE FROM sangga_keyword_master_tmp
-- WHERE id = 1023


SELECT *
FROM sangga_keyword_master_tmp
WHERE `keyword` LIKE CONCAT('%','keyword','%');

UPDATE sangga_keyword_master AS S INNER JOIN sangga_keyword_master_tmp AS T
ON S.sns_type = T.sns_type AND S.keyword_id = T.keyword_id
SET S.include = T.include,
S.exclude = T.exclude;

DROP TABLE sangga_keyword_master_tmp;

--

# drop table sangga_sentiment_analysis;
CREATE TABLE sangga_sentiment_analysis(	
	id INT(10) AUTO_INCREMENT NOT NULL PRIMARY KEY,
	sns_type VARCHAR(1) NOT NULL,	
	keyword_id INT(10) NOT NULL,	
	keyword VARCHAR(100) NOT NULL,	
	hd_emd_name VARCHAR(4000) NOT NULL,	
	hd_emd_code INT(20) NOT NULL,		
	nice_category1_code VARCHAR(20) NOT NULL,		
	nice_category1_name VARCHAR(100) NOT NULL,		
	positive_count INT(10) NOT NULL,	
	negative_count INT(10) NOT NULL,		
	neutral_count INT(10) NOT NULL,
	sentiment_score	DOUBLE NOT NULL DEFAULT(0), 	
	register_date DATETIME NOT NULL,
	UNIQUE KEY(sns_type, keyword_id, register_date)
); 

SELECT *
FROM sangga_sentiment_analysis;

SELECT COUNT(*)
FROM sangga_sentiment_analysis;

--

SELECT DISTINCT(keyword), ROUND(AVG(sentiment_score),2) AS score, SUM(positive_count) + SUM(negative_count) + SUM(neutral_count) AS `count`
FROM sangga_sentiment_analysis
GROUP BY keyword_id
HAVING `count` > 30
ORDER BY score DESC, `count` DESC;


SELECT keyword, hd_emd_name, SUM(positive_count) + SUM(negative_count) + SUM(neutral_count) AS `count`
FROM sangga_sentiment_analysis
GROUP BY keyword_id
HAVING keyword = '우리들'
ORDER BY `count` DESC;



SELECT nice_category1_name, SUM(positive_count) AS positive, SUM(negative_count) AS negative
		FROM sangga_sentiment_analysis
		GROUP BY nice_category1_name

SELECT S.id, S.place_name, S.address_refined, S.hd_emd_name, 
			(SELECT nice_category1_name
			FROM category
			WHERE nice_category1_code = S.nice_category1_code
			LIMIT 1)  AS nice_category1_name,
			(SELECT SUM(positive_count) + SUM(negative_count) + SUM(neutral_count)
			FROM sangga_sentiment_analysis
			WHERE keyword = '아리솔태권도')AS sentiment_count
		FROM sangga AS S
		WHERE S.place_name = '아리솔태권도'


SELECT S.hd_emd_name, S.hd_emd_code, S.nice_category1_code AS nice_category1_code, 
	(SELECT nice_category1_name
	FROM category
	WHERE nice_category1_code = S.nice_category1_code
	LIMIT 1)
FROM sangga AS S
WHERE S.id = 1;
		

SELECT SUM(positive_count) AS positive,SUM(negative_count) AS negative
		FROM sangga_sentiment_analysis;


SELECT hd_emd_name, SUM(positive_count) AS positive, SUM(negative_count) AS negative
FROM sangga_sentiment_analysis
GROUP BY hd_emd_name;

SELECT hd_emd_name, ROUND(AVG(sentiment_score),2) AS score, DATE_FORMAT(register_date, '%m') AS `month`
FROM sangga_sentiment_analysis
WHERE hd_emd_name = '새롬동'
GROUP BY `month`;

SELECT SUM(positive_count) AS positive,SUM(negative_count) AS negative
FROM sangga_sentiment_analysis
WHERE keyword = '우리들';

SELECT sns_type, keyword, hd_emd_name, nice_category1_name, positive_count,negative_count,neutral_count,sentiment_score AS score,DATE_FORMAT(register_date, '%m') AS `month`
FROM sangga_sentiment_analysis
GROUP BY `month`;

SELECT sns_type, keyword, hd_emd_name, nice_category1_name, positive_count,negative_count,neutral_count,AVG(sentiment_score) AS score,DATE_FORMAT(register_date, '%m') AS `month`
FROM sangga_sentiment_analysis
WHERE keyword = '우리들'
GROUP BY `month`;

 INSERT INTO sangga_sentiment_analysis
(sns_type,keyword_id,keyword,hd_emd_name,hd_emd_code,nice_category1_code,nice_category1_name,
positive_count,negative_count,neutral_count,sentiment_score,register_date)
VALUES
('B',8805,'페르마수학학원','아름동','36110530','N07','학문/교육',
6,2,4,1'20191008')

SELECT sns_type, keyword, keyword_id, include, `exclude`
		FROM sangga_keyword_master 

SELECT hd_emd_name
FROM sangga
WHERE keyword_id = 8858


SELECT S.hd_emd_name, S.hd_emd_code, S.nice_category1_code, C.nice_category1_name
FROM sangga AS S
LEFT JOIN category AS C
ON S.nice_category1_code = C.nice_category1_code
WHERE S.id = 8858

SELECT S.hd_emd_name, S.hd_emd_code, S.nice_category1_code, C.nice_category1_name
		FROM sangga AS S
		LEFT JOIN category AS C
		ON S.nice_category1_code = C.nice_category1_code
		WHERE S.id = 8805
		LIMIT 1


CREATE TABLE test(
	id INT(10) NOT NULL,
	content VARCHAR(100) NOT NULL
);


CREATE TABLE test2(
	id INT(10) NOT NULL,
	content VARCHAR(100) NOT NULL
);

INSERT INTO test
VALUES (3, (SELECT content FROM test2 WHERE id = 1)), 
	(4, "hmm..")


SELECT *
FROM test;




SELECT *
FROM sangga_keyword_master
WHERE keyword = '대성학원';







