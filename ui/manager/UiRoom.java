package ui.manager;
import service.RoomService;
import entity.Room;
import libary.InputHelper;
import libary.erros.DataNotFoundException;

public class UiRoom {
    private static final RoomService roomService = RoomService.getObject();
    public static  void showRoomMenu() {
        int choice;
        do {
            System.out.println("===== QUAN LY PHONG =====");
            System.out.println("1. Them phong");
            System.out.println("2. Sua phong");
            System.out.println("3. Xoa phong");
            // System.out.println("4. Tim kiem phong");
            System.out.println("4. Xem danh sach phong");
            System.out.println("0. Tro ve menu quan ly");
            System.out.print("Chon chuc nang: ");

            choice = InputHelper.inputInt();

            switch (choice) {
                case 1:
                    addRoom();
                    break;
                case 2:
                    updateRoom();
                    break;
                case 3:
                    deleteRoom();
                    break;
                // case 4:
                //     // Call method to manage invoices
                //     break;
                case 4:
                    getAllRooms();
                    break;
                case 0:
                    ui.manager.UiManager.showManagerMenu();
                    break;
                default:
                    System.out.println("Lua chon khong hop le. Vui long chon lai.");
            }
        } while (choice != 0);

    }

    public static void addRoom() {
        System.out.println("===== THEM PHONG =====");
        long id = System.currentTimeMillis() / 1000L;
        System.out.print("Nhap ten phong: ");
        String roomName = InputHelper.inputString();
        System.out.println("Nhap gia thue phong: ");
        double monthlyRent = InputHelper.inputDouble();
        Room room = new Room(id, roomName, monthlyRent, entity.RoomStatus.AVAILABLE);
        roomService.add(room);
        System.out.println("Phong da duoc them thanh cong!");
    }

    public static void getAllRooms() {
        System.out.println("===== DANH SACH PHONG =====");
        int index = 1;
        for (Room room : roomService.getAll()) {
            System.out.println(index + ". ID: " + room.getId() + ", Ten phong: " + room.getName() + ", Gia thue: " + room.getMonthlyRent() + ", Trang thai: " + room.getStatus());
            index++;
        }
    }

    public static void updateRoom(){
        System.out.println("===== SUA PHONG =====");
        System.out.println("Nhap ID phong can sua: ");
        long id = InputHelper.inputLong();
        try {
             Room room = roomService.findById(id);
             System.out.println("Nhap ten phong moi (" +"Ten cu: " + room.getName() + "): ");
             String newName = InputHelper.inputString();
             System.out.println("Nhap gia thue moi (" +"Gia cu: " + room.getMonthlyRent() + "): ");
             double newMonthlyRent = InputHelper.inputDouble();
             Room roomUpdated = new Room(id, newName, newMonthlyRent);
             roomService.update(id, roomUpdated);
             System.out.println("Phong da duoc cap nhat thanh cong!");
        } catch (DataNotFoundException e) {
            System.out.println("Khong tim thay phong co ID: " + id);
        }
       

    }

    public static void deleteRoom() {
        System.out.println("===== XOA PHONG =====");
        System.out.println("Nhap ID phong can xoa: ");
        long id = InputHelper.inputLong();
        try {
            System.out.println("Ban co chac muon xoa phong co ID: " + id + "? (Y/N)");
            String confirmation = InputHelper.inputString();
            if (confirmation.equalsIgnoreCase("Y")) {
                roomService.delete(id);
                System.out.println("Phong da duoc xoa thanh cong!");
            }
        } catch (DataNotFoundException e) {
            System.out.println("Khong tim thay phong co ID: " + id);
        }
    }
}
