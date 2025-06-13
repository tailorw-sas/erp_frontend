package com.kynsoft.finamer.invoicing.infrastructure.repository.command.custom;

import com.kynsof.share.core.infrastructure.exceptions.InvoiceInsertException;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAdjustment;
import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.UUID;

@Repository
public class ManageAdjustmentWriteCustomRepositoryImpl implements ManageAdjustmentWriteCustomRepository{

    private static final Logger log = LoggerFactory.getLogger(ManageAdjustmentWriteCustomRepositoryImpl.class.getName());

    @Autowired
    private DataSource dataSource;

    @Override
    public UUID insert(ManageAdjustment adjustment) {
        String sql = "{ call pr_insert_adjustment(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try(Connection connection = dataSource.getConnection();
            CallableStatement statement = connection.prepareCall(sql)){
            statement.setObject(1, adjustment.getId());
            statement.setObject(2, adjustment.getAmount());
            statement.setObject(3, adjustment.getDate());
            statement.setObject(4, adjustment.isDeleteInvoice());
            statement.setObject(5, adjustment.getDeleted());
            statement.setObject(6, adjustment.getDeletedAt());
            statement.setObject(7, adjustment.getDescription());
            statement.setObject(8, adjustment.getEmployee());
            statement.setObject(9, adjustment.getUpdatedAt());
            statement.setObject(10, adjustment.getPaymentTransactionType() != null ? adjustment.getPaymentTransactionType().getId() : null);
            statement.setObject(11, adjustment.getRoomRate() != null ? adjustment.getRoomRate().getId() : null);
            statement.setObject(12, adjustment.getTransaction() != null ? adjustment.getTransaction().getId() : null);

            statement.registerOutParameter(1, Types.OTHER);
            statement.registerOutParameter(13, Types.INTEGER);

            statement.execute();

            UUID adjustmentId = (UUID)statement.getObject(1);
            Integer adjustmentGenId = statement.getInt(13);

            adjustment.setId(adjustmentId);
            adjustment.setAdjustmentId(adjustmentGenId.longValue());
        } catch (PersistenceException e) {
            log.error("Error inserting invoice into database", e);
            throw new InvoiceInsertException("Error inserting invoice into database", e);
        } catch (SQLException sqlException){
            log.error("Error inserting roomrate via JDBC", sqlException);
            throw new InvoiceInsertException("Error inserting invoice into database via JDBC", sqlException);
        }
        return adjustment.getId();
    }
}
