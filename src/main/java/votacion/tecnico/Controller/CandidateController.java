package votacion.tecnico.Controller;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import votacion.tecnico.Dto.CandidateResponse;
import votacion.tecnico.Dto.CreateCandidateRequest;
import votacion.tecnico.Dto.PageResponse;
import votacion.tecnico.Service.CandidateService;

@RestController
@Validated
@RequestMapping("/candidates")
public class CandidateController {

    private final CandidateService candidateService;

    // Inicializa el controlador de candidatos con el servicio encargado de la lógica de negocio.
    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
        // Fin de función CandidateController
    }

    // Registra un nuevo candidato y devuelve la ubicación del recurso creado.
    @PostMapping
    public ResponseEntity<CandidateResponse> create(@Valid @RequestBody CreateCandidateRequest request) {
        CandidateResponse response = candidateService.create(request);
        return ResponseEntity.created(URI.create("/candidates/" + response.id())).body(response);
        // Fin de función create
    }

    // Obtiene la lista paginada de candidatos registrados y permite filtrar por cedula, nombre y partido.
    @GetMapping
    public PageResponse<CandidateResponse> getAll(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "La pagina no puede ser negativa") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "El tamanio de pagina debe ser mayor a cero") int size,
            @RequestParam(required = false) String cedula,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String party) {
        return candidateService.getAll(page, size, cedula, name, party);
        // Fin de función getAll
    }

    // Busca y devuelve la información de un candidato a partir de su identificador.
    @GetMapping("/{id}")
    public CandidateResponse getById(@PathVariable String id) {
        return candidateService.getById(id);
        // Fin de función getById
    }

    // Elimina un candidato por su identificador cuando no tiene votos asociados que lo bloqueen.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        candidateService.delete(id);
        return ResponseEntity.noContent().build();
        // Fin de función delete
    }
}
