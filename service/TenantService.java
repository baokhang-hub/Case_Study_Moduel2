package service;

import java.util.List;

import entity.Tenant;
import libary.erros.DataNotFoundException;
import repository.TenantRepository;

public class TenantService implements IService<Tenant> {
    private List<Tenant> tenants;
    private static final TenantService TENANT_SERVICE_INSTANCE = new TenantService();
    private final TenantRepository tenantRepository = new TenantRepository();

    public static TenantService getObject() {
        return TENANT_SERVICE_INSTANCE;
    }

    public TenantService() {
        this.tenants = tenantRepository.read();
    }

    @Override
    public void add(Tenant tenant) {
        tenants.add(tenant);
        tenantRepository.write(tenants);
    }

    @Override
    public void update(long id, Tenant updatedTenant) {
        int index = findIndexById(id);
        tenants.set(index, updatedTenant);
        tenantRepository.write(tenants);
    }

    @Override
    public void delete(long id) {
        int index = findIndexById(id);
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
}
