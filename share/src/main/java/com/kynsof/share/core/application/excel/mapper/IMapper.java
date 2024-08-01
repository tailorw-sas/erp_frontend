package com.kynsof.share.core.application.excel.mapper;

import java.util.List;

public interface IMapper <T,R>{

    public R toObject(T obj);

    public T toEntity(R obj);

    public List<R> toObjectList(List<T> listObj);
    public List<T> toEntityList(List<R> list);
}
