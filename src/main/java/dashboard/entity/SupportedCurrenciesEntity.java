package dashboard.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "supported_currencies")
@Data
public class SupportedCurrenciesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "name", nullable = false)
    private String name;
}
