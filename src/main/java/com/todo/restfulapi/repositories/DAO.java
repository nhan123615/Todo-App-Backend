package com.todo.restfulapi.repositories;

import java.util.List;

public interface DAO<T> {
    List<T> findAll();
    T findById(Long id);
    T save(T vo);
    void delete(Long id);
}
