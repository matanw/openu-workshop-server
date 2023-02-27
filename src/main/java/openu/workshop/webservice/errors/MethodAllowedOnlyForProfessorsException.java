package openu.workshop.webservice.errors;

import org.springframework.http.HttpStatus;

public class MethodAllowedOnlyForProfessorsException extends
    ApiError {

  public MethodAllowedOnlyForProfessorsException() {
    super("this method is allowed to use only for professor",
        HttpStatus.FORBIDDEN);
  }
}
