package repository;

import java.util.List;
import entity.Room;
import java.io.*;
import java.util.ArrayList;
import entity.RoomStatus;

public class RoomRepository implements IRepository<Room> {
    private static final String FILE_NAME = "data/room.csv";

    @Override
    public void write(List<Room> list) {
        String dataStr = "";
        for (Room room : list) {
            dataStr += room.getId() + "," + room.getName() + "," + room.getMonthlyRent() + "," + room.getStatus()
                    + "\n";
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
    public List<Room> read() {
        List<Room> roomList = new ArrayList<>();
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
                String name = data[1];
                double monthlyRent = Double.parseDouble(data[2]);
                RoomStatus status = RoomStatus.valueOf(data[3]);
                Room room = new Room(id, name, monthlyRent, status);
                roomList.add(room);
            }
            bufferedReader.close();
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }
        return roomList;
    }

}
