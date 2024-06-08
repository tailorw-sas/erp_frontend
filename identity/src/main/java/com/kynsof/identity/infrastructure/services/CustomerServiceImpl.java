package com.kynsof.identity.infrastructure.services;

import com.kynsof.identity.application.query.customer.getbyid.CustomerResponse;
import com.kynsof.identity.domain.dto.CustomerDto;
import com.kynsof.identity.domain.interfaces.service.ICustomerService;
import com.kynsof.identity.infrastructure.identity.Customer;
import com.kynsof.identity.infrastructure.identity.Wallet;
import com.kynsof.identity.infrastructure.repository.command.CustomerWriteDataJPARepository;
import com.kynsof.identity.infrastructure.repository.query.CustomerReadDataJPARepository;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private CustomerWriteDataJPARepository repositoryCommand;

    @Autowired
    private CustomerReadDataJPARepository repositoryQuery;

    @Override
    public void create(CustomerDto dto) {
       Customer customer = new Customer(dto);
        Wallet wallet = new Wallet();
        wallet.setCustomer(customer);
        wallet.setId(UUID.randomUUID());
        wallet.setBalance(BigDecimal.valueOf(0));
        wallet.setTransactions(new HashSet<>());

        customer.setWallet(wallet);
        this.repositoryCommand.save(customer);
    }

    @Override
    public void update(CustomerDto customer) {
        this.repositoryCommand.save(new Customer(customer));
    }

    @Override
    public void delete(CustomerDto customer) {
        Customer delete = new Customer(customer);
        delete.setDeleted(Boolean.TRUE);
        delete.setEmail(delete.getEmail() + "-" + UUID.randomUUID());

        this.repositoryCommand.save(delete);
    }

    @Override
    public void deleteAll(List<UUID> customers) {
        List<Customer> delete = new ArrayList<>();
        for (UUID id : customers) {
            try {
                CustomerDto user = this.findById(id);
                Customer d = new Customer(user);
                d.setDeleted(Boolean.TRUE);
                d.setEmail(d.getEmail() + "-" + UUID.randomUUID());

                delete.add(d);
            } catch (Exception e) {
                System.err.println("Customer not found!!!");
            }
        }
        this.repositoryCommand.saveAll(delete);
    }

    @Override
    public CustomerDto findById(UUID id) {
        
        Optional<Customer> object = this.repositoryQuery.findById(id);
        if (object.isPresent()) {
            return object.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.CUSTOMER_NOT_FOUND, new ErrorField("id", "Customer not found.")));

    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<Customer> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Customer> data = this.repositoryQuery.findAll(specifications, pageable);
        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<Customer> data) {
        List<CustomerResponse> patients = new ArrayList<>();
        for (Customer o : data.getContent()) {
            patients.add(new CustomerResponse(o.toAggregate()));
        }
        return new PaginatedResponse(patients, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    public void updateDelete() {
        List<Customer> customer = this.repositoryQuery.findAll();
        for (Customer module : customer) {
            if (module.getDeleted() == null || !module.getDeleted().equals(Boolean.TRUE)) {
                module.setDeleted(Boolean.FALSE);
            }
            this.repositoryCommand.save(module);
        }
    }

}
