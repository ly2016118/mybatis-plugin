package personal.mybatisx.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
public @interface MexEncrypt {
    /**
     * 配置指定ID 对应的加密方式
     * @return
     */
    String id() default "";
  
    
    /**<per>
     * 需要加密的属性数组，如果实体类，注解在属性上，则认为直接赋值
     * 注解在方法参数上：适用于请求
     *   参数是基本类型及其封装类，无需配置此属性
     *   参数是复杂类型：
     *     Map: 必须配置此属性 ，并且value属性必须是String
     *     Collection: 1、如果是JavaBean 必须配置此属性，遵循ognl ，2、若不是JavaBean，那么只能是String类型
     *     JavaBean: 必须配置此属性，遵循ognl 
     *  
     * 注解在属性上： 适用于响应
     *   只能注解在属性上；
     *   如果属性是复杂类型：
     *      Map: 必须配置此属性 ，并且value属性必须是String
     *      Collection: 1、如果是JavaBean 必须配置此属性，遵循ognl ，2、若不是JavaBean，那么只能是String类型
     *      JavaBean: 必须配置此属性，遵循ognl 
     * @return
     */
    String[] properties() default{};
    

}
