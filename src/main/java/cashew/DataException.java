package cashew;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * System level exception handler
 *
 * @author Jejelowo B. Festus
 * @author mail.festus@gmail.com
 */
public class DataException {

    private static final Logger LOG = LoggerFactory.getLogger(DataException.class);

    public static void handle(Exception e) {
        handle(null, e);
    }

    public static void handle(String message, Exception e) {
        if (e.getCause() instanceof org.hibernate.PropertyValueException) {
            org.hibernate.PropertyValueException ex
                    = (org.hibernate.PropertyValueException) e.getCause();
            LOG.info(ex.getMessage() + " " + ex.getPropertyName() + " " + ex.getEntityName(), ex);
        } else if (e.getCause() instanceof org.hibernate.exception.DataException) {
            org.hibernate.exception.DataException exception
                    = (org.hibernate.exception.DataException) e.getCause();
            LOG.info(exception.getMessage() + " " + exception.getSQL() + " "
                    + exception.getErrorCode(), exception);
        } else if (e.getCause() instanceof org.hibernate.exception.SQLGrammarException) {
            org.hibernate.exception.SQLGrammarException sg
                    = (org.hibernate.exception.SQLGrammarException) e.getCause();
            LOG.info(sg.getMessage() + " " + sg.getSQL() + " " + sg.getErrorCode(), e);
        } else if (e.getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException cv = (ConstraintViolationException) e.getCause();
            Set<ConstraintViolation<?>> violations = cv.getConstraintViolations();
            violations.forEach((v) -> {
                LOG.info(v.getRootBean() + " " + v.getMessage() + " " + v.getInvalidValue(), v);
            });
        } else {
            LOG.error(message, e);
        }
    }
}
