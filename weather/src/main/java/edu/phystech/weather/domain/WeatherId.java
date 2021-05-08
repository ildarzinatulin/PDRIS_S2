package edu.phystech.weather.domain;

import java.io.Serializable;
import java.util.Objects;

public class WeatherId implements Serializable {
    private String date;
    private String city;

    public WeatherId() {}

    public WeatherId(String date, String city) {
        this.date = date;
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WeatherId weatherId = (WeatherId) o;
        return date.equals(weatherId.date) &&
                city.equals(weatherId.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, city);
    }
}
