package org.ihie.des.ember.client;

import org.hl7.fhir.instance.model.api.IBaseResource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.api.SummaryEnum;
import ca.uhn.fhir.rest.client.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpClient;
import ca.uhn.fhir.rest.client.api.IRestfulClient;
import ca.uhn.fhir.rest.server.EncodingEnum;

public class EmberTestClient implements IRestfulClient
{
    public static void main(String[] args)
    {
        FhirContext ctx = FhirContext.forDstu2();
        String serverBase = "http://localhost:8080/EmberServer/fhir/";

        // Create the client
        IRestfulClient client 	= ctx.newRestfulClient(IRestfulClient.class, serverBase);

        Patient resource		= client.fetchResourceFromUrl( Patient.class, serverBase );
        // Try the client out! This method will invoke the server
        //List<Patient> patients = client.getPatient(new StringDt("SMITH"));
        System.out.println( "wait" );
    }

    @Override
    public <T extends IBaseResource> T fetchResourceFromUrl(Class<T> theResourceType, String theUrl)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FhirContext getFhirContext()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IHttpClient getHttpClient()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getServerBase()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void registerInterceptor(IClientInterceptor theInterceptor)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setEncoding(EncodingEnum theEncoding)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setPrettyPrint(Boolean thePrettyPrint)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setSummary(SummaryEnum theSummary)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void unregisterInterceptor(IClientInterceptor theInterceptor)
    {
        // TODO Auto-generated method stub
        
    }
}
