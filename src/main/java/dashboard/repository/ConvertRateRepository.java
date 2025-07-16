package dashboard.repository;

import dashboard.entity.ConvertRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConvertRateRepository extends JpaRepository<ConvertRateEntity, Long> {
}
