package ui;

import entity.User;
import entity.UserRole;
import libary.InputHelper;
import service.UserService;
import ui.manager.UiManager;
import ui.tenant.TenantPortalUI;

public class MainUi {
    private static final UserService userService = UserService.getObject();

    public static void main(String[] args) {
        createManagerAccountIfNeeded();
        service.TenantService.getObject();

        while (true) {
            System.out.println("===== DANG NHAP QUAN LY KHU TRO =====");
            System.out.println("Nhap 0 tai ten dang nhap de thoat.");
            System.out.print("Ten dang nhap: ");
            String username = InputHelper.inputString();
            if (username.equals("0")) {
                System.out.println("Ket thuc chuong trinh.");
                return;
            }
            System.out.print("Mat khau: ");
            String password = InputHelper.inputString();

            User user = userService.authenticate(username, password);
            if (user == null) {
                System.out.println("Ten dang nhap hoac mat khau khong dung.");
                continue;
            }

            if (user.getRole() == UserRole.MANAGER) {
                UiManager.showManagerMenu();
            } else if (user.getRole() == UserRole.TENANT) {
                TenantPortalUI.showMenu(user);
            }
        }
    }

    private static void createManagerAccountIfNeeded() {
        if (userService.hasManagerAccount()) {
            return;
        }

        System.out.println("===== TAO TAI KHOAN QUAN LY LAN DAU =====");
        while (!userService.hasManagerAccount()) {
            System.out.print("Tao ten dang nhap quan ly: ");
            String username = InputHelper.inputString();
            System.out.print("Tao mat khau (toi thieu 7 ky tu, co chu hoa, so va ky tu dac biet): ");
            String password = InputHelper.inputString();
            String passwordError = userService.getPasswordValidationError(password);
            if (passwordError != null) {
                System.out.println(passwordError);
                continue;
            }
            System.out.print("Nhap lai mat khau: ");
            String confirmPassword = InputHelper.inputString();
            if (!password.equals(confirmPassword)) {
                System.out.println("Mat khau nhap lai khong khop.");
                continue;
            }
            try {
                userService.createManagerAccount(username, password);
                System.out.println("Da tao tai khoan quan ly. Hay dang nhap de tiep tuc.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
