package com.linzen.util;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
public class DownUtil {

    /**
     * 下载excel
     *
     * @param fileName excel名称
     * @param workbook
     */
    public static void dowloadExcel(Workbook workbook, String fileName) {
        try {
            HttpServletResponse response = ServletUtil.getResponse();
            assert response != null;
            response.setCharacterEncoding(Constants.UTF8);
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, Constants.UTF8));
            @Cleanup ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载文件
     *
     * @param file     文件
     * @param fileName 订单信息.pdf
     */
    public static void dowloadFile(File file, String fileName) {
        HttpServletResponse response = ServletUtil.getResponse();
        HttpServletRequest request = ServletUtil.getRequest();
        try {
            @Cleanup InputStream is = new FileInputStream(file);
            @Cleanup BufferedInputStream bis = new BufferedInputStream(is);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/x-download");
            //编码的文件名字,关于中文乱码的改造
            String codeFileName = "";
            String agent = request.getHeader("USER-AGENT").toLowerCase();
            if (-1 != agent.indexOf("msie") || -1 != agent.indexOf("trident")) {
                //IE
                codeFileName = URLEncoder.encode(fileName, "UTF-8");
            } else if (-1 != agent.indexOf("mozilla")) {
                //火狐，谷歌
                codeFileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            } else {
                codeFileName = URLEncoder.encode(fileName, "UTF-8");
            }
            response.setHeader("Content-Disposition", "attachment;filename=\"" + codeFileName + "\"");
            @Cleanup OutputStream os = response.getOutputStream();
            int i;
            byte[] buff = new byte[1024 * 8];
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
            }
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载文件
     *
     * @param paths    路径
     * @param fileName 订单信息.pdf
     */
    public static void dowloadFile(String paths, String fileName) {
        HttpServletResponse response = ServletUtil.getResponse();
        HttpServletRequest request = ServletUtil.getRequest();
        try {
            @Cleanup InputStream is = new FileInputStream(new File(XSSEscape.escapePath(paths)));
            @Cleanup BufferedInputStream bis = new BufferedInputStream(is);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain");
            if (fileName.endsWith(".pdf")) {
                response.setContentType("application/pdf");
            }
            if (fileName.contains(".svg")) {
                response.setContentType("image/svg+xml");
            }
            //编码的文件名字,关于中文乱码的改造
            String codeFileName = "";
            String agent = request.getHeader("USER-AGENT").toLowerCase();
            if (-1 != agent.indexOf("msie") || -1 != agent.indexOf("trident")) {
                //IE
                codeFileName = URLEncoder.encode(fileName, "UTF-8");
            } else if (-1 != agent.indexOf("mozilla")) {
                //火狐，谷歌
                codeFileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            } else {
                codeFileName = URLEncoder.encode(fileName, "UTF-8");
            }
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(codeFileName.getBytes(), "utf-8"));
            @Cleanup OutputStream os = response.getOutputStream();
            int i;
            byte[] buff = new byte[1024 * 8];
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
            }
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示验证码
     */
    public static void downCode(Integer stringLength) {
        HttpServletResponse response = DownUtil.getResponse();
        CodeUtil codeUtil = new CodeUtil();
        codeUtil.getRandcode(response, stringLength);
    }

    /**
     * 流返回界面
     */
    public static void write(BufferedImage image) {
        try {
            HttpServletResponse response = DownUtil.getResponse();
            //将内存中的图片通过流动形式输出到客户端
            ImageIO.write(image, "PNG", response.getOutputStream());
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * 设置img的response
     */
    public static HttpServletResponse getResponse() {
        HttpServletResponse response = ServletUtil.getResponse();
        response.setCharacterEncoding("UTF-8");
        //设置相应类型,告诉浏览器输出的内容为图片
        response.setContentType("image/jpeg");
        //设置响应头信息，告诉浏览器不要缓存此内容
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expire", 0);
        return response;
    }

    /**
     * 显示预览的pdf
     */
    public static void dowloadFile(File file) {
        try {
            @Cleanup OutputStream outputStream = null;
            @Cleanup InputStream in = null;
            //读取指定路径下面的文件
            in = new FileInputStream(file);
            ServletUtil.getResponse().setContentType("application/pdf;charset=utf-8");
            //编码的文件名字,关于中文乱码的改造
            String codeFileName = "";
            String agent = ServletUtil.getRequest().getHeader("USER-AGENT").toLowerCase();
            if (-1 != agent.indexOf("msie") || -1 != agent.indexOf("trident")) {
                //IE
                codeFileName = URLEncoder.encode(file.getName(), "UTF-8");
            } else if (-1 != agent.indexOf("mozilla")) {
                //火狐，谷歌
                codeFileName = new String(file.getName().getBytes("UTF-8"), StandardCharsets.ISO_8859_1);
            } else {
                codeFileName = URLEncoder.encode(file.getName(), "UTF-8");
            }
            ServletUtil.getResponse().setHeader("Content-Disposition", "filename=" + codeFileName);
            outputStream = new BufferedOutputStream(ServletUtil.getResponse().getOutputStream());
            //创建存放文件内容的数组
            byte[] buff = new byte[1024];
            //所读取的内容使用n来接收
            int n;
            //当没有读取完时,继续读取,循环
            while ((n = in.read(buff)) != -1) {
                //将字节数组的数据全部写入到输出流中
                outputStream.write(buff, 0, n);
            }
            //强制将缓存区的数据进行输出
            outputStream.flush();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * 显示文件
     */
    public static void dowloadFile(String file) {
        try {
            HttpServletResponse response = ServletUtil.getResponse();
            @Cleanup ServletOutputStream outputStream1 = response.getOutputStream();
            @Cleanup OutputStream outputStream = null;
            @Cleanup InputStream in = null;
            //读取指定路径下面的文件
            in = new FileInputStream(file);
            outputStream = new BufferedOutputStream(outputStream1);
            //创建存放文件内容的数组
            byte[] buff = new byte[1024];
            //所读取的内容使用n来接收
            int n;
            //当没有读取完时,继续读取,循环
            while ((n = in.read(buff)) != -1) {
                //将字节数组的数据全部写入到输出流中
                outputStream.write(buff, 0, n);
            }
            //强制将缓存区的数据进行输出
            outputStream.flush();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * 下载svg
     *
     * @param file
     */
    public static void dowloadSvgFile(File file) {
        try {
            @Cleanup OutputStream outputStream = null;
            @Cleanup InputStream in = null;
            //读取指定路径下面的文件
            in = new FileInputStream(file);
            ServletUtil.getResponse().setContentType("image/svg+xml;charset=utf-8");
            //编码的文件名字,关于中文乱码的改造
            String codeFileName = "";
            String agent = ServletUtil.getRequest().getHeader("USER-AGENT").toLowerCase();
            if (-1 != agent.indexOf("msie") || -1 != agent.indexOf("trident")) {
                //IE
                codeFileName = URLEncoder.encode(file.getName(), "UTF-8");
            } else if (-1 != agent.indexOf("mozilla")) {
                //火狐，谷歌
                codeFileName = new String(file.getName().getBytes("UTF-8"), "iso-8859-1");
            } else {
                codeFileName = URLEncoder.encode(file.getName(), "UTF-8");
            }
            ServletUtil.getResponse().setHeader("Content-Disposition", "filename=" + codeFileName);
            outputStream = new BufferedOutputStream(ServletUtil.getResponse().getOutputStream());
            //创建存放文件内容的数组
            byte[] buff = new byte[1024];
            //所读取的内容使用n来接收
            int n;
            //当没有读取完时,继续读取,循环
            while ((n = in.read(buff)) != -1) {
                //将字节数组的数据全部写入到输出流中
                outputStream.write(buff, 0, n);
            }
            //强制将缓存区的数据进行输出
            outputStream.flush();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * 下载文件
     *
     * @param str
     * @param fileName
     */
    public static Boolean downloadFile(String str, String fileName) {
        HttpServletResponse response = ServletUtil.getResponse();
        HttpServletRequest request = ServletUtil.getRequest();
        OutputStream os = null;
        try {
            response.reset();
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes(), "ISO8859-1"));
            byte[] bytes = str.getBytes("utf-8");
            os = response.getOutputStream();
            // 将字节流传入到响应流里,响应到浏览器
            os.write(bytes);
            os.close();
            return true;
        } catch (Exception ex) {
            log.error("导出失败:", ex);
            throw new RuntimeException("导出失败");
        } finally {
            try {
                if (null != os) {
                    os.close();
                }
            } catch (IOException ioEx) {
                log.error("导出失败:", ioEx);
            }
        }
    }
}
