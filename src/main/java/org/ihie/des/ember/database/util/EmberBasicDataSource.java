package org.ihie.des.ember.database.util;

import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

@Component
@PropertySource("file:/etc/ember/ember.properties")
public class EmberBasicDataSource extends BasicDataSource
{
    private BasicDataSource basicDataSource;

    @Value("${db.driver.oracle}")
    private String driver;
    @Value("${db.oracle.url}")
    private String url;
    @Value("${db.oracle.user}")
    private String username;
    @Value("${db.oracle.password}")
    private String password;

    @Bean
    public BasicDataSource getBasicDataSource()
    {
        basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName( driver );
        basicDataSource.setUrl( this.url );
        basicDataSource.setUsername( this.username );
        basicDataSource.setPassword( this.password );

        return basicDataSource;
    }

    public java.sql.Connection getConnection() throws SQLException
    {
        return this.basicDataSource.getConnection();

    }


    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
    {
       return new PropertySourcesPlaceholderConfigurer();
    }

}
