package votacion.tecnico.Service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import votacion.tecnico.Dto.CandidateResponse;
import votacion.tecnico.Dto.CreateCandidateRequest;
import votacion.tecnico.Dto.PageResponse;
import votacion.tecnico.Exception.ConflictException;
import votacion.tecnico.Exception.ResourceNotFoundException;
import votacion.tecnico.Model.candidate;
import votacion.tecnico.Repository.CandidateRepository;
import votacion.tecnico.Repository.VotersRepository;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final VotersRepository votersRepository;

    // Inicializa el servicio de candidatos con los repositorios necesarios para validaciones y persistencia.
    public CandidateService(CandidateRepository candidateRepository, VotersRepository votersRepository) {
        this.candidateRepository = candidateRepository;
        this.votersRepository = votersRepository;
        // Fin de función CandidateService
    }

    // Crea un nuevo candidato validando que su cedula no exista ni como candidato ni como votante.
    public CandidateResponse create(CreateCandidateRequest request) {
        if (candidateRepository.existsById(request.cedula())) {
            throw new ConflictException("Ya existe un candidato con la cedula " + request.cedula());
        }

        if (votersRepository.existsById(request.cedula())) {
            throw new ConflictException("La cedula " + request.cedula() + " ya esta registrada como votante");
        }

        candidate candidate = new candidate();
        candidate.setCedula(request.cedula());
        candidate.setName(request.name());
        candidate.setParty(request.party());
        candidate.setVotes(0);

        try {
            return toResponse(candidateRepository.save(candidate));
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("No fue posible registrar el candidato por conflicto de datos");
        }
        // Fin de función create
    }

    // Obtiene todos los candidatos ordenados por cedula y los transforma a formato de respuesta.
    public PageResponse<CandidateResponse> getAll(int page, int size, String cedula, String name, String party) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "cedula"));
        Specification<candidate> specification = buildCandidateSpecification(cedula, name, party);
        Page<CandidateResponse> candidatePage = candidateRepository.findAll(specification, pageable)
                .map(this::toResponse);

        return new PageResponse<>(
                candidatePage.getContent(),
                candidatePage.getNumber(),
                candidatePage.getSize(),
                candidatePage.getTotalElements(),
                candidatePage.getTotalPages(),
                candidatePage.isLast());
        // Fin de función getAll
    }

    // Busca un candidato por id y devuelve su informacion o lanza un error si no existe.
    public CandidateResponse getById(String id) {
        return candidateRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un candidato con id " + id));
        // Fin de función getById
    }

    // Elimina un candidato existente siempre que no tenga votos relacionados que impidan el borrado.
    public void delete(String id) {
        candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un candidato con id " + id));

        try {
            candidateRepository.delete(candidate);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("No se puede eliminar el candidato porque tiene votos asociados");
        }
        // Fin de función delete
    }

    // Construye los criterios dinamicos para filtrar la lista de candidatos según los parametros recibidos.
    private Specification<candidate> buildCandidateSpecification(String cedula, String name, String party) {
        return Specification.where(containsIgnoreCase("cedula", cedula))
                .and(containsIgnoreCase("name", name))
                .and(containsIgnoreCase("party", party));
        // Fin de funcion buildCandidateSpecification
    }

    // Genera un filtro parcial e insensible a mayusculas para el campo indicado cuando el valor tiene contenido.
    private Specification<candidate> containsIgnoreCase(String field, String value) {
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

    // Convierte la entidad de candidato en el DTO que se devuelve al cliente.
    private CandidateResponse toResponse(candidate candidate) {
        return new CandidateResponse(
                candidate.getCedula(),
                candidate.getName(),
                candidate.getParty(),
                candidate.getVotes());
        // Fin de función toResponse
    }
}
