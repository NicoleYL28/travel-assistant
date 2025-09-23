package oocl.travelassistant.dto;

public class UserLoginDTO {
    private String usernameOrEmail; // 必填
    private String password;        // 必填

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
