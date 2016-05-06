package org.ihie.des.ember.services.patient.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ihie.des.ember.database.util.OracleUtil;
import org.ihie.des.ember.services.patient.bean.PatientServiceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.composite.ContactPointDt;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Patient.Contact;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.IdentifierUseEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.rest.param.StringParam;

@Component
public class PatientServiceDAO
{
    protected static Logger LOG = Logger.getLogger(PatientServiceDAO.class);

    private OracleUtil oracleUtil;

    @SuppressWarnings("unused")
    public ca.uhn.fhir.model.dstu2.resource.Patient getPatientDAO( StringParam givenNameParm, StringParam familyNameParm, StringParam medicalRecordNumberParm, StringParam socialSecurityNumberParm )
    {
        String queryType        = null;
        
        if( givenNameParm != null && familyNameParm != null )
        {
            queryType = PatientServiceBean.PATIENT_QUERY_BY_LAST_AND_FIRST_NAMES;
        }
        else if( medicalRecordNumberParm != null )
        {
            queryType = PatientServiceBean.PATIENT_QUERY_BY_MEDICAL_RECORD_NUMBER;
        }
        else if( socialSecurityNumberParm != null )
        {
            queryType = PatientServiceBean.PATIENT_QUERY_BY_SOCIAL_SECURITY_NUMBER;
        }
            
        
        Patient patient         = new Patient();
        Connection con          = null;
        PreparedStatement ps    = null;
        ResultSet rs            = null;
        String query            = PatientServiceBean.getPatientQuery( queryType );
        
        try
        {
            con = this.oracleUtil.getOracleDatabaseConnection();
            ps  = con.prepareStatement( query );
            
            if( PatientServiceBean.PATIENT_QUERY_BY_LAST_AND_FIRST_NAMES.contains( queryType ) )
            {
                ps.setString( 1, familyNameParm.getValue() );
                ps.setString( 2,  givenNameParm.getValue() );
            }
            else if( PatientServiceBean.PATIENT_QUERY_BY_MEDICAL_RECORD_NUMBER.equals( queryType ) )
            {
                ps.setString( 1, medicalRecordNumberParm.getValue() );
            }
            else if ( PatientServiceBean.PATIENT_QUERY_BY_SOCIAL_SECURITY_NUMBER.equals( queryType ) )
            {
                ps.setString( 1, socialSecurityNumberParm.getValue() );
            }
            
            rs  = ps.executeQuery();
            
            while( rs.next() )
            {
               Long personId                    = rs.getLong(   1 );
               int institutionIdPerson          = rs.getInt(    2 );
               String ssn                       = rs.getString( 3 );
               String nameLast                  = rs.getString( 4 );
               String nameFirst                 = rs.getString( 5 );
               String nameMiddle                = rs.getString( 6 );
               String nameSuffix                = rs.getString( 7 );
               String namePrefix                = rs.getString( 8 );
               java.sql.Date dob                = rs.getDate(   9 );
               java.sql.Date dod                = rs.getDate(   10 );
               String genderCode                = rs.getString( 11 );
               int genderSysId                  = rs.getInt(    12 );
               String raceCode                  = rs.getString( 13 );
               int raceSysId                    = rs.getInt(    14 );
               String maritalStatusCode         = rs.getString( 15 );
               int maritalStatusSysID           = rs.getInt(    16 );
               String religionCode              = rs.getString( 17 );
               int religionSysId                = rs.getInt(    18 );
               String veteranYN                 = rs.getString( 19 );
               String humanYN                   = rs.getString( 20 );
               String dayPhoneNumber            = rs.getString( 21 );
               String nightPhoneNumber          = rs.getString( 22 );
               String homeAddressStreet         = rs.getString( 23 );
               String homeAddressStreet2        = rs.getString( 24 );
               String homeAddressCity           = rs.getString( 25 );
               String homeAddressStateCode      = rs.getString( 26 );
               int homeAddressStateSysId        = rs.getInt(    27 );
               String homeAddressZipCodeCode    = rs.getString( 28 );
               int homeAddressZipCodeSysId      = rs.getInt(    29 );
               String mothersMaidenNameFirst    = rs.getString( 30 );
               String mothersMaidenNameLast     = rs.getString( 31 );
               java.sql.Date mothersDob         = rs.getDate(   32 );
               java.sql.Date fathersDob         = rs.getDate(   33 );
               String workEmailAddress          = rs.getString( 34 );
               String homeEmailAddress          = rs.getString( 35 );
               int institutionIdPatient         = rs.getInt(    36 );
               Long patientId                   = rs.getLong(   37 );
               String medicalRecordNumber       = rs.getString( 38 );
               int medicalRecordNumberSysId     = rs.getInt(    39 );
               java.sql.Date registrationDate   = rs.getDate(   40 );
               String statusCode                = rs.getString( 41 );
               int statusCodeSysId              = rs.getInt(    42 );
               String vipYn                     = rs.getString( 43 );
               java.sql.Date registrationEndDate= rs.getDate(   44 );
               
               patient.addIdentifier().setSystem( PatientServiceBean.PATIENT_SYSTEM ).setValue( patientId.toString() );
               patient.getIdentifier().get(0).setUse(IdentifierUseEnum.OFFICIAL);
               patient.addIdentifier().setSystem( PatientServiceBean.PATIENT_MEDICAL_RECORD_NUMBER ).setValue( medicalRecordNumber );
               patient.getIdentifier().get(1).setUse(IdentifierUseEnum.SECONDARY);
               patient.addIdentifier().setSystem( PatientServiceBean.PATIENT_SOCIAL_SECURITY_NUMBER ).setValue( ssn );
                              
               patient.addName().addGiven( nameFirst ).addGiven( nameMiddle ).addFamily( nameLast ).addSuffix( nameSuffix ).addPrefix( namePrefix );
               
               DateDt birthdate                 = new DateDt();
               birthdate.setValue( dob );
               patient.setBirthDate( birthdate ); 
               
               List<AddressDt> addresses        = new ArrayList<AddressDt>();
               AddressDt add1                   = new AddressDt();
               add1.addLine( homeAddressStreet );
               add1.addLine( homeAddressStreet2 );
               add1.setCity( homeAddressCity );
               add1.setState( homeAddressStateCode );
               add1.setPostalCode( homeAddressZipCodeCode );
               patient.setAddress( addresses );
               
               Contact contact                  = new Contact();
               contact.setAddress( add1 );
               
               if( PatientServiceBean.PATIENT_GENDER_FEMALE.equals( genderCode ) )
               {
                   contact.setGender( AdministrativeGenderEnum.FEMALE );
               }
               else if( PatientServiceBean.PATIENT_GENDER_MALE.equals( genderCode ) )
               {
                   contact.setGender( AdministrativeGenderEnum.MALE );
               }
               else if( PatientServiceBean.PATIENT_GENDER_UNKNOWN.equals( genderCode ) )
               {
                   contact.setGender( AdministrativeGenderEnum.UNKNOWN );
               }
               else
               {
                   contact.setGender( AdministrativeGenderEnum.OTHER );                   
               }
               
               List<ContactPointDt> contactPoints   = new ArrayList<ContactPointDt>();
               
               ContactPointDt contact1              = new ContactPointDt();
               contact1.setValue( dayPhoneNumber );
               contactPoints.add( contact1 );
               
               ContactPointDt contact2              = new ContactPointDt();
               contact2.setValue( nightPhoneNumber );
               contactPoints.add( contact2 );

               ContactPointDt contact3              = new ContactPointDt();
               contact3.setValue( workEmailAddress );
               contactPoints.add( contact3 );

               ContactPointDt contact4              = new ContactPointDt();
               contact4.setValue( homeEmailAddress );
               contactPoints.add( contact4 );

               contact.setTelecom( contactPoints );
               patient.addContact( contact );
            }
        }
        catch( Exception e )
        {
            PatientServiceDAO.LOG.error( "PatientServiceDAO.getPatientDAO() threw an Exception while attempting to pull the patient with givenNameParm=" + givenNameParm + ", familyNameParm=" + familyNameParm );
        }
        finally
        {
            try
            {
                if( rs != null )
                {
                    rs.close();
                }
            }
            catch( Exception eRS )
            {
                PatientServiceDAO.LOG.error( "PatientServiceDAO.getPatientDAO() threw an Exception while attempting to close the RS, eRS=" + eRS );                
            }
            
            try
            {
                if( ps != null )
                {
                    ps.close();
                }
            }
            catch( Exception ePS )
            {
                PatientServiceDAO.LOG.error( "PatientServiceDAO.getPatientDAO() threw an Exception while attempting to close the PS, ePS=" + ePS );                                
            }
            
            try
            {
                if( con != null && !con.isClosed() )
                {
                    con.close();
                }
            }
            catch( Exception eCON )
            {
                PatientServiceDAO.LOG.error( "PatientServiceDAO.getPatientDAO() threw an Exception while attempting to close the CON, eCON=" + eCON );                
            }
        }

        return patient;
    }
    
    
    @Autowired
    public void setOracleUtil(OracleUtil oracleUtil)
    {
        this.oracleUtil = oracleUtil;
    }
    
}
