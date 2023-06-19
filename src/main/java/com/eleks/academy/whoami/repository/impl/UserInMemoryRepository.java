package com.eleks.academy.whoami.repository.impl;

import com.eleks.academy.whoami.core.exception.UserNotFoundException;
import com.eleks.academy.whoami.dmo.User;
import com.eleks.academy.whoami.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class UserInMemoryRepository implements UserRepository {
    private SimpleJpaRepository simpleJpaRepository;

    private HashMap<String, User> users = new HashMap<>();
    @Override
    public User findByUsername(String username) {
        return null;
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public User findByEmail(String email) {
        String query = "SELECT * FROM users WHERE email = `" + email + "`";
        RowMapper<User> userRowMapper = (resultSet, rowNum) -> {
            User user = new User();
            user.setEmail(resultSet.getString("email"));
            user.setPassword(resultSet.getString("password"));
            // ...
            // Додаткові поля та обробка результату
            return user;
        };

        List<User> users = jdbcTemplate.query(query, new Object[]{email}, userRowMapper);

        if (!users.isEmpty()) {
            return users.get(0);
        }

        return null;
    }

    @Override
    public List<User> findAll() {
        return simpleJpaRepository.findAll();
    }

    @Override
    public List<User> findAll(Sort sort) {
        return simpleJpaRepository.findAll(sort);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return simpleJpaRepository.findAll(pageable);
    }

    @Override
    public List<User> findAllById(Iterable<Long> longs) {
        return simpleJpaRepository.findAllById(longs);
    }

    @Override
    public long count() {
        return simpleJpaRepository.count();
    }

    @Override
    public void deleteById(Long aLong) {
        simpleJpaRepository.deleteById(aLong);
    }

    @Override
    public void delete(User entity) {
        simpleJpaRepository.delete(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        simpleJpaRepository.deleteAllById(longs);
    }

    @Override
    public void deleteAll(Iterable<? extends User> entities) {
        simpleJpaRepository.deleteAll(entities);
    }

    @Override
    public void deleteAll() {
        simpleJpaRepository.deleteAll();
    }

    @Override
    public <S extends User> S save(S entity) {
        return (S) simpleJpaRepository.save(entity);
    }

    @Override
    public <S extends User> List<S> saveAll(Iterable<S> entities) {
        return simpleJpaRepository.saveAll(entities);
    }

    @Override
    public Optional<User> findById(Long aLong) {
        return simpleJpaRepository.findById(aLong);
    }

    @Override
    public boolean existsById(Long aLong) {
        return simpleJpaRepository.existsById(aLong);
    }

    @Override
    public void flush() {
        simpleJpaRepository.flush();
    }

    @Override
    public <S extends User> S saveAndFlush(S entity) {
        return (S) simpleJpaRepository.saveAndFlush(entity);
    }

    @Override
    public <S extends User> List<S> saveAllAndFlush(Iterable<S> entities) {
        return simpleJpaRepository.saveAll(entities);
    }

    @Override
    public void deleteAllInBatch(Iterable<User> entities) {
        simpleJpaRepository.deleteAllInBatch(entities);
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {
        simpleJpaRepository.deleteAllByIdInBatch(longs);
    }

    @Override
    public void deleteAllInBatch() {
        simpleJpaRepository.deleteAllInBatch();
    }

    @Override
    public User getOne(Long aLong) {
        return (User) simpleJpaRepository.getOne(aLong);
    }

    @Override
    public User getById(Long aLong) {
        return (User) simpleJpaRepository.getById(aLong);
    }

    @Override
    public <S extends User> Optional<S> findOne(Example<S> example) {
        return simpleJpaRepository.findOne(example);
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example) {
        return simpleJpaRepository.findAll(example);
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
        return simpleJpaRepository.findAll(example, sort);
    }

    @Override
    public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
        return simpleJpaRepository.findAll(example, pageable);
    }

    @Override
    public <S extends User> long count(Example<S> example) {
        return simpleJpaRepository.count(example);
    }

    @Override
    public <S extends User> boolean exists(Example<S> example) {
        return simpleJpaRepository.exists(example);
    }

    @Override
    public <S extends User, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return (R) simpleJpaRepository.findBy(example, queryFunction);
    }
}
