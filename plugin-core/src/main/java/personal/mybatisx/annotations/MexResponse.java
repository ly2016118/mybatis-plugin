package personal.mybatisx.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import personal.mybatisx.enums.EncryptOrDecrypt;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface MexResponse {
    /**
     * 加解密属性
     * @return
     */
    String[] fields();
    /**
     * 加密还是解密
     * @return
     */
    EncryptOrDecrypt doType();
    /**
     * 加密或解密使用的秘钥配置ID
     * @return
     */
    String id() default "";
}
