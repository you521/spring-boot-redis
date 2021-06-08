package com.you.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.you.redis.entity.User;
import com.you.redis.javautil.RedisUtil;
import com.you.redis.service.UserService;


@SpringBootTest
class SpringBootRedisApplicationTests {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserService userService;
    
	@Test
	void contextLoads() {
	 User user = userService.findUserById(2);
     // 存储字符串
     boolean save = redisUtil.set("name::test::redis",user.toString());
     System.out.println("save--------------------->"+save);
	 List<User> listUser = userService.findAll();
	 Map<Object, Object> map = new HashMap<Object, Object>();
	 for (User user2 : listUser)
    {
        map.put(Integer.toString(user2.getUserId()), user2);
    }
	  // 存储哈希值
      boolean f = redisUtil.hmset("name::map::redis",map);
      System.out.println("f-------->"+f);
	}

}
