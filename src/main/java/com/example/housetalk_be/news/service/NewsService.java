package com.example.housetalk_be.news.service;

import com.example.housetalk_be.news.dto.NewsDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.apache.commons.text.StringEscapeUtils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import java.text.SimpleDateFormat;
import java.util.*;


@Service
@RequiredArgsConstructor
// 뉴스 수집
public class NewsService {

    @Value("${naver.client.id}")
    private String naverClientId;

    @Value("${naver.client.secret}")
    private String naverClientSecret;

    @Value("${kakao.rest.key}")
    private String kakaoRestKey;

    // ------------------ 전체 뉴스 가져오기 ------------------
    public List<NewsDto> getHousingNews() {
        List<NewsDto> naverNews = getNaverNews();
        List<NewsDto> kakaoNews = getKakaoNews();

        // 네이버, 카카오 각각 최신순 정렬
        naverNews.sort(Comparator.comparing(this::parseNaverDate).reversed());
        kakaoNews.sort(Comparator.comparing(this::parseKakaoDate).reversed());

        // 합쳐서 전체 최신순 정렬
        List<NewsDto> allNews = new ArrayList<>();
        allNews.addAll(naverNews);
        allNews.addAll(kakaoNews);
        allNews.sort(Comparator.comparing(this::parseCombinedDate).reversed());

        return allNews;
    }

    // ------------------ 네이버 뉴스 ------------------
    private List<NewsDto> getNaverNews() {
        // 네이버 API query (부동산 관련 키워드 OR 조건)
        String query = "부동산  주택시장  분양  입주물량  전세  월세  청약  주택공급";
        String url = "https://openapi.naver.com/v1/search/news.json?query=" + query + "&display=20&sort=date";

        // HTTP 헤더 설정 (네이버 API 인증)
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", naverClientId);
        headers.set("X-Naver-Client-Secret", naverClientSecret);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            // 네이버 뉴스 API 호출
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            // JSON 파싱 후 NewsDto 리스트 반환
            return parseNaverNews(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList(); // 실패 시 빈 리스트 반환
        }
    }

    // ------------------ 네이버 뉴스 파싱 ------------------
    private List<NewsDto> parseNaverNews(String json) {
        List<NewsDto> newsList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        int idx = 0;

        try {
            // JSON "items" 배열 추출
            JsonNode items = mapper.readTree(json).get("items");
            for (JsonNode item : items) {
                // HTML 태그 제거
                String title = clean(item.get("title").asText());
                String description = clean(item.get("description").asText());

                // 부동산 관련 뉴스 필터링 (현재 사용 중)
                if (!isHousingRelated(title + description)) continue;

                // NewsDto 객체 생성
                newsList.add(
                        NewsDto.builder()
                                .id("네이버-" + System.currentTimeMillis() + "-" + (idx++)) // 고유 ID
                                .source("네이버") // 출처
                                .title(title)
                                .summary(description)
                                .date(item.get("pubDate").asText()) // 원본 날짜 문자열
                                .link(item.get("link").asText())
                                .color("success") // 프론트용 배지 색상
                                .build()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newsList; // 파싱된 뉴스 리스트 반환
    }


    // ------------------ 네이버 뉴스 날짜 파싱 ------------------
    private Date parseNaverDate(NewsDto news) {
        try {
            // "Wed, 20 Dec 2023 12:34:56 +0900" 형식 파싱
            return new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
                    .parse(news.getDate());
        } catch (Exception e) {
            return new Date(0); // 실패 시 Epoch 반환
        }
    }


    // ------------------ 카카오 뉴스 ------------------
    private List<NewsDto> getKakaoNews() {
        // 카카오 검색 query (부동산 관련 키워드)
        String query = "부동산 주택시장 분양 입주물량 전세 월세 청약 주택공급";
        String url = "https://dapi.kakao.com/v2/search/web?query=" + query + "&sort=recency&size=20";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoRestKey); // 인증 헤더

        HttpEntity<Void> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            // JSON 파싱 후 NewsDto 리스트 반환
            return parseKakaoNews(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList(); // 실패 시 빈 리스트 반환
        }
    }

    // ------------------ 카카오 뉴스 파싱 ------------------
    private List<NewsDto> parseKakaoNews(String json) {
        List<NewsDto> newsList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        int idx = 0;

        try {
            // JSON "documents" 배열 추출
            JsonNode documents = mapper.readTree(json).get("documents");
            for (JsonNode doc : documents) {
                String title = clean(doc.get("title").asText());
                String description = clean(doc.get("contents").asText());
                String url = doc.get("url").asText();

                // 부동산 관련 뉴스 필터링
//                if (!isHousingRelated(title + description)) continue;

                // 신뢰할 수 있는 뉴스 도메인 체크
//                if (!isNewsDomain(url)) continue;

                // NewsDto 객체 생성
                newsList.add(
                        NewsDto.builder()
                                .id("카카오-" + System.currentTimeMillis() + "-" + (idx++))
                                .source("카카오")
                                .title(title)
                                .summary(description)
                                .date(doc.get("datetime").asText()) // ISO 8601 날짜
                                .link(doc.get("url").asText())
                                .color("danger")
                                .build()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newsList;
    }

    // ------------------ 카카오 뉴스 날짜 파싱 ------------------
    private Date parseKakaoDate(NewsDto news) {
        try {
            // ISO 8601 "2026-01-27T22:37:00.000+09:00" 형식
            OffsetDateTime odt = OffsetDateTime.parse(news.getDate(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            return Date.from(odt.toInstant());
        } catch (Exception e) {
            e.printStackTrace();
            return new Date(0);
        }
    }

    // ------------------ 전체 뉴스 날짜 통합 파싱 ------------------
    private Date parseCombinedDate(NewsDto news) {
        try {
            if (news.getSource().equals("네이버")) return parseNaverDate(news);
            else return parseKakaoDate(news);
        } catch (Exception e) {
            return new Date(0);
        }
    }

    // ------------------ 유틸 메서드 ------------------

    // 뉴스 제목/내용이 부동산 관련 키워드를 포함하는지 체크
    // 사용: parseNaverNews, parseKakaoNews
    private boolean isHousingRelated(String text) {
        return text.contains("부동산")
                || text.contains("주택")
                || text.contains("분양")
                || text.contains("입주")
                || text.contains("입주물량")
                || text.contains("청약")
                || text.contains("공급")
                || text.contains("거래량")
                || text.contains("집값")
                || text.contains("아파트");
    }

    // 뉴스 URL이 신뢰할 수 있는 언론사인지 체크
    // 현재: 미사용, 필요시 parseKakaoNews에서 적용 가능
    private boolean isNewsDomain(String url) {
        return url.contains("news.naver.com")
                || url.contains("v.daum.net")
                || url.contains("mk.co.kr")
                || url.contains("chosun.com")
                || url.contains("joongang.co.kr")
                || url.contains("hani.co.kr")
                || url.contains("sedaily.com");
    }

    // HTML 태그 제거
    // 사용: parseNaverNews, parseKakaoNews
    private String clean(String text) {
        if (text == null) return "";
        // 1. HTML 태그 제거
        String noHtml = text.replaceAll("<[^>]*>", "");
        // 2. HTML 엔티티 변환
        return StringEscapeUtils.unescapeHtml4(noHtml);
    }
}
