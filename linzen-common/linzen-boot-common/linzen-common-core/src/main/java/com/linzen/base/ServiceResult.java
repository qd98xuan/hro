package com.linzen.base;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.linzen.base.vo.PageListVO;
import com.linzen.base.vo.PaginationVO;
import com.linzen.constant.MsgCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceResult<T> {

    @Schema(description = "状态码")
    private Integer code;

    @Schema(description = "返回信息")
    private String msg;

    @Schema(description = "返回数据")
    private T data;

    public static <T> ServiceResult<T> success() {
        ServiceResult<T> serviceResult = new ServiceResult<>();
        serviceResult.setCode(200);
        serviceResult.setMsg(MsgCode.SU000.get());
        return serviceResult;
    }

    public static <T> ServiceResult<T> success(String msg) {
        ServiceResult<T> serviceResult = new ServiceResult<>();
        serviceResult.setCode(200);
        serviceResult.setMsg(msg);
        return serviceResult;
    }

    public static <T> ServiceResult<T> success(T object) {
        ServiceResult<T> serviceResult = new ServiceResult<>();
        serviceResult.setData(object);
        serviceResult.setCode(200);
        serviceResult.setMsg(MsgCode.SU000.get());
        return serviceResult;
    }

    public static <T> ServiceResult<T> success(String msg, T object) {
        ServiceResult<T> serviceResult = new ServiceResult<>();
        serviceResult.setData(object);
        serviceResult.setCode(200);
        serviceResult.setMsg(msg);
        return serviceResult;
    }

    public static <T> ServiceResult<T> error(Integer code, String message) {
        ServiceResult<T> serviceResult = new ServiceResult<>();
        serviceResult.setCode(code);
        serviceResult.setMsg(message);
        return serviceResult;
    }

    public static ServiceResult<String> error(String msg, String data) {
        ServiceResult<String> serviceResult = new ServiceResult<>();
        serviceResult.setMsg(msg);
        serviceResult.setData(data);
        return serviceResult;
    }

    public static <T> ServiceResult<T> error(String msg) {
        ServiceResult<T> serviceResult = new ServiceResult<>();
        serviceResult.setMsg(msg);
        serviceResult.setCode(400);
        return serviceResult;
    }

    public static <T> ServiceResult<PageListVO<T>> pageList(List<T> list, PaginationVO pagination) {
        ServiceResult<PageListVO<T>> serviceResult = new ServiceResult<>();
        PageListVO<T> vo = new PageListVO<>();
        vo.setList(list);
        vo.setPagination(pagination);
        serviceResult.setData(vo);
        serviceResult.setCode(200);
        serviceResult.setMsg(MsgCode.SU000.get());
        return serviceResult;
    }

    public static <T> ServiceResult<DataInterfacePageListVO<T>> pageList(List<T> list, PaginationVO pagination, String dataProcessing) {
        ServiceResult<DataInterfacePageListVO<T>> serviceResult = new ServiceResult<>();
        DataInterfacePageListVO<T> vo = new DataInterfacePageListVO<>();
        vo.setList(list);
        vo.setPagination(pagination);
        vo.setDataProcessing(dataProcessing);
        serviceResult.setCode(200);
        serviceResult.setData(vo);
        serviceResult.setMsg(MsgCode.SU000.get());
        return serviceResult;
    }

}
