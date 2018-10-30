package personal.mybatisx.security;

/**
 * 加密解密公约接口
 * @author lyu
 *
 */
public interface DecryptAndEncryptType {
    
    /**
     * 加密
     * @param input 明文
     * @param securityKey  秘钥
     * @return
     */
    public String encrypt(String input,String securityKey)throws Exception;
    
    /**
     * 解密
     * @param input 密文
     * @param securityKey 秘钥
     * @return
     */
    public String decrypt(String input,String securityKey)throws Exception;

}
