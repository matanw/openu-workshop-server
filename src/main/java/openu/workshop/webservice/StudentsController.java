package openu.workshop.webservice;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentsController {

  @GetMapping("/students")
  public Student GetStudent(@RequestParam int id,
      @RequestHeader("Authorization") String authorization){
    String[] authParts = authorization.split(" ");
    if (!authParts[0].equals("Basic")){
      throw new RuntimeException();
    }

    String[] loginInfo = new String(Base64.decodeBase64(authParts[1])).split(":");
    if (!loginInfo[0].equals("aaa") || !loginInfo[1].equals("bbb")){
      throw new RuntimeException();
    }



    return new Student(id, "matan");
  }

}
