package com.kynsoft.finamer.audit.domain.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SearchRequest  implements Serializable {
    private List<FilterCriteria> filter;
    private String query;
    private Integer pageSize;
    private Integer page;
    private String sortBy;
    private SortTypeEnum sortType;
}
