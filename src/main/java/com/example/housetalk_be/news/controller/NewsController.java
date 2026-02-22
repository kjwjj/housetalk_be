package com.example.housetalk_be.news.controller;

import com.example.housetalk_be.news.dto.NewsDto;
import com.example.housetalk_be.news.service.NewsService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
@CrossOrigin(origins = "http://localhost:5173")
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public List<NewsDto> getNews() {
        return newsService.getHousingNews();
    }
}
