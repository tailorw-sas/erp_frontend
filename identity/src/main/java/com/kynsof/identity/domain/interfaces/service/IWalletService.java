package com.kynsof.identity.domain.interfaces.service;

import com.kynsof.identity.domain.dto.WalletDto;

import java.util.UUID;

public interface IWalletService {

    //    void create(WalletDto object);
//
//    void update(WalletDto object);
//
//    void delete(UUID id);
//
//    WalletDto findById(UUID id);
//
//    WalletDto getByCustomerId(UUID id);
//
//    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
    WalletDto findByCustomerId(UUID customerId);


}
