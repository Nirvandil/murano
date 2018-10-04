package cf.sukharev.murano.service.exceptions;

public class EntityNotFoundException extends RuntimeException {
    EntityNotFoundException(String message) {
        super(message);
    }
}
