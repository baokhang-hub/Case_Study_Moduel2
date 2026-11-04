package entity;

public class Room {
    private long id;
    private String name;
    private double monthlyRent;
    private RoomStatus status;

    public Room() {
        // Default constructor
    }

    public Room(long id, String name, double monthlyRent) {
        this.id = id;
        this.name = name;
        this.monthlyRent = monthlyRent;
        this.status = RoomStatus.AVAILABLE; // Default status
    }

    public Room(long id, String name, double monthlyRent, RoomStatus status) {
        this.id = id;
        this.name = name;
        this.monthlyRent = monthlyRent;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(double monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }
}
