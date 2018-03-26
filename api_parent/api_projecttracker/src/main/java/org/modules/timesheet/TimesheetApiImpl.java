package org.modules.timesheet;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dao.timesheet.TimesheetConst;
import org.dao.timesheet.TimesheetDao;
import org.dao.timesheet.TimesheetDaoException;
import org.dao.timesheet.TimesheetDaoFactory;
import org.dto.ClientDto;
import org.dto.EmployeeDto;
import org.dto.EventDto;
import org.dto.ProjectEventDto;
import org.dto.ProjectTaskDto;
import org.dto.TimesheetDto;
import org.dto.TimesheetHistDto;
import org.dto.TimesheetHoursDto;
import org.dto.adapter.orm.ProjectObjectFactory;
import org.dto.adapter.orm.TimesheetObjectFactory;
import org.modules.admin.ProjectAdminApi;
import org.modules.admin.ProjectAdminApiException;
import org.modules.admin.ProjectAdminApiFactory;
import org.modules.employee.EmployeeApi;
import org.modules.employee.EmployeeApiException;
import org.modules.employee.EmployeeApiFactory;
import org.modules.timesheet.invoice.InvoiceTimesheetApiException;

import com.InvalidDataException;
import com.api.config.AppPropertyPool;
import com.api.foundation.AbstractTransactionApiImpl;
import com.api.messaging.email.EmailMessageBean;
import com.api.persistence.DaoClient;
import com.util.RMT2String;
import com.util.assistants.Verifier;
import com.util.assistants.VerifyException;

/**
 * Implementation of TimesheetApi that manages an employee's timesheet
 * activities.
 * 
 * @author Roy Terrell
 * 
 */
class TimesheetApiImpl extends AbstractTransactionApiImpl implements TimesheetApi {

    private static final String PARM_NAME_TIMESHEET_ID = "Timesheet Id";
    
    private static final String PARM_NAME_CLIENT_ID = "Client Id";
    
    private static final String PARM_NAME_EMPLOYEE_ID = "Employee Id";
    
    private static final Logger logger = Logger.getLogger(TimesheetApiImpl.class);

    private TimesheetDaoFactory daoFact;

    private TimesheetDao dao;

    private int currentProjectId;

    private TimesheetDto ts;

    private Map<ProjectTaskDto, List<EventDto>> tsHours;

    /**
     * 
     */
    protected TimesheetApiImpl() {
        super();
        this.dao = this.daoFact.createRmt2OrmDao();
        this.setSharedDao(this.dao);
        return;
    }

    /**
     * 
     * @param appName
     */
    protected TimesheetApiImpl(String appName) {
        super();
        this.dao = this.daoFact.createRmt2OrmDao(appName);
        this.setSharedDao(this.dao);
        return;
    }

