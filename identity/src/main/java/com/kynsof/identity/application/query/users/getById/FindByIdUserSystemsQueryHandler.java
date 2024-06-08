package com.kynsof.identity.application.query.users.getById;

import com.kynsof.identity.domain.dto.UserSystemDto;
import com.kynsof.identity.domain.interfaces.service.IUserSystemService;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import org.springframework.stereotype.Component;

@Component
public class FindByIdUserSystemsQueryHandler implements IQueryHandler<FindByIdUserSystemsQuery, UserSystemsByIdResponse>  {

    private final IUserSystemService serviceImpl;

    public FindByIdUserSystemsQueryHandler(IUserSystemService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public UserSystemsByIdResponse handle(FindByIdUserSystemsQuery query) {
        UserSystemDto userSystemDto = serviceImpl.findById(query.getId());

        return new UserSystemsByIdResponse(userSystemDto);
    }
}
