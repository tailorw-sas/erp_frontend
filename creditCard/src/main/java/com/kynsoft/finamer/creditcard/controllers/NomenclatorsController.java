package com.kynsoft.finamer.creditcard.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.query.manageAgency.search.GetSearchManageAgencyQuery;
import com.kynsoft.finamer.creditcard.application.query.manageCollectionStatus.search.GetSearchManageCollectionStatusQuery;
import com.kynsoft.finamer.creditcard.application.query.manageCreditCardType.search.GetSearchManageCreditCardTypeQuery;
import com.kynsoft.finamer.creditcard.application.query.manageHotel.search.GetSearchManageHotelQuery;
import com.kynsoft.finamer.creditcard.application.query.manageLanguage.search.GetSearchManageLanguageQuery;
import com.kynsoft.finamer.creditcard.application.query.manageMerchantCommission.search.GetSearchManageMerchantCommissionQuery;
import com.kynsoft.finamer.creditcard.application.query.manageMerchantHotelEnrolle.findHotelsByMerchant.FindHotelsByMerchantQuery;
import com.kynsoft.finamer.creditcard.application.query.manageMerchantHotelEnrolle.search.GetSearchManageMerchantHotelEnrolleQuery;
import com.kynsoft.finamer.creditcard.application.query.manageTransactionStatus.search.GetSearchManageTransactionStatusQuery;
import com.kynsoft.finamer.creditcard.application.query.manageVCCTransactionType.search.GetManageVCCTransactionTypeQuery;
import com.kynsoft.finamer.creditcard.application.query.managerMerchant.search.GetSearchManagerMerchantQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nomenclators")
public class NomenclatorsController {

    private final IMediator mediator;

    public NomenclatorsController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("/agencies")
    public ResponseEntity<?> agencies(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageAgencyQuery query = new GetSearchManageAgencyQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/creditcardtype")
    public ResponseEntity<?> creditcardtype(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageCreditCardTypeQuery query = new GetSearchManageCreditCardTypeQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/hotels")
    public ResponseEntity<?> hotels(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageHotelQuery query = new GetSearchManageHotelQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/languages")
    public ResponseEntity<?> languages(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageLanguageQuery query = new GetSearchManageLanguageQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/mechantcommissions")
    public ResponseEntity<?> mechantcommissions(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageMerchantCommissionQuery query = new GetSearchManageMerchantCommissionQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/merchanthotelenrolle")
    public ResponseEntity<?> merchanthotelenrolle(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageMerchantHotelEnrolleQuery query = new GetSearchManageMerchantHotelEnrolleQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/merchants")
    public ResponseEntity<?> merchants(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManagerMerchantQuery query = new GetSearchManagerMerchantQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/transactionstatus")
    public ResponseEntity<?> transactionstatus(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageTransactionStatusQuery query = new GetSearchManageTransactionStatusQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/vcctransactiontype")
    public ResponseEntity<?> vcctransactiontype(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetManageVCCTransactionTypeQuery query = new GetManageVCCTransactionTypeQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/collectionstatus")
    public ResponseEntity<?> collectionstatus(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageCollectionStatusQuery query = new GetSearchManageCollectionStatusQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/hotels-by-merchant")
    public ResponseEntity<?> hotelsByMerchant(@RequestBody SearchRequest request){
        Pageable pageable = PageableUtil.createPageable(request);

        FindHotelsByMerchantQuery query = new FindHotelsByMerchantQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }
}
