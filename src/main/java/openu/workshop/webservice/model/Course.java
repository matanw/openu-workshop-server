package openu.workshop.webservice.model;

import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Course {


  @Id
  private int id;
  private String name;

  @ManyToOne
  private Professor professor;

  private boolean tasksSet;

  @OneToMany
  private Collection<Task> tasks;

  @OneToMany(mappedBy = "course")
  private  Collection<Registration> registrations;

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

  public Collection<Task> getTasks() {
    return tasks;
  }

  public void setTasks(Collection<Task> tasks) {
    this.tasks = tasks;
  }

  public Collection<Registration> getRegistrations() {
    return registrations;
  }

  public void setRegistrations(
      Collection<Registration> registrations) {
    this.registrations = registrations;
  }
}

