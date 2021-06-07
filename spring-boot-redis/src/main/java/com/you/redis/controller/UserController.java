package com.you.redis.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * user控制类
 * @author Administrator
 *
 */

import com.alibaba.fastjson.JSONObject;
import com.you.redis.entity.User;
import com.you.redis.service.UserService;
@RestController
@RequestMapping(value = "/user")
public class UserController
{
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    /**
     * 通过id查询user信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/findUserById", method = RequestMethod.GET, produces = "application/json")
    public User findUserById(@RequestParam("id") Integer id) {
        return userService.findUserById(id);
    }
    
    /**
     * 查询所有的用户信息
     * @return
     */
    @RequestMapping(value = "/findAll", method = RequestMethod.GET, produces = "application/json")
    public List<User> findAll(){
        return userService.findAll();
    }
    
    /**
     * 保存用户信息
     * @return
     */
    @RequestMapping(value = "/saveUser", method = RequestMethod.POST, produces = "application/json")
    public JSONObject saveUser(@RequestBody User user){
        logger.info("传入参数为：{}",user.toString());
        JSONObject jsonObject = new JSONObject();
        Integer row = userService.saveUser(user);
        logger.info("返回参数为：{}",row);
        jsonObject.put("id", user.getUserId());
        return jsonObject;
    }
    
    /**
     * 通过id删除用户信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/deleteById", method = RequestMethod.GET, produces = "application/json")
    public JSONObject deleteById(@RequestParam("id") Integer id) {
        JSONObject jsonObject = new JSONObject();
        //删除记录的行数
        Integer rowNumber = userService.deleteById(id);
        logger.info("返回参数为：{}",rowNumber);
        jsonObject.put("rowNumber", rowNumber);
        return jsonObject;
    }
    
    /**
     * 更新 用户信息
     * @param user
     * @return
     */
    @RequestMapping(value = "/updateUser", method = RequestMethod.POST, produces = "application/json")
    public JSONObject updateUser(@RequestBody User user){
        logger.info("传入参数为：{}",user.toString());
        JSONObject jsonObject = new JSONObject();
        Integer rowNumber = userService.updateUser(user);
        logger.info("返回参数为：{}",rowNumber);
        jsonObject.put("rowNumber", rowNumber);
        return jsonObject;
    }
}
