package personal.mybatisx.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.session.defaults.DefaultSqlSession.StrictMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import personal.mybatisx.annotations.MexDecrypt;
import personal.mybatisx.annotations.MexEncrypt;
import personal.mybatisx.annotations.MexResponse;
import personal.mybatisx.beans.AnnotationInfoClass;
import personal.mybatisx.beans.ParameterMetaData;
import personal.mybatisx.beans.Token;
import personal.mybatisx.enums.EncryptOrDecrypt;
import personal.mybatisx.resolver.ExpressionResolver;
import personal.mybatisx.security.SecurityMannager;

/**
 * 参数转换处理
 * 
 * <pre>
 * 以下方法是指 Dao层接口方法
 * mybatis 中，方法参数被转换成 ParamMap ，StrictMap ，和原型
 *   ParamMap 是对方法中有 “多个” 参数的转换，以，map形式存放
 *            1、当方法参数没有被 @Param 标记 ，方法中每个参数都会被储存到两个key 上，
 *               一个是下标key 一个是 字符串 'param'+(下标加1)为key ，
 *               如：第一个参数，下标为0 ： 储存为 {0=参数1,param1 = 参数1,...}
 *            2、当参数标记了 @Param("ownerName") 则   参数封装为{0=参数1,ownerName=参数1,...}
 *   StrictMap 是对方法中参数参数只有一个 ，并且是集合或者数组的封装
 *            1、如果是集合  Collection 子类 将被封装成 两个key的map 
 *               如 List<User> 被封装成 {collection=[User,User...],list=[User,User]} 
 *            2、对数组的封装    Array  将被封装成一个key的map
 *               如String[] 被封装成 {array=[value1,value2...]} 
 *   原型: 方法中只有一个参数，并且参数类型为 Class<?> (既不是数组也不是集合的单一参数)  
 *         此类参数  ，mybatis 不做封装   
 *         
 * 本类对参数的解析，覆盖以上所有情况，并且刨除调 Java 基本类型以及其封装类型及其数组
 * 加解密实质是对String类型的参数或属性进行操作，其它类型均不受理
 * @author lyu
 *
 */
public class ParameterTransforHandler {

    private static final String GENERIC_NAME_PREFIX = "param";
    private static final String COLLECTION = "collection";
    private static final String LIST = "list";
    private static final String ARRAYS = "array";

    private static final Logger log = LoggerFactory.getLogger(ParameterTransforHandler.class);
    private SecurityMannager mannage = new SecurityMannager();

    public void setMannage(SecurityMannager mannage) {
        this.mannage = mannage;
    }

