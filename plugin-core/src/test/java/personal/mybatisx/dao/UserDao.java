package personal.mybatisx.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import personal.mybatisx.annotations.MexDecrypt;
import personal.mybatisx.annotations.MexEncrypt;
import personal.mybatisx.annotations.MexNeededEncryptOrDecrypt;
import personal.mybatisx.annotations.MexRequest;
import personal.mybatisx.annotations.MexResponse;
import personal.mybatisx.enums.EncryptOrDecrypt;
import personal.mybatisx.model.House;
import personal.mybatisx.model.User;

@Repository
//接口必须注解 @MexNeededEncryptOrDecrypt 才会对此接口的方法进行扫描
@MexNeededEncryptOrDecrypt
public interface UserDao {
    
    @Insert("insert into t_user (name,phone) values (#{name},#{phone})")
    @MexRequest
    public void save(@MexEncrypt(properties={"phone"})User user);
    
    @Insert("insert into t_user (name,phone) values (#{name},#{phone})")
    @MexRequest
    public void saveUseRSA(@MexEncrypt(id="rsaId",properties={"phone"})User user);
    
    @MexRequest
    public void saveList(@MexEncrypt(properties={"phone"})List<User> user);
    
    @MexRequest
    public void saveList1(@MexEncrypt(properties={"phone"})List<User> user,String ha);
    
    @Update("update t_user set phone =#{phone} where id=#{id}")
    @MexRequest
    public void update(@MexEncrypt(properties={"phone"})User user);
    
    @Update("update t_user  set phone =#{phone} where id=#{id, jdbcType=INTEGER }")
    @MexRequest
    public void update2(@MexEncrypt(properties={"phone"})Map<String,Object> user);
    
    @Update("update t_user  set phone =#{param1.phone} where id=#{param1.id, jdbcType=INTEGER }")
    @MexRequest
    public void update7(@MexEncrypt(properties={"phone"})Map<String,Object> user,int a,String b);
    
    @Update("update t_user  set phone =#{param1.user.phone} where id=#{param1.user.id, jdbcType=INTEGER }")
    @MexRequest
    public void update8(@MexEncrypt(properties={"user.phone"})Map<String,User> user,int a,String b);
    
    @MexRequest
    public void update3(@MexEncrypt List<String> phones);
    
    @MexRequest
    public void update6(@MexEncrypt String[] phones);
    
    @Update("update t_user set phone =#{user.phone} where id=#{user.id}")
    @MexRequest
    public void update1(@Param("user")@MexEncrypt(properties={"phone"})User user,String id,int page);
    
    
    @Select("select * from t_user limit 2")
    @MexResponse(doType=EncryptOrDecrypt.DECRYPT,fields={"phone"})
    public List<User> findAll();
    
    
    @Select("select * from t_user where phone = #{phone}")
    @MexResponse(doType=EncryptOrDecrypt.DECRYPT,fields={"phone"})
    @MexRequest
    public User findByPhone(@Param("phone")@MexDecrypt String phone);
    
    @Select("select * from t_user where phone = #{0}")
    @MexResponse(doType=EncryptOrDecrypt.DECRYPT,fields={"phone"})
    @MexRequest
    public User findByPhone1(@MexDecrypt String phone);
    
    @Update("update t_user set phone =#{0} where id=#{1}")
    @MexRequest
    public void update4(@MexDecrypt String phone, int id);
    
    
    @Update("update t_user set phone =#{phone} where id=#{id}")
    @MexRequest
    public void update5(@MexDecrypt @Param("phone") String phone,@MexDecrypt @Param("id") String id);
    
    @MexResponse(doType=EncryptOrDecrypt.DECRYPT,fields={"phone","houses[{cx}].address","phones[0]"},id="rsaId")
    public User findUserAndHouses(@Param("id")int id);
    
    @MexResponse(doType=EncryptOrDecrypt.DECRYPT,fields={"phone"},id="rsaId")
    @Select("select * from t_user where id =17")
    public List<User> findByIdArray(String[] ids);
    
    @MexResponse(doType=EncryptOrDecrypt.DECRYPT,fields={"[{x}]"},id="rsaId")
    @Select("select phone from t_user where id in (13,14,15)")
    public String[] findUserPhonesReturnedArray();
    
    @MexResponse(doType=EncryptOrDecrypt.DECRYPT,fields={"[{x}]"},id="rsaId")
    @Select("select phone from t_phone where user_id =#{0}")
    public String[] findPhonesReturnedArray(int userId);
    
    @MexResponse(doType=EncryptOrDecrypt.DECRYPT,fields={"phone"},id="rsaId")
    @Select("select * from t_user where id in (13,14,15)")
    public User[] findUsersReturnedArray();
    
    @Select("select * from t_house limit 2")
    @MexResponse(doType=EncryptOrDecrypt.DECRYPT,fields={"phone"},id="rsaId")
    public List<House> findAllHouse();
    
    @Select("select * from t_house where user_id = #{id}")
    public List<House> findHouseById(int userId);
    
    @Insert("insert into t_house (address,color,user_id) values (#{address},#{color},#{userId})")
    @MexRequest
    public void saveHouse(@MexEncrypt(properties={"address"},id="rsaId")House house);
    
    

}
