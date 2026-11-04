package repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import entity.Tenant;

public class TenantRepository implements IRepository<Tenant> {
    private static final String FILE_NAME = "data/tenant.csv";

    @Override
    public void write(List<Tenant> tenants) {
        String dataStr = "";
        for (Tenant tenant : tenants) {
            dataStr += tenant.getId() + "," + tenant.getFullName() + ","
                    + tenant.getPhoneNumber() + "," + tenant.getIdentityNumber() + "\n";
        }

        try {
            FileWriter fileWriter = new FileWriter(FILE_NAME);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(dataStr);
            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    @Override
    public List<Tenant> read() {
        List<Tenant> tenants = new ArrayList<>();

        try {
            FileReader fileReader = new FileReader(FILE_NAME);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                String[] data = line.split(",");
                long id = Long.parseLong(data[0]);
                String fullName = data[1];
                String phoneNumber = data[2];
                String identityNumber = data[3];

                tenants.add(new Tenant(id, fullName, phoneNumber, identityNumber));
            }
            bufferedReader.close();
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }

        return tenants;
    }
}
