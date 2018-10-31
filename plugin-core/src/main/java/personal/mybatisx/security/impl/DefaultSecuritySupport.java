package personal.mybatisx.security.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import personal.mybatisx.prop.SecuirtyPropeties;
import personal.mybatisx.prop.SecuritiesConfigratureBean;
import personal.mybatisx.security.DecryptAndEncryptType;
import personal.mybatisx.security.SecurityBeanFactory;
import personal.mybatisx.security.SecurityBeanFactory.SignType;
import personal.mybatisx.security.SecuritySupport;
/**
 * 加解密逻辑处理
 * @author lyu
 *
 */
public class DefaultSecuritySupport implements SecuritySupport {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultSecuritySupport.class);

    public String encrypt(String id, String input) {
        if(StringUtils.isBlank(input)) return null;
        return doEncryptOrDecrypt(id, input,true);
    }

    public String decrypt(String id, String input) {
        if(StringUtils.isBlank(input)) return null;
        return doEncryptOrDecrypt(id, input,false);
    }

    /**
     * 加解密操作
     * @param id
     * @param input
     * @param isEncrypt
     * @return
     */
    private String doEncryptOrDecrypt(String id, String input,boolean isEncrypt) {
        SecuritiesConfigratureBean securitiesConfigratureBean = SecuirtyPropeties.get(id);
        String signTypeStr = securitiesConfigratureBean.getSignType();
        SignType signType = SecurityBeanFactory.SignType.valueOf(signTypeStr);
        DecryptAndEncryptType securityBean = SecurityBeanFactory.build().getSecurityBean(signType);
        if(null == securityBean){
            // 抛异常
            throw new RuntimeException("没有找到指定ID的秘钥信息，id:"+id);
        }
        String result = "";
        String securityKey = SecurityBeanFactory.build().getSecurityKey(signType, securitiesConfigratureBean, isEncrypt);
        try {
            if(isEncrypt)result =  securityBean.encrypt(input, securityKey);
            else result =  securityBean.decrypt(input, securityKey);
        } catch (Exception e) {
            log.error("没有成功的加解密，请检查配置，明密文："+input,e);
            return input;
        }
        return result;
    }

}
