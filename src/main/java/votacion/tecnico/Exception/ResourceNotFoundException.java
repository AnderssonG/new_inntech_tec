package votacion.tecnico.Exception;

public class ResourceNotFoundException extends RuntimeException {
    // Crea una excepción para indicar que el recurso solicitado no existe en el sistema.
    public ResourceNotFoundException(String message) {
        super(message);
        // Fin de función ResourceNotFoundException
    }
}
