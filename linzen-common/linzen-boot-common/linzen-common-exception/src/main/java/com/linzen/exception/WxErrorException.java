package com.linzen.exception;

/**
 * 微信异常封装
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class WxErrorException extends Exception {

  private static final long serialVersionUID = -6357149550353160810L;

  private WxError error;

  public WxErrorException(WxError error) {
    super(error.toString());
    this.error = error;
  }
  
  public WxErrorException(WxError error, Throwable cause) {
	    super(error.toString(), cause);
	    this.error = error;
  }
  
  public WxError getError() {
    return this.error;
  }

}
