package org.ihie.des.ember.services.allergy.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AllergyIntoleranceBean implements Serializable
{
    public static final String getAllergiesBasedOnPatientAndDateRangeQuery()
    {
        StringBuilder query = new StringBuilder();
              
        query.append( "\n WITH cteGlobal AS "                                                                               );
        query.append( "\n ( "                                                                                               );
        query.append( "\n    SELECT "                                                                                       );
        query.append( "\n       g.institution_id, "                                                                         );
        query.append( "\n       g.person_id "                                                                               );
        query.append( "\n    FROM "                                                                                         );
        query.append( "\n       rmrs.person p JOIN rmrs.person g "                                                          );
        query.append( "\n       ON "                                                                                        );
        query.append( "\n       g.global_person_id = p.global_person_id "                                                   );
        query.append( "\n       AND "                                                                                       );
        query.append( "\n       ( "                                                                                         );
        query.append( "\n          ("                                                                                       );
        query.append( "\n             p.institution_id = ? "                                                                ); // Bind var #1
        query.append( "\n             AND "                                                                                 );
        query.append( "\n             p.person_id = ? "                                                                     ); // Bind var #2
        query.append( "\n          ) "                                                                                      );
        query.append( "\n          OR "                                                                                     );
        query.append( "\n          ( "                                                                                      );
        query.append( "\n             g.global_match_up_to_date_yn IS NULL "                                                );
        query.append( "\n             AND "                                                                                 );
        query.append( "\n             p.global_match_up_to_date_yn IS NULL "                                                );
        query.append( "\n          ) "                                                                                      );
        query.append( "\n       ) "                                                                                         );
        query.append( "\n       JOIN rmrs.institution_tree t "                                                              );
        query.append( "\n       ON "                                                                                        );
        query.append( "\n       t.child_institution_id = g.institution_id "                                                 );
        query.append( "\n       AND "                                                                                       );
        query.append( "\n       t.parent_institution_id = 101 "                                                             );
        query.append( "\n    WHERE "                                                                                        );
        query.append( "\n       p.institution_id = ? "                                                                      ); // Bind var #3
        query.append( "\n       AND "                                                                                       );
        query.append( "\n       p.person_id = ? "                                                                           ); // Bind var #4
        query.append( "\n ), "                                                                                              );
        query.append( "\n cteAllergyBase AS "                                                                               );
        query.append( "\n ( "                                                                                               );
        query.append( "\n    SELECT "                                                                                       );
        query.append( "\n       v.service_code, "                                                                           );
        query.append( "\n       v.service_sys_id, "                                                                         );
        query.append( "\n       v.value_text_for_display allergy_description, "                                             );
        query.append( "\n       MIN(v.physiologic_time) effective_time_low, "                                               );
        query.append( "\n       MAX(v.physiologic_time) effective_time_high "                                               );
        query.append( "\n    FROM "                                                                                         );
        query.append( "\n       cteGlobal g JOIN rmrs.clinical_variable v "                                                 );
        query.append( "\n       ON "                                                                                        );
        query.append( "\n       v.service_code IN ('7717', '3924', '36074') "                                               );
        query.append( "\n       AND "                                                                                       );
        query.append( "\n       v.service_sys_id = 1 "                                                                      );
        query.append( "\n       AND "                                                                                       );
        query.append( "\n       v.institution_id = g.institution_id "                                                       );
        query.append( "\n       AND "                                                                                       );
        query.append( "\n       v.patient_id = g.person_id "                                                                );
        query.append( "\n       AND "                                                                                       );
        query.append( "\n       v.value_type = 'C' "                                                                        );
        query.append( "\n       AND "                                                                                       );
        query.append( "\n       v.value_text_for_display IS NOT NULL "                                                      );
        query.append( "\n    GROUP BY "                                                                                     );                                            
        query.append( "\n       service_code, "                                                                             );                                            
        query.append( "\n       service_sys_id, "                                                                           );                                            
        query.append( "\n       value_text_for_display "                                                                    );                                            
        query.append( "\n ), "                                                                                              );                                            
        query.append( "\n cteServiceCodes AS "                                                                              );                                            
        query.append( "\n ( "                                                                                               );                                            
        query.append( "\n    SELECT "                                                                                       );                                            
        query.append( "\n       999999 id_index, "                                                                          );                                            
        query.append( "\n       b.service_code base_code, "                                                                 );                                            
        query.append( "\n       b.service_sys_id base_sys_id, "                                                             );                                            
        query.append( "\n       b.service_code, "                                                                           );                                            
        query.append( "\n       s.name service_code_system, "                                                               );                                            
        query.append( "\n       s.hl7_oid service_oid, "                                                                    );                                            
        query.append( "\n       c.name service_name "                                                                       );                                            
        query.append( "\n    FROM "                                                                                         );                                            
        query.append( "\n       cteAllergyBase b JOIN rmrs.code_system s "                                                  );                                            
        query.append( "\n       ON "                                                                                        );                                            
        query.append( "\n       s.sys_id = b.service_sys_id  "                                                              );                                            
        query.append( "\n       JOIN rmrs.concept c "                                                                       );                                            
        query.append( "\n       ON "                                                                                        );                                            
        query.append( "\n       c.sys_id = b.service_sys_id "                                                               );                                            
        query.append( "\n       AND "                                                                                       );                                            
        query.append( "\n       c.code = b.service_code "                                                                   );                                            
        query.append( "\n    UNION ALL "                                                                                    );                                            
        query.append( "\n       SELECT "                                                                                    );                                            
        query.append( "\n          m.source_sys_id id_index, "                                                              );                                            
        query.append( "\n          b.service_code base_code, "                                                              );                                            
        query.append( "\n          b.service_sys_id base_sys_id, "                                                          );                                            
        query.append( "\n          c.code service_cide, "                                                                   );                                            
        query.append( "\n          s.name service_code_system, "                                                            );                                            
        query.append( "\n          s.hl7_oid service_oid, "                                                                 );                                            
        query.append( "\n          c.name service_name "                                                                    );                                            
        query.append( "\n       FROM "                                                                                      );                                            
        query.append( "\n          cteAllergyBase b JOIN rmrs.concept_mapping m "                                           );                                            
        query.append( "\n          ON "                                                                                     );                                            
        query.append( "\n          m.target_sys_id = b.service_sys_id "                                                     );                                            
        query.append( "\n          AND "                                                                                    );                                            
        query.append( "\n          m.target_code = b.service_code "                                                         );                                            
        query.append( "\n          AND "                                                                                    );                                            
        query.append( "\n          m.source_sys_id IN (41, 78) "                                                            );                                            
        query.append( "\n          JOIN rmrs.code_system s "                                                                );                                            
        query.append( "\n          ON "                                                                                     );                                            
        query.append( "\n          s.sys_id = m.source_sys_id "                                                             );                                            
        query.append( "\n          JOIN rmrs.concept c "                                                                    );                                            
        query.append( "\n          ON "                                                                                     );                                            
        query.append( "\n          c.sys_id = m.source_sys_id "                                                             );                                            
        query.append( "\n          AND "                                                                                    );                                            
        query.append( "\n          c.code = m.source_code "                                                                 );                                            
        query.append( "\n ), "                                                                                              );                                            
        query.append( "\n cteServiceCodeSelected AS "                                                                       );                                            
        query.append( "\n ( "                                                                                               );                                            
        query.append( "\n    SELECT "                                                                                       );                                            
        query.append( "\n       ROW_NUMBER() OVER(PARTITION BY base_code, base_sys_id ORDER BY id_index) select_index, "    );                                            
        query.append( "\n       base_code, "                                                                                );                                            
        query.append( "\n       base_sys_id, "                                                                              );                                            
        query.append( "\n       service_code, "                                                                             );                                            
        query.append( "\n       service_code_system, "                                                                      );                                            
        query.append( "\n       service_oid, "                                                                              );                                            
        query.append( "\n       service_name "                                                                              );                                            
        query.append( "\n    FROM "                                                                                         );                                            
        query.append( "\n       cteServiceCodes "                                                                           );                                            
        query.append( "\n )"                                                                                                );                                            
        query.append( "\n SELECT "                                                                                          );                                            
        query.append( "\n    s.service_code, "                                                                              );                                            
        query.append( "\n    s.service_name, "                                                                              );                                            
        query.append( "\n    s.service_oid, "                                                                               );                                            
        query.append( "\n    s.service_code_system, "                                                                       );                                            
        query.append( "\n    REPLACE(TO_CHAR(ORA_HASH(s.service_code),'xxxxxxxx') "                                         );                                            
        query.append( "\n            || "                                                                                   );                                            
        query.append( "\n            TO_CHAR(ORA_HASH(s.service_name),'xxxxxxxx') "                                         );                                            
        query.append( "\n            || "                                                                                   );                                            
        query.append( "\n            TO_CHAR(ORA_HASH(s.service_code_system),'xxxxxxxx') "                                  );                                            
        query.append( "\n            || "                                                                                   );                                            
        query.append( "\n            TO_CHAR(ORA_HASH(s.service_oid),'xxxxxxxx'),' ','') service_guid, "                    );                                            
        query.append( "\n    b.allergy_description, "                                                                       );                                            
        query.append( "\n    b.effective_time_low, "                                                                        );                                            
        query.append( "\n    b.effective_time_high "                                                                        );                                            
        query.append( "\n FROM "                                                                                            );                                            
        query.append( "\n    cteAllergyBase b JOIN cteServiceCodeSelected s "                                               );                                            
        query.append( "\n    ON "                                                                                           );                                            
        query.append( "\n    s.select_index = 1 "                                                                           );                                            
        query.append( "\n    AND "                                                                                          );                                            
        query.append( "\n    s.base_code = b.service_code "                                                                 );                                            
        query.append( "\n    AND "                                                                                          );                                            
        query.append( "\n    s.base_sys_id = b.service_sys_id "                                                             );                                            
        
        return query.toString();
    }
    
}
