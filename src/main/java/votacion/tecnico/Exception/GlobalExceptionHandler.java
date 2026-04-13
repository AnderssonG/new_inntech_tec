package votacion.tecnico.Exception;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Maneja los errores de recurso no encontrado y responde con estado 404.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, List.of(ex.getMessage()));
        // Fin de función handleNotFound
    }

    // Maneja los errores de conflicto y responde con estado 409.
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(ConflictException ex) {
        return buildResponse(HttpStatus.CONFLICT, List.of(ex.getMessage()));
        // Fin de función handleConflict
    }

    // Maneja los errores de validacion del request y responde con estado 400 y el detalle por campo.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .toList();

        return buildResponse(HttpStatus.BAD_REQUEST, details);
        // Fin de función handleValidation
    }

    // Maneja conflictos de integridad de base de datos y responde con estado 409.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleIntegrityViolation(DataIntegrityViolationException ex) {
        return buildResponse(HttpStatus.CONFLICT,
                List.of("La operacion no se pudo completar por restricciones de integridad en la base de datos"));
        // Fin de función handleIntegrityViolation
    }

    // Maneja errores no controlados y responde con estado 500.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                List.of("Ocurrio un error interno al procesar la solicitud"));
        // Fin de función handleGeneric
    }

    // Formatea el mensaje de error de un campo validado para devolverlo al cliente.
    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
        // Fin de función formatFieldError
    }

    // Construye la respuesta estandar de error con fecha, estado HTTP y detalles del problema.
    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, List<String> details) {
        ApiErrorResponse body = new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                details);
        return ResponseEntity.status(status).body(body);
        // Fin de función buildResponse
    }
}
