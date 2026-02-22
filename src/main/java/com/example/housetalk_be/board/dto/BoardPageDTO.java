package com.example.housetalk_be.board.dto;


import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class BoardPageDTO {
    private List<BoardResponseDTO> content;
    private int totalPages;
    private long totalElements;

    public BoardPageDTO(Page<BoardResponseDTO> page) {
        this.content = page.getContent();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
    }
}