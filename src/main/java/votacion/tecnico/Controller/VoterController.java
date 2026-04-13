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
import votacion.tecnico.Dto.CreateVoterRequest;
import votacion.tecnico.Dto.PageResponse;
import votacion.tecnico.Dto.VoterResponse;
import votacion.tecnico.Service.VoterService;

@RestController
@Validated
@RequestMapping("/voters")
public class VoterController {

    private final VoterService voterService;

    // Inicializa el controlador de votantes con el servicio encargado de la logica de negocio.
    public VoterController(VoterService voterService) {
        this.voterService = voterService;
        // Fin de función VoterController
    }

    // Registra un nuevo votante y devuelve la ubicacion del recurso creado.
    @PostMapping
    public ResponseEntity<VoterResponse> create(@Valid @RequestBody CreateVoterRequest request) {
        VoterResponse response = voterService.create(request);
        return ResponseEntity.created(URI.create("/voters/" + response.id())).body(response);
        // Fin de función create
    }

    // Obtiene la lista paginada de votantes registrados y permite filtrar por cedula, nombre, email y estado de voto.
    @GetMapping
    public PageResponse<VoterResponse> getAll(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "La pagina no puede ser negativa") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "El tamanio de pagina debe ser mayor a cero") int size,
            @RequestParam(required = false) String cedula,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean hasVoted) {
        return voterService.getAll(page, size, cedula, name, email, hasVoted);
        // Fin de función getAll
    }

    // Busca y devuelve la informacion de un votante a partir de su identificador.
    @GetMapping("/{id}")
    public VoterResponse getById(@PathVariable String id) {
        return voterService.getById(id);
        // Fin de función getById
    }

    // Elimina un votante por su identificador cuando no tiene restricciones de integridad asociadas.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        voterService.delete(id);
        return ResponseEntity.noContent().build();
        // Fin de función delete
    }
}
