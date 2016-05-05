package org.ihie.des.ember.services.patient.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.ihie.des.ember.database.util.OracleUtil;
import org.ihie.des.ember.services.patient.bean.PatientServiceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.model.dstu2.resource.Patient;

@Component
public class PatientServiceDAO
{
    private OracleUtil oracleUtil;

    public ca.uhn.fhir.model.dstu2.resource.Patient getPatientByGivenAndFamilyNamesDAO( String givenName, String familyName )
    {
        Patient patient         = new Patient();
        Connection con          = null;
        PreparedStatement ps    = null;
        ResultSet rs            = null;
        String query            = PatientServiceBean.getPatientByGivenAndFamilyNamesQuery();
        
        try
        {
            con = this.oracleUtil.getOracleDatabaseConnection();
            ps  = con.prepareStatement( query );
            rs  = ps.executeQuery();
            
            
        }
        catch( Exception e )
        {
            
        }
        finally
        {
            
        }

        return patient;
    }
    
    
    @Autowired
    public void setOracleUtil(OracleUtil oracleUtil)
    {
        this.oracleUtil = oracleUtil;
    }
    
}
