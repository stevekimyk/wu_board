package com.wu.board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WuBoardApplication {

    //스프링으로 만든 웹서버를 (문)열어주세요 라는 뜻
    public static void main(String[] args) {
        SpringApplication.run(WuBoardApplication.class, args);
    }
}
