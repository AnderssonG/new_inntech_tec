package votacion.tecnico.Dto;

public record VoterResponse(
        String id,
        String name,
        String email,
        boolean hasVoted) {
}
