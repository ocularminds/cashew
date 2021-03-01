package cashew.error;

public class NotFoundError extends RuntimeException {
    private static final long serialVersionUID = -3915153147790977128L;
    public NotFoundError(String name) {
        super("Record not found. '" + name + "'.");
    }
}
