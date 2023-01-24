package openu.workshop.webservice;

import java.util.Map;
import javax.persistence.EntityManager;
import openu.workshop.webservice.db.JPAWrapper;
import openu.workshop.webservice.model.Course;
import openu.workshop.webservice.model.Professor;
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

  //tbd delete
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

  class LoginInformation{
    public String username;
    public String password;
  }

 public Professor getAuthenticatedProfessor(Map<String, String> headers) throws Exception {
   LoginInformation loginInformation = getLoginInformationOrThrows401(headers);
   if (loginInformation.username.length() == 0 || loginInformation.username.charAt(0) != 'p') {
     //todo: 400 ? 401? 403?
     throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
   }
   String id = loginInformation.username.substring(1);
   Professor professor;
   try (JPAWrapper jpaWrapper = new JPAWrapper()) {
     EntityManager entityManager = jpaWrapper.getEntityManager();
     professor = entityManager.find(Professor.class, id);//todo handle nuull in all find calls
   }
   if (!professor.getPassword().equals(loginInformation.password)){
     throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
   }
   return professor;
 }


  public LoginInformation getLoginInformationOrThrows401(Map<String, String> headers){
    if (!headers.containsKey(AUTHORIZATION_HEADER)){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    String authorization=headers.get(AUTHORIZATION_HEADER);

    String[] authParts = authorization.split(" ");
    if (!authParts[0].equals("Basic")) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    String[] loginInfo = new String(Base64.decodeBase64(authParts[1])).split(":");
    if (loginInfo.length != 2) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    return new LoginInformation(){
      {
        username=loginInfo[0];
        password=loginInfo[1];
      }
    };
  }

}
