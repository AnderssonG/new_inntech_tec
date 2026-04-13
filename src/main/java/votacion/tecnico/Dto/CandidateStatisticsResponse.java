package votacion.tecnico.Dto;

public record CandidateStatisticsResponse(
        String candidateId,
        String candidateName,
        String party,
        int totalVotes,
        double votePercentage) {
}
