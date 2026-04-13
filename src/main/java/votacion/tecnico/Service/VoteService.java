package votacion.tecnico.Service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import votacion.tecnico.Dto.CandidateStatisticsResponse;
import votacion.tecnico.Dto.CreateVoteRequest;
import votacion.tecnico.Dto.VoteResponse;
import votacion.tecnico.Dto.VoteStatisticsResponse;
import votacion.tecnico.Exception.ConflictException;
import votacion.tecnico.Exception.ResourceNotFoundException;
import votacion.tecnico.Model.candidate;
import votacion.tecnico.Model.voters;
import votacion.tecnico.Model.votes;
import votacion.tecnico.Repository.CandidateRepository;
import votacion.tecnico.Repository.VotersRepository;
import votacion.tecnico.Repository.VotesRepository;

@Service
public class VoteService {

    private final VotesRepository votesRepository;
    private final VotersRepository votersRepository;
    private final CandidateRepository candidateRepository;

    // Inicializa el servicio de votos con los repositorios necesarios para validar, guardar y consultar estadisticas.
    public VoteService(
            VotesRepository votesRepository,
            VotersRepository votersRepository,
            CandidateRepository candidateRepository) {
        this.votesRepository = votesRepository;
        this.votersRepository = votersRepository;
        this.candidateRepository = candidateRepository;
        // Fin de función VoteService
    }

    // Registra un voto verificando que existan el votante y el candidato y que el votante no haya votado antes.
    @Transactional
    public VoteResponse create(CreateVoteRequest request) {
        voters voter = votersRepository.findById(request.voterId())
                .orElseThrow(() -> new ResourceNotFoundException("No existe un votante con id " + request.voterId()));

        candidate candidate = candidateRepository.findById(request.candidateId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe un candidato con id " + request.candidateId()));

        if (voter.isHasVoted() || votesRepository.existsByVoterCedula(voter.getCedula())) {
            throw new ConflictException("El votante con id " + voter.getCedula() + " ya emitio un voto");
        }

        votes vote = new votes();
        vote.setVoterCedula(voter.getCedula());
        vote.setCandidateCedula(candidate.getCedula());

        votes savedVote = votesRepository.save(vote);
        return toResponse(savedVote);
        // Fin de función create
    }

    // Obtiene todos los votos emitidos ordenados por identificador y los transforma a formato de respuesta.
    public List<VoteResponse> getAll() {
        return votesRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(this::toResponse)
                .toList();
        // Fin de función getAll
    }

    // Calcula el total de votos, el total de votantes que han votado y el porcentaje por candidato.
    public VoteStatisticsResponse getStatistics() {
        List<candidate> candidates = candidateRepository.findAll(
                Sort.by(Sort.Order.desc("votes"), Sort.Order.asc("cedula")));

        long totalVotes = votesRepository.count();
        long totalVotersWhoHaveVoted = votersRepository.countByHasVotedTrue();

        List<CandidateStatisticsResponse> candidateStatistics = candidates.stream()
                .map(candidate -> {
                    long candidateVotes = votesRepository.countByCandidateCedula(candidate.getCedula());
                    return new CandidateStatisticsResponse(
                            candidate.getCedula(),
                            candidate.getName(),
                            candidate.getParty(),
                            Math.toIntExact(candidateVotes),
                            calculatePercentage(candidateVotes, totalVotes));
                })
                .toList();

        return new VoteStatisticsResponse(totalVotes, totalVotersWhoHaveVoted, candidateStatistics);
        // Fin de función getStatistics
    }

    // Calcula el porcentaje de votos de un candidato respecto al total de votos emitidos.
    private double calculatePercentage(long candidateVotes, long totalVotes) {
        if (totalVotes == 0) {
            return 0.0;
        }
        return (candidateVotes * 100.0) / totalVotes;
        // Fin de función calculatePercentage
    }

    // Convierte la entidad de voto en el DTO que se devuelve al cliente.
    private VoteResponse toResponse(votes vote) {
        return new VoteResponse(
                vote.getId(),
                vote.getVoterCedula(),
                vote.getCandidateCedula());
        // Fin de función toResponse
    }
}
