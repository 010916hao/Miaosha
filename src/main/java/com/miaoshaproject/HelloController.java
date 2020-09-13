package com.miaoshaproject;

import com.miaoshaproject.dao.UserDOMapper;
import com.miaoshaproject.dataobject.UserDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController

public class HelloController {

    @Resource
    private UserDOMapper userDOMapper;
//    @GetMapping("hello")
    @RequestMapping(method = RequestMethod.GET, value = "hello")
    public String helloWorld() {
        UserDO userDO = userDOMapper.selectByPrimaryKey(1);
        userDO.setName("123");
        return userDO.getName();
    }
}
