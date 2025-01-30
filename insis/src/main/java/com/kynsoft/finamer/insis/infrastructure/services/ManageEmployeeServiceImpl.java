package com.kynsoft.finamer.insis.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.insis.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.insis.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.insis.infrastructure.model.ManageEmployee;
import com.kynsoft.finamer.insis.infrastructure.repository.command.ManageEmployeeWriteDataJPARepository;
import com.kynsoft.finamer.insis.infrastructure.repository.query.ManageEmployeeReadDataJPARepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ManageEmployeeServiceImpl implements IManageEmployeeService {

    private final ManageEmployeeWriteDataJPARepository writeRepository;
    private final ManageEmployeeReadDataJPARepository readRepository;

    public ManageEmployeeServiceImpl(ManageEmployeeWriteDataJPARepository writeRepository,
                                     ManageEmployeeReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public UUID create(ManageEmployeeDto dto) {
        ManageEmployee employee = new ManageEmployee(dto);
        return writeRepository.save(employee).getId();
    }

    @Override
    public void update(ManageEmployeeDto dto) {
        ManageEmployee employee = new ManageEmployee(dto);
        writeRepository.save(employee);
    }

    @Override
    public ManageEmployeeDto findById(UUID id) {
        Optional<ManageEmployee> employee = readRepository.findById(id);
        if(employee.isPresent()){
            return employee.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_EMPLOYEE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.MANAGE_EMPLOYEE_NOT_FOUND.getReasonPhrase())));
    }
}
