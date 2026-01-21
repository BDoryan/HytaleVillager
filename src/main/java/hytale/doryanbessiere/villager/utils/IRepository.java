package hytale.doryanbessiere.villager.utils;

import java.util.List;

public interface IRepository<T> {

    public void create(T data);
    public void set(T data);
    public void delete(T data);
    public List<T> findAll();
    public T findById(Object id);
    public boolean existById(Object search);

}
