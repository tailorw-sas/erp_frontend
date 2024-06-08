package com.kynsof.identity.application.query.users.getSearch;

import com.kynsof.share.core.domain.bus.query.IQuery;
import com.kynsof.share.core.domain.request.FilterCriteria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetSearchUserSystemsQuery implements IQuery {

    private Pageable pageable;
    private List<FilterCriteria> filter;
    private String query;

}
