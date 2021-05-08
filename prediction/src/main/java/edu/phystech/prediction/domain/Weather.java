package edu.phystech.prediction.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@IdClass(WeatherId.class)
public class Weather {
    @Id
    private String date;
    @Id
    private String city;
    private double maxTemperature;
    private double minTemperature;
    private double avgTemperature;
    private double maxWind;
    private double totalPrecipitation;
    private double avgVisibility;
    private double avgHumidity;

    public Weather(String date, String city, double maxTemperature, double minTemperature, double avgTemperature,
            double maxWind, double totalPrecipitation, double avgVisibility, double avgHumidity) {
        this.date = date;
        this.city = city;
        this.maxTemperature = maxTemperature;
        this.minTemperature = minTemperature;
        this.avgTemperature = avgTemperature;
        this.maxWind = maxWind;
        this.totalPrecipitation = totalPrecipitation;
        this.avgVisibility = avgVisibility;
        this.avgHumidity = avgHumidity;
    }

    public Weather() {}

    public String getDate() {
        return date;
    }

    public String getCity() {
        return city;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public double getAvgTemperature() {
        return avgTemperature;
    }

    public double getMaxWind() {
        return maxWind;
    }

    public double getTotalPrecipitation() {
        return totalPrecipitation;
    }

    public double getAvgVisibility() {
        return avgVisibility;
    }

    public double getAvgHumidity() {
        return avgHumidity;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "date='" + date + '\'' +
                ", city='" + city + '\'' +
                ", maxTemperature=" + maxTemperature +
                ", minTemperature=" + minTemperature +
                ", avgTemperature=" + avgTemperature +
                ", maxWind=" + maxWind +
                ", totalPrecipitation=" + totalPrecipitation +
                ", avgVisibility=" + avgVisibility +
                ", avgHumidity=" + avgHumidity +
                '}';
    }
}
