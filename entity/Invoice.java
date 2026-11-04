package entity;

import java.time.YearMonth;

public class Invoice {
    private long id;
    private long contractId;
    private YearMonth billingMonth;
    private double roomRent;
    private int occupantCount;
    private double electricityFee;
    private double waterFee;
    private InvoiceStatus status;

    public Invoice() {
    }

    public Invoice(long id, long contractId, YearMonth billingMonth, double roomRent,
                   int occupantCount, double electricityFee, double waterFee, InvoiceStatus status) {
        this.id = id;
        this.contractId = contractId;
        this.billingMonth = billingMonth;
        this.roomRent = roomRent;
        this.occupantCount = occupantCount;
        this.electricityFee = electricityFee;
        this.waterFee = waterFee;
        this.status = status;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getContractId() { return contractId; }
    public void setContractId(long contractId) { this.contractId = contractId; }
    public YearMonth getBillingMonth() { return billingMonth; }
    public void setBillingMonth(YearMonth billingMonth) { this.billingMonth = billingMonth; }
    public double getRoomRent() { return roomRent; }
    public void setRoomRent(double roomRent) { this.roomRent = roomRent; }
    public int getOccupantCount() { return occupantCount; }
    public void setOccupantCount(int occupantCount) { this.occupantCount = occupantCount; }
    public double getElectricityFee() { return electricityFee; }
    public void setElectricityFee(double electricityFee) { this.electricityFee = electricityFee; }
    public double getWaterFee() { return waterFee; }
    public void setWaterFee(double waterFee) { this.waterFee = waterFee; }
    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }
}
