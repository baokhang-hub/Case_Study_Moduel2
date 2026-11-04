package repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import entity.Contract;
import entity.ContractStatus;

public class ContractRepository implements IRepository<Contract> {
    private static final String FILE_NAME = "data/contract.csv";

    @Override
    public void write(List<Contract> contracts) {
        String dataStr = "";
        for (Contract contract : contracts) {
            dataStr += contract.getId() + "," + contract.getRoomId() + ","
                    + contract.getTenantId() + "," + contract.getOccupantCount() + ","
                    + contract.getStartDate() + "," + contract.getEndDate() + ","
                    + contract.getDeposit() + "," + contract.getStatus() + "\n";
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
    public List<Contract> read() {
        List<Contract> contracts = new ArrayList<>();

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
                long roomId = Long.parseLong(data[1]);
                long tenantId = Long.parseLong(data[2]);
                int occupantCount = Integer.parseInt(data[3]);
                LocalDate startDate = LocalDate.parse(data[4]);
                LocalDate endDate = LocalDate.parse(data[5]);
                double deposit = Double.parseDouble(data[6]);
                ContractStatus status = ContractStatus.valueOf(data[7]);

                contracts.add(new Contract(id, roomId, tenantId, occupantCount,
                        startDate, endDate, deposit, status));
            }
            bufferedReader.close();
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }

        return contracts;
    }
}
