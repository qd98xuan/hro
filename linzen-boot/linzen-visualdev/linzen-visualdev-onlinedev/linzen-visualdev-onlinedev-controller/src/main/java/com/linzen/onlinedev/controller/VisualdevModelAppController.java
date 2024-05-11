package com.linzen.onlinedev.controller;

import cn.hutool.core.bean.BeanUtil;
import com.linzen.base.ServiceResult;
import com.linzen.base.entity.VisualdevEntity;
import com.linzen.base.service.VisualdevService;
import com.linzen.base.vo.DownloadVO;
import com.linzen.config.ConfigValueUtil;
import com.linzen.constant.MsgCode;
import com.linzen.emnus.ExportModelTypeEnum;
import com.linzen.emnus.ModuleTypeEnum;
import com.linzen.exception.WorkFlowException;
import com.linzen.onlinedev.model.BaseDevModelVO;
import com.linzen.util.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 0代码app无表开发
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Tag(name = "0代码app无表开发", description = "ModelAppData")
@RestController
@RequestMapping("/api/visualdev/OnlineDev/App")
public class VisualdevModelAppController {

    @Autowired
    private VisualdevService visualdevService;

    @Autowired
    private FileExport fileExport;

    @Autowired
    private ConfigValueUtil configValueUtil;

    @Operation(summary = "功能导出")
    @PostMapping("/{modelId}/Actions/ExportData")
    public ServiceResult<DownloadVO> exportData(@PathVariable("modelId") String modelId) {
        VisualdevEntity visualdevEntity = visualdevService.getInfo(modelId);
        BaseDevModelVO vo = BeanUtil.toBean(visualdevEntity, BaseDevModelVO.class);
        vo.setModelType(ExportModelTypeEnum.App.getMessage());
        DownloadVO downloadVO = fileExport.exportFile(vo, configValueUtil.getTemporaryFilePath(), visualdevEntity.getFullName(), ModuleTypeEnum.VISUAL_APP.getTableName());
        return ServiceResult.success(downloadVO);
    }

    @Operation(summary = "功能导入")
    @PostMapping(value = "/Model/Actions/ImportData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResult<String> ImportData(@RequestPart("file") MultipartFile multipartFile) throws WorkFlowException {
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.VISUAL_APP.getTableName())) {
            return ServiceResult.error(MsgCode.IMP002.get());
        }
        //获取文件内容
        String fileContent = FileUtil.getFileContent(multipartFile);
        BaseDevModelVO vo = JsonUtil.createJsonToBean(fileContent, BaseDevModelVO.class);
        if (vo.getModelType() == null || !vo.getModelType().equals(ExportModelTypeEnum.App.getMessage())) {
            return ServiceResult.error("请导入对应功能的json文件");
        }
        VisualdevEntity visualdevEntity = BeanUtil.toBean(vo, VisualdevEntity.class);
        String modelId = visualdevEntity.getId();
        if (StringUtil.isNotEmpty(modelId)) {
            VisualdevEntity entity = visualdevService.getInfo(modelId);
            if (entity != null) {
                return ServiceResult.error("已存在相同功能");
            }
        }
        visualdevEntity.setCreatorTime(DateUtil.getNowDate());
        visualdevEntity.setUpdateTime(null);
        visualdevService.create(visualdevEntity);
        return ServiceResult.success(MsgCode.IMP001.get());
    }
}
