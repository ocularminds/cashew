package cashew.routes;

import cashew.error.DuplicateError;
import cashew.error.ValidationError;
import cashew.error.ValidationErrors;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import cashew.Fault;
import cashew.error.AuthorizationError;
import cashew.error.InvalidParamsException;
import cashew.error.NotFoundError;

/**
 *
 * @author Babatope Festus
 */
public abstract class Route {

    protected final transient MessageSource messageSource;

    public Route(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public ResponseEntity<ValidationError> error(final Exception e, final HttpStatus status, final String ref) {
        final String message = Optional.of(e.getMessage()).orElse(e.getClass().getSimpleName());
        return new ResponseEntity<>(new ValidationError(ref, message), status);
    }

    public ValidationErrors validate(List<FieldError> fieldErrors, MessageSource source) {
        ValidationErrors errors = new ValidationErrors();
        fieldErrors.forEach((fieldError) -> {
            String localizedErrorMessage = resolveLocalizedErrorMessage(fieldError, source);
            errors.add(fieldError.getField(), localizedErrorMessage);
        });
        return errors;
    }

    /**
     * If the message was not found, returns the most accurate field error code
     * instead.
     *
     * @param fieldError
     * @param source
     * @return
     */
    public String resolveLocalizedErrorMessage(FieldError fieldError, MessageSource source) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        String localizedErrorMessage = source.getMessage(fieldError, currentLocale);
        if (localizedErrorMessage.equals(fieldError.getDefaultMessage())) {
            String[] fieldErrorCodes = fieldError.getCodes();
            localizedErrorMessage = fieldErrorCodes[0];
        }

        return localizedErrorMessage;
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void notFound() {
        // No-op, return empty 404
    }

    @ExceptionHandler(NotFoundError.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void notFoundException() {
        // No-op, return empty 404
    }

    /**
     *
     * @param e Duplicate Exception
     * @return
     */
    @ExceptionHandler(DuplicateError.class)
    public Fault duplicateException(final DuplicateError e) {
        return new Fault(
                Integer.toString(HttpStatus.CONFLICT.value()),
                e.getMessage()
        );
    }

    /**
     *
     * @param e InvalidParamsException
     * @return
     */
    @ExceptionHandler(InvalidParamsException.class)
    public ResponseEntity<ValidationError> invalidParamsException(final InvalidParamsException e) {
        return error(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /**
     *
     * @param e AuthorizationError
     * @return
     */
    @ExceptionHandler(AuthorizationError.class)
    public Fault unauthorizedException(final AuthorizationError e) {
        return new Fault(Integer.toString(HttpStatus.UNAUTHORIZED.value()), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrors processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return validate(fieldErrors, messageSource);
    }
}
