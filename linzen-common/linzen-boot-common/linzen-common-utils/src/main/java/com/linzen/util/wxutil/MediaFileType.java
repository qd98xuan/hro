package com.linzen.util.wxutil;

/**
 * 多媒体文件类型
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public enum MediaFileType {
	/**
	 * 图文
	 */
	News("news"),
	/**
	 * 图片
	 */
	Image("image"),
	/**
	 * 语音
	 */
	Voice("voice"),
	/**
	 * 视频
	 */
	Video("video"),
	/**
	 * 缩略图
	 */
	Thumb("thumb"),
	/**
	 * 文件
	 */
	File("file");

	private String message;

	MediaFileType(String message) {
	     this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
