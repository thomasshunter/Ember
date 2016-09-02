package ca.uhn.example.config;

import org.ihie.des.ember.database.util.EmberBasicDataSource;
import org.ihie.des.ember.services.patient.dao.PatientServiceDAO;
import org.ihie.des.ember.services.patient.service.PatientServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.to.FhirTesterMvcConfig;
import ca.uhn.fhir.to.TesterConfig;

//@formatter:off
/**
 * This spring config file configures the web testing module. It serves two
 * purposes: 1. It imports FhirTesterMvcConfig, which is the spring config for
 * the tester itself 2. It tells the tester which server(s) to talk to, via the
 * testerConfig() method below
 */

@Configuration
@ComponentScan("org.ihie.des")
@PropertySource("file:/etc/ember/ember.properties")
@Import(FhirTesterMvcConfig.class)
public class FhirTesterConfig
{
    @Value("${db.driver.oracle}")
    private String driver               = "oracle.jdbc.driver.OracleDriver";
    @Value("${db.oracle.url}")
    private String url                  = "jdbc:oracle:thin:@//oracle-test-ot.ihie.org:1521/EMBR_HIET_SRVC";
    @Value("${db.oracle.user}")
    private String username             = "EMBER_APP_USER";
    @Value("${db.oracle.password}")
    private String password             = "A9Pscvfjw=G^UMce";

    @Autowired
    private Environment env;
    

    /**
     * This bean tells the testing webpage which servers it should configure
     * itself to communicate with. In this example we configure it to talk to
     * the local server, as well as one public server. If you are creating a
     * project to deploy somewhere else, you might choose to only put your own
     * server's address here.
     * 
     * Note the use of the ${serverBase} variable below. This will be replaced
     * with the base URL as reported by the server itself. Often for a simple
     * Tomcat (or other container) installation, this will end up being
     * something like "http://localhost:8080/hapi-fhir-jpaserver-example". If
     * you are deploying your server to a place with a fully qualified domain
     * name, you might want to use that instead of using the variable.
     */
    @Bean
    public TesterConfig testerConfig()
    {
        TesterConfig retVal = new TesterConfig();
        
        retVal.addServer()
                .withId("home")
                .withFhirVersion(FhirVersionEnum.DSTU2)
                .withBaseUrl("${serverBase}/fhir")
                .withName("Local Tester")
              .addServer()
                  .withId("hapi")
                  .withFhirVersion(FhirVersionEnum.DSTU2)
                  .withBaseUrl("http://fhirtest.uhn.ca/baseDstu2")
                  .withName("Public HAPI Test Server");

        /*
         * Use the method below to supply a client "factory" which can be used
         * if your server requires authentication
         */
        // retVal.setClientFactory(clientFactory);

        return retVal;
    }

    
    @Bean
    public EmberBasicDataSource getEmberBasicDataSource()
    {        
        System.setProperty( "db.driver.oracle", this.driver );
        System.setProperty( "db.oracle.url", this.url );
        System.setProperty( "db.oracle.user", this.username );
        System.setProperty( "db.oracle.password", this.password );
        
        EmberBasicDataSource emberBasicDataSource = new EmberBasicDataSource();
        emberBasicDataSource.setDriverClassName( this.driver );
        emberBasicDataSource.setUrl( this.url );
        emberBasicDataSource.setUsername( this.username );
        emberBasicDataSource.setPassword( this.password );

        return emberBasicDataSource;
    }

    @Bean
    public PatientServiceDAO getPatientServiceDAO()
    {
        PatientServiceDAO patientServiceDAO = new PatientServiceDAO();
        patientServiceDAO.setEmberBasicDataSource( this.getEmberBasicDataSource() );
        
        return patientServiceDAO;
    }
    
    @Bean
    public PatientServiceService getPatientServiceService()
    {
        PatientServiceService patientServiceService = new PatientServiceService();
        patientServiceService.setPatientServiceDAO( this.getPatientServiceDAO() );
        
        return patientServiceService;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
    {
       return new PropertySourcesPlaceholderConfigurer();
    }

}
// @formatter:on
