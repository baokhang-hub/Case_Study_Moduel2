package ui.manager;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;

import entity.Contract;
import entity.ContractStatus;
import entity.Room;
import entity.RoomStatus;
import entity.Tenant;
import libary.InputHelper;
import libary.erros.DataNotFoundException;
import service.ContractService;
import service.RoomService;
import service.TenantService;

public class ContractUI {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final ContractService contractService = ContractService.getObject();
    private static final RoomService roomService = RoomService.getObject();
    private static final TenantService tenantService = TenantService.getObject();

    public static void showContractMenu() {
        int choice;
        do {
            System.out.println("===== QUAN LY HOP DONG =====");
            System.out.println("1. Tao hop dong");
            System.out.println("2. Gia han hop dong");
            System.out.println("3. Ket thuc hop dong");
            System.out.println("4. Tim hop dong theo ID");
            System.out.println("5. Xem danh sach hop dong");
            System.out.println("0. Tro ve menu quan ly");
            System.out.print("Chon chuc nang: ");

            choice = InputHelper.inputInt();
            switch (choice) {
                case 1:
                    addContract();
                    break;
                case 2:
                    renewContract();
                    break;
                case 3:
                    endContract();
                    break;
                case 4:
                    findContract();
                    break;
                case 5:
                    getAllContracts();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Lua chon khong hop le. Vui long chon lai.");
            }
        } while (choice != 0);
    }

    public static void addContract() {
        if (!showAvailableRooms()) {
            System.out.println("Hien khong co phong trong.");
            return;
        }
        if (!showTenants()) {
            System.out.println("Chua co khach thue. Hay them khach thue truoc.");
            return;
        }

        System.out.print("Nhap ID phong: ");
        long roomId = InputHelper.inputLong();
        System.out.print("Nhap ID khach thue: ");
        long tenantId = InputHelper.inputLong();
        System.out.print("Nhap so nguoi o: ");
        int occupantCount = InputHelper.inputInt();
        System.out.print("Nhap ngay bat dau (dd/MM/yyyy): ");
        String startDateInput = InputHelper.inputString();
        System.out.print("Nhap ngay ket thuc (dd/MM/yyyy): ");
        String endDateInput = InputHelper.inputString();
        System.out.print("Nhap tien coc: ");
        double deposit = InputHelper.inputDouble();

        try {
            LocalDate startDate = LocalDate.parse(startDateInput, DATE_FORMATTER);
            LocalDate endDate = LocalDate.parse(endDateInput, DATE_FORMATTER);
            long id = System.currentTimeMillis();
            Contract contract = new Contract(id, roomId, tenantId, occupantCount,
                    startDate, endDate, deposit, ContractStatus.ACTIVE);

            contractService.add(contract);
            System.out.println("Da tao hop dong thanh cong. ID hop dong: " + id);
        } catch (DateTimeParseException e) {
            System.out.println("Ngay khong hop le. Hay nhap theo dang dd/MM/yyyy.");
        } catch (DataNotFoundException e) {
            System.out.println("Khong tim thay phong hoac khach thue theo ID da nhap.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    private static boolean showAvailableRooms() {
        System.out.println("===== PHONG TRONG =====");
        boolean found = false;
        for (Room room : roomService.getAll()) {
            if (room.getStatus() == RoomStatus.AVAILABLE) {
                System.out.println("ID: " + room.getId() + ", Ten phong: " + room.getName()
                        + ", Gia thue: " + room.getMonthlyRent());
                found = true;
            }
        }
        return found;
    }

    private static boolean showTenants() {
        System.out.println("===== KHACH THUE =====");
        for (Tenant tenant : tenantService.getAll()) {
            System.out.println("ID: " + tenant.getId() + ", Ho ten: " + tenant.getFullName());
        }
        return !tenantService.getAll().isEmpty();
    }

    public static void renewContract() {
        System.out.print("Nhap ID hop dong can gia han: ");
        long id = InputHelper.inputLong();
        System.out.print("Nhap ngay ket thuc moi (dd/MM/yyyy): ");
        String newEndDateInput = InputHelper.inputString();

        try {
            LocalDate newEndDate = LocalDate.parse(newEndDateInput, DATE_FORMATTER);
            contractService.renewContract(id, newEndDate);
            System.out.println("Da gia han hop dong. Phong van o trang thai OCCUPIED.");
        } catch (DateTimeParseException e) {
            System.out.println("Ngay khong hop le. Hay nhap theo dang dd/MM/yyyy.");
        } catch (DataNotFoundException e) {
            System.out.println("Khong tim thay hop dong co ID: " + id);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void endContract() {
        System.out.print("Nhap ID hop dong can ket thuc: ");
        long id = InputHelper.inputLong();

        try {
            contractService.endContract(id);
            System.out.println("Da ket thuc hop dong. Phong da chuyen sang AVAILABLE.");
        } catch (DataNotFoundException e) {
            System.out.println("Khong tim thay hop dong co ID: " + id);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void findContract() {
        System.out.print("Nhap ID hop dong: ");
        long id = InputHelper.inputLong();

        try {
            printContract(contractService.findById(id));
        } catch (DataNotFoundException e) {
            System.out.println("Khong tim thay hop dong co ID: " + id);
        }
    }

    public static void getAllContracts() {
        System.out.println("===== DANH SACH HOP DONG =====");
        if (contractService.getAll().isEmpty()) {
            System.out.println("Chua co hop dong nao.");
            return;
        }

        for (Contract contract : contractService.getAll()) {
            printContract(contract);
        }
    }

    private static void printContract(Contract contract) {
        System.out.println("ID: " + contract.getId()
                + ", ID phong: " + contract.getRoomId()
                + ", ID khach thue: " + contract.getTenantId()
                + ", So nguoi o: " + contract.getOccupantCount()
                + ", Tu: " + contract.getStartDate().format(DATE_FORMATTER)
                + ", Den: " + contract.getEndDate().format(DATE_FORMATTER)
                + ", Tien coc: " + contract.getDeposit()
                + ", Trang thai: " + contract.getStatus());
    }
}
