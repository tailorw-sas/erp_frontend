package com.kynsof.share.core.application.excel;

import com.kynsof.share.core.application.excel.iterator.ExcelBeanIterator;
import com.kynsof.share.core.application.excel.reader.AbstractReader;

import java.util.Iterator;
public class ExcelBean<T> implements Iterable<T>{

    private final AbstractReader<T> beanReader;

    public ExcelBean(AbstractReader<T> beanReader) {
        this.beanReader = beanReader;
    }

    @Override
    public Iterator<T> iterator() {
        return new ExcelBeanIterator<T>(beanReader);
    }



}
