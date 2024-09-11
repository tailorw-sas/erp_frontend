package com.kynsoft.finamer.settings.application.query.managerMerchantConfig.search;

import com.kynsof.share.core.domain.bus.query.IQuery;
import com.kynsof.share.core.domain.request.FilterCriteria;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Data
@AllArgsConstructor
public class GetSearchManagerMerchantConfigQuery implements IQuery {

    private Pageable pageable;
    private List<FilterCriteria> filter;
    private String query;
}
