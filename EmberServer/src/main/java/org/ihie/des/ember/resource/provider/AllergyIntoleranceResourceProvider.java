package org.ihie.des.ember.resource.provider;

import java.util.List;

import javax.annotation.Resource;

import org.ihie.des.ember.services.allergy.service.AllergyIntoleranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;

@Service("allergyIntoleranceResourceProvider")
@Resource(name="allergyIntoleranceResourceProvider")
public class AllergyIntoleranceResourceProvider implements IResourceProvider
{
    private static AllergyIntoleranceResourceProvider instance = null;

    private AllergyIntoleranceService allergyIntoleranceService;
    
    public final static AllergyIntoleranceResourceProvider getInstance() 
    {
        return (instance == null) ? new AllergyIntoleranceResourceProvider() : instance;
    }

    @Override
    public Class<AllergyIntolerance> getResourceType() 
    {
        return AllergyIntolerance.class;
    }

    @Search()
    public List<AllergyIntolerance> getAllergyIntolerance( 
                                                            @RequiredParam(name = AllergyIntolerance.SP_PATIENT) ReferenceParam patId, 
                                                            @OptionalParam(name = AllergyIntolerance.SP_DATE) DateRangeParam range
                                                         ) 
    {
        List<AllergyIntolerance> allergies = this.allergyIntoleranceService.getAllergiesBasedOnPatientAndDateRangeService( patId, range );
        
        return allergies;
    }

    @Autowired
    public void setAllergyIntoleranceService(AllergyIntoleranceService allergyIntoleranceService)
    {
        this.allergyIntoleranceService = allergyIntoleranceService;
    }
    
}
