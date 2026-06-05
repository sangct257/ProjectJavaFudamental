package ra.yourprojectname;

import ra.yourprojectname.presentation.login.LoginView;
import ra.yourprojectname.until.DatabaseSeeder;

public class Main {
    public static void main(String[] args) {
        DatabaseSeeder.seedData();
        new LoginView().showLoginInfo();
    }
}
