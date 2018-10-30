package personal.mybatisx.model;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class User {
    
    private int id;
    private String name;
    private String phone;
    
    private Address addr;
    
    private List<House> houses;
    private String[] phones;
    
    private Map<String,House> mhouse;
    
    public int getId() {
        return id;
    }
    public User(){}
    public User( String name, String phone) {
        this.name = name;
        this.phone = phone;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public Address getAddr() {
        return addr;
    }
    public void setAddr(Address addr) {
        this.addr = addr;
    }
    @Override
    public String toString() {
        
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
    public List<House> getHouses() {
        return houses;
    }
    public String[] getPhones() {
        return phones;
    }
    public void setPhones(String[] phones) {
        this.phones = phones;
    }
    public void setHouses(List<House> houses) {
        this.houses = houses;
    }
    public Map<String, House> getMhouse() {
        return mhouse;
    }
    public void setMhouse(Map<String, House> mhouse) {
        this.mhouse = mhouse;
    }

    
    
}
