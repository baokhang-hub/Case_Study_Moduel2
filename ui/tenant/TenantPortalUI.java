package ui.tenant;

import java.time.format.DateTimeFormatter;

import entity.Contract;
import entity.ContractStatus;
import entity.Invoice;
import entity.Room;
import entity.RoomIssue;
import entity.User;
import libary.InputHelper;
import service.ContractService;
import service.InvoiceService;
import service.RoomIssueService;
import service.RoomService;
import service.UserService;

public class TenantPortalUI {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final ContractService contractService = ContractService.getObject();
    private static final InvoiceService invoiceService = InvoiceService.getObject();
    private static final RoomService roomService = RoomService.getObject();
    private static final RoomIssueService roomIssueService = RoomIssueService.getObject();
    private static final UserService userService = UserService.getObject();

    public static void showMenu(User user) {
        int choice;
        do {
            System.out.println("===== TAI KHOAN KHACH THUE =====");
            System.out.println("1. Xem phong va hop dong cua toi");
            System.out.println("2. Xem hoa don cua toi");
            System.out.println("3. Bao su co phong");
            System.out.println("4. Doi mat khau");
            System.out.println("0. Dang xuat");
            System.out.print("Chon chuc nang: ");
            choice = InputHelper.inputInt();
            switch (choice) {
                case 1: showMyRoomAndContract(user); break;
                case 2: showMyInvoices(user); break;
                case 3: reportRoomIssue(user); break;
                case 4: changePassword(user); break;
                case 0: break;
                default: System.out.println("Lua chon khong hop le.");
            }
        } while (choice != 0);
    }

    private static Contract findActiveContract(User user) {
        for (Contract contract : contractService.getAll()) {
            if (contract.getTenantId() == user.getTenantId()
                    && contract.getStatus() == ContractStatus.ACTIVE) {
                return contract;
            }
        }
        return null;
    }

    private static void showMyRoomAndContract(User user) {
        Contract contract = findActiveContract(user);
        if (contract == null) {
            System.out.println("Tai khoan nay chua co hop dong thue phong dang hoat dong.");
            return;
        }
        Room room = roomService.findById(contract.getRoomId());
        System.out.println("Ten phong: " + room.getName());
        System.out.println("Gia thue thang: " + room.getMonthlyRent() + " VND");
        System.out.println("Hop dong tu " + contract.getStartDate().format(DATE_FORMATTER)
                + " den " + contract.getEndDate().format(DATE_FORMATTER));
    }

    private static void showMyInvoices(User user) {
        Contract currentContract = findActiveContract(user);
        if (currentContract == null) {
            System.out.println("Tai khoan nay chua co hoa don.");
            return;
        }

        boolean hasInvoice = false;
        for (Invoice invoice : invoiceService.getAll()) {
            if (invoice.getContractId() == currentContract.getId()) {
                hasInvoice = true;
                System.out.println("ID: " + invoice.getId()
                        + ", Ngay lap: " + invoice.getInvoiceDate().format(DATE_FORMATTER)
                        + ", Tong tien: " + invoiceService.calculateTotal(invoice) + " VND"
                        + ", Trang thai: " + invoice.getStatus());
            }
        }
        if (!hasInvoice) {
            System.out.println("Hop dong hien tai chua co hoa don.");
        }
    }

    private static void reportRoomIssue(User user) {
        Contract contract = findActiveContract(user);
        if (contract == null) {
            System.out.println("Chi khach thue co hop dong dang hoat dong moi co the bao su co.");
            return;
        }
        Room room = roomService.findById(contract.getRoomId());
        System.out.println("Phong dang thue: " + room.getName());
        System.out.println("Nhap 0 de huy va quay lai menu tai khoan.");
        System.out.print("Mo ta su co: ");
        String description = InputHelper.inputString();
        if (description.equals("0")) {
            System.out.println("Da huy bao cao su co.");
            return;
        }
        try {
            RoomIssue issue = roomIssueService.reportIssue(room.getId(), user.getTenantId(), description);
            System.out.println("Da gui bao cao su co. Ma su co: " + issue.getId());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void changePassword(User user) {
        while (true) {
            System.out.print("Nhap mat khau moi (toi thieu 7 ky tu, co chu hoa, so va ky tu dac biet): ");
            String newPassword = InputHelper.inputString();
            String validationError = userService.getPasswordChangeError(user, newPassword);
            if (validationError != null) {
                System.out.println(validationError);
                continue;
            }

            System.out.print("Nhap lai mat khau moi: ");
            String confirmPassword = InputHelper.inputString();
            if (!newPassword.equals(confirmPassword)) {
                System.out.println("Mat khau nhap lai khong khop. Hay nhap lai mat khau moi tu dau.");
                continue;
            }
            try {
                userService.changePassword(user, newPassword);
                System.out.println("Da doi mat khau thanh cong.");
                return;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
