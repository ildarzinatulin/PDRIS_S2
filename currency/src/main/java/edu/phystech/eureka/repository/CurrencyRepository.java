package edu.phystech.eureka.repository;

import edu.phystech.eureka.domain.Currency;
import org.springframework.data.repository.CrudRepository;

public interface CurrencyRepository extends CrudRepository<Currency, String> {
}
