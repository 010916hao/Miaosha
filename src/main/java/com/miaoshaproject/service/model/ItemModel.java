package com.miaoshaproject.service.model;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ItemModel {

    private Integer id;
    //商品名
    @NotBlank(message = "商品名不能为空")
    private String title;
    //商品价格
    @NotNull(message = "商品价格不能不填写")
    private BigDecimal price;
    //商品库存
    @NotNull(message = "商品库存不能不填写")
    @Min(value = 0, message = "商品库存不能少于零")
    private Integer stock;
    //商品的描述
    private String description;
    //销量
//    @NotNull(message = "商品销量不能不填写")
    @Min(value = 0, message = "商品销量不能少于零")
    private Integer sales;
    //商品描述图片的url
    @NotBlank(message = "商品描述图片必须存在")
    private String imgUrl;
    //使用聚合模型，如果promoModel不为空，则表示其拥有了还未结束的秒杀活动
    private PromoModel promoModel;

}
