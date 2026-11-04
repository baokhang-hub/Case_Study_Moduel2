package ui.manager;

import libary.InputHelper;
import service.ContractService;

public class UiManager {

    public static  void showManagerMenu() {
        int choice;
        do {
            ContractService.getObject().processExpiredContracts();
            System.out.println("===== QUAN LY KHU TRO =====");
            System.out.println("1. Quan ly phong");
            System.out.println("2. Quan ly khach thue");
            System.out.println("3. Quan ly hop dong");
            System.out.println("4. Quan ly hoa don");
            System.out.println("5. Xem bao cao");
            System.out.println("0. Dang xuat");
            System.out.print("Chon chuc nang: ");

            choice = InputHelper.inputInt();

            switch (choice) {
                case 1:
                    ui.manager.UiRoom.showRoomMenu();
                    break;
                case 2:
                    ui.manager.TenantUI.showTenantMenu();
                    break;
                case 3:
                    ui.manager.ContractUI.showContractMenu();
                    break;
                case 4:
                    ui.manager.InvoiceUI.showInvoiceMenu();
                    break;
                case 5:
                    ui.manager.ReportUI.showReportMenu();
                    break;
                case 0:
                    System.out.println("Dang xuat");
                    break;
                default:
                    System.out.println("Lua chon khong hop le. Vui long chon lai.");
            }
        } while (choice != 0);

    }
}
