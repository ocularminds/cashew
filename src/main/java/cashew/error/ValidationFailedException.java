package cashew.error;

public class ValidationFailedException extends RuntimeException {
    private static final long serialVersionUID = -3915153999490977128L;
    public ValidationFailedException(String msg) {
        super(msg);
    }
}
