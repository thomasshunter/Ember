package org.ihie.des.ember.server;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.ihie.des.ember.resource.provider.PatientResourceProvider;
import org.ihie.des.ember.resource.provider.ObservationResourceProvider;

import ca.uhn.fhir.rest.server.HardcodedServerAddressStrategy;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;

@SuppressWarnings("serial")
@WebServlet(urlPatterns ={ "/fhir/*" }, displayName = "FHIR Server")
public class EmberRestfulServlet extends RestfulServer
{    
    public EmberRestfulServlet()
    {
        String serverBaseUrl = "http://localhost:8080/Ember";
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
        resourceProviders.add( new PatientResourceProvider() );
        resourceProviders.add(new ObservationResourceProvider());
        
        setResourceProviders(resourceProviders);
    }

}