    /**
     * @param dao
     */
    protected TimesheetApiImpl(DaoClient dao) {
        super(dao);
        this.dao = this.daoFact.createRmt2OrmDao(this.getSharedDao());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.RMT2Base#init()
     */
    @Override
    public void init() {
        super.init();
        this.daoFact = new TimesheetDaoFactory();
        return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modules.timesheet.TimesheetApi#get(int)
     */
    @Override
    public TimesheetDto get(Integer timesheetId) throws TimesheetApiException {
        this.validateNumericParam(timesheetId, PARM_NAME_TIMESHEET_ID);
        
        TimesheetDto criteria = TimesheetObjectFactory.createTimesheetDtoInstance(null);
        criteria.setTimesheetId(timesheetId);
        List<TimesheetDto> results = null;

        StringBuilder buf = new StringBuilder();
        try {
            results = this.dao.fetch(criteria);
            if (results == null) {
                return null;
            }
        } catch (TimesheetDaoException e) {
            buf.append("Database error occurred retrieving single timesheet by id, "
                    + timesheetId);
            this.msg = buf.toString();
            logger.error(this.msg);
            throw new TimesheetApiException(this.msg, e);
        }

        if (results.size() > 1) {
            buf.append("Error: Query method is expecting a single timesheet object to be returned using timesheet id, ");
            buf.append(timesheetId);
            buf.append(".  Instead ");
            buf.append(results.size());
            buf.append("  were returned.");
            this.msg = buf.toString();
            logger.error(this.msg);
            throw new TimesheetApiException(this.msg);
        }
        return results.get(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modules.timesheet.TimesheetApi#get(java.lang.String)
     */
    @Override
    public List<TimesheetDto> get(TimesheetDto criteria) throws TimesheetApiException {
        List<TimesheetDto> results = null;
        StringBuilder buf = new StringBuilder();
        try {
            results = this.dao.fetch(criteria);
            if (results == null) {
                return null;
            }
        } catch (TimesheetDaoException e) {
            buf.append("Database error occurred retrieving timesheet(s) using customer criteria: " + criteria);
            this.msg = buf.toString();
            throw new TimesheetApiException(this.msg, e);
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modules.timesheet.TimesheetApi#getExt(int)
     */
    @Override
    public TimesheetDto getExt(Integer timesheetId) throws TimesheetApiException {
        this.validateNumericParam(timesheetId, PARM_NAME_TIMESHEET_ID);
        
        TimesheetDto criteria = TimesheetObjectFactory.createTimesheetDtoInstance(null);
        criteria.setTimesheetId(timesheetId);
        List<TimesheetDto> results = null;

        StringBuilder buf = new StringBuilder();
        try {
            results = this.dao.fetchExt(criteria);
            if (results == null) {
                return null;
            }
        } catch (TimesheetDaoException e) {
            buf.append("Database error occurred retrieving single extended timesheet by id, "
                    + timesheetId);
            this.msg = buf.toString();
            logger.error(this.msg);
            throw new TimesheetApiException(this.msg, e);
        }

        if (results.size() > 1) {
            buf.append("Error: Query method is expecting a single extended timesheet object to be returned using timesheet id, ");
            buf.append(timesheetId);
            buf.append(".  Instead ");
            buf.append(results.size());
            buf.append("  were returned.");
            this.msg = buf.toString();
            logger.error(this.msg);
            throw new TimesheetApiException(this.msg);
        }
        return results.get(0);
    }

//    /*
//     * (non-Javadoc)
//     * 
//     * @see org.modules.timesheet.TimesheetApi#getByClient(int)
//     */
//    @Override
//    public List<TimesheetDto> getByClient(Integer clientId) throws TimesheetApiException {
//        this.validateNumericParam(clientId, PARM_NAME_CLIENT_ID);
//        
//        TimesheetDto criteria = TimesheetObjectFactory.createTimesheetDtoInstance(null);
//        criteria.setClientId(clientId);
//        List<TimesheetDto> results = null;
//        StringBuilder buf = new StringBuilder();
//        try {
//            results = this.dao.fetch(criteria);
//            if (results == null) {
//                return null;
//            }
//        } catch (TimesheetDaoException e) {
//            buf.append("Database error occurred retrieving timesheet(s) by client id: " + clientId);
//            this.msg = buf.toString();
//            logger.error(this.msg);
//            throw new TimesheetApiException(this.msg, e);
//        }
//        return results;
//    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modules.timesheet.TimesheetApi#getClientApproved(int)
     */
    @Override
    public List<TimesheetDto> getClientApproved(Integer clientId) throws TimesheetApiException {
        this.validateNumericParam(clientId, PARM_NAME_CLIENT_ID);
        
        TimesheetDto criteria = TimesheetObjectFactory.createTimesheetDtoInstance(null);
        criteria.setClientId(clientId);
        criteria.setStatusId(TimesheetConst.STATUS_APPROVED);
        List<TimesheetDto> results = null;
        StringBuilder buf = new StringBuilder();
        try {
            results = this.dao.fetchExt(criteria);
            if (results == null) {
                return null;
            }
        } catch (TimesheetDaoException e) {
            buf.append("Database error occurred retrieving approved extended timesheet(s) by client id: "
                    + clientId);
            this.msg = buf.toString();
            logger.error(this.msg);
            throw new TimesheetApiException(this.msg, e);
        }
        return results;
    }

//    /*
//     * (non-Javadoc)
//     * 
//     * @see org.modules.timesheet.TimesheetApi#getByEmployee(int)
//     */
//    @Override
//    public List<TimesheetDto> getByEmployee(Integer employeeId)  throws TimesheetApiException {
//        this.validateNumericParam(employeeId, PARM_NAME_EMPLOYEE_ID);
//        
//        TimesheetDto criteria = TimesheetObjectFactory.createTimesheetDtoInstance(null);
//        criteria.setEmpId(employeeId);
//        List<TimesheetDto> results = null;
//        StringBuilder buf = new StringBuilder();
//        try {
//            results = this.dao.fetch(criteria);
//            if (results == null) {
//                return null;
//            }
//        } catch (TimesheetDaoException e) {
//            buf.append("Database error occurred retrieving timesheet(s) by employee id: " + employeeId);
//            this.msg = buf.toString();
//            logger.error(this.msg);
//            throw new TimesheetApiException(this.msg, e);
//        }
//        return results;
//    }

//    /*
//     * (non-Javadoc)
//     * 
//     * @see
//     * org.modules.timesheet.TimesheetApi#getByEmployee(java.lang.Integer[],
//     * int)
//     */
//    @Override
//    public List<TimesheetDto> getByStatus(Integer[] timesheetId, Integer statusId) throws TimesheetApiException {
//        this.validateNumericParam(statusId, "Status Id");
//        
//        TimesheetDto criteria = TimesheetObjectFactory.createTimesheetDtoInstance(null);
//        List<Integer> timesheetIdList = Arrays.asList(timesheetId);
//        criteria.setTimesheetIdList(timesheetIdList);
//        criteria.setStatusId(statusId);
//        List<TimesheetDto> results = null;
//        StringBuilder buf = new StringBuilder();
//        try {
//            results = this.dao.fetch(criteria);
//            if (results == null) {
//                return null;
//            }
//        } catch (TimesheetDaoException e) {
//            buf.append("Database error occurred retrieving timesheet(s) by status id: " + statusId);
//            this.msg = buf.toString();
//            logger.error(this.msg);
//            throw new TimesheetApiException(this.msg, e);
//        }
//        return results;
//    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modules.timesheet.TimesheetApi#getProjectTaskByTimesheet(int)
     */
    @Override
    public List<ProjectTaskDto> getProjectTaskByTimesheet(Integer timesheetId) throws TimesheetApiException {
        this.validateNumericParam(timesheetId, PARM_NAME_TIMESHEET_ID);
        
        ProjectTaskDto criteria = ProjectObjectFactory.createProjectTaskDtoInstance(null);
        criteria.setTimesheetId(timesheetId);
        List<ProjectTaskDto> results = null;
        StringBuilder buf = new StringBuilder();
        try {
            results = this.dao.fetchProjectTask(criteria);
            if (results == null) {
                return null;
            }
        } catch (TimesheetDaoException e) {
            buf.append("Database error occurred retrieving timesheet project/task(s) by timesheet id: "
                    + timesheetId);
            this.msg = buf.toString();
            logger.error(this.msg);
            throw new TimesheetApiException(this.msg, e);
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modules.timesheet.TimesheetApi#getProjectTaskExtByTimesheet(int)
     */
    @Override
    public List<ProjectTaskDto> getProjectTaskExtByTimesheet(Integer timesheetId) throws TimesheetApiException {
        this.validateNumericParam(timesheetId, PARM_NAME_TIMESHEET_ID);
        
        ProjectTaskDto criteria = ProjectObjectFactory.createProjectTaskExtendedDtoInstance(null);
        criteria.setTimesheetId(timesheetId);
        List<ProjectTaskDto> results = null;
        StringBuilder buf = new StringBuilder();
        try {
            results = this.dao.fetchProjectTaskExt(criteria);
            if (results == null) {
                return null;
            }
        } catch (TimesheetDaoException e) {
            buf.append("Database error occurred retrieving extended timesheet project/task(s) by timesheet id: "
                    + timesheetId);
            this.msg = buf.toString();
            logger.error(this.msg);
            throw new TimesheetApiException(this.msg, e);
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modules.timesheet.TimesheetApi#getEventByTimesheet(int)
     */
    @Override
    public List<ProjectEventDto> getEventByTimesheet(Integer timesheetId) throws TimesheetApiException {
        this.validateNumericParam(timesheetId, PARM_NAME_TIMESHEET_ID);
        
        ProjectEventDto criteria = ProjectObjectFactory.createProjectEventDtoInstance(null);
        criteria.setTimesheetId(timesheetId);
        List<ProjectEventDto> results = null;
        StringBuilder buf = new StringBuilder();
        try {
            results = this.dao.fetchEvent(criteria);
            if (results == null) {
                return null;
            }
        } catch (TimesheetDaoException e) {
            buf.append("Database error occurred retrieving timesheet event(s) by timesheet id: "
                    + timesheetId);
            this.msg = buf.toString();
            logger.error(this.msg);
            throw new TimesheetApiException(this.msg, e);
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modules.timesheet.TimesheetApi#getCurrentStatus(int)
     */
    @Override
    public TimesheetHistDto getCurrentStatus(Integer timesheetId) throws TimesheetApiException {
        this.validateNumericParam(timesheetId, PARM_NAME_TIMESHEET_ID);
        
        TimesheetHistDto obj = TimesheetObjectFactory.createTimesheetHistoryDtoInstance(null);
        obj.setTimesheetId(timesheetId);
        obj.setCurrentStatusFlag(true);
        List<TimesheetHistDto> list = this.dao.fetchStatusHistory(obj);
        if (list == null) {
            return null;
        }
        return list.get(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modules.timesheet.TimesheetApi#fetchHours(int)
     */
    @Override
    public List<TimesheetHoursDto> getHours(Integer timesheetId) throws TimesheetApiException {
        this.validateNumericParam(timesheetId, PARM_NAME_TIMESHEET_ID);
        
        TimesheetHoursDto criteria = TimesheetObjectFactory.createTimesheetHoursDtoInstance(null);
        criteria.setTimesheetId(timesheetId);
        List<TimesheetHoursDto> results = null;
        StringBuilder buf = new StringBuilder();
        try {
            results = this.dao.fetchHours(criteria);
            if (results == null) {
                return null;
            }
        } catch (TimesheetDaoException e) {
            buf.append("Database error occurred retrieving timesheet hours by timesheet id: " + timesheetId);
            this.msg = buf.toString();
            logger.error(this.msg);
            throw new TimesheetApiException(this.msg, e);
        }
        return results;
    }

    /**
     * Drives the process of saving the data of a single timesheet.
     * <p>
     * First the base timesheet data is processed followed by the processing of
     * each task's time. This method is also responsible for procesing those
     * tasks that are selected for deletion.
     * <p>
     * Creates a new client record in the event the client does not exist for
     * the tiemsheet's client id.
     * 
     * @param timesheet
     *            an instance of {@link TimesheetDto}
     * @param hours
     *            a Map<{@link ProjectTaskDto}, List<{@link EventDto}>>
     * @return
     * @throws TimesheetApiException
     */
    @Override
    public int updateTimesheet(TimesheetDto timesheet, Map<ProjectTaskDto, List<EventDto>> hours)
            throws TimesheetApiException {

        this.validateTimesheet(timesheet);
        int timesheetId = 0;
        if (timesheet.getTimesheetId() == 0) {
            // Insert timesheet row.
            this.dao.maintainTimesheet(timesheet);
        }

        // An update will be performed regardless. Since the timesheet display
        // value is based on the primary key, create time sheet's display value.
        if (timesheet.getDisplayValue() == null) {
            String displayValue = RMT2String.padInt(timesheet.getTimesheetId(),
                    this.getMaxDisplayValueDigits(), RMT2String.PAD_LEADING);
            timesheet.setDisplayValue(displayValue);
        }
        // Perform database update of timesheet so to capture the display value.
        int rc = 0;
        rc = this.dao.maintainTimesheet(timesheet);
        timesheetId = timesheet.getTimesheetId();

        // Save timesheet hours
        int projId = this.saveTimesheetHours(timesheetId, hours);

        // Set timesheet status to Draft.
        this.changeTimesheetStatus(timesheetId, TimesheetConst.STATUS_DRAFT);

        // if needed, update timesheet header with project id
        if ((timesheet.getProjId() == 0 && timesheet.getTimesheetId() > 0 && projId > 0)
                || (timesheet.getProjId() != projId)) {
            timesheet.setProjId(projId);
            rc = this.dao.maintainTimesheet(timesheet);
            logger.info("Return code of last timesheet update operation: " + rc);
        }
        return timesheetId;
    }

    private int saveTimesheetHours(int timesheetId, Map<ProjectTaskDto, List<EventDto>> hours)
            throws TimesheetApiException {

        ProjectTaskDto pt = null;
        Iterator<ProjectTaskDto> keys = hours.keySet().iterator();
        int projId = 0;
        while (keys.hasNext()) {
            pt = keys.next();
            if (pt.isDeleteFlag()) {
                // Delete project task and its events
                this.deleteProjectTask(pt.getProjectTaskId());
            }
            else {
                // Ensure that the current project id property of the
                // timesheet api is set in order to perform validations
                // against each task.
                if (this.currentProjectId == 0) {
                    // Since this is the first time, all remaining projects
                    // must equal that of the first occurrence.
                    this.currentProjectId = pt.getProjId();
                    projId = pt.getProjId();
                }
                // Apply changes to project task and its events
                ProjectTaskDto obj = ProjectObjectFactory.createProjectTaskDtoInstance(null);
                obj.setProjectTaskId(pt.getProjectTaskId());
                obj.setTaskId(pt.getTaskId());
                obj.setProjId(pt.getProjId());
                obj.setTimesheetId(timesheetId);
                this.updateProjectTask(obj);
                List<EventDto> events = hours.get(pt);
                this.updateEvent(obj.getProjectTaskId(), events);
            } // end else

        } // end while
        return projId;
    }

    /**
     * Get value that indicates the total number of numeircal digits that
     * composes the timesheet's displayValue.
     * 
     * @return Maximum size as an int. Returns 5 if a problem occurred
     *         converting the property value to a number.
     * @throws InvoiceTimesheetApiException
     *             Prooperty file access errors.
     */
    private int getMaxDisplayValueDigits() throws InvoiceTimesheetApiException {
        int sheetIdSize = 0;
        String temp = null;
        try {
            temp = AppPropertyPool.getProperty("sheet_id_size");
            sheetIdSize = Integer.parseInt(temp);
            return sheetIdSize;
        } catch (NumberFormatException e) {
            StringBuffer buf = new StringBuffer();
            buf.append("An invalid value was found to be assoication with properties value, sheet_id_size.  Unable to convert ");
            buf.append(temp);
            buf.append(" to a number.  Defaulting to 5");
            logger.info(buf);
            return 5;
        } catch (Exception e) {
            this.msg = "Unable to obtain value representing the maximum display digits for time sheet id";
            throw new InvoiceTimesheetApiException(this.msg, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modules.timesheet.TimesheetApi#updateProjectTask(org.dto.ProjectTaskDto
     * )
     */
    @Override
    public int updateProjectTask(ProjectTaskDto projectTask) throws TimesheetApiException {
        this.validateProjectTask(projectTask);
        return this.dao.maintainProjectTask(projectTask);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modules.timesheet.TimesheetApi#updateEvent(org.dto.EventDto)
     */
    @Override
    public int updateEvent(EventDto event) throws TimesheetApiException {
        this.validateEvent(event);
        return this.dao.maintainEvent(event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modules.timesheet.TimesheetApi#updateEvent(int, java.util.List)
     */
    @Override
    public int updateEvent(Integer projectTaskId, List<EventDto> events) throws TimesheetApiException {
        this.validateEvent(projectTaskId, events);
        // Begin processing each item in the List of events
        int count = 0;
        for (EventDto event : events) {
            event.setProjectTaskId(projectTaskId);
            this.updateEvent(event);
            count++;
        }
        return count;
    }

    /**
     * Verifies that a client and an employee is associated with the timesheet.
     * 
     * @param ts
     *            The timesheet to be validated.
     * @throws InvalidTimesheetException
     *             If the client id and/or the employee id is found not to be
     *             associated with the timesheet.
     */
    private void validateTimesheet(TimesheetDto ts) throws InvalidTimesheetException {
        try {
            Verifier.verifyNotNull(ts);
        }
        catch (VerifyException e) {
            this.msg = "The base timesheet instance cannot be null.";
            throw new InvalidTimesheetException(this.msg, e);
        }

        try {
            Verifier.verifyPositive(ts.getClientId());
        }
        catch (VerifyException e) {
            this.msg = "Timesheet must be associated with a client";
            throw new InvalidTimesheetException(this.msg, e);
        }
        
        try {
            Verifier.verifyPositive(ts.getEmpId());
        }
        catch (VerifyException e) {
            this.msg = "Timesheet must be associated with an employee";
            throw new InvalidTimesheetException(this.msg, e);
        }
        return;
    }

    /**
     * Verifies that the event object contains proper data.
     * 
     * @param event
     *            The event object that is to be validated.
     * @throws InvalidEventException
     *             <i>event</i> is null, project task id or the event date does
     *             not have values within <i>event</i>.
     */
    private void validateEvent(EventDto event) throws InvalidEventException {
        try {
            Verifier.verifyNotNull(event);
        }
        catch (VerifyException e) {
            this.msg = "Project event object cannot be null";
            throw new InvalidEventException(this.msg, e);
        }

        try {
            Verifier.verifyPositive(event.getProjectTaskId());
        }
        catch (VerifyException e) {
            this.msg = "A project/task id is required";
            throw new InvalidEventException(this.msg, e);
        }
        
        try {
            Verifier.verifyNotNull(event.getEventDate());
        }
        catch (VerifyException e) {
            this.msg = "Event date is required";
            throw new InvalidEventException(this.msg, e);
        }
    }

    /**
     * 
     * @param projectTaskId
     * @param events
     * @throws InvalidEventException
     */
    private void validateEvent(Integer projectTaskId, List<EventDto> events) throws InvalidEventException {
        try {
          Verifier.verifyNotNull(projectTaskId);
        }
        catch (VerifyException e) {
            throw new InvalidDataException("Project Task Id is required", e);
        }
        try {
            Verifier.verifyPositive(projectTaskId);
        }
        catch (VerifyException e) {
            this.msg = "A project/task id must be valid in order to assoicate multiple events with a project task";
            throw new InvalidDataException(this.msg, e);
        }

        // Verify that project task exists
        ProjectAdminApiFactory f = new ProjectAdminApiFactory();
        ProjectAdminApi projApi = f.createApi(this.getSharedDao());
        try {
            ProjectTaskDto criteria = ProjectObjectFactory.createProjectTaskExtendedDtoInstance(null);
            criteria.setProjectTaskId(projectTaskId);
            List<ProjectTaskDto> projTaskDto = projApi.getProjectTask(criteria);
            if (projTaskDto == null) {
                this.msg = "project/task id, "
                        + projectTaskId
                        + ", must exist in the system in order to assoicate multiple events with a project task";
                throw new InvalidEventException(this.msg);
            }
        } catch (ProjectAdminApiException e) {
            throw new InvalidEventException("Error validating project task entity existence", e);
        } finally {
            f = null;
            projApi = null;
        }

        // Verify that events is valid
        if (events == null) {
            this.msg = "The collection containing the lists of project events must be valid in order to assoicate multiple events with a project task";
            throw new InvalidEventException(this.msg);
        }
        return;
    }

    /**
     * Verifies that individual values for timesheet, project, and task have
     * been set.
     * 
     * @param pt
     *            The project-task record that is to be validated.
     * @throws InvalidProjectTaskException
     *             if the values for timesheet, project, and/or task are not
     *             set.
     */
    private void validateProjectTask(ProjectTaskDto pt) throws InvalidProjectTaskException {
        try {
            Verifier.verifyPositive(pt.getProjId());
        }
        catch (VerifyException e) {
            this.msg = "Proejct Id is required when creating Project-Task";
            throw new InvalidProjectTaskException(this.msg, e);
        }
        try {
            Verifier.verifyPositive(pt.getTaskId());
        }
        catch (VerifyException e) {
            this.msg = "Task Id is required when creating Project-Task";
            throw new InvalidProjectTaskException(this.msg, e);
        }
    }

    /**
     * Verifies that the task has a project id assigned and ensures that the
     * project id is congruent with the current project id assoicated with this
     * API.
     * 
     * @param task
     *            an instance of {@link ProjectTaskDto}
     * @throws InvalidTaskException
     */
    private void validateTask(ProjectTaskDto task) throws InvalidTaskException {
        try {
            Verifier.verifyNotNull(task);
        } catch (VerifyException e) {
            this.msg = "Thimesheet task is invalid or null.";
            throw new InvalidTaskException(this.msg, e);
        }

        if (task.getProjId() == 0) {
            this.msg = "Timesheet is required to have an assigned project";
            throw new InvalidTaskException(this.msg);
        }

        if (this.currentProjectId == 0) {
            this.msg = "Timesheet API is required to have a master project id assigned so that each task's project id can be compared";
            throw new InvalidTaskException(this.msg);
        }
        if (task.getProjId() != this.currentProjectId) {
            this.msg = "Found a conflicting project assinged to timesheet task named, "
                    + task.getTaskDescription()
                    + ".  Be sure that all tasks are associated with the same project for the timesheet";
            throw new InvalidTaskException(this.msg);
        }

    }

    /**
     * Verifies that changing the status of the timesheet identified as
     * <i>timesheetId</i> to the new status represented as <i>newStatusId</i> is
     * legal.
     * <p>
     * The change is considered legal only if an exception is not thrown. The
     * following sequence must be followed when changing the status of a
     * purchase order:
     * <p>
     * <ul>
     * <li>The timesheet must be new in order to change the status to
     * "Not Submitted"</li>
     * <li>The timesheet must be in "Not Submitted" status before changing to
     * "Submitted".</li>
     * <li>The timesheet must be in "Submitted" status before changing to
     * "Received".</li>
     * <li>The timesheet must be in "Received" status before changing to
     * "Approved" or "Declined".</li>
     * </ul>
     * 
     * @param timesheetId
     *            Target timesheet id
     * @param newStatusId
     *            The id of the status that is to be assigned to the timesheet
     * @return The id of the old status.
     * @throws TimesheetApiException
     *             Inability to obtain current status
     * @throws InvalidStatusChangeException
     *             When the prospective new status is not in sequence to the
     *             current status regarding changing the status of the
     *             timesheet. The exception should give a detail explanation as
     *             to the reason why the status cannot be changed.
     */
    protected int verifyStatusChange(int timesheetId, int newStatusId) throws InvalidStatusChangeException, 
                TimesheetApiException {
        TimesheetHistDto tsh = null;
        int currentStatusId = 0;

        tsh = this.getCurrentStatus(timesheetId);
        currentStatusId = (tsh == null ? TimesheetConst.STATUS_NEW : tsh
                .getStatusId());
        switch (newStatusId) {
            case TimesheetConst.STATUS_DRAFT:
                if (currentStatusId != TimesheetConst.STATUS_NEW) {
                    this.msg = "Timesheet status can only change to Not Submitted when the current status is New";
                    throw new InvalidStatusChangeException(this.msg);
                }
                break;

            case TimesheetConst.STATUS_SUBMITTED:
                if (currentStatusId != TimesheetConst.STATUS_DRAFT) {
                    this.msg = "Timesheet status can only change to Submitted when the current status is Not Submitted";
                    throw new InvalidStatusChangeException(this.msg);
                }
                break;

            case TimesheetConst.STATUS_APPROVED:
            case TimesheetConst.STATUS_DECLINED:
                if (currentStatusId != TimesheetConst.STATUS_SUBMITTED) {
                    this.msg = "Timesheet status can only change to Approved or Declined when the current status is Submitted";
                    throw new InvalidStatusChangeException(this.msg);
                }
                break;
        } // end outer switch

        return currentStatusId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modules.timesheet.TimesheetApi#changeTimesheetStatus(int, int)
     */
    @Override
    public void changeTimesheetStatus(Integer timesheetId, Integer newStatusId) throws TimesheetApiException {
        // See if it is legal to transition to newStatusId
        this.verifyStatusChange(timesheetId, newStatusId);

        // terminate current status
        TimesheetHistDto oldHist = this.getCurrentStatus(timesheetId);
        if (oldHist != null) {
            this.dao.maintainStatusHistory(oldHist);
        }
        // Add new current status
        TimesheetHistDto newHist = TimesheetObjectFactory
                .createTimesheetHistoryDtoInstance(null);
        newHist.setTimesheetId(timesheetId);
        newHist.setStatusId(newStatusId);
        this.dao.maintainStatusHistory(newHist);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modules.timesheet.TimesheetApi#setCurrentProjectId(int)
     */
    @Override
    public void setCurrentProjectId(Integer projectId) {
        this.currentProjectId = projectId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modules.timesheet.TimesheetApi#getCurrentProjectId()
     */
    @Override
    public int getCurrentProjectId() {
        return this.currentProjectId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modules.timesheet.TimesheetApi#approve(int)
     */
    @Override
    public void approve(Integer timesheetId) throws TimesheetApiException {
        this.validateNumericParam(timesheetId, PARM_NAME_TIMESHEET_ID);
        
        // Set timesheet status to Approved.
        this.changeTimesheetStatus(timesheetId, TimesheetConst.STATUS_APPROVED);
        return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modules.timesheet.TimesheetApi#decline(int)
     */
    @Override
    public void decline(Integer timesheetId) throws TimesheetApiException {
        this.validateNumericParam(timesheetId, PARM_NAME_TIMESHEET_ID);
        
        // Set timesheet status to Declined
        this.changeTimesheetStatus(timesheetId, TimesheetConst.STATUS_DECLINED);
        return;
    }

    /**
     * Handles the request to submit an employee's timesheet for manager
     * approval..
     * 
     * @param timesheetId
     * @throws TimesheetApiException
     */
    @Override
    public void submit(Integer timesheetId) throws TimesheetApiException {
        this.validateNumericParam(timesheetId, PARM_NAME_TIMESHEET_ID);
        
        this.load(timesheetId);
        // Set timesheet status to Draft.
        this.changeTimesheetStatus(this.ts.getTimesheetId(), TimesheetConst.STATUS_SUBMITTED);

        // Email timesheet
        TimesheetTransmissionApi api = new SmtpTimesheetTransmissionApiImpl();
        try {
            // Get employee profile
            EmployeeApiFactory empFact = new EmployeeApiFactory();
            EmployeeApi empApi = empFact.createApi(this.getSharedDao());
            EmployeeDto employee = empApi.getEmployee(this.ts.getEmpId());
            EmployeeDto manager = empApi.getEmployee(employee.getManagerId());

            // get client profile
            ProjectAdminApiFactory projFact = new ProjectAdminApiFactory();
            ProjectAdminApi projApi = projFact.createApi(this.getSharedDao());
            ClientDto clientCriteria = ProjectObjectFactory.createClientDtoInstance(null);
            clientCriteria.setClientId(this.ts.getClientId());
            List<ClientDto> clients = projApi.getClient(clientCriteria);

            // send timesheet via email
            EmailMessageBean msg = api.createConfirmationMessage(this.ts,
                    employee, manager, clients.get(0), this.tsHours);
            api.send(msg);
        } catch (TimesheetTransmissionException e) {
            this.msg = "SMTP error occurred attempting to send timesheet: " + this.ts.getDisplayValue();
            throw new TimesheetApiException(this.msg, e);
        } catch (EmployeeApiException e) {
            this.msg = "Data access error fetching timesheet's employee profile: " + this.ts.getEmpId();
            throw new TimesheetApiException(this.msg, e);
        } catch (ProjectAdminApiException e) {
            this.msg = "Data access error fetching timesheet's client profile: " + this.ts.getClientId();
            throw new TimesheetApiException(this.msg, e);
        } finally {
            api = null;
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modules.timesheet.TimesheetApi#deleteTimesheet(int)
     */
    @Override
    public int deleteTimesheet(Integer timesheetId) throws TimesheetApiException {
        this.validateNumericParam(timesheetId, PARM_NAME_TIMESHEET_ID);
        
        // Delete all project/tasks belonging to the timesheet
        this.deleteProjectTasks(timesheetId);
        // Now delete the timesheet
        TimesheetDto criteria = TimesheetObjectFactory.createTimesheetDtoInstance(null);
        criteria.setTimesheetId(timesheetId);
        int rc = this.dao.deleteTimesheet(criteria);
        return rc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modules.timesheet.TimesheetApi#deleteProjectTask(int)
     */
    @Override
    public int deleteProjectTask(Integer projectTaskId) throws TimesheetApiException {
        this.validateNumericParam(projectTaskId, "Project Task Id");
        
        // delete events first...
        this.deleteEvents(projectTaskId);
        // Now delete the project/task
        ProjectTaskDto criteria = ProjectObjectFactory.createProjectTaskDtoInstance(null);
        criteria.setProjectTaskId(projectTaskId);
        int rc = this.dao.deleteProjectTask(criteria);
        return rc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modules.timesheet.TimesheetApi#deleteProjectTasks(int)
     */
    @Override
    public int deleteProjectTasks(Integer timesheetId) throws TimesheetApiException {
        this.validateNumericParam(timesheetId, PARM_NAME_TIMESHEET_ID);
        
        List<ProjectTaskDto> ptList = this.getProjectTaskByTimesheet(timesheetId);
        int count = 0;
        for (ProjectTaskDto item : ptList) {
            this.deleteProjectTask(item.getProjectTaskId());
            count++;
        }
        return count;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modules.timesheet.TimesheetApi#deleteEvents(int)
     */
    @Override
    public int deleteEvents(Integer projectTaskId) throws TimesheetApiException {
        this.validateNumericParam(projectTaskId, "Project Task Id");
        
        EventDto criteria = ProjectObjectFactory.createEventDtoInstance(null);
        criteria.setProjectTaskId(projectTaskId);
        int rc = this.dao.deleteEvent(criteria);
        return rc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modules.timesheet.TimesheetApi#deleteEvent(int)
     */
    @Override
    public int deleteEvent(Integer eventId) throws TimesheetApiException {
        this.validateNumericParam(eventId, "Event Id");
        
        EventDto criteria = ProjectObjectFactory.createEventDtoInstance(null);
        criteria.setEventId(eventId);
        int rc = this.dao.deleteEvent(criteria);
        return rc;
    }

    /**
     * Emails a copy of an employee's timesheet to the employee's manager using
     * <i>timesheet</i>, <i>employee</i>, <i>client</i>, and <i>hours</i>.
     * 
     * @param timesheet
     *            An instance of {@link TimesheetDto}
     * @param employee
     *            An instance of {@link EmployeeDto}
     * @param client
     *            An instance of {@link ClientDto}
     * @param hours
     *            A Map containing the hours for each project/task. The key is
     *            represented as {@link ProjectTaskDto} and the values is
     *            represented as a List of {@link EventDto} objects.
     * @return 1 when email is successfully sent and 0 when there are no
     *         project-task hours to process.
     * @throws TimesheetApiException
     *             Validation errors
     * @throws TimesheetTransmissionException
     *             Error occurs sending timesheet data to its designated
     *             recipient via the SMTP protocol.
     */
    @Override
    public int send(TimesheetDto timesheet, EmployeeDto employee, ClientDto client, 
            Map<ProjectTaskDto, List<EventDto>> hours) throws TimesheetApiException, 
              TimesheetTransmissionException {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modules.timesheet.TimesheetApi#load(int)
     */
    @Override
    public void load(Integer timesheetId) throws TimesheetApiException {
        this.validateNumericParam(timesheetId, PARM_NAME_TIMESHEET_ID);
        
        // Fetch Timesheet
        TimesheetDto ts = this.getExt(timesheetId);
        if (ts == null) {
            this.ts = null;
            this.tsHours = null;
            return;
        }

        // Fetch project/tasks
        List<ProjectTaskDto> ptList = this.getProjectTaskExtByTimesheet(timesheetId);
        if (ptList == null || ptList.size() <= 0) {
            this.ts = null;
            this.tsHours = null;
            return;
        }

        // We have a valid timesheet and one or more project/task which will
        // allow us to build the object graph.
        Map<ProjectTaskDto, List<EventDto>> hrs = new LinkedHashMap<ProjectTaskDto, List<EventDto>>();

        ProjectAdminApiFactory projApiFact = new ProjectAdminApiFactory();
        ProjectAdminApi api = projApiFact.createApi(this.getSharedDao());
        try {
            for (ProjectTaskDto pt : ptList) {
                try {
                    EventDto eventCriteria = ProjectObjectFactory.createEventDtoInstance(null);
                    eventCriteria.setProjectTaskId(pt.getProjectTaskId());
                    List<EventDto> evts = api.getEvent(eventCriteria, null, null);
                    if (evts == null || evts.size() <= 0) {
                        this.ts = null;
                        this.tsHours = null;
                        return;
                    }
                    // Add project details to collection
                    hrs.put(pt, evts);
                } catch (ProjectAdminApiException e) {
                    this.msg = "Timesheet API load operation failed.  Error occurred fetching events for project/task id, "
                            + pt.getProjectTaskId();
                    throw new TimesheetApiException(this.msg, e);
                }
            }
        } finally {
            projApiFact = null;
            api = null;
        }

        // Set member variables
        this.ts = ts;
        this.tsHours = hrs;
        return;
    }

    private void validateNumericParam(Integer parmValue, String parmName) {
        try {
            Verifier.verifyNotNull(parmValue);    
        }
        catch (VerifyException e) {
            throw new InvalidDataException((parmName == null ? "Unknown parameter " : parmName) + " is required", e);
        }
        try {
            Verifier.verifyNotNegative(parmValue);    
        }
        catch (VerifyException e) {
            throw new InvalidDataException((parmName == null ? "Unknown parameter " : parmName) + " cannot be negative", e);
        }
    }
}
