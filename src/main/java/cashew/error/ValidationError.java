package cashew.error;

/**
 *
 * @author Jejelowo .B. Festus
 * @author mail.festus@gmail.com
 */
public class ValidationError {

    private String field;
    private String message;

    public ValidationError() {
    }

    public ValidationError(String path, String msg) {
        this.field = path;
        this.message = msg;
    }

    /**
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * @param field the field to set
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
