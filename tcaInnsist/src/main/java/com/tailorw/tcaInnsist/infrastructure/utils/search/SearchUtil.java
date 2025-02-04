package com.tailorw.tcaInnsist.infrastructure.utils.search;

import com.kynsof.share.core.domain.request.FilterCriteria;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SearchUtil {
    public static <T> T getValueByKey(List<FilterCriteria> filterCriteria, String key, Class<T> type) {
        return filterCriteria.stream()
                .filter(f -> f.getKey().equalsIgnoreCase(key))
                .map(FilterCriteria::getValue)
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst()
                .orElse(null);
    }

    public static <T> T[] convertToArray(Object array, Class<T> targetType){
        if(array instanceof List){
            List<?> list = (List<?>) array;
            return list.stream()
                .map(targetType::cast)
                .toArray(size -> (T[]) Array.newInstance(targetType, size));
        } else {
            return null;
        }
    }

    public static <T, R> List<R> convertToResponse(List<T> dtos, Function<T, R> mapper) {
        return dtos.stream().map(mapper).collect(Collectors.toList());
    }
}
