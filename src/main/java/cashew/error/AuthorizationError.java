package cashew.error;

public class AuthorizationError extends RuntimeException {

    private static final long serialVersionUID = -3915153143490007128L;

    public AuthorizationError(String message) {
        super(message);
    }
}
