package personal.mybatisx.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;

import personal.mybatisx.beans.Token;
import personal.mybatisx.model.Address;
import personal.mybatisx.model.House;
import personal.mybatisx.model.Person;
import personal.mybatisx.model.Shop;
import personal.mybatisx.model.User;

public class TestM {
    private static final Pattern pattern = Pattern.compile("\\{(.+?)\\}");
    public static void main(String[] args) throws Exception {
//       User u = new User();
//       u.setName("haha");
//       Address addr = new Address();
//       u.setAddr(addr);
//       addr.setCity("hahaodajso");
//       Method[] methods = u.getClass().getMethods();
//       for(Method m:methods)
//       System.out.println(m.getName());
       
//       String property = BeanUtilsBean2.getInstance().getProperty(u, "addr.city");
//       System.out.println(property);
//       BeanUtilsBean2.getInstance().setProperty(u, "addr.city", "北京");
//       String property1 = BeanUtilsBean2.getInstance().getProperty(u, "addr.city");
//       System.out.println(property1);
       
//         Map<String ,User> map = new HashMap<String, User>();
//         map.put("addr", u);
//       String property = BeanUtilsBean2.getInstance().getProperty(map, "name");
//       System.out.println(property);
        test();
        
//        String xx = "dajo#{ii}dajo";
//        
//        Matcher m = pattern.matcher(xx);
//        if(m.find()){
//            System.out.println("find");
//        }
        
    }

    
    
