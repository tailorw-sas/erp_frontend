package com.kynsof.identity.infrastructure.dataSeeder;

import com.kynsof.identity.domain.dto.BusinessDto;
import com.kynsof.identity.domain.dto.UserStatus;
import com.kynsof.identity.domain.dto.UserSystemDto;
import com.kynsof.identity.domain.dto.enumType.EBusinessStatus;
import com.kynsof.identity.infrastructure.identity.Business;
import com.kynsof.identity.infrastructure.identity.UserSystem;
import com.kynsof.identity.infrastructure.repository.command.BusinessWriteDataJPARepository;
import com.kynsof.identity.infrastructure.repository.command.UserSystemsWriteDataJPARepository;
import com.kynsof.identity.infrastructure.repository.query.*;
import com.kynsof.share.core.domain.EUserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.UUID;

@Component
public class DataSeeder implements ApplicationRunner {

    private final UserSystemReadDataJPARepository readRepository;
    private final UserSystemsWriteDataJPARepository writeRepository;
    private final BusinessReadDataJPARepository businessReadDataJPARepository;
    private final BusinessWriteDataJPARepository businessWriteDataJPARepository;
    private final ModuleReadDataJPARepository moduleQuery;
    private final PermissionReadDataJPARepository permissionReadDataJPARepository;
    private final BusinessModuleReadDataJPARepository businessModuleReadDataJPARepository;
    private final DataSource dataSource;

    @Value("${identity.usersystem.id}")
    private String defaultUserid;
    
    @Value("${identity.usersystem.email}")
    private String defaultEmail;
    
    @Value("${identity.usersystem.username}")
    private String defaultUserName;
    
    @Value("${identity.usersystem.name}")
    private String defaultName;
    
    @Value("${identity.usersystem.lastName}")
    private String defaultLastName;

    @Value("${identity.business.id}")
    private String defaultBusinessId;
    
    @Value("${identity.business.name}")
    private String defaultBusinessName;
    
    @Value("${identity.business.description}")
    private String defaultBusinessDescription;
    
    @Value("${identity.business.logoUrl}")
    private String defaultBusinessLogo;
    
    @Value("${identity.business.ruc}")
    private String defaultBusinessRuc;
    
    @Value("${identity.business.address}")
    private String defaultBusinessAddress;

    @Autowired
    public DataSeeder(UserSystemReadDataJPARepository readRepository, UserSystemsWriteDataJPARepository writeRepository, BusinessReadDataJPARepository businessReadDataJPARepository, BusinessWriteDataJPARepository businessWriteDataJPARepository, ModuleReadDataJPARepository moduleQuery, PermissionReadDataJPARepository permissionReadDataJPARepository, BusinessModuleReadDataJPARepository businessModuleReadDataJPARepository, DataSource dataSource) {
        this.readRepository = readRepository;
        this.writeRepository = writeRepository;
        this.businessReadDataJPARepository = businessReadDataJPARepository;
        this.businessWriteDataJPARepository = businessWriteDataJPARepository;
        this.moduleQuery = moduleQuery;
        this.permissionReadDataJPARepository = permissionReadDataJPARepository;
        this.businessModuleReadDataJPARepository = businessModuleReadDataJPARepository;
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // ID del usuario que deseas verificar y crear si no existe
    	UUID userId = UUID.fromString(defaultUserid);
    	
        if (!readRepository.existsById(userId)) {
            // Si no existe, crear el usuario
            UserSystemDto userDto = new UserSystemDto();
            userDto.setId(userId);
            userDto.setUserName(defaultUserName);
            userDto.setEmail(defaultEmail);
            userDto.setName(defaultName);
            userDto.setLastName(defaultLastName);
            userDto.setStatus(UserStatus.ACTIVE);
            userDto.setUserType(EUserType.SYSTEM);
            writeRepository.save(new UserSystem(userDto));

            System.out.println("Seeder: Usuario creado con éxito.");
        } else {
            System.out.println("Seeder: El usuario con ID " + userId + " ya existe.");
        }
        
        UUID businessId = UUID.fromString(defaultBusinessId);
        if (!businessReadDataJPARepository.existsById(businessId)) {
            // Si no existe, crear el usuario
            BusinessDto create = new BusinessDto(
                    businessId,
                    defaultBusinessName,
                    "",
                    "",
                    defaultBusinessDescription,
                    defaultBusinessLogo,
                    defaultBusinessRuc,
                    EBusinessStatus.ACTIVE,
                    null,
                    defaultBusinessAddress
            );
            businessWriteDataJPARepository.save(new Business(create));


            if (moduleQuery.count() == 0) {
                ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("module_system.sql"));
            }
            if (permissionReadDataJPARepository.count() == 0) {
                ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("permission.sql"));
            }
            if (businessModuleReadDataJPARepository.count() == 0) {
                ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("business_module.sql"));
            }
            System.out.println("Seeder: Usuario creado con éxito.");
        } else {
            System.out.println("Seeder: El usuario con ID " + userId + " ya existe.");
        }
    }
}
