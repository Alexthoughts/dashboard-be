package dashboard.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "notes")
public class NoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "text")
    private String text;
}
