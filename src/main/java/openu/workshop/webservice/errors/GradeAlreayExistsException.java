package openu.workshop.webservice.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class GradeAlreayExistsException extends ApiError {


  public GradeAlreayExistsException() {
    super("grade already exists", HttpStatus.BAD_REQUEST);
  }
}
