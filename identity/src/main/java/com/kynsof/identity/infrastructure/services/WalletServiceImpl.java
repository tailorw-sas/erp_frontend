package com.kynsof.identity.infrastructure.services;

import com.kynsof.identity.domain.dto.WalletDto;
import com.kynsof.identity.domain.interfaces.service.IWalletService;
import com.kynsof.identity.infrastructure.identity.Wallet;
import com.kynsof.identity.infrastructure.repository.query.WalletReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class WalletServiceImpl implements IWalletService {


    @Autowired
    private WalletReadDataJPARepository repositoryQuery;

    @Override
    public WalletDto findByCustomerId(UUID customerId) {
        Optional<Wallet> wallet = repositoryQuery.findByCustomerId(customerId);
        if (wallet.isPresent()) {

        return wallet.get().toAggregate();
        }
        return new WalletDto();
    }

//    @Autowired
//    private BusinessModuleReadDataJPARepository businessModuleReadDataJPARepository;
//
//    @Override
//    public void create(BusinessDto object) {
//        this.repositoryCommand.save(new Business(object));
//    }
//
//    @Override
//    public void update(BusinessDto objectDto) {
//        this.repositoryCommand.save(new Business(objectDto));
//    }
//
//    @Override
//    public void delete(UUID id) {
//
//        BusinessDto objectDelete = this.findById(id);
//        objectDelete.setStatus(EBusinessStatus.INACTIVE);
//
//        objectDelete.setDeleteAt(ConfigureTimeZone.getTimeZone());
//        objectDelete.setDeleted(true);
//
//        this.repositoryCommand.save(new Business(objectDelete));
//    }
//
//    @Override
//    public BusinessDto getByCustomerId(UUID id) {
//
//        Optional<Business> object = this.repositoryQuery.findById(id);
//        if (object.isPresent()) {
//            return object.get().toAggregate();
//        }
//
//        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BUSINESS_NOT_FOUND, new ErrorField("id", "Business not found.")));
//    }
//
//   // @Cacheable(cacheNames = CacheConfig.BUSINESS_CACHE, unless = "#result == null")
//    @Override
//    public BusinessDto findById(UUID id) {
//        Optional<Business> object = this.repositoryQuery.findById(id);
//        if (object.isPresent()) {
//            BusinessDto businessDto = object.get().toAggregate();
//
//            List<ModuleSystem> moduleSystems = businessModuleReadDataJPARepository.findModulesByBusinessId(id);
//            List<ModuleDto> moduleDtoList = moduleSystems.stream()
//                    .map(moduleSystem -> new ModuleDto(
//                                    moduleSystem.getId(),
//                                    moduleSystem.getName(),
//                                    moduleSystem.getImage(),
//                                    moduleSystem.getDescription(),
//                                    null
//                            )
//                    )
//                    .collect(Collectors.toList());
//
//            businessDto.setModuleDtoList(moduleDtoList);
//            return businessDto;
//        } else {
//            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BUSINESS_NOT_FOUND, new ErrorField("Business.id", "Business not found.")));
//        }
//    }
//
//    @Override
//    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
//        filterCreteria(filterCriteria);
//        GenericSpecificationsBuilder<Business> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
//        Page<Business> data = this.repositoryQuery.findAll(specifications, pageable);
//        return getPaginatedResponse(data);
//    }
//
//    private void filterCreteria(List<FilterCriteria> filterCriteria) {
//        for (FilterCriteria filter : filterCriteria) {
//            if ("status".equals(filter.getKey()) && filter.getValue() instanceof String) {
//                try {
//                    EBusinessStatus enumValue = EBusinessStatus.valueOf((String) filter.getValue());
//                    filter.setValue(enumValue);
//                } catch (IllegalArgumentException e) {
//                    System.err.println("Valor inválido para el tipo Enum Empresa: " + filter.getValue());
//                }
//            }
//        }
//    }
//
//    private PaginatedResponse getPaginatedResponse(Page<Business> data) {
//        List<BusinessResponse> patients = new ArrayList<>();
//        for (Business o : data.getContent()) {
//            patients.add(new BusinessResponse(o.toAggregate()));
//        }
//        return new PaginatedResponse(patients, data.getTotalPages(), data.getNumberOfElements(),
//                data.getTotalElements(), data.getSize(), data.getNumber());
//    }


}
