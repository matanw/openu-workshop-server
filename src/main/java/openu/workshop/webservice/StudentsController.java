package openu.workshop.webservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentsController {

@GetMapping("/students")
  public Student GetStudent(@RequestParam int id){
    return new Student(id, "matan");
  }

}
