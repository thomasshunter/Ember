package org.ihie.des.ember.services.allergy.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ihie.des.ember.database.util.EmberBasicDataSource;
import org.ihie.des.ember.services.allergy.bean.AllergyIntoleranceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;
import ca.uhn.fhir.model.dstu2.valueset.AllergyIntoleranceStatusEnum;

@Component
public class AllergyIntoleranceDAO
{
    protected static Logger LOG = Logger.getLogger(AllergyIntoleranceDAO.class);
    
    private EmberBasicDataSource emberBasicDataSource;

    public List<AllergyIntolerance> getAllergiesBasedOnPatientAndDateRangeDAO( Long patientId, Integer institutionId, java.sql.Date lowerSQL, java.sql.Date upperSQL )
    {
        List<AllergyIntolerance> allergies  = new ArrayList<AllergyIntolerance>();
        Connection con                      = null;
        PreparedStatement ps                = null;
        ResultSet rs                        = null;
        String query                        = AllergyIntoleranceBean.getAllergiesBasedOnPatientAndDateRangeQuery();
        long patId                          = 0;
        
        try
        {
            con = this.getEmberBasicDataSource().getConnection();
            ps  = con.prepareStatement( query );
            ps.setInt(  1, institutionId );
            ps.setLong( 2, patientId );
            ps.setInt(  3, institutionId );
            ps.setLong( 4, patientId );
            
            rs  = ps.executeQuery();
            
            while( rs.next() )
            {
                String serviceCode              = rs.getString( 1 );
                String serviceName              = rs.getString( 2 );
                String serviceOid               = rs.getString( 3 );
                String serviceCodeConcept       = rs.getString( 4 );
                String serviceGuid              = rs.getString( 5 );
                String allergyDescription       = rs.getString( 6 );
                Date effectiveTimeLow           = rs.getDate(   7 );
                Date effectiveTimeHigh          = rs.getDate(   8 );
                
                AllergyIntolerance anAllIntol   = new AllergyIntolerance();
                anAllIntol.setId( serviceCode );
                anAllIntol.getSubstance().setText( allergyDescription );
                anAllIntol.setStatus(AllergyIntoleranceStatusEnum.ACTIVE);
                IdentifierDt id                 = new IdentifierDt( "urn:system", serviceCode );
                anAllIntol.addIdentifier( id ); 
                
                allergies.add( anAllIntol );
            }
        }
        catch( Exception e )
        {
            AllergyIntoleranceDAO.LOG.error( "AllergyIntoleranceDAO.getAllergiesBasedOnPatientAndDateRangeDAO() threw an Exception while attempting to get allergies for a patient, patientId=" + patientId + ", e=" + e );
        }
        finally
        {
            try
            {
                if( rs != null )
                {
                    rs.close();
                }
            }
            catch( Exception eRS )
            {
                AllergyIntoleranceDAO.LOG.error( "AllergyIntoleranceDAO.getAllergiesBasedOnPatientAndDateRangeDAO() threw an Exception while attempting to close the RS, eRS=" + eRS );                
            }
        
            try
            {
                if( ps != null )
                {
                    ps.close();
                }
            }
            catch( Exception ePS )
            {
                AllergyIntoleranceDAO.LOG.error( "AllergyIntoleranceDAO.getAllergiesBasedOnPatientAndDateRangeDAO() threw an Exception while attempting to close the PS, ePS=" + ePS );                                
            }
        
            try
            {
                if( con != null && !con.isClosed() )
                {
                    con.close();
                }
            }
            catch( Exception eCON )
            {
                AllergyIntoleranceDAO.LOG.error( "AllergyIntoleranceDAO.getAllergiesBasedOnPatientAndDateRangeDAO() threw an Exception while attempting to close the CON, eCON=" + eCON );                
            }
        }    
       
        return allergies;
    }

    
    @Autowired
    public void setEmberBasicDataSource(EmberBasicDataSource emberBasicDataSource)
    {
        this.emberBasicDataSource = emberBasicDataSource;
    }
    public EmberBasicDataSource getEmberBasicDataSource()
    {
        return emberBasicDataSource;
    }

}
