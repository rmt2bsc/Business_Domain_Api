package org.rmt2.api.entity.adapter;

import org.dao.mapping.orm.rmt2.ProjEvent;
import org.dao.mapping.orm.rmt2.ProjProjectTask;
import org.dao.mapping.orm.rmt2.ProjTimesheet;
import org.dao.mapping.orm.rmt2.ProjTimesheetHist;
import org.dao.mapping.orm.rmt2.VwTimesheetHours;
import org.dao.mapping.orm.rmt2.VwTimesheetList;
import org.dao.timesheet.TimesheetConst;
import org.dto.EventDto;
import org.dto.ProjectTaskDto;
import org.dto.TimesheetDto;
import org.dto.TimesheetHistDto;
import org.dto.TimesheetHoursDto;
import org.dto.adapter.orm.ProjectObjectFactory;
import org.dto.adapter.orm.TimesheetObjectFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rmt2.api.ProjectTrackerMockDataFactory;

import com.api.util.RMT2Date;

/**
 * Test adapters pertaining to the Timesheet module.
 * 
 * @author roy.terrell
 *
 */
public class TimesheetModuleAdapterTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testOrmProjTimesheet() {
        ProjTimesheet o = ProjectTrackerMockDataFactory.createMockOrmProjTimesheet(111, 1110, 1234, 2220, "INVREF1230",
                        "2018-01-01", "2018-01-07", "ExtReNo1000");
        TimesheetDto dto = TimesheetObjectFactory.createTimesheetDtoInstance(o);
        
