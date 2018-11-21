package personal.mybatisx.resolver;

import java.util.Collection;
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

/**
 * 将带表达式的路径解析成真实可用的 ognl 路径
 * 
 * @author lyu
 */
public class ExpressionResolver {

    private static final Pattern pattern = Pattern.compile("\\{(.+?)\\}");
    private static final String LEFT_MID_BRACKET = "[";

    /**
     * 解析参数中可用的路径表达式
     * @param pt
     * @param props
     * @param metaObject
     * @param fullName
     * @param token
     * @return
     */
    public static List<String> resolverToOgnlPath(PropertyTokenizer pt, List<String> props, MetaObject metaObject,
        String fullName, Token token) {
        
        if (null == props){
            props = new LinkedList<String>();
        }
        String index = pt.getIndex();
        if (token.hasIndex(index)) { //
            throw new RuntimeException("表达式变量名称重复，表达式：" + fullName);
        }
        token.setIndex(index);
        
        Object originalObject =null;
        //数组，并且是第一次解析
        if(fullName.startsWith(LEFT_MID_BRACKET)&&props.size()==0){
            originalObject = metaObject.getOriginalObject();
            resolverIfStartWihtArray(props, originalObject, index, fullName);
            //判断是否有子节点
            if(pt.hasNext()){
                token.appendVariableName("[0]."+pt.next().getName(),true);
                return resolverToOgnlPath(pt.next(), props, metaObject,fullName,token);
            }
            else return props;
        }
        
        //不是数组开头，并且不是第一次解析，进行下面的解析操作
         originalObject = metaObject.getValue(token.getVariableName());
        // 没有子节点，跳出递归的条件
        if (!pt.hasNext()) {
            // 判断是否是第一次解析
            if (props.size() == 0 && StringUtils.isBlank(index)) {
                props.add(fullName);
            }
            // 判断是否有表达式
            else if (StringUtils.isNotBlank(index)) {
                Matcher m = pattern.matcher(index);
                if (!m.find()) {
                    Integer.parseInt(index);
                    props.add(fullName);
                    return props;
                }
                int size = getSize(originalObject);
                if (props.size() == 0) {
                    // 是表达式，需要替换
                    for (int i = 0; i < size; i++) {
                        String newPropName = fullName.replace(index, i + "");
                        props.add(newPropName);
                    }
                } else {
                    // 循环原有属性节点
                    resolveMultipleNode(pt, props, metaObject, index);
                }
            }
            return props;
        }
        
        //-----------------------------------------------------
        
        PropertyTokenizer next = pt.next();
        int currentSize = 0;

        // 非集合
        if (StringUtils.isBlank(index)) {
            // 不是集合 不需要新增元素，无需解析，直接解析下一个元素
            token.appendVariableName(next.getName(), false);
            return resolverToOgnlPath(pt.next(), props, metaObject, fullName, token);
        }
        // 如果是map ，不需要解析下标
        else if (originalObject instanceof Map) {
            token.appendVariableName(next.getIndexedName(), false);
            return resolverToOgnlPath(pt.next(), props, metaObject, fullName, token);
        }
        // 如果是集合或数组
        else {
            currentSize = getSize(originalObject);
        }
        Matcher m = pattern.matcher(index);
        // 不是表达式，则说明已经指定值 直接解析下一个节点
        if (!m.find()) {
            token.appendVariableName(next.getIndexedName(), false);
            // if()props.add(e)
            return resolverToOgnlPath(pt.next(), props, metaObject, fullName, token);
        }
        // 判断现有集合是否为空,如果为空，则认为是第一次计算集合元素
        if (props.size() == 0) {
            // 是表达式，需要替换
            for (int i = 0; i < currentSize; i++) {
                String newPropName = fullName.replace(index, i + "");
                String nprop = newPropName.substring(0, newPropName.lastIndexOf(LEFT_MID_BRACKET));
                // 需要获得当前元素下子集合个数
                MetaObject metaObjectForProperty = metaObject.metaObjectForProperty(nprop);
                Object originalObject2 = metaObjectForProperty.getOriginalObject();
                int subsize = getSize(originalObject2);
                // 如果子集合无元素，证明是无效元素，无效做操作
                if (subsize != 0) {
                    props.add(newPropName);
                }
            }
            // 不为空 证明已经解析过前面节点，此时需要循环原集合
        } else {
            resolveMultipleNode(pt, props, metaObject, index);
        }
        token.appendVariableName(next.getName(), true);
        return resolverToOgnlPath(pt.next(), props, metaObject, fullName, token);
    }

    /**
     * 解析多节点，一般指Collection Or Array
     * @param pt
     * @param props
     * @param metaObject 节点代理类
     * @param index 
     */
    private static void resolveMultipleNode(PropertyTokenizer pt, List<String> props, MetaObject metaObject, String index) {
        // 循环原有属性节点
        List<String> sub = new LinkedList<String>();
        List<String> remove = new LinkedList<String>();
        for (int i = 0; i < props.size(); i++) {
            // 拿出原有值，进行替换
            String oldNameProp = props.get(i);
            String newNameProp = oldNameProp.replace(index, i + "");
            props.set(i, newNameProp);

            String nprop = null;
            if (pt.hasNext()) {
                nprop = newNameProp.replace(pt.getChildren(), "");
                nprop = nprop.substring(0, nprop.lastIndexOf(LEFT_MID_BRACKET));
            } else {
                nprop = newNameProp.substring(0, newNameProp.lastIndexOf(LEFT_MID_BRACKET));
            }
            // 需要获得当前元素下子集合个数
            MetaObject metaObjectForProperty = metaObject.metaObjectForProperty(nprop);
            Object originalObject2 = metaObjectForProperty.getOriginalObject();
            int subsize = getSize(originalObject2);
            // 如果子集合无元素，证明是无效元素，无效做操作
            if (subsize == 0) {
                remove.add(newNameProp);
            }
            // 循环现有集合,添加子元素
            for (int j = 1; j < subsize; j++) {
                String newPropName = oldNameProp.replace(index, j + "");
                sub.add(newPropName);
            }
        }
        props.removeAll(remove);
        props.addAll(sub);
    }

    @SuppressWarnings("rawtypes")
    public static int getSize(final Object originalObject) {
        if(null == originalObject) return 0;
        int size = 0;
        if (originalObject instanceof Collection) {
            Collection col = (Collection)originalObject;
            size = col.size();
        } else if (originalObject.getClass().isArray()) {
            Object[] array = (Object[])originalObject;
            size = array.length;
        }
        return size;
    }

    public static MetaObject createMetaObject(Object target){
        return  MetaObject.forObject(target, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());

    }
    
    /**
     * 解析开始以 [] 为表达式的
     * @param props
     * @param originalObject
     * @param index
     * @param fullName
     */
    private static void resolverIfStartWihtArray(List<String> props,Object originalObject,String index,String fullName){
        
        int collectionSize = getSize(originalObject);
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
}
