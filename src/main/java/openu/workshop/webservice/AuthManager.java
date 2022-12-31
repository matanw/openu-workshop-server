package openu.workshop.webservice;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AuthManager {

  public void ValidateAuth(String authorization) {
    String[] authParts = authorization.split(" ");
    if (!authParts[0].equals("Basic")) {
      throw new RuntimeException();
    }

    String[] loginInfo = new String(Base64.decodeBase64(authParts[1])).split(":");
    if (!loginInfo[0].equals("aaa") || !loginInfo[1].equals("bbb")) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
  }


}
