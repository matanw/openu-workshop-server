package openu.workshop.webservice.auth;

public class LoginInformation{

  public LoginInformation(String username, String password, LoginType loginType) {
    this.username = username;
    this.password = password;
    this.loginType = loginType;
  }

  public String username;
  public String password;

  public LoginType loginType;
}