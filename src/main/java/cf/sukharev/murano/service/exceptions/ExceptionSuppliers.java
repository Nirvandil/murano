package cf.sukharev.murano.service.exceptions;

import java.util.function.Supplier;

public class ExceptionSuppliers {
    private ExceptionSuppliers() {
        throw new IllegalStateException("This class should not be instantiated.");
    }

    public static Supplier<EntityNotFoundException> notFoundException(String message) {
        return () -> new EntityNotFoundException(message);
    }

    public static EntityAlreadyExistsException alreadyExistsException(String message) {
        return new EntityAlreadyExistsException(message);
    }
}
