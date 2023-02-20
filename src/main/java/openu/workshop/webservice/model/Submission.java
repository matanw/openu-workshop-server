package openu.workshop.webservice.model;

import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Submission {

  @EmbeddedId
  private SubmissionID id;
  private Date submitDate;
  private Integer grade;
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name="Name",column=@Column(name="file_name")),
      @AttributeOverride(name="Data",column=@Column(name="file_data"))
  })
  private FileObject file;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name="Name",column=@Column(name="feedbackFile_name")),
      @AttributeOverride(name="Data",column=@Column(name="feedbackFile_data"))
  })
  private FileObject feedbackFile;

 @ManyToOne
  private Task task;

  public Submission(){}


  public SubmissionID getId() {
    return id;
  }

  public void setId(SubmissionID id) {
    this.id = id;
  }

  public Date getSubmitDate() {
    return submitDate;
  }

  public void setSubmitDate(Date submitDate) {
    this.submitDate = submitDate;
  }

  public Integer getGrade() {
    return grade;
  }

  public void setGrade(Integer grade) {
    this.grade = grade;
  }

  public Task getTask() {
    return task;
  }

  public void setTask(Task task) {
    this.task = task;
  }

  public FileObject getFile() {
    return file;
  }

  public void setFile(FileObject file) {
    this.file = file;
  }

  public FileObject getFeedbackFile() {
    return feedbackFile;
  }

  public void setFeedbackFile(FileObject feedbackFile) {
    this.feedbackFile = feedbackFile;
  }
}