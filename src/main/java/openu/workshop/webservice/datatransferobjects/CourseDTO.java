package openu.workshop.webservice.datatransferobjects;

import java.util.Collection;
import openu.workshop.webservice.model.Course;
import openu.workshop.webservice.model.Professor;

public class CourseDTO {
  private int id;
  private String name;
  private String professorId;
  private boolean tasksSet;

  public CourseDTO(){

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

  public String getProfessorId() {
    return professorId;
  }

  public void setProfessorId(String professorId) {
    this.professorId = professorId;
  }

  public boolean isTasksSet() {
    return tasksSet;
  }

  public void setTasksSet(boolean tasksSet) {
    this.tasksSet = tasksSet;
  }

  public static CourseDTO FromModel(Course course){
    CourseDTO courseDTO = new CourseDTO();
    courseDTO.setId(course.getId());
    courseDTO.setProfessorId(course.getProfessor().getId());
    courseDTO.setName(course.getName());
    courseDTO.setTasksSet(course.isTasksSet());
    return courseDTO;
  }
}

