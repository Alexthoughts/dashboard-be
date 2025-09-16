package dashboard.repository;

import dashboard.entity.ConvertRateEntity;
import dashboard.entity.SupportedCurrenciesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConvertRateRepository extends JpaRepository<ConvertRateEntity, Long> {
    Optional<ConvertRateEntity> findByFromCurrencyIdAndToCurrencyId(
            SupportedCurrenciesEntity fromCurrency,
            SupportedCurrenciesEntity toCurrency
    );
}
