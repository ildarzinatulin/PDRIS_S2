package edu.phystech.eureka.service;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.phystech.eureka.domain.Currency;
import edu.phystech.eureka.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final RestTemplate restTemplate;

    private final static String URL_FOR_CURRENCY_REQUEST = "http://www.cbr.ru/scripts/XML_daily.asp?date_req=";
    private final static String END_OF_RESPONSE_PATTERN = "</Value></Valute>";
    private final static String CURRENCY_RESPONSE_START_PATTERN = "<Valute ID=\"R01235\">"
            + "<NumCode>840</NumCode>"
            + "<CharCode>USD</CharCode>"
            + "<Nominal>1</Nominal>"
            + "<Name>Доллар США</Name>"
            + "<Value>";

    @Autowired
    public CurrencyService(CurrencyRepository currencyRepository, RestTemplateBuilder builder) {
        this.currencyRepository = currencyRepository;
        this.restTemplate = builder.build();
    }

    public List<Currency> getDollarCurrencyForLastDays(int numberOfDays) {
        return IntStream.range(0, numberOfDays)
                .mapToObj(this::getInstantForDaysBefore)
                .map(this::getDollarCurrency)
                .collect(Collectors.toList());
    }

    private Instant getInstantForDaysBefore(int days) {
        return Instant.now().minus(days, ChronoUnit.DAYS);
    }

    private Currency getDollarCurrency(Instant date) {
        DateFormat dateFormatDay = new SimpleDateFormat("yyyy-MM-dd");
        String stringDate = dateFormatDay.format(Date.from(date));
        Optional<Currency> currency = currencyRepository.findById(stringDate);
        if (currency.isPresent()) {
            return currency.get();
        }

        ResponseEntity<String> response = restTemplate.getForEntity(createUrlForInstant(date), String.class);
        return getCurrencyByResponse(response, stringDate);
    }

    private Currency getCurrencyByResponse(ResponseEntity<String> response, String date) {
        double currencyValue = getCurrencyValueByResponse(response);
        Currency currency = new Currency(date, currencyValue);
        currencyRepository.save(currency);
        return currency;
    }

    private double getCurrencyValueByResponse(ResponseEntity<String> response) {
        String body = response.getBody();
        System.out.println(body);
        assert body != null;
        int valueStartIndex = body.indexOf(CURRENCY_RESPONSE_START_PATTERN) + CURRENCY_RESPONSE_START_PATTERN.length();
        body = body.substring(valueStartIndex);
        int valueEndIndex = body.indexOf(END_OF_RESPONSE_PATTERN);
        return Double.parseDouble(body.substring(0, valueEndIndex).replace(",", "."));
    }

    private String createUrlForInstant(Instant time) {
        DateFormat dateFormatDay = new SimpleDateFormat("dd/MM/yyyy");
        return URL_FOR_CURRENCY_REQUEST + dateFormatDay.format(Date.from(time));
    }
}
