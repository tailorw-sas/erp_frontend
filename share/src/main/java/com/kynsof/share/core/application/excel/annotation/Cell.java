package com.kynsof.share.core.application.excel.annotation;

import com.kynsof.share.core.application.excel.CustomCellType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Cell {
    public int position();

    public CustomCellType cellType() default CustomCellType.STRING;

    public String headerName() default "";
}
