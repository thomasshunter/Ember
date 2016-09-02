package org.ihie.des.ember.services.patient.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.ihie.des.ember.services.patient.bean.NameResolver;
import org.ihie.des.ember.services.patient.dao.PatientServiceDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;

@Service("patientServiceService")
public class PatientServiceService
{
    protected static Logger LOG                             = Logger.getLogger(PatientServiceService.class);
    public static final String DELIMITER_PLUS_SIGN          = "+";
    public static final String AMPERSAND                    = "&";
    private PatientServiceDAO patientServiceDAO;
    
    public List<IResource> getPatientByGivenNameFamilyNameAndGenderService( StringParam givenParm, StringParam familyParm, StringParam genderParm )
    {
        String given            = givenParm.getValue();
        String family           = familyParm.getValue();
        String gender           = genderParm.getValue();

        List<IResource> pats    = this.patientServiceDAO.getPatientByGivenNameFamilyNameAndGenderDAO( given.trim(), family.trim(), gender.trim() );
        
        return pats;        
    }
    
    public List<IResource> getPatientByGivenFamilyBirthdateGenderPhoneService( StringParam givenParm, StringParam familyParm, DateParam birthdateParm, StringParam genderParm, StringParam phoneParm )
    {
        String given            = givenParm.getValue();
        String family           = familyParm.getValue();
        DateTimeDt dob          = birthdateParm.getValueAsDateTimeDt();
        java.util.Date dt       = dob.getValue();
        java.sql.Date dtSQL     = new java.sql.Date( dt.getTime() );
        String gender           = genderParm.getValue();
        String phone            = phoneParm.getValue();
        
        List<IResource> pats    = this.patientServiceDAO.getPatientByGivenFamilyBirthdateGenderPhoneDAO( given, family, dtSQL, gender, phone );
                
        return pats;
    }
    
    
    
    
    // http://localhost:8080/EmberServer/fhir/Patient?name=PATIENT%20TEST&birthdate=1937-09-09&identifier=91&gender=F&organization=1
    
    public ca.uhn.fhir.model.dstu2.resource.Patient getPatientByNameMrnDobService( StringParam name, TokenParam mrnToken, DateParam dob, TokenParam genderToken, ReferenceParam org )
    {
        if( name == null || mrnToken == null || dob == null || genderToken == null || org == null  )
        {
            PatientServiceService.LOG.error( "PatientServiceServcie.getPatientByNameMrnDobService() encountered invalid input. Bailing." );
            
            return null;
        }
        else
        {
            String fullName             = name.getValue();
            NameResolver resolved       = NameResolver.create( fullName );
            String givenName            = resolved.getFirstName();  // This is getting it backwards
            String familyName           = resolved.getLastName();
            String mrn                  = mrnToken.getValue();
            java.util.Date dateOfBirth  = dob.getValue();
            java.sql.Date dateOfBirthSQL= new java.sql.Date( dateOfBirth.getTime() );
            String gender               = genderToken.getValue();
            String inst                 = org.getValue();
            int institution             = Integer.parseInt( inst );
            
            Patient patient             = this.patientServiceDAO.getPatientByNameMrnDobDAO( familyName, givenName, mrn, dateOfBirthSQL, gender, institution );
            patient.setId( "Patient/" + mrnToken.getValue() );
           
            return patient;
        }
    }
    
    @Deprecated
    public ca.uhn.fhir.model.dstu2.resource.Patient getPatientService( StringParam givenNameParm, StringParam familyNameParm, StringParam medicalRecordNumberParm, StringParam socialSecurityNumberParm, IdDt insitutionNumberPlusMRN )
    {
        boolean validInputs         = false;
        int institutionId           = -1;
        String medicalRecordNumber  = null;
        
        if( insitutionNumberPlusMRN != null )
        {
            String instIdPlusPersonIdString = insitutionNumberPlusMRN.getValueAsString();
            PatientServiceService.LOG.info( "PatientServiceService.getPatientService(), instIdPlusPersonIdString=" + instIdPlusPersonIdString );
            
            instIdPlusPersonIdString        = instIdPlusPersonIdString.replaceAll( "Patient/", "" );

            int indexOfPlusSign             = instIdPlusPersonIdString.indexOf( PatientServiceService.DELIMITER_PLUS_SIGN );
            int indexOfAmpersand            = instIdPlusPersonIdString.indexOf( PatientServiceService.AMPERSAND );
            
            if( indexOfPlusSign > -1 )
            {
                String instIdString = instIdPlusPersonIdString.substring( 0, indexOfPlusSign );
                institutionId       = Integer.parseInt( instIdString.trim() );
                
                if( indexOfAmpersand > -1 )
                {
                    medicalRecordNumber = instIdPlusPersonIdString.substring( indexOfPlusSign + 1, indexOfAmpersand );
                }
                else
                {
                    medicalRecordNumber = instIdPlusPersonIdString.substring( indexOfPlusSign + 1, instIdPlusPersonIdString.length() );
                }
            }
            
            validInputs = true;
        }
        else if( givenNameParm != null && familyNameParm != null )
        {
            validInputs = true;
        }
        else if( medicalRecordNumberParm != null )
        {
            validInputs = true;
        }
        else if( socialSecurityNumberParm != null )
        {
            validInputs = true;
        }

        Patient patient = null;
        
        if( validInputs )
        {
            patient = this.getPatientServiceDAO().getPatientDAO( givenNameParm, familyNameParm, medicalRecordNumberParm, socialSecurityNumberParm, institutionId, medicalRecordNumber );
        }
        else
        {
            PatientServiceService.LOG.error( "PatientServiceService.getPatientService() encountered no valid combination of inputs:"
                                                + "\n\t institutionId="
                                                + institutionId
                                                + "\n medicalRecordNumber="
                                                + medicalRecordNumber
                                                + "\t givenNameParm=" 
                                                + givenNameParm 
                                                + "\n\t familyNameParm=" 
                                                + familyNameParm 
                                                + "\n\t medicalRecordNumberParm=" 
                                                + medicalRecordNumberParm 
                                                + "\n\t socialSecurityNumberParm=" 
                                                + socialSecurityNumberParm 
                                           );
        }
        
        return patient;
    }


    @Autowired
    public void setPatientServiceDAO(PatientServiceDAO patientServiceDAO)
    {
        this.patientServiceDAO = patientServiceDAO;
    }
    public PatientServiceDAO getPatientServiceDAO()
    {
        return patientServiceDAO;
    }

    @SuppressWarnings("unused")
	public static void main( String[] args )
    {
        PatientServiceService pats  = new PatientServiceService();
        
        StringParam givenNameParm            = new StringParam("MARK");
        StringParam familyNameParm           = new StringParam("TIRITILLI");
        StringParam medicalRecordNumberParm  = new StringParam("8FD9B908ABE33E04270F07AA5D616731");
        StringParam patientId                = new StringParam("34001925");
        StringParam socialSecurityNumberParm = new StringParam();
        
        Patient pat                 = pats.getPatientService( givenNameParm, familyNameParm, medicalRecordNumberParm, socialSecurityNumberParm, null );
        
        System.out.println( "pat=" + pat );
    }

}
