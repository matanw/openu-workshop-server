package openu.workshop.webservice.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class MethodAllowedOnlyForStudentsException extends
    ApiError {

  public MethodAllowedOnlyForStudentsException() {
    super("this method is allowed to use only for student",
        HttpStatus.FORBIDDEN);
  }
}
