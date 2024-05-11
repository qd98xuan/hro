package com.linzen.base.util.common;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.google.common.base.CaseFormat;
import com.linzen.base.model.DownloadCodeForm;
import com.linzen.base.model.print.PrintOption;
import com.linzen.base.service.IPrintDevService;
import com.linzen.model.visualJson.FieLdsModel;
import com.linzen.model.visualJson.analysis.FormAllModel;
import com.linzen.util.FilePathUtil;
import com.linzen.util.FileUploadUtils;
import com.linzen.util.TableFeildsEnum;
import com.linzen.util.context.SpringContext;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 代码生成通用方法类
 * 微服务单体不同处在此类 调整变通
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class FormCommonUtil {

    public static final String IS_CLOUD = "single";

    private static IPrintDevService iPrintDevService = SpringContext.getBean(IPrintDevService.class);

    /**
     * 获取需要生成的文件对象
     *
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    public static FileOutConfig getFileOutConfig(String fileName, String templatePath, DownloadCodeForm downloadCodeForm, String javaPath, String temName,
                                                 String typeStr, boolean concurrencyLock) {
        return new FileOutConfig(templatePath + File.separator + "java" + File.separator + temName) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                if ("entity".equals(typeStr)) {
                    List<TableField> fieldAll = tableInfo.getFields();
                    TableField mainTableField = fieldAll.stream().filter(tableField -> tableField.isKeyFlag()).findFirst().orElse(null);
                    fieldAll = fieldAll.stream().filter(DataControlUtils.distinctByKey(t -> t.getName())).collect(Collectors.toList());
                    if (mainTableField != null) {
                        fieldAll.stream().filter(tableField -> tableField.getName().equals(mainTableField.getName())).forEach(t -> t.setKeyFlag(mainTableField.isKeyFlag()));
                    }
                    for (TableField field : fieldAll) {
                        String name = field.getName().toLowerCase().replaceAll("f_" , "" );
                        field.setPropertyName(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name));
                        boolean fv = field.getName().equalsIgnoreCase(TableFeildsEnum.VERSION.getField() );
                        if (fv && concurrencyLock) {
                            field.setFill("1" );
                        }
                    }
                    tableInfo.setFields(fieldAll);
                }
                String eachName = "";
                String frontName = "";
                String modulName = downloadCodeForm.getModule();
                String framePath = downloadCodeForm.getModulePackageName();
                switch (typeStr) {
                    case "controller":
                        eachName = tableInfo.getControllerName();
                        framePath = getCloudPath("-controller" , downloadCodeForm);
                        break;
                    case "entity":
                        eachName = tableInfo.getEntityName();
                        framePath = getCloudPath("-entity" , downloadCodeForm);
                        break;
                    case "mapper":
                        eachName = tableInfo.getMapperName();
                        framePath = getCloudPath("-biz" , downloadCodeForm);
                        break;
                    case "xml":
                        eachName = tableInfo.getMapperName();
                        if ("cloud".equals(FormCommonUtil.IS_CLOUD)) {
                            framePath = "linzen-" + modulName + File.separator + "linzen-" + modulName + "-biz" + File.separator
                                    + "src" + File.separator + "main" + File.separator + "resources";
                            return javaPath + fileName + File.separator + "java" + File.separator + framePath + File.separator + "mapper"
                                    + File.separator + eachName + StringPool.DOT_XML;
                        }
                        return javaPath + fileName + File.separator + "resources" + File.separator + "mapper"
                                + File.separator + eachName + StringPool.DOT_XML;
                    case "service":
                        eachName = tableInfo.getServiceName();
                        framePath = getCloudPath("-biz" , downloadCodeForm);
                        break;
                    case "impl":
                        eachName = tableInfo.getServiceImplName();
                        frontName = "service" + File.separator;
                        framePath = getCloudPath("-biz" , downloadCodeForm);
                    default:
                        break;
                }
                return javaPath + fileName + File.separator + "java" + File.separator + framePath + File.separator + frontName + typeStr
                        + File.separator + eachName + StringPool.DOT_JAVA;
            }
        };
    }

    /**
     * 获取微服务框架路径
     *
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * 日期")
     */
    public static String getCloudPath(String houzui, DownloadCodeForm downloadCodeForm) {
        String framePath = "";
        boolean isCloud = "cloud".equals(FormCommonUtil.IS_CLOUD);
        if (isCloud) {
            framePath = "linzen-" + downloadCodeForm.getModule()
                    + File.separator
                    + "linzen-" + downloadCodeForm.getModule() + houzui
                    + File.separator
                    + "src" + File.separator + "main" + File.separator + "java"
                    + File.separator
                    + downloadCodeForm.getModulePackageName().replace(".", File.separator);
        } else {
            framePath = downloadCodeForm.getModulePackageName().replace(".", File.separator);
        }
        return framePath;
    }


    public static String getLocalBasePath() {
        return FileUploadUtils.getLocalBasePath();
    }

    public static String getPath(String type) {
        return FilePathUtil.getFilePath(type);
    }

    public static List<PrintOption> getList(List<String> ids) {
        return iPrintDevService.getPrintTemplateOptions(ids);
    }

    /**
     * 合计和千分位字段转换至前端可用
     * @param sourceList 原字段列表
     * @param type  列表类型4为行内编辑
     * @return 新字段列表
     */
    public static List<String> getSummaryList(List<String> sourceList,Integer type){
        List<String> finalFieldsTotal = new ArrayList<>();
        String suffix = "_name";
        if (type == 4) {
            suffix = "";
        }
        if(CollectionUtils.isEmpty(sourceList)){return finalFieldsTotal;}
        for (String field : sourceList) {
            String finalField;
            if (field.startsWith("linzen" )) {
                String fieldName = field.substring(field.lastIndexOf("linzen_" )).replace("linzen_" , "" );
                String tableName = field.substring(field.indexOf("_" ) + 1, field.lastIndexOf("_linzen" ));
                finalField = tableName + "." + fieldName + suffix;
            } else {
                finalField = field + suffix;
            }
            finalFieldsTotal.add(finalField);
        }
        return finalFieldsTotal;
    }

    /**
     * 合计千分位字段列表
     *
     * @param mast      主表字段
     * @param mastTable 副表字段
     * @param type      列表类型 4-行内编辑
     * @return
     */
    public static List<String> getSummaryThousandList(List<FormAllModel> mast, List<FormAllModel> mastTable, Integer type) {
        String suffix = "_name";
        if (type == 4) {
            suffix = "";
        }
        List<String> thousandsField = new ArrayList<>();
        for (FormAllModel f : mast) {
            FieLdsModel fm = f.getFormColumnModel().getFieLdsModel();
            if (fm.isThousands()) {
                thousandsField.add(fm.getVModel() + suffix);
            }
        }
        for (FormAllModel f : mastTable) {
            FieLdsModel fm = f.getFormMastTableModel().getMastTable().getFieLdsModel();
            if (fm.isThousands()) {
                thousandsField.add(f.getFormMastTableModel().getTable() + "." + fm.getVModel() + suffix);
            }
        }
        return thousandsField;
    }

}
