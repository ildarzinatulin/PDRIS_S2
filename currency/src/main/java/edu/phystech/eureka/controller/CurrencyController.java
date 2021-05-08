package edu.phystech.eureka.controller;

import java.util.List;

import edu.phystech.eureka.domain.Currency;
import edu.phystech.eureka.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrencyController {
    private final CurrencyService currencyService;

    @Autowired
    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/")
    public List<Currency> getWeatherForLastDays(@RequestParam(required = false, defaultValue = "1") Integer days) {
        return currencyService.getDollarCurrencyForLastDays(days);
    }
}
