package com.wu.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDto {
    private String city;
    private String description;
    private double temp;
    private double feelsLike;
    private int humidity;
    private double windSpeed;
    private String iconCode;
}
