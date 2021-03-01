package cashew;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

/**
 * The Fault class is processing error wrapper object for holding details result
 * after certain operations.
 *
 * The fault basically contains the error(error code) and the fault(error
 * description) with arbitrary object which is any additional data to be
 * returned along with the Fault.
 *
 * @author Jejelowo B. Festus
 * @author mail.festus@gmail.com
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Fault implements Serializable {

    /**
     * The error code
     */
    private String error;

    /**
     * The error description
     */
    private String description;

    /**
     * Error group
     */
    private String group;

    /**
     * Optional data
     */
    private String value;

    /**
     * Optional field
     */
    private String additionalField;

    /**
     * Optional data
     */
    private Object data;
    private boolean redirect;
    private String target;
    public static final long serialVersionUID = 43287412;

    private static Map<String, String> errors = Fault.load();

    public Fault() {
    }

    public Fault(String error, String description) {
        this(error, description, null);
    }

    /**
     * Primary constructor for the Fault class
     *
     * @param error Error code
     * @param fault Error details or description
     * @param data Optional data returned with the error if successful
     *
     */
    public Fault(String error, String description, Object data) {
        this.error = error;
        this.description = description;
        this.data = data;
    }

    public Fault(String value, Long data) {
        this.value = value;
        this.data = data;
    }

    /**
     * @return Compares the error code to '00' to check is they are equal
     */
    public boolean isSuccess() {
        return this.error.equals("00") || this.error.startsWith("20");
    }

    /**
     * Checks is the error code meant failure. That is not equals 00
     *
     * @return boolean result of comparing error with 00
     */
    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }

    /**
     * @return if the response is to be redirected;
     */
    public boolean isRedirect() {
        return this.redirect;
    }

    /**
     *
     * @return set target url to be redirected to
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * @return if the response is to be redirected;
     */
    public String getTarget() {
        return this.target;
    }

    /**
     * Checks is the error code meant failure. That is not equals 00
     *
     * @return boolean result of comparing error with 00
     */
    public boolean isFailed() {
        return !isSuccess();
    }

    /**
     * For optional data returned with the error
     *
     * @return Optional data
     */
    public Object getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     *
     * @return The optional data group
     */
    public String getGroup() {
        return group;
    }

    /**
     * Assigns new error group
     *
     * @param group String error group which also acts as optional data
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     *
     * @return String error code
     */
    public String getError() {
        return error;
    }

    /**
     * Assigns new error code
     *
     * @param error String error code
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     *
     * @return String the error description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Assigns error details
     *
     * @param description String error description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return Optional data
     */
    public Object getData() {
        return data;
    }

    /**
     * Sets data value
     *
     * @param o Optional data
     */
    public void setData(Object o) {
        data = o;
    }

    /**
     * ISO Error code mapper
     *
     * @param err String error code
     * @param defaultValue
     * @return Error description
     */
    public static String explain(String err, String defaultValue) {
        return errors.get(err) != null ? errors.get(err) : defaultValue;
    }

    /**
     * ISO Error code mapper
     *
     * @param err String error code
     * @return Error description
     */
    public static String explain(String err) {
        return explain(err, "Unknown error");
    }

    private static Map<String, String> load() {
        Map<String, String> responses = new HashMap<>();
        responses.put("200", "Successful");
        responses.put("201", "Successful");
        responses.put("404", "Service not unavailable ");
        responses.put("300", "Duplicate Transmission");
        responses.put("500", "System malfunction");
        responses.put("301", "Rate limit exceeded.");
        return responses;
    }

    /**
     * @return the additionalField
     */
    public String getAdditionalField() {
        return additionalField;
    }

    /**
     * @param additionalField the additionalField to set
     */
    public void setAdditionalField(String additionalField) {
        this.additionalField = additionalField;
    }
}
