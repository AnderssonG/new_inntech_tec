package votacion.tecnico.Dto;

public record CandidateResponse(
        String id,
        String name,
        String party,
        int votes) {
}
