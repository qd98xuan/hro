package com.linzen.model.document;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class DocumentListVO {
    @Schema(description ="主键id")
    private String id;
    @Schema(description ="文件夹名称")
    private String fullName;
    @Schema(description ="文档分类(0-文件夹，1-文件)")
    private Integer type;
    @Schema(description ="创建日期")
    private long creatorTime;
    @Schema(description ="是否分享")
    private Integer isShare;
    @Schema(description ="大小")
    private String fileSize;
    @Schema(description ="父级Id")
    private String parentId;
    @Schema(description ="后缀名")
    private String fileExtension;
    @Schema(description ="文档下载地址")
    private String uploaderUrl;
    @Schema(description ="文件路径")
    private String filePath;
    @Schema(description ="是否支持预览")
    private String isPreview;
}
