package com.you.redis.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.you.redis.entity.User;

@Repository("userDao")
public interface UserDao
{

    User findUserById(Integer id);

    List<User> findAll();

    Integer saveUser(User user);

    Integer deleteById(Integer id);

    Integer updateUser(User user);

}
