package votacion.tecnico.Exception;

public class ConflictException extends RuntimeException {
    // Crea una excepcion para indicar que la operacion no puede realizarse por conflicto de negocio o integridad.
    public ConflictException(String message) {
        super(message);
        // Fin de función ConflictException
    }
}
