package openu.workshop.webservice.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Course {


  @Id
  private int id;
  private String name;

  public Course(){

  }


  public Course(int id, String name) {
    this.id = id;
    this.name = name;
  }

  //todo:professor
  //todo:setted _requirment


  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
