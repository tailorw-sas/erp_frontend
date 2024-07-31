package com.kynsof.identity.infrastructure.dataSeeder;

import com.kynsof.identity.domain.dto.BusinessDto;
import com.kynsof.identity.domain.dto.BusinessModuleDto;
import com.kynsof.identity.domain.dto.ModuleDto;
import com.kynsof.identity.domain.dto.PermissionDto;
import com.kynsof.identity.domain.dto.UserPermissionBusinessDto;
import com.kynsof.identity.domain.dto.UserStatus;
import com.kynsof.identity.domain.dto.UserSystemDto;
import com.kynsof.identity.domain.dto.enumType.EBusinessStatus;
import com.kynsof.identity.domain.dto.enumType.ModuleStatus;
import com.kynsof.identity.domain.dto.enumType.PermissionStatusEnm;
import com.kynsof.identity.infrastructure.identity.Business;
import com.kynsof.identity.infrastructure.identity.BusinessModule;
import com.kynsof.identity.infrastructure.identity.UserSystem;
import com.kynsof.identity.infrastructure.identity.ModuleSystem;
import com.kynsof.identity.infrastructure.identity.Permission;
import com.kynsof.identity.infrastructure.identity.UserPermissionBusiness;
import com.kynsof.identity.infrastructure.repository.command.BusinessModuleWriteDataJPARepository;
import com.kynsof.identity.infrastructure.repository.command.BusinessWriteDataJPARepository;
import com.kynsof.identity.infrastructure.repository.command.ModuleWriteDataJPARepository;
import com.kynsof.identity.infrastructure.repository.command.PermissionWriteDataJPARepository;
import com.kynsof.identity.infrastructure.repository.command.UserPermissionBusinessWriteDataJPARepository;
import com.kynsof.identity.infrastructure.repository.command.UserSystemsWriteDataJPARepository;
import com.kynsof.identity.infrastructure.repository.query.*;
import com.kynsof.share.core.domain.EUserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    
    private final ModuleWriteDataJPARepository moduleWriteRepository;
    private final BusinessModuleWriteDataJPARepository businessModuleWriteRepository;
    private final PermissionWriteDataJPARepository permissionWriteDataJPARepository;
    private final UserPermissionBusinessReadDataJPARepository userPermissionBusinessReadDataJPARepository;
    private final UserPermissionBusinessWriteDataJPARepository userPermissionBusinessWriteDataJPARepository;
    
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
    
    private List<ModuleSystem> modules;

    @Autowired
    public DataSeeder(UserSystemReadDataJPARepository readRepository, UserSystemsWriteDataJPARepository writeRepository, BusinessReadDataJPARepository businessReadDataJPARepository, BusinessWriteDataJPARepository businessWriteDataJPARepository, ModuleReadDataJPARepository moduleQuery, PermissionReadDataJPARepository permissionReadDataJPARepository, BusinessModuleReadDataJPARepository businessModuleReadDataJPARepository, DataSource dataSource, ModuleWriteDataJPARepository moduleWriteRepository, BusinessModuleWriteDataJPARepository businessModuleWriteRepository, PermissionWriteDataJPARepository permissionWriteDataJPARepository, UserPermissionBusinessReadDataJPARepository userPermissionBusinessReadDataJPARepository, UserPermissionBusinessWriteDataJPARepository userPermissionBusinessWriteDataJPARepository) {
        this.readRepository = readRepository;
        this.writeRepository = writeRepository;
        this.businessReadDataJPARepository = businessReadDataJPARepository;
        this.businessWriteDataJPARepository = businessWriteDataJPARepository;
        this.moduleQuery = moduleQuery;
        this.permissionReadDataJPARepository = permissionReadDataJPARepository;
        this.businessModuleReadDataJPARepository = businessModuleReadDataJPARepository;
        this.dataSource = dataSource;
        this.moduleWriteRepository = moduleWriteRepository;
        this.businessModuleWriteRepository = businessModuleWriteRepository;
        this.permissionWriteDataJPARepository = permissionWriteDataJPARepository;
        this.userPermissionBusinessReadDataJPARepository = userPermissionBusinessReadDataJPARepository;
        this.userPermissionBusinessWriteDataJPARepository = userPermissionBusinessWriteDataJPARepository;
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
            
        } else {
            System.out.println("Seeder: El usuario con ID " + userId + " ya existe.");
        }
        
        try {
        	
        	loadModuleSystem();
            loadBusinessModule(businessId);
            loadPermissions(businessId);
            loadPermissionsUsers(businessId);
            
        }catch(Exception ex) {
        	System.out.print("Error en el seeding de identity: " + ex.toString());
        }
        
    }
    
    
    
    private void loadModuleSystem() {
    	System.out.println("Seeder: Inicio creacion de ModuleSystem.");
    	modules = new ArrayList<ModuleSystem>() {{
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "COLLECTION-MANAGEMENT", null, "COLLECTION MANAGEMENT MODULE", ModuleStatus.ACTIVE, "COLMA")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "INVOICE-MANAGEMENT", null, "INVOICE MANAGEMENT MODULE", ModuleStatus.ACTIVE, "INVMA")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "PAYMENT-MANAGEMENT", null, "PAYMENT MANAGEMENT MODULE", ModuleStatus.ACTIVE, "PAYMA")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "ACTION-LOG", null, "ACTION LOG MANAGER", ModuleStatus.ACTIVE, "ACTLO")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "ALERT", null, "ALERT MODULE", ModuleStatus.ACTIVE, "ALERT")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "CHARGE-TYPE", null, "CHARGER TYPE", ModuleStatus.ACTIVE, "CHART")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "LANGUAGE", null, "LANGUAGE MODULE", ModuleStatus.ACTIVE, "LANG")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "REPORT-PARAM-TYPE", null, "REPORT PARAM TYPE MODULE", ModuleStatus.ACTIVE, "REPT")));
    		
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "BANK-ACCOUNT", null, "BANK ACCOUNT MANAGER", ModuleStatus.ACTIVE, "BNKAC")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "INVOICE-STATUS", null, "INVOICE STATUS MODULE", ModuleStatus.ACTIVE, "INVST")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "INVOICE-TYPE", null, "INVOICE TYPE MODULE", ModuleStatus.ACTIVE, "INVTP")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "ACCOUNT-TYPE", null, "ACCOUNT TYPE MANAGER", ModuleStatus.ACTIVE, "ACCTY")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "BANK", null, "BANK MANAGER", ModuleStatus.ACTIVE, "BNK")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "RATE-PLAN", null, "RATE PLAN MODULE", ModuleStatus.ACTIVE, "RATEP")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "CURRENCY", null, "CURRENCY MANAGER", ModuleStatus.ACTIVE, "CURR")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "INVOICE-TRANSACTION-TYPE", null, "INVOICE-TRANSACTION-TYPE MODULE", ModuleStatus.ACTIVE, "INVTT")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "EMPLOYEE", null, "EMPLOYEE MANAGER", ModuleStatus.ACTIVE, "EMPLO")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "B2BPARTNER", null, "B2BPARTNER MODULE", ModuleStatus.ACTIVE, "BBPA")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "B2BPARTNER-TYPE", null, "B2BPARTNER TYPE MODULE", ModuleStatus.ACTIVE, "BBTYP")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "COLLECTION-STATUS", null, "COLLECTION STATUS MODULE", ModuleStatus.ACTIVE, "COLST")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "MERCHANT", null, "MERCHANT MODULE", ModuleStatus.ACTIVE, "MERCH")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "CREDIT-CARD-TYPE", null, "CREDIT CARD TYPE MODULE", ModuleStatus.ACTIVE, "CRCTY")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "MERCHANT-BANK-ACCOUNT", null, "MERCHANT BANCK ACCOUNT MODULE", ModuleStatus.ACTIVE, "MEBAC")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "MERCHANT-COMMISSION", null, "MERCHANT COMMISSION MODULE", ModuleStatus.ACTIVE, "MECOM")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "MERCHANT-CURRENCY", null, "MERCHANT CURRENCY MODULE", ModuleStatus.ACTIVE, "MECUR")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "MERCHANT-HOTEL-ENROLLE", null, "MERCHANR HOTEL ENROLLE MODULE", ModuleStatus.ACTIVE, "MEHOE")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "RECONCILE-TRANSACTION-STATUS", null, "RECONCILE TRASACTION STATUS MODULE", ModuleStatus.ACTIVE, "RETRS")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "TRANSACTION-STATUS", null, "TRANSACTION STATUS MODULE", ModuleStatus.ACTIVE, "TRAST")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "DEPARTMENT-GROUP", null, "DEPARTMENT GROUP MANAGER", ModuleStatus.ACTIVE, "DEPGR")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "AGENCY", null, "AGENCY MODULE", ModuleStatus.ACTIVE, "AGC")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "VCC-TRANSACTION-TYPE", null, "VCC TRANSACTION TYPE MODULE", ModuleStatus.ACTIVE, "VCCTR")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "ROOM-CATEGORY", null, "ROOM CATEGORY MANAGER", ModuleStatus.ACTIVE, "ROMCT")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "ROOM-TYPE", null, "ROOM TYPE MANAGER", ModuleStatus.ACTIVE, "ROTYP")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "TIMEZONE", null, "TIMEZONE MANAGER", ModuleStatus.ACTIVE, "TIMEZ")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "REGION", null, "REGION MANAGER", ModuleStatus.ACTIVE, "REGIO")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "COUNTRY", null, "COUNTRY MANAGER", ModuleStatus.ACTIVE, "CTRY")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "CITY-STATE", null, "CITY STATE MANAGER", ModuleStatus.ACTIVE, "CITST")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "AGENCY-TYPE", null, "AGENCY TYPE MODULE", ModuleStatus.ACTIVE, "AGCT")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "CLIENT", null, "CLIENT MODULE", ModuleStatus.ACTIVE, "CLI")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "HOTEL", null, "HOTEL MODULE", ModuleStatus.ACTIVE, "HOTEL")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "TRADING-COMPANY", null, "TRADING-COMPANY MODULE", ModuleStatus.ACTIVE, "TCMP")));
    		add(new ModuleSystem(new ModuleDto(UUID.randomUUID(), "REPORT", null, "REPORT MODULE", ModuleStatus.ACTIVE, "RPRT")));

    	} };
    	
    	 
    	for(ModuleSystem module: modules){
    		saveModuleSystemIfNotExists(module);
    	}
    	
    	System.out.println("Seeder: Fin creacion de ModuleSystem.");
    }
    
    private void loadBusinessModule(UUID businessId) {
    	System.out.println("Seeder: Inicio creacion de BusinessModule.");
    	BusinessDto businessDto = businessReadDataJPARepository.findById(businessId).orElse(null).toAggregate();
    	
    	List<BusinessModule> businessModules = new ArrayList<BusinessModule>() {{
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("COLMA"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("INVMA"))));
    		
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("BNKAC"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("INVST"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("INVTP"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("ACCTY"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("BNK"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("RATEP"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("CURR"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("INVTT"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("EMPLO"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("BBPA"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("BBTYP"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("COLST"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("MERCH"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("CRCTY"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("MEBAC"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("MECOM"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("MECUR"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("MEHOE"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("RETRS"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("TRAST"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("DEPGR"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("AGC"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("VCCTR"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("ROMCT"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("ROTYP"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("TIMEZ"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("REGIO"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("CTRY"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("CITST"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("AGCT"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("CLI"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("HOTEL"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("TCMP"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("RPRT"))));

    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("PAYMA"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("ACTLO"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("ALERT"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("CHART"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("LANG"))));
    		add(new BusinessModule(new BusinessModuleDto(UUID.randomUUID(), businessDto, findModuleSystemDtoByCode("REPT"))));

    		
    	} };
    	
    	for(BusinessModule businessModule: businessModules) {
    		saveBusinessModuleIfNotExists(businessModule);
    	}
    	
    	System.out.println("Seeder: Fin creacion de BusinessModule.");
    }
    
    private void loadPermissions(UUID businessId) {
    	System.out.println("Seeder: Inicio creacion de Permission.");
    	BusinessDto businessDto = businessReadDataJPARepository.findById(businessId).orElse(null).toAggregate();
    	
    	List<Permission> permissions = new ArrayList<Permission>() {{    		
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "COLLECTION MANAGEMENT:LIST", "", findModuleSystemDtoByCode("COLMA"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "COLLECTION")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "COLLECTION MANAGEMENT:CREATE", "", findModuleSystemDtoByCode("COLMA"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "COLLECTION CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "COLLECTION MANAGEMENT:EDIT", "", findModuleSystemDtoByCode("COLMA"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "COLLECTION EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "COLLECTION MANAGEMENT:DELETE", "", findModuleSystemDtoByCode("COLMA"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "COLLECTION DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "INVOICE-TRANSACTION-TYPE:CREATE", "", findModuleSystemDtoByCode("INVTT"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "INVOICE-TRANSACTION-TYPE CERATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "ACTION-LOG:LIST", "", findModuleSystemDtoByCode("ACTLO"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "ACTION-LOG LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "ACTION-LOG:CREATE", "", findModuleSystemDtoByCode("ACTLO"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "ACTION-LOG CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "ACTION-LOG:EDIT", "", findModuleSystemDtoByCode("ACTLO"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "ACTION-LOG EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "ACTION-LOG:DELETE", "", findModuleSystemDtoByCode("ACTLO"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "ACTION-LOG DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "ALERT:LIST", "", findModuleSystemDtoByCode("ALERT"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "ALERT-LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "ALERT:CREATE", "", findModuleSystemDtoByCode("ALERT"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "ALERT-CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "ALERT:EDIT", "", findModuleSystemDtoByCode("ALERT"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "ALERT-EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "ALERT:DETAIL", "", findModuleSystemDtoByCode("ALERT"), PermissionStatusEnm.ACTIVE, "DETAIL", LocalDateTime.now(), false, false, "ALERT-DETAIL")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "ALERT:DELETE", "", findModuleSystemDtoByCode("ALERT"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "ALERT-DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "CHARGE-TYPE:LIST", "", findModuleSystemDtoByCode("CHART"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "CHARGE-TYPE LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "LANGUAGE:LIST", "", findModuleSystemDtoByCode("LANG"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "LANGUAGE LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "LANGUAGE:CREATE", "", findModuleSystemDtoByCode("LANG"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "LANGUAGE CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "LANGUAGE:EDIT", "", findModuleSystemDtoByCode("LANG"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "LANGUAGE EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "LANGUAGE:DELETE", "", findModuleSystemDtoByCode("LANG"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "LANGUAGE DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "REPORT-PARAM-TYPE:LIST", "REPORT PARAM TYPE LIST", findModuleSystemDtoByCode("REPT"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "REPORT PARAM TYPE LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "BANK-ACCOUNT:LIST", "", findModuleSystemDtoByCode("BNKAC"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "BANK-ACCOUNT LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "REPORT-PARAM-TYPE:CREATE", "", findModuleSystemDtoByCode("REPT"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "REPORT PARAM TYPE CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "REPORT-PARAM-TYPE:EDIT", "", findModuleSystemDtoByCode("REPT"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "REPORT PARAM TYPE EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "BANK-ACCOUNT:CREATE", "", findModuleSystemDtoByCode("BNKAC"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "BANK-ACCOUNT CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "REPORT-PARAM-TYPE:DELETE", "", findModuleSystemDtoByCode("REPT"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "REPORT PARAM TYPE DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "BANK-ACCOUNT:EDIT", "", findModuleSystemDtoByCode("BNKAC"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "BANK-ACCOUNT EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "BANK-ACCOUNT:DELETE", "", findModuleSystemDtoByCode("BNKAC"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "BANK-ACCOUNT DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "INVOICE-STATUS:LIST", "", findModuleSystemDtoByCode("INVST"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "INVOICE STATUS LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "INVOICE-STATUS:CREATE", "", findModuleSystemDtoByCode("INVST"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "INVOICE STATUS CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "INVOICE-STATUS:EDIT", "", findModuleSystemDtoByCode("INVST"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "INVOICE STATUS EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "INVOICE-STATUS:DELETE", "", findModuleSystemDtoByCode("INVST"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "INVOICE STATUS DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "INVOICE-STATUS:DETAIL", "", findModuleSystemDtoByCode("INVST"), PermissionStatusEnm.ACTIVE, "DETAIL", LocalDateTime.now(), false, false, "INVOICE STATUS DETAIL")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "INVOICE-TYPE:LIST", "", findModuleSystemDtoByCode("INVTP"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "INVOICE TYPE LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "INVOICE-TYPE:CREATE", "", findModuleSystemDtoByCode("INVTP"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "INVOICE TYPE CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "INVOICE-TYPE:EDIT", "", findModuleSystemDtoByCode("INVTP"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "INVOICE TYPE EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "INVOICE-TYPE:DELETE", "", findModuleSystemDtoByCode("INVTP"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "INVOICE TYPE DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "INVOICE-TYPE:DETAIL", "", findModuleSystemDtoByCode("INVTP"), PermissionStatusEnm.ACTIVE, "DETAIL", LocalDateTime.now(), false, false, "INVOICE TYPE DETAIL")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "ACCOUNT-TYPE:LIST", "", findModuleSystemDtoByCode("ACCTY"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "ACCOUNT-TYPE LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "ACCOUNT-TYPE:CREATE", "", findModuleSystemDtoByCode("ACCTY"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "ACCOUNT-TYPE CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "ACCOUNT-TYPE:EDIT", "", findModuleSystemDtoByCode("ACCTY"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "ACCOUNT-TYPE EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "ACCOUNT-TYPE:DELETE", "", findModuleSystemDtoByCode("ACCTY"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "ACCOUNT-TYPE DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "BANK:LIST", "", findModuleSystemDtoByCode("BNK"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "BANK LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "BANK:CREATE", "", findModuleSystemDtoByCode("BNK"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "BANK CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "BANK:EDIT", "", findModuleSystemDtoByCode("BNK"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "BANK EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "BANK:DELETE", "", findModuleSystemDtoByCode("BNK"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "BANK DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "RATE-PLAN:LIST", "", findModuleSystemDtoByCode("RATEP"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "RATE-PLAN LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "RATE-PLAN:CREATE", "", findModuleSystemDtoByCode("RATEP"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "RATE-PLAN EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "RATE-PLAN:EDIT", "", findModuleSystemDtoByCode("RATEP"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "RATE-PLAN EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "RATE-PLAN:DELETE", "", findModuleSystemDtoByCode("RATEP"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "RATE-PLAN DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "RATE-PLAN:DETAIL", "", findModuleSystemDtoByCode("RATEP"), PermissionStatusEnm.ACTIVE, "DETAIL", LocalDateTime.now(), false, false, "RATE-PLAN DETAIL")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "CURRENCY:LIST", "", findModuleSystemDtoByCode("CURR"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "CURRENCY LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "CURRENCY:CREATE", "", findModuleSystemDtoByCode("CURR"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "CURRENCY CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "CURRENCY:EDIT", "", findModuleSystemDtoByCode("CURR"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "CURRENCY EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "CURRENCY:DELETE", "", findModuleSystemDtoByCode("CURR"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "CURRENCY DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "INVOICE-TRANSACTION-TYPE:LIST", "", findModuleSystemDtoByCode("INVTT"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "INVOICE-TRANSACTION-TYPE LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "INVOICE-TRANSACTION-TYPE:EDIT", "", findModuleSystemDtoByCode("INVTT"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "INVOICE-TRANSACTION-TYPE EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "INVOICE-TRANSACTION-TYPE:DELETE", "", findModuleSystemDtoByCode("INVTT"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "INVOICE-TRANSACTION-TYPE DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "EMPLOYEE:LIST", "", findModuleSystemDtoByCode("EMPLO"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "EMPLOYEE LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "EMPLOYEE:CREATE", "", findModuleSystemDtoByCode("EMPLO"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "EMPLOYEE CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "EMPLOYEE:EDIT", "", findModuleSystemDtoByCode("EMPLO"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "EMPLOYEE EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "INVOICE-TRANSACTION-TYPE:DETAIL", "", findModuleSystemDtoByCode("INVTT"), PermissionStatusEnm.ACTIVE, "DETAIL", LocalDateTime.now(), false, false, "INVOICE-TRANSACTION-TYPE DETAIL")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "EMPLOYEE:DELETE", "", findModuleSystemDtoByCode("EMPLO"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "EMPLOYEE DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "EMPLOYEE:CLONE", "", findModuleSystemDtoByCode("EMPLO"), PermissionStatusEnm.ACTIVE, "CLONE", LocalDateTime.now(), false, false, "EMPLOYEE CLONE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "EMPLOYEE:ASSIGN", "", findModuleSystemDtoByCode("EMPLO"), PermissionStatusEnm.ACTIVE, "ASSIGN", LocalDateTime.now(), false, false, "EMPLOYEE ASSIGN")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "B2BPARTNER:CREATE", "B2BPARTNER CREATE PERMISSION", findModuleSystemDtoByCode("BBPA"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "B2BPARTNER CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "B2BPARTNER:LIST", "B2BPARTNER LIST PERMISSION", findModuleSystemDtoByCode("BBPA"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "B2BPARTNER LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "B2BPARTNER:EDIT", "B2BPARTNER EDIT PERMISSION", findModuleSystemDtoByCode("BBPA"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "B2BPARTNER EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "B2BPARTNER:DELETE", "B2BPARTNER DELETE PERMISSION", findModuleSystemDtoByCode("BBPA"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "B2BPARTNER DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "B2BPARTNER-TYPE:CREATE", "B2BPARTNER-TYPE CREATE PERMISSION", findModuleSystemDtoByCode("BBTYP"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "B2BPARTNER-TYPE CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "B2BPARTNER-TYPE:LIST", "B2BPARTNER-TYPE LIST PERMISSION", findModuleSystemDtoByCode("BBTYP"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "B2BPARTNER-TYPE LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "B2BPARTNER-TYPE:EDIT", "B2BPARTNER-TYPE EDIT PERMISSION", findModuleSystemDtoByCode("BBTYP"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "B2BPARTNER-TYPE EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "B2BPARTNER-TYPE:DELETE", "B2BPARTNER-TYPE DELETE PERMISSION", findModuleSystemDtoByCode("BBTYP"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "B2BPARTNER-TYPE DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "COLLECTION-STATUS:LIST", "COLLECTION-STATUS LIST PERMISSION", findModuleSystemDtoByCode("COLST"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "COLLECTION-STATUS LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "COLLECTION-STATUS:CREATE", "COLLECTION-STATUS CREATE PERMISSION", findModuleSystemDtoByCode("COLST"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "COLLECTION-STATUS CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "COLLECTION-STATUS:EDIT", "COLLECTION-STATUS EDIT PERMISSION", findModuleSystemDtoByCode("COLST"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "COLLECTION-STATUS EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "COLLECTION-STATUS:DELETE", "COLLECTION-STATUS DELETE PERMISSION", findModuleSystemDtoByCode("COLST"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "COLLECTION-STATUS DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "CREDIT-CARD-TYPE:LIST", "CREDIT-CARD-TYPE LIST PERMISSION", findModuleSystemDtoByCode("CRCTY"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "CREDIT-CARD-TYPE LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "CREDIT-CARD-TYPE:CREATE", "CREDIT-CARD-TYPE CREATE PERMISSION", findModuleSystemDtoByCode("CRCTY"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "CREDIT-CARD-TYPE CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "CREDIT-CARD-TYPE:EDIT", "CREDIT-CARD-TYPE EDIT PERMISSION", findModuleSystemDtoByCode("CRCTY"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "CREDIT-CARD-TYPE EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "DEPARTMENT-GROUP:LIST", "", findModuleSystemDtoByCode("DEPGR"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "DEPARTMENT-GROUP LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "DEPARTMENT-GROUP:CREATE", "", findModuleSystemDtoByCode("DEPGR"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "DEPARTMENT-GROUP CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "VCC-TRANSACTION-TYPE:LIST", "VCC-TRANSACTION-TYPE LIST PERMISSION", findModuleSystemDtoByCode("VCCTR"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "VCC-TRANSACTION-TYPE LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "DEPARTMENT-GROUP:EDIT", "", findModuleSystemDtoByCode("DEPGR"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "DEPARTMENT-GROUP EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "AGENCY:LIST", "", findModuleSystemDtoByCode("AGC"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "AGENCY LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "DEPARTMENT-GROUP:DELETE", "", findModuleSystemDtoByCode("DEPGR"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "DEPARTMENT-GROUP DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "VCC-TRANSACTION-TYPE:CREATE", "VCC-TRANSACTION-TYPE CREATE PERMISSION", findModuleSystemDtoByCode("VCCTR"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "VCC-TRANSACTION-TYPE CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "VCC-TRANSACTION-TYPE:EDIT", "VCC-TRANSACTION-TYPE EDIT PERMISSION", findModuleSystemDtoByCode("VCCTR"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "VCC-TRANSACTION-TYPE EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "VCC-TRANSACTION-TYPE:DELETE", "VCC-TRANSACTION-TYPE DELETE PERMISSION", findModuleSystemDtoByCode("VCCTR"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "VCC-TRANSACTION-TYPE DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "MERCHANT:LIST", "MERCHANT LIST PERMISSION", findModuleSystemDtoByCode("MERCH"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "MERCHANT LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "MERCHANT:CREATE", "MERCHANT CRETE PERMISSION", findModuleSystemDtoByCode("MERCH"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "MERCHANT CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "MERCHANT:EDIT", "MERCHANT EDIT PERMISSION", findModuleSystemDtoByCode("MERCH"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "MERCHANT EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "ROOM-CATEGORY:LIST", "", findModuleSystemDtoByCode("ROMCT"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "ROOM-CATEGORY LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "ROOM-CATEGORY:CREATE", "", findModuleSystemDtoByCode("ROMCT"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "ROOM-CATEGORY CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "MERCHANT-BANK-ACCOUNT:LIST", "MERCHANT-BANK-ACCOUNT LIST PERMISSION", findModuleSystemDtoByCode("MEBAC"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "MERCHANT-BANK-ACCOUNT LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "ROOM-CATEGORY:EDIT", "", findModuleSystemDtoByCode("ROMCT"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "ROOM-CATEGORY EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "ROOM-CATEGORY:DELETE", "", findModuleSystemDtoByCode("ROMCT"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "ROOM-CATEGORY DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "MERCHANT-BANK-ACCOUNT:CREATE", "MERCHANT-BANK-ACCOUNT CREATE PERMISSION", findModuleSystemDtoByCode("MEBAC"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "MERCHANT-BANK-ACCOUNT CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "MERCHANT-BANK-ACCOUNT:EDIT", "MERCHANT-BANK-ACCOUNT EDIT PERMISSION", findModuleSystemDtoByCode("MEBAC"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "MERCHANT-BANK-ACCOUNT EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "ROOM-TYPE:LIST", "", findModuleSystemDtoByCode("ROTYP"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "ROOM-TYPE LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "ROOM-TYPE:CREATE", "", findModuleSystemDtoByCode("ROTYP"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "ROOM-TYPE CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "ROOM-TYPE:EDIT", "", findModuleSystemDtoByCode("ROTYP"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "ROOM-TYPE EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "ROOM-TYPE:DELETE", "", findModuleSystemDtoByCode("ROTYP"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "ROOM-TYPE DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "AGENCY:CREATE", "", findModuleSystemDtoByCode("AGC"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "AGENCY CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "TIMEZONE:LIST", "", findModuleSystemDtoByCode("TIMEZ"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "TIMEZONE LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "TIMEZONE:CREATE", "", findModuleSystemDtoByCode("TIMEZ"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "TIMEZONE CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "TIMEZONE:EDIT", "", findModuleSystemDtoByCode("TIMEZ"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "TIMEZONE EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "TIMEZONE:DELETE", "", findModuleSystemDtoByCode("TIMEZ"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "TIMEZONE DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "REGION:LIST", "", findModuleSystemDtoByCode("REGIO"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "REGION LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "REGION:CREATE", "", findModuleSystemDtoByCode("REGIO"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "REGION CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "REGION:EDIT", "", findModuleSystemDtoByCode("REGIO"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "REGION EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "REGION:DELETE", "", findModuleSystemDtoByCode("REGIO"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "REGION DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "COUNTRY:LIST", "", findModuleSystemDtoByCode("CTRY"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "COUNTRY LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "COUNTRY:CREATE", "", findModuleSystemDtoByCode("CTRY"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "COUNTRY CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "COUNTRY:EDIT", "", findModuleSystemDtoByCode("CTRY"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "COUNTRY EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "COUNTRY:DELETE", "", findModuleSystemDtoByCode("CTRY"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "COUNTRY DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "AGENCY:EDIT", "", findModuleSystemDtoByCode("AGC"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "AGENCY EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "AGENCY:DELETE", "", findModuleSystemDtoByCode("AGC"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "AGENCY DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "CITY-STATE:LIST", "", findModuleSystemDtoByCode("CITST"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "CITY-STATE LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "MERCHANT-BANK-ACCOUNT:DELETE", "MERCHANT-BANK-ACCOUNT DELETE PERMISSION", findModuleSystemDtoByCode("MEBAC"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "MERCHANT-BANK-ACCOUNT DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "CITY-STATE:CREATE", "", findModuleSystemDtoByCode("CITST"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "CITY-STATE CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "CITY-STATE:EDIT", "", findModuleSystemDtoByCode("CITST"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "CITY-STATE EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "MERCHANT:DELETE", "MERCHANT DELETE PERMISSION", findModuleSystemDtoByCode("MERCH"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "MERCHANT DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "CITY-STATE:DELETE", "", findModuleSystemDtoByCode("CITST"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "CITY-STATE DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "MERCHANT-COMMISSION:LIST", "MERCHANT-COMMISSION LIST PERMISSION", findModuleSystemDtoByCode("MECOM"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "MERCHANT-COMMISSION LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "MERCHANT-COMMISSION:CREATE", "MERCHANT-COMMISSION CREATE PERMISSION", findModuleSystemDtoByCode("MECOM"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "MERCHANT-COMMISSION CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "MERCHANT-COMMISSION:EDIT", "MERCHANT-COMMISSION EDIT PERMISSION", findModuleSystemDtoByCode("MECOM"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "MERCHANT-COMMISSION EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "MERCHANT-COMMISSION:DELETE", "MERCHANT-COMMISSION DELETE PERMISSION", findModuleSystemDtoByCode("MECOM"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "MERCHANT-COMMISSION DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "MERCHANT-CURRENCY:LIST", "MERCHANT-CURRENCY LIST PERMISSION", findModuleSystemDtoByCode("MECUR"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "MERCHANT-CURRENCY LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "MERCHANT-CURRENCY:CREATE", "MERCHANT-CURRENCY CREATE", findModuleSystemDtoByCode("MECUR"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "MERCHANT-CURRENCY CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "MERCHANT-CURRENCY:EDIT", "MERCHANT-CURRENCY EDIT PERMISSION", findModuleSystemDtoByCode("MECUR"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "MERCHANT-CURRENCY EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "MERCHANT-CURRENCY:DELETE", "MERCHANT-CURRENCY DELETE PERMISSION", findModuleSystemDtoByCode("MECUR"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "MERCHANT-CURRENCY DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "MERCHANT-HOTEL-ENROLLE:LIST", "MERCHANT-HOTEL-ENROLLE LIST PERMISSION", findModuleSystemDtoByCode("MEHOE"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "MERCHANT-HOTEL-ENROLLE LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "MERCHANT-HOTEL-ENROLLE:CREATE", "MERCHANT-HOTEL-ENROLLE CREATE PERMISSION", findModuleSystemDtoByCode("MEHOE"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "MERCHANT-HOTEL-ENROLLE CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "MERCHANT-HOTEL-ENROLLE:EDIT", "MERCHANT-HOTEL-ENROLLE EDIT PERMISSION", findModuleSystemDtoByCode("MEHOE"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "MERCHANT-HOTEL-ENROLLE EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "MERCHANT-HOTEL-ENROLLE:DELETE", "MERCHANT-HOTEL-ENROLLE DELETE PERMISSION", findModuleSystemDtoByCode("MEHOE"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "MERCHANT-HOTEL-ENROLLE DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "RECONCILE-TRANSACTION-STATUS:LIST", "RECONCILE-TRANSACTION-STATUS LIST PERMISSION", findModuleSystemDtoByCode("RETRS"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "RECONCILE-TRANSACTION-STATUS LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "AGENCY-TYPE:LIST", "", findModuleSystemDtoByCode("AGCT"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "AGENCY-TYPE LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "RECONCILE-TRANSACTION-STATUS:CREATE", "RECONCILE-TRANSACTION-STATUS CREATE PERMISSION", findModuleSystemDtoByCode("RETRS"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "RECONCILE-TRANSACTION-STATUS CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "RECONCILE-TRANSACTION-STATUS:EDIT", "RECONCILE-TRANSACTION-STATUS EDIT PERMISSION", findModuleSystemDtoByCode("RETRS"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "RECONCILE-TRANSACTION-STATUS EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "RECONCILE-TRANSACTION-STATUS:DELETE", "RECONCILE-TRANSACTION-STATUS DELETE PERMISSION", findModuleSystemDtoByCode("RETRS"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "RECONCILE-TRANSACTION-STATUS DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "TRANSACTION-STATUS:LIST", "TRANSACTION-STATUS LIST PERMISSION", findModuleSystemDtoByCode("TRAST"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "TRANSACTION-STATUS LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "TRANSACTION-STATUS:CREATE", "TRANSACTION-STATUS CREATE PERMISSION", findModuleSystemDtoByCode("TRAST"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "TRANSACTION-STATUS CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "AGENCY-TYPE:CREATE", "", findModuleSystemDtoByCode("AGCT"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "AGENCY-TYPE CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "TRANSACTION-STATUS:EDIT", "TRANSACTION-STATUS EDIT PERMISSION", findModuleSystemDtoByCode("TRAST"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "TRANSACTION-STATUS EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "AGENCY-TYPE:EDIT", "", findModuleSystemDtoByCode("AGCT"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "AGENCY-TYPE EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "TRANSACTION-STATUS:DELETE", "TRANSACTION-STATUS DELETE PERMISSION", findModuleSystemDtoByCode("TRAST"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "TRANSACTION-STATUS DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "AGENCY-TYPE:DELETE", "", findModuleSystemDtoByCode("AGCT"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "AGENCY-TYPE DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "AGENCY-TYPE:DETAIL", "", findModuleSystemDtoByCode("AGCT"), PermissionStatusEnm.ACTIVE, "DETAIL", LocalDateTime.now(), false, false, "AGENCY-TYPE DETAIL")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "CLIENT:LIST", "", findModuleSystemDtoByCode("CLI"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "CLIENT LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "CLIENT:CREATE", "", findModuleSystemDtoByCode("CLI"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "CLIENT CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "CLIENT:EDIT", "", findModuleSystemDtoByCode("CLI"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "CLIENT EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "CLIENT:DELETE", "", findModuleSystemDtoByCode("CLI"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "CLIENT DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "CLIENT:DETAIL", "", findModuleSystemDtoByCode("CLI"), PermissionStatusEnm.ACTIVE, "DETAIL", LocalDateTime.now(), false, false, "CLIENT DEATAIL")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "HOTEL:LIST", "", findModuleSystemDtoByCode("HOTEL"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "HOTEL LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "HOTEL:CREATE", "", findModuleSystemDtoByCode("HOTEL"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "HOTEL CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "HOTEL:EDIT", "", findModuleSystemDtoByCode("HOTEL"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "HOTEL EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "HOTEL:DELETE", "", findModuleSystemDtoByCode("HOTEL"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "HOTEL DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "HOTEL:DETAIL", "", findModuleSystemDtoByCode("HOTEL"), PermissionStatusEnm.ACTIVE, "DETAIL", LocalDateTime.now(), false, false, "HOTEL DETAIL")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "TRADING-COMPANY:LIST", "", findModuleSystemDtoByCode("TCMP"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "TRADING-COMPANY LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "TRADING-COMPANY:CREATE", "", findModuleSystemDtoByCode("TCMP"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "TRADING-COMPANY CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "TRADING-COMPANY:EDIT", "", findModuleSystemDtoByCode("TCMP"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "TRADING-COMPANY EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "TRADING-COMPANY:DELETE", "", findModuleSystemDtoByCode("TCMP"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "TRADING-COMPANY DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "TRADING-COMPANY:DETAIL", "", findModuleSystemDtoByCode("TCMP"), PermissionStatusEnm.ACTIVE, "DETAIL", LocalDateTime.now(), false, false, "TRADING-COMPANY DETAIL")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "REPORT:LIST", "", findModuleSystemDtoByCode("RPRT"), PermissionStatusEnm.ACTIVE, "LIST", LocalDateTime.now(), false, false, "REPORT LIST")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "REPORT:CREATE", "", findModuleSystemDtoByCode("RPRT"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "REPORT CREATE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "REPORT:EDIT", "", findModuleSystemDtoByCode("RPRT"), PermissionStatusEnm.ACTIVE, "EDIT", LocalDateTime.now(), false, false, "REPORT EDIT")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "REPORT:DELETE", "", findModuleSystemDtoByCode("RPRT"), PermissionStatusEnm.ACTIVE, "DELETE", LocalDateTime.now(), false, false, "REPORT DELETE")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "REPORT:DETAIL", "", findModuleSystemDtoByCode("RPRT"), PermissionStatusEnm.ACTIVE, "DETAIL", LocalDateTime.now(), false, false, "REPORT DETAIL")));
    		add(new Permission(new PermissionDto(UUID.randomUUID(), "AGENCY:DETAIL", "", findModuleSystemDtoByCode("AGC"), PermissionStatusEnm.ACTIVE, "DETAIL", LocalDateTime.now(), false, false, "AGENCY DETAILT")));

    		add(new Permission(new PermissionDto(UUID.randomUUID(), "CHARGE-TYPE:CREATE", "", findModuleSystemDtoByCode("CHART"), PermissionStatusEnm.ACTIVE, "CREATE", LocalDateTime.now(), false, false, "CHARGE-TYPE CREATE")));
    	}};
    	
    	for(Permission permission: permissions) {
    		savePermissionIfNotExists(permission);
    	}
    	
    	System.out.println("Seeder: Fin creacion de Permission.");
    }
    
    private void loadPermissionsUsers(UUID businessId) {
    	System.out.println("Seeder: Inicio creacion de UserPermissionBusiness.");
    	
    	UserSystemDto userDto = readRepository.findByEmail(defaultEmail).orElse(null).toAggregate();
    	BusinessDto businessDto = businessReadDataJPARepository.findById(businessId).orElse(null).toAggregate();
    	
    	List<UserPermissionBusiness> permissionsUsers = new ArrayList<UserPermissionBusiness>() {{     		
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("COLLECTION MANAGEMENT:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("COLLECTION MANAGEMENT:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("COLLECTION MANAGEMENT:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("COLLECTION MANAGEMENT:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("INVOICE-TRANSACTION-TYPE:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("ACTION-LOG:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("ACTION-LOG:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("ACTION-LOG:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("ACTION-LOG:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("ALERT:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("ALERT:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("ALERT:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("ALERT:DETAIL"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("ALERT:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("CHARGE-TYPE:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("LANGUAGE:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("LANGUAGE:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("LANGUAGE:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("LANGUAGE:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("REPORT-PARAM-TYPE:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("BANK-ACCOUNT:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("REPORT-PARAM-TYPE:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("REPORT-PARAM-TYPE:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("BANK-ACCOUNT:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("REPORT-PARAM-TYPE:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("BANK-ACCOUNT:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("BANK-ACCOUNT:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("INVOICE-STATUS:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("INVOICE-STATUS:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("INVOICE-STATUS:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("INVOICE-STATUS:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("INVOICE-STATUS:DETAIL"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("INVOICE-TYPE:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("INVOICE-TYPE:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("INVOICE-TYPE:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("INVOICE-TYPE:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("INVOICE-TYPE:DETAIL"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("ACCOUNT-TYPE:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("ACCOUNT-TYPE:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("ACCOUNT-TYPE:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("ACCOUNT-TYPE:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("BANK:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("BANK:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("BANK:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("BANK:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("RATE-PLAN:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("RATE-PLAN:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("RATE-PLAN:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("RATE-PLAN:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("RATE-PLAN:DETAIL"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("CURRENCY:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("CURRENCY:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("CURRENCY:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("CURRENCY:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("INVOICE-TRANSACTION-TYPE:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("INVOICE-TRANSACTION-TYPE:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("INVOICE-TRANSACTION-TYPE:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("EMPLOYEE:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("EMPLOYEE:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("EMPLOYEE:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("INVOICE-TRANSACTION-TYPE:DETAIL"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("EMPLOYEE:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("EMPLOYEE:CLONE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("EMPLOYEE:ASSIGN"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("B2BPARTNER:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("B2BPARTNER:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("B2BPARTNER:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("B2BPARTNER:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("B2BPARTNER-TYPE:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("B2BPARTNER-TYPE:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("B2BPARTNER-TYPE:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("B2BPARTNER-TYPE:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("COLLECTION-STATUS:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("COLLECTION-STATUS:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("COLLECTION-STATUS:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("COLLECTION-STATUS:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("CREDIT-CARD-TYPE:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("CREDIT-CARD-TYPE:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("CREDIT-CARD-TYPE:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("DEPARTMENT-GROUP:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("DEPARTMENT-GROUP:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("VCC-TRANSACTION-TYPE:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("DEPARTMENT-GROUP:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("AGENCY:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("DEPARTMENT-GROUP:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("VCC-TRANSACTION-TYPE:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("VCC-TRANSACTION-TYPE:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("VCC-TRANSACTION-TYPE:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("MERCHANT:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("MERCHANT:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("MERCHANT:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("ROOM-CATEGORY:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("ROOM-CATEGORY:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("MERCHANT-BANK-ACCOUNT:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("ROOM-CATEGORY:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("ROOM-CATEGORY:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("MERCHANT-BANK-ACCOUNT:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("MERCHANT-BANK-ACCOUNT:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("ROOM-TYPE:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("ROOM-TYPE:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("ROOM-TYPE:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("ROOM-TYPE:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("AGENCY:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("TIMEZONE:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("TIMEZONE:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("TIMEZONE:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("TIMEZONE:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("REGION:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("REGION:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("REGION:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("REGION:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("COUNTRY:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("COUNTRY:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("COUNTRY:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("COUNTRY:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("AGENCY:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("AGENCY:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("CITY-STATE:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("MERCHANT-BANK-ACCOUNT:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("CITY-STATE:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("CITY-STATE:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("MERCHANT:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("CITY-STATE:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("MERCHANT-COMMISSION:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("MERCHANT-COMMISSION:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("MERCHANT-COMMISSION:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("MERCHANT-COMMISSION:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("MERCHANT-CURRENCY:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("MERCHANT-CURRENCY:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("MERCHANT-CURRENCY:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("MERCHANT-CURRENCY:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("MERCHANT-HOTEL-ENROLLE:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("MERCHANT-HOTEL-ENROLLE:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("MERCHANT-HOTEL-ENROLLE:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("MERCHANT-HOTEL-ENROLLE:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("RECONCILE-TRANSACTION-STATUS:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("AGENCY-TYPE:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("RECONCILE-TRANSACTION-STATUS:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("RECONCILE-TRANSACTION-STATUS:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("RECONCILE-TRANSACTION-STATUS:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("TRANSACTION-STATUS:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("TRANSACTION-STATUS:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("AGENCY-TYPE:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("TRANSACTION-STATUS:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("AGENCY-TYPE:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("TRANSACTION-STATUS:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("AGENCY-TYPE:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("AGENCY-TYPE:DETAIL"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("CLIENT:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("CLIENT:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("CLIENT:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("CLIENT:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("CLIENT:DETAIL"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("HOTEL:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("HOTEL:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("HOTEL:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("HOTEL:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("HOTEL:DETAIL"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("TRADING-COMPANY:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("TRADING-COMPANY:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("TRADING-COMPANY:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("TRADING-COMPANY:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("TRADING-COMPANY:DETAIL"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("REPORT:LIST"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("REPORT:CREATE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("REPORT:EDIT"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("REPORT:DELETE"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("REPORT:DETAIL"), businessDto)));
    		add(new UserPermissionBusiness(new UserPermissionBusinessDto(UUID.randomUUID(), userDto, findPermissionByCode("AGENCY:DETAIL"), businessDto)));

    	}};
    	
    	for(UserPermissionBusiness userPermissionBusiness: permissionsUsers) {
    		saveUserPermissionBusinessIfNotExists(userPermissionBusiness);
    	}
    	
    	System.out.println("Seeder: Fin creacion de UserPermissionBusiness.");
    }
    
    void saveUserPermissionBusinessIfNotExists(UserPermissionBusiness entity) {
    	if(userPermissionBusinessReadDataJPARepository.countByUserAndBusinessAndPermission(entity.getUser().getId(), entity.getBusiness().getId(), entity.getPermission().getId()) == 0) {
    		userPermissionBusinessWriteDataJPARepository.save(entity);
    	}
    }
    
    void saveModuleSystemIfNotExists(ModuleSystem entity) {
    	if(moduleQuery.countByCodeAndNotId(entity.getCode(), entity.getId()) == 0) {
    		moduleWriteRepository.save(entity);
    	}
    	
    }
    
    void saveBusinessModuleIfNotExists(BusinessModule entity) {
    	if(businessModuleReadDataJPARepository.countByBussinessIdAndModuleId(entity.getBusiness().getId(), entity.getModule().getId()) == 0) {
    		businessModuleWriteRepository.save(entity);
    	}
    }
    
    void savePermissionIfNotExists(Permission entity) {
    	if(permissionReadDataJPARepository.countByCodeAndNotId(entity.getCode(), entity.getId()) == 0) {
    		permissionWriteDataJPARepository.save(entity);
    	}
    }
    
    ModuleDto findModuleSystemDtoByCode(String code){
    	ModuleSystem moduleSystem = moduleQuery.findByCode(code);
    	return new ModuleDto(moduleSystem.getId(), moduleSystem.getName(), moduleSystem.getImage(), moduleSystem.getDescription(), moduleSystem.getStatus(), moduleSystem.getCode());
    }
    
    PermissionDto findPermissionByCode(String code) {
    	return permissionReadDataJPARepository.findByCode(code).toAggregate();
    }
}
