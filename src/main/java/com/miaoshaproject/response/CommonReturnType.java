package com.miaoshaproject.response;

import lombok.Data;


@Data
public class CommonReturnType {
    //标明对应请求的返回处理结果 success或fail
    private String status;

    //若status==success，则返回前端需要的json数据
    //若status==fail，则使用通用的错误码格式
    private Object data;

    //定义一个通用的创建方法
    public static CommonReturnType create(Object data) {
        return CommonReturnType.create(data, "success");
    }

    public static CommonReturnType create(Object data, String status) {
        CommonReturnType type = new CommonReturnType();
        type.setStatus(status);
        type.setData(data);
        return type;
    }
}
