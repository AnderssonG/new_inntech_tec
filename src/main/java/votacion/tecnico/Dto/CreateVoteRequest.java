package votacion.tecnico.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateVoteRequest(
        @NotBlank(message = "El voter_id es obligatorio")
        @Size(max = 20, message = "El voter_id no puede superar 20 caracteres")
        String voterId,

        @NotBlank(message = "El candidate_id es obligatorio")
        @Size(max = 20, message = "El candidate_id no puede superar 20 caracteres")
        String candidateId) {
}
