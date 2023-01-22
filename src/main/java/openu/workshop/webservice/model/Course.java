package openu.workshop.webservice.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Course {


  @Id
  private int id;
  private String name;

  @ManyToOne
  private Professor professor;

  private boolean tasksSet;

  public Course(){

  }


  public Course(int id, String name,Professor professor) {
    this.id = id;
    this.name = name;
    this.professor=professor;
    this.tasksSet=false;
  }

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

  public Professor getProfessor() {
    return professor;
  }

  public void setProfessor(Professor professor) {
    this.professor = professor;
  }

  public boolean isTasksSet() {
    return tasksSet;
  }

  public void setTasksSet(boolean tasksSet) {
    this.tasksSet = tasksSet;
  }
}

