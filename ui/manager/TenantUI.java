package ui.manager;

import entity.Tenant;
import libary.InputHelper;
import libary.erros.DataNotFoundException;
import service.TenantService;

public class TenantUI {
    private static final TenantService tenantService = TenantService.getObject();

    public static void showTenantMenu() {
        int choice;
        do {
            System.out.println("===== QUAN LY KHACH THUE =====");
            System.out.println("1. Them khach thue");
            System.out.println("2. Sua thong tin khach thue");
            System.out.println("3. Xoa khach thue");
            System.out.println("4. Tim khach thue theo ID");
            System.out.println("5. Xem danh sach khach thue");
            System.out.println("0. Tro ve menu quan ly");
            System.out.print("Chon chuc nang: ");

            choice = InputHelper.inputInt();

            switch (choice) {
                case 1:
                    addTenant();
                    break;
                case 2:
                    updateTenant();
                    break;
                case 3:
                    deleteTenant();
                    break;
                case 4:
                    findTenant();
                    break;
                case 5:
                    getAllTenants();
                    break;
                case 0:
                    ui.manager.UiManager.showManagerMenu();
                    break;
                default:
                    System.out.println("Lua chon khong hop le. Vui long chon lai.");
            }
        } while (choice != 0);
    }

    public static void addTenant() {
        System.out.println("===== THEM KHACH THUE =====");
        long id = System.currentTimeMillis();

        System.out.print("Nhap ho ten: ");
        String fullName = InputHelper.inputString();
        System.out.print("Nhap so dien thoai: ");
        String phoneNumber = InputHelper.inputString();
        System.out.print("Nhap so CCCD/giay to: ");
        String identityNumber = InputHelper.inputString();

        tenantService.add(new Tenant(id, fullName, phoneNumber, identityNumber));
        System.out.println("Da them khach thue thanh cong. ID: " + id);
    }

    public static void updateTenant() {
        System.out.println("===== SUA THONG TIN KHACH THUE =====");
        System.out.print("Nhap ID khach thue can sua: ");
        long id = InputHelper.inputLong();

        try {
            Tenant tenant = tenantService.findById(id);

            System.out.print("Nhap ho ten moi (hien tai: " + tenant.getFullName() + "): ");
            String fullName = InputHelper.inputString();
            System.out.print("Nhap so dien thoai moi (hien tai: " + tenant.getPhoneNumber() + "): ");
            String phoneNumber = InputHelper.inputString();
            System.out.print("Nhap so CCCD/giay to moi (hien tai: " + tenant.getIdentityNumber() + "): ");
            String identityNumber = InputHelper.inputString();

            tenantService.update(id, new Tenant(id, fullName, phoneNumber, identityNumber));
            System.out.println("Da cap nhat khach thue thanh cong!");
        } catch (DataNotFoundException e) {
            System.out.println("Khong tim thay khach thue co ID: " + id);
        }
    }

    public static void deleteTenant() {
        System.out.println("===== XOA KHACH THUE =====");
        System.out.print("Nhap ID khach thue can xoa: ");
        long id = InputHelper.inputLong();

        try {
            System.out.println("Ban co chac muon xoa khach thue co ID: " + id + "? (Y/N)");
            String confirmation = InputHelper.inputString();
            if (confirmation.equalsIgnoreCase("Y")) {
                tenantService.delete(id);
                System.out.println("Da xoa khach thue thanh cong!");
            }
        } catch (DataNotFoundException e) {
            System.out.println("Khong tim thay khach thue co ID: " + id);
        }
    }

    public static void findTenant() {
        System.out.println("===== TIM KHACH THUE =====");
        System.out.print("Nhap ID khach thue: ");
        long id = InputHelper.inputLong();

        try {
            printTenant(tenantService.findById(id));
        } catch (DataNotFoundException e) {
            System.out.println("Khong tim thay khach thue co ID: " + id);
        }
    }

    public static void getAllTenants() {
        System.out.println("===== DANH SACH KHACH THUE =====");
        if (tenantService.getAll().isEmpty()) {
            System.out.println("Chua co khach thue nao.");
            return;
        }

        int index = 1;
        for (Tenant tenant : tenantService.getAll()) {
            System.out.print(index + ". ");
            printTenant(tenant);
            index++;
        }
    }

    private static void printTenant(Tenant tenant) {
        System.out.println("ID: " + tenant.getId()
                + ", Ho ten: " + tenant.getFullName()
                + ", So dien thoai: " + tenant.getPhoneNumber()
                + ", So CCCD/giay to: " + tenant.getIdentityNumber());
    }
}