    public  static void test(){
        
        User u = new User();
        u.setName("haha");
        u.setPhone("18800000000");
        //两个电话
        String[] phones = new String[]{"18880000000","13300000000"};
        u.setPhones(phones);
        //--------------------------------------
        Address addr = new Address("成都","四川");
        u.setAddr(addr);
        
        List<Shop> shops = new ArrayList<Shop>();
        Shop sp = new Shop("XX水果店", "百货");
        Shop sp1 = new Shop("XX超时", "百货");
        shops.add(sp);
        shops.add(sp1);
        addr.setShops(shops);
        //------------------------------------
        List<House> houses = new ArrayList<House>();
        House h1 = new House("上海", "白色", 120);
        //住两人
        List<Person> ps = new ArrayList<Person>();
        Person p = new Person("张三", 18);
        ps.add(p);
        Person p1 = new Person("李四", 20);
        ps.add(p1);
        h1.setPersons(ps);
        
        //空房
        House h2 = new House("重庆", "红色", 220);
        houses.add(h1);
        houses.add(h2);
        u.setHouses(houses);

        //------------------------------------------------
        
        House h3 = new House("天津", "红色", 220);
        Map<String,House> mh = new HashMap<String, House>();
        mh.put("h3", h3);
        u.setMhouse(mh);
        House[] h = new House[]{h1,h2,h3};
        List<House> hs = new ArrayList<House>();
        hs.add(h3);
        hs.add(h2);
        hs.add(h1);
        MetaObject m =  MetaObject.forObject(phones, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
        List<String > propNames = new ArrayList<String>();
        //拿到所有房子下住的人的姓名
        String propName = "houses[{x}].persons[{d}]";
        String propName1 = "addr.shops[{d}].name";
        String propName2 = "phones[{d}]";
        String propName3 = "address";
//        propNames.add(propName);
//        propNames.add(propName1);
//        propNames.add(propName2);
//        Object value2 = m.getValue("[0].address");
//        System.out.println(value2);
        propNames.add(propName3);
        
      Object value2 = m.getValue("[0]");
      System.out.println(value2);
        
//        for(String pname:propNames){
//            PropertyTokenizer pt = new PropertyTokenizer(pname);
//            List<String> realOgnlPropNames = testx(pt, null, m, pname,new Token(pt.getName()==null?pt.getIndex():pt.getName()));
//            System.out.println(realOgnlPropNames);
//            //循环拿到所有属性值
//            for(String realOgnlPropName:realOgnlPropNames){
//                Object value = m.getValue(realOgnlPropName);
//                System.out.println(value);
//                
//            }
//        }
    }
    
    
    public static List<String> testx(PropertyTokenizer pt ,List<String> props,MetaObject metaObject,String fullName,Token token) {
        if(null == props) props = new LinkedList<String>();
        String index = pt.getIndex();
        Object originalObject =null;
        if(fullName.startsWith("[")&&props.size()==0){
            originalObject = metaObject.getOriginalObject();
            resolver(props, originalObject, index, fullName);
            
            if(pt.hasNext()){
                token.appendVariableName("[0]."+pt.next().getName(),true);
                return testx(pt.next(), props, metaObject,fullName,token);
            }
            else return props;
        }else{
            originalObject = metaObject.getValue(token.getVariableName());
        }
        
        //没有子节点，跳出递归的条件
        if(!pt.hasNext()){
            //判断是否是第一次解析
            if(props.size() == 0 &&StringUtils.isBlank(index)){
                props.add(fullName);
            }
            //判断是否有表达式
            else if(StringUtils.isNotBlank(index)){
                Matcher m = pattern.matcher(index);
                if(m.find()){
                    int size = getCollectionSize(originalObject);
                    if(props.size() == 0 ){
                      //是表达式，需要替换
                        for(int i=0;i<size;i++){
                            String newPropName = fullName.replace(index, i+"");
                            props.add(newPropName);
                        }
                    }else{
                      //循环原有属性节点
                        List<String> sub = new LinkedList<String>();
                        List<String> remove = new LinkedList<String>();
                      //需要替换表达式
                        for(int i=0;i<props.size();i++){
                            String oldNameProp = props.get(i);
                            String newNameProp = oldNameProp.replace(index, i+"");
                            props.set(i, newNameProp);
                            resolveSubProp(metaObject, index, sub, remove, oldNameProp, newNameProp, newNameProp);
                        }
                        props.removeAll(remove);
                        props.addAll(sub);
                    }
                }else{
                    Integer.parseInt(index);
                }
            }
            return props;
        }
//        if(null == token) 
        PropertyTokenizer next = pt.next();
        int currentSize = 0;
        
        //非集合
        if(StringUtils.isBlank(index)){
//            不是集合 不需要新增元素，无需解析，直接解析下一个元素
            token.appendVariableName(next.getName(),false);
            return testx(pt.next(), props, metaObject,fullName,token);
        }
        //如果是map ，不需要解析下标
        else if(originalObject instanceof Map){
            token.appendVariableName(next.getIndexedName(),false);
           return testx(pt.next(), props, metaObject,fullName,token);
        }
        //如果是集合或数组
        else {
            currentSize = getCollectionSize(originalObject);  
        }
        Matcher m = pattern.matcher(index);
        //不是表达式，则说明已经指定值 直接解析下一个节点
          if(!m.find()){
              token.appendVariableName(next.getIndexedName(),false);
//              if()props.add(e)
             return testx(pt.next(), props, metaObject,fullName,token);
          }
         //判断现有集合是否为空,如果为空，则认为是第一次计算集合元素
          if(props.size() == 0){
              //是表达式，需要替换
              for(int i=0;i<currentSize;i++){
                  String newPropName = fullName.replace(index, i+"");
                  
                  String nprop= newPropName.substring(0, newPropName.lastIndexOf("["));
                //需要获得当前元素下子集合个数
                  MetaObject metaObjectForProperty = metaObject.metaObjectForProperty(nprop);
                  Object originalObject2 = metaObjectForProperty.getOriginalObject();
                  int subsize = getCollectionSize(originalObject2);
                  //如果子集合无元素，证明是无效元素，无效做操作
                  if(subsize != 0) {
                      props.add(newPropName);
                  }
              }
          //不为空 证明已经解析过前面节点，此时需要循环原集合    
          }else{
              //循环原有属性节点
              List<String> sub = new LinkedList<String>();
              List<String> remove = new LinkedList<String>();
              for(int i = 0;i<props.size();i++){
                  //  拿出原有值，进行替换
                  String oldNameProp = props.get(i);
                  String newNameProp = oldNameProp.replace(index, i+"");
                  props.set(i, newNameProp);
                  String nprop = newNameProp.replace(pt.getChildren(), "");
                  resolveSubProp(metaObject, index, sub, remove, oldNameProp, newNameProp, nprop);
              }
              props.removeAll(remove);
              props.addAll(sub);
        }
          token.appendVariableName(next.getName(),true);
          return testx(pt.next(), props, metaObject,fullName,token);
        
    }



    private static void resolveSubProp(MetaObject metaObject, String index, List<String> sub, List<String> remove,
        String oldNameProp, String newNameProp, String nprop) {
        nprop= nprop.substring(0, nprop.lastIndexOf("["));
          //需要获得当前元素下子集合个数
          MetaObject metaObjectForProperty = metaObject.metaObjectForProperty(nprop);
          Object originalObject2 = metaObjectForProperty.getOriginalObject();
          int subsize = getCollectionSize(originalObject2);
          //如果子集合无元素，证明是无效元素，无效做操作
          if(subsize == 0) {
              remove.add(newNameProp);
          }
          //循环现有集合,添加子元素
          for(int j=1;j<subsize;j++){
              String newPropName = oldNameProp.replace(index, j+"");
              sub.add(newPropName);
          }
    }
    
    
    private static void resolver(List<String> props,Object originalObject,String index,String fullName){
        
        int collectionSize = getCollectionSize(originalObject);
        Matcher m = pattern.matcher(index);
        if(!m.find()){
            props.add(fullName);
            return;
        }
        for(int i =0;i<collectionSize;i++){
            String newPropName = fullName.replace(index, i+"");
            props.add(newPropName);
        }
        
    }
    
    @SuppressWarnings("rawtypes")
    public static int getCollectionSize(final Object originalObject){
        int size = 0;
        if(originalObject instanceof Collection){
            Collection col = (Collection)originalObject;
            size = col.size();
        }
        else if(originalObject.getClass().isArray()){
            Object[] array = (Object[])originalObject;
            size = array.length;
        }
        return size;
    }
    
}
