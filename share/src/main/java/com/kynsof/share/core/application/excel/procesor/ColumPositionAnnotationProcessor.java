package com.kynsof.share.core.application.excel.procesor;

import com.kynsof.share.core.application.excel.BeanField;
import com.kynsof.share.core.application.excel.CellInfo;
import com.kynsof.share.core.application.excel.annotation.Cell;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ColumPositionAnnotationProcessor<T> extends AnnotationProcessor<T>{

    private final Map<CellInfo, BeanField> annotatedFields = new HashMap<>();
    @Override
    public void process(Class<T> object) {
        Field[] fields = object.getDeclaredFields();
        for (Field field : fields) {
           if(field.isAnnotationPresent(Cell.class)){
              Cell cell = field.getAnnotation(Cell.class);
               CellInfo cellInfo = new CellInfo(cell.position(),cell.cellType(), cell.headerName());
              BeanField beanField = new BeanField(field);
              annotatedFields.put(cellInfo,beanField);
           }
        }
    }

}
