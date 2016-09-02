package org.ihie.des.ember.services.institution.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class InstitutionIdentifierBean implements Serializable
{

    public static String getInstitutionIdentifierByCodeSystemBean( String institutionName )
    {
        StringBuilder query             = new StringBuilder();
        boolean includeInstitutionName  = (institutionName != null && institutionName.trim().length() > 0 ) ? true : false;
        
        if( includeInstitutionName )
        {
            query.append( "\n ( "                               );
        }
        
        query.append( "\n    SELECT "                           );
        query.append( "\n       INSTITUTION_NAME, "             );
        query.append( "\n       INSTITUTION_ID, "               );
        query.append( "\n       DESCRIPTION, "                  );
        query.append( "\n       ADDRESS_STREET, "               );
        query.append( "\n       ADDRESS_STREET_2, "             );
        query.append( "\n       ADDRESS_CITY, "                 );
        query.append( "\n       ADDRESS_STATE_CODE, "           );
        query.append( "\n       ADDRESS_STATE_SYS_ID, "         );
        query.append( "\n       ADDRESS_ZIP_CODE_CODE, "        );
        query.append( "\n       ADDRESS_ZIP_CODE_SYS_ID, "      );
        query.append( "\n       AUTHORITY_NAME, "               );
        query.append( "\n       HL7_OID, "                      );
        query.append( "\n       ADDRESS_COUNTY_CODE, "          );
        query.append( "\n       ADDRESS_COUNTY_SYS_ID, "        );
        query.append( "\n       ABBREVIATION, "                 );
        query.append( "\n       DEFINER_YN "                    );
        query.append( "\n    FROM "                             );
        query.append( "\n       INSTITUTION "                   );
        query.append( "\n    WHERE "                            );
        query.append( "\n       INSTITUTION_ID = ? "            );  // Bind var #1
        
        if( includeInstitutionName )
        {
            query.append( "\n )"                                    );
            query.append( "\n UNION "                               );
            query.append( "\n ("                                    );
            query.append( "\n    SELECT "                           );
            query.append( "\n       INSTITUTION_NAME, "             );
            query.append( "\n       INSTITUTION_ID, "               );
            query.append( "\n       DESCRIPTION, "                  );
            query.append( "\n       ADDRESS_STREET, "               );
            query.append( "\n       ADDRESS_STREET_2, "             );
            query.append( "\n       ADDRESS_CITY, "                 );
            query.append( "\n       ADDRESS_STATE_CODE, "           );
            query.append( "\n       ADDRESS_STATE_SYS_ID, "         );
            query.append( "\n       ADDRESS_ZIP_CODE_CODE, "        );
            query.append( "\n       ADDRESS_ZIP_CODE_SYS_ID, "      );
            query.append( "\n       AUTHORITY_NAME, "               );
            query.append( "\n       HL7_OID, "                      );
            query.append( "\n       ADDRESS_COUNTY_CODE, "          );
            query.append( "\n       ADDRESS_COUNTY_SYS_ID, "        );
            query.append( "\n       ABBREVIATION, "                 );
            query.append( "\n       DEFINER_YN "                    );
            query.append( "\n    FROM "                             );
            query.append( "\n       INSTITUTION "                   );
            query.append( "\n    WHERE "                            );
            query.append( "\n       INSTITUTION_NAME = ? "          ); // Bind var #2        
            query.append( "\n )"                                    );
        }
        
        return query.toString();
    }
}
