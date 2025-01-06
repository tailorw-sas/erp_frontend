package com.kynsoft.finamer.insis.infrastructure.model;

import com.kynsoft.finamer.insis.domain.dto.ManageEmployeeDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_employee")
public class ManageEmployee {

    @Id
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    public LocalDateTime updatedAt;

    public ManageEmployee(ManageEmployeeDto dto){
        this.id = dto.getId();
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.email = dto.getEmail();
        this.updatedAt = dto.getUpdatedAt();
    }

    public ManageEmployeeDto toAggregate(){
        return new ManageEmployeeDto(
                id,
                firstName,
                lastName,
                email,
                updatedAt
        );
    }
}
