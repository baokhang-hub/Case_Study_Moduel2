package ui.manager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import entity.Contract;
import entity.Invoice;
import entity.InvoiceStatus;
import entity.Room;
import entity.RoomIssue;
import entity.RoomIssueStatus;
import entity.Tenant;
import libary.InputHelper;
import libary.erros.DataNotFoundException;
import service.ContractService;
import service.InvoiceService;
import service.ReportService;
import service.RoomIssueService;
import service.RoomService;
import service.TenantService;

public class ReportUI {
    private static final int REMINDER_DAYS = 15;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final ReportService reportService = ReportService.getObject();
    private static final RoomIssueService roomIssueService = RoomIssueService.getObject();
    private static final ContractService contractService = ContractService.getObject();
    private static final RoomService roomService = RoomService.getObject();
    private static final TenantService tenantService = TenantService.getObject();
    private static final InvoiceService invoiceService = InvoiceService.getObject();

    public static void showReportMenu() {
        int choice;
        do {
            System.out.println("===== BAO CAO QUAN LY =====");
            System.out.println("1. Hop dong sap het han");
            System.out.println("2. Tinh hinh hoa don");
            System.out.println("3. Tinh hinh su co phong");
            System.out.println("4. Cap nhat trang thai su co");
            System.out.println("0. Tro ve menu quan ly");
            System.out.print("Chon chuc nang: ");
            choice = InputHelper.inputInt();
            switch (choice) {
                case 1: showExpiringContracts(); break;
                case 2: showInvoiceSituation(); break;
                case 3: showRoomIssues(); break;
                case 4: updateRoomIssueStatus(); break;
                case 0: break;
                default: System.out.println("Lua chon khong hop le.");
            }
        } while (choice != 0);
    }

    private static void showExpiringContracts() {
        List<Contract> contracts = reportService.getExpiringContracts(REMINDER_DAYS);
        System.out.println("===== HOP DONG SAP HET HAN TRONG " + REMINDER_DAYS + " NGAY =====");
        if (contracts.isEmpty()) {
            System.out.println("Khong co hop dong sap het han.");
            return;
        }
        LocalDate today = LocalDate.now();
        for (Contract contract : contracts) {
            Tenant tenant = tenantService.findById(contract.getTenantId());
            Room room = roomService.findById(contract.getRoomId());
            long daysRemaining = ChronoUnit.DAYS.between(today, contract.getEndDate());
            System.out.println("Hop dong: " + contract.getId()
                    + ", Phong: " + room.getName()
                    + ", Khach thue: " + tenant.getFullName()
                    + ", Dien thoai: " + tenant.getPhoneNumber()
                    + ", Het han: " + contract.getEndDate().format(DATE_FORMATTER)
                    + ", Con lai: " + daysRemaining + " ngay");
        }
    }

    private static void showInvoiceSituation() {
        List<Invoice> invoices = reportService.getInvoices();
        System.out.println("===== TINH HINH HOA DON =====");
        if (invoices.isEmpty()) {
            System.out.println("Chua co hoa don nao.");
            return;
        }
        System.out.println("Tong so hoa don: " + invoices.size());
        System.out.println("Da thanh toan: " + reportService.countInvoicesByStatus(InvoiceStatus.PAID)
                + " hoa don, tong thu: " + reportService.sumInvoicesByStatus(InvoiceStatus.PAID) + " VND");
        System.out.println("Chua thanh toan: " + reportService.countInvoicesByStatus(InvoiceStatus.UNPAID)
                + " hoa don, cong no: " + reportService.sumInvoicesByStatus(InvoiceStatus.UNPAID) + " VND");
        System.out.println("===== HOA DON CHUA THANH TOAN =====");
        boolean hasUnpaid = false;
        for (Invoice invoice : invoices) {
            if (invoice.getStatus() == InvoiceStatus.UNPAID) {
                hasUnpaid = true;
                Contract contract = contractService.findById(invoice.getContractId());
                Room room = roomService.findById(contract.getRoomId());
                Tenant tenant = tenantService.findById(contract.getTenantId());
                System.out.println("ID: " + invoice.getId()
                        + ", Phong: " + room.getName()
                        + ", Khach thue: " + tenant.getFullName()
                        + ", Ngay lap: " + invoice.getInvoiceDate().format(DATE_FORMATTER)
                        + ", So tien: " + invoiceService.calculateTotal(invoice) + " VND");
            }
        }
        if (!hasUnpaid) {
            System.out.println("Khong co hoa don chua thanh toan.");
        }
    }

    private static void showRoomIssues() {
        List<RoomIssue> issues = reportService.getUnresolvedRoomIssues();
        System.out.println("===== SU CO PHONG CHUA XU LY =====");
        if (issues.isEmpty()) {
            System.out.println("Khong co su co nao dang cho xu ly.");
            return;
        }
        for (RoomIssue issue : issues) {
            printRoomIssue(issue);
        }
    }

    private static void updateRoomIssueStatus() {
        List<RoomIssue> issues = reportService.getUnresolvedRoomIssues();
        System.out.println("===== SU CO PHONG CHUA XU LY =====");
        if (issues.isEmpty()) {
            System.out.println("Khong co su co nao dang cho xu ly.");
            return;
        }
        for (RoomIssue issue : issues) {
            printRoomIssue(issue);
        }

        System.out.print("Nhap ID su co can cap nhat: ");
        long id = InputHelper.inputLong();
        System.out.println("1. OPEN");
        System.out.println("2. IN_PROGRESS");
        System.out.println("3. RESOLVED");
        System.out.print("Chon trang thai: ");
        int choice = InputHelper.inputInt();
        RoomIssueStatus status;
        switch (choice) {
            case 1: status = RoomIssueStatus.OPEN; break;
            case 2: status = RoomIssueStatus.IN_PROGRESS; break;
            case 3: status = RoomIssueStatus.RESOLVED; break;
            default:
                System.out.println("Trang thai khong hop le.");
                return;
        }
        try {
            roomIssueService.updateStatus(id, status);
            System.out.println("Da cap nhat trang thai su co.");
        } catch (DataNotFoundException e) {
            System.out.println("Khong tim thay su co co ID: " + id);
        }
    }

    private static void printRoomIssue(RoomIssue issue) {
        Room room = roomService.findById(issue.getRoomId());
        Tenant tenant = tenantService.findById(issue.getTenantId());
        System.out.println("ID su co: " + issue.getId()
                + ", Phong: " + room.getName()
                + ", Khach thue: " + tenant.getFullName()
                + ", Ngay bao: " + issue.getReportDate().format(DATE_FORMATTER)
                + ", Trang thai: " + issue.getStatus()
                + ", Mo ta: " + issue.getDescription());
    }
}
