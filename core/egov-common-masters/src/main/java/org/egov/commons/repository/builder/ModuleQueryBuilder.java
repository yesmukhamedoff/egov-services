package org.egov.commons.repository.builder;

import java.util.List;

import org.egov.commons.config.ApplicationProperties;
import org.egov.commons.web.contract.ModuleGetRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ModuleQueryBuilder {
	private static final Logger logger = LoggerFactory.getLogger(ModuleQueryBuilder.class);

	@Autowired
	private ApplicationProperties applicationProperties;

	private static final String BASE_QUERY = "SELECT id, name, enabled, contextroot, parentmodule, displayname, ordernumber FROM eg_module";

	@SuppressWarnings("rawtypes")
	public String getQuery(ModuleGetRequest moduleGetRequest, List preparedStatementValues) {
		StringBuilder selectQuery = new StringBuilder(BASE_QUERY);

		addWhereClause(selectQuery, preparedStatementValues, moduleGetRequest);
		addOrderByClause(selectQuery, moduleGetRequest);
		addPagingClause(selectQuery, preparedStatementValues, moduleGetRequest);

		logger.debug("Query : " + selectQuery);
		return selectQuery.toString();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addWhereClause(StringBuilder selectQuery, List preparedStatementValues,
			ModuleGetRequest moduleGetRequest) {

		if (moduleGetRequest.getId() == null && moduleGetRequest.getName() == null
				&& moduleGetRequest.getTenantId() == null)
			return;

		selectQuery.append(" WHERE");
		boolean isAppendAndClause = false;

		if (moduleGetRequest.getTenantId() != null) {
			isAppendAndClause = true;
			selectQuery.append(" tenantId = ?");
			preparedStatementValues.add(moduleGetRequest.getTenantId());
		}

		if (moduleGetRequest.getId() != null) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, selectQuery);
			selectQuery.append(" id IN " + getIdQuery(moduleGetRequest.getId()));
		}

		if (moduleGetRequest.getName() != null) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, selectQuery);
			selectQuery.append(" name = ?");
			preparedStatementValues.add(moduleGetRequest.getName());
		}

		if (moduleGetRequest.getEnabled() != null) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, selectQuery);
			selectQuery.append(" enabled = ?");
			preparedStatementValues.add(moduleGetRequest.getEnabled());
		}
		if (moduleGetRequest.getContextRoot() != null) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, selectQuery);
			selectQuery.append(" contextroot = ?");
			preparedStatementValues.add(moduleGetRequest.getContextRoot());
		}

		if (moduleGetRequest.getParentModule() != null) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, selectQuery);
			selectQuery.append(" parentmodule = ?");
			preparedStatementValues.add(moduleGetRequest.getParentModule());
		}
	}

	private void addOrderByClause(StringBuilder selectQuery, ModuleGetRequest moduleGetRequest) {
		String sortBy = (moduleGetRequest.getSortBy() == null ? "name" : moduleGetRequest.getSortBy());
		String sortOrder = (moduleGetRequest.getSortOrder() == null ? "ASC" : moduleGetRequest.getSortOrder());
		selectQuery.append(" ORDER BY " + sortBy + " " + sortOrder);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addPagingClause(StringBuilder selectQuery, List preparedStatementValues,
			ModuleGetRequest moduleGetRequest) {
		// handle limit(also called pageSize) here
		selectQuery.append(" LIMIT ?");
		long pageSize = Integer.parseInt(applicationProperties.commonsSearchPageSizeDefault());
		if (moduleGetRequest.getPageSize() != null)
			pageSize = moduleGetRequest.getPageSize();
		preparedStatementValues.add(pageSize); // Set limit to pageSize

		// handle offset here
		selectQuery.append(" OFFSET ?");
		int pageNumber = 0; // Default pageNo is zero meaning first page
		if (moduleGetRequest.getPageNumber() != null)
			pageNumber = moduleGetRequest.getPageNumber() - 1;
		preparedStatementValues.add(pageNumber * pageSize); // Set offset to
															// pageNo * pageSize
	}

	/**
	 * This method is always called at the beginning of the method so that and
	 * is prepended before the field's predicate is handled.
	 * 
	 * @param appendAndClauseFlag
	 * @param queryString
	 * @return boolean indicates if the next predicate should append an "AND"
	 */
	private boolean addAndClauseIfRequired(boolean appendAndClauseFlag, StringBuilder queryString) {
		if (appendAndClauseFlag)
			queryString.append(" AND");

		return true;
	}

	private static String getIdQuery(List<Long> idList) {
		StringBuilder query = new StringBuilder("(");
		if (idList.size() >= 1) {
			query.append(idList.get(0).toString());
			for (int i = 1; i < idList.size(); i++) {
				query.append(", " + idList.get(i));
			}
		}
		return query.append(")").toString();
	}
}
