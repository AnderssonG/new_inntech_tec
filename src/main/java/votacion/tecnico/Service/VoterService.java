package votacion.tecnico.Service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import votacion.tecnico.Dto.CreateVoterRequest;
import votacion.tecnico.Dto.PageResponse;
import votacion.tecnico.Dto.VoterResponse;
import votacion.tecnico.Exception.ConflictException;
import votacion.tecnico.Exception.ResourceNotFoundException;
import votacion.tecnico.Model.voters;
import votacion.tecnico.Repository.CandidateRepository;
import votacion.tecnico.Repository.VotersRepository;

@Service
public class VoterService {

    private final VotersRepository votersRepository;
    private final CandidateRepository candidateRepository;

    // Inicializa el servicio de votantes con los repositorios necesarios para validaciones y persistencia.
    public VoterService(VotersRepository votersRepository, CandidateRepository candidateRepository) {
        this.votersRepository = votersRepository;
        this.candidateRepository = candidateRepository;
        // Fin de función VoterService
    }

    // Crea un nuevo votante validando cedula, email y que no exista como candidato.
    public VoterResponse create(CreateVoterRequest request) {
        if (votersRepository.existsById(request.cedula())) {
            throw new ConflictException("Ya existe un votante con la cedula " + request.cedula());
        }

        if (votersRepository.existsByEmail(request.email())) {
            throw new ConflictException("Ya existe un votante con el email " + request.email());
        }

        if (candidateRepository.existsById(request.cedula())) {
            throw new ConflictException("La cedula " + request.cedula() + " ya esta registrada como candidato");
        }

        voters voter = new voters();
        voter.setCedula(request.cedula());
        voter.setName(request.name());
        voter.setEmail(request.email());
        voter.setHasVoted(false);

        try {
            return toResponse(votersRepository.save(voter));
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("No fue posible registrar el votante por conflicto de datos");
        }
        // Fin de función create
    }

    // Obtiene todos los votantes ordenados por cedula y los transforma a formato de respuesta.
    public PageResponse<VoterResponse> getAll(int page, int size, String cedula, String name, String email, Boolean hasVoted) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "cedula"));
        Specification<voters> specification = buildVoterSpecification(cedula, name, email, hasVoted);
        Page<VoterResponse> voterPage = votersRepository.findAll(specification, pageable)
                .map(this::toResponse);

        return new PageResponse<>(
                voterPage.getContent(),
                voterPage.getNumber(),
                voterPage.getSize(),
                voterPage.getTotalElements(),
                voterPage.getTotalPages(),
                voterPage.isLast());
        // Fin de función getAll
    }

    // Busca un votante por id y devuelve su informacion o lanza un error si no existe.
    public VoterResponse getById(String id) {
        return votersRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un votante con id " + id));
        // Fin de función getById
    }

    // Elimina un votante existente siempre que no tenga votos relacionados que impidan el borrado.
    public void delete(String id) {
        voters voter = votersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un votante con id " + id));

        try {
            votersRepository.delete(voter);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("No se puede eliminar el votante porque tiene votos asociados");
        }
        // Fin de función delete
    }

    // Construye los criterios dinamicos para filtrar la lista de votantes segun los parametros recibidos.
    private Specification<voters> buildVoterSpecification(String cedula, String name, String email, Boolean hasVoted) {
        return Specification.where(containsIgnoreCase("cedula", cedula))
                .and(containsIgnoreCase("name", name))
                .and(containsIgnoreCase("email", email))
                .and(equalsHasVoted(hasVoted));
        // Fin de funcion buildVoterSpecification
    }

    // Genera un filtro parcial e insensible a mayusculas para el campo indicado cuando el valor tiene contenido.
    private Specification<voters> containsIgnoreCase(String field, String value) {
        return (root, query, criteriaBuilder) -> {
            if (value == null || value.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(field)),
                    "%" + value.trim().toLowerCase() + "%");
        };
        // Fin de funcion containsIgnoreCase
    }

    // Genera un filtro exacto para el estado de voto cuando el parametro fue enviado.
    private Specification<voters> equalsHasVoted(Boolean hasVoted) {
        return (root, query, criteriaBuilder) -> {
            if (hasVoted == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("hasVoted"), hasVoted);
        };
        // Fin de funcion equalsHasVoted
    }

    // Convierte la entidad de votante en el DTO que se devuelve al cliente.
    private VoterResponse toResponse(voters voter) {
        return new VoterResponse(
                voter.getCedula(),
                voter.getName(),
                voter.getEmail(),
                voter.isHasVoted());
        // Fin de función toResponse
    }
}
