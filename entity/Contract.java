package entity;

import java.time.LocalDate;

public class Contract {
    private long id;
    private long roomId;
    private long tenantId;
    private int occupantCount;
    private LocalDate startDate;
    private LocalDate endDate;
    private double deposit;
    private ContractStatus status;

    public Contract() {
        // Default constructor
    }

    public Contract(long id, long roomId, long tenantId, int occupantCount,
                    LocalDate startDate, LocalDate endDate, double deposit,
                    ContractStatus status) {
        this.id = id;
        this.roomId = roomId;
        this.tenantId = tenantId;
        this.occupantCount = occupantCount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.deposit = deposit;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public long getTenantId() {
        return tenantId;
    }

    public void setTenantId(long tenantId) {
        this.tenantId = tenantId;
    }

    public int getOccupantCount() {
        return occupantCount;
    }

    public void setOccupantCount(int occupantCount) {
        this.occupantCount = occupantCount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public double getDeposit() {
        return deposit;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    public ContractStatus getStatus() {
        return status;
    }

    public void setStatus(ContractStatus status) {
        this.status = status;
    }
}
