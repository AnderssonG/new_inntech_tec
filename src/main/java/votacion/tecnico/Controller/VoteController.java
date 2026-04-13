package votacion.tecnico.Controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import votacion.tecnico.Dto.CreateVoteRequest;
import votacion.tecnico.Dto.VoteResponse;
import votacion.tecnico.Dto.VoteStatisticsResponse;
import votacion.tecnico.Service.VoteService;

@RestController
@Validated
@RequestMapping("/votes")
public class VoteController {

    private final VoteService voteService;

    // Inicializa el controlador de votos con el servicio encargado de la logica de negocio.
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
        // Fin de función VoteController
    }

    // Registra un nuevo voto validando el votante y el candidato enviados en la solicitud.
    @PostMapping
    public ResponseEntity<VoteResponse> create(@Valid @RequestBody CreateVoteRequest request) {
        VoteResponse response = voteService.create(request);
        return ResponseEntity.created(URI.create("/votes/" + response.id())).body(response);
        // Fin de función create
    }

    // Obtiene la lista completa de votos emitidos en el sistema.
    @GetMapping
    public List<VoteResponse> getAll() {
        return voteService.getAll();
        // Fin de función getAll
    }

    // Calcula y devuelve las estadisticas generales de la votacion.
    @GetMapping("/statistics")
    public VoteStatisticsResponse getStatistics() {
        return voteService.getStatistics();
        // Fin de función getStatistics
    }
}
