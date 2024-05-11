package com.linzen.base.model.Template7;


import com.linzen.model.visualJson.FieLdsModel;
import lombok.Data;

import java.util.List;

@Data
public class MastModel {

    //主表的属性
    private List<FieLdsModel> mastList;
    //系统自带的赋值
    private List<KeyModel> keyMastList;
}
