package org.ihie.des.ember.resource.provider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.ihie.des.ember.resource.util.Util;
import org.ihie.des.ember.services.allergy.service.AllergyIntoleranceService;
import org.ihie.des.ember.services.patient.bean.PatientServiceBean;
import org.ihie.des.ember.services.patient.service.PatientServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.IncludeParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;

/**
 * All resource providers must implement IResourceProvider
 */
@Service("patientResourceProvider")
@Resource(name="patientResourceProvider")
public class PatientResourceProvider implements IResourceProvider
{      
    private PatientServiceService patientServiceService;
    private AllergyIntoleranceService allergyIntoleranceService;
    
    /**
     * The getResourceType method comes from IResourceProvider, and must be
     * overridden to indicate what type of resource this provider supplies.
     */
    @Override
    public Class<ca.uhn.fhir.model.dstu2.resource.Patient> getResourceType()
    {
        return ca.uhn.fhir.model.dstu2.resource.Patient.class;
    }

    @Search(compartmentName = "AllergyIntolerance")
    public List<AllergyIntolerance> searchAllergyIntolerance( @IdParam IdDt patientId, @OptionalParam(name = AllergyIntolerance.SP_DATE) DateRangeParam range )
    {
        ReferenceParam patId                = PatientResourceProvider.newReference( patientId );
        
        List<AllergyIntolerance> allergies  = this.allergyIntoleranceService.getAllergiesBasedOnPatientAndDateRangeService( patId, range );
        
        return allergies;

       // return org.ihie.des.ember.resource.provider.AllergyIntoleranceResourceProvider.getInstance().getAllergyIntolerance( newReference(id), range );
    }

    @Search()
    public List<IResource> getPatientsByFirstLastDobGenderSsnAndPhoneNumber(
                                                                               @RequiredParam(name = Patient.SP_GIVEN) StringParam given, 
                                                                               @RequiredParam(name = Patient.SP_FAMILY) StringParam family,
                                                                               @RequiredParam(name = Patient.SP_BIRTHDATE) DateParam birthdate,              // e.g. 2011-01-02
                                                                               @RequiredParam(name = Patient.SP_GENDER) StringParam gender,
                                                                               @RequiredParam(name = Patient.SP_PHONE) StringParam phone,
                                                                               @IncludeParam(allow =
                                                                                               { 
                                                                                                       "Patient:link", 
                                                                                                       "AllergyIntolerance:patient" 
                                                                                               }
                                                                                            ) Set<Include> includes                                                                           )
    {        
        List<IResource> pats        = this.patientServiceService.getPatientByGivenFamilyBirthdateGenderPhoneService( given, family, birthdate, gender, phone );

        Calendar cal                = Calendar.getInstance();
        java.util.Date endDate      = cal.getTime();
        cal.add( Calendar.MONTH, -1 );
        java.util.Date startDate    = cal.getTime();
        Set<Include> refIncludes    = new HashSet<Include>();
        
        addIncludes( pats, pats.get(0).getId(), new DateRangeParam( startDate, endDate ), includes, refIncludes );

        Patient.Link link       = new Patient.Link();
        
        link.setOther(Util.prepareContainedResource(pats.get(0), null ));

        return pats;
    }
    
    
    @Search()
    public List<IResource> getPatientsByGivenFamilyGender( 
                                                            @RequiredParam(name = Patient.SP_GIVEN) StringParam givenName,
                                                            @RequiredParam(name = Patient.SP_FAMILY) StringParam familyName, 
                                                            @RequiredParam(name = Patient.SP_GENDER) StringParam gender, 
                                                            @IncludeParam(allow =
                                                                            { 
                                                                                    "Patient:link", 
                                                                                    "AllergyIntolerance:patient" 
                                                                            }
                                                                          ) Set<Include> includes                                                                           )
    {
        if( givenName == null || familyName == null || gender == null )
        {
            throw new InternalErrorException( "Unable to search: givenName, familyName or gender are missing. All three are required. Please try again." );
        }
        
        List<IResource> pats                    = this.patientServiceService.getPatientByGivenNameFamilyNameAndGenderService( givenName, familyName, gender );

        Calendar cal                            = Calendar.getInstance();
        java.util.Date endDate                  = cal.getTime();
        cal.add( Calendar.MONTH, -1 );
        java.util.Date startDate                = cal.getTime();
        Set<Include> refIncludes                = new HashSet<Include>();
        
        Patient targetPatient                   = (Patient) pats.get( 1 ); // Looking for specific data.
        List<IdentifierDt> identifiers          = targetPatient.getIdentifier();
        Iterator<IdentifierDt> identifiersIt    = identifiers.iterator();
        String patientIdString                  = null;
        
        while( identifiersIt.hasNext() )
        {
            IdentifierDt anIdentifier   = identifiersIt.next();
            String identifierSystem     = anIdentifier.getSystem();
                        
            if( PatientServiceBean.PATIENT_SYSTEM.equals( identifierSystem ) )  // PatientId
            {
                patientIdString = anIdentifier.getValue();
                break;
            }
        }
        
        IdDt patientId                          = new IdDt(patientIdString);
        
        addIncludes( pats, patientId, new DateRangeParam( startDate, endDate ), includes, refIncludes );

        Patient.Link link       = new Patient.Link();
        
        link.setOther(Util.prepareContainedResource(pats.get(0), null ));
        
               
        return pats;        
    }
    
