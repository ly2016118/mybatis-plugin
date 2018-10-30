package personal.mybatisx.beans;

import personal.mybatisx.annotations.MexRequest;
import personal.mybatisx.annotations.MexResponse;

/**
 * 注解信息存放
 * @author lyu
 *
 */
public class AnnotationInfoClass {
    
    private MexRequest request;
    private MexResponse response;
    public MexRequest getRequest() {
        return request;
    }
    public void setRequest(MexRequest request) {
        this.request = request;
    }
    public MexResponse getResponse() {
        return response;
    }
    public void setResponse(MexResponse response) {
        this.response = response;
    }
    
    

}
