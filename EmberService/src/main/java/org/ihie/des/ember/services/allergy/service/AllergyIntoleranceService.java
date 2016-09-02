package org.ihie.des.ember.services.allergy.service;

import java.util.List;

import org.ihie.des.ember.services.allergy.dao.AllergyIntoleranceDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;

@Service("allergyIntoleranceService")
public class AllergyIntoleranceService
{
    public static final String PREFIX_PATIENT   = "Patient/";
    private AllergyIntoleranceDAO allergyIntoleranceDAO;
    
    public List<AllergyIntolerance> getAllergiesBasedOnPatientAndDateRangeService( ReferenceParam patId, DateRangeParam range )
    {
        String patientId                    = patId.getValue();        
        Long patIdLong                      = Long.parseLong( patientId );
        java.sql.Date lowerSQL              = null;
        java.sql.Date upperSQL              = null;
        int institutionId                   = 1;
        
        if( range != null )
        {
            DateParam lowerBound    = range.getLowerBound();
            DateParam upperBound    = range.getUpperBound();
            DateTimeDt lower        = lowerBound.getValueAsDateTimeDt();
            DateTimeDt upper        = upperBound.getValueAsDateTimeDt();
            java.util.Date lowerDt  = lower.getValue();
            java.util.Date upperDt  = upper.getValue();
            lowerSQL                = new java.sql.Date( lowerDt.getTime() );
            upperSQL                = new java.sql.Date( upperDt.getTime() );
        }
        
        List<AllergyIntolerance> allergies  = this.allergyIntoleranceDAO.getAllergiesBasedOnPatientAndDateRangeDAO( patIdLong, institutionId, lowerSQL, upperSQL );
        
        return allergies;
    }

    @Autowired
    public void setAllergyIntoleranceDAO(AllergyIntoleranceDAO allergyIntoleranceDAO)
    {
        this.allergyIntoleranceDAO = allergyIntoleranceDAO;
    }
}
