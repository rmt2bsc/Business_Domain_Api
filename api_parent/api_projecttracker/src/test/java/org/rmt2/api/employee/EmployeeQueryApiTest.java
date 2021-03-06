package org.rmt2.api.employee;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.util.List;

import org.dao.admin.EmployeeDaoException;
import org.dao.mapping.orm.rmt2.ProjEmployee;
import org.dao.mapping.orm.rmt2.ProjEmployeeTitle;
import org.dao.mapping.orm.rmt2.ProjEmployeeType;
import org.dao.mapping.orm.rmt2.VwEmployeeExt;
import org.dao.mapping.orm.rmt2.VwEmployeeProjects;
import org.dto.ClientDto;
import org.dto.EmployeeDto;
import org.dto.EmployeeTitleDto;
import org.dto.EmployeeTypeDto;
import org.dto.ProjectEmployeeDto;
import org.dto.adapter.orm.EmployeeObjectFactory;
import org.dto.adapter.orm.ProjectObjectFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modules.ProjectTrackerApiConst;
import org.modules.employee.EmployeeApi;
import org.modules.employee.EmployeeApiException;
import org.modules.employee.EmployeeApiFactory;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.ProjectTrackerMockData;
import org.rmt2.api.ProjectTrackerMockDataFactory;

import com.InvalidDataException;
import com.api.persistence.AbstractDaoClientImpl;
import com.api.persistence.DatabaseException;
import com.api.persistence.db.orm.Rmt2OrmClientFactory;
import com.api.util.RMT2Date;

