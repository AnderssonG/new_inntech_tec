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
@Table(name = "candidates")
public class candidate {
    @Id
    @Column(name = "cedula", length = 20, nullable = false)
    private String cedula;

    @Column(name = "name")
    private String name;

    @Column(name = "party", length = 255, nullable = true)
    private String party;

    @Column(name = "votes", nullable = false)
    private int votes;
}
