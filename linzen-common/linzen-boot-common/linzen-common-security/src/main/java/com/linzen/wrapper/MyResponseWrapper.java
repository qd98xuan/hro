package com.linzen.wrapper;

import com.linzen.handler.IRestHandler;
import com.linzen.util.StringUtil;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Response封装
 * 处理类型:
 * application/json
 */
public class MyResponseWrapper extends HttpServletResponseWrapper {
    private ByteArrayOutputStream buffer;
    private ServletOutputStream out = null;
    private PrintWriter writer = null;

    private List<IRestHandler> handlers;
    private boolean supportResponse;

    public MyResponseWrapper(HttpServletResponse resp, List<IRestHandler> handlers) throws IOException {
        super(resp);
        this.handlers = handlers.stream().filter(IRestHandler::supportResponse).collect(Collectors.toList());
        supportResponse = !handlers.isEmpty();
        //返回处理器顺序翻转
        Collections.reverse(handlers);
        if(supportResponse) {
            buffer = new ByteArrayOutputStream();
        }
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if(supportResponse) {
            if (out == null) {
                out = new WapperedOutputStream(buffer);
            }
            return out;
        }else{
            return super.getOutputStream();
        }
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if(supportResponse) {
            if (writer == null) {
                writer = new PrintWriter(new OutputStreamWriter(buffer, this.getCharacterEncoding()));
            }
            return writer;
        }else{
            return super.getWriter();
        }
    }

    @Override
    public void flushBuffer() throws IOException {
        if(supportResponse) {
            if (out != null) {
                out.flush();
            }
            if (writer != null) {
                writer.flush();
            }
        }else{
            super.flushBuffer();
        }
    }

    @Override
    public void reset() {
        if(supportResponse) {
            buffer.reset();
        }else{
            super.reset();
        }
    }

    public void doFinal() throws IOException{
        if(supportResponse) {
            flushBuffer();
            if (buffer.size() > 0) {
                byte[] byteArray = buffer.toByteArray();
                if (supportResponse && isJsonBodyRequest()) {
                    getResponse().setContentLength(-1);
                    getResponse().setCharacterEncoding(StandardCharsets.UTF_8.name());
                    String data = new String(byteArray, StandardCharsets.UTF_8);
                    for (IRestHandler handler : handlers) {
                        data = handler.processResponse(data);
                    }
                    writeResponse(data);
                    return;
                }
                writeResponse(byteArray);
            }
        }
    }

    private void writeResponse(String responseString)
            throws IOException {
        PrintWriter out = getResponse().getWriter();
        out.write(responseString);
        out.flush();
        out.close();
    }

    private void writeResponse(byte[] responseData) throws IOException {
        ServletOutputStream outputStream = getResponse().getOutputStream();
        outputStream.write(responseData);
        outputStream.flush();
        outputStream.close();
    }

    private class WapperedOutputStream extends ServletOutputStream {
        private ByteArrayOutputStream bos = null;

        public WapperedOutputStream(ByteArrayOutputStream stream) throws IOException {
            bos = stream;
        }

        @Override
        public void write(int b) throws IOException {
            bos.write(b);
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener listener) {

        }
    }


    protected boolean isJsonBodyRequest(){
        String contentType = getResponse().getContentType();
        if(StringUtil.isNotEmpty(contentType)) {
            if (StringUtils.substringMatch(contentType, 0, MediaType.APPLICATION_JSON_VALUE)) {
                return true;
            }
        }
        return false;
    }
}