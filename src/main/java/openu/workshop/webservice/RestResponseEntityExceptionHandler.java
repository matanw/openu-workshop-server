package openu.workshop.webservice;


import java.util.HashMap;
import java.util.Map;
import openu.workshop.webservice.errors.ApiError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler
    extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value
      = { ApiError.class })
  protected ResponseEntity<Object> handleApiError(
      ApiError ex, WebRequest request) {
    Map<String, String> response= Map.ofEntries(
        Map.entry("message", ex.getMessage()),
        Map.entry("type", "server-thrown-error")
    );
    return handleExceptionInternal(ex, response,
        new HttpHeaders(), ex.getHttpStatus(), request);
  }
}