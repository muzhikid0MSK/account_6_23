package com.example.account.util;

import android.content.ContentValues;
import android.util.Log;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConvertUtil {

    public static Map<String, Object> convertObjectToMap(Object object, Class klass){
        Map<String, Object> result = new HashMap<>();
        Field[] allFields = klass.getDeclaredFields();
        for (Field field : allFields) {
            try{
                field.setAccessible(true);
                Object value = field.get(object);
                if (value != null){
                    result.put(convertHumpToLine(field.getName()), value);
                }
            }catch (Exception e){
                Log.e("ERROR", "CONVERT ERROR !");
                e.printStackTrace();
            }
        }
        return result;
    }

    public static ContentValues convertMapToContentValues(Map<String, Object> map){
        ContentValues contentValues = new ContentValues();
        for (String key : map.keySet()) {
            if (map.get(key) instanceof Integer){
                contentValues.put(key, (Integer) map.get(key));
            }else if (map.get(key) instanceof Long){
                contentValues.put(key, (Long) map.get(key));
            }else if (map.get(key) instanceof Float){
                contentValues.put(key, (Float) map.get(key));
            }else if (map.get(key) instanceof Double){
                contentValues.put(key, (Double) map.get(key));
            }else if (map.get(key) instanceof String){
                contentValues.put(key, (String) map.get(key));
            }else if (map.get(key) instanceof Boolean){
                contentValues.put(key, (Boolean) map.get(key));
            }else if (map.get(key) instanceof Timestamp) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                contentValues.put(key, (String) dateFormat.format((Timestamp) map.get(key)));
            }else{
                Log.e("ERROR", "WRONG TYPE" + Objects.requireNonNull(map.get(key)).getClass());
            }
        }
        return contentValues;
    }

    public static String convertHumpToLine(String humpWord){
        return humpWord.replaceAll("[A-Z]","_$0").toLowerCase();
    }

    public static Integer convertMonthToDays(String year, String month){
        return convertMonthToDays(Integer.valueOf(year), Integer.valueOf(month));
    }

    public static Integer convertMonthToDays(Integer year, Integer month){
        switch (month){
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 2:
                if (year % 4 == 0 && year % 400 != 0){
                    return 29;
                }else {
                    return 28;
                }
            default:
                return 30;
        }
    }

}
