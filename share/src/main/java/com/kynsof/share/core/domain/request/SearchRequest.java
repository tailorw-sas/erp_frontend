package com.kynsof.share.core.domain.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class SearchRequest  implements Serializable {
    private List<FilterCriteria> filter;
    private String query;
    private Integer pageSize;
    private Integer page;
    private String sortBy;
    private SortTypeEnum sortType;
}
