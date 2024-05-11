package com.linzen.base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class PageModel {
    /**
     * 每页行数
     */
    private int rows;
    /**
     *  当前页
     */
    private int page;
    /**
     * 总页数
     */
    private long total;
    /**
     * 总记录数
     */
    private long records;
    /**
     *  排序列
     */
    private String sidx;
    /**
     *  排序类型
     */
    private String sord;
    /**
     *  查询条件
     */
    private String queryJson;
    /**
     * 查询关键字
     */
    private String keyword;

    public <T> List<T> setData(List<T> data, long records) {
        this.records = records;
        if(this.records>0){
            this.total = this.records % this.rows == 0 ? this.records / this.rows : this.records/this.rows+1;
        }else {
            this.total = 0;
        }
        return data;
    }
}
