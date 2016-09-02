package org.ihie.des.ember.services.institution.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.ihie.des.ember.database.util.EmberBasicDataSource;
import org.ihie.des.ember.services.institution.bean.InstitutionIdentifierBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.model.dstu2.resource.Organization;

@Component
public class InstitutionIdenfitierDAO
{
    protected static Logger LOG = Logger.getLogger(InstitutionIdenfitierDAO.class);
    
    private EmberBasicDataSource emberBasicDataSource;

    
    public ca.uhn.fhir.model.dstu2.resource.Organization getInstitutionIdentifierByCodeSystemDAO( int institutionIdParm, String institutionNameParm )
    {
        Connection con          = null;
        PreparedStatement ps    = null;
        ResultSet rs            = null;
        String query            = InstitutionIdentifierBean.getInstitutionIdentifierByCodeSystemBean( institutionNameParm );
        Organization org        = new Organization();
        
        try
        {
            con = this.getEmberBasicDataSource().getConnection();
            ps  = con.prepareStatement( query );
            rs  = ps.executeQuery();
            
            while( rs.next() )
            {
                String institutionName  = rs.getString( 1 );
                int institutionId       = rs.getInt(    2 );
                
                org.setId("Organization/" + institutionId );
                org.setName( institutionName );
                
                break;
            }
        }
        catch( Exception e )
        {
            InstitutionIdenfitierDAO.LOG.error( "InstitutionIdenfitierDAO.getInstitutionIdentifierByCodeSystemDAO() threw an Exception while attempting to get the institution for institutionIdParm=" + institutionIdParm + ", and institutionNameParm=" + institutionNameParm );
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
                InstitutionIdenfitierDAO.LOG.error( "InstitutionIdenfitierDAO.getInstitutionIdentifierByCodeSystemDAO() threw an Exception while attempting to close the RS, eRS=" + eRS );                
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
                InstitutionIdenfitierDAO.LOG.error( "InstitutionIdenfitierDAO.getInstitutionIdentifierByCodeSystemDAO() threw an Exception while attempting to close the PS, ePS=" + ePS );                                
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
                InstitutionIdenfitierDAO.LOG.error( "InstitutionIdenfitierDAO.getInstitutionIdentifierByCodeSystemDAO() threw an Exception while attempting to close the CON, eCON=" + eCON );                
            }
        }
        
        return org;
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
