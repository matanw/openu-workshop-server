package openu.workshop.webservice.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class FileAlreadyExistsException extends ApiError {

  public FileAlreadyExistsException() {
    super("the file you try to create is already exists, use 'put' to edit it",
        HttpStatus.BAD_REQUEST);
  }
}