        Assert.assertEquals(111, dto.getTimesheetId());
        Assert.assertEquals(1110, dto.getClientId());
        Assert.assertEquals(1234, dto.getProjId());
        Assert.assertEquals(2220, dto.getEmpId());
        Assert.assertEquals("INVREF1230", dto.getInvoiceRefNo());
        Assert.assertEquals(RMT2Date.stringToDate("2018-01-01"), dto.getBeginPeriod());
        Assert.assertEquals(RMT2Date.stringToDate("2018-01-07"), dto.getEndPeriod());
        Assert.assertEquals("ExtReNo1000", dto.getExtRef());
        Assert.assertEquals("Comments" + dto.getTimesheetId(), dto.getComments());
        Assert.assertEquals("0000000111", dto.getDisplayValue());
        Assert.assertEquals(dto.getTimesheetId(), dto.getDocumentId());
        Assert.assertEquals(RMT2Date.stringToDate("2018-01-01"), dto.getDateCreated());
        Assert.assertEquals(RMT2Date.stringToDate("2018-01-07"), dto.getDateUpdated());
        Assert.assertEquals("testuser", dto.getUpdateUserId());
        Assert.assertEquals("1.2.3.4", dto.getIpCreated());
        Assert.assertEquals("1.2.3.4", dto.getIpUpdated());
    }
    
    @Test
    public void testOrmVwTimesheetList() {
        VwTimesheetList o = ProjectTrackerMockDataFactory.createMockOrmVwTimesheetList(111, 1110, 1234, 2220,
                        "INVREF1230", "2018-01-01", "2018-01-07", "ExtReNo1000",
                        3330, "DRAFT", "ACCT-111", 40, 0, 70.00, 80.00);
        TimesheetDto dto = TimesheetObjectFactory.createTimesheetExtendedDtoInstance(o);
        
        Assert.assertEquals(111, dto.getTimesheetId());
        Assert.assertEquals(1110, dto.getClientId());
        Assert.assertEquals(1234, dto.getProjId());
        Assert.assertEquals(2220, dto.getEmpId());
        Assert.assertEquals("INVREF1230", dto.getInvoiceRefNo());
        Assert.assertEquals(RMT2Date.stringToDate("2018-01-01"), dto.getBeginPeriod());
        Assert.assertEquals(RMT2Date.stringToDate("2018-01-07"), dto.getEndPeriod());
        Assert.assertEquals("ExtReNo1000", dto.getExtRef());
        Assert.assertEquals("Comments" + dto.getTimesheetId(), dto.getComments());
        Assert.assertEquals("0000000111", dto.getDisplayValue());
        Assert.assertEquals(dto.getTimesheetId(), dto.getDocumentId());
        Assert.assertNull(dto.getDateCreated());
        Assert.assertNull(dto.getDateUpdated());
        Assert.assertNull(dto.getUpdateUserId());
        Assert.assertNull(dto.getIpCreated());
        Assert.assertNull(dto.getIpUpdated());
        Assert.assertEquals(3330, dto.getEmployeeManagerId());
        Assert.assertEquals("DRAFT", dto.getStatusName());
        Assert.assertEquals("ACCT-111", dto.getClientAccountNo());
        Assert.assertEquals(40, dto.getBillHrs(), 0);
        Assert.assertEquals(0, dto.getNonBillHrs(), 0);
        Assert.assertEquals(70.00, dto.getEmployeeHourlyRate(), 0);
        Assert.assertEquals(80.00, dto.getEmployeeHourlyOverRate(), 0);
        Assert.assertEquals(TimesheetConst.STATUS_DRAFT, dto.getStatusId());
        Assert.assertEquals(dto.getStatusName() + "Description", dto.getStatusDescription());
        Assert.assertEquals(RMT2Date.stringToDate("2018-01-01"), dto.getStatusEffectiveDate());
        Assert.assertEquals(RMT2Date.stringToDate("2018-01-07"), dto.getStatusEndDate());
        Assert.assertEquals(222, dto.getEmployeeTypeId());
        Assert.assertEquals(5555, dto.getStatusHistId());
        Assert.assertEquals(dto.getEmployeeLastname() + ", " + dto.getEmployeeFirstname(), dto.getEmployeeFullName());
    }
    
    @Test
    public void testOrmProjProjectTask() {
        ProjProjectTask o = ProjectTrackerMockDataFactory.createMockOrmProjProjectTask(1000, 2000, 3000, 4000);
        ProjectTaskDto dto = ProjectObjectFactory.createProjectTaskDtoInstance(o);
        
        Assert.assertEquals(1000, dto.getProjectTaskId());
        Assert.assertEquals(2000, dto.getTaskId());
        Assert.assertEquals(3000, dto.getTimesheetId());
        Assert.assertEquals(4000, dto.getProjId());
    }
    
    @Test
    public void testOrmVwTimesheetHours() {
        VwTimesheetHours o = ProjectTrackerMockDataFactory.createMockOrmVwTimesheetHours(111, 1110, 4440, 2220,
                1112220, 123401, 444441, "2018-01-07", 8, true);
        TimesheetHoursDto dto = TimesheetObjectFactory.createTimesheetHoursDtoInstance(o);
        
        Assert.assertEquals(111, dto.getTimesheetId());
        Assert.assertEquals(1110, dto.getClientId());
        Assert.assertEquals(4440, dto.getProjId());
        Assert.assertEquals(2220, dto.getEmpId());
        Assert.assertEquals(1112220, dto.getTaskId());
        Assert.assertEquals(123401, dto.getEventId());
        Assert.assertEquals(444441, dto.getProjectTaskId());
        Assert.assertEquals(8, dto.getEventHours(), 0);
        Assert.assertEquals(1, dto.getTaskBillable());
        
        // Verify derived properties
        Assert.assertEquals(RMT2Date.stringToDate("2018-01-07"), dto.getEventDate());
        Assert.assertEquals(RMT2Date.stringToDate("2018-01-07"), dto.getBeginPeriod());
        Assert.assertEquals(RMT2Date.stringToDate("2018-01-07"), dto.getProjectEffectiveDate());
        Assert.assertEquals(RMT2Date.stringToDate("2018-01-07"), dto.getDateCreated());
        Assert.assertEquals(RMT2Date.stringToDate("2018-01-08"), dto.getEndPeriod());
        Assert.assertEquals(RMT2Date.stringToDate("2018-01-08"), dto.getProjectEndDate());
        Assert.assertEquals("InvoiceRefNo" + o.getTimesheetId(), dto.getInvoiceRefNo());
        Assert.assertEquals("ExtRefNo" + o.getTimesheetId(), dto.getExtRef());
        Assert.assertEquals(dto.getTimesheetId(), dto.getDocumentId());
        Assert.assertEquals("0000000111", dto.getDisplayValue());
        Assert.assertEquals("ProjectName" + o.getProjectId(), dto.getProjectDescription());
        Assert.assertEquals("TaskName" + o.getTaskId(), dto.getTaskDescription());
    }
    
    @Test
    public void testOrmProjTimesheetHist() {
        ProjTimesheetHist o = ProjectTrackerMockDataFactory.createMockOrmProjTimesheetHist(
                ProjectTrackerMockDataFactory.TEST_TIMESHEET_HIST_ID,
                ProjectTrackerMockDataFactory.TEST_TIMESHEET_ID,
                TimesheetConst.STATUS_DRAFT, "2018-01-01", "2018-01-10");
        TimesheetHistDto dto = TimesheetObjectFactory.createTimesheetHistoryDtoInstance(o);
        
        Assert.assertEquals(ProjectTrackerMockDataFactory.TEST_TIMESHEET_HIST_ID, dto.getStatusHistId());
        Assert.assertEquals(ProjectTrackerMockDataFactory.TEST_TIMESHEET_ID, dto.getTimesheetId());
        Assert.assertEquals(TimesheetConst.STATUS_DRAFT, dto.getStatusId());
        Assert.assertEquals(RMT2Date.stringToDate("2018-01-01"), dto.getStatusEffectiveDate());
        Assert.assertEquals(RMT2Date.stringToDate("2018-01-10"), dto.getStatusEndDate());
        Assert.assertEquals("testuser", dto.getUpdateUserId());
        Assert.assertEquals("1.2.3.4", dto.getIpCreated());
        Assert.assertEquals("1.2.3.4", dto.getIpUpdated());
    }
    
    @Test
    public void testOrmProjEvent() {
        ProjEvent o =  ProjectTrackerMockDataFactory.createMockOrmProjEvent(123401, 444441, "2018-01-01", 8);
        EventDto dto = ProjectObjectFactory.createEventDtoInstance(o);
        
        Assert.assertEquals(123401, dto.getEventId());
        Assert.assertEquals(444441, dto.getProjectTaskId());
        Assert.assertEquals(8, dto.getEventHours(), 0);
        Assert.assertEquals(RMT2Date.stringToDate("2018-01-01"), dto.getEventDate());
        
        // Test Setters
        dto = ProjectObjectFactory.createEventDtoInstance(null);
        dto.setProjectTaskId(444441);
        dto.setEventId(123401);
        dto.setEventDate(RMT2Date.stringToDate("2018-01-01"));
        dto.setEventHours(8);
        Assert.assertEquals(123401, dto.getEventId());
        Assert.assertEquals(444441, dto.getProjectTaskId());
        Assert.assertEquals(8, dto.getEventHours(), 0);
        Assert.assertEquals(RMT2Date.stringToDate("2018-01-01"), dto.getEventDate());
    }
}
