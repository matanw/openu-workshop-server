package openu.workshop.webservice.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Task {

  @Id
  private int id;
  private Date submissionDeadline;
  private Date checkDeadLine;
  private double weightInGrade;

  public Task(){}

  public Task(int id, Date submissionDeadline, Date checkDeadLine, double weightInGrade) {
    this.id = id;
    this.submissionDeadline = submissionDeadline;
    this.checkDeadLine = checkDeadLine;
    this.weightInGrade = weightInGrade;
  }

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
}
