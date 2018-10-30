package personal.mybatisx.model;

public class Shop {

    private String name;
    private String type;
    
    
    
    public Shop(){}
    
    public Shop(String name, String type) {
        super();
        this.name = name;
        this.type = type;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    
    
}
