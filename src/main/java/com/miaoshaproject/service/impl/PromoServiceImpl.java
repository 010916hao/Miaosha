package com.miaoshaproject.service.impl;

import com.miaoshaproject.dao.PromoDOMapper;
import com.miaoshaproject.dataobject.PromoDO;
import com.miaoshaproject.service.PromoService;
import com.miaoshaproject.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

@Service
public class PromoServiceImpl implements PromoService {

    @Resource
    PromoDOMapper promoDOMapper;


    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        //获取对应商品的秒杀活动信息
        PromoDO promoDO = promoDOMapper.selectByItemId(itemId);
        //dataobject -> model
        PromoModel promoModel = convertFromDataObject(promoDO);
        if (promoModel == null)
            return null;
        //判断当前时间秒杀活动正在开始或正在进行
        if (promoModel.getStartDate().isAfterNow())
            promoModel.setStatus(1);
        else if (promoModel.getEndDate().isBeforeNow())
            promoModel.setStatus(3);
        else
            promoModel.setStatus(2);
        return promoModel;
    }

    private PromoModel convertFromDataObject(PromoDO promoDO) {
        if (promoDO == null)
            return null;
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDO, promoModel);
        promoModel.setPromoItemPrice(new BigDecimal(promoDO.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        promoModel.setEndDate((new DateTime(promoDO.getEndDate())));
        return promoModel;
    }
}
