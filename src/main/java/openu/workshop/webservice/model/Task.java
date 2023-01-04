package openu.workshop.webservice.model;

import java.util.Date;

public class Task {

  private int id;
  private Date deadline; //todo:fields

  public Task(int id, Date deadline) {
    this.id = id;
    this.deadline = deadline;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Date getDeadline() {
    return deadline;
  }

  public void setDeadline(Date deadline) {
    this.deadline = deadline;
  }
}
