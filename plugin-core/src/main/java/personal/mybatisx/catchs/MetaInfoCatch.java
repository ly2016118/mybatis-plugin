package personal.mybatisx.catchs;

/**
 * 缓存源信息
 * @author lyu
 *
 */
public interface MetaInfoCatch {
    
    
    

    public boolean catchInfo(String key,Object value);
    
    /**
     * 根据key 获得value
     * @param key
     * @return
     */
    public Object get(String key);
    /**
     * 根据key 获得value
     * @param key
     * @param clazz
     * @return
     */
    public <T>T get(String key,Class<T> clazz);
    
    /**
     * 刷新缓存
     */
    public void flushCath();
    
    
}
