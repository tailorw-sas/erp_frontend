package com.kynsoft.finamer.payment.infrastructure.services;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsof.share.core.infrastructure.specifications.LogicalOperation;
import com.kynsof.share.core.infrastructure.specifications.SearchOperation;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.infrastructure.identity.Payment;
import com.kynsoft.finamer.payment.infrastructure.identity.PaymentView;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManageEmployeeReadDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.view.PaymentViewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentViewService {

    private final PaymentViewRepository paymentViewRepository;
    private final ManageEmployeeReadDataJPARepository employeeReadDataJPARepository;

    public PaymentViewService(PaymentViewRepository paymentViewRepository,
                              ManageEmployeeReadDataJPARepository employeeReadDataJPARepository) {
        this.paymentViewRepository = paymentViewRepository;
        this.employeeReadDataJPARepository = employeeReadDataJPARepository;
    }

    public List<PaymentView> getAllPayments() {
        return paymentViewRepository.findAll();
    }

    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria, UUID employeeId) {
        List<UUID> agencyIds = this.employeeReadDataJPARepository.findAgencyIdsByEmployeeId(employeeId);
        List<UUID> hotelIds = this.employeeReadDataJPARepository.findHotelsIdsByEmployeeId(employeeId);

        if (filterCriteria == null || filterCriteria.isEmpty()) {
            filterCriteria = new ArrayList<>();
            FilterCriteria fcAgency = new FilterCriteria();
            fcAgency.setKey("agencyId");
            fcAgency.setLogicalOperation(LogicalOperation.AND);
            fcAgency.setOperator(SearchOperation.IN);
            fcAgency.setValue(agencyIds);
            filterCriteria.add(fcAgency);

            FilterCriteria fcHotel = new FilterCriteria();
            fcHotel.setKey("hotelId");
            fcHotel.setLogicalOperation(LogicalOperation.AND);
            fcHotel.setOperator(SearchOperation.IN);
            fcHotel.setValue(hotelIds);
            filterCriteria.add(fcHotel);
        }
        else {
            for (FilterCriteria filter : filterCriteria) {
                if (("agencyId".equals(filter.getKey()) || "hotelId".equals(filter.getKey()))
                        && filter.getOperator() == SearchOperation.IN) {
                    if (filter.getValue() instanceof List<?> valueList) {
                        List<UUID> allowedIds = "agencyId".equals(filter.getKey()) ? agencyIds : hotelIds;
                        List<UUID> filteredIds = valueList.stream()
                                .map(item -> {
                                    if (item instanceof UUID) {
                                        return (UUID) item;
                                    } else if (item instanceof String) {
                                        try {
                                            return UUID.fromString((String) item);
                                        } catch (IllegalArgumentException e) {
                                            return null;
                                        }
                                    }
                                    return null;
                                })
                                .filter(Objects::nonNull)
                                .filter(allowedIds::contains)
                                .collect(Collectors.toList());

                        filter.setValue(filteredIds);
                    }
                }
            }
        }

        GenericSpecificationsBuilder<Payment> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<PaymentView> data = this.paymentViewRepository.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<PaymentView> data) {
//        List<PaymentSearchResponse> responses = new ArrayList<>();
//        for (PaymentSearchProjection p : data.getContent()) {
//            responses.add(new PaymentSearchResponse(p));
//        }
        return new PaginatedResponse(data.getContent(), data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}