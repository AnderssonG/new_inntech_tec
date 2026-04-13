package votacion.tecnico.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import votacion.tecnico.Model.voters;

public interface VotersRepository extends JpaRepository<voters, String>, JpaSpecificationExecutor<voters> {
    boolean existsByEmail(String email);

    long countByHasVotedTrue();
}
