package com.kynsof.share.core.application.excel;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class BeanField {

    public final Field field;

    public BeanField( Field field) {
        this.field = field;
    }


    public void setFieldValue(Object value,Object instance){
        String setterName = "set" + Character.toUpperCase(this.field.getName().charAt(0)) + this.field.getName().substring(1);
        try {
            Method setterMethod = this.field.getDeclaringClass().getMethod(setterName, this.field.getType());
            setterMethod.invoke(instance,value);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public Object getFieldValue(Object instance){
        String getterName = "get" + Character.toUpperCase(this.field.getName().charAt(0)) + this.field.getName().substring(1);
        try {
            Method getterMethod = this.field.getDeclaringClass().getMethod(getterName);
            return getterMethod.invoke(instance);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public Class<?> getFieldType(){
       return field.getType();
    }
}
