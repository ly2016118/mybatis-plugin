package personal.mybatisx.plugins;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import personal.mybatisx.annotations.MexNeededEncryptOrDecrypt;
import personal.mybatisx.annotations.MexRequest;
import personal.mybatisx.annotations.MexResponse;
import personal.mybatisx.beans.AnnotationInfoClass;
import personal.mybatisx.beans.ParameterMetaData;
import personal.mybatisx.handler.ParameterTransforHandler;
import personal.mybatisx.security.SecurityBeanFactory;
import personal.mybatisx.util.aes.AESUtil;
import personal.mybatisx.util.md5.MD5Util;
import personal.mybatisx.util.rsa.RSAUtil;



/**
 * 加解密转换
 * @author lyu
 *
 */
//queryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds)
//query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql) throws SQLException;
//query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException;
@Intercepts({@Signature( type= Executor.class, method = "update", args = {MappedStatement.class,Object.class})
    ,@Signature( type= Executor.class, method = "query", args = {MappedStatement.class,Object.class,RowBounds.class,ResultHandler.class,CacheKey.class,BoundSql.class})
    ,@Signature( type= Executor.class, method = "query", args = {MappedStatement.class,Object.class,RowBounds.class,ResultHandler.class})
//    ,@Signature( type= Executor.class, method = "queryCursor", args = {MappedStatement.class,Object.class,RowBounds.class})
})
public class EncryptionAndDecryptionPluginOnExecutor implements Interceptor{
    
    private static final Logger log = LoggerFactory.getLogger(EncryptionAndDecryptionPluginOnExecutor.class);
    
    private ParameterTransforHandler parameterTransforHandler =new ParameterTransforHandler();
    
    public void setParameterTransforHandler(ParameterTransforHandler parameterTransforHandler) {
        this.parameterTransforHandler = parameterTransforHandler;
    }

    @SuppressWarnings({"rawtypes"})
    public Object intercept(Invocation invocation) throws Throwable {
        
        Object[] args = invocation.getArgs();
        Object object = args[0];
        MappedStatement stm = (MappedStatement)object;
        //类加方法
        String classNameAndMethod = stm.getId();
        String className = classNameAndMethod.substring(0,classNameAndMethod.lastIndexOf("."));
        String methodName = classNameAndMethod.substring(classNameAndMethod.lastIndexOf(".")+1);
        //反射类名 得到请求的dao 接口
        Class<?> interfaceClass = Class.forName(className);
        //检查是否需要加解密处理
        MexNeededEncryptOrDecrypt annotation = interfaceClass.getAnnotation(MexNeededEncryptOrDecrypt.class);
        //无需，则立即返回
        if(null == annotation){
            return invocation.proceed();
        }
        //需要加解密
        //1、获得当前接口的所有方法
        Method[] methods = interfaceClass.getMethods();
        //保存响应是否解密操作
        AnnotationInfoClass infoClass = new AnnotationInfoClass(); 
        List<ParameterMetaData> pms = null;
        Class returnType = null;
        //2、循环方法，拿到相应的方法并解析
        for(Method m:methods){
            if(m.getName().equals(methodName)){
                MexRequest reqAnnotation = m.getAnnotation(MexRequest.class);
                MexResponse respAnnotation = m.getAnnotation(MexResponse.class);
                infoClass.setRequest(reqAnnotation);
                //当前方法不需要加解密
                if(null != respAnnotation){ 
                    returnType = m.getReturnType();
                    if(returnType.isPrimitive()){
                        log.warn("响应类型为无效类型[基本类型和void] 注解无效");
//                        throw new RuntimeException("响应类型为无效类型[基本类型和void] 注解无效");
                    }else{
                        infoClass.setResponse(respAnnotation);
                    }
                }
                //请求参数无需处理，跳出循环
                if(null == reqAnnotation) {break;}
                //解析参数注解 封装哪些需要进行加解密
                pms =  parameterTransforHandler.resolverNeedTransforParams(m);
                break;
            }
        }
        //3、需要转换，则调用转换逻辑
        Configuration configuration = stm.getConfiguration();
        if(null!=infoClass.getRequest()&& null!=pms && pms.size()>0){
            Object parameter = args[1];
            if(parameter instanceof String){
                parameterTransforHandler.transforStringParameters(args, pms.get(0));
            }else{
                MetaObject metaObject = configuration.newMetaObject(parameter);
                boolean useActualParamName = configuration.isUseActualParamName();
                parameterTransforHandler.transforParameters(metaObject, pms,useActualParamName);
            }
        }
        //4、处理响应
       Object result = invocation.proceed();
     
       if(result==null) return result;
       if(result instanceof String){
           return parameterTransforHandler.transforStringResult(result, infoClass);
       }
       parameterTransforHandler.transforResult(result, infoClass);
       
    return result;
            
        
    }


   
    
    public Object plugin(Object target) {
        
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
        //默认注册加密工具
        SecurityBeanFactory.build()
            .registerSignTypeBean(SecurityBeanFactory.SignType.AES, new AESUtil())
            .registerSignTypeBean(SecurityBeanFactory.SignType.MD5, new MD5Util())
            .registerSignTypeBean(SecurityBeanFactory.SignType.RSA, new RSAUtil()); 
    }

}
