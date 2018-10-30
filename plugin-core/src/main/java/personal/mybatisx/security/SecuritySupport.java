package personal.mybatisx.security;

/**
 * 安全接口，加解密规约
 * @author lyu
 *
 */
public interface SecuritySupport {
    
    /**<per>
     * 加密
     * @param id 指定使用特定ID的加密配置 进行加密
     * @param input 需要加密的数据
     * @return  密文
     */
    public String encrypt(String id,String input);
    
    
    /**<per>
     * 解密
     * @param id 指定使用特定ID解密配置
     * @param input 需要解密的内容
     * @return 明文
     */
    public String decrypt(String id,String input);

}
