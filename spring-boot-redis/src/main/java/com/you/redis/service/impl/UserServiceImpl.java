package com.you.redis.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.you.redis.dao.UserDao;
import com.you.redis.entity.User;
import com.you.redis.service.UserService;

@Service(value = "userService")
@CacheConfig(cacheManager = "defaultCacheManager", cacheNames = {"user"})
public class UserServiceImpl implements UserService
{

    @Resource
    private UserDao userDao;
    
    @Override
    @Cacheable(key = "'user_'+#id", unless="#result == null")
    public User findUserById(Integer id)
    {
        User user = userDao.findUserById(id);
        return user;
    }

    @Override
    public List<User> findAll()
    {
        List<User> listUser = userDao.findAll();
        return listUser;
    }

    @Override
    public Integer saveUser(User user)
    {
        Integer row = userDao.saveUser(user);
        return row;
    }

    @Override
    public Integer deleteById(Integer id)
    {
        Integer row = userDao.deleteById(id);
        return row;
    }

    @Override
    public Integer updateUser(User user)
    {
        Integer row = userDao.updateUser(user);
        return row;
    }

}
