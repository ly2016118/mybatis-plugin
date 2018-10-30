package personal.mybatisx.model;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class House {
    
    private String address;
    private String color;
    private int size;
    private int userId;
    List<Person> persons;
    
    
    public House(){}
    
    public House(String address, String color, int userId) {
        super();
        this.address = address;
        this.color = color;
        this.userId = userId;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    
    @Override
    public String toString() {
        
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
    public List<Person> getPersons() {
        return persons;
    }
    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    

}
