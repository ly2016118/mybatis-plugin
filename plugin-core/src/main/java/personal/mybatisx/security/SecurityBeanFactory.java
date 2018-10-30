package personal.mybatisx.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import personal.mybatisx.prop.SecuritiesConfigratureBean;

/**
 * 存储注册的 加解密方法
 * @author lyu
 *
 */
public class SecurityBeanFactory {
    
    //储存加解密的实现类
    private static final Map<SignType,DecryptAndEncryptType> SignTypeFactoryBean = new ConcurrentHashMap<SecurityBeanFactory.SignType, DecryptAndEncryptType>();
    
    
    private SecurityBeanFactory(){}
    
    public static SecurityBeanFactory build(){
        return BeanCreater.build();
    }
    
    private static class BeanCreater{
        private static final SecurityBeanFactory BEAN_FACTORY = new SecurityBeanFactory();
        public static SecurityBeanFactory build(){
            return BEAN_FACTORY;
        }
    }

    /**
     * 获得加解密类
     * @param signType
     * @return
     */
    public DecryptAndEncryptType getSecurityBean(SignType signType){
        return SignTypeFactoryBean.get(signType);
        
        
    }
    
    /**
     * 注册加密类
     * @param signType
     * @param type
     */
    public SecurityBeanFactory registerSignTypeBean(SignType signType,DecryptAndEncryptType bean){
        SignTypeFactoryBean.put(signType, bean);
        return this;
    }
    
    
    public String getSecurityKey(SignType type,SecuritiesConfigratureBean config,boolean isEncrypt){
        if(SignType.AES == type||SignType.MD5 == type) return config.getSimpleKey();
        //加密
        if(SignType.RSA == type&&isEncrypt) return config.getPriKey();
        if(SignType.RSA == type&&!isEncrypt) return config.getPubKey();
        throw new RuntimeException("没有找到相应配置的秘钥，请检查配置");
        
    }
    
    /**
     * 加密方式枚举
     * @author lyu
     *
     */
    public static enum SignType{
        RSA,MD5,AES
    }
}
