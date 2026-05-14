package com.wu.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    private Long   id;
    private String boardType;   // notice / free / qna / gallery
    private String title;
    private String writer;
    private String content;
    private String regDate;
    private int    viewCount;
    private int    commentCount;
    private boolean isNew;
}
