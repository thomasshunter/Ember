package org.ihie.des.ember.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.client.IRestfulClientFactory;
import ca.uhn.fhir.rest.server.EncodingEnum;

// %3A = :
public class EmberTestClient
{
    private static final Log LOG    = LogFactory.getLog(EmberTestClient.class);

    public static void main(String[] args)
    {
        String serverBase                           = "http://localhost:8080/EmberServer/fhir";
        //String serverBase                           = "https://visage-test.ihie.org:8443/EmberServer/fhir";
        FhirContext ctx                             = FhirContext.forDstu2();
               
        IRestfulClientFactory restfulClientFactory  = ctx.getRestfulClientFactory();
        restfulClientFactory.setConnectTimeout(IRestfulClientFactory.DEFAULT_CONNECT_TIMEOUT * 3);
        restfulClientFactory.setSocketTimeout(IRestfulClientFactory.DEFAULT_SOCKET_TIMEOUT * 18);
        
        IGenericClient client                       = ctx.newRestfulGenericClient( serverBase  );
        client.setEncoding( EncodingEnum.JSON );
        client.setPrettyPrint( true );

        //Bundle bundleOfAllOrganizations             = client.search().forResource(Organization.class).execute();
        
        //LOG.info( "All Organizations: " + bundleOfAllOrganizations.toListOfResources() );
        
        // Equivalent to: http://localhost:8080/EmberServer/fhir/Patient/1+6891303
        Patient patient                             = client.read( Patient.class, "1+6891303");
        
        
        LOG.info( patient );
    }

}
