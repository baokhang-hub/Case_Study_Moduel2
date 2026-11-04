package service;

import java.util.ArrayList;
import java.util.List;

import entity.Contract;
import entity.Invoice;
import entity.InvoiceStatus;
import entity.RoomIssue;
import entity.RoomIssueStatus;

public class ReportService {
    private static final ReportService INSTANCE = new ReportService();
    private final ContractService contractService = ContractService.getObject();
    private final InvoiceService invoiceService = InvoiceService.getObject();
    private final RoomIssueService roomIssueService = RoomIssueService.getObject();

    public static ReportService getObject() {
        return INSTANCE;
    }

    public List<Contract> getExpiringContracts(int withinDays) {
        return contractService.getContractsExpiringWithin(withinDays);
    }

    public List<Invoice> getInvoices() {
        return invoiceService.getAll();
    }

    public int countInvoicesByStatus(InvoiceStatus status) {
        int count = 0;
        for (Invoice invoice : invoiceService.getAll()) {
            if (invoice.getStatus() == status) {
                count++;
            }
        }
        return count;
    }

    public double sumInvoicesByStatus(InvoiceStatus status) {
        double total = 0;
        for (Invoice invoice : invoiceService.getAll()) {
            if (invoice.getStatus() == status) {
                total += invoiceService.calculateTotal(invoice);
            }
        }
        return total;
    }

    public List<RoomIssue> getUnresolvedRoomIssues() {
        List<RoomIssue> unresolved = new ArrayList<>();
        for (RoomIssue issue : roomIssueService.getAll()) {
            if (issue.getStatus() != RoomIssueStatus.RESOLVED) {
                unresolved.add(issue);
            }
        }
        return unresolved;
    }
}
