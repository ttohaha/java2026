package blinov_first.dao;

import blinov_first.entity.AbstractEntity;
import blinov_first.exception.DaoException;

import java.util.List;
import java.util.Optional;

public abstract class BaseDao<T extends AbstractEntity> {
    public abstract boolean insert(T t) throws DaoException;
    public abstract boolean delete(T t) throws DaoException;
    public abstract boolean delete(long id) throws DaoException;
    public abstract List<T> findAll() throws DaoException;
    public abstract Optional<T> findById(long id) throws DaoException;
    public abstract T update(T t) throws DaoException;
}