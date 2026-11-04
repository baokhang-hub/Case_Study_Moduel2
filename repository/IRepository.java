package repository;
import java.util.List;

public interface IRepository<T> {
    void write(List<T> list);
    List<T> read();
}  