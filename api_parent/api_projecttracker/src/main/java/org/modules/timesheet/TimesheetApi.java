package org.modules.timesheet;

import java.util.List;
import java.util.Map;

import org.dto.ClientDto;
import org.dto.EmployeeDto;
import org.dto.EventDto;
import org.dto.ProjectEventDto;
import org.dto.ProjectTaskDto;
import org.dto.TimesheetDto;
import org.dto.TimesheetHistDto;
import org.dto.TimesheetHoursDto;

import com.api.foundation.TransactionApi;

/**
 * An interface which defines methods that are responsible for managing the time
 * an employee spends working on projects as well as the life cycle of the
 * timesheet itself.
 * <p>
 * Included in this interface are methods which function to provide various
 * alternatives for querying the database for tmesheet data. Data can be queried
 * at various levels such as the client, timesheet, project, or task.
 * <p>
 * Also provides functionality for invoicing a single item or invoicing multiple
 * items from a single call.
 * 
 * @author Roy Terrell
 * 
 */
public interface TimesheetApi extends TransactionApi {

    /**
     * Fetches the entire timesheet object graph and makes that timesheet
     * current to the API.
     * <p>
     * Data pertaining to the base timesheet, project/tasks, and its hours are
     * gathered and assembled into various member variables. This information is
     * stored within the API and is used for internal purposes.
     * 
     * @param timesheetId
     *            The id of timesheet to load
     * @throws TimesheetApiException
     */
    void load(int timesheetId) throws TimesheetApiException;

    /**
     * Assignss the project id which this timesheet will be governed by
     * 
     * @param projectId
     */
    void setCurrentProjectId(int projectId);

    /**
     * Returns the project id that governs this timesheet.
     * 
     * @return
     */
    int getCurrentProjectId();

    /**
     * 
     * @param timesheetId
     * @return
     * @throws TimesheetApiException
     */
    TimesheetDto get(int timesheetId) throws TimesheetApiException;

    /**
     * 
     * @param criteria
     * @return
     * @throws TimesheetApiException
     */
    List<TimesheetDto> get(String criteria) throws TimesheetApiException;

    /**
     * 
     * @param timesheetId
     * @return
     * @throws TimesheetApiException
     */
    TimesheetDto getExt(int timesheetId) throws TimesheetApiException;

    /**
     * 
     * @param clientId
     * @return
     * @throws TimesheetApiException
     */
    List<TimesheetDto> getByClient(int clientId) throws TimesheetApiException;

    /**
     * Get all client approved timesheets.
     * 
     * @param clientId
     * @return
     * @throws TimesheetApiException
     */
    List<TimesheetDto> getClientApproved(int clientId)
            throws TimesheetApiException;

    /**
     * 
     * @param employeeId
     * @return
     * @throws TimesheetApiException
     */
    List<TimesheetDto> getByEmployee(int employeeId)
            throws TimesheetApiException;

    /**
     * 
     * @param timesheetId
     * @param statusId
     * @return
     * @throws TimesheetApiException
     */
    List<TimesheetDto> getByStatus(Integer timesheetId[], int statusId)
            throws TimesheetApiException;

    /**
     * 
     * @param timesheetId
     * @return
     * @throws TimesheetApiException
     */
    List<ProjectTaskDto> getProjectTaskByTimesheet(int timesheetId)
            throws TimesheetApiException;

    /**
     * 
     * @param timesheetId
     * @return
     * @throws TimesheetApiException
     */
    List<ProjectTaskDto> getProjectTaskExtByTimesheet(int timesheetId)
            throws TimesheetApiException;

    /**
     * 
     * @param timesheetId
     * @return
     * @throws TimesheetApiException
     */
    List<ProjectEventDto> getEventByTimesheet(int timesheetId)
            throws TimesheetApiException;

    /**
     * Retreives the current status of a timesheet.
     * 
     * @param timesheetId
     *            The Id of the timesheet to retreive the status.
     * @return {@link TimesheetHistDto}
     * @throws TimesheetApiException
     */
    TimesheetHistDto getCurrentStatus(int timesheetId)
            throws TimesheetApiException;

    /**
     * Retrieves timesheet hours of a timesheet using timesheetId.
     * 
     * @param timesheetId
     * @return List of {@link TimesheetHoursDto} objects
     * @throws TimesheetApiException
     */
    List<TimesheetHoursDto> getHours(int timesheetId)
            throws TimesheetApiException;

