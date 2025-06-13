package com.kynsoft.finamer.invoicing.infrastructure.repository.command.custom;

import com.kynsof.share.core.infrastructure.exceptions.InvoiceInsertException;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageRoomRate;
import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

@Repository
public class ManageRoomRateWriteCustomRepositoryImpl implements ManageRoomRateWriteCustomRepository{

    private final static Logger log = LoggerFactory.getLogger(ManageRoomRateWriteCustomRepositoryImpl.class.getName());
    @Autowired
    private DataSource dataSource;

    @Override
    public UUID insert(ManageRoomRate roomRate) {
        String sql = "{ call pr_insert_room_rate(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try(Connection connection = dataSource.getConnection();
            CallableStatement statement = connection.prepareCall(sql)){

            connection.setAutoCommit(false);
            statement.setObject(1, roomRate.getId());
            statement.setObject(2, roomRate.getAdults());
            statement.setObject(3, roomRate.getCheckIn());
            statement.setObject(4, roomRate.getCheckOut());
            statement.setObject(5, roomRate.getChildren());
            statement.setObject(6, roomRate.isDeleteInvoice());
            statement.setObject(7, roomRate.getDeleted());
            statement.setObject(8, roomRate.getDeletedAt());
            statement.setObject(9, roomRate.getHotelAmount());
            statement.setObject(10, roomRate.getInvoiceAmount());
            statement.setObject(11, roomRate.getNights());
            statement.setObject(12, roomRate.getRateAdult());
            statement.setObject(13, roomRate.getRateChild());
            statement.setObject(14, roomRate.getRemark());
            statement.setObject(15, roomRate.getRoomNumber());
            statement.setObject(16, roomRate.getUpdatedAt());
            statement.setObject(17, roomRate.getBooking() != null ? roomRate.getBooking().getId() : null);

            statement.registerOutParameter(1, Types.OTHER);
            statement.registerOutParameter(18, Types.INTEGER);

            statement.execute();

            UUID roomRateId = (UUID) statement.getObject(1);
            int roomRateGenId = statement.getInt(18);

            roomRate.setRoomRateId((long) roomRateGenId);

            connection.commit();
        } catch (PersistenceException e) {
            log.error("Error inserting invoice into database", e);
            throw new InvoiceInsertException("Error inserting invoice into database", e);
        } catch (SQLException sqlException){
            log.error("Error inserting roomrate via JDBC", sqlException);
            throw new InvoiceInsertException("Error inserting invoice into database via JDBC", sqlException);
        }
        return roomRate.getId();
    }
}
