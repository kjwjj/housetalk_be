package com.example.housetalk_be.news.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 기존 뉴스
public class NewsDto {
    private String id;
    private String source;
    private String title;
    private String summary;
    private String date;
    private String link;
    private String color;
}
