package openu.workshop.webservice.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class FileNotExistException extends ApiError {

  public FileNotExistException() {
    super("Requested file does not exists", HttpStatus.NOT_FOUND);
  }
}
