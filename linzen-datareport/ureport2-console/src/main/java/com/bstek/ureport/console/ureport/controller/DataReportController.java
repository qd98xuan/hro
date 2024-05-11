package com.bstek.ureport.console.ureport.controller;

import cn.hutool.core.util.ObjectUtil;
import com.bstek.ureport.build.ReportBuilder;
import com.bstek.ureport.console.BaseServletAction;
import com.bstek.ureport.console.cache.TempObjectCache;
import com.bstek.ureport.console.designer.ReportDefinitionWrapper;
import com.bstek.ureport.console.ureport.entity.ReportEntity;
import com.bstek.ureport.console.ureport.entity.UserEntity;
import com.bstek.ureport.console.ureport.model.*;
import com.bstek.ureport.console.ureport.service.ReportService;
import com.bstek.ureport.console.ureport.service.UserService;
import com.bstek.ureport.console.ureport.util.DownUtil;
import com.bstek.ureport.console.ureport.util.UreportPreviewUtil;
import com.bstek.ureport.console.ureport.util.UreportUtil;
import com.bstek.ureport.console.util.*;
import com.bstek.ureport.definition.ReportDefinition;
import com.bstek.ureport.definition.datasource.DatasourceDefinition;
import com.bstek.ureport.definition.datasource.JdbcDatasourceDefinition;
import com.bstek.ureport.export.ReportRender;
import com.bstek.ureport.export.html.HtmlReport;
import com.bstek.ureport.model.Report;
import com.linzen.database.util.DynamicDataSourceUtil;
import com.linzen.util.JsonUtil;
import com.linzen.util.StringUtil;
import com.linzen.util.UserProvider;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 核心控制层，大部分功能
 */
@Slf4j
public class DataReportController extends BaseServletAction {

