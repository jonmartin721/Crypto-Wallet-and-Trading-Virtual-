import java.util.HashMap;

public class LoginInfo {


    private HashMap<String, String> usersAndPasswords;

    LoginInfo() {

        //entering temporary username and password for us to test with
        String testUsername = "admin";
        String testPassword = "popcorn";
        usersAndPasswords.put(testUsername, testPassword);


    }

    public boolean isAuthenticated(String username, String password) {
        return usersAndPasswords.containsKey(username) && usersAndPasswords.get(username).equals(password);
    }
}
