package com.kynsof.identity.application.command.user.changeSelectedBusiness;


import com.kynsof.identity.domain.dto.UserSystemDto;
import com.kynsof.identity.domain.interfaces.service.IRedisService;
import com.kynsof.identity.domain.interfaces.service.IUserSystemService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.infrastructure.redis.CacheConfig;
import org.springframework.stereotype.Component;

@Component
public class ChangeSelectedBusinessCommandHandler implements ICommandHandler<ChangeSelectedBusinessCommand> {
    private final IUserSystemService userSystemService;
    private final IRedisService redisService;
    public ChangeSelectedBusinessCommandHandler(IUserSystemService userSystemService, IRedisService redisService) {

        this.userSystemService = userSystemService;
        this.redisService = redisService;
    }

    @Override
    public void handle(ChangeSelectedBusinessCommand command) {
        UserSystemDto user = userSystemService.findById(command.getUserId());
        user.setSelectedBusiness(command.getBusinessId());
        userSystemService.update(user);
        redisService.deleteKey(CacheConfig.USER_CACHE+"::"+command.getUserId());
        command.setResul(true);
    }
}
