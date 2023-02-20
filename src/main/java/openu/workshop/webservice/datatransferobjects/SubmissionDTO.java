package openu.workshop.webservice.datatransferobjects;

import java.util.Date;
import openu.workshop.webservice.model.Submission;


public class SubmissionDTO {
  private Date submitDate;
  private Integer grade;
  private boolean hasFeedbackFile;

  private String studentId;

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

  public boolean isHasFeedbackFile() {
    return hasFeedbackFile;
  }

  public void setHasFeedbackFile(boolean hasFeedbackFile) {
    this.hasFeedbackFile = hasFeedbackFile;
  }

  public String getStudentId() {
    return studentId;
  }

  public void setStudentId(String studentId) {
    this.studentId = studentId;
  }

  public static SubmissionDTO FromModel(Submission submission){
    SubmissionDTO submissionDTO=new SubmissionDTO();
    submissionDTO.setSubmitDate(submission.getSubmitDate());
    submissionDTO.setGrade(submission.getGrade());
    submissionDTO.setHasFeedbackFile(submission.getFeedbackFile()!=null);
    submissionDTO.setStudentId(submission.getId().studentId);
    return submissionDTO;
   }
}