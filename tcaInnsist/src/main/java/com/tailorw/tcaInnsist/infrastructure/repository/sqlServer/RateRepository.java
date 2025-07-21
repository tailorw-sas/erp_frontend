package com.tailorw.tcaInnsist.infrastructure.repository.sqlServer;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.tailorw.tcaInnsist.infrastructure.model.Rate;
import com.tailorw.tcaInnsist.infrastructure.model.ManageConnection;
import com.tailorw.tcaInnsist.infrastructure.repository.SQLServerDBConfiguration;
import com.tailorw.tcaInnsist.infrastructure.repository.interfaces.IRateRepository;
import com.tailorw.tcaInnsist.infrastructure.utils.search.SearchUtil;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class RateRepository extends SQLServerDBConfiguration implements IRateRepository {

    @Override
    public List<Rate> findAllByBookingId(ManageConnection tcaConfigurationProperties, String reservationNumber, String cuponNumber, String checkInDate, String checkOutDate){
        List<Rate> rates = new ArrayList<>();

        try{
            String query = "SELECT tbd1_0.ibuff, tbd1_0.r_pers_adu, tbd1_0.r_pers_men, TRIM(tbd1_0.r_cod_age) r_cod_age, tbd1_0.r_importe, tbd1_0.r_fec_ini, " +
                    "   tbd1_0.r_fec_fin, TRIM(tbd1_0.r_cupon_fac) r_cupon_fac, tbd1_0.r_nom, tbd1_0.r_fecha_creacion, " +
                    "   tbd1_0.r_monto, tbd1_0.r_fec_fac, tbd1_0.r_tfa_adulto, tbd1_0.r_tfa_menor, " +
                    "   TRIM(tbd1_0.r_tpo_plan) r_tpo_plan, tbd1_0.r_observaciones, tbd1_0.r_res_cve, tbd1_0.r_num_hab, " +
                    "   TRIM(tbd1_0.r_tpo_hab) r_tpo_hab, tbd1_0.r_estancia, tbd1_0.r_pers, " +
                    "   tdb2_0.d_factura, tdb2_0.d_folio, tdb2_0.d_Cotizacion, " +
                    "   tbd1_0.r_num_ren, tdb2_0.d_tpo_hab, " +
                    "   HASHBYTES('SHA2_256', " +
                    "       CONCAT_WS('|', " +
                    "           tbd1_0.ibuff, tbd1_0.r_pers_adu, tbd1_0.r_pers_men, tbd1_0.r_cod_age, tbd1_0.r_importe, tbd1_0.r_fec_ini, " +
                    "           tbd1_0.r_fec_fin, tbd1_0.r_cupon_fac, tbd1_0.r_nom, tbd1_0.r_fecha_creacion, " +
                    "           tbd1_0.r_monto, tbd1_0.r_fec_fac, tbd1_0.r_tfa_adulto, tbd1_0.r_tfa_menor, " +
                    "           tbd1_0.r_tpo_plan, tbd1_0.r_observaciones, tbd1_0.r_res_cve, tbd1_0.r_num_hab, " +
                    "           tbd1_0.r_tpo_hab, tbd1_0.r_estancia, tbd1_0.r_pers, " +
                    "           tdb2_0.d_factura, tdb2_0.d_folio, tdb2_0.d_Cotizacion " +
                    "       )" +
                    "   ) AS RegistroHash " +
                    " FROM hottfr tbd1_0 " +
                    " JOIN hotdfc tdb2_0 ON tbd1_0.r_num_fac = tdb2_0.d_factura and tdb2_0.d_fecha_reg = tbd1_0.r_fec_fac and tbd1_0.r_cod_age = tdb2_0.d_cod_age " +
                    " WHERE tbd1_0.r_fec_ini = ? " +
                    "  AND tbd1_0.r_fec_fin = ? " +
                    "  AND tbd1_0.r_res_cve = ? " +
                    "  AND tbd1_0.r_cupon_fac = ? ";

            Connection connection = createConnection(getUrl(tcaConfigurationProperties.getServer(), tcaConfigurationProperties.getPort(), tcaConfigurationProperties.getDbName()), tcaConfigurationProperties.getUserName(), tcaConfigurationProperties.getPassword());
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            int index = 1;
            preparedStatement.setString(index++, checkInDate);
            preparedStatement.setString(index++, checkOutDate);
            preparedStatement.setString(index++, reservationNumber);
            preparedStatement.setString(index, cuponNumber);

            ResultSet resultSet = preparedStatement.executeQuery();

            rates = convertResultSetToRates(resultSet);
        } catch (Exception ex) {
            Logger.getLogger(RateRepository.class.getName()).log(Level.WARNING, null, ex);
        }

        return rates;
    }

    @Override
    public List<Rate> findAllByCriteria(ManageConnection tcaConfigurationProperties, List<FilterCriteria> filterCriteria, String roomType){
        List<Rate> rates = new ArrayList<>();

        try{
            String checkIn = SearchUtil.getValueByKey(filterCriteria, "checkInDate", String.class);
            String checkOut = SearchUtil.getValueByKey(filterCriteria, "checkOutDate", String.class);
            Object agenciesFilter = SearchUtil.getValueByKey(filterCriteria, "agencyCode", Object.class);
            String[] agencies = SearchUtil.convertToArray(agenciesFilter, String.class);

            String query = "SELECT tbd1_0.ibuff, tbd1_0.r_pers_adu, tbd1_0.r_pers_men, TRIM(tbd1_0.r_cod_age) r_cod_age, tbd1_0.r_importe, tbd1_0.r_fec_ini, " +
                    "   tbd1_0.r_fec_fin, TRIM(tbd1_0.r_cupon_fac) r_cupon_fac, tbd1_0.r_nom, tbd1_0.r_fecha_creacion, " +
                    "   tbd1_0.r_monto, tbd1_0.r_fec_fac, tbd1_0.r_tfa_adulto, tbd1_0.r_tfa_menor, " +
                    "   TRIM(tbd1_0.r_tpo_plan) r_tpo_plan, tbd1_0.r_observaciones, tbd1_0.r_res_cve, tbd1_0.r_num_hab, " +
                    "   TRIM(tbd1_0.r_tpo_hab) r_tpo_hab, tbd1_0.r_estancia, tbd1_0.r_pers, " +
                    "   tdb2_0.d_factura, tdb2_0.d_folio, tdb2_0.d_Cotizacion, " +
                    "   tbd1_0.r_num_ren, tdb2_0.d_tpo_hab, " +
                    "   HASHBYTES('SHA2_256', " +
                    "       CONCAT_WS('|', " +
                    "           tbd1_0.ibuff, tbd1_0.r_pers_adu, tbd1_0.r_pers_men, tbd1_0.r_cod_age, tbd1_0.r_importe, tbd1_0.r_fec_ini, " +
                    "           tbd1_0.r_fec_fin, tbd1_0.r_cupon_fac, tbd1_0.r_nom, tbd1_0.r_fecha_creacion, " +
                    "           tbd1_0.r_monto, tbd1_0.r_fec_fac, tbd1_0.r_tfa_adulto, tbd1_0.r_tfa_menor, " +
                    "           tbd1_0.r_tpo_plan, tbd1_0.r_observaciones, tbd1_0.r_res_cve, tbd1_0.r_num_hab, " +
                    "           tbd1_0.r_tpo_hab, tbd1_0.r_estancia, tbd1_0.r_pers, " +
                    "           tdb2_0.d_factura, tdb2_0.d_folio, tdb2_0.d_Cotizacion " +
                    "       )" +
                    "   ) AS RegistroHash " +
                    " FROM hottfr tbd1_0 " +
                    " JOIN hotdfc tdb2_0 ON tbd1_0.r_num_fac = tdb2_0.d_factura and tdb2_0.d_fecha_reg = tbd1_0.r_fec_fac and tbd1_0.r_cod_age = tdb2_0.d_cod_age " +
                    " WHERE lower(cast(tbd1_0.r_fec_ini AS varchar(max))) = COALESCE(lower(?), lower(cast(tbd1_0.r_fec_ini AS varchar(max)))) " +
                    "  AND lower(cast(tbd1_0.r_fec_fin AS varchar(max))) = COALESCE(lower(?), lower(cast(tbd1_0.r_fec_fin AS varchar(max)))) " +
                    "  AND lower(cast(tbd1_0.r_tpo_hab AS varchar(max))) LIKE COALESCE(lower(?), lower(cast(tbd1_0.r_tpo_hab AS varchar(max)))) ";
            if(agencies!=null && agencies.length > 0){
                String placeholders = String.join(",", Collections.nCopies(agencies.length, "?"));
                query += " AND lower(cast(tbd1_0.r_cod_age AS varchar(max))) IN (" + placeholders + ")";
            }

            Connection connection = createConnection(getUrl(tcaConfigurationProperties.getServer(), tcaConfigurationProperties.getPort(), tcaConfigurationProperties.getDbName()), tcaConfigurationProperties.getUserName(), tcaConfigurationProperties.getPassword());
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            int index = 1;
            preparedStatement.setString(index++, checkIn);
            preparedStatement.setString(index++, checkOut);
            preparedStatement.setString(index++, (roomType != null) ? roomType + "%" : null);

            if(agencies != null){
                for(String agency : agencies){
                    preparedStatement.setString(index++, agency.toUpperCase());
                }
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            rates = convertResultSetToRates(resultSet);
        } catch (Exception ex) {
            Logger.getLogger(RateRepository.class.getName()).log(Level.WARNING, null, ex);
        }

        return rates;
    }

    @Override
    public List<Rate> findByInvoiceCreatedAt(ManageConnection tcaConfigurationProperties, LocalDate invoiceDate, String roomType){
        List<Rate> rates = new ArrayList<>();

        try{
            String query = "SELECT tbd1_0.ibuff, tbd1_0.r_pers_adu, tbd1_0.r_pers_men, TRIM(tbd1_0.r_cod_age) r_cod_age, tbd1_0.r_importe, tbd1_0.r_fec_ini, " +
                    "   tbd1_0.r_fec_fin, TRIM(tbd1_0.r_cupon_fac) r_cupon_fac, tbd1_0.r_nom, tbd1_0.r_fecha_creacion, " +
                    "   tbd1_0.r_monto, tbd1_0.r_fec_fac, tbd1_0.r_tfa_adulto, tbd1_0.r_tfa_menor, " +
                    "   TRIM(tbd1_0.r_tpo_plan) r_tpo_plan, tbd1_0.r_observaciones, tbd1_0.r_res_cve, tbd1_0.r_num_hab, " +
                    "   TRIM(tbd1_0.r_tpo_hab) r_tpo_hab, tbd1_0.r_estancia, tbd1_0.r_pers, " +
                    "   tdb2_0.d_factura, tdb2_0.d_folio, tdb2_0.d_Cotizacion, " +
                    "   tbd1_0.r_num_ren, tdb2_0.d_tpo_hab, " +
                    "   HASHBYTES('SHA2_256', " +
                    "       CONCAT_WS('|', " +
                    "           tbd1_0.ibuff, tbd1_0.r_pers_adu, tbd1_0.r_pers_men, tbd1_0.r_cod_age, tbd1_0.r_importe, tbd1_0.r_fec_ini, " +
                    "           tbd1_0.r_fec_fin, tbd1_0.r_cupon_fac, tbd1_0.r_nom, tbd1_0.r_fecha_creacion, " +
                    "           tbd1_0.r_monto, tbd1_0.r_fec_fac, tbd1_0.r_tfa_adulto, tbd1_0.r_tfa_menor, " +
                    "           tbd1_0.r_tpo_plan, tbd1_0.r_observaciones, tbd1_0.r_res_cve, tbd1_0.r_num_hab, " +
                    "           tbd1_0.r_tpo_hab, tbd1_0.r_estancia, tbd1_0.r_pers, " +
                    "           tdb2_0.d_factura, tdb2_0.d_folio, tdb2_0.d_Cotizacion " +
                    "       )" +
                    "   ) AS RegistroHash " +
                    " FROM hottfr tbd1_0 " +
                    " JOIN hotdfc tdb2_0 ON tbd1_0.r_num_fac = tdb2_0.d_factura and tdb2_0.d_fecha_reg = tbd1_0.r_fec_fac and tbd1_0.r_cod_age = tdb2_0.d_cod_age " +
                    " WHERE tbd1_0.r_fec_fac = ? ";
            if(roomType != null){
                query += " AND tbd1_0.r_tpo_hab LIKE ? ";
            }

            Connection connection = createConnection(getUrl(tcaConfigurationProperties.getServer(), tcaConfigurationProperties.getPort(), tcaConfigurationProperties.getDbName()), tcaConfigurationProperties.getUserName(), tcaConfigurationProperties.getPassword());
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            int index = 1;
            preparedStatement.setString(index++, invoiceDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            if(roomType != null){
                preparedStatement.setString(index, roomType + "%");
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            rates = convertResultSetToRates(resultSet);
        } catch (Exception ex) {
            Logger.getLogger(RateRepository.class.getName()).log(Level.WARNING, null, ex);
            throw new RuntimeException(ex.getMessage());
        }

        return rates;
    }

    @Override
    public List<Rate> findBetweenInvoiceDates(ManageConnection tcaConfigurationProperties, LocalDate invoiceDateStart, LocalDate invoiceDateEnd, String roomType) {
        List<Rate> rates = new ArrayList<>();

        try{
            String query = "SELECT tbd1_0.ibuff, tbd1_0.r_pers_adu, tbd1_0.r_pers_men, TRIM(tbd1_0.r_cod_age) r_cod_age, tbd1_0.r_importe, tbd1_0.r_fec_ini, " +
                    "   tbd1_0.r_fec_fin, TRIM(tbd1_0.r_cupon_fac) r_cupon_fac, tbd1_0.r_nom, tbd1_0.r_fecha_creacion, " +
                    "   tbd1_0.r_monto, tbd1_0.r_fec_fac, tbd1_0.r_tfa_adulto, tbd1_0.r_tfa_menor, " +
                    "   TRIM(tbd1_0.r_tpo_plan) r_tpo_plan, tbd1_0.r_observaciones, tbd1_0.r_res_cve, tbd1_0.r_num_hab, " +
                    "   TRIM(tbd1_0.r_tpo_hab) r_tpo_hab, tbd1_0.r_estancia, tbd1_0.r_pers, " +
                    "   tdb2_0.d_factura, tdb2_0.d_folio, tdb2_0.d_Cotizacion, " +
                    "   tbd1_0.r_num_ren, tdb2_0.d_tpo_hab, " +
                    "   HASHBYTES('SHA2_256', " +
                    "       CONCAT_WS('|', " +
                    "           tbd1_0.ibuff, tbd1_0.r_pers_adu, tbd1_0.r_pers_men, tbd1_0.r_cod_age, tbd1_0.r_importe, tbd1_0.r_fec_ini, " +
                    "           tbd1_0.r_fec_fin, tbd1_0.r_cupon_fac, tbd1_0.r_nom, tbd1_0.r_fecha_creacion, " +
                    "           tbd1_0.r_monto, tbd1_0.r_fec_fac, tbd1_0.r_tfa_adulto, tbd1_0.r_tfa_menor, " +
                    "           tbd1_0.r_tpo_plan, tbd1_0.r_observaciones, tbd1_0.r_res_cve, tbd1_0.r_num_hab, " +
                    "           tbd1_0.r_tpo_hab, tbd1_0.r_estancia, tbd1_0.r_pers, " +
                    "           tdb2_0.d_factura, tdb2_0.d_folio, tdb2_0.d_Cotizacion " +
                    "       )" +
                    "   ) AS RegistroHash " +
                    " FROM hottfr tbd1_0 " +
                    " JOIN hotdfc tdb2_0 ON tbd1_0.r_num_fac = tdb2_0.d_factura and tdb2_0.d_fecha_reg = tbd1_0.r_fec_fac and tbd1_0.r_cod_age = tdb2_0.d_cod_age " +
                    //" WHERE CONVERT(DATE, tbd1_0.r_fec_fac, 112) BETWEEN ? AND ? ";
                    " WHERE tbd1_0.r_fec_fac >= ? AND tbd1_0.r_fec_fac < ? ";
            if(roomType != null){
                query += " AND tbd1_0.r_tpo_hab LIKE ? ";
            }

            Connection connection = createConnection(getUrl(tcaConfigurationProperties.getServer(), tcaConfigurationProperties.getPort(), tcaConfigurationProperties.getDbName()), tcaConfigurationProperties.getUserName(), tcaConfigurationProperties.getPassword());
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            int index = 1;
            preparedStatement.setString(index++, invoiceDateStart.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            preparedStatement.setString(index++, invoiceDateEnd.plusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            if(roomType != null){
                preparedStatement.setString(index, roomType + "%");
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            rates = convertResultSetToRates(resultSet);
        } catch (Exception ex) {
            Logger.getLogger(RateRepository.class.getName()).log(Level.WARNING, null, ex);
        }

        return rates;
    }

    private static List<Rate> convertResultSetToRates(ResultSet resultSet) throws SQLException {
        List<Rate> rates = new ArrayList<>();
        while (resultSet.next()){
            Rate rate = new Rate(resultSet.getString("r_cod_age"),
                    resultSet.getString("r_fec_ini"),
                    resultSet.getString("r_fec_fin"),
                    resultSet.getInt("r_estancia"),
                    resultSet.getString("r_res_cve"),
                    resultSet.getString("r_nom"),
                    resultSet.getDouble("r_importe"),
                    resultSet.getString("r_tpo_hab"),
                    resultSet.getString("r_cupon_fac"),
                    resultSet.getInt("r_pers"),
                    resultSet.getInt("r_pers_adu"),
                    resultSet.getInt("r_pers_men"),
                    resultSet.getString("r_tpo_plan"),
                    resultSet.getString("r_fec_fac"),
                    resultSet.getString("r_fecha_creacion"),
                    resultSet.getDouble("r_importe"),
                    resultSet.getDouble("r_importe"),
                    resultSet.getDouble("r_tfa_adulto"),
                    resultSet.getDouble("r_tfa_menor"),
                    resultSet.getString("r_observaciones"),
                    resultSet.getString("r_num_hab"),
                    resultSet.getDouble("r_monto"),
                    resultSet.getString("d_factura"),
                    resultSet.getString("d_folio"),
                    resultSet.getDouble("d_Cotizacion"),
                    resultSet.getString("r_num_ren"),
                    resultSet.getString("d_tpo_hab"),
                    resultSet.getString("RegistroHash")
            );
            rates.add(rate);
        }
        return rates;
    }

}
