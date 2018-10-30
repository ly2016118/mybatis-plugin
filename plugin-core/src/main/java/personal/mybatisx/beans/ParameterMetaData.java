package personal.mybatisx.beans;
/**
 * 参数信息
 * @author lyu
 *
 */
public class ParameterMetaData {
    
    private String id;
    //参数下标
    private int index;
    //参数名称
    private String name;
    //参数名被改过  当开发人员使用了 @Param 注解
    private boolean nameChanged;

    private String[] properties;

    //是加密吗？  标注是加密还是解密
    private boolean encrypt=false;
    //此参数决定mybatis如何封装参数的key
//    private boolean useActualParamName;
    
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isNameChanged() {
        return nameChanged;
    }
    public void setNameChanged(boolean nameChanged) {
        this.nameChanged = nameChanged;
    }
    public String[] getProperties() {
        return properties;
    }
    public void setProperties(String[] properties) {
        this.properties = properties;
    }
    public boolean isEncrypt() {
        return encrypt;
    }
    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    //获得mybatis 的参数封装key
    public String getActualParamName(boolean useActualParamName) {
        if(useActualParamName) return "arg"+index;
        else return index+"";
    }

    
    

}
