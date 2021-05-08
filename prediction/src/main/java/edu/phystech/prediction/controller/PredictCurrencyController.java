package edu.phystech.prediction.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.phystech.prediction.service.PredictCurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PredictCurrencyController {
    private final PredictCurrencyService predictCurrencyService;

    @Autowired
    public PredictCurrencyController(PredictCurrencyService predictCurrencyService) {
        this.predictCurrencyService = predictCurrencyService;
    }

    @GetMapping("/")
    public double getWeatherForLastDays() throws JsonProcessingException {
        predictCurrencyService.fit();
        return predictCurrencyService.predict();
    }
}
