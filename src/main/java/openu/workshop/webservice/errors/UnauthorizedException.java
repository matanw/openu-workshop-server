package openu.workshop.webservice.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class UnauthorizedException extends ApiError {

  public UnauthorizedException() {
    super("Unauthorized", HttpStatus.UNAUTHORIZED);
  }
}
