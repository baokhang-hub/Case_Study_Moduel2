package repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import entity.RoomIssue;
import entity.RoomIssueStatus;

public class RoomIssueRepository implements IRepository<RoomIssue> {
    private static final String FILE_NAME = "data/room_issue.csv";

    @Override
    public void write(List<RoomIssue> issues) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (RoomIssue issue : issues) {
                writer.write(issue.getId() + "," + issue.getRoomId() + "," + issue.getTenantId()
                        + "," + issue.getReportDate() + "," + issue.getStatus() + ","
                        + issue.getDescription().replace("\n", " ").replace("\r", " "));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    @Override
    public List<RoomIssue> read() {
        List<RoomIssue> issues = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", 6);
                issues.add(new RoomIssue(Long.parseLong(data[0]), Long.parseLong(data[1]),
                        Long.parseLong(data[2]), LocalDate.parse(data[3]),
                        RoomIssueStatus.valueOf(data[4]), data[5]));
            }
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }
        return issues;
    }
}
