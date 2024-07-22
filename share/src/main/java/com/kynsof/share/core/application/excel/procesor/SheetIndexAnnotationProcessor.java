package com.kynsof.share.core.application.excel.procesor;

import com.kynsof.share.core.application.excel.BeanField;
import com.kynsof.share.core.application.excel.annotation.SheetIndex;
import lombok.Getter;

import java.lang.reflect.Field;

@Getter
public class SheetIndexAnnotationProcessor<T> extends AnnotationProcessor<T>{
    private BeanField beanField;
    @Override
    public void process(Class<T> object) {
        Field[] fields = object.getDeclaredFields();
        for (Field field : fields) {
            if(field.isAnnotationPresent(SheetIndex.class)){
                 beanField = new BeanField(field);
            }
        }
    }
}
