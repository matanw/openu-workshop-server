package openu.workshop.webservice.errors;

import org.springframework.http.HttpStatusCode;

public class ApiError extends  Exception{
  private HttpStatusCode httpStatus;
  private String message;

  public ApiError(String message, HttpStatusCode httpStatus) {
    super(message);
    this.httpStatus = httpStatus;
    this.message = message;
  }

  public HttpStatusCode getHttpStatus() {
    return httpStatus;
  }

  public void setHttpStatus(HttpStatusCode httpStatus) {
    this.httpStatus = httpStatus;
  }

  @Override
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
