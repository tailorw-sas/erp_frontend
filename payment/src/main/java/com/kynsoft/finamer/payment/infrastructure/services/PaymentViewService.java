package com.kynsoft.finamer.payment.infrastructure.services;


import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsof.share.core.infrastructure.specifications.LogicalOperation;
import com.kynsof.share.core.infrastructure.specifications.SearchOperation;
import com.kynsoft.finamer.payment.application.query.objectResponse.search.PaymentSearchResponse;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.infrastructure.identity.Payment;
import com.kynsoft.finamer.payment.infrastructure.identity.PaymentView;
import com.kynsoft.finamer.payment.infrastructure.identity.projection.PaymentSearchProjection;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManageEmployeeReadDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.view.PaymentViewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentViewService {

    private final PaymentViewRepository paymentViewRepository;
    private final ManageEmployeeReadDataJPARepository employeeReadDataJPARepository;
    public PaymentViewService(PaymentViewRepository paymentViewRepository, ManageEmployeeReadDataJPARepository employeeReadDataJPARepository) {
        this.paymentViewRepository = paymentViewRepository;
        this.employeeReadDataJPARepository = employeeReadDataJPARepository;
    }

    public List<PaymentView> getAllPayments() {
        return paymentViewRepository.findAll();
    }

    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria, UUID employeeId) {
      //  filterCriteriaForEmployee(filterCriteria, employeeId);

        GenericSpecificationsBuilder<Payment> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        //  Page<Payment> data = this.repositoryQuery.findAll(specifications, pageable);
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

    private void filterCriteriaForEmployee(List<FilterCriteria> filterCriteria, UUID employeeId) {
        for (FilterCriteria filter : filterCriteria) {
            if ("status".equals(filter.getKey()) && filter.getValue() instanceof String) {
                try {
                    Status enumValue = Status.valueOf((String) filter.getValue());
                    filter.setValue(enumValue);
                } catch (IllegalArgumentException e) {
                    System.err.println("Valor inválido para el tipo Enum Status: " + filter.getValue());
                }
            }
        }

        List<UUID> agencyIds = this.employeeReadDataJPARepository.findAgencyIdsByEmployeeId(employeeId);
        FilterCriteria fcAgency = new FilterCriteria();
        fcAgency.setKey("agency.id");
        fcAgency.setLogicalOperation(LogicalOperation.AND);
        fcAgency.setOperator(SearchOperation.IN);
        fcAgency.setValue(agencyIds);
        filterCriteria.add(fcAgency);

        List<UUID> hotelIds = this.employeeReadDataJPARepository.findHotelsIdsByEmployeeId(employeeId);
        FilterCriteria fcHotel = new FilterCriteria();
        fcHotel.setKey("hotel.id");
        fcHotel.setLogicalOperation(LogicalOperation.AND);
        fcHotel.setOperator(SearchOperation.IN);
        fcHotel.setValue(hotelIds);
        filterCriteria.add(fcHotel);
    }
}