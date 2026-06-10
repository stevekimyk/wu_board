package com.wu.board.service;

import com.wu.board.dto.WeatherDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WeatherService {

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Value("${weather.api.key:}")
    private String apiKey;

    private RestTemplate restTemplate;

    // 익산시 좌표 (도시명 문자열 인코딩 문제 방지)
    private static final double LAT = 35.9439;
    private static final double LON = 126.9544;
    private static final String URL  =
            "https://api.openweathermap.org/data/2.5/weather" +
            "?lat={lat}&lon={lon}&appid={apiKey}&units=metric&lang=ko";

    @PostConstruct
    public void init() {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }

    @SuppressWarnings("unchecked")
    public WeatherDto getCurrentWeather() {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("weather.api.key 가 설정되지 않았습니다.");
            return null;
        }
        try {
            Map<String, Object> res = restTemplate.getForObject(URL, Map.class, LAT, LON, apiKey);
            if (res == null) return null;

            Map<String, Object> main    = (Map<String, Object>) res.get("main");
            Map<String, Object> wind    = (Map<String, Object>) res.get("wind");
            List<Map<String, Object>> weatherList = (List<Map<String, Object>>) res.get("weather");
            Map<String, Object> weather = weatherList.get(0);

            return new WeatherDto(
                    (String) res.get("name"),
                    (String) weather.get("description"),
                    ((Number) main.get("temp")).doubleValue(),
                    ((Number) main.get("feels_like")).doubleValue(),
                    ((Number) main.get("humidity")).intValue(),
                    ((Number) wind.get("speed")).doubleValue(),
                    (String) weather.get("icon")
            );
        } catch (Exception e) {
            log.warn("날씨 정보 조회 실패: {}", e.getMessage());
            return null;
        }
    }
}
