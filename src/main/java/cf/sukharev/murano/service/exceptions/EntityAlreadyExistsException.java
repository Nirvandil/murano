package cf.sukharev.murano.service.exceptions;

class EntityAlreadyExistsException extends RuntimeException {
    EntityAlreadyExistsException(String message) {
        super(message);
    }
}
