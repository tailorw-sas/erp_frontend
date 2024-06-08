package com.kynsof.identity.infrastructure.services;

import com.kynsof.identity.application.query.users.getSearch.UserSystemsResponse;
import com.kynsof.identity.domain.dto.UserStatus;
import com.kynsof.identity.domain.dto.UserSystemDto;
import com.kynsof.identity.domain.interfaces.service.IUserSystemService;
import com.kynsof.identity.infrastructure.identity.UserSystem;
import com.kynsof.identity.infrastructure.repository.command.UserSystemsWriteDataJPARepository;
import com.kynsof.identity.infrastructure.repository.query.UserSystemReadDataJPARepository;
import com.kynsof.share.core.domain.EUserType;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserSystemServiceImpl implements IUserSystemService {

    @Autowired
    private UserSystemsWriteDataJPARepository repositoryCommand;

    @Autowired
    private UserSystemReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(UserSystemDto userSystemDto) {
        UserSystem data = new UserSystem(userSystemDto);
        UserSystem userSystem = this.repositoryCommand.save(data);
        return userSystem.getId();
    }

    @Override
    public void update(UserSystemDto userSystemDto) {
        this.repositoryCommand.save(new UserSystem(userSystemDto));
    }

    @Override
    public void delete(UserSystemDto userSystemDto) {
        UserSystem delete = new UserSystem(userSystemDto);
        delete.setStatus(UserStatus.INACTIVE);
        delete.setDeleted(Boolean.TRUE);
        delete.setEmail(delete.getEmail() + "-" + UUID.randomUUID());
        delete.setUserName(delete.getUserName() + "-" + UUID.randomUUID());

        this.repositoryCommand.save(delete);
    }

    @Override
    public void deleteAll(List<UUID> users) {
        List<UserSystem> delete = new ArrayList<>();
        for (UUID id : users) {
            try {
                UserSystemDto user = this.findById(id);
                UserSystem d = new UserSystem(user);
                d.setStatus(UserStatus.INACTIVE);
                d.setDeleted(Boolean.TRUE);
                d.setEmail(d.getEmail() + "-" + UUID.randomUUID());
                d.setUserName(d.getUserName() + "-" + UUID.randomUUID());

                delete.add(d);
            } catch (Exception e) {
                System.err.println("User not found!!!");
            }
        }
        this.repositoryCommand.saveAll(delete);
    }

    @Override
    public UserSystemDto findById(UUID id) {
        Optional<UserSystem> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.USER_NOT_FOUND, new ErrorField("id", "User not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<UserSystem> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<UserSystem> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private void filterCriteria(List<FilterCriteria> filterCriteria) {
        for (FilterCriteria filter : filterCriteria) {

            if ("status".equals(filter.getKey()) && filter.getValue() instanceof String) {
                try {
                    UserStatus enumValue = UserStatus.valueOf((String) filter.getValue());
                    filter.setValue(enumValue);
                } catch (IllegalArgumentException e) {
                    System.err.println("Valor inválido para el tipo Enum RoleStatus: " + filter.getValue());
                }
            } else if ("userType".equals(filter.getKey()) && filter.getValue() instanceof String) {
                try {
                    EUserType enumValue = EUserType.valueOf((String) filter.getValue());
                    filter.setValue(enumValue);
                } catch (IllegalArgumentException e) {
                    System.err.println("Valor inválido para el tipo Enum RoleStatus: " + filter.getValue());
                }
            }
        }
    }

    private PaginatedResponse getPaginatedResponse(Page<UserSystem> data) {
        List<UserSystemsResponse> userSystemsResponses = new ArrayList<>();
        for (UserSystem p : data.getContent()) {
            userSystemsResponses.add(new UserSystemsResponse(p.toAggregate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    public void updateDelete() {
        List<UserSystem> customer = this.repositoryQuery.findAll();
        for (UserSystem module : customer) {
            if (module.getDeleted() == null || !module.getDeleted().equals(Boolean.TRUE)) {
                module.setDeleted(Boolean.FALSE);
            }
            this.repositoryCommand.save(module);
        }
    }

    @Override
    public Long countByUserNameAndNotId(String userName, UUID id) {
        return this.repositoryQuery.countByUserNameAndNotId(userName, id);
    }

    @Override
    public Long countByEmailAndNotId(String email, UUID id) {
        return this.repositoryQuery.countByEmailAndNotId(email, id);
    }

}
