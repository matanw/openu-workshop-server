package openu.workshop.webservice.auth;

import java.util.Map;
import openu.workshop.webservice.errors.UnauthorizedException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AuthManager {

  // for some reason, it converted to lower case
  final String AUTHORIZATION_HEADER ="authorization";


  public LoginInformation GetLoginInformationOrThrows401(Map<String, String> headers) throws UnauthorizedException{
    if (!headers.containsKey(AUTHORIZATION_HEADER)){
      throw new UnauthorizedException();
    }
    String authorization=headers.get(AUTHORIZATION_HEADER);

    String[] authParts = authorization.split(" ");
    if (!authParts[0].equals("Basic")) {
      throw new UnauthorizedException();
    }

    String[] loginInfo = new String(Base64.decodeBase64(authParts[1])).split(":");
    if (loginInfo.length != 2) {
      throw new UnauthorizedException();
    }
    String username=loginInfo[0];
    String password=loginInfo[1];
    if (username.length()==0){
      throw new UnauthorizedException();
    }
    if (username.charAt(0)=='p'){
      return new LoginInformation(username.substring(1),password, LoginType.PROFESSOR);
    }
    if (username.charAt(0)=='s'){
      return new LoginInformation(username.substring(1),password, LoginType.STUDENT);
    }
    throw new UnauthorizedException();
  }

}
