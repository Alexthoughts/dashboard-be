package dashboard.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "convert_rate")
@Data
public class ConvertRateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_currency_id", nullable = false)
    private SupportedCurrenciesEntity fromCurrencyId;

    @ManyToOne
    @JoinColumn(name = "to_currency_id", nullable = false)
    private SupportedCurrenciesEntity toCurrencyId;

    @Column(name = "convertedAmount")
    private Double convertedAmount;
}
