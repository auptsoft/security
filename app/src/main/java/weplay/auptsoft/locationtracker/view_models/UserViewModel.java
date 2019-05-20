package weplay.auptsoft.locationtracker.view_models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weplay.auptsoft.locationtracker.models.User;

/**
 * Created by Andrew on 5.2.19.
 */

public class UserViewModel {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;
    private String confirmPassword;

    private String firstNameMsg;
    private String lastNameMsg;
    private String emailErrorMsg;
    private String phoneNumberMsg;
    private String passwordMsg;
    private String confirmPasswordMsg;

    private boolean isValidated;

    public boolean validateFirstName() {
        if(getFirstName().length() <= 2) {
            firstNameMsg = "First Name too short";

            return false;
        }
        firstNameMsg = "";
        return true;
    }

    public boolean validateLastName() {
        if(getLastName().length() <= 2) {
            lastNameMsg = "Last Name too short";
            return  false;
        }

        lastNameMsg = "";
        return true;
    }

    public boolean validateEmail() {
        Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPattern.matcher(getEmail());

        if (matcher.find()) {
            emailErrorMsg = "";
            return true;
        } else  {
            emailErrorMsg = "invalid email";
            return false;
        }
    }

    public boolean validatePhoneNumber() {
        Pattern phoneNumberPattern = Pattern.compile("\\d{6,15}");

        Matcher matcher = phoneNumberPattern.matcher(getPhoneNumber());
        if (getPhoneNumber().length() < 6 || getPhoneNumber().length()> 15) {
            phoneNumberMsg = "invalid Phone number";
            return false;
        } else {
            phoneNumberMsg = "";
            return true;
        }
    }

    public boolean validatePassword() {
        if (getPassword().length() < 6) {
            passwordMsg = "Password too short. At least 6 characters is required";
            return false;
        } else {
            passwordMsg = "";
            return true;
        }
    }

    public boolean validateConfirmPassword() {
        if (getPassword().equals(getConfirmPassword())) {
            confirmPasswordMsg = "";
            return true;
        } else {
            confirmPasswordMsg = "Password do not match";
            return false;
        }
    }

    public boolean getIsValidated() {
        return validateFirstName() & validateLastName() & validateEmail() & validatePhoneNumber() & validatePassword() & validateConfirmPassword();
    }

    public UserViewModel(User user) {
        this();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
    }

    public UserViewModel(String firstName, String lastName, String email, String phoneNumber, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public UserViewModel() {
        this("", "", "", "", "", "", "", "", "", "", "", "");
    }

    public UserViewModel(String firstName, String lastName, String email, String phoneNumber, String password, String confirmPassword, String firstNameMsg, String lastNameMsg, String emailErrorMsg, String phoneNumberMsg, String passwordMsg, String confirmPasswordMsg) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;

        this.confirmPassword = confirmPassword;
        this.firstNameMsg = firstNameMsg;
        this.lastNameMsg = lastNameMsg;
        this.emailErrorMsg = emailErrorMsg;
        this.phoneNumberMsg = phoneNumberMsg;
        this.passwordMsg = passwordMsg;
        this.confirmPasswordMsg = confirmPasswordMsg;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        validateFirstName();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        validateLastName();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        validateEmail();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        validatePhoneNumber();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        validatePassword();
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
        validateConfirmPassword();
    }

    public String getFirstNameMsg() {
        return firstNameMsg;
    }

    public void setFirstNameMsg(String firstNameMsg) {
        this.firstNameMsg = firstNameMsg;
    }

    public String getLastNameMsg() {
        return lastNameMsg;
    }

    public void setLastNameMsg(String lastNameMsg) {
        this.lastNameMsg = lastNameMsg;
    }

    public String getEmailErrorMsg() {
        return emailErrorMsg;
    }

    public void setEmailErrorMsg(String emailErrorMsg) {
        this.emailErrorMsg = emailErrorMsg;
    }

    public String getPhoneNumberMsg() {
        return phoneNumberMsg;
    }

    public void setPhoneNumberMsg(String phoneNumberMsg) {
        this.phoneNumberMsg = phoneNumberMsg;
    }

    public String getPasswordMsg() {
        return passwordMsg;
    }

    public void setPasswordMsg(String passwordMsg) {
        this.passwordMsg = passwordMsg;
    }

    public String getConfirmPasswordMsg() {
        return confirmPasswordMsg;
    }

    public void setConfirmPasswordMsg(String confirmPasswordMsg) {
        this.confirmPasswordMsg = confirmPasswordMsg;
    }

    public static String toJson(UserViewModel userViewModel) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("firstName", userViewModel.getFirstName());
        jsonObject.accumulate("lastName", userViewModel.getLastName());
        jsonObject.accumulate("email", userViewModel.getEmail());
        jsonObject.accumulate("phoneNumber", userViewModel.getPhoneNumber());
        jsonObject.accumulate("password", userViewModel.getPassword());

        return jsonObject.toString();
    }

    public static UserViewModel fromJson(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        String firstName = jsonObject.getString("firstName");
        String lastName = jsonObject.getString("lastName");
        String email = jsonObject.getString("email");
        String phoneNumber = jsonObject.getString("phoneNumber");
        String password = jsonObject.getString("password");

        return new UserViewModel(firstName, lastName, email, phoneNumber, password);
    }
}
