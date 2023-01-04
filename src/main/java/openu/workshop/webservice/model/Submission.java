package openu.workshop.webservice.model;

import java.util.Date;

public class Submission {
//todo: student
  private Date submitDate;
  private Integer grade;

  public Submission(Date submitDate, Integer grade) {
    this.submitDate = submitDate;
    this.grade = grade;
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
}