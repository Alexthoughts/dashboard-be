package dashboard.repository;


import dashboard.entity.SupportedCurrenciesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportedCurrenciesRepository extends JpaRepository<SupportedCurrenciesEntity, Long> {
    SupportedCurrenciesEntity findBySymbol(String symbol);
}
