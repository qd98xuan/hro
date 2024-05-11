package com.linzen.util;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.linzen.exception.ImportException;
import lombok.Cleanup;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class ExcelUtil {

    /**
     * Workbook 转 MultipartFile
     *
     * @param workbook excel文档
     * @param fileName 文件名
     * @return MultipartFile
     */
    public static MultipartFile workbookToCommonsMultipartFile(Workbook workbook, String fileName) {
        //Workbook转FileItem
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        FileItem fileItem = factory.createItem("textField", "text/plain", true, fileName);
        try {
            OutputStream os = fileItem.getOutputStream();
            workbook.write(os);
            os.close();
            //FileItem转MultipartFile
            MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
            return multipartFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载excel
     *
     * @param fileName excel名称
     * @param workbook
     */
    public static void dowloadExcel(Workbook workbook, String fileName) {
        try {
            HttpServletResponse response = ServletUtil.getResponse();
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * excel转成实体
     * @param filePath 路径
     * @param titleRows 行
     * @param headerRows 列
     * @param pojoClass 实体
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(String filePath, Integer titleRows, Integer headerRows, Class<T> pojoClass){
        if (StringUtils.isBlank(filePath)){
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(new File(XSSEscape.escapePath(filePath)), pojoClass, params);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * excel转成实体
     * @param file 文件
     * @param titleRows 行
     * @param headerRows 列
     * @param pojoClass 实体
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(File file, Integer titleRows, Integer headerRows, Class<T> pojoClass) throws ImportException {
        if (file == null){
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(file, pojoClass, params);
        } catch (Exception e) {
            throw new ImportException(e.getMessage());
        }
        return list;
    }

    /**
     * excel转成实体
     * @param inputStream 文件流
     * @param titleRows 行
     * @param headerRows 列
     * @param pojoClass 实体
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcelByInputStream(InputStream inputStream, Integer titleRows, Integer headerRows, Class<T> pojoClass) throws Exception {
        if (inputStream == null){
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        list = ExcelImportUtil.importExcel(inputStream, pojoClass, params);
        return list;
    }

    /**
     * excel转成实体
     * @param file 文件
     * @param titleRows 行
     * @param headerRows 列
     * @param pojoClass 实体
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass){
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(file.getInputStream(), pojoClass, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 备用方案，读取不到时间暂用此方法
     * 通过基础poi读取NUMERIC转换成时间格式
     * @param
     * @return
     * @copyright 领致信息技术有限公司
     * @date 2023-04-01
     */
    public static void imoportExcelToMap(File file, Integer titleIndex,List<Map> excelDataList){
        List<Map<String, Object>> mapList = new ArrayList<>();
        FileInputStream inputStream = null;
        try{
            String fileName = file.getName();
            @Cleanup Workbook workbook = null;
            inputStream = new FileInputStream(file);
            try{
                workbook = new HSSFWorkbook(inputStream);
            }catch (Exception e){
                inputStream = new FileInputStream(file);
                workbook = new XSSFWorkbook(inputStream);
            }

            Sheet sheet = workbook.getSheetAt(0);
            Row titleRow = sheet.getRow(titleIndex-1);

            for (int i = titleIndex; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                Map<String, Object> map = new HashMap<>();
                for (int j = 0; j <  row.getPhysicalNumberOfCells(); j++) {
                    Cell cell = row.getCell(j);
                    Cell titleCell = titleRow.getCell(j);
                    if (cell!=null&& CellType.NUMERIC.equals(cell.getCellType())) {
                        short format = cell.getCellStyle().getDataFormat();
                        if (cell.getDateCellValue() != null && format > 0) {
                            //表头数据
                            String titleName = titleCell.getStringCellValue();
                            if (StringUtil.isEmpty(titleName)) {
                                titleName=sheet.getRow(titleIndex-2).getCell(j).getStringCellValue();
                            }
                            //单元格内容
                            Date dateCellValue = cell.getDateCellValue();
                            String valueName = DateUtil.daFormat(dateCellValue);
                            map.put(titleName, valueName);
                        }
                    }
                }
                mapList.add(map);
            }
            //基础poi读取到时间同步到easypoi读取到的数据中去
            if (!CollectionUtils.sizeIsEmpty(mapList)) {
                for (int n = 0; n < mapList.size(); n++) {
                    Map<String, Object> a = mapList.get(n);
                    Map b = excelDataList.get(n);
                    if (a != null) {
                        for (String key : a.keySet()) {
                            if (b.containsKey(key)) b.put(key, a.get(key));
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
