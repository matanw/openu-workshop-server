package openu.workshop.webservice.datatransferobjects;

import java.util.Date;
import openu.workshop.webservice.model.Course;
import openu.workshop.webservice.model.Professor;
import openu.workshop.webservice.model.Task;
import openu.workshop.webservice.model.TaskID;

public class TaskDTO {


  private int id;
  private Date submissionDeadline;
  private Date checkDeadLine;
  private double weightInGrade;

  public TaskDTO(){}


  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Date getSubmissionDeadline() {
    return submissionDeadline;
  }

  public void setSubmissionDeadline(Date submissionDeadline) {
    this.submissionDeadline = submissionDeadline;
  }

  public Date getCheckDeadLine() {
    return checkDeadLine;
  }

  public void setCheckDeadLine(Date checkDeadLine) {
    this.checkDeadLine = checkDeadLine;
  }

  public double getWeightInGrade() {
    return weightInGrade;
  }

  public void setWeightInGrade(double weightInGrade) {
    this.weightInGrade = weightInGrade;
  }

  public static TaskDTO FromModel(Task task){
    TaskDTO taskDTO = new TaskDTO();
    taskDTO.setId(task.getId().taskId);
    taskDTO.setSubmissionDeadline(task.getSubmissionDeadline());
    taskDTO.setCheckDeadLine(task.getCheckDeadLine());
    taskDTO.setWeightInGrade(task.getWeightInGrade());
    return taskDTO;
  }

  public Task ToModel(int courseId){
    Task task = new Task();
    TaskID taskID = new TaskID();
    taskID.taskId =getId();
    taskID.courseId=courseId;
    task.setId(taskID);
    task.setSubmissionDeadline(getSubmissionDeadline());
    task.setCheckDeadLine(getCheckDeadLine());
    task.setWeightInGrade(getWeightInGrade());
    return task;
  }

}
