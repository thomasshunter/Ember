package org.ihie.des.ember.resource.provider;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.ihie.des.ember.database.util.OracleUtil;
import org.ihie.des.ember.services.patient.dao.PatientServiceDAO;
import org.ihie.des.ember.services.patient.service.PatientServiceService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.UriDt;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;

/**
 * All resource providers must implement IResourceProvider
 */
@Service("patientResourceProvider")
@Resource(name="patientResourceProvider")
public class PatientResourceProvider implements IResourceProvider
{    
    /**
     * The getResourceType method comes from IResourceProvider, and must be
     * overridden to indicate what type of resource this provider supplies.
     */
    @Override
    public Class<Patient> getResourceType()
    {
        return Patient.class;
    }

    /**
     * The "@Read" annotation indicates that this method supports the read
     * operation. Read operations should return a single resource instance.
     * 
     * @param theId
     *            The read operation takes one parameter, which must be of type
     *            IdDt and must be annotated with the "@Read.IdParam"
     *            annotation.
     * @return Returns a resource matching this identifier, or null if none
     *         exists.
     */
    @Read()
    public Patient getResourceById(@IdParam IdDt theId)
    {
        Patient patient = new Patient();
        patient.addIdentifier();
        patient.getIdentifier().get(0).setSystem(new UriDt("urn:hapitest:mrns"));
        patient.getIdentifier().get(0).setValue("00002");
        patient.addName().addFamily("Test");
        patient.getName().get(0).addGiven("PatientOne");
        patient.setGender(AdministrativeGenderEnum.FEMALE);
        
        return patient;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search
     * operation. You may have many different method annotated with this
     * annotation, to support many different search criteria. This example
     * searches by family name.
     * 
     * @param theFamilyName
     *            This operation takes one parameter which is the search
     *            criteria. It is annotated with the "@Required" annotation.
     *            This annotation takes one argument, a string containing the
     *            name of the search criteria. The datatype here is StringParam,
     *            but there are other possible parameter types depending on the
     *            specific search criteria.
     * @return This method returns a list of Patients. This list may contain
     *         multiple matching resources, or it may also be empty.
     */
    @Search()
    public List<Patient> getPatientByFamilyAndGivenNames( @RequiredParam(name = Patient.SP_FAMILY) StringParam familyNameParm, @RequiredParam(name = Patient.SP_GIVEN) StringParam givenNameParm )
    {
        StringParam medicalRecordNumberParm     = null;
        StringParam socialSecurityNumberParm    = null;
        Patient patient                         = this.getPatientServiceService().getPatientService( givenNameParm, familyNameParm, medicalRecordNumberParm, socialSecurityNumberParm );

        /*
        patient.addIdentifier();
        patient.getIdentifier().get(0).setUse(IdentifierUseEnum.OFFICIAL);
        patient.getIdentifier().get(0).setSystem(new UriDt("urn:hapitest:mrns"));
        patient.getIdentifier().get(0).setValue("00001");
        patient.addName();
        patient.getName().get(0).addFamily(theFamilyName.getValue());
        patient.getName().get(0).addGiven("PatientOne");
        patient.setGender(AdministrativeGenderEnum.MALE);
        */
        
        return Collections.singletonList(patient);
    }
    
    @Bean()
    public PatientServiceService getPatientServiceService()
    {
        OracleUtil oracleUtil               = new OracleUtil();
        PatientServiceDAO patDAO            = new PatientServiceDAO();
        patDAO.setOracleUtil(oracleUtil);

        PatientServiceService patService    = new PatientServiceService();
        patService.setPatientServiceDAO( patDAO );
        
        return patService;
    }

}
// END SNIPPET: provider
