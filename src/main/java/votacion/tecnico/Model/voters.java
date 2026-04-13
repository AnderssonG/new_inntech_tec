package votacion.tecnico.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "voters")
public class voters {
    @Id
    @Column(name = "cedula", length = 20, nullable = false)
    private String cedula;

    @Column(name = "name")
    private String name;

    @Column(name = "email", length = 120, nullable = false, unique = true)
    private String email;

    @Column(name = "has_voted", nullable = false)
    private boolean hasVoted;

}
