package dashboard.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "holidays")
public class HolidayEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "local_name")
    private String localName;

    @Column(name = "date")
    private Date date;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "fixed")
    private Boolean fixed;

    @Column(name = "global")
    private Boolean global;
}
