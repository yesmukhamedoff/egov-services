package org.egov.egf.bill.domain.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.domain.exception.CustomBindException;
import org.egov.common.domain.model.Pagination;
import org.egov.egf.bill.domain.model.Checklist;
import org.egov.egf.bill.domain.model.ChecklistSearch;
import org.egov.egf.bill.domain.repository.ChecklistRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;

@Import(TestConfiguration.class)
@RunWith(SpringRunner.class)
public class ChecklistServiceTest {

	private ChecklistService checklistService;
	
	@Mock
	private ChecklistRepository checklistRepository;
	
	@Mock
	private SmartValidator validator;
	
	private BindingResult errors = new BeanPropertyBindingResult(null, null);

	private RequestInfo requestInfo = new RequestInfo();
	
	@Test
	public final void test_create() {

		List<Checklist> expextedResult = getChecklists();
		
		when(checklistRepository.save(any(List.class), any(RequestInfo.class))).thenReturn(expextedResult);
		
		List<Checklist> actualResult = checklistService.create(expextedResult, errors, requestInfo);

		assertEquals(expextedResult, actualResult);

	}
	
	@Test
	public final void test_update() {

		List<Checklist> expextedResult = getChecklists();

		when(checklistRepository.update(any(List.class), any(RequestInfo.class))).thenReturn(expextedResult);

		List<Checklist> actualResult = checklistService.update(expextedResult, errors, requestInfo);

		assertEquals(expextedResult, actualResult);

	}
	
	@Test
	public final void test_search() {

		List<Checklist> checklists = getChecklists();
		ChecklistSearch checklistSearch = new ChecklistSearch();
		checklistSearch.setTenantId("default");
		Pagination<Checklist> expextedResult = new Pagination<>();

		expextedResult.setPagedData(checklists);

		when(checklistRepository.search(checklistSearch)).thenReturn(expextedResult);

		Pagination<Checklist> actualResult = checklistService.search(checklistSearch, errors);

		assertEquals(expextedResult, actualResult);
	}
	
	@Test
	public final void test_save() {

		Checklist expextedResult = getChecklists().get(0);

		when(checklistRepository.save(any(Checklist.class))).thenReturn(expextedResult);

		Checklist actualResult = checklistService.save(expextedResult);

		assertEquals(expextedResult, actualResult);
	}
	
	@Test(expected = CustomBindException.class)
	public final void test_save_with_null_req() {

		List<Checklist> expextedResult = getChecklists();

		when(checklistRepository.save(any(List.class), any(RequestInfo.class))).thenReturn(expextedResult);

		List<Checklist> actualResult = checklistService.create(null, errors, requestInfo);

		assertEquals(expextedResult, actualResult);

	}

	@Test
	public final void test_update1() {

		Checklist expextedResult = getChecklists().get(0);

		when(checklistRepository.update(any(Checklist.class))).thenReturn(expextedResult);

		Checklist actualResult = checklistService.update(expextedResult);

		assertEquals(expextedResult, actualResult);
	}
	
	@Test(expected = CustomBindException.class)
	public final void test_update_with_null_req() {

		List<Checklist> expextedResult = getChecklists();

		when(checklistRepository.update(any(List.class), any(RequestInfo.class))).thenReturn(expextedResult);

		List<Checklist> actualResult = checklistService.update(null, errors, requestInfo);

		assertEquals(expextedResult, actualResult);

	}
	
	private List<Checklist> getChecklists() {

		List<Checklist> checklists = new ArrayList<Checklist>();
		
		Checklist checklist = Checklist.builder().id("b96561462fdc484fa97fa72c3944ad89")
				.build();
		checklist.setTenantId("default");
		
		checklists.add(checklist);
		return checklists;
	}
	
}
