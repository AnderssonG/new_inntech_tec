package votacion.tecnico.Dto;

public record VoteResponse(
        Long id,
        String voterId,
        String candidateId) {
}
