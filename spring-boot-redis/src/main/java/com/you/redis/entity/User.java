package com.you.redis.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor   //生成一个无参构造方法
@AllArgsConstructor  //生成一个全参数的构造方法
public class User implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Integer userId;

    private String userName;
    
    private Integer userAge;
    
    private String userPassword;
}
