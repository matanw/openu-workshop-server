package openu.workshop.webservice.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class TaskIDMismatchException extends ApiError {

  public TaskIDMismatchException() {
    super("task id in url and in body don't match", HttpStatus.BAD_REQUEST);
  }
}
