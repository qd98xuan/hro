package com.linzen.controller;

import com.linzen.base.ServiceResult;
import com.linzen.model.ReportManageModel;
import com.linzen.util.JsonUtil;
import com.linzen.util.PinYinUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 专业报表
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "专业报表", description = "获取专业报表列表")
@RestController
@RequestMapping("/api/extend/ReportManage")
public class ReportManageController {

    /**
     * 列表
     *
     * @return
     */
    @Operation(summary = "获取专业报表列表")
    @GetMapping
    public ServiceResult list() {
        List<ReportManageModel> data = new ArrayList<>();
        int num = 1000000000;
        for (int i = 0; i < fullNameList().length; i++) {
            ReportManageModel model = new ReportManageModel();
            model.setId(String.valueOf(num+i+1));
            model.setFullName(fullNameList()[i]);
            model.setUrlAddress(PinYinUtil.getFullSpell(fullNameList()[i]));
            if (i < 8) {
                model.setCategory(categoryList()[0]);
            }else if(i>=8 && i<=12){
                model.setCategory(categoryList()[1]);
            }else if(i>=13 && i<=14){
                model.setCategory(categoryList()[2]);
            }else if(i>=15 && i<=17){
                model.setCategory(categoryList()[3]);
            }else if(i>=18 && i<=20){
                model.setCategory(categoryList()[4]);
            }else if(i>=21 && i<=23){
                model.setCategory(categoryList()[5]);
            }else if(i>23){
                model.setCategory(categoryList()[6]);
            }
            data.add(model);
        }
        return ServiceResult.success(JsonUtil.listToJsonField(data));
    }

    private String[] categoryList() {
        String[] category = {"报表示例", "Excel表格类", "Word文档类", "分栏与分组", "报表套打", "图表类", "其他示例"};
        return category;
    }

    private String[] fullNameList() {
        String[] fullName = {"房地产驾驶舱", "数字化营销", "市场营销", "SMT车间看板", "学校综合业绩表", "热线机器人数据分析", "渠道零售",
                "承包方调查表", "多维透视表", "复杂交叉表", "煤矿三量基础表", "土地资源", "小学课程表", "销售合同模板", "干部任免审批表",
                "单级分组", "多级分组", "分栏报表", "国航机票", "客户订单套打", "快递单套打", "常规图表", "人员离职分析", "销售分析趋势",
                "标签打印", "报表水印", "文档目录"};
        return fullName;
    }

}
