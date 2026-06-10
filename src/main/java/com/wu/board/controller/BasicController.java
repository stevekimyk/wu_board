package com.wu.board.controller;

import com.wu.board.service.PostcodeService;
import com.wu.board.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class BasicController {

    private final WeatherService  weatherService;
    private final PostcodeService postcodeService;

    @GetMapping("/")
    String index(@RequestParam(value = "postcode", required = false) String postcodeKeyword,
                 Model model) {
        model.addAttribute("activeMenu",       "home");
        model.addAttribute("weather",          weatherService.getCurrentWeather());
        model.addAttribute("postcodeKeyword",  postcodeKeyword);
        if (postcodeKeyword != null && !postcodeKeyword.isEmpty()) {
            model.addAttribute("postcodeList", postcodeService.search(postcodeKeyword));
        }
        return "index";
    }
}
