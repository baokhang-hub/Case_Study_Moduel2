package repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
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
                        + invoice.getInvoiceDate() + "," + invoice.getRoomRent() + ","
                        + invoice.getOccupantCount() + "," + invoice.getElectricityUsageKwh() + ","
                        + invoice.getElectricityFee() + ","
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
                double electricityUsageKwh;
                double electricityFee;
                double waterFee;
                InvoiceStatus status;
                LocalDate invoiceDate;
                if (data.length == 8) {
                    // Compatibility with invoices saved before electricity usage was recorded.
                    invoiceDate = YearMonth.parse(data[2]).atDay(1);
                    electricityFee = Double.parseDouble(data[5]);
                    electricityUsageKwh = electricityFee / 2_000;
                    waterFee = Double.parseDouble(data[6]);
                    status = InvoiceStatus.valueOf(data[7]);
                } else {
                    // Older files saved only the billing month (yyyy-MM).
                    invoiceDate = data[2].length() == 7
                            ? YearMonth.parse(data[2]).atDay(1)
                            : LocalDate.parse(data[2]);
                    electricityUsageKwh = Double.parseDouble(data[5]);
                    electricityFee = Double.parseDouble(data[6]);
                    waterFee = Double.parseDouble(data[7]);
                    status = InvoiceStatus.valueOf(data[8]);
                }
                invoices.add(new Invoice(
                        Long.parseLong(data[0]), Long.parseLong(data[1]), invoiceDate,
                        Double.parseDouble(data[3]), Integer.parseInt(data[4]),
                        electricityUsageKwh, electricityFee, waterFee, status));
            }
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }
        return invoices;
    }
}
