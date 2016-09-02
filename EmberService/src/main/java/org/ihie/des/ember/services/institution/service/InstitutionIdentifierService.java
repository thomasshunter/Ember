package org.ihie.des.ember.services.institution.service;

import org.ihie.des.ember.services.institution.dao.InstitutionIdenfitierDAO;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.model.dstu2.resource.Organization;

@Service("institutionIdentifierService")
public class InstitutionIdentifierService
{
    private InstitutionIdenfitierDAO institutionIdenfitierDAO;
    
    public ca.uhn.fhir.model.dstu2.resource.Organization getInstitutionIdentifierByCodeSystemService( int institutionIdParm, String institutionNameParm )
    {
        Organization org    = this.institutionIdenfitierDAO.getInstitutionIdentifierByCodeSystemDAO( institutionIdParm, institutionNameParm );
        
        
        
        return org;
    }
    

}
