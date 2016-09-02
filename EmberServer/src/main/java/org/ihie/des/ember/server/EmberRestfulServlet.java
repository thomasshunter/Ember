package org.ihie.des.ember.server;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.apache.commons.dbcp.BasicDataSource;
import org.ihie.des.ember.database.util.EmberBasicDataSource;
import org.ihie.des.ember.resource.provider.AllergyIntoleranceResourceProvider;
import org.ihie.des.ember.resource.provider.ObservationResourceProvider;
import org.ihie.des.ember.resource.provider.PatientResourceProvider;
import org.ihie.des.ember.services.allergy.dao.AllergyIntoleranceDAO;
import org.ihie.des.ember.services.allergy.service.AllergyIntoleranceService;
import org.ihie.des.ember.services.patient.dao.PatientServiceDAO;
import org.ihie.des.ember.services.patient.service.PatientServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.EncodingEnum;
import ca.uhn.fhir.rest.server.HardcodedServerAddressStrategy;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;

@Configuration
@ComponentScan("org.ihie.des")
@PropertySource("file:/etc/ember/ember.properties")
@SuppressWarnings("serial")
@WebServlet(urlPatterns ={ "/fhir/*" }, displayName = "FHIR Server")
public class EmberRestfulServlet extends RestfulServer
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
    
    private PatientResourceProvider patientResourceProvider;
    private ObservationResourceProvider observationResourceProvider;
    private AllergyIntoleranceResourceProvider allergyIntoleranceResourceProvider;
        
    public EmberRestfulServlet()
    {
        super(FhirContext.forDstu2()); // Support DSTU2
        String serverBaseUrl = "http://visage-test.ihie.org:8080/EmberServer";
        setServerAddressStrategy(new HardcodedServerAddressStrategy(serverBaseUrl));
    }
    
    /**
     * The initialize method is automatically called when the servlet is
     * starting up, so it can be used to configure the servlet to define
     * resource providers, or set up configuration, interceptors, etc.
     */
    @Override
    protected void initialize() throws ServletException
    {
        /*
         * The servlet defines any number of resource providers, and configures
         * itself to use them by calling setResourceProviders()
         */
        List<IResourceProvider> resourceProviders = new ArrayList<IResourceProvider>();
        resourceProviders.add( this.getPatientResourceProvider() );
        resourceProviders.add( this.getAllergyIntoleranceResourceProvider() );
        
        setDefaultPrettyPrint(true);
        setDefaultResponseEncoding(EncodingEnum.JSON);

        setResourceProviders(resourceProviders);
    }
    
    
    @Bean 
    public PatientResourceProvider getPatientResourceProvider()
    {
        this.patientResourceProvider = new PatientResourceProvider();
        this.patientResourceProvider.setPatientServiceService( this.getPatientServiceService() );
        this.patientResourceProvider.setAllergyIntoleranceService( this.getAllergyIntoleranceService() );
        
        return this.patientResourceProvider;
    }

    @Bean
    public AllergyIntoleranceResourceProvider getAllergyIntoleranceResourceProvider()
    {
        this.allergyIntoleranceResourceProvider = new AllergyIntoleranceResourceProvider();
        this.allergyIntoleranceResourceProvider.setAllergyIntoleranceService( this.getAllergyIntoleranceService() );
        
        return this.allergyIntoleranceResourceProvider;
    }
    
    
    @Bean
    public EmberBasicDataSource getEmberBasicDataSource()
    {
        
        
        BasicDataSource dataSource                  = new BasicDataSource();
        dataSource.setDriverClassName( this.driver );
        dataSource.setUrl( this.url );
        dataSource.setUsername( this.username );
        dataSource.setPassword( this.password );

        EmberBasicDataSource emberBasicDataSource   = new EmberBasicDataSource();
        emberBasicDataSource.setBasicDataSource( dataSource );
        
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
    public AllergyIntoleranceService getAllergyIntoleranceService()
    {
        AllergyIntoleranceService allergyIntoleranceService = new AllergyIntoleranceService();
        allergyIntoleranceService.setAllergyIntoleranceDAO( getAllergyIntoleranceDAO() );
        
        return allergyIntoleranceService;
    }

    @Bean
    public AllergyIntoleranceDAO getAllergyIntoleranceDAO()
    {
        AllergyIntoleranceDAO allergyIntoleranceDAO = new AllergyIntoleranceDAO();
        allergyIntoleranceDAO.setEmberBasicDataSource( this.getEmberBasicDataSource() );
        
        return allergyIntoleranceDAO;
    }
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
    {
       return new PropertySourcesPlaceholderConfigurer();
    }

    
    @Autowired
    public void setPatientResourceProvider(PatientResourceProvider patientResourceProvider)
    {
        this.patientResourceProvider = patientResourceProvider;
    }

    //@Autowired
    public void setObservationResourceProvider(ObservationResourceProvider observationResourceProvider)
    {
        this.observationResourceProvider = observationResourceProvider;
    }    
}