    /*
    @Search()
    public List<Patient> getPatientByMrn(
                                            @RequiredParam(name = Patient.SP_IDENTIFIER) TokenParam mrn, 
                                            @OptionalParam(name = Patient.SP_BIRTHDATE) DateParam birthdate, 
                                            @OptionalParam(name = Patient.SP_GENDER) TokenParam gender, 
                                            @OptionalParam(name = Patient.SP_ORGANIZATION) ReferenceParam organization, 
                                            @IncludeParam(allow = { "Patient:link" }) Set<Include> includes
                                        )
    {
        List<Patient> pats = new ArrayList<Patient>();
        
        
        return pats;
    }
    */
    
    // https://localhost:8443/EmberServer/fhir/Patient?name=PATIENT%20TEST&birthdate=1937-09-09&identifier=91&gender=F&organization=1&_include=AllergyIntolerance:patient
    @Search()
    public List<IResource> getPatientByGivenAndFamilyNames( 
                                                                                            @RequiredParam(name = Patient.SP_NAME) StringParam name, 
                                                                                            @OptionalParam(name = Patient.SP_IDENTIFIER) TokenParam mrn,
                                                                                            @OptionalParam(name = Patient.SP_BIRTHDATE) DateParam dob,              // e.g. 2011-01-02
                                                                                            @OptionalParam(name = Patient.SP_GENDER) TokenParam gender,
                                                                                            @OptionalParam(name = Patient.SP_ORGANIZATION) ReferenceParam org, 
                                                                                            @IncludeParam(allow =
                                                                                                            { 
                                                                                                                    "Patient:link", 
                                                                                                                    "AllergyIntolerance:patient", 
                                                                                                                    "Encounter:participant", 
                                                                                                                    "Order:detail", 
                                                                                                                    "DiagnosticOrder:orderer", 
                                                                                                                    "DiagnosticReport:specimen", 
                                                                                                                    "DiagnosticReport:result", 
                                                                                                                    "MedicationOrder:prescriber",
                                                                                                                    "MedicationDispense:authorizingPrescription" 
                                                                                                            }
                                                                                                         ) Set<Include> includes, 
                                                                                            @IncludeParam(reverse = true, allow =
                                                                                                             { 
                                                                                                                     "Appointment:patient", 
                                                                                                                     "Encounter:patient", 
                                                                                                                     "Order:subject", 
                                                                                                                     "DiagnosticOrder:subject", 
                                                                                                                     "ProcedureRequest:subject", 
                                                                                                                     "DeviceUseRequest:subject", 
                                                                                                                     "NutritionOrder:patient",
                                                                                                                     "ReferralRequest:patient", 
                                                                                                                     "DiagnosticReport:subject", 
                                                                                                                     "MedicationOrder:patient", 
                                                                                                                     "MedicationDispense:patient", 
                                                                                                                     "Immunization:patient", 
                                                                                                                     "Specimen:subject", 
                                                                                                                     "Composition:subject",
                                                                                                                     "Media:subject", 
                                                                                                                     "Observation:subject", 
                                                                                                                     "Condition:patient", 
                                                                                                                     "Procedure:subject", 
                                                                                                                     "AllergyIntolerance:patient", 
                                                                                                                     "ClinicalImpression:patient", 
                                                                                                                     "FamilyMemberHistory:patient",
                                                                                                                     "QuestionnaireResponse:subject" }) Set<Include> revIncludes                                                                               
                                                                                                         )
    { 
        if( name == null && mrn == null && dob == null )
        {
            throw new InternalErrorException( "Unable to search: name, identifier [medical record number] and birthdate were all absent." );
        }
        
        Patient patient             = this.patientServiceService.getPatientByNameMrnDobService( name, mrn, dob, gender, org );        
        List<IResource> list        = new ArrayList<IResource>();
        list.add( patient );
        
        Calendar cal                = Calendar.getInstance();
        java.util.Date endDate      = cal.getTime();
        cal.add( Calendar.MONTH, -1 );
        java.util.Date startDate    = cal.getTime();
        
        addIncludes( list, patient.getId(), new DateRangeParam( startDate, endDate ), includes, revIncludes );

        Patient.Link link       = new Patient.Link();
        
        link.setOther(Util.prepareContainedResource(patient, null ));

        
        return list;
        
        //return read(Name.create(FhirQuery.getValue(name)), mrn, dob, gender, org, includes);
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
        StringParam medicalRecordNumberParm     = null;
        StringParam socialSecurityNumberParm    = null;
        StringParam givenNameParm               = null;
        StringParam familyNameParm              = null;
        
        Patient patient                         = this.patientServiceService.getPatientService( givenNameParm, familyNameParm, medicalRecordNumberParm, socialSecurityNumberParm, theId );
        
        return patient;
    }

