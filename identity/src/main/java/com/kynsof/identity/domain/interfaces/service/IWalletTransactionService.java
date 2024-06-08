package com.kynsof.identity.domain.interfaces.service;

import com.kynsof.identity.domain.dto.WalletTransactionDto;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IWalletTransactionService {

    UUID create(WalletTransactionDto object);

    void update(WalletTransactionDto object);

    WalletTransactionDto findById(UUID id);

    WalletTransactionDto getById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

}
