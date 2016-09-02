package org.ihie.des.ember.database.util;

import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmberBasicDataSource extends BasicDataSource
{
    private BasicDataSource basicDataSource;


    public java.sql.Connection getConnection() throws SQLException
    {
        return this.basicDataSource.getConnection();
    }


    @Autowired
    public void setBasicDataSource(BasicDataSource basicDataSource)
    {
        this.basicDataSource = basicDataSource;
    }



}
