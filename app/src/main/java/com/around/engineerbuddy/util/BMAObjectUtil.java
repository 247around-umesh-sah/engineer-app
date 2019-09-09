package com.around.engineerbuddy.util;

import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.around.engineerbuddy.entity.BMAObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.Primitives;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BMAObjectUtil {

    public static final String GetterPrefix = "get";
    public static final String KeyPathSeparatorRegEx = "\\.";

    public static boolean isEmpty(Object o) {
        if (null == o) {
            return true;
        }
        if (o instanceof String) {
            return isEmptyStr((String) o);
        }
        if (o instanceof CharSequence) {
            return ((CharSequence) o).length() == 0;
        }
        if (o instanceof Map) {
            return ((Map<?, ?>) o).size() == 0;
        }
        if (o instanceof Collection) {
            return ((Collection<?>) o).isEmpty();
        }
        if (o.getClass().isArray()) {
            return ((Object[]) o).length == 0;
        }
        return false;
    }

    public static boolean isEmptyStr(String _v) {
        return _v == null || _v.trim().length() == 0 || _v.equalsIgnoreCase("null");
    }

    public static boolean isEmptyView(View view) {
        return isEmptyStr(getTextFromView(view));
    }

    public static String getTextFromView(View view) {
        return (view != null && view instanceof TextView) ? ((TextView) view).getText().toString().trim() : "";
    }

    public static boolean isNonEmptyStr(String _v) {
        return !isEmptyStr(_v);
    }

    public static <T> T objectForClass(String className, Class<T> entity) {
        return Primitives.wrap(entity).cast(objectForClass(className));
    }

    public static <T> T objectForClass(String className) {
        Object object = null;
        try {
            Class<?> classObj = Class.forName(className);
            object = objectForClass(classObj);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return (T) object;
    }

    public static <T> T objectForClass(Class<T> classObj) {
        try {
            if (classObj.getModifiers() == 0) {
                try {
                    Constructor<?>[] ctors = classObj.getDeclaredConstructors();
                    Constructor<?> ctor;
                    for (Constructor<?> ctor1 : ctors) {
                        ctor = ctor1;
                        if (ctor.getGenericParameterTypes().length == 0) {
                            break;
                        }
                    }
                    ctor = classObj.getDeclaredConstructor();
                    try {
                        ctor.setAccessible(true);
                        return (T) ctor.newInstance();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                return classObj.newInstance();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String methodNameForKeyWithPrefix(String aKey, String aPrefix) {
        if (aKey != null) {
            if (aPrefix != null) {
                StringBuilder aBuffer = new StringBuilder();
                char[] someChars = aKey.toCharArray();

                someChars[0] = Character.toUpperCase(someChars[0]);

                aBuffer.append(aPrefix);
                aBuffer.append(someChars);

                return aBuffer.toString();
            }

            throw new IllegalArgumentException("RuntimeKeyValue.methodNameForKeyWithPrefix: null prefix.");
        }

        throw new IllegalArgumentException("RuntimeKeyValue.methodNameForKeyWithPrefix: null key.");
    }

    public static Class<?> classForObject(Object anObject) {
        if (anObject != null) {
            Class<?> aClass = anObject.getClass();

            if (anObject instanceof Class) {
                aClass = (Class<?>) anObject;
            }
            return aClass;
        }
        throw new IllegalArgumentException("Runtime.classForObject: null object.");
    }

    public static Method getterMethodWithNameInClass(String aName, Class<?> aClass, Class<?>[] paramTypes) {
        if (aName != null) {
            if (aClass != null) {
                Method aMethod = null;

                try {
                    aMethod = aClass.getDeclaredMethod(aName, paramTypes);
                    aMethod.setAccessible(true);
                    if (aMethod.getReturnType().equals(Void.TYPE)) {
                        aMethod = null;
                    }
                } catch (NoSuchMethodException anException) {
                } catch (SecurityException anException) {
                }
                if (aMethod == null) {
                    aClass = aClass.getSuperclass();

                    if (aClass != null) {
                        aMethod = getterMethodWithNameInClass(aName, aClass, paramTypes);
                    }
                }
                return aMethod;
            }
            throw new IllegalArgumentException("Runtime.getterMethodWithNameInClass: null class.");
        }
        throw new IllegalArgumentException("Runtime.getterMethodWithNameInClass:null entityName.");
    }

    public static Field setterFieldWithNameInClass(String fieldName, Class<?> aClass, Class<?> fldClass) {
        if (fieldName == null || aClass == null) {
            return null;
        }
        try {
            Field wantedField = null;
            Field[] fields = aClass.getFields();
            if (fields != null && fields.length > 0) {
                for (Field currentField : fields) {
                    String sfieldName = currentField.getName();
                    if (fieldName.equalsIgnoreCase(sfieldName)) {
                        if (wantedField == null) {
                            currentField.setAccessible(true);
                            if (currentField.isAccessible()) {
                                wantedField = currentField;
                            }
                        }
                        if (fldClass == currentField.getClass()) {
                            wantedField = currentField;
                        }
                    }
                }
                return wantedField;
            }
        } catch (SecurityException anException) {
            anException.printStackTrace();
        }
        return null;
    }

    public static Field fieldWithSerializedName(String name, Class<?> aClass) {
        if (isEmptyStr(name) || aClass == null) {
            return null;
        }
        try {
            Field wantedField = null;
            Field[] fields = aClass.getFields();
            if (fields != null && fields.length > 0) {
                for (Field currentField : fields) {
                    if (!currentField.isAnnotationPresent(SerializedName.class)) {
                        continue;
                    }
                    SerializedName serializedName = currentField.getAnnotation(SerializedName.class);
                    if (name.equals(serializedName.value())) {
                        currentField.setAccessible(true);
                        if (currentField.isAccessible()) {
                            wantedField = currentField;
                        }
                        break;
                    }
                }
                return wantedField;
            }
        } catch (SecurityException anException) {
            anException.printStackTrace();
        }
        return null;
    }

    public static Object invokeMethod(String methodName, Object obj) {
        Object result = null;
        try {
            result = obj.getClass().getMethod(methodName).invoke(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String stringValueForArray(Object[] array) {
        if (array == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(array.length * 16);
        sb.append("( ");
        boolean isFirst = true;
        for (Object o : array) {
            String s = stringValue(o);
            if (s == null) {
                s = "[null]";
            }
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(", ");
            }
            sb.append(s);
        }
        sb.append(" )");
        return sb.toString();
    }

    public static String stringValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return String.valueOf(value);
        }
        if (value instanceof Object[]) {
            return stringValueForArray((Object[]) value);
        }
        return value.toString();
    }

    public static boolean boolValue(Object v) {
        if (v == null) {
            return false;
        }

        if (v instanceof String) {
            if (v.equals("")) {
                return false;
            }
            String s = (String) v;
            char c0 = s.charAt(0);
            if ((c0 == 'Y' || c0 == 'y') && s.equalsIgnoreCase("YES")) {
                return true;
            }
            if ((c0 == 'T' || c0 == 't') && s.equalsIgnoreCase("TRUE")) {
                return true;
            }
            return s.length() == 1 && c0 == '1';
        }

        if (v instanceof Boolean) {
            return (Boolean) v;
        }

        if (v instanceof Number) {
            return ((Number) v).intValue() != 0;
        }

        if (v instanceof Collection) {
            return ((Collection<?>) v).size() > 0;
        }

        return true;
    }

    public static double doubleValue(Object v) {
        if (v == null) {
            return 0.0;
        }
        if (v instanceof Number) {
            return ((Number) v).doubleValue();
        }
        if (v instanceof String) {
            String s = (String) v;
            if (s.trim().length() == 0) {
                return 0.0;
            }
            try {
                if (s.indexOf('.') >= 0) {
                    return new BigDecimal(s).doubleValue();
                }
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return doubleValue(v.toString());
    }

    public static long longValue(Object v) {
        if (v == null) {
            return 0;
        }
        if (v instanceof Number) {
            return ((Number) v).longValue();
        }
        if (v instanceof String) {
            String s = (String) v;
            if (s.length() == 0) {
                return 0;
            }
            try {
                if (s.indexOf('.') >= 0) {
                    return new BigDecimal(s).longValue();
                }
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return longValue(v.toString());
    }

    public static float floatValue(Object v) {
        if (v == null) {
            return 0;
        }
        if (v instanceof Number) {
            return ((Number) v).floatValue();
        }
        if (v instanceof String) {
            String s = (String) v;
            if (s.length() == 0) {
                return 0;
            }
            try {
                if (s.indexOf('.') >= 0) {
                    return new BigDecimal(s).floatValue();
                }
                return Float.parseFloat(s);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return floatValue(v.toString());
    }

    public static int intValue(Object v) {
        if (v == null) {
            return 0;
        }
        if (v instanceof Number) {
            return ((Number) v).intValue();
        }
        if (v instanceof String) {
            String s = (String) v;
            if (s.trim().length() == 0) {
                return 0;
            }
            try {
                if (s.indexOf('.') >= 0) {
                    return new BigDecimal(s).intValue();
                }
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return intValue(v.toString());
    }

    public static boolean isNumber(Object v) {
        if (isEmpty(v)) {
            return false;
        }
        CharSequence cs = (CharSequence) v;
        int length = cs.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static Object valueForSerializedName(String name, Object anObject) {
        if (isNonEmptyStr(name)) {
            if (anObject != null) {
                Class<?> aClass = classForObject(anObject);
                Field aField = fieldWithSerializedName(name, aClass);
                if (aField != null) {
                    Object aValue = null;
                    try {
                        aField.setAccessible(true);
                        aValue = aField.get(anObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return aValue;
                }
                throw new RuntimeException(
                        "RuntimeKeyValue.valueForKey: no getter method/Field found for SerializedName'" + name + "' in " + anObject.getClass() + ".");
            }
            throw new IllegalArgumentException("RuntimeKeyValue.valueForKey: null object.");
        }
        throw new IllegalArgumentException("RuntimeKeyValue.valueForKey: null key.");
    }

    public static Object valueForKey(String aKey, Object anObject) {
        if (aKey != null) {
            if (anObject != null) {
                Class<?> aClass = classForObject(anObject);
                String aName = methodNameForKeyWithPrefix(aKey, BMAObjectUtil.GetterPrefix);
                Method aMethod = getterMethodWithNameInClass(aName, aClass, new Class[] {});
                Field aField = setterFieldWithNameInClass(aKey, aClass, null);
                //First try and get the METHOD. If no method then go for the
                //Field. If the Field does not work either throw an exception
                if (aMethod == null) {
                    aMethod = getterMethodWithNameInClass(aKey, aClass, new Class[] {});
                }

                if (aMethod != null) {
                    Object aValue = null;
                    try {
                        aValue = aMethod.invoke(anObject);

                    } catch (Exception anException) {
                        // throw new RuntimeException("RuntimeKeyValue.valueForKey: aKey = " + aKey + " " + anObject.getClass() + " " + anException);
                    }

                    return aValue;
                } else if (aField != null) {
                    Object aValue = null;
                    try {
                        aField.setAccessible(true);
                        aValue = aField.get(anObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return aValue;
                }

                throw new RuntimeException(
                        "RuntimeKeyValue.valueForKey: no getter method/Field found for key'" + aKey + "' in " + anObject.getClass() + ".");
            }

            throw new IllegalArgumentException("RuntimeKeyValue.valueForKey: null object.");
        }

        throw new IllegalArgumentException("RuntimeKeyValue.valueForKey: null key.");
    }

    public static Map<String, Object> stringToMap(String mapString, String mapSeparator, String keyValueSeparator) {
        Map<String, Object> valueMap = new HashMap<>();
        if (isEmptyStr(mapString) || isEmptyStr(mapSeparator) || isEmptyStr(keyValueSeparator)) {
            return valueMap;
        }
        String[] mapValueArr = mapString.split(mapSeparator);
        if (mapValueArr.length == 0) {
            return valueMap;
        }
        for (String mapValue : mapValueArr) {
            String[] keyValueArr = mapValue.split(keyValueSeparator);
            if (keyValueArr.length < 2 || isEmptyStr(keyValueArr[0])) {
                continue;
            }
            valueMap.put(keyValueArr[0].trim(), keyValueArr[1].trim());
        }
        return valueMap;
    }

    public static Object valueForKeyPath(String aKeyPath, Object anObject) {
        if (anObject == null) {
            return null;
        }
        String[] path = aKeyPath.split(BMAObjectUtil.KeyPathSeparatorRegEx);
        int len = path.length;
        Object current = anObject;

        for (int i = 0; i < len && current != null; i++) {
            if (current instanceof BMAObject) {
                current = valueForKey(path[i], current);
            } else if (current instanceof Map) {
                current = ((Map) current).get(path[i]);
            }
        }
        return current;
    }

    public static <K, V> HashMap<K, V> arrayToKeyValue(List<V> objectArray, String aKeyPath) {
        return arrayToKeyValue(objectArray, aKeyPath, null);
    }

    public static <K, V> HashMap<K, V> arrayToKeyValue(List<V> objectArray, String aKeyPath, String activeKey) {
        HashMap<K, V> mapToReturn = new HashMap<>();
        for (V anObject : objectArray) {
            boolean canAdd = isEmptyStr(activeKey) || boolValue(valueForKeyPath(activeKey, anObject));
            if (!canAdd) {
                continue;
            }
            mapToReturn.put((K) valueForKeyPath(aKeyPath, anObject), anObject);
        }
        return mapToReturn;
    }

    public static <V> LongSparseArray<V> arrayToLongSparseArray(List<V> objectArray, String aKeyPath) {
        return arrayToLongSparseArray(objectArray, aKeyPath, null);
    }

    public static <V> LongSparseArray<V> arrayToLongSparseArray(List<V> objectArray, String aKeyPath, String activeKey) {
        LongSparseArray<V> mapToReturn = new LongSparseArray<>();
        for (V anObject : objectArray) {
            boolean canAdd = isEmptyStr(activeKey) || boolValue(valueForKeyPath(activeKey, anObject));
            if (!canAdd) {
                continue;
            }
            mapToReturn.put((Long) valueForKeyPath(aKeyPath, anObject), anObject);
        }
        return mapToReturn;
    }

    public static <V> SparseArray<V> arrayToSparseArray(List<V> objectArray, String aKeyPath) {
        return arrayToSparseArray(objectArray, aKeyPath, null);
    }

    public static <V> SparseArray<V> arrayToSparseArray(List<V> objectArray, String aKeyPath, String activeKey) {
        SparseArray<V> mapToReturn = new SparseArray<>();
        for (V anObject : objectArray) {
            boolean canAdd = isEmptyStr(activeKey) || boolValue(valueForKeyPath(activeKey, anObject));
            if (!canAdd) {
                continue;
            }
            mapToReturn.put((int) valueForKeyPath(aKeyPath, anObject), anObject);
        }
        return mapToReturn;
    }

    public static <K, V> HashMap<K, ArrayList<V>> arrayToKeyValueList(List<V> objectArray, String aKeyPath) {
        return arrayToKeyValueList(objectArray, aKeyPath, null);
    }

    public static <K, V> HashMap<K, ArrayList<V>> arrayToKeyValueList(List<V> objectArray, String aKeyPath, String activeKey) {
        HashMap<K, ArrayList<V>> returnMap = new HashMap<>();
        if (objectArray == null || isEmptyStr(aKeyPath)) {
            return returnMap;
        }
        for (V anObject : objectArray) {
            boolean canAdd = isEmptyStr(activeKey) || boolValue(valueForKeyPath(activeKey, anObject));
            if (!canAdd) {
                continue;
            }
            K key = (K) valueForKeyPath(aKeyPath, anObject);
            if (key == null) {
                continue;
            }
            ArrayList<V> values = returnMap.get(key);
            if (values == null) {
                values = new ArrayList<>();
                returnMap.put(key, values);
            }
            values.add(anObject);
        }
        return returnMap;
    }

    public static <V> LongSparseArray<ArrayList<V>> arrayToLongSparseArrayList(List<V> objectArray, String aKeyPath) {
        return arrayToLongSparseArrayList(objectArray, aKeyPath, null);
    }

    public static <V> LongSparseArray<ArrayList<V>> arrayToLongSparseArrayList(List<V> objectArray, String aKeyPath, String activekey) {
        LongSparseArray<ArrayList<V>> returnMap = new LongSparseArray<>();
        if (objectArray == null || isEmptyStr(aKeyPath)) {
            return returnMap;
        }
        for (V anObject : objectArray) {
            boolean canAdd = isEmptyStr(activekey) || boolValue(valueForKeyPath(activekey, anObject));
            if (!canAdd) {
                continue;
            }
            Long key = (Long) valueForKeyPath(aKeyPath, anObject);
            if (key == null) {
                continue;
            }
            ArrayList<V> values = returnMap.get(key);
            if (values == null) {
                values = new ArrayList<>();
                returnMap.put(key, values);
            }
            values.add(anObject);
        }
        return returnMap;
    }

}
