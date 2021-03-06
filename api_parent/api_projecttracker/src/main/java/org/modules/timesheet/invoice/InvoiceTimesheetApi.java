package org.modules.timesheet.invoice;

import java.util.List;

import com.api.foundation.TransactionApi;

/**
 * An interface which defines methods that are responsible for invoicing
 * timesheets
 * <p>
 * Functionality exists for invoicing a single item or invoicing multiple items
 * from a single call.
 * 
 * @author Roy Terrell
 * 
 */
public interface InvoiceTimesheetApi extends TransactionApi {

    /**
     * Return information that was involved in the invoicing process.
     * <p>
     * This generally includes the clients, their timesheets, and the
     * transaction id's produced from the invoiceing process.
     * 
     * @return an instance of {@link InvoiceResultsBean}
     */
    InvoiceResultsBean getResults();

    /**
     * Invoices a single timesheet.
     * 
     * @param timesheetId
     *            The unique identifier of the timesheet to invoice.
     * @return int
     * @throws InvoiceTimesheetApiException
     */
    int invoice(Integer timesheetId) throws InvoiceTimesheetApiException;

    /**
     * Invoices all timesheets related to one or more clients.
     * <p>
     * An invoice id shall be created per client that is procesed.
     * 
     * @param clientIdList
     *            A List of client unique identifiers.
     * @return A List of invoice id's where each invoice id represent one of the
     *         clients that were processed.
     * @throws InvoiceTimesheetApiException
     */
    List<Integer> invoice(List<Integer> clientIdList) throws InvoiceTimesheetApiException;

    /**
     * Calculates the invoice amount of the timesheet using regular rate and
     * overtime rate to determine regular pay and overtime pay, respectively.
     * 
     * @param timesheetId
     *            The id of the timesheet that is to be calculated.
     * @return The invoice amount.
     * @throws InvoiceTimesheetApiException
     *             Problem occurred gathering the timesheet data.
     */
    double calculateInvoice(Integer timesheetId) throws InvoiceTimesheetApiException;

    /**
     * Calculates a timesheet's billable hours.
     * 
     * @param timesheetId
     *            The id of the timesheet that is to be calculated.
     * @return The amount of billable hours.
     * @throws InvoiceTimesheetApiException
     *             Problem occurred gathering the timesheet data.
     */
    double calculateBillableHours(Integer timesheetId) throws InvoiceTimesheetApiException;

    /**
     * Calculates a timesheet's non-billable hours.
     * 
     * @param timesheetId
     *            The id of the timesheet that is to be calculated.
     * @return The amount of non-billable hours.
     * @throws InvoiceTimesheetApiException
     *             Problem occurred gathering the timesheet data.
     */
    double calculateNonBillableHours(Integer timesheetId) throws InvoiceTimesheetApiException;
}
