package com.linzen.model.visualJson.analysis;

import java.util.ArrayList;
import java.util.List;

/**
 * 引擎模板
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public enum FormEnum {

    //子表
    table("table"),
    //主表
    mast("mast"),
    //表单子表
    mastTable("mastTable"),

    //栅格
    row("row"),
    //折叠
    collapse("collapse"),
    collapseItem("collapseItem"),
    //标签
    tab("tab"),
    tabItem("tabItem"),
    //表格
    tableGrid("tableGrid"),
    //表格
    tableGridTr("tableGridTr"),
    //表格
    tableGridTd("tableGridTd"),
    //卡片
    card("card"),

    //分组标题
    groupTitle("groupTitle"),
    //分割线
    divider("divider"),
    //文本
    LINZENText("text"),
    //按钮
    button("button"),
    //关联表单属性
    relationFormAttr("relationFormAttr"),
    //关联表单属性
    popupAttr("popupAttr"),
    // 条形码
    BARCODE ("barcode"),
    // 连接
    link ("link"),
    // 提示语
    alert ("alert"),
    //二维码
    QR_CODE("qrcode");

    private String message;

    FormEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    //无用的对象
    private static List<String> isNodeList=new ArrayList<String>(){{
        add(FormEnum.groupTitle.getMessage());
        add(FormEnum.divider.getMessage());
        add(FormEnum.LINZENText.getMessage());
        add(FormEnum.button.getMessage());
//        add(FormEnum.relationFormAttr.getMessage());
        add(FormEnum.BARCODE.getMessage());
        add(FormEnum.QR_CODE.getMessage());
        add(FormEnum.alert.getMessage());
        add(FormEnum.link.getMessage());
    }};


    public static boolean isModel(String value){
        boolean isData = isNodeList.contains(value);
        return isData;
    }


}
