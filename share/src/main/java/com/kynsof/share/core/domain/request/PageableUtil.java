package com.kynsof.share.core.domain.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtil {

    public static Pageable createPageable(SearchRequest request) {
        Sort sort = Sort.unsorted();
        if (request.getSortBy() != null && !request.getSortBy().isEmpty()) {
            Sort.Order order = new Sort.Order(
                    request.getSortType().equals(SortTypeEnum.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC,
                    request.getSortBy()
            ).ignoreCase();
            sort = Sort.by(order);
        }
        return PageRequest.of(request.getPage(), request.getPageSize(), sort);
    }
}