    @Autowired
    private ReportService reportService;
    @Autowired
    private ReportRender reportRender;
    @Autowired
    private UserService userService;

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = retriveMethod(req);
        String method = req.getMethod();
        if (method.equals("POST")) {
            if (url == null) {
                savaData(req, resp);
            } else if (url.contains("Actions/Copy")) {
                String id = url.split("/")[0];
                copyReport(req, resp, id);
            } else if (url.contains("Actions/Import")) {//导入
                importDataReport(req, resp);
            }
        } else if (method.equals("PUT")) {
            if (url.contains("Actions/State")) {
                String id = url.split("/")[0];
                stateDataReport(req, resp, id);
            }else {
                String id = url;//ids.substring(strStartIndex + 1);
                if (StringUtil.isEmpty(id)) {
                    writeObjectToJson(resp, ActionResult.fail("数据不存在"));
                    return;
                }
                updateData(req, resp, id);
            }
        } else if (method.equals("DELETE")) {
            String id = url;
            if (StringUtil.isEmpty(id)) {
                writeObjectToJson(resp, ActionResult.fail("数据不存在"));
                return;
            }
            deleteData(req, resp, id);
        } else if (method.equals("GET")) {
            if (url == null) {
                getList(req, resp);
            } else if (url.equals("init")) {
                init(req, resp);
            } else if (url.equals("Selector")) {
                Selector(req, resp);
            } else if (url.equals("preview")) {
                previewData(req, resp);
            } else if (url.contains("Actions/Export")) {
                //截取id
                String id = url.split("/")[0];
                exportDataReport(req, resp, id);
            } else {
                //打开报表
                String id = url;
                if (StringUtil.isEmpty(id)) {
                    writeObjectToJson(resp, ActionResult.fail("数据不存在"));
                    return;
                }
                getInfo(req, resp, id);
            }
        }
    }

    /**
     * 更新状态
     *
     * @param req
     * @param resp
     * @param id
     */
    private void stateDataReport(HttpServletRequest req, HttpServletResponse resp, String id) throws IOException {
        ReportEntity entity = reportService.GetInfo(id);
        if(entity==null){
            writeObjectToJson(resp, ActionResult.fail("更新接口状态失败，数据不存在"));
        }else {
            entity.setDelFlag("0".equals(String.valueOf(entity.getDelFlag()))?1:0);
            reportService.Update(id,entity);
            writeObjectToJson(resp, ActionResult.success("更新接口状态成功"));
        }
    }

    /**
     * 导出报表
     *
     * @param req
     * @param resp
     * @param id
     */
    private void exportDataReport(HttpServletRequest req, HttpServletResponse resp, String id) {
        ReportEntity entity = reportService.GetInfo(id);
        String contentJson = entity.getContent();
        try {
            byte[] content = entity.getContent().getBytes("UTF-8");
            ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
            ReportDefinition reportDefinition = UreportUtil.parseReport(inputStream, entity.getFullName(), true);
            for (DatasourceDefinition definition : reportDefinition.getDatasources()) {
                if (definition instanceof JdbcDatasourceDefinition) {
                    contentJson = contentJson.replaceAll("password=\"" + ((JdbcDatasourceDefinition) definition).getPassword() + "\"", "password=\"\"");
                }
            }
            entity.setContent(contentJson);
        } catch (Exception e) {

        }
        String objectToString = JsonUtil.getObjectToString(entity);
        DownUtil.downloadFile(objectToString, entity.getFullName() + ".json");
    }

    /**
     * 导入
     *
     * @param req
     * @param resp
     */
    private void importDataReport(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String fileContent = null;
        try {
            String contentType = req.getContentType();
            String type = "0";
            if (contentType != null && !"".equals(contentType) && contentType.contains("multipart/form-data")) {
                HttpSession session = req.getSession();
                MultipartResolver resolver = new CommonsMultipartResolver(session.getServletContext());
                MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(req);
                MultipartHttpServletRequest multipartRequest2 = WebUtils.getNativeRequest(multipartRequest, MultipartHttpServletRequest.class);
                MultipartFile file = multipartRequest2.getFile("file");
                type = multipartRequest2.getParameter("type");
                if (file != null) {
                    fileContent = FileUtil.getFileContent(file);
                    session.setAttribute("fileContent", fileContent);
                }
            }
            ReportEntity entity = JsonUtil.getJsonToBean(fileContent, ReportEntity.class);
            if (entity.getContent() == null || "".equals(entity.getContent())) {
                writeObjectToJson(resp, ActionResult.fail("导入失败，数据有误"));
            }
            StringJoiner joiner = new StringJoiner("、");
            ReportEntity reportEntity = reportService.GetInfo(entity.getId());
            if (reportEntity != null) {
                joiner.add("ID");
            }
            if (reportService.IsExistByFullName(entity.getFullName(), null)) {
                joiner.add("名称");
            }
            if (ObjectUtil.equal(type, "0") && joiner.length() > 0) {
                writeObjectToJson(resp, ActionResult.fail(joiner.toString() + "重复"));
            }
            if (ObjectUtil.equal(type, "1") && joiner.length() > 0) {
                String copyNum = UUID.randomUUID().toString().substring(0, 5);
                entity.setFullName(entity.getFullName() + ".副本" + copyNum);
                entity.setEnCode(entity.getEnCode() + copyNum);
                entity.setId(null);
            }
            String token = req.getHeader("Authorization");
            String userId = UserProvider.getLoginUserId();
            entity.setCreatorTime(new Date());
            entity.setCreatorUser(userId);
            entity.setUpdateTime(null);
            entity.setUpdateUser(null);
            entity.setDelFlag(0);
            reportService.Create(entity);
            writeObjectToJson(resp, ActionResult.success("导入成功"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            writeObjectToJson(resp, ActionResult.fail("导入失败，数据有误"));
        }
    }

    //初始化
    public void init(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ReportDefinition reportDef = reportRender.parseReport("classpath:template/template.ureport.xml");
        writeObjectToJson(resp, ActionResult.success(new ReportDefinitionWrapper(reportDef)));
    }

    //列表
    public void getList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String currentPage = req.getParameter("currentPage");
        String pageSize = req.getParameter("pageSize");
        String delFlag = req.getParameter("delFlag");

        PaginationReport paginationReport = new PaginationReport();
        paginationReport.setKeyword(req.getParameter("keyword"));
        if (ObjectUtil.isNotEmpty(currentPage)) {
            paginationReport.setCurrentPage(Long.parseLong(currentPage));
        }
        paginationReport.setCategory(req.getParameter("category"));
        if (ObjectUtil.isNotEmpty(pageSize)) {
            paginationReport.setPageSize(Long.parseLong(pageSize));
        }
        if (ObjectUtil.isNotEmpty(delFlag)) {
            paginationReport.setDelFlag(Integer.parseInt(delFlag));
        }

        List<ReportEntity> data = reportService.GetList(paginationReport);
        List<ReportListVO> list = JsonUtil.getJsonToList(data, ReportListVO.class);
        for (ReportListVO vo : list) {
            if (vo.getCreatorUser() != null && !vo.getCreatorUser().equals("")) {
                UserEntity entity = userService.getInfo(vo.getCreatorUser());
                if (entity != null) {
                    vo.setCreatorUser(entity.getRealName() + "/" + entity.getAccount());
                } else {
                    vo.setCreatorUser("");
                }
            }
            UserEntity entity1 = null;
            if (vo.getUpdateUser() != null && !vo.getUpdateUser().equals("")) {
                entity1 = userService.getInfo(vo.getUpdateUser());
                if (entity1 != null) {
                    vo.setUpdateUser(entity1.getRealName() + "/" + entity1.getAccount());
                } else {
                    vo.setUpdateUser("");
                }
            }
        }
        PaginationVO pagination = JsonUtil.getJsonToBean(paginationReport, PaginationVO.class);
        writeObjectToJson(resp, ActionResult.page(list, pagination));
    }

    //下拉
    public void Selector(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ReportEntity> data = reportService.GetList().stream().filter(t -> Objects.equals(t.getDelFlag(), 1)).collect(Collectors.toList());
        List<ReportSelectorVO> list = JsonUtil.getJsonToList(data, ReportSelectorVO.class);
        ListVO vo = new ListVO();
        vo.setList(list);
        writeObjectToJson(resp, ActionResult.success(vo));
    }

    //预览报表
    public void previewData(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String page = req.getParameter("page");
        String token = req.getHeader("Authorization");
        String isSwitch = req.getParameter("isSwitch");
        if ("preview".equals(id)) {
            try {
                //未保存文件在编辑器预览
                ReportDefinition reportDefinition = (ReportDefinition) TempObjectCache.getObject(token);
                Map<String, Object> parameters = buildParameters(req);
                ReportBuilder reportBuilder = new ReportBuilder();
                Connection connection = DynamicDataSourceUtil.getCurrentConnection();
                Report report;
                try {
                    if ("true".equals(isSwitch) && TempObjectCache.getObject(token + "_report") != null) {
                        report = (Report) TempObjectCache.getObject(token + "_report");
                    } else {
                        report = reportBuilder.buildReports(reportDefinition, parameters, connection);
                    }
                } finally {
                    JdbcUtils.closeConnection(connection);
                }
                UreportPreviewUtil previewUtil = new UreportPreviewUtil();
                HtmlReport htmlReport = null;
                //分页操作
                if ("".equals(page) || null == page || "0".equals(page)) {
                    htmlReport = previewUtil.loadReport(report, false, 0);
                } else {
                    htmlReport = previewUtil.loadReport(report, true, Integer.valueOf(page));
                    TempObjectCache.putObject(token + "_report", report);
                }
                htmlReport.setStyle(reportDefinition.getStyle());
                htmlReport.setSearchFormData(reportDefinition.buildSearchFormData(report.getContext().getDatasetMap(), parameters));
                writeObjectToJson(resp, ActionResult.success(htmlReport));
            } catch (QueryTimeoutException qt) {
                log.error(qt.getMessage());
                writeObjectToJson(resp, ActionResult.fail("查询数据库超时， 请减少查询的数据量"));
            } catch (Exception e) {
//                e.printStackTrace();
                log.error(e.getMessage(), e);
                writeObjectToJson(resp, ActionResult.fail("缓存已超时"));
            }
        } else {
            //通过id预览
            ReportEntity entity = reportService.GetInfo(id);
            if (Objects.equals(entity.getDelFlag(), 0)) {
                writeObjectToJson(resp, ActionResult.fail("报表已被禁用"));
            }
            Map<String, Object> parameters = buildParameters(req);
            UreportPreviewUtil previewUtil = new UreportPreviewUtil();
            Connection connection = null;
            try {
                connection = DynamicDataSourceUtil.getCurrentConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            ReportPreviewVO vo;
            if ("".equals(page) || null == page || "0".equals(page)) {
                vo = previewUtil.preview(entity, false, 1, parameters, connection, false);
            } else {
                vo = previewUtil.preview(entity, true, Integer.valueOf(page), parameters, connection, "true".equals(isSwitch));
            }
            vo.setDelFlag(entity.getDelFlag());
            try {
                connection.close();
            } catch (SQLException throwables) {
                log.error("点击预览报错:" + throwables.getMessage());
            }
            writeObjectToJson(resp, ActionResult.success(vo));
        }
    }

    //通过id打开到报表编辑器
    public void getInfo(HttpServletRequest req, HttpServletResponse resp, String id) throws ServletException, IOException {
        ReportEntity entity = reportService.GetInfo(id);
        ReportDefinition reportDefinition = null;
        if (entity == null) {
            writeObjectToJson(resp, ActionResult.fail("数据不存在"));
        }
        byte[] content = entity.getContent().getBytes("UTF-8");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
        reportDefinition = UreportUtil.parseReport(inputStream, entity.getFullName(), true);
        ReportDefinitionWrapper wrapper = new ReportDefinitionWrapper(reportDefinition);
        ReportInfoModel model = JsonUtil.getJsonToBean(entity, ReportInfoModel.class);
        writeObjectToJson(resp, ActionResult.successTOBase(wrapper, model));
    }

    //保存报表
    public void savaData(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String payload = RequestUtil.getPayload(req);
        String token = req.getHeader("Authorization");
        Map<String, Object> map = JsonUtil.stringToMap(payload);
        if (map == null) {
            writeObjectToJson(resp, ActionResult.fail("不能添加空数据"));
        } else {
            ReportCrForm reportCrForm = JsonUtil.getJsonToBean(map, ReportCrForm.class);
            reportCrForm.setContent(UreportUtil.decodeContent(reportCrForm.getContent()));
            ReportEntity entity = JsonUtil.getJsonToBean(reportCrForm, ReportEntity.class);
            if (reportService.IsExistByFullName(entity.getFullName(), entity.getId())) {
                writeObjectToJson(resp, ActionResult.fail("名称不能重复"));
            } else {
                //检查表格内容是否合规
                byte[] content = entity.getContent().getBytes("UTF-8");
                ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
                UreportUtil.parseReport(inputStream, entity.getFullName());

                String userId = UserProvider.getLoginUserId();
                entity.setCreatorUser(userId);
                reportService.Create(entity);
                Object id = entity.getId();
                writeObjectToJson(resp, ActionResult.success(id));
            }
        }
    }

    //修改
    public void updateData(HttpServletRequest req, HttpServletResponse resp, String id) throws ServletException, IOException {
        String token = req.getHeader("Authorization");
        String payload = RequestUtil.getPayload(req);
        Map<String, Object> map = JsonUtil.stringToMap(payload);
        if (id == null || id.equals("")) {
            writeObjectToJson(resp, ActionResult.fail("数据不存在，修改失败"));
        } else {
            ReportUpForm reportUpForm = JsonUtil.getJsonToBean(map, ReportUpForm.class);
            reportUpForm.setContent(UreportUtil.decodeContent(reportUpForm.getContent()));
            ReportEntity entity = JsonUtil.getJsonToBean(reportUpForm, ReportEntity.class);
            //entity.setContent(UreportUtil.decodeContent(entity.getContent()));
            if (reportService.IsExistByFullName(entity.getFullName(), id)) {
                writeObjectToJson(resp, ActionResult.fail("名称不能重复"));
            } else {
                //检查表格内容是否合规
                byte[] content = entity.getContent().getBytes("UTF-8");
                ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
                UreportUtil.parseReport(inputStream, entity.getFullName());

                String userId = UserProvider.getLoginUserId();
                entity.setUpdateUser(userId);
                boolean flags = reportService.Update(id, entity);
                if (flags) {
                    writeObjectToJson(resp, ActionResult.success("修改成功"));
                } else {
                    writeObjectToJson(resp, ActionResult.fail("数据不存在，修改失败"));
                }
            }
        }
    }

    //复制报表
    public void copyReport(HttpServletRequest req, HttpServletResponse resp, String id) throws ServletException, IOException {
        String token = req.getHeader("Authorization");
        if (id == null || id.equals("")) {
            writeObjectToJson(resp, ActionResult.fail("数据不存在，复制失败"));
        } else {
            ReportEntity entity = reportService.GetInfo(id);
            String userId = UserProvider.getLoginUserId();
            entity.setCreatorUser(userId);
            boolean flags = reportService.Copy(entity);
            if (flags) {
                writeObjectToJson(resp, ActionResult.success("复制成功"));
            } else {
                writeObjectToJson(resp, ActionResult.fail("数据不存在，复制失败"));
            }
        }
    }

    //删除
    public void deleteData(HttpServletRequest req, HttpServletResponse resp, String id) throws ServletException, IOException {
        if (id == null || id.equals("")) {
            writeObjectToJson(resp, ActionResult.fail("数据不存在，修改失败"));
        } else {
            ReportEntity entity = reportService.GetInfo(id);
            boolean flags = reportService.Delete(entity);
            if (flags) {
                writeObjectToJson(resp, ActionResult.success("删除成功"));
            } else {
                writeObjectToJson(resp, ActionResult.fail("数据不存在，修改失败"));
            }
        }
    }

    /*//通过id导出报表
    public void exportData(HttpServletRequest req, HttpServletResponse resp, String id, String type) throws ServletException, IOException {
        ReportEntity entity = reportService.GetInfo(id);
        if (entity == null) {
            writeObjectToJson(resp, ActionResult.fail("导出数据不能为空"));
        }
        String fileName = entity.getFullName();
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("content-Type", "application/x-download");
        Connection connection = null;
        try {
            if (!dataSourceConfig.isMultiTenancy()) {
                connection = dataSource.getConnection();
            } else {
                connection = JdbcUtil.getConn(dataSourceConfig.getUserName(), dataSourceConfig.getPassword(), dataSourceConfig.getUrl().replace("{dbName}", TenantHolder.getDatasourceName()));
            }
        } catch (Exception throwables) {
            throwables.printStackTrace();
            log.error("数据源错误：" + throwables.getMessage());
        }
        if (type.toLowerCase().equals("pdf")) {
            resp.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".pdf", "UTF-8"));
            OutputStream outputStream = resp.getOutputStream();
            UreportPdfUtil pdfUtil = new UreportPdfUtil();
            pdfUtil.buildPdfToConnection(entity, outputStream, connection);
            outputStream.flush();
            outputStream.close();
        } else if (type.toLowerCase().equals("word")) {
            resp.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".docx", "UTF-8"));
            UreportWordUtil wordUtil = new UreportWordUtil();
            XWPFDocument xwpfDocument = wordUtil.buildWord(entity, connection);
            xwpfDocument.write(resp.getOutputStream());
            xwpfDocument.close();
        } else if (type.toLowerCase().equals("excel")) {
            resp.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".xlsx", "UTF-8"));
            UreportExcelUtil excelUtil = new UreportExcelUtil();
            Workbook workbook = excelUtil.buildExcel(entity, false, false, connection);
            workbook.write(resp.getOutputStream());
            workbook.close();
        }
    }*/


    @Override
    public String url() {
        return "/Data";
    }

    protected void writeObjectToJson(HttpServletResponse resp, Object obj) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        ObjectMapper mapper = new ObjectMapper();
//        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        OutputStream out = resp.getOutputStream();
        try {
            mapper.writeValue(out, obj);
        } finally {
            out.flush();
            out.close();
        }
    }

}
