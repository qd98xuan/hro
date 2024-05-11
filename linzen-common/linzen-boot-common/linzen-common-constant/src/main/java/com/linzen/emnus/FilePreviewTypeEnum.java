package com.linzen.emnus;
/**
 * 文件预览方式
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public enum  FilePreviewTypeEnum {
    /**
     * yozo:永中预览;  doc:kk文档预览;
     */
    YOZO_ONLINE_PREVIEW("yozoOnlinePreview"),
    LOCAL_PREVIEW("localPreview");
    FilePreviewTypeEnum(String type) {
        this.type = type;
    }
    private String type;

    public String getType() {
        return type;
    }
}
