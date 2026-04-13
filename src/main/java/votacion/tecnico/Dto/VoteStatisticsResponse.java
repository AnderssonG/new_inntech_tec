package votacion.tecnico.Dto;

import java.util.List;

public record VoteStatisticsResponse(
        long totalVotes,
        long totalVotersWhoHaveVoted,
        List<CandidateStatisticsResponse> candidates) {
}
