package entity;

import java.time.LocalDate;

public class RoomIssue {
    private long id;
    private long roomId;
    private long tenantId;
    private LocalDate reportDate;
    private RoomIssueStatus status;
    private String description;

    public RoomIssue() {
    }

    public RoomIssue(long id, long roomId, long tenantId, LocalDate reportDate,
                     RoomIssueStatus status, String description) {
        this.id = id;
        this.roomId = roomId;
        this.tenantId = tenantId;
        this.reportDate = reportDate;
        this.status = status;
        this.description = description;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getRoomId() { return roomId; }
    public void setRoomId(long roomId) { this.roomId = roomId; }
    public long getTenantId() { return tenantId; }
    public void setTenantId(long tenantId) { this.tenantId = tenantId; }
    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
    public RoomIssueStatus getStatus() { return status; }
    public void setStatus(RoomIssueStatus status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
