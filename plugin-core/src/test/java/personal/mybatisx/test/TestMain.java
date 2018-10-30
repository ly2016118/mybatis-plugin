package personal.mybatisx.test;

import java.io.IOException;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import personal.mybatisx.dao.UserDao;
import personal.mybatisx.model.User;


public class TestMain {
    
    
    private SqlSessionFactory sf;
    @Before
    public void before(){
        try {
            sf = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream("config/configuration.xml"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
//    @Test
    public void testSave(){
        SqlSession session = sf.openSession();
        UserDao dao = session.getMapper(UserDao.class);
        dao.findByPhone("1333344");
        session.close();
    }
//    @Test
    public void update(){
        SqlSession session = sf.openSession();
        UserDao dao = session.getMapper(UserDao.class);
        
        dao.update(new User());;
        session.close();
    }
    @Test
    public void update1(){
        SqlSession session = sf.openSession();
        UserDao dao = session.getMapper(UserDao.class);
        
        dao.update1(new User(), "1", 2);
        session.close(); 
    }

}
