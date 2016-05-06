package org.ihie.des.ember.services.patient.service;

import org.apache.log4j.Logger;
import org.ihie.des.ember.services.patient.dao.PatientServiceDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.param.StringParam;

@Service("patientServiceService")
public class PatientServiceService
{
    protected static Logger LOG = Logger.getLogger(PatientServiceService.class);

    private PatientServiceDAO patientServiceDAO;
    
    public ca.uhn.fhir.model.dstu2.resource.Patient getPatientQuery( StringParam givenNameParm, StringParam familyNameParm, StringParam medicalRecordNumberParm, StringParam socialSecurityNumberParm )
    {
        boolean validInputs = false;
        
        if( givenNameParm != null && familyNameParm != null )
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
            patient = this.patientServiceDAO.getPatientDAO( givenNameParm, familyNameParm, medicalRecordNumberParm, socialSecurityNumberParm );
        }
        else
        {
            PatientServiceService.LOG.error( "PatientServiceService.getPatientQuery() encountered no valid combination of inputs:" 
                                                + "\n\t givenNameParm=" 
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

    public static void main( String[] args )
    {
        PatientServiceService pats  = new PatientServiceService();
        
        StringParam givenNameParm            = new StringParam("MARK");
        StringParam familyNameParm           = new StringParam("TIRITILLI");
        StringParam medicalRecordNumberParm  = new StringParam("8FD9B908ABE33E04270F07AA5D616731");
        StringParam patientId                = new StringParam("34001925");
        StringParam socialSecurityNumberParm = new StringParam();
        
        Patient pat                 = pats.getPatientQuery( givenNameParm, familyNameParm, medicalRecordNumberParm, socialSecurityNumberParm );
        
        System.out.println( "pat=" + pat );
    }
}
