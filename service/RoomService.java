package service;
import java.util.List;
import entity.Room;
import libary.erros.DataNotFoundException;
import repository.RoomRepository;

public class RoomService implements IService<Room>  {
    private List<Room> rooms;
    private static final RoomService RoomServiceInstance = new RoomService();
    private final RoomRepository roomRepository = new RoomRepository();
    
    public static RoomService getObject() {
        return RoomServiceInstance;
    }
    public RoomService() {
        this.rooms = roomRepository.read();
    }

    @Override
    public  void add(Room newRoom) {
        this.rooms.add(newRoom);
        roomRepository.write(this.rooms);
    }

    @Override
    public void update(long id, Room updatedRoom) {
        int index = this.findIndexById(id);
        this.rooms.set(index, updatedRoom);
        roomRepository.write(this.rooms);
    }   

    @Override
    public void delete(long id) {
        int index = this.findIndexById(id);
        this.rooms.remove(index);
        roomRepository.write(this.rooms);
    }

    @Override
    public List<Room> getAll() {
       return this.rooms;
    }

    @Override
    public int findIndexById(long id) {
        for (int i = 0; i < this.rooms.size(); i++) {
            Room room = this.rooms.get(i);
            if (room.getId() == id) {
                return i;
            }
        }
        throw new DataNotFoundException();
    }

    @Override
    public Room findById(long id) {
        int index = this.findIndexById(id);
        return this.rooms.get(index);
    }
}