    public final static ReferenceParam newReference(final IdDt id)
    {
        return new ReferenceParam(id.getValue());
    }

    
    private void addIncludes( List<IResource> list, IdDt patientId, DateRangeParam range, final Set<Include> includes, final Set<Include> revIncludes)
    {
        if (Util.contains(includes, AllergyIntolerance.INCLUDE_PATIENT))
        {
            list.addAll( searchAllergyIntolerance(patientId, range) );
        }
        
        /*
        if (Util.contains(revIncludes, Appointment.INCLUDE_PATIENT))
        {
//            list.addAll(searchAppointment(id, range, updated, fixSort(Appointment.SP_DATE, sort, updated)));
        }
        if (Util.contains(revIncludes, Encounter.INCLUDE_PATIENT))
        {
//            list.addAll(searchEncounter(id, range, updated, includes, fixSort(Encounter.SP_DATE, sort, updated)));
        }
        if (Util.contains(revIncludes, Order.INCLUDE_SUBJECT))
        {
//            list.addAll(searchOrder(id, range, updated, includes, fixSort(Order.SP_DATE, sort, updated)));
        }
        if (Util.contains(revIncludes, DiagnosticOrder.INCLUDE_SUBJECT))
        {
//            list.addAll(searchDiagnosticOrder(id, range, updated, fixSort(DiagnosticOrder.SP_EVENT_DATE, sort, updated)));
        }
        if (Util.contains(revIncludes, ProcedureRequest.INCLUDE_SUBJECT))
        {
//            list.addAll(searchProcedureRequest(id, updated));
        }
        if (Util.contains(revIncludes, DeviceUseRequest.INCLUDE_SUBJECT))
        {
//            list.addAll(searchDeviceUseRequest(id, updated));
        }
        if (Util.contains(revIncludes, NutritionOrder.INCLUDE_PATIENT))
        {
//            list.addAll(searchNutritionOrder(id, range, updated, fixSort(NutritionOrder.SP_DATETIME, sort, updated)));
        }
        if (Util.contains(revIncludes, ReferralRequest.INCLUDE_PATIENT))
        {
//            list.addAll(searchReferralRequest(id, range, updated, fixSort(ReferralRequest.SP_DATE, sort, updated)));
        }
        if (Util.contains(revIncludes, DiagnosticReport.INCLUDE_SUBJECT))
        {
//            list.addAll(searchDiagnosticReport(id, range, updated, includes, sort));
        }
        if (Util.contains(revIncludes, MedicationOrder.INCLUDE_PATIENT))
        {
//            list.addAll(searchMedicationOrder(id, range, updated, includes, fixSort(MedicationOrder.SP_DATEWRITTEN, sort, updated)));
        }
        if (Util.contains(revIncludes, MedicationDispense.INCLUDE_PATIENT))
        {
//            list.addAll(searchMedicationDispense(id, range, updated, includes, fixSort(MedicationDispense.SP_WHENHANDEDOVER, sort, updated)));
        }
        if (Util.contains(revIncludes, Immunization.INCLUDE_PATIENT))
        {
//            list.addAll(searchImmunization(id, range, updated, fixSort(Immunization.SP_DATE, sort, updated)));
        }
        if (Util.contains(revIncludes, Specimen.INCLUDE_SUBJECT))
        {
//            list.addAll(searchSpecimen(id, range, updated, fixSort(Specimen.SP_COLLECTED, sort, updated)));
        }
        if (Util.contains(revIncludes, Composition.INCLUDE_SUBJECT))
        {
//            list.addAll(searchMedia(id, range, updated, fixSort(Composition.SP_DATE, sort, updated)));
        }
        if (Util.contains(revIncludes, Media.INCLUDE_SUBJECT))
        {
//            list.addAll(searchMedia(id, range, updated, fixSort(Media.SP_CREATED, sort, updated)));
        }
        if (Util.contains(revIncludes, Observation.INCLUDE_SUBJECT))
        {
//            list.addAll(searchObservation(id, range, updated, fixSort(Observation.SP_DATE, sort, updated)));
        }
        if (Util.contains(revIncludes, Condition.INCLUDE_PATIENT))
        {
//            list.addAll(searchCondition(id, range, updated, fixSort(Condition.SP_DATE_RECORDED, sort, updated)));
        }
        if (Util.contains(revIncludes, Procedure.INCLUDE_PATIENT))
        {
//            list.addAll(searchProcedure(id, range, updated, fixSort(Procedure.SP_DATE, sort, updated)));
        }        
        if (Util.contains(revIncludes, ClinicalImpression.INCLUDE_PATIENT))
        {
//            list.addAll(searchClinicalImpression(id, range, updated, fixSort(ClinicalImpression.SP_DATE, sort, updated)));
        }
        if (Util.contains(revIncludes, FamilyMemberHistory.INCLUDE_PATIENT))
        {
//            list.addAll(searchFamilyMemberHistory(id, range, updated, fixSort(FamilyMemberHistory.SP_DATE, sort, updated)));
        }
        if (Util.contains(revIncludes, QuestionnaireResponse.INCLUDE_SUBJECT))
        {
//            list.addAll(searchQuestionnaireResponse(id, null, range, updated, fixSort(QuestionnaireResponse.SP_AUTHORED, sort, updated)));
        }
        
        */
    }

    
    @Autowired
    public void setPatientServiceService(PatientServiceService patientServiceService)
    {
        this.patientServiceService = patientServiceService;
    }
    
    @Autowired
    public void setAllergyIntoleranceService(AllergyIntoleranceService allergyIntoleranceService)
    {
        this.allergyIntoleranceService = allergyIntoleranceService;
    }

    
}