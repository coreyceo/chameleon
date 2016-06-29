package com.bidhee.model.comm;

import com.loopj.android.http.RequestParams;

import java.lang.reflect.Field;

/**
 * Created by JinjinLee on 1/6/16.
 */
public class RequestParamUtil {

    public static RequestParams makeParamsByObject(Object object) {

        RequestParams params = new RequestParams();
        for (Field field : object.getClass().getFields()) {
            try {
                System.out.println("Request Object :" + field.getGenericType() + " "
                        + field.getName() + " = " + field.get(object));
                params.put(field.getName(), (String) field.get(object));

            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("Object Parsing Error :" + e.getLocalizedMessage());
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("Object Parsing Error :" + e.getLocalizedMessage());
            }
        }

        return params;
    }
}
