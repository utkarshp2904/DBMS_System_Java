
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class login {
    static String UserName;
    static String Password;

    public String verification(String userName, String password) {
        boolean flag = false;
        String path = "login.txt";

        try {
            Scanner sc = new Scanner(new File(path));
            sc.useDelimiter("[,\n]");
            while (sc.hasNext()) {
                UserName = sc.next();
                Password = sc.next();

                if (UserName.trim().equals(userName.trim()) && Password.trim().equals(password.trim())) {
                    flag = true;
                }
            }
            sc.close();
            if (flag) {
                System.out.println("Login Successful !!");
                return userName;
            } else {
                System.out.println("Invalid Credentials !!");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }
}
