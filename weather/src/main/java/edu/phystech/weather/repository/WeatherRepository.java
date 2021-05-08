package edu.phystech.weather.repository;

import edu.phystech.weather.domain.Weather;
import edu.phystech.weather.domain.WeatherId;
import org.springframework.data.repository.CrudRepository;

public interface WeatherRepository extends CrudRepository<Weather, WeatherId> {
}
