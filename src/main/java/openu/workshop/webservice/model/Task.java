package openu.workshop.webservice.model;

import java.util.Date;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Task {
  @EmbeddedId
  private TaskID id;
  private Date submissionDeadline;
  private Date checkDeadLine;
  private double weightInGrade;

  @Embedded
  private FileObject file;

  public Task(){}

  @ManyToOne
  private Course course;

  public Task(TaskID id, Date submissionDeadline, Date checkDeadLine, double weightInGrade) {
    this.id = id;
    this.submissionDeadline = submissionDeadline;
    this.checkDeadLine = checkDeadLine;
    this.weightInGrade = weightInGrade;
  }

  public TaskID getId() {
    return id;
  }

  public void setId(TaskID id) {
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

  public FileObject getFile() {
    return file;
  }

  public void setFile(FileObject file) {
    this.file = file;
  }
}
