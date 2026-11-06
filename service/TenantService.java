package service;

import java.util.List;

import entity.Tenant;
import libary.erros.DataNotFoundException;
import repository.TenantRepository;

public class TenantService implements IService<Tenant> {
    private List<Tenant> tenants;
    private static final TenantService TENANT_SERVICE_INSTANCE = new TenantService();
    private final TenantRepository tenantRepository = new TenantRepository();
    private final UserService userService = UserService.getObject();

    public static TenantService getObject() {
        return TENANT_SERVICE_INSTANCE;
    }

    public TenantService() {
        this.tenants = tenantRepository.read();
        for (Tenant tenant : tenants) {
            userService.ensureTenantAccount(tenant);
        }
    }

    @Override
    public void add(Tenant tenant) {
        validateTenant(tenant);
        for (Tenant existingTenant : tenants) {
            if (existingTenant.getIdentityNumber().equals(tenant.getIdentityNumber())) {
                throw new IllegalArgumentException("CCCD/giay to nay da duoc dang ky.");
            }
        }
        userService.createTenantAccount(tenant);
        tenants.add(tenant);
        tenantRepository.write(tenants);
    }

    @Override
    public void update(long id, Tenant updatedTenant) {
        validateTenant(updatedTenant);
        int index = findIndexById(id);
        for (Tenant tenant : tenants) {
            if (tenant.getId() != id && tenant.getIdentityNumber().equals(updatedTenant.getIdentityNumber())) {
                throw new IllegalArgumentException("CCCD/giay to nay da duoc dang ky.");
            }
        }
        userService.updateTenantCredentials(id, updatedTenant.getIdentityNumber(), updatedTenant.getPhoneNumber());
        tenants.set(index, updatedTenant);
        tenantRepository.write(tenants);
    }

    @Override
    public void delete(long id) {
        int index = findIndexById(id);
        userService.deleteTenantAccount(id);
        tenants.remove(index);
        tenantRepository.write(tenants);
    }

    @Override
    public List<Tenant> getAll() {
        return tenants;
    }

    @Override
    public int findIndexById(long id) {
        for (int i = 0; i < tenants.size(); i++) {
            if (tenants.get(i).getId() == id) {
                return i;
            }
        }
        throw new DataNotFoundException();
    }

    @Override
    public Tenant findById(long id) {
        return tenants.get(findIndexById(id));
    }

    private void validateTenant(Tenant tenant) {
        if (tenant == null) {
            throw new IllegalArgumentException("Khach thue khong duoc rong.");
        }
        if (tenant.getFullName() == null || tenant.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Ho ten khach thue khong duoc de trong.");
        }
        if (tenant.getIdentityNumber() == null || !tenant.getIdentityNumber().trim().matches("[0-9]{12}")) {
            throw new IllegalArgumentException("CCCD phai gom dung 12 chu so.");
        }
        if (tenant.getPhoneNumber() == null || tenant.getPhoneNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("So dien thoai khong duoc de trong.");
        }
        tenant.setIdentityNumber(tenant.getIdentityNumber().trim());
        tenant.setPhoneNumber(normalizeAndValidatePhoneNumber(tenant.getPhoneNumber()));
    }

    private String normalizeAndValidatePhoneNumber(String input) {
        String phoneNumber = input.replaceAll("[\\s-]", "");
        if (phoneNumber.startsWith("+84")) {
            phoneNumber = "0" + phoneNumber.substring(3);
        }
        boolean validMobile = phoneNumber.matches("0[35789][0-9]{8}");
        boolean validLandline = phoneNumber.matches("02[0-9]{8,9}");
        if (!validMobile && !validLandline) {
            throw new IllegalArgumentException(
                    "So dien thoai khong hop le. Nhap so di dong 10 so hoac so ban 10-11 so cua Viet Nam.");
        }
        return phoneNumber;
    }
}
