package org.ihie.des.ember.services.patient.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PatientServiceBean implements Serializable
{
    public static final String PATIENT_QUERY_BY_LAST_AND_FIRST_NAMES        = "PATIENT_QUERY_LAST_FIRST";
    public static final String PATIENT_QUERY_BY_MEDICAL_RECORD_NUMBER       = "PATIENT_QUERY_MRN";
    public static final String PATIENT_QUERY_BY_SOCIAL_SECURITY_NUMBER      = "PATIENT_QUERY_SSN";
    
    public static final String PATIENT_SYSTEM                               = "urn:RMRS:ID";
    public static final String PATIENT_MEDICAL_RECORD_NUMBER                = "urn:RMRS:MRN";
    public static final String PATIENT_SOCIAL_SECURITY_NUMBER               = "urn:RMRS:SSN";
    
    public static final String PATIENT_GENDER_FEMALE                        = "F";
    public static final String PATIENT_GENDER_MALE                          = "M";
    public static final String PATIENT_GENDER_UNKNOWN                       = "U";
    
    public static String getPatientQuery( String patientQueryByType )
    {
        StringBuilder query = new StringBuilder();
        
        query.append( "\n SELECT "                                      );
        query.append( "\n    per.PERSON_ID, "                           ); // 1
        query.append( "\n    per.INSTITUTION_ID, "                      ); // 2
        query.append( "\n    per.SOCIAL_SECURITY_NUMBER, "              ); // 3
        query.append( "\n    per.NAME_LAST, "                           ); // 4
        query.append( "\n    per.NAME_FIRST, "                          ); // 5
        query.append( "\n    per.NAME_MIDDLE, "                         ); // 6
        query.append( "\n    per.NAME_SUFFIX, "                         ); // 7
        query.append( "\n    per.NAME_PREFIX, "                         ); // 8
        query.append( "\n    per.DATE_OF_BIRTH, "                       ); // 9
        query.append( "\n    per.DATE_OF_DEATH, "                       ); // 10
        query.append( "\n    per.GENDER_CODE, "                         ); // 11
        query.append( "\n    per.GENDER_SYS_ID, "                       ); // 12 
        query.append( "\n    per.RACE_CODE, "                           ); // 13
        query.append( "\n    per.RACE_SYS_ID, "                         ); // 14
        query.append( "\n    per.MARITAL_STATUS_CODE, "                 ); // 15
        query.append( "\n    per.MARITAL_STATUS_SYS_ID, "               ); // 16
        query.append( "\n    per.RELIGION_CODE, "                       ); // 17
        query.append( "\n    per.RELIGION_SYS_ID, "                     ); // 18
        query.append( "\n    per.VETERAN_YN, "                          ); // 19
        query.append( "\n    per.HUMAN_YN, "                            ); // 20
        query.append( "\n    per.DAY_PHONE_NUMBER, "                    ); // 21
        query.append( "\n    per.NIGHT_PHONE_NUMBER, "                  ); // 22
        query.append( "\n    per.HOME_ADDRESS_STREET, "                 ); // 23
        query.append( "\n    per.HOME_ADDRESS_STREET_2, "               ); // 24
        query.append( "\n    per.HOME_ADDRESS_CITY, "                   ); // 25
        query.append( "\n    per.HOME_ADDRESS_STATE_CODE, "             ); // 26
        query.append( "\n    per.HOME_ADDRESS_STATE_SYS_ID, "           ); // 27
        query.append( "\n    per.HOME_ADDRESS_ZIP_CODE_CODE, "          ); // 28
        query.append( "\n    per.HOME_ADDRESS_ZIP_CODE_SYS_ID, "        ); // 29
        query.append( "\n    per.MOTHERS_MAIDEN_NAME_FIRST, "           ); // 30
        query.append( "\n    per.MOTHERS_MAIDEN_NAME_LAST, "            ); // 31
        query.append( "\n    per.MOTHERS_DATE_OF_BIRTH, "               ); // 32
        query.append( "\n    per.FATHERS_DATE_OF_BIRTH, "               ); // 33
        query.append( "\n    per.WORK_EMAIL_ADDRESS, "                  ); // 34
        query.append( "\n    per.HOME_EMAIL_ADDRESS, "                  ); // 35
        query.append( "\n    pat.INSTITUTION_ID, "                      ); // 36
        query.append( "\n    pat.PATIENT_ID, "                          ); // 37
        query.append( "\n    pat.MEDICAL_RECORD_NUMBER, "               ); // 38
        query.append( "\n    pat.MEDICAL_RECORD_NUMBER_SYS_ID, "        ); // 39
        query.append( "\n    pat.REGISTRATION_DATE, "                   ); // 40
        query.append( "\n    pat.STATUS_CODE, "                         ); // 41
        query.append( "\n    pat.STATUS_SYS_ID, "                       ); // 42
        query.append( "\n    pat.VIP_YN, "                              ); // 43
        query.append( "\n    pat.REGISTRATION_END_DATE "                ); // 44
        query.append( "\n FROM "                                        );
        query.append( "\n    PERSON per INNER JOIN PATIENT pat "        );
        query.append( "\n    ON "                                       );
        query.append( "\n    per.PERSON_ID = pat.PATIENT_ID "           );
        query.append( "\n WHERE "                                       );
        
        if( PatientServiceBean.PATIENT_QUERY_BY_LAST_AND_FIRST_NAMES.equals( patientQueryByType ) )
        {
            query.append( "\n    per.NAME_LAST = ? "                    );
            query.append( "\n    AND "                                  );
            query.append( "\n    per.NAME_FIRST = ? "                   );
        }
        else if( PatientServiceBean.PATIENT_QUERY_BY_MEDICAL_RECORD_NUMBER.equals( patientQueryByType ) )
        {
            query.append( "\n    pat.MEDICAL_RECORD_NUMBER = ? "        );
        }
        else if( PatientServiceBean.PATIENT_QUERY_BY_SOCIAL_SECURITY_NUMBER.equals( patientQueryByType ) )
        {
            query.append( "\n    per.SOCIAL_SECURITY_NUMBER = ? "       );
        }
                
        return query.toString();
    }
}
