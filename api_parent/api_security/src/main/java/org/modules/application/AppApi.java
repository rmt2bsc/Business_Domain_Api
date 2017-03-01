package org.modules.application;

import java.util.List;

import org.dto.ApplicationDto;

import org.modules.SecurityModuleException;

import com.api.foundation.TransactionApi;

/**
 * An API contract for for managing the application module.
 * 
 * @author rterrell
 * 
 */
public interface AppApi extends TransactionApi {

    /**
     * Retrieve all Application records.
     * 
     * @return A List of {@link ApplicationDto} objects or null if no data is
     *         found.
     * @throws SecurityModuleException
     */
    List<ApplicationDto> get() throws SecurityModuleException;

    /**
     * Retrieve an Application record using its unique key id.
     * 
     * @param uid
     *            A unique id identifying the entity targeted to be fetched.
     * @return An instance of {@link ApplicationDto}
     * @throws SecurityModuleException
     */
    ApplicationDto get(int uid) throws SecurityModuleException;

    /**
     * Retrieve an application record using its unique key id.
     * 
     * @param appName
     *            the name of the application.
     * @return An instance of {@link ApplicationDto}
     * @throws ApplicationApiException
     */
    ApplicationDto get(String appName) throws AppApiException;

    /**
     * Create a new Application instance of a particular category.
     * 
     * @return an instance of {@link ApplicationDto}
     */
    ApplicationDto create();

    /**
     * Persist changes to a single Application entity to the database.
     * <p>
     * This method will handle SQL inserts and updates where applicable. When
     * the entity's unique id is equal to zero, an insert is performed. When the
     * unique id is greater than zero, an update is performed.
     * 
     * @param dto
     *            An instance of {@link ApplicationDto} containing the
     *            application related data to be applied to the database.
     * @return the total number of rows effecting existing records, or the
     *         unique identifier for a new record.
     * @throws AppApiException
     */
    int update(ApplicationDto dto) throws AppApiException;

    /**
     * Delete an application object from a data source by its unique identifier.
     * 
     * @param uid
     *            The primary key of the user.
     * @return the total number of records deleted.
     * @throws AppApiException
     */
    int delete(int uid) throws AppApiException;

    /**
     * Delete an application object from a data source by its name
     * 
     * @param appName
     *            The name of the application
     * @return the total number of records deleted.
     * @throws AppApiException
     */
    int delete(String appName) throws AppApiException;
}