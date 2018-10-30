package personal.mybatisx.prop;


public class SecuritiesConfigratureBean {
    
    private String id;
    private String signType;
    private String pubKey;
    private String priKey;
    //加密和解密都是一个key时配置此项
    private String simpleKey;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getSignType() {
        return signType;
    }
    public void setSignType(String signType) {
        this.signType = signType;
    }
    public String getPubKey() {
        return pubKey;
    }
    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }
    public String getPriKey() {
        return priKey;
    }
    public void setPriKey(String priKey) {
        this.priKey = priKey;
    }
    public String getSimpleKey() {
        return simpleKey;
    }
    public void setSimpleKey(String simpleKey) {
        this.simpleKey = simpleKey;
    }

    


}
