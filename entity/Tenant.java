package entity;

public class Tenant {
    private long id;
    private String fullName;
    private String phoneNumber;
    private String identityNumber;

    public Tenant() {
        // Default constructor
    }

    public Tenant(long id, String fullName, String phoneNumber, String identityNumber) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.identityNumber = identityNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }
}
