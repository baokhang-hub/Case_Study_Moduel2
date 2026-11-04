package service;

import java.time.LocalDate;
import java.util.List;

import entity.Contract;
import entity.ContractStatus;
import entity.RoomIssue;
import entity.RoomIssueStatus;
import libary.erros.DataNotFoundException;
import repository.RoomIssueRepository;

public class RoomIssueService {
    private final RoomIssueRepository repository = new RoomIssueRepository();
    private final ContractService contractService = ContractService.getObject();
    private final List<RoomIssue> issues = repository.read();
    private static final RoomIssueService INSTANCE = new RoomIssueService();

    public static RoomIssueService getObject() {
        return INSTANCE;
    }

    public RoomIssueService() {
    }

    public RoomIssue reportIssue(long roomId, long tenantId, String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Mo ta su co khong duoc de trong.");
        }
        boolean hasActiveContract = false;
        for (Contract contract : contractService.getAll()) {
            if (contract.getRoomId() == roomId && contract.getTenantId() == tenantId
                    && contract.getStatus() == ContractStatus.ACTIVE) {
                hasActiveContract = true;
                break;
            }
        }
        if (!hasActiveContract) {
            throw new IllegalArgumentException("Khach thue khong co hop dong ACTIVE tai phong nay.");
        }

        RoomIssue issue = new RoomIssue(System.currentTimeMillis(), roomId, tenantId,
                LocalDate.now(), RoomIssueStatus.OPEN, description.trim());
        issues.add(issue);
        repository.write(issues);
        return issue;
    }

    public void updateStatus(long id, RoomIssueStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Trang thai su co khong hop le.");
        }
        RoomIssue issue = findById(id);
        issue.setStatus(status);
        repository.write(issues);
    }

    public List<RoomIssue> getAll() {
        return issues;
    }

    public int findIndexById(long id) {
        for (int i = 0; i < issues.size(); i++) {
            if (issues.get(i).getId() == id) {
                return i;
            }
        }
        throw new DataNotFoundException();
    }

    public RoomIssue findById(long id) {
        return issues.get(findIndexById(id));
    }
}
