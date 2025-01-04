package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.payment.domain.dto.TestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "test")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "test",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class Test implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;
    @Column(unique = true)
    private String userName;

    @Column(nullable = true)
    private Boolean deleted = false;

    public Test(TestDto dto) {
        this.id = dto.getId();
        this.userName = dto.getUserName();
    }

    public TestDto toAggregate() {
        return new TestDto(id, userName);
    }

}
