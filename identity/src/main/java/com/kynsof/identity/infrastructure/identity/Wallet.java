package com.kynsof.identity.infrastructure.identity;

import com.kynsof.identity.domain.dto.WalletDto;
import com.kynsof.identity.domain.dto.WalletTransactionDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private BigDecimal balance;

    @OneToOne()
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "wallet",fetch = FetchType.EAGER)
    private Set<WalletTransaction> transactions = new HashSet<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void addTransaction(WalletTransaction transaction) {
        transactions.add(transaction);
        transaction.setWallet(this);
    }

    public void removeTransaction(WalletTransaction transaction) {
        transactions.remove(transaction);
        transaction.setWallet(null);
    }

    public void credit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public void debit(BigDecimal amount) {
        balance = balance.subtract(amount);
    }

    public Wallet(WalletDto dto) {
        this.id = dto.getId();
        this.balance = dto.getBalance();
        this.customer = new Customer(dto.getCustomer());
     //   this.transactions = dto.getTransactions().stream().map(WalletTransaction::new).collect(Collectors.toSet());
    }
    public WalletDto toAggregate() {
        List<WalletTransactionDto> walletTransactions = transactions != null ?
                transactions.stream().map(WalletTransaction::toAggregate).collect(Collectors.toList()) : new ArrayList<>();
        return new WalletDto(id,balance,customer.toAggregate(),walletTransactions );
    }
}