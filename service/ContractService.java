package service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import entity.Contract;
import entity.ContractStatus;
import entity.Room;
import entity.RoomStatus;
import libary.erros.DataNotFoundException;
import repository.ContractRepository;

public class ContractService implements IService<Contract> {
    private List<Contract> contracts;
    private static final ContractService CONTRACT_SERVICE_INSTANCE = new ContractService();
    private final ContractRepository contractRepository = new ContractRepository();
    private final RoomService roomService = RoomService.getObject();
    private final TenantService tenantService = TenantService.getObject();

    public static ContractService getObject() {
        return CONTRACT_SERVICE_INSTANCE;
    }

    public ContractService() {
        this.contracts = contractRepository.read();
        processExpiredContracts();
    }

    public void processExpiredContracts() {
        LocalDate today = LocalDate.now();
        boolean changed = false;
        for (Contract contract : contracts) {
            if (contract.getStatus() == ContractStatus.ACTIVE && contract.getEndDate().isBefore(today)) {
                contract.setStatus(ContractStatus.ENDED);
                Room room = roomService.findById(contract.getRoomId());
                room.setStatus(RoomStatus.AVAILABLE);
                roomService.update(room.getId(), room);
                changed = true;
            }
        }
        if (changed) {
            contractRepository.write(contracts);
        }
    }

    public List<Contract> getContractsExpiringWithin(int days) {
        if (days < 0) {
            throw new IllegalArgumentException("So ngay can bao truoc khong duoc am.");
        }
        processExpiredContracts();
        LocalDate today = LocalDate.now();
        LocalDate limitDate = today.plusDays(days);
        List<Contract> expiringContracts = new ArrayList<>();
        for (Contract contract : contracts) {
            if (contract.getStatus() == ContractStatus.ACTIVE
                    && !contract.getEndDate().isBefore(today)
                    && !contract.getEndDate().isAfter(limitDate)) {
                expiringContracts.add(contract);
            }
        }
        return expiringContracts;
    }

    public boolean hasActiveContractForTenant(long tenantId) {
        for (Contract contract : getAll()) {
            if (contract.getTenantId() == tenantId && contract.getStatus() == ContractStatus.ACTIVE) {
                return true;
            }
        }
        return false;
    }

    private void validateContract(Contract contract) {
        if (contract == null) {
            throw new IllegalArgumentException("Hop dong khong duoc rong.");
        }
        if (contract.getStartDate() == null || contract.getEndDate() == null
                || contract.getEndDate().isBefore(contract.getStartDate())) {
            throw new IllegalArgumentException("Ngay ket thuc phai bang hoac sau ngay bat dau.");
        }
        if (contract.getOccupantCount() <= 0) {
            throw new IllegalArgumentException("So nguoi o phai lon hon 0.");
        }
        if (contract.getDeposit() < 0) {
            throw new IllegalArgumentException("Tien coc khong duoc am.");
        }
    }



    @Override
    public void add(Contract contract) {
        processExpiredContracts();
        validateContract(contract);
        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new IllegalArgumentException("Hop dong moi phai co trang thai ACTIVE.");
        }

        Room room = roomService.findById(contract.getRoomId());
        tenantService.findById(contract.getTenantId());
        if (hasActiveContractForTenant(contract.getTenantId())) {
            throw new IllegalStateException("Khach thue nay da co hop dong dang ACTIVE.");
        }
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new IllegalStateException("Phong dang duoc thue.");
        }

        contracts.add(contract);
        room.setStatus(RoomStatus.OCCUPIED);
        roomService.update(room.getId(), room);
        contractRepository.write(contracts);
    }

    @Override
    public void update(long id, Contract contract) {
        throw new UnsupportedOperationException(
                "Hop dong khong ho tro cap nhat tong quat. Hay dung gia han hoac ket thuc hop dong.");
    }

    public void renewContract(long id, LocalDate newEndDate) {
        processExpiredContracts();
        Contract contract = findById(id);
        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new IllegalStateException("Chi co the gia han hop dong dang ACTIVE.");
        }
        if (newEndDate == null || !newEndDate.isAfter(contract.getEndDate())) {
            throw new IllegalArgumentException("Ngay ket thuc moi phai sau ngay ket thuc hien tai.");
        }

        contract.setEndDate(newEndDate);
        contractRepository.write(contracts);
    }

    public void endContract(long id) {
        processExpiredContracts();
        Contract contract = findById(id);
        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new IllegalStateException("Hop dong nay da ket thuc.");
        }

        Room room = roomService.findById(contract.getRoomId());
        contract.setStatus(ContractStatus.ENDED);
        room.setStatus(RoomStatus.AVAILABLE);
        roomService.update(room.getId(), room);
        contractRepository.write(contracts);
    }

    @Override
    public void delete(long id) {
        int index = findIndexById(id);
        Contract contract = contracts.get(index);
        if (contract.getStatus() == ContractStatus.ACTIVE) {
            throw new IllegalStateException("Hay ket thuc hop dong truoc khi xoa.");
        }

        contracts.remove(index);
        contractRepository.write(contracts);
    }

    @Override
    public List<Contract> getAll() {
        processExpiredContracts();
        return contracts;
    }

    @Override
    public int findIndexById(long id) {
        for (int i = 0; i < contracts.size(); i++) {
            if (contracts.get(i).getId() == id) {
                return i;
            }
        }
        throw new DataNotFoundException();
    }

    @Override
    public Contract findById(long id) {
        processExpiredContracts();
        return contracts.get(findIndexById(id));
    }
}
