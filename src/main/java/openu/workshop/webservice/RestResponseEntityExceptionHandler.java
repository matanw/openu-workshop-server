package openu.workshop.webservice;


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
  protected ResponseEntity<Object> handleConflict(
      ApiError ex, WebRequest request) {
    String bodyOfResponse = "This should be application specific";
    return handleExceptionInternal(ex, ex.getMessage(),
        new HttpHeaders(), ex.getHttpStatus(), request);
  }
}