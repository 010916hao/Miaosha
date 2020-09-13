package com.miaoshaproject.controller;


import com.alibaba.druid.util.StringUtils;
import com.miaoshaproject.controller.viewobject.UserVO;
import com.miaoshaproject.dataobject.UserDO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EnumBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.miaoshaproject.controller.BaseController.CONTENT_TYPE_FORMED;
import static com.miaoshaproject.error.EnumBusinessError.PARAMETER_VALIDATION_ERROR;

@RestController("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class UserController  {

    @Resource
    private UserService userService;

    @Resource
    private HttpServletRequest httpServletRequest;

    //用户登陆接口
    @PostMapping(value = "/login", consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType login(@RequestParam(name = "telphone") String telphone,
                                  @RequestParam(name = "password") String password)
            throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        if (StringUtils.isEmpty(telphone) || StringUtils.isEmpty(password)) {
            throw new BusinessException(PARAMETER_VALIDATION_ERROR, "用户名或密码错误");
        }
        //用户登陆服务，用来验证用户登陆是否合法
        UserModel userModel =  userService.validateLogin(telphone, this.encodeByMD5(password));

        //将登陆session加入到用户登陆成功的session内
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);

        return CommonReturnType.create(null);
    }

    //用户注册接口
    @PostMapping(value = "/register", consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType register(@RequestParam(name = "name") String name,
                                     @RequestParam(name = "gender") Integer gender,
                                     @RequestParam(name = "age") Integer age,
                                     @RequestParam(name = "otpCode") String otpCode,
                                     @RequestParam(name = "telphone") String telphone,
                                     @RequestParam(name = "password") String password) throws UnsupportedEncodingException, NoSuchAlgorithmException, BusinessException {
        //验证手机号和对应的optCode相符合
        String inSessionOtpCode = (String) httpServletRequest.getSession().getAttribute(telphone);
        if(!com.alibaba.druid.util.StringUtils.equals(inSessionOtpCode, otpCode)) {
            throw new BusinessException(PARAMETER_VALIDATION_ERROR, "短信验证码错误");
        }
        //用户的注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender((byte)(int)gender);
        userModel.setAge(age);
        userModel.setTelphone(telphone);
        userModel.setRegisterMod("ByPhone");
        //userModel.setEncryptPassword(MD5Encoder.encode(password.getBytes()));
        userModel.setEncryptPassword(this.encodeByMD5(password));
        userService.register(userModel);
        return CommonReturnType.create(null);
    }

    public String encodeByMD5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        //加密字符串
        String newStr = base64Encoder.encode(md5.digest(str.getBytes("utf-8")));
        return newStr;
    }
    //用户获取otp短信接口
    @PostMapping(value = "/getotp", consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType getOtp(@RequestParam(name = "telphone") String telphone) {
        //需要按照一定规则生成otp验证码
        Random random = new Random();
        int randomInt = random.nextInt(89999);
        randomInt = randomInt +  10000;
        String optCode = String.valueOf(randomInt);

        //将otp验证码同对应用户的手机号相关联, 使用http session的方式绑定
        httpServletRequest.getSession().setAttribute(telphone, optCode);
        //将otp验证码通过短信通道发送给用户,省略
        System.out.println("telphone: " + telphone + "&otpCode: " + optCode);
        return CommonReturnType.create(null);
    }

    @GetMapping("/get")
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BusinessException {
        //调用service服务获取对应id的用户对象，并返回给前端
        UserModel userModel = userService.getUserById(id);

        //若获取的用户对象信息不存在
        if (userModel == null) {
            throw new BusinessException(EnumBusinessError.USER_NOT_EXIST);
        }

        UserVO userVO = convertFromUserModel(userModel);
        return CommonReturnType.create(userVO);
    }

    private UserVO convertFromUserModel(UserModel userModel) {
        if (userModel == null)
            return null;
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel, userVO);
        return userVO;
    }



}
