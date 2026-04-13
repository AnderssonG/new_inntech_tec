package votacion.tecnico.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import votacion.tecnico.Model.votes;

public interface VotesRepository extends JpaRepository<votes, Long> {
    boolean existsByVoterCedula(String voterCedula);

    long countByCandidateCedula(String candidateCedula);
}
