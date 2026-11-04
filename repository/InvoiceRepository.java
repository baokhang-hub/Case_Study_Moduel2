package repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import entity.Invoice;
import entity.InvoiceStatus;

public class InvoiceRepository implements IRepository<Invoice> {
    private static final String FILE_NAME = "data/invoice.csv";

    @Override
    public void write(List<Invoice> invoices) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Invoice invoice : invoices) {
                writer.write(invoice.getId() + "," + invoice.getContractId() + ","
                        + invoice.getBillingMonth() + "," + invoice.getRoomRent() + ","
                        + invoice.getOccupantCount() + "," + invoice.getElectricityFee() + ","
                        + invoice.getWaterFee() + "," + invoice.getStatus());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    @Override
    public List<Invoice> read() {
        List<Invoice> invoices = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                invoices.add(new Invoice(
                        Long.parseLong(data[0]), Long.parseLong(data[1]), YearMonth.parse(data[2]),
                        Double.parseDouble(data[3]), Integer.parseInt(data[4]),
                        Double.parseDouble(data[5]), Double.parseDouble(data[6]),
                        InvoiceStatus.valueOf(data[7])));
            }
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }
        return invoices;
    }
}
