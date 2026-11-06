package repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import entity.User;
import entity.UserRole;

public class UserRepository implements IRepository<User> {
    private static final String FILE_NAME = "data/user.csv";

    @Override
    public void write(List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (User user : users) {
                writer.write(user.getId() + "," + user.getUsername() + "," + user.getPasswordHash()
                        + "," + user.getRole() + "," + user.getTenantId() + ","
                        + user.mustChangePassword());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    @Override
    public List<User> read() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                users.add(new User(Long.parseLong(data[0]), data[1], data[2],
                        UserRole.valueOf(data[3]), Long.parseLong(data[4]),
                        Boolean.parseBoolean(data[5])));
            }
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }
        return users;
    }
}
