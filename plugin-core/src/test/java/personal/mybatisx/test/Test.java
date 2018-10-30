package personal.mybatisx.test;

public class Test {

    public static void main(String[] args) {
        int b = 0;
        boolean baseType = isBaseType(b);
        System.out.println(baseType);

    }

    
    private static boolean isBaseType(Object type) {
        if(type.getClass().isPrimitive()){
            System.out.println("isPrimitive");
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
