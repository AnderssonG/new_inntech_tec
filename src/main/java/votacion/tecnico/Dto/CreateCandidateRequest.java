package votacion.tecnico.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCandidateRequest(
        @NotBlank(message = "La cédula es obligatoria")
        @Size(max = 20, message = "La cédula no puede superar 20 caracteres")
        String cedula,

        @NotBlank(message = "El nombre es obligatorio")
        String name,

        @Size(max = 255, message = "El partido no puede superar 255 caracteres")
        String party) {
}
