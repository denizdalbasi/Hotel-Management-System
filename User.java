
import java.util.List;

public class User {
    private String username;
    private String password;
    static final String USER_FILE_PATH = "C:\\Users\\Ultimate\\Desktop\\java_proje\\user.txt";

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static User findUser(String username) {
        List<String> lines = FileHandler.readFile(USER_FILE_PATH);
        for (String line : lines) {
            String[] parts = line.split(",");
            if (username.equals(parts[0])) {
                return new User(parts[0], parts[1]);
            }
        }
        return null;
    }

    public static void addUser(String username, String password) {
        FileHandler.appendToFile(USER_FILE_PATH, username + "," + password);
    }
}
