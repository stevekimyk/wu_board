package com.wu.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostcodeDto {
    private String zipNo;      // 우편번호
    private String roadAddr;   // 도로명주소
    private String jibunAddr;  // 지번주소
    private String siNm;       // 시도명
    private String sggNm;      // 시군구명
    private String emdNm;      // 읍면동명
}
