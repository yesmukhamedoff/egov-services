package org.egov.inv.domain.service;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.Constants;
import java.util.Date;
import org.egov.common.DomainService;
import org.egov.common.Pagination;
import org.egov.common.exception.CustomBindException;
import org.egov.common.exception.ErrorCode;
import org.egov.common.exception.InvalidDataException;
import org.egov.inv.model.Indent;
import org.egov.inv.model.Indent.IndentStatusEnum;
import org.egov.inv.model.IndentDetail;
import org.egov.inv.model.IndentRequest;
import org.egov.inv.model.IndentResponse;
import org.egov.inv.model.IndentSearch;
import org.egov.inv.persistence.entity.IndentDetailEntity;
import org.egov.inv.persistence.repository.IndentDetailJdbcRepository;
import org.egov.inv.persistence.repository.IndentJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class IndentService extends DomainService {
	/*
	 * @Autowired private StoreRepository storeRepository;
	 * 
	 * @Autowired private DepartmentRepository departmentRepository;
	 */

	@Autowired
	private IndentJdbcRepository indentRepository;
	@Value("${inv.indents.save.topic}")
	private String saveTopic;
	@Value("${inv.indents.save.key}")
	private String saveKey;

	@Value("${inv.indents.update.topic}")
	private String updateTopic;
	@Value("${inv.indents.update.key}")
	private String updateKey;

	@Autowired
	private IndentDetailJdbcRepository indentDetailJdbcRepository;

	@Transactional
	public IndentResponse create(IndentRequest indentRequest) {

		try {
			List<Indent> indents = fetchRelated(indentRequest.getIndents());
			validate(indents, Constants.ACTION_CREATE);
			List<String> sequenceNos = indentRepository.getSequence(Indent.class.getSimpleName(), indents.size());
			int i = 0;
			for (Indent b : indents) {
				b.setId(sequenceNos.get(i));
				// move to id-gen with format <ULB short code>/<Store
				// Code>/<fin. Year>/<serial No.>
				b.setIndentNumber(sequenceNos.get(i));
				i++;
				int j = 0;
				//TO-DO : when workflow implemented change this to created
				b.setIndentStatus(IndentStatusEnum.APPROVED);
				b.setAuditDetails(getAuditDetails(indentRequest.getRequestInfo(), Constants.ACTION_CREATE));
				List<String> detailSequenceNos = indentRepository.getSequence(IndentDetail.class.getSimpleName(),
						indents.size());
				for (IndentDetail d : b.getIndentDetails()) {
					d.setId(detailSequenceNos.get(j));
					d.setTenantId(b.getTenantId());
					j++;
				}
			}
			kafkaQue.send(saveTopic, saveKey, indentRequest);

			IndentResponse response = new IndentResponse();
			response.setIndents(indentRequest.getIndents());
			response.setResponseInfo(getResponseInfo(indentRequest.getRequestInfo()));
			return response;
		} catch (CustomBindException e) {
			throw e;
		}

	}

	@Transactional
	public IndentResponse update(IndentRequest indentRequest) {

		try {
			String tenantId="";
			List<Indent> indents = fetchRelated(indentRequest.getIndents());
			String indentNumber="";
			List<String> ids=new ArrayList<String>();
			validate(indents, Constants.ACTION_UPDATE);
			for (Indent b : indents) {
			 int j=0;
			 if(!indentNumber.isEmpty())
				 indentNumber=b.getIndentNumber();
				b.setAuditDetails(getAuditDetails(indentRequest.getRequestInfo(), Constants.ACTION_UPDATE));
				for (IndentDetail d : b.getIndentDetails()) {
					if(d.getId()==null)
						d.setId(indentRepository.getSequence(IndentDetail.class.getSimpleName(),1).get(0));
					ids.add(d.getId());
					d.setTenantId(b.getTenantId());
					if(tenantId.isEmpty())
						tenantId=b.getTenantId();
					j++;
				}
			}
			
			kafkaQue.send(saveTopic, saveKey, indentRequest);
            indentDetailJdbcRepository.markDeleted(ids,tenantId,"indentdetail","indentNumber",indentNumber);
			IndentResponse response = new IndentResponse();
			response.setIndents(indentRequest.getIndents());
			response.setResponseInfo(getResponseInfo(indentRequest.getRequestInfo()));
			return response;
		} catch (CustomBindException e) {
			throw e;
		}

	}

	public IndentResponse search(IndentSearch is) {
		IndentResponse response = new IndentResponse();
		Pagination<Indent> search = indentRepository.search(is);
		if (!search.getPagedData().isEmpty()) {
			List<String> indentNumbers = new ArrayList<>();
			for (Indent indent : search.getPagedData()) {
				indentNumbers.add(indent.getIndentNumber());
			}

			List<IndentDetailEntity> indentDetails = indentDetailJdbcRepository.find(indentNumbers, is.getTenantId());

			IndentDetail detail = null;
			for (Indent indent : search.getPagedData()) {
				for (IndentDetailEntity detailEntity : indentDetails) {
					if (indent.getIndentNumber().equalsIgnoreCase(detailEntity.getIndentNumber())) {
						detail = detailEntity.toDomain();
						indent.addIndentDetailsItem(detail);
					}
				}
			}
		}
		response.setIndents(search.getPagedData());
		response.setPage(getPage(search));
		return response;

	}

	private void validate(List<Indent> indents, String method) {

		try {
			Long currentDate=	new Date().getTime();
			switch (method) {
			
			case Constants.ACTION_CREATE: {
				if (indents == null) {
					throw new InvalidDataException("indents", ErrorCode.NOT_NULL.getCode(), null);
				}
				for(Indent indent: indents)
				{
					if(indent.getIndentDate().compareTo(currentDate) > 0)
					{
						throw new InvalidDataException("indentDate", ErrorCode.DATE_LE_CURRENTDATE.getCode(), indent.getIndentDate().toString());	
					}
					
					if(indent.getExpectedDeliveryDate().compareTo(currentDate) < 0)
					{
						throw new InvalidDataException("indentDate", ErrorCode.DATE_GE_CURRENTDATE.getCode(), indent.getIndentDate().toString());	
					}
				}
				
			}
				break;

			}
		} catch (IllegalArgumentException e) {

		}

	}

	public List<Indent> fetchRelated(List<Indent> indents) {
		for (Indent indent : indents) {
			// fetch related items
			/*
			 * if (indent.getIssueStore() != null) { Store issueStore =
			 * storeRepository.findById(indent.getIssueStore()); if (issueStore
			 * == null) { throw new InvalidDataException("issueStore",
			 * "issueStore.invalid", " Invalid issueStore"); }
			 * indent.setIssueStore(issueStore); } if (indent.getIndentStore()
			 * != null) { Store indentStore =
			 * storeRepository.findById(indent.getIndentStore()); if
			 * (indentStore == null) { throw new
			 * InvalidDataException("indentStore", "indentStore.invalid",
			 * " Invalid indentStore"); } indent.setIndentStore(indentStore); }
			 * if (indent.getDepartment() != null) { Department department =
			 * departmentRepository.findById(indent.getDepartment()); if
			 * (department == null) { throw new
			 * InvalidDataException("department", "department.invalid",
			 * " Invalid department"); } indent.setDepartment(department); }
			 */

		}

		return indents;
	}

}