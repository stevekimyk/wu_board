package com.wu.board.service;

import com.wu.board.dto.PostcodeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PostcodeService {

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Value("${postcode.api.key:}")
    private String apiKey;

    private RestTemplate restTemplate;

    private static final String URL = "https://www.juso.go.kr/addrlink/addrLinkApi.do";

    @PostConstruct
    public void init() {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }

    @SuppressWarnings("unchecked")
    public List<PostcodeDto> search(String keyword) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("postcode.api.key 가 설정되지 않았습니다.");
            return new ArrayList<>();
        }
        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(URL)
                    .queryParam("currentPage",  1)
                    .queryParam("countPerPage", 10)
                    .queryParam("keyword",      keyword)
                    .queryParam("confmKey",     apiKey)
                    .queryParam("resultType",   "json")
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUri();

            Map<String, Object> res = restTemplate.getForObject(uri, Map.class);
            if (res == null) return new ArrayList<>();

            Map<String, Object> results = (Map<String, Object>) res.get("results");
            List<Map<String, Object>> jusoList = (List<Map<String, Object>>) results.get("juso");
            if (jusoList == null) return new ArrayList<>();

            List<PostcodeDto> list = new ArrayList<>();
            for (Map<String, Object> juso : jusoList) {
                list.add(new PostcodeDto(
                        (String) juso.get("zipNo"),
                        (String) juso.get("roadAddr"),
                        (String) juso.get("jibunAddr"),
                        (String) juso.get("siNm"),
                        (String) juso.get("sggNm"),
                        (String) juso.get("emdNm")
                ));
            }
            return list;

        } catch (Exception e) {
            log.warn("우편번호 조회 실패: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}
