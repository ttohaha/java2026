package blinov_first.dao;

import blinov_first.entity.Item;
import blinov_first.exception.DaoException;
import java.util.List;

public interface ItemDao {
    boolean add(Item item) throws DaoException;
    List<Item> findAll() throws DaoException;
}