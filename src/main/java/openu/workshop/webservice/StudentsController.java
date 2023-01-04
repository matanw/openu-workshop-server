package openu.workshop.webservice;

import openu.workshop.webservice.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentsController {

  @Autowired
  private AuthManager authManager;


  @GetMapping("/students")
  public Student GetStudent(@RequestParam int id,
      @RequestHeader("Authorization") String authorization) {
    authManager.ValidateAuth(authorization);
    return new Student(id, "matan");
  }

}
