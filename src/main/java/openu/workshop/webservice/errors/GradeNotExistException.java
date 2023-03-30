package openu.workshop.webservice.errors;

import org.springframework.http.HttpStatus;

public class GradeNotExistException extends ApiError {

  public GradeNotExistException() {
    super("grade does not exists", HttpStatus.NOT_FOUND);
  }
}