    /**
     * Creates a new or modifies an existing timesheet including all hours for
     * each project task.
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
    int updateTimesheet(TimesheetDto timesheet,
            Map<ProjectTaskDto, List<EventDto>> hours)
            throws TimesheetApiException;

    /**
     * Assigns a new status to a timesheet and applies the changes to timesheet
     * status history table in the database.
     * <p>
     * Before the new status assignment, the current status is terminated by
     * assigning an end date of the current day. Since there is no logic
     * implemented in this method to govern the movement of timesheet statuses,
     * invoke method, verifyStatusChange(int, int), prior to this method in
     * order to verify that moving to the new status is in alignment with the
     * business rules of changing statuses for timesheets.
     * 
     * @param timesheetId
     *            The id of the target timesheet
     * @param newStatusId
     *            The id of the new status that is to be assigned to the
     *            timesheet.
     * @throws TimesheetApiException
     */
    void changeTimesheetStatus(int timesheetId, int newStatusId)
            throws TimesheetApiException;

    /**
     * Deletes a single timesheet from the system using its primary key.
     * <p>
     * All project/tasks that are associated with <i>timesheetId</i> are
     * removed.
     * 
     * @param timesheetId
     *            The id of the target timesheet.
     * @return The number of timesheets deleted.
     * @throws TimesheetApiException
     *             if a database access error occurs.
     */
    int deleteTimesheet(int timesheetId) throws TimesheetApiException;

    /**
     * Deletes a single project-task by its primary key.
     * <p>
     * All events that are associated with each <i>projectTaskId</i> are
     * removed.
     * 
     * @param projectTaskId
     *            The id of the project-task to delete.
     * @return The total number of project/tasks deleted.
     * @throws TimesheetApiException
     *             if a database access error occurs.
     */
    int deleteProjectTask(int projectTaskId) throws TimesheetApiException;

    /**
     * Deletes all project/tasks that are related to a particular timesheet.
     * <p>
     * All events that are associated with each timesheet's project/task are
     * removed.
     * 
     * @param timesheetId
     *            The id of the timesheet to delete project-tasks
     * @return The number of project/tasks deleted.
     * @throws TimesheetApiException
     *             if a database access error occurs.
     */
    int deleteProjectTasks(int timesheetId) throws TimesheetApiException;

    /**
     * Deletes all events that are related to a project/task.
     * 
     * @param projectTaskId
     *            The id of the project-task to delete all events
     * @return The total number of events deleted.
     * @throws TimesheetApiException
     *             if a database access error occurs.
     */
    int deleteEvents(int projectTaskId) throws TimesheetApiException;

    /**
     * Delete a single event using its primary key.
     * 
     * param eventId The id of the event to delete.
     * 
     * @return The total number of events deleted
     * @throws TimesheetApiException
     *             if a database access error occurs.
     */
    int deleteEvent(int enventId) throws TimesheetApiException;

    /**
     * Approves an employee's timesheet.
     * 
     * @param timesheetId
     * @throws TimesheetApiException
     */
    void approve(int timesheetId) throws TimesheetApiException;

    /**
     * Declines an employee's timesheet.
     * 
     * @param timesheetId
     * @throws TimesheetApiException
     */
    void decline(int timesheetId) throws TimesheetApiException;

    /**
     * Submits an employee's timesheet for approval/declination.
     * 
     * @param timesheet
     * @param hours
     * @throws TimesheetApiException
     */
    void submit(int timesheetId) throws TimesheetApiException;

    /**
     * Creates a new or updates an existing project/task.
     * 
     * @param projectTask
     *            An instance of {@link ProjectTaskDto}
     * @return The id of the new timesheet project/task created or the total
     *         number of existing project/task modified.
     * @throws TimesheetApiException
     */
    int updateProjectTask(ProjectTaskDto projectTask)
            throws TimesheetApiException;

    /**
     * Creates new or updates an existing project event.
     * 
     * @param event
     *            An instance of {@link EventDto}
     * @return The id of the new timesheet event created or the total number of
     *         existing events modified.
     * @throws TimesheetApiException
     */
    int updateEvent(EventDto event) throws TimesheetApiException;

    /**
     * Creates or modifies all events belonging to a Project/Task group.
     * 
     * @param projectTaskId
     *            The id of the project/task group.
     * @param events
     *            A List of {@link EventDto} objects
     * @return Total number of events processed.
     * @throws TimesheetApiException
     */
    int updateEvent(int projectTaskId, List<EventDto> events)
            throws TimesheetApiException;

    /**
     * Sends a copy of a given timesheet to a designated recipient.
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
     * @return int
     * @throws TimesheetApiException
     *             <i>timesheet</i>, <i>employee</i>, <i>client</i>, or
     *             <i>hours</i> are null or invalid. <i>hours</i> does not
     *             contain any entries.
     * @throws TimesheetTransmissionException
     *             Error occurs sending timesheet data to its designated
     *             recipient.
     */
    int send(TimesheetDto timesheet, EmployeeDto employee, ClientDto client,
            Map<ProjectTaskDto, List<EventDto>> hours)
            throws TimesheetApiException, TimesheetTransmissionException;

}