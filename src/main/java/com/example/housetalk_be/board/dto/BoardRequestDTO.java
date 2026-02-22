package com.example.housetalk_be.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardRequestDTO {
    private String title;
    private String content;
}