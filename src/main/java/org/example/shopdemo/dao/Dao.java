package org.example.shopdemo.dao;

import org.example.shopdemo.dto.BooksFilter;
import org.example.shopdemo.entity.Books;

import java.util.List;
import java.util.Optional;

public interface Dao<K, E> {
    E save(E e);
    boolean delete(K id);
    boolean update(E e);
    List<E> findAll();
//    List<Books> findAll(BooksFilter filter);
    Optional<E> findById(K id);
}
