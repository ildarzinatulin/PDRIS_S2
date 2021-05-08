package edu.phystech.weather.service;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.phystech.weather.domain.Weather;
import edu.phystech.weather.domain.WeatherId;
import edu.phystech.weather.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {
    @Value("${api.weather.key}")
    private String apiKey;
    private String urlForPrediction;
    private final DateFormat dateFormatDay = new SimpleDateFormat("yyyy-MM-dd");
    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;
    private final WeatherRepository weatherRepository;
    private final Logger logger = Logger.getLogger(Weather.class.getName());

    @Autowired
    public WeatherService(WeatherRepository weatherRepository, ObjectMapper mapper,
            RestTemplateBuilder restTemplateBuilder) {
        this.weatherRepository = weatherRepository;
        this.mapper = mapper;
        this.restTemplate = restTemplateBuilder.build();
    }

    @PostConstruct
    public void init() {
        urlForPrediction = "http://api.weatherapi.com/v1/forecast.json?key=" + apiKey + "&q=Moscow&days=2";
    }

    public Weather getWeatherPrediction() throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity(urlForPrediction, String.class);
        return getTomorrowWeatherByResponse(response);
    }

    public List<Weather> getWeatherForLastDays(int numberOfDays) {
        return getWeatherForLastDays(numberOfDays, "Moscow");
    }

    public List<Weather> getWeatherForLastDays(int numberOfDays, String city) {
        return IntStream.range(0, numberOfDays)
                .mapToObj(this::getInstantForDaysBefore)
                .map(i -> getWeather(i, city))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Weather getWeather(Instant date, String city) {
        Optional<Weather> weather = weatherRepository.findById(new WeatherId(createStringDateByInstant(date), city));
        if (weather.isPresent()) {
            return weather.get();
        }
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(createUrlForInstantAndCity(date, city),
                String.class);
        try {
            return getWeatherByResponse(response);
        } catch (JsonProcessingException e) {
            logger.log(Level.ALL, "Problem with parsing weather response: ", e);
            return null;
        }
    }

    private Instant getInstantForDaysBefore(int days) {
        return Instant.now().minus(days, ChronoUnit.DAYS);
    }

    private String createUrlForInstantAndCity(Instant date, String city) {
        return "http://api.weatherapi.com/v1/history.json?key=" + apiKey + "&q=" + city + "&dt=" +
                createStringDateByInstant(date);
    }

    private String createStringDateByInstant(Instant date) {
        return dateFormatDay.format(Date.from(date));
    }

    private Weather getWeatherByResponse(ResponseEntity<String> response) throws JsonProcessingException {
        JsonNode root = mapper.readTree(response.getBody());
        Weather weather = new Weather(
                root.findValue(ResponseKeys.DATE).asText(),
                root.findValue(ResponseKeys.NAME).asText(),
                root.findValue(ResponseKeys.MAXIMUM_TEMPERATURE).asDouble(),
                root.findValue(ResponseKeys.MINIMUM_TEMPERATURE).asDouble(),
                root.findValue(ResponseKeys.AVERAGE_TEMPERATURE).asDouble(),
                root.findValue(ResponseKeys.MAXIMUM_WIND_SPEED).asDouble(),
                root.findValue(ResponseKeys.TOTAL_PRECIPITATE).asDouble(),
                root.findValue(ResponseKeys.AVERAGE_VISIBILITY).asDouble(),
                root.findValue(ResponseKeys.AVERAGE_HUMIDITY).asDouble()
        );
        weatherRepository.save(weather);
        return weather;
    }

    private Weather getTomorrowWeatherByResponse(ResponseEntity<String> response) throws JsonProcessingException {
        JsonNode root = mapper.readTree(response.getBody());
        String city = root.findValue(ResponseKeys.NAME).asText();
        root = root.findValue(ResponseKeys.FORECAST).get(1);
        return new Weather(
                root.findValue(ResponseKeys.DATE).asText(),
                city,
                root.findValue(ResponseKeys.MAXIMUM_TEMPERATURE).asDouble(),
                root.findValue(ResponseKeys.MINIMUM_TEMPERATURE).asDouble(),
                root.findValue(ResponseKeys.AVERAGE_TEMPERATURE).asDouble(),
                root.findValue(ResponseKeys.MAXIMUM_WIND_SPEED).asDouble(),
                root.findValue(ResponseKeys.TOTAL_PRECIPITATE).asDouble(),
                root.findValue(ResponseKeys.AVERAGE_VISIBILITY).asDouble(),
                root.findValue(ResponseKeys.AVERAGE_HUMIDITY).asDouble()
        );
    }

    private static class ResponseKeys {
        private static final String DATE = "date";
        private static final String NAME = "name";
        private static final String MAXIMUM_TEMPERATURE = "maxtemp_c";
        private static final String MINIMUM_TEMPERATURE = "mintemp_c";
        private static final String AVERAGE_TEMPERATURE = "avgtemp_c";
        private static final String MAXIMUM_WIND_SPEED = "maxwind_kph";
        private static final String TOTAL_PRECIPITATE = "totalprecip_mm";
        private static final String AVERAGE_VISIBILITY = "avgvis_km";
        private static final String AVERAGE_HUMIDITY = "avghumidity";
        private static final String FORECAST = "forecastday";
    }
}
