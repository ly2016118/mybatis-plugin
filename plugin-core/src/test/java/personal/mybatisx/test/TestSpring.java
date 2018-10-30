package personal.mybatisx.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import personal.mybatisx.dao.UserDao;
import personal.mybatisx.model.House;
import personal.mybatisx.model.User;
import personal.mybatisx.prop.SecuirtyPropeties;
import personal.mybatisx.prop.SecuritiesConfigratureBean;
import personal.mybatisx.security.SecurityBeanFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:config/applicationContext.xml"})
public class TestSpring {
    
    @Autowired
    private UserDao dao;
    String privateKey="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKuhJi58c0dmNcMUB7lOZTDWpwccKqz9fZtvecyx2+fmvOS5mrKUX2j7TBh+SPw3KLRK+wX77iOC3sNyT/rqdJx4BICXYikf57cna51kTS/9ofgQTNZo9Tc00z2R7fLxoImFVsq5KnskVvgw8Ld2mDbYVGT5OeLlan6m11VHozSFAgMBAAECgYAp4KVHXHhoaIkfs2h7D6TjOM0CkB3dnfCjF505lABbYlKe4KbdghSRsheMNmwqlm7pJw5/FEyz/3/JYsAfMkwHZl/H+lxUJ4JFTcYS4ggKclcvBGRbR7A5YxK6mNG3rZxEt09m9IdRKmyHbc5HYlvGMvEA+kuTcPoP1ANCkDB+9QJBAPLGDaBh7+y8jzX9NztGjACL1r2hXBcSHhM1JHykDgCWUNmldJ4yswLOvNbh/rpj2rWHdmQ4+b8bCh0Nk26pLwMCQQC0+tp6D8q9hxEU3n/mq3pHcCzS/fYh/wArJB1ZSjTeiMyh9RULvS189ueKDqN3CwggD0pjjxMuMduSLOhj2pPXAkAn1UktFISqQjH5OMfUKPoVqNweFximDGn02tZWwRAvFnrDizs8LbNjdYpUc5Y4/ONIv6Y0QYjwyz34kF6tXIMDAkEAnOhwyyst56xWzvUphuBdH9rNwhCVeVwQ1VZ2a5e+xsFsSW6nVIosChA3JqcSan+SB9m58R92zbJEYDO3N/ul+QJBAMR7sWueZWH6FosWPFZCB82v+2T90Ay27H7jZ+u/mbdouD7eLVWAVE83JRClIL8gSd+J0OfM0YC3qxLF5xEkp4A=";
    String publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCroSYufHNHZjXDFAe5TmUw1qcHHCqs/X2bb3nMsdvn5rzkuZqylF9o+0wYfkj8Nyi0SvsF++4jgt7Dck/66nSceASAl2IpH+e3J2udZE0v/aH4EEzWaPU3NNM9ke3y8aCJhVbKuSp7JFb4MPC3dpg22FRk+Tni5Wp+ptdVR6M0hQIDAQAB";
    
    @Before
    public void before(){
        //注册配置文件
        SecuritiesConfigratureBean configBean = new SecuritiesConfigratureBean();
        //默认配置ID 为空时，取此ID
        configBean.setId(SecuirtyPropeties.DEFAULT_CONFIG_KEY);
        configBean.setSimpleKey("12345678");
        configBean.setSignType(SecurityBeanFactory.SignType.MD5.name());
        SecuirtyPropeties.put(SecuirtyPropeties.DEFAULT_CONFIG_KEY, configBean);
        
        //设置一套RSA
        SecuritiesConfigratureBean rsaConfigBean = new SecuritiesConfigratureBean();
        rsaConfigBean.setId("rsaId");
        rsaConfigBean.setPriKey(privateKey);
        rsaConfigBean.setPubKey(publicKey);
        rsaConfigBean.setSignType(SecurityBeanFactory.SignType.RSA.name());
        SecuirtyPropeties.put("rsaId", rsaConfigBean);
    }
    
    @Test
    public void mainTest(){
//        saveWithRsa();
//        save();
//        saveList();
//        saveList1();
//        update1();
//        update2();
//        update3();
//        update4();
//        update6();
//        update7();
//        update8();
//        findAll();
//        findByPhone();
//        saveHouse();
        findUserAndHouses();
//        findByIdArray();
//        findUserPhonseReturnedArray();
//        findUsersReturnedArray();
    }
    
    public void save(){
//        for(int i = 0 ;i<10;i++){
//            User user = new User("测试"+i,"1880000000"+i); 
//            dao.save(user);
//        }
      User user = new User("测试","1880000000"); 
      dao.save(user);
    }
    
    public void saveWithRsa(){
        User user = new User("测试","1880000001"); 
        dao.saveUseRSA(user);
    }
    
    public void saveList(){
        List<User> list = new ArrayList<User>();
        for(int i = 0 ;i<10;i++){
            User user = new User("测试"+i,"1880000000"+i); 
            list.add(user);
        }
        dao.saveList(list);
    }
    public void saveList1(){
        List<User> list = new ArrayList<User>();
        for(int i = 0 ;i<10;i++){
            User user = new User("测试"+i,"1880000000"+i); 
            list.add(user);
        }
        dao.saveList1(list,"haio");
    }

    public void update1(){
        User user = new User();
        user.setPhone("1880000000");
        user.setId(25);
        dao.update1(user,"1",2);

    }
    
    public void update2(){
        Map<String,Object> user = new HashMap<String, Object>();
        user.put("phone", "1880000001");
        user.put("id", 27);
        dao.update2(user);
    }
    
    public void update3(){
        
        List<String> phones = new ArrayList<String>();
        phones.add("18800000001");
        phones.add("18800000002");
        dao.update3(phones);
    }
    
    public void update4(){
        dao.update4("18811100001", 126);
    }
    
    public void update6(){
        String[] phones = new String[]{"1880000000","1880000001"};
        dao.update6(phones);
    }
    
    public void update7(){
        Map<String,Object> user = new HashMap<String, Object>();
        user.put("phone", "1880000001");
        user.put("id", 1);
        dao.update7(user, 1, "haha");
    }
    
    public void update8(){
        Map<String,User> user = new HashMap<String, User>();
        User u = new User("", "1880000001x");
        u.setId(27);
        user.put("user", u);
        
        dao.update8(user, 1, "haha");
    }
    public void findAll(){
        List<User> all = dao.findAll();
        System.out.println("all:"+all);
    }

    public void findByPhone(){
        User findByPhone = dao.findByPhone("18800000001");
        System.out.println(findByPhone);
    }
    
    public void saveHouse(){
        House h = new House("北京xx", "redxc", 27);
        dao.saveHouse(h);
    }
    
    
    public void findUserAndHouses(){
        
        User u = dao.findUserAndHouses(27);
//        String address = u.getHouses().get(0).getAddress();
        System.out.println(u.getHouses());
        for(String phone:u.getPhones()){
            System.out.println(phone);
        }
        
    }
    
    public void findByIdArray(){
        
        List<User> findByIdArray = dao.findByIdArray(new String[]{"1"});
        System.out.println(findByIdArray);
        
    }
    
    public void findUserPhonseReturnedArray(){
        String[] userPhones = dao.findUserPhonesReturnedArray();
        for(String phone:userPhones){
            System.out.println(phone);
            
        }
    }
    
    public void findUsersReturnedArray(){
        User[] users = dao.findUsersReturnedArray();
        for(User u:users)
        System.out.println(u);
    }
}
