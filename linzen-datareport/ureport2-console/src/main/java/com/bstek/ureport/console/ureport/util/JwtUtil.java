package com.bstek.ureport.console.ureport.util;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.util.Date;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
public class JwtUtil {

    /**
     * 获取jwt中的携带的Redis的token
     * @param token
     * @return
     */
    public static String getRealToken(String token) {
        String realToken;
        try {
            SignedJWT sjwt = SignedJWT.parse(token.split(" ")[1]);
            JWTClaimsSet claims = sjwt.getJWTClaimsSet();
            realToken =  String.valueOf(claims.getClaim("token"));
            return realToken;
        } catch (Exception e) {
            return realToken = null;
        }
    }

    /**
     * getUserInfo
     * @param token
     * @return
     */
    public static JWTClaimsSet getUserInfo(String token) {
        String realToken;
        try {
            SignedJWT sjwt = SignedJWT.parse(token.split(" ")[1]);
            JWTClaimsSet claims = sjwt.getJWTClaimsSet();
            return claims;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取jwt中的过期时间
     * @param token
     * @return
     */
    public static Date getExp(String token){
        Date date;
        try {
            SignedJWT sjwt = SignedJWT.parse(token.split(" ")[1]);
            JWTClaimsSet claims = sjwt.getJWTClaimsSet();
            date = (Date)claims.getClaim("exp");
            return date;
        } catch (Exception e) {
            return date = null;
        }
    }

}
