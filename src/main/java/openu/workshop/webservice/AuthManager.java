package openu.workshop.webservice;

import java.util.Map;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AuthManager {

  // for some reason, it converted to lower case
  final String AUTHORIZATION_HEADER ="authorization";

  final Boolean DISABLE_AUTH = Boolean.FALSE;

  public void ValidateAuth(Map<String, String> headers){
    if (DISABLE_AUTH){
      return;
    }
    if (!headers.containsKey(AUTHORIZATION_HEADER)){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    String authorization=headers.get(AUTHORIZATION_HEADER);

    String[] authParts = authorization.split(" ");
    if (!authParts[0].equals("Basic")) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    String[] loginInfo = new String(Base64.decodeBase64(authParts[1])).split(":");
    if (!loginInfo[0].equals("aaa") || !loginInfo[1].equals("bbb")) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
  }


}
