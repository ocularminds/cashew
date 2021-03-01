package cashew.error;

public class JsonParserError extends RuntimeException {
    private static final long serialVersionUID = -3915153143433977128L;
    public JsonParserError(final String json) {
        super("Unable to parser input json: '" + json + "'.");
    }
}
