package org.egov.pa.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.egov.pa.model.Document;
import org.egov.pa.model.KPI;
import org.egov.pa.model.KpiTarget;
import org.egov.pa.model.KpiTargetList;
import org.egov.pa.repository.KpiMasterRepository;
import org.egov.pa.service.KpiMasterService;
import org.egov.pa.web.contract.KPIGetRequest;
import org.egov.pa.web.contract.KPIRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component("kpiMasterServ")
@Slf4j
public class KpiMasterServiceImpl implements KpiMasterService {
	
	@Autowired 
	@Qualifier("kpiMasterRepo")
	private KpiMasterRepository kpiMasterRepository;

	@Override
	public KPIRequest createNewKpi(KPIRequest kpiRequest) {
		int numberOfIds = kpiRequest.getKpis().size(); 
    	log.info("KPI Message Received at Service Level : " + kpiRequest);
    	List<Long> kpiIdList = kpiMasterRepository.getNewKpiIds(numberOfIds);
    	log.info("KPI Master Next ID Generated is : " + kpiIdList);
    	if(kpiIdList.size() == kpiRequest.getKpis().size()) { 
    		for(int i = 0 ; i < kpiRequest.getKpis().size() ; i++) { 
    			kpiRequest.getKpis().get(i).setId(kpiIdList.get(i));
    		}
    	}
    	setCreatedDateAndUpdatedDate(kpiRequest);
    	prepareDocumentObjects(kpiRequest);
    	kpiMasterRepository.persistKpi(kpiRequest);
    	return kpiRequest;
	}
	
	@Override
	public KPIRequest updateNewKpi(KPIRequest kpiRequest) {
		log.info("KPI Message Received at Service Level : " + kpiRequest);
    	List<KpiTarget> updateList = new ArrayList<>(); 
    	List<KpiTarget> insertList = new ArrayList<>();
    	searchKpiTarget(kpiRequest, updateList, insertList);
    	log.info("KPI Targets to be updated : " + updateList);
    	log.info("KPI Targets to be inserted : " + insertList);
    	setCreatedDateAndUpdatedDate(kpiRequest);
    	prepareDocumentObjects(kpiRequest);
    	kpiMasterRepository.updateKpi(kpiRequest);
    	if(updateList.size() > 0) { 
    		KpiTargetList targetList = new KpiTargetList(); 
    		targetList.setTargetList(updateList);
    		kpiMasterRepository.updateKpiTarget(targetList);
    	} 
    	if(insertList.size() > 0) { 
    		KpiTargetList targetList = new KpiTargetList(); 
    		targetList.setTargetList(insertList);
    		kpiMasterRepository.persistKpiTarget(targetList);
    	}
    	return kpiRequest;
	}
	
	@Override
	public KPIRequest deleteNewKpi(KPIRequest kpiRequest) {
		log.info("KPI Message Received at Service Level : " + kpiRequest);
    	setCreatedDateAndUpdatedDate(kpiRequest);
    	kpiMasterRepository.deleteKpi(kpiRequest);
    	return kpiRequest;
	}
	
	@Override
	public List<KPI> searchKpi(KPIGetRequest kpiGetRequest) {
		log.info("KPI Get Request Received at Service Level : " + kpiGetRequest); 
    	return kpiMasterRepository.searchKpi(kpiGetRequest);
	}
	
	
	private void searchKpiTarget(KPIRequest kpiRequest, List<KpiTarget> updateList, List<KpiTarget> insertList) { 
    	for(KPI kpi : kpiRequest.getKpis()) { 
    		if(null != kpi.getKpiTarget()) {
    			KpiTarget kpiTarget = kpi.getKpiTarget();
    			if(null != kpiTarget.getId()) { 
    				updateList.add(kpiTarget); 
    			} else { 
    				kpiTarget.setCreatedBy(kpiRequest.getRequestInfo().getUserInfo().getId());
    				insertList.add(kpiTarget);
    			}
    		}
    	}
    }
	
	private void prepareDocumentObjects(KPIRequest kpiRequest) { 
    	List<KPI> kpiList = kpiRequest.getKpis(); 
    	for(KPI kpi : kpiList) {
    		if(null != kpi.getDocuments() && kpi.getDocuments().size() > 0) { 
    			for(Document doc : kpi.getDocuments()) { 
    				doc.setKpiCode(kpi.getCode());
    			}
    		}
    	}
    }
    
    private void setCreatedDateAndUpdatedDate(KPIRequest kpiRequest) { 
    	List<KPI> kpiList = kpiRequest.getKpis();
    	for(KPI kpi : kpiList) { 
    		kpi.setCreatedDate(new java.util.Date().getTime());
        	kpi.setLastModifiedDate(new java.util.Date().getTime());
        	if(null != kpi.getKpiTarget()) { 
        		kpi.getKpiTarget().setCreatedDate(new java.util.Date().getTime());
        		kpi.getKpiTarget().setLastModifiedDate(new java.util.Date().getTime());
        	}
    	}
    }
    
    public boolean checkNameOrCodeExists(KPIRequest kpiRequest, Boolean createOrUpdate) { 
    	List<KPI> kpiList = kpiMasterRepository.checkNameOrCodeExists(kpiRequest);
    	if(!createOrUpdate) { 
    		for(KPI kpi : kpiList) { 
    			for(int i=0 ; i<kpiRequest.getKpis().size() ; i++) {
    				if(kpiRequest.getKpis().get(i).getId() == kpi.getId()) {
        				return false; 
        			}
    			}
    		}
    	}
    	if(kpiList.size() > 0) 
    		return true;
    	else 
    		return false;
    }
}
