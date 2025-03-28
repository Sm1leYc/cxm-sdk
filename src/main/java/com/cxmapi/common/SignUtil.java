package com.cxmapi.common;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * @author yuanmingchun
 */
public class SignUtil {

    // 签名生成算法
    public static String getSign(String body, String secretKey, String nonce, String timestamp){
        Digester md5 = new Digester(DigestAlgorithm.SHA256);
        String content = (body == null ? "" : body) + "." + secretKey + "." + nonce + "." + timestamp;
        return md5.digestHex(content);
    }
}
