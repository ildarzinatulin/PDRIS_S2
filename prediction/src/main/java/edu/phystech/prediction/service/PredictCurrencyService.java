package edu.phystech.prediction.service;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import edu.phystech.prediction.domain.Currency;
import edu.phystech.prediction.domain.Weather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class PredictCurrencyService {
    private final EurekaClient discoveryClient;
    private final RestTemplate restTemplate;
    private final SimpleRegression regression;

    private static final String WEATHER_SERVICE_NAME = "WEATHER";
    private static final String CURRENCY_SERVICE_NAME = "CURRENCY";
    private static final int NUMBER_PREVIOUS_DAYS_FOR_FIT = 7;

    @Autowired
    public PredictCurrencyService(@Qualifier("eurekaClient") EurekaClient discoveryClient, RestTemplate restTemplate) {
        this.discoveryClient = discoveryClient;
        this.restTemplate = restTemplate;
        regression = new SimpleRegression();
    }

    public void fit() {
        List<Weather> weathers = getWeatherForLastDays(NUMBER_PREVIOUS_DAYS_FOR_FIT);
        List<Currency> currencies = getDollarCurrencyForLastDays(NUMBER_PREVIOUS_DAYS_FOR_FIT);
        fit(weathers, currencies);
    }

    public void fit(List<Weather> weathers, List<Currency> currencies) {
        IntStream.range(0, Math.min(currencies.size(), weathers.size()))
                .forEach(i -> {
                    regression.addData(weathers.get(i).getAvgTemperature(), currencies.get(i).getValue());
                });
    }

    public double predict(Weather weather) {
        return regression.predict(weather.getMaxTemperature());
    }

    public double predict() throws JsonProcessingException {
        Weather weather = getWeatherPrediction();
        return regression.predict(weather.getMaxTemperature());
    }

    private List<Weather> getWeatherForLastDays(int days) {
        InstanceInfo instance = discoveryClient.getNextServerFromEureka(WEATHER_SERVICE_NAME, false);
        String requestUrl = UriComponentsBuilder
                .fromUri(URI.create(instance.getHomePageUrl()))
                .queryParam("days", days)
                .toUriString();

        ResponseEntity<Weather[]> response = restTemplate.getForEntity(requestUrl, Weather[].class);
        return List.of(Objects.requireNonNull(response.getBody(), "Response from weather service is null"));
    }

    private List<Currency> getDollarCurrencyForLastDays(int days) {
        InstanceInfo instance = discoveryClient.getNextServerFromEureka(CURRENCY_SERVICE_NAME, false);
        String requestUrl = UriComponentsBuilder
                .fromUri(URI.create(instance.getHomePageUrl()))
                .queryParam("days", days)
                .toUriString();

        System.out.println("requestUrl: " + requestUrl);
        System.out.println("homePageUri: " + instance.getHomePageUrl());

        ResponseEntity<Currency[]> response = restTemplate.getForEntity(requestUrl, Currency[].class);
        return List.of(Objects.requireNonNull(response.getBody(), "Response from currency service is null"));
    }

    private Weather getWeatherPrediction() {
        Weather weather = getWeatherForLastDays(1).get(0);
        return Objects.requireNonNull(weather, "Response from weather service is empty");
    }

}
