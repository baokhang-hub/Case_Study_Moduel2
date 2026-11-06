package ui.manager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import entity.Contract;
import entity.ContractStatus;
import entity.Invoice;
import entity.Room;
import entity.Tenant;
import libary.InputHelper;
import libary.erros.DataNotFoundException;
import service.ContractService;
import service.InvoiceService;
import service.RoomService;
import service.TenantService;

public class InvoiceUI {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final InvoiceService invoiceService = InvoiceService.getObject();
    private static final ContractService contractService = ContractService.getObject();
    private static final RoomService roomService = RoomService.getObject();
    private static final TenantService tenantService = TenantService.getObject();

    public static void showInvoiceMenu() {
        int choice;
        do {
            System.out.println("===== QUAN LY HOA DON =====");
            System.out.println("1. Lap hoa don");
            System.out.println("2. Danh dau da thanh toan");
            System.out.println("3. Tim hoa don theo ID");
            System.out.println("4. Xem danh sach hoa don");
            System.out.println("0. Tro ve menu quan ly");
            System.out.print("Chon chuc nang: ");
            choice = InputHelper.inputInt();
            switch (choice) {
                case 1: createInvoice(); break;
                case 2: markAsPaid(); break;
                case 3: findInvoice(); break;
                case 4: showAllInvoices(); break;
                case 0: break;
                default: System.out.println("Lua chon khong hop le.");
            }
        } while (choice != 0);
    }

    private static void createInvoice() {
        if (!showActiveContracts()) {
            System.out.println("Chua co hop dong dang hoat dong.");
            return;
        }
        System.out.print("Nhap ID hop dong: ");
        long contractId = InputHelper.inputLong();
        System.out.print("Nhap ngay lap hoa don (dd/MM/yyyy): ");
        String dateInput = InputHelper.inputString();
        System.out.print("Nhap so kWh dien da su dung: ");
        double electricityUsageKwh = InputHelper.inputDouble();
        try {
            LocalDate invoiceDate = LocalDate.parse(dateInput, DATE_FORMATTER);
            Invoice invoice = invoiceService.createInvoice(contractId, invoiceDate, electricityUsageKwh);
            System.out.println("Da lap hoa don thanh cong:");
            printInvoice(invoice);
        } catch (DateTimeParseException e) {
            System.out.println("Ngay khong hop le. Hay nhap theo dang dd/MM/yyyy.");
        } catch (DataNotFoundException | IllegalArgumentException | IllegalStateException e) {
            System.out.println(e instanceof DataNotFoundException
                    ? "Khong tim thay hop dong hoac phong theo ID da nhap." : e.getMessage());
        }
    }

    private static boolean showActiveContracts() {
        System.out.println("===== HOP DONG DANG HOAT DONG =====");
        boolean found = false;
        for (Contract contract : contractService.getAll()) {
            if (contract.getStatus() == ContractStatus.ACTIVE) {
                Room room = roomService.findById(contract.getRoomId());
                Tenant tenant = tenantService.findById(contract.getTenantId());
                System.out.println("ID hop dong: " + contract.getId() + ", Phong: " + room.getName()
                        + ", Khach thue: " + tenant.getFullName());
                found = true;
            }
        }
        return found;
    }

    private static void markAsPaid() {
        System.out.print("Nhap ID hoa don da thanh toan: ");
        long id = InputHelper.inputLong();
        try {
            invoiceService.markAsPaid(id);
            System.out.println("Da cap nhat trang thai hoa don PAID.");
        } catch (DataNotFoundException | IllegalStateException e) {
            System.out.println(e instanceof DataNotFoundException
                    ? "Khong tim thay hoa don co ID: " + id : e.getMessage());
        }
    }

    private static void findInvoice() {
        System.out.print("Nhap ID hoa don: ");
        long id = InputHelper.inputLong();
        try {
            printInvoice(invoiceService.findById(id));
        } catch (DataNotFoundException e) {
            System.out.println("Khong tim thay hoa don co ID: " + id);
        }
    }

    private static void showAllInvoices() {
        if (invoiceService.getAll().isEmpty()) {
            System.out.println("Chua co hoa don nao.");
            return;
        }
        for (Invoice invoice : invoiceService.getAll()) {
            printInvoice(invoice);
        }
    }

    private static void printInvoice(Invoice invoice) {
        Contract contract = contractService.findById(invoice.getContractId());
        Room room = roomService.findById(contract.getRoomId());
        Tenant tenant = tenantService.findById(contract.getTenantId());
        System.out.println("ID: " + invoice.getId()
                + ", Phong: " + room.getName()
                + ", Khach thue: " + tenant.getFullName()
                + ", Ngay lap: " + invoice.getInvoiceDate().format(DATE_FORMATTER)
                + ", Tien phong: " + invoice.getRoomRent()
                + ", So nguoi: " + invoice.getOccupantCount()
                + ", Dien tieu thu: " + invoice.getElectricityUsageKwh() + " kWh"
                + ", Tien dien: " + invoice.getElectricityFee()
                + ", Tien nuoc: " + invoice.getWaterFee()
                + ", Tong tien: " + invoiceService.calculateTotal(invoice)
                + ", Trang thai: " + invoice.getStatus());
    }
}
