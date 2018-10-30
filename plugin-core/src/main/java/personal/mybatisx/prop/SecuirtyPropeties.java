package personal.mybatisx.prop;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * 配置储存
 * @author lyu
 *
 */
public class SecuirtyPropeties {
    
    public static final String DEFAULT_CONFIG_KEY = "defaultConfig";
    
    private static Map<String ,SecuritiesConfigratureBean> keyprops = new HashMap<String, SecuritiesConfigratureBean>();

    
    
    public static void setKeyprops(Map<String, SecuritiesConfigratureBean> keyprops) {
        SecuirtyPropeties.keyprops = keyprops;
    }

    public static void put(String id,SecuritiesConfigratureBean configBean){
        if(StringUtils.isBlank(id)) id = DEFAULT_CONFIG_KEY;
        keyprops.put(id, configBean);
    }
    
    public static SecuritiesConfigratureBean get(String id){
        if(StringUtils.isBlank(id)) id = DEFAULT_CONFIG_KEY;
        return keyprops.get(id);
    }
    
}
