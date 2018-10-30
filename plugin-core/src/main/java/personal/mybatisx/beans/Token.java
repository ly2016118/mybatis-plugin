package personal.mybatisx.beans;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class Token {
    private static final String INDEX_REG = "[0]";
    //变量名
    private String variableName;
    //迭代次数
    private int times = 0;
    private Map<String ,String> index = new HashMap<String, String>();
    
//    跳出循环时 是不是集合
    
    public Token(){}
    public Token(String name) {
        this.variableName = name;
    }
    public String getVariableName() {
       
        return variableName;
    }
    public void appendVariableName(String variableName,boolean needAppendIndex) {
        if(needAppendIndex&&StringUtils.isNotBlank(this.variableName)) this.variableName+=INDEX_REG;
        if(StringUtils.isNotBlank(this.variableName)){
            this.variableName = this.variableName+"."+variableName;
        }
        else this.variableName = variableName;
    }
    public int getTimes() {
        return times;
    }
    public void setTimes(int times) {
        this.times = times;
    }

    public void setIndex(String index){
        if(StringUtils.isBlank(index)) return;
        this.index.put(index, null);
    }
    
    public boolean hasIndex(String index){
        if(StringUtils.isBlank(index)) return false;
        return this.index.containsKey(index);
        
    }
}