    /**
     * 
     * @param metaObject
     * @param params 需要被转换的参数
     * @throws Exception
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void transforParameters(MetaObject metaObject, List<ParameterMetaData> params, boolean useActualParamName)
        throws Exception {
        if (null == params || params.size() == 0)
            return;
        Object originalParam = metaObject.getOriginalObject();
        /**
         * 1、多个参数 混合复杂参解析
         */
        if (originalParam instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap<Object> paramMap = (ParamMap<Object>)originalParam;
            transforParameters(paramMap, metaObject, params, useActualParamName);
            return;
        }
        /**
         * 2、只有一个参数 ，并且是集合或 数组
         */
        if (originalParam instanceof StrictMap) {
            StrictMap<Object> map = (StrictMap<Object>)originalParam;
            if (null == map || map.size() == 0) {
                throw new RuntimeException("参数不能为空 ");
            }
            // 集合类型的参数
            if (map.containsKey(COLLECTION)) {
                Object object = map.get(COLLECTION);
                transforCollectionParameters((Collection)object, params.get(0), map,true,useActualParamName);
            } else {// 数组类型的参数
                Object[] object = (Object[])map.get(ARRAYS);
                if (null == object || object.length == 0)
                    return;
                resolverArrayParam(object, params.get(0), map);
            }
            return;
        }
        /**
         * 3、只有一个参数 非集合数组
         */
        ParameterMetaData parameterMetaData = params.get(0);
        String[] properties = parameterMetaData.getProperties();
        Token vali = new Token();
        for (String key : properties) {
            boolean hasKey = vali.hasIndex(key);
            if (hasKey) {
                throw new RuntimeException("不能配置两个完全一样的表达式：" + key);
            }
            vali.setIndex(key);
            PropertyTokenizer pt = new PropertyTokenizer(key);
            List<String> ognlPath =
                ExpressionResolver.resolverToOgnlPath(pt, null, metaObject, key, new Token(pt.getName()));
            for (String path : ognlPath) {
                Object value = metaObject.getValue(path);
                String input = doEncryptOrDecrypt(parameterMetaData.getId(),parameterMetaData.isEncrypt(), (String)value);
                metaObject.setValue(key, input);
            }
        }
    }

    
    /**
     * 处理复杂的结果集
     * @param metaObject
     * @param infoClass
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void transforResult(Object result, AnnotationInfoClass infoClass){
        if(null == result) return;
        if(result instanceof String ) return;
        MetaObject metaObject = ExpressionResolver.createMetaObject(result);
        Object originalObject = metaObject.getOriginalObject();
        if(isBaseType(originalObject)) {
            log.warn("不支持的类型：Java的基本类型和它的封装类型以及其数组形式");
            return;
        }
        MexResponse response = infoClass.getResponse();
        
        String[] fields = response.fields();
        if(fields==null||fields.length ==0){
            log.warn("没有配置需要转换的属性：结果类型："+originalObject.getClass().getName());
            return;
        }
        
        boolean isCollection = metaObject.isCollection();
        //如果是集合参
        if(isCollection){
            Collection coll = (Collection)originalObject;
            if(coll ==null ||coll.size()==0) return;
            boolean isStringCollection = false;
            for(Object origData:coll){
                if(isBaseType(origData)) {
                    log.warn("不支持的类型：Java的基本类型和它的封装类型以及其数组形式");
                    return;
                }
                if(origData instanceof String){
                    isStringCollection = true;
                    break;
                }
                MetaObject metaObject0 = ExpressionResolver.createMetaObject(origData);
                setParameters(response, fields, metaObject0);
            }
            //处理Collection<String>
           if(isStringCollection){
               EncryptOrDecrypt doType = response.doType();
               boolean isEncrypt = true;
               if(EncryptOrDecrypt.DECRYPT.equals(doType)) isEncrypt = false;
               List<Object> resolverStringCollection = resolverStringCollection(coll, response.id(),isEncrypt );
               coll.clear();
               coll.addAll(resolverStringCollection);
           }
           return;
        }
        //非集合
        setParameters(response, fields, metaObject);
        
    }
    
    /**
     * 处理响应结果是String 类型的参数
     * @param result
     * @param infoClass
     * @return
     */
    public String transforStringResult(Object result,AnnotationInfoClass infoClass){
        MexResponse response = infoClass.getResponse();
        if (EncryptOrDecrypt.DECRYPT.equals(response.doType())) {
            return mannage.getSecuritySupport().encrypt(response.id(), (String)result);
        } else {
            return mannage.getSecuritySupport().decrypt(response.id(), (String)result);
        }
    }
    /**
     * 解析数组型参数
     * 
     * @param array
     * @param parameterMetaData
     * @param map
     */
    private void resolverArrayParam(Object[] array, ParameterMetaData parameterMetaData, StrictMap<Object> map) {
        if (null == array || array.length == 0)
            return;
        String[] properties = parameterMetaData.getProperties();
        // 如果注解没有配置 properties 属性 ，值解析String[] 类型
        if (null == properties || properties.length == 0) {
            resolverArrayWithoutProp(array, parameterMetaData, map);
            return;
        }
        MetaObject metaObject = ExpressionResolver.createMetaObject(array);
        setParameters(parameterMetaData, properties, metaObject);

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void resolverArrayWithoutProp(Object[] array, ParameterMetaData pmd, Map map) {
        Object[] reArray = new Object[array.length];
        int index = 0;
        boolean isChanged = false;
        for (Object tmp : array) {
            if (tmp instanceof String) {
                String result = doEncryptOrDecrypt(pmd.getId(),pmd.isEncrypt(), (String)tmp);
                reArray[index++] = result;
                isChanged = true;
            } else {
                reArray[index++] = tmp;
            }
        }
        if (isChanged)
            map.put(ARRAYS, reArray);
    }

    /**
     * 解析集合参数
     * 
     * @param collection
     * @param parameterMetaData 
     * @param map mybatis 中封装的Map参数
     * @param isSingleParam 接口参数是否是一个参数
     * @param useActualParamName 决定  mybatis 中封装参数的方式
     */
    @SuppressWarnings("rawtypes")
    private void transforCollectionParameters(Collection collection, ParameterMetaData parameterMetaData,
        Map map,boolean isSingleParam,boolean useActualParamName) {
        
        if (null == collection || collection.size() == 0)
            return;

        String[] properties = parameterMetaData.getProperties();
        // 如果注解没有配置 properties 属性 ，只解析List<String> 类型
        if (null == properties || properties.length == 0) {
            resolverCollectionWithoutProp(collection, parameterMetaData, map,isSingleParam,useActualParamName);
            return;
        }
        for(Object bean : collection){
            MetaObject metaObject = ExpressionResolver.createMetaObject(bean);
            setParameters(parameterMetaData, properties, metaObject);
        }
    }

    /**
     * 设置参数
     * @param parameterMetaData
     * @param properties
     * @param metaObject
     */
    private void setParameters(ParameterMetaData parameterMetaData, String[] properties, MetaObject metaObject) {
        for (String prop : properties) {
            PropertyTokenizer pt = new PropertyTokenizer(prop);
            List<String> ognlPath =
                ExpressionResolver.resolverToOgnlPath(pt, null, metaObject, prop, new Token(pt.getName()));
            for (String path : ognlPath) {
                Object value = metaObject.getValue(path);
                if(null == value) continue;
                if(value instanceof String){
                    String input = (String)value;
                    if(StringUtils.isBlank(input)) continue;
                    String result = doEncryptOrDecrypt(parameterMetaData.getId(),parameterMetaData.isEncrypt(), input);
                    metaObject.setValue(path, result);
                }
            }
        }
    }
    
    /**
     * 设置替换参数
     * @param response
     * @param properties
     * @param metaObject
     */
    private void setParameters(MexResponse response, String[] properties, MetaObject metaObject) {
        ParameterMetaData pmd = new ParameterMetaData();
        if(EncryptOrDecrypt.ENCRYPT.equals(response.doType()))pmd.setEncrypt(true);
        pmd.setId(response.id());
        setParameters(pmd, properties, metaObject);
    }

    /**
     * 解析注解中没有配置 properties 的 集合
     * 
     * @param array
     * @param pmd
     * @param map
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void resolverCollectionWithoutProp(Collection collection, ParameterMetaData pmd, Map map,boolean isSingleParam,boolean useActualParamName) {
        List<Object> collection1 = new ArrayList<Object>();
        boolean isChanged = false;
        for (Object tmp : collection) {
            if (tmp instanceof String) {
                String result = doEncryptOrDecrypt(pmd.getId(),pmd.isEncrypt(), (String)tmp);
                collection1.add(result);
                isChanged = true;
            } else {
                collection1.add(tmp);
            }
        }
        if (isChanged&&isSingleParam) {
            map.put(LIST, collection1);
            map.put(COLLECTION, collection1);
        }else{
            map.put(pmd.getName(), collection1);
            String actualParamName = getActualParamName(useActualParamName, pmd.getIndex());
            map.put(actualParamName, collection1);
        }
    }

    private List<Object> resolverStringCollection(Collection collection,String id,boolean isEncrypt){
        
        List<Object> collectionResult = new ArrayList<Object>();
        for (Object tmp : collection) {
            if (tmp instanceof String) {
                String result = doEncryptOrDecrypt(id,isEncrypt, (String)tmp);
                collectionResult.add(result);
            } else {
                collectionResult.add(tmp);
            }
        }
        return collectionResult;
        
    }
    
    
    @SuppressWarnings("rawtypes")
    private void transforParameters(ParamMap<Object> paramMap, MetaObject metaObject, List<ParameterMetaData> params,
        boolean useActualParamName) {
        
        //循环需要解析的参数
        for(ParameterMetaData pm:params){
            MetaObject currentObject = metaObject.metaObjectForProperty(pm.getName());
            Object originalObject = currentObject.getOriginalObject();
            //1、简单基本类型参 排除
            if(isBaseType(originalObject)) {
                throw new IllegalArgumentException("加解密注解 [MexDecrypt、MexEncrypt] 不能注解在基本类型及其封装类型上");
            }
            
            //2、String 类型参
            if(originalObject instanceof String){
                resolverStringParam(paramMap, pm,useActualParamName);
                continue;
            }
            
            //3、 Map 参数
            if(originalObject instanceof Map){
                setParameters(pm, pm.getProperties(), currentObject);
                continue;
            }
            //4、 集合参数
            if(originalObject instanceof Collection){
                transforCollectionParameters((Collection)originalObject, pm, paramMap,false,useActualParamName);
                continue;
            }
            //5、 数组参数
            
            //6、bean 
            
            setParameters(pm, pm.getProperties(), currentObject);
        }
        

    }

    /**
     * 
     * @param id 秘钥配置ID
     * @param isEncrypt
     * @param input
     * @return
     */
    private String doEncryptOrDecrypt(String id,boolean isEncrypt, String input) {
        String result = "";
        if (isEncrypt) {
            result = mannage.getSecuritySupport().encrypt(id, input);
        } else {
            result = mannage.getSecuritySupport().decrypt(id, input);
        }
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void resolverStringParam(MapperMethod.ParamMap paramMap, ParameterMetaData tmp,boolean useActualParamName) {
        String input = (String)paramMap.get(tmp.getName());
        String result = doEncryptOrDecrypt(tmp.getId(),tmp.isEncrypt(), input);
        //设置 param1 param2 ... 类型的参数
        if (StringUtils.isNotBlank(result))
            paramMap.put(tmp.getName(), result);
        
        //设置 arg0 arg1 ... 0 1 ... 类型的参数
        String actualParamName = getActualParamName(useActualParamName, tmp.getIndex());
        paramMap.put(actualParamName, result);
    }

    private String getActualParamName(boolean useActualParamName,int index){
        if(useActualParamName) return "arg"+index;
        else return ""+index;
    }
    
    /**
     * 如果接口参数是一个参数，并且是无set方法的类型
     * 
     * @param args
     * @param param
     */
    public void transforStringParameters(Object[] args, ParameterMetaData param) {
        if (param.isEncrypt()) {
            args[1] = mannage.getSecuritySupport().encrypt(param.getId(), (String)args[1]);
        } else {
            args[1] = mannage.getSecuritySupport().decrypt(param.getId(), (String)args[1]);
        }

    }

    /**
     * 解析需要转换的参数
     * 
     * @param m
     * @return
     */
    public List<ParameterMetaData> resolverNeedTransforParams(Method m) {

        List<ParameterMetaData> pms = new ArrayList<ParameterMetaData>();
        // 解析参数注解 封装哪些需要进行加解密
        Annotation[][] parameterAnnotations = m.getParameterAnnotations();
        // 分析参数注解
        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] an = parameterAnnotations[i];
            if (null == an)
                continue;
            ParameterMetaData pm = new ParameterMetaData();
            // 是否有加密或解密其中之一
            boolean has = false;
            for (Annotation tmp : an) {
                if (tmp instanceof Param) {
                    Param p = (Param)tmp;
                    if (!"".equals(p.value()) & null != p.value()) {
                        pm.setName(p.value());
                        pm.setNameChanged(true);
                    }
                }
                // 加密
                if (tmp instanceof MexEncrypt) {
                    if (has)
                        throw new IllegalArgumentException("参数中不能同时在一个参数上注解加密和解密");
                    has = true;
                    MexEncrypt en = (MexEncrypt)tmp;
                    pm.setProperties(en.properties());;
                    pm.setEncrypt(true);
                    pm.setId(en.id());
                    setName(i, pm);
                }
                // 解密
                if (tmp instanceof MexDecrypt) {
                    if (has)
                        throw new IllegalArgumentException("参数中不能同时在一个参数上注解加密和解密");
                    has = true;
                    MexDecrypt en = (MexDecrypt)tmp;
                    pm.setProperties(en.properties());
                    pm.setId(en.id());
                    setName(i, pm);
                }
            }
            // 判断是否需要转换此参数，需要则加入集合
            if (has) {
                pm.setIndex(i);
                pms.add(pm);
            } else {
                pm = null;
            }
        }
        return pms;

    }

    private void setName(int i, ParameterMetaData pm) {
        // 如果为空
        if ("".equals(pm.getName()) || null == pm.getName()) {
            final String genericParamName = GENERIC_NAME_PREFIX + String.valueOf(i + 1);
            // mybatis 中如果没有标注 Param ，mybatis 默认将参数名称设置成 param1 param2 ...
            pm.setName(genericParamName);
        }
    }

    private boolean isBaseType(Object type) {
        if(type.getClass().isPrimitive()){
            return true;
        }
        if (type instanceof Integer)
            return true;
        if (type instanceof Short)
            return true;
        if (type instanceof Byte)
            return true;
        if (type instanceof Long)
            return true;
        if (type instanceof Float)
            return true;
        if (type instanceof Double)
            return true;
        if (type instanceof Character)
            return true;
        if (type instanceof Boolean)
            return true;
        
        if(type instanceof byte[]){
            return true;
        }
        if(type instanceof short[]){
            return true;
        }
        if(type instanceof int[]){
            return true;
        }
        if(type instanceof long[]){
            return true;
        }
        if(type instanceof float[]){
            return true;
        }
        if(type instanceof double[]){
            return true;
        }
        if(type instanceof char[]){
            return true;
        }
        if(type instanceof boolean[]){
            return true;
        }
        return false;

    }

}
