package personal.mybatisx.handler;

import org.apache.ibatis.executor.result.DefaultResultHandler;
import org.apache.ibatis.session.ResultContext;

import personal.mybatisx.beans.AnnotationInfoClass;

/**
 * 加解密结果集处理
 * @author lyu
 *
 */
public class SecuirtyResultHandler extends DefaultResultHandler {

     private AnnotationInfoClass annotationInfoClass;
     
    public SecuirtyResultHandler(){}
    
    public SecuirtyResultHandler(AnnotationInfoClass annotationInfoClass){
        this.annotationInfoClass = annotationInfoClass;
    }
    @Override
    public void handleResult(ResultContext<? extends Object> context) {
       
        Object resultObject = context.getResultObject();
        System.out.println("resultObject:"+resultObject.getClass());
        super.handleResult(context);
    }

}