/**
 * Test the query functionality of the Employee module of the Project Tracker Api.
 * 
 * @author rterrell
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, ResultSet.class })
public class EmployeeQueryApiTest extends ProjectTrackerMockData {

    public static final int EXT_EMP_PROJ_COUNT = 10;
    public static final int EMP_PROJ_COUNT = 0;
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        super.tearDown();
        return;
    }
    
    @Test
    public void testSuccess_Fetch_All_Clients() {
        // Stub all clients fetch.
        VwEmployeeProjects mockCriteria = new VwEmployeeProjects();
        mockCriteria.setEmpId(ProjectTrackerMockDataFactory.TEST_EMPLOYEE_ID);
        try {
            when(this.mockPersistenceClient.retrieveList(eq(mockCriteria))).thenReturn(this.mockVwEmployeeProjectsFetchMultiple);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Fetch all employee projects case setup failed");
        }
        
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        List<ClientDto> results = null;
        try {
            results = api.getClients(ProjectTrackerMockDataFactory.TEST_EMPLOYEE_ID);
        } catch (EmployeeApiException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(results);
        Assert.assertEquals(5, results.size());
        for (int ndx = 0; ndx < results.size(); ndx++) {
            ClientDto obj = results.get(ndx);
            Assert.assertEquals(obj.getClientId(), (ProjectTrackerMockDataFactory.TEST_CLIENT_ID + ndx));
            Assert.assertEquals(obj.getClientName(), (ProjectTrackerMockDataFactory.TEST_CLIENT_ID + ndx) + " Company");
        }
    }
    
    @Test
    public void testValidation_Fetch_All_Clients_Null_Input_EmployeeId() {
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        VwEmployeeProjects vep = new VwEmployeeProjects();
        ProjectEmployeeDto criteria = ProjectObjectFactory.createEmployeeProjectDtoInstance(vep);
        criteria.setEmpId(ProjectTrackerMockDataFactory.TEST_EMPLOYEE_ID);
        try {
            api.getClients(null);
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof InvalidDataException);
            e.printStackTrace();
        }
    }
    
    @Test
    public void testValidation_Fetch_All_Clients_Negative_Input_EmployeeId() {
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        VwEmployeeProjects vep = new VwEmployeeProjects();
        ProjectEmployeeDto criteria = ProjectObjectFactory.createEmployeeProjectDtoInstance(vep);
        criteria.setEmpId(-1000);
        try {
            api.getClients(null);
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof InvalidDataException);
            e.printStackTrace();
        }
    }

    @Test
    public void testValidation_Fetch_All_Clients_Zero_Input_EmployeeId() {
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        VwEmployeeProjects vep = new VwEmployeeProjects();
        ProjectEmployeeDto criteria = ProjectObjectFactory.createEmployeeProjectDtoInstance(vep);
        criteria.setEmpId(0);
        try {
            api.getClients(null);
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof InvalidDataException);
            e.printStackTrace();
        }
    }
    
    @Test
    public void testError_Fetch_All_Clients_DB_Access_Fault() {
        VwEmployeeProjects mockCriteria = new VwEmployeeProjects();
        mockCriteria.setEmpId(ProjectTrackerMockDataFactory.TEST_EMPLOYEE_ID);
        try {
            when(this.mockPersistenceClient.retrieveList(eq(mockCriteria)))
                    .thenThrow(new DatabaseException("A database error occurred"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Fetch all employee projects case setup failed");
        }
        
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        try {
            api.getClients(ProjectTrackerMockDataFactory.TEST_EMPLOYEE_ID);
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof EmployeeApiException);
            Assert.assertTrue(e.getCause() instanceof EmployeeDaoException);
            Assert.assertTrue(e.getCause().getCause() instanceof DatabaseException);
            e.printStackTrace();
        }
    }
    
    @Test
    public void testSuccess_Fetch_Employee_List_Using_Criteria() {
        // Stub all employee fetch.
        ProjEmployee mockCriteria = new ProjEmployee();
        mockCriteria.setManagerId(ProjectTrackerMockDataFactory.TEST_MANAGER_ID);
        try {
            when(this.mockPersistenceClient.retrieveList(eq(mockCriteria))).thenReturn(this.mockEmployeeFetchMultiple);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Fetch all employee case setup failed");
        }
        
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        EmployeeDto criteria = EmployeeObjectFactory.createEmployeeDtoInstance(null);
        criteria.setManagerId(ProjectTrackerMockDataFactory.TEST_MANAGER_ID);
        List<EmployeeDto> results = null;
        try {
            results = api.getEmployee(criteria);
        } catch (EmployeeApiException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(results);
        Assert.assertEquals(5, results.size());
        for (int ndx = 0; ndx < results.size(); ndx++) {
            EmployeeDto obj = results.get(ndx);
            Assert.assertEquals(obj.getEmployeeId(), (ProjectTrackerMockDataFactory.TEST_EMPLOYEE_ID + ndx));
            Assert.assertEquals(obj.getManagerId(), ProjectTrackerMockDataFactory.TEST_MANAGER_ID);
            Assert.assertEquals(obj.getEmployeeTitleId(), (ProjectTrackerMockDataFactory.TEST_EMPLOYEE_TITLE_ID + ndx));
            Assert.assertEquals(obj.getLoginId(), (ProjectTrackerMockDataFactory.TEST_LOGIN_ID + ndx));
            int startYear = 2010 + ndx;
            Assert.assertEquals(RMT2Date.stringToDate(startYear + "-01-01"), obj.getStartDate());
            if (obj.getTerminationDate() != null) {
                Assert.assertEquals(obj.getEmployeeTypeId(), 202);
                Assert.assertEquals(obj.getIsManager(), 1);
                Assert.assertEquals(RMT2Date.stringToDate("2018-01-01"), obj.getTerminationDate());
            }
            else {
                Assert.assertEquals(obj.getEmployeeTypeId(), 201);
                Assert.assertEquals(obj.getIsManager(), 0);
            }
            int nameSeed = 1 + ndx;
            Assert.assertEquals(obj.getEmployeeFirstname(), "first_name_" + nameSeed);
            Assert.assertEquals(obj.getEmployeeLastname(), "last_name_" + nameSeed);
            Assert.assertEquals(("111-11-500" + ndx), obj.getSsn());
            Assert.assertEquals(ProjectTrackerMockDataFactory.TEST_COMPANY_NAME, obj.getEmployeeCompanyName());
            Assert.assertEquals(EMP_PROJ_COUNT, obj.getProjectCount());
        }
    }
    
    @Test
    public void testSuccess_Fetch_Single_Employee() {
        // Stub all employee fetch.
        ProjEmployee mockCriteria = new ProjEmployee();
        mockCriteria.setEmpId(ProjectTrackerMockDataFactory.TEST_EMPLOYEE_ID);
        try {
            when(this.mockPersistenceClient.retrieveList(eq(mockCriteria))).thenReturn(this.mockEmployeeFetchSingle);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Fetch all employee case setup failed");
        }
        
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        EmployeeDto criteria = EmployeeObjectFactory.createEmployeeDtoInstance(null);
        criteria.setEmployeeId(ProjectTrackerMockDataFactory.TEST_EMPLOYEE_ID);
        EmployeeDto results = null;
        try {
            results = api.getEmployee(ProjectTrackerMockDataFactory.TEST_EMPLOYEE_ID);
        } catch (EmployeeApiException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(results);
        Assert.assertEquals(results.getEmployeeId(), 2220);
        Assert.assertEquals(results.getManagerId(), 3330);
        Assert.assertEquals(results.getEmployeeTitleId(), 101);
        Assert.assertEquals(results.getLoginId(), 999991);
        Assert.assertEquals(RMT2Date.stringToDate("2010-01-01"), results.getStartDate());
        Assert.assertEquals(RMT2Date.stringToDate("2018-01-01"), results.getTerminationDate());
        Assert.assertEquals(results.getEmployeeTypeId(), 201);
        Assert.assertEquals(results.getIsManager(), 0);
        Assert.assertEquals(results.getEmployeeFirstname(), "first_name_1");
        Assert.assertEquals(results.getEmployeeLastname(), "last_name_1");
        Assert.assertEquals("111-11-5000", results.getSsn());
        Assert.assertEquals("ABC Company", results.getEmployeeCompanyName());
        Assert.assertEquals(EMP_PROJ_COUNT, results.getProjectCount());

    }
    
    @Test
    public void testSuccess_Fetch_Single_EmployeeExt() {
        VwEmployeeExt mockCriteria = new VwEmployeeExt();
        mockCriteria.setEmployeeId(ProjectTrackerMockDataFactory.TEST_EMPLOYEE_ID);
        try {
            when(this.mockPersistenceClient.retrieveList(eq(mockCriteria))).thenReturn(this.mockExtEmployeeFetchSingle);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Fetch single extended employee case setup failed");
        }
        
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        EmployeeDto criteria = EmployeeObjectFactory.createEmployeeExtendedDtoInstance(null);
        criteria.setEmployeeId(ProjectTrackerMockDataFactory.TEST_EMPLOYEE_ID);
        List<EmployeeDto> results = null;
        try {
            results = api.getEmployeeExt(criteria);
        } catch (EmployeeApiException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        EmployeeDto dto = results.get(0);
        Assert.assertEquals(dto.getEmployeeId(), 2220);
        Assert.assertEquals(dto.getManagerId(), 3330);
        Assert.assertEquals(dto.getEmployeeTitle(), "EmployeeTitle");
        Assert.assertEquals(dto.getLoginId(), 999991);
        Assert.assertEquals(RMT2Date.stringToDate("2010-01-01"), dto.getStartDate());
        Assert.assertEquals(RMT2Date.stringToDate("2018-01-01"), dto.getTerminationDate());
        Assert.assertEquals(dto.getEmployeeType(), "EmployeeType");
        Assert.assertEquals(dto.getIsManager(), 0);
        Assert.assertEquals(dto.getEmployeeFirstname(), "first_name_1");
        Assert.assertEquals(dto.getEmployeeLastname(), "last_name_1");
        Assert.assertEquals("111-11-5000", dto.getSsn());
        Assert.assertEquals("ABC Company", dto.getEmployeeCompanyName());
        Assert.assertEquals(EXT_EMP_PROJ_COUNT, dto.getProjectCount());
    }
    
    @Test
    public void testValidation_Fetch_Employee_List_Using_Null_Criteria() {
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        EmployeeDto criteria = null;
        try {
            api.getEmployee(criteria);
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof InvalidDataException);
            e.printStackTrace();
        }
    }
    
    @Test
    public void testValidation_Fetch_Employee_Single_Using_Null_EmployeeId() {
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        Integer empId = null;
        try {
            api.getEmployee(empId);
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof InvalidDataException);
            e.printStackTrace();
        }
    }
    
    @Test
    public void testValidation_Fetch_EmployeeExt_Single_Using_Null_EmployeeId() {
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        try {
            api.getEmployeeExt(null);
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof InvalidDataException);
            e.printStackTrace();
        }
    }
    
    @Test
    public void testValidation_Fetch_Employee_Single_Using_Negative_EmployeeId() {
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        Integer empId = -1000;
        try {
            api.getEmployee(empId);
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof InvalidDataException);
            e.printStackTrace();
        }
    }
    
    @Test
    public void testValidation_Fetch_Employee_Single_Using_Zero_EmployeeId() {
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        Integer empId = 0;
        try {
            api.getEmployee(empId);
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof InvalidDataException);
            e.printStackTrace();
        }
    }
    
    @Test
    public void testError_Fetch_Employee_List_DB_Access_Fault() {
        ProjEmployee mockCriteria = new ProjEmployee();
        mockCriteria.setManagerId(ProjectTrackerMockDataFactory.TEST_MANAGER_ID);
        try {
            when(this.mockPersistenceClient.retrieveList(eq(mockCriteria)))
                    .thenThrow(new DatabaseException("A database error occurred"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Fetch all employee case setup failed");
        }
        
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        EmployeeDto criteria = EmployeeObjectFactory.createEmployeeDtoInstance(null);
        criteria.setManagerId(ProjectTrackerMockDataFactory.TEST_MANAGER_ID);
        try {
            api.getEmployee(criteria);
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof EmployeeApiException);
            Assert.assertTrue(e.getCause() instanceof EmployeeDaoException);
            Assert.assertTrue(e.getCause().getCause() instanceof DatabaseException);
            e.printStackTrace();
        }
    }
    
    @Test
    public void testError_Fetch_Employee_Single_DB_Access_Fault() {
        ProjEmployee mockCriteria = new ProjEmployee();
        mockCriteria.setEmpId(ProjectTrackerMockDataFactory.TEST_EMPLOYEE_ID);
        try {
            when(this.mockPersistenceClient.retrieveList(eq(mockCriteria)))
                    .thenThrow(new DatabaseException("A database error occurred"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Fetch all employee case setup failed");
        }
        
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        try {
            api.getEmployee(ProjectTrackerMockDataFactory.TEST_EMPLOYEE_ID);
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof EmployeeApiException);
            Assert.assertTrue(e.getCause() instanceof EmployeeApiException);
            Assert.assertTrue(e.getCause().getCause() instanceof EmployeeDaoException);
            Assert.assertTrue(e.getCause().getCause().getCause() instanceof DatabaseException);
            e.printStackTrace();
        }
    }
    
    @Test
    public void testError_Fetch_EmployeeExt_Single_DB_Access_Fault() {
        VwEmployeeExt mockCriteria = new VwEmployeeExt();
        mockCriteria.setEmployeeId(ProjectTrackerMockDataFactory.TEST_EMPLOYEE_ID);
        try {
            when(this.mockPersistenceClient.retrieveList(eq(mockCriteria)))
                    .thenThrow(new DatabaseException("A database error occurred"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Fetch single extended employee case setup failed");
        }
        
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        EmployeeDto criteria = EmployeeObjectFactory.createEmployeeExtendedDtoInstance(null);
        criteria.setEmployeeId(ProjectTrackerMockDataFactory.TEST_EMPLOYEE_ID);
        try {
            api.getEmployeeExt(criteria);
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof EmployeeApiException);
            Assert.assertTrue(e.getCause() instanceof EmployeeDaoException);
            Assert.assertTrue(e.getCause().getCause() instanceof DatabaseException);
            e.printStackTrace();
        }
    }
    
    @Test
    public void testSuccess_Fetch_Employee_Titles() {
        ProjEmployeeTitle mockCriteria = new ProjEmployeeTitle();
        try {
            when(this.mockPersistenceClient.retrieveList(eq(mockCriteria))).thenReturn(this.mockEmployeeTitleFetchMultiple);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Fetch all employee titles case setup failed");
        }
        
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        List<EmployeeTitleDto> results = null;
        try {
            results = api.getEmployeeTitles();
        } catch (EmployeeApiException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(results);
        Assert.assertEquals(5, results.size());
        for (int ndx = 0; ndx < results.size(); ndx++) {
            EmployeeTitleDto dto = results.get(ndx);
            Assert.assertEquals((101 + ndx), dto.getEmployeeTitleId());
            Assert.assertEquals(("Employee Title " + (1 + ndx)), dto.getEmployeeTitleDescription());
        }
    }
    
    @Test
    public void testError_Fetch_Employee_Titles_DB_Access_Fault() {
        ProjEmployeeTitle mockCriteria = new ProjEmployeeTitle();
        try {
            when(this.mockPersistenceClient.retrieveList(eq(mockCriteria)))
                    .thenThrow(new DatabaseException("A database error occurred"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Fetch all employee titles case setup failed");
        }
        
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        try {
            api.getEmployeeTitles();
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof EmployeeApiException);
            Assert.assertTrue(e.getCause() instanceof EmployeeDaoException);
            Assert.assertTrue(e.getCause().getCause() instanceof DatabaseException);
            e.printStackTrace();
        }
    }
    
    @Test
    public void testSuccess_Fetch_Employee_Types() {
        ProjEmployeeType mockCriteria = new ProjEmployeeType();
        try {
            when(this.mockPersistenceClient.retrieveList(eq(mockCriteria))).thenReturn(this.mockEmployeeTypeFetchMultiple);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Fetch all employee types case setup failed");
        }
        
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        List<EmployeeTypeDto> results = null;
        try {
            results = api.getEmployeeTypes();
        } catch (EmployeeApiException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(results);
        Assert.assertEquals(3, results.size());
        for (int ndx = 0; ndx < results.size(); ndx++) {
            EmployeeTypeDto dto = results.get(ndx);
            Assert.assertEquals((201 + ndx), dto.getEmployeeTypeId());
            Assert.assertEquals(("Employee Type " + (1 + ndx)), dto.getEmployeeTypeDescription());
        }
    }
    
    @Test
    public void testError_Fetch_Employee_Types_DB_Access_Fault() {
        ProjEmployeeType mockCriteria = new ProjEmployeeType();
        try {
            when(this.mockPersistenceClient.retrieveList(eq(mockCriteria)))
                .thenThrow(new DatabaseException("A database error occurred"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Fetch all employee types case setup failed");
        }
        
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        try {
            api.getEmployeeTypes();
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof EmployeeApiException);
            Assert.assertTrue(e.getCause() instanceof EmployeeDaoException);
            Assert.assertTrue(e.getCause().getCause() instanceof DatabaseException);
            e.printStackTrace();
        }
    }
    
    @Test
    public void testSuccess_Fetch_Managers() {
        // Set all mock employe data objects to be managers
        for (ProjEmployee emp : this.mockEmployeeFetchMultiple) {
            emp.setIsManager(ProjectTrackerApiConst.EMPLOYEE_MANAGER_FLAG);
        }
        // Stub all employee fetch.
        ProjEmployee mockCriteria = new ProjEmployee();
        mockCriteria.setIsManager(ProjectTrackerApiConst.EMPLOYEE_MANAGER_FLAG);
        try {
            when(this.mockPersistenceClient.retrieveList(eq(mockCriteria))).thenReturn(this.mockEmployeeFetchMultiple);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Fetch all employee case setup failed");
        }
        
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        List<EmployeeDto> results = null;
        try {
            results = api.getManagers();
        } catch (EmployeeApiException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(results);
        Assert.assertEquals(5, results.size());
        for (int ndx = 0; ndx < results.size(); ndx++) {
            EmployeeDto obj = results.get(ndx);
            Assert.assertEquals(obj.getEmployeeId(), (ProjectTrackerMockDataFactory.TEST_EMPLOYEE_ID + ndx));
            Assert.assertEquals(obj.getManagerId(), ProjectTrackerMockDataFactory.TEST_MANAGER_ID);
            Assert.assertEquals(obj.getEmployeeTitleId(), (ProjectTrackerMockDataFactory.TEST_EMPLOYEE_TITLE_ID + ndx));
            Assert.assertEquals(obj.getLoginId(), (ProjectTrackerMockDataFactory.TEST_LOGIN_ID + ndx));
            Assert.assertEquals(obj.getIsManager(), 1);
            int startYear = 2010 + ndx;
            Assert.assertEquals(RMT2Date.stringToDate(startYear + "-01-01"), obj.getStartDate());
            if (obj.getTerminationDate() != null) {
                Assert.assertEquals(obj.getEmployeeTypeId(), 202);
                Assert.assertEquals(RMT2Date.stringToDate("2018-01-01"), obj.getTerminationDate());
            }
            else {
                Assert.assertEquals(obj.getEmployeeTypeId(), 201);
            }
            int nameSeed = 1 + ndx;
            Assert.assertEquals(obj.getEmployeeFirstname(), "first_name_" + nameSeed);
            Assert.assertEquals(obj.getEmployeeLastname(), "last_name_" + nameSeed);
            Assert.assertEquals(("111-11-500" + ndx), obj.getSsn());
            Assert.assertEquals(ProjectTrackerMockDataFactory.TEST_COMPANY_NAME, obj.getEmployeeCompanyName());
            Assert.assertEquals(EMP_PROJ_COUNT, obj.getProjectCount());
        }
    }
    
    @Test
    public void testError_Fetch_Managers_DB_Access_Fault() {
        ProjEmployee mockCriteria = new ProjEmployee();
        mockCriteria.setIsManager(ProjectTrackerApiConst.EMPLOYEE_MANAGER_FLAG);
        try {
            when(this.mockPersistenceClient.retrieveList(eq(mockCriteria)))
                   .thenThrow(new DatabaseException("A database error occurred"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Fetch all employee case setup failed");
        }
        
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        try {
            api.getManagers();
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof EmployeeApiException);
            Assert.assertTrue(e.getCause() instanceof EmployeeDaoException);
            Assert.assertTrue(e.getCause().getCause() instanceof DatabaseException);
            e.printStackTrace();
        }
    }
    
    @Test
    public void testSuccess_Fetch_Project_Employee_List() {
        VwEmployeeProjects mockCriteria = new VwEmployeeProjects();
        mockCriteria.setBusinessId(ProjectTrackerMockDataFactory.TEST_BUSINESS_ID);
        try {
            when(this.mockPersistenceClient.retrieveList(eq(mockCriteria))).thenReturn(this.mockVwEmployeeProjectsFetchMultiple);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Fetch all employee projects case setup failed");
        }
        
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        VwEmployeeProjects vep = new VwEmployeeProjects();
        ProjectEmployeeDto criteria = ProjectObjectFactory.createEmployeeProjectDtoInstance(vep);
        criteria.setBusinessId(ProjectTrackerMockDataFactory.TEST_BUSINESS_ID);
        List<ProjectEmployeeDto> results = null;
        try {
            results = api.getProjectEmployee(criteria);
        } catch (EmployeeApiException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(results);
        Assert.assertEquals(5, results.size());
        for (int ndx = 0; ndx < results.size(); ndx++) {
            ProjectEmployeeDto obj = results.get(ndx);
            Assert.assertEquals(obj.getClientId(), (ProjectTrackerMockDataFactory.TEST_CLIENT_ID + ndx));
            Assert.assertEquals(obj.getClientName(), (ProjectTrackerMockDataFactory.TEST_CLIENT_ID + ndx) + " Company");
            Assert.assertEquals(obj.getProjId(), (ProjectTrackerMockDataFactory.TEST_PROJ_ID + ndx));
            Assert.assertEquals(ProjectTrackerMockDataFactory.TEST_EMP_PROJ_ID + ndx, obj.getEmpProjId());
            Assert.assertEquals(ProjectTrackerMockDataFactory.TEST_EMPLOYEE_ID, obj.getEmpId());
            Assert.assertEquals(ProjectTrackerMockDataFactory.TEST_BUSINESS_ID, obj.getBusinessId());
            Assert.assertEquals(obj.getProjectDescription(), "Project 222" + ndx);
            Assert.assertEquals(RMT2Date.stringToDate("2018-01-01"), obj.getProjectEffectiveDate());
            Assert.assertEquals(RMT2Date.stringToDate("2018-02-01"), obj.getProjectEndDate());
            Assert.assertEquals(RMT2Date.stringToDate("2018-01-01"), obj.getProjEmpEffectiveDate());
            Assert.assertEquals(RMT2Date.stringToDate("2018-02-01"), obj.getProjEmpEndDate());
        }
    }
    
    @Test
    public void testError_Fetch_Project_Employee_List_DB_Access_Fault() {
        VwEmployeeProjects mockCriteria = new VwEmployeeProjects();
        mockCriteria.setBusinessId(ProjectTrackerMockDataFactory.TEST_BUSINESS_ID);
        try {
            when(this.mockPersistenceClient.retrieveList(eq(mockCriteria)))
                   .thenThrow(new DatabaseException("A database error occurred"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Fetch all employee projects case setup failed");
        }
        
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        VwEmployeeProjects vep = new VwEmployeeProjects();
        ProjectEmployeeDto criteria = ProjectObjectFactory.createEmployeeProjectDtoInstance(vep);
        criteria.setBusinessId(ProjectTrackerMockDataFactory.TEST_BUSINESS_ID);
        try {
            api.getProjectEmployee(criteria);
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof EmployeeApiException);
            Assert.assertTrue(e.getCause() instanceof EmployeeDaoException);
            Assert.assertTrue(e.getCause().getCause() instanceof DatabaseException);
            e.printStackTrace();
        }
    }
    
    @Test
    public void testValidation_Fetch_Project_Employee_List_Null_Criteria() {
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        ProjectEmployeeDto criteria = null;
        try {
            api.getProjectEmployee(criteria);
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof InvalidDataException);
            e.printStackTrace();
        }
    }
    
    @Test
    public void testSuccess_Fetch_Project_Employee_Single() {
        VwEmployeeProjects mockCriteria = new VwEmployeeProjects();
        mockCriteria.setEmpProjId(ProjectTrackerMockDataFactory.TEST_EMP_PROJ_ID);
        try {
            when(this.mockPersistenceClient.retrieveList(eq(mockCriteria))).thenReturn(this.mockVwEmployeeProjectsFetchSingle);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Fetch single employee projects case setup failed");
        }
        
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        ProjectEmployeeDto results = null;
        try {
            results = api.getProjectEmployee(ProjectTrackerMockDataFactory.TEST_EMP_PROJ_ID);
        } catch (EmployeeApiException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(results);
        Assert.assertEquals(results.getClientId(), ProjectTrackerMockDataFactory.TEST_CLIENT_ID);
        Assert.assertEquals(results.getClientName(), ProjectTrackerMockDataFactory.TEST_CLIENT_ID + " Company");
        Assert.assertEquals(results.getProjId(), ProjectTrackerMockDataFactory.TEST_PROJ_ID);
        Assert.assertEquals(ProjectTrackerMockDataFactory.TEST_EMP_PROJ_ID, results.getEmpProjId());
        Assert.assertEquals(ProjectTrackerMockDataFactory.TEST_EMPLOYEE_ID, results.getEmpId());
        Assert.assertEquals(ProjectTrackerMockDataFactory.TEST_BUSINESS_ID, results.getBusinessId());
        Assert.assertEquals(results.getProjectDescription(), "Project 2220");
        Assert.assertEquals(RMT2Date.stringToDate("2018-01-01"),
                results.getProjectEffectiveDate());
        Assert.assertEquals(RMT2Date.stringToDate("2018-02-01"),
                results.getProjectEndDate());
        Assert.assertEquals(RMT2Date.stringToDate("2018-01-01"),
                results.getProjEmpEffectiveDate());
        Assert.assertEquals(RMT2Date.stringToDate("2018-02-01"), results.getProjEmpEndDate());
    }
    
    @Test
    public void testError_Fetch_Project_Employee_Single_DB_Access_Fault() {
        VwEmployeeProjects mockCriteria = new VwEmployeeProjects();
        mockCriteria.setEmpProjId(ProjectTrackerMockDataFactory.TEST_EMP_PROJ_ID);
        try {
            when(this.mockPersistenceClient.retrieveList(eq(mockCriteria)))
                  .thenThrow(new DatabaseException("A database error occurred"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Fetch single employee projects case setup failed");
        }
        
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        try {
            api.getProjectEmployee(ProjectTrackerMockDataFactory.TEST_EMP_PROJ_ID);
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof EmployeeApiException);
            Assert.assertTrue(e.getCause() instanceof EmployeeDaoException);
            Assert.assertTrue(e.getCause().getCause() instanceof DatabaseException);
            e.printStackTrace();
        }
    }
    
    @Test
    public void testValidation_Fetch_Project_Employee_Single_Null_Key() {
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        Integer empProjId = null;
        try {
            api.getProjectEmployee(empProjId);
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof InvalidDataException);
            e.printStackTrace();
        }
    }
    
    @Test
    public void testValidation_Fetch_Project_Employee_Single_Zero_Key() {
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        Integer empProjId = 0;
        try {
            api.getProjectEmployee(empProjId);
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof InvalidDataException);
            e.printStackTrace();
        }
    }
    
    @Test
    public void testValidation_Fetch_Project_Employee_Single_Negative_Key() {
        EmployeeApi api = EmployeeApiFactory.createApi(this.mockDaoClient);
        Integer empProjId = -1000;
        try {
            api.getProjectEmployee(empProjId);
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof InvalidDataException);
            e.printStackTrace();
        }
    }
   }