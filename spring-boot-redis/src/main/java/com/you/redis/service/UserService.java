package com.you.redis.service;

import java.util.List;

import com.you.redis.entity.User;

public interface UserService
{
   
    User findUserById(Integer id);

    List<User> findAll();

    Integer saveUser(User user);

    Integer deleteById(Integer id);

    Integer updateUser(User user);

}
