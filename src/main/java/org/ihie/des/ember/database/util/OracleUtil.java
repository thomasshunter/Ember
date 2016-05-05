package org.ihie.des.ember.database.util;

import java.io.Serializable;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("serial")
@Component
public class OracleUtil implements Serializable
{
    private static final Log LOG            = LogFactory.getLog( OracleUtil.class );
    protected DateFormat dfYyyyMmDd         = new SimpleDateFormat( "yyyy-MM-dd" );
    @SuppressWarnings("unused") 
    private DateFormat df                   = new SimpleDateFormat("MM/dd/yyyy");
    private EmberBasicDataSource dataSourceOracle;
  
            
    public Connection getOracleDatabaseConnection()
    {
        Connection con = null;

        try
        {
            con = dataSourceOracle.getConnection();
        }
        catch (Exception e)
        {
            OracleUtil.LOG.error("OracleUtil.getOracleDatabaseConnection() threw an Exception while attempting to get the connection, e=" + e);
        }

        return con;
    }
    
    public java.sql.Date makeSqlDate( java.util.Date original )
    {
        long dateLong = original.getTime();
        
        return new java.sql.Date( dateLong );
    }

    public java.sql.Date makeSqlDate( String original )
    {
        java.sql.Date sqlDate = null;
        
        try
        {
            java.util.Date parsedUtilDate   = dfYyyyMmDd.parse( original );  
            sqlDate                         = new java.sql.Date( parsedUtilDate.getTime() );            
        }
        catch( Exception e )
        {
            OracleUtil.LOG.error( "OracleUtil.makeSqlDate() threw an Exception while attempting to convert original=" + original + " into a java.sql.Date, e=" + e );
        }

        return sqlDate;
    }

    
    public static java.sql.Date convertUtilDateToSQL( java.util.Date orig )
    {
        if( orig == null )
        {
            return null;
        }
        else
        {
            java.sql.Date date = new java.sql.Date( orig.getTime() );
            return date;
        }
    }
    
    @Autowired
    public void setDataSourceOracle(EmberBasicDataSource dataSourceOracle)
    {
        this.dataSourceOracle = dataSourceOracle;
    }
       
}
