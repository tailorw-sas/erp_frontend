package com.kynsof.share.core.application.excel.iterator;

import com.kynsof.share.core.application.excel.reader.AbstractReader;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class ExcelBeanIterator<T> implements Iterator<T> {

    private T bean;

    private final AbstractReader<T> beanReader;

    public ExcelBeanIterator(AbstractReader<T> beanReader) {
        this.beanReader = beanReader;
    }

    @Override
    public boolean hasNext() {
        this.bean = beanReader.readSingleLine();
        boolean finished = Objects.nonNull(bean);
        if (finished){
            beanReader.close();
        }
        return finished;
    }

    @Override
    public T next() {
        if (Objects.isNull(bean)){
            throw new NoSuchElementException();
        }
        return bean;
    }
}
