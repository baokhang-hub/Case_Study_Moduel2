package service;

import java.time.YearMonth;
import java.util.List;

import entity.Contract;
import entity.ContractStatus;
import entity.Invoice;
import entity.InvoiceStatus;
import entity.Room;
import libary.erros.DataNotFoundException;
import repository.InvoiceRepository;

public class InvoiceService implements IService<Invoice> {
    private static final double ELECTRICITY_FEE_PER_PERSON = 100_000;
    private static final double WATER_FEE_PER_PERSON = 100_000;

    private final InvoiceRepository invoiceRepository = new InvoiceRepository();
    private final ContractService contractService = ContractService.getObject();
    private final RoomService roomService = RoomService.getObject();
    private final List<Invoice> invoices = invoiceRepository.read();

    private static final InvoiceService INSTANCE = new InvoiceService();

    public static InvoiceService getObject() {
        return INSTANCE;
    }

    public InvoiceService() {
    }

    public Invoice createInvoice(long contractId, YearMonth billingMonth) {
        if (billingMonth == null) {
            throw new IllegalArgumentException("Thang lap hoa don khong duoc de trong.");
        }
        Contract contract = contractService.findById(contractId);
        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new IllegalStateException("Chi lap hoa don cho hop dong dang ACTIVE.");
        }
        YearMonth startMonth = YearMonth.from(contract.getStartDate());
        YearMonth endMonth = YearMonth.from(contract.getEndDate());
        if (billingMonth.isBefore(startMonth) || billingMonth.isAfter(endMonth)) {
            throw new IllegalArgumentException("Thang hoa don phai nam trong thoi han hop dong.");
        }
        for (Invoice invoice : invoices) {
            if (invoice.getContractId() == contractId && invoice.getBillingMonth().equals(billingMonth)) {
                throw new IllegalStateException("Hop dong nay da co hoa don trong thang da chon.");
            }
        }

        Room room = roomService.findById(contract.getRoomId());
        int people = contract.getOccupantCount();
        Invoice invoice = new Invoice(System.currentTimeMillis(), contractId, billingMonth,
                room.getMonthlyRent(), people,
                people * ELECTRICITY_FEE_PER_PERSON, people * WATER_FEE_PER_PERSON,
                InvoiceStatus.UNPAID);
        invoices.add(invoice);
        invoiceRepository.write(invoices);
        return invoice;
    }

    public void markAsPaid(long id) {
        Invoice invoice = findById(id);
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("Hoa don nay da duoc thanh toan.");
        }
        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.write(invoices);
    }

    public double calculateTotal(Invoice invoice) {
        return invoice.getRoomRent() + invoice.getElectricityFee() + invoice.getWaterFee();
    }

    @Override
    public void add(Invoice invoice) {
        throw new UnsupportedOperationException("Hay dung createInvoice de lap hoa don.");
    }

    @Override
    public void update(long id, Invoice invoice) {
        throw new UnsupportedOperationException("Hoa don khong ho tro cap nhat tong quat.");
    }

    @Override
    public void delete(long id) {
        throw new UnsupportedOperationException("Khong ho tro xoa hoa don.");
    }

    @Override
    public List<Invoice> getAll() {
        return invoices;
    }

    @Override
    public int findIndexById(long id) {
        for (int i = 0; i < invoices.size(); i++) {
            if (invoices.get(i).getId() == id) {
                return i;
            }
        }
        throw new DataNotFoundException();
    }

    @Override
    public Invoice findById(long id) {
        return invoices.get(findIndexById(id));
    }
}
