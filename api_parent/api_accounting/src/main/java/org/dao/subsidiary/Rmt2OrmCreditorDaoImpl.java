package org.dao.subsidiary;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dao.AccountingSqlConst;
import org.dao.mapping.orm.rmt2.Creditor;
import org.dao.mapping.orm.rmt2.CustomerActivity;
import org.dao.mapping.orm.rmt2.VwCreditorXactHist;
import org.dto.CreditorDto;
import org.dto.CreditorTypeDto;
import org.dto.CreditorXactHistoryDto;
import org.dto.SubsidiaryContactInfoDto;
import org.dto.SubsidiaryDto;
import org.dto.SubsidiaryXactHistoryDto;
import org.dto.adapter.orm.account.subsidiary.Rmt2SubsidiaryDtoFactory;

import com.api.persistence.DatabaseException;
import com.api.persistence.PersistenceClient;
import com.util.RMT2Date;
import com.util.RMT2String;
import com.util.UserTimestamp;

/**
 * An implementation of {@link CreditorDao}. It provides functionality that
 * creates, updates, deletes, and queries creditor subsidiary account data.
 * 
 * @author Roy Terrell
 * 
 */
class Rmt2OrmCreditorDaoImpl extends AbstractRmt2SubsidiaryContactDaoImpl
        implements CreditorDao {

    /**
     * Construce a Rmt2OrmCreditorDaoImpl object initialized with a connection
     * to the database
     */
    public Rmt2OrmCreditorDaoImpl() {
        super();
    }

    public Rmt2OrmCreditorDaoImpl(String appName) {
        super(appName);
        return;
    }

    public Rmt2OrmCreditorDaoImpl(PersistenceClient client) {
        super(client);
        return;
    }

    /**
     * Calculate and return the creditor's balance.
     * 
     * @param creditorId
     *            the unique id of the creditor.
     * @return the balance as a double
     * @throws CreditorDaoException
     */
    @Override
    public double calculateBalance(int creditorId)
            throws SubsidiaryDaoException {
        String sql = RMT2String.replace(
                AccountingSqlConst.SQL_CREDITOR_BALANCE,
                String.valueOf(creditorId), "$1");
        double bal = 0;
        try {
            ResultSet rs = this.client.executeSql(sql);
            if (rs.next()) {
                bal = rs.getDouble("balance");
            }
            return bal;
        } catch (Exception e) {
            throw new CreditorDaoException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dao.subsidiary.CreditorDao#fetch(org.dto.CreditorTypeDto)
     */
    @Override
    public List<CreditorTypeDto> fetch(CreditorTypeDto criteria)
            throws CreditorDaoException {
        return null;
    }

    /**
     * Fetches the creditor's transaction history from the
     * <i>creditor_activitiy</i> table.
     * 
     * @param creditorId
     *            An integer representing the creditor id.
     * @return a list of {@link CreditorXactHistoryDto} objects or null when the
     *         query returns an empty result set.
     * @throws CreditorDaoException
     */
    @Override
    public List<CreditorXactHistoryDto> fetchTransactionHistory(int creditorId)
            throws CreditorDaoException {
        VwCreditorXactHist obj = new VwCreditorXactHist();
        obj.addCriteria(VwCreditorXactHist.PROP_CREDITORID, creditorId);
        obj.addOrderBy(VwCreditorXactHist.PROP_XACTDATE,
                VwCreditorXactHist.ORDERBY_DESCENDING);
        obj.addOrderBy(VwCreditorXactHist.PROP_XACTID,
                VwCreditorXactHist.ORDERBY_DESCENDING);

        List<VwCreditorXactHist> results = null;
        try {
            results = this.client.retrieveList(obj);
        } catch (DatabaseException e) {
            throw new CreditorDaoException(e);
        }

        List<CreditorXactHistoryDto> list = new ArrayList<CreditorXactHistoryDto>();
        for (VwCreditorXactHist item : results) {
            CreditorXactHistoryDto dto = Rmt2SubsidiaryDtoFactory
                    .createCreditorTransactionInstance(item);
            list.add(dto);
        }
        return list;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dao.subsidiary.CreditorDao#fetch(org.dto.CreditorDto)
     */
    @Override
    public List<CreditorDto> fetch(CreditorDto criteria)
            throws CreditorDaoException {
        List<CreditorDto> results = null;
        boolean useCreditorParms = false;
        boolean useContactParms = false;
        if (criteria != null) {
            useContactParms = (criteria.getTaxId() != null
                    || criteria.getContactName() != null || criteria
                    .getPhoneCompany() != null);

            useCreditorParms = (criteria.getAccountNo() != null
                    || criteria.getCreditorId() > 0
                    || criteria.getContactId() > 0 || criteria
                    .getCreditorTypeId() > 0);
        }
        // Determine the query sequence for obtaining the creditor data.
        // local=>remote or remote=>local.
        if (useContactParms && !useCreditorParms) {
            // Fetch by web service first
            results = this.fetchWebServiceFirst(criteria);
        }
        else {
            // Fetch by local DB first
            results = this.fetchLocalFirst(criteria);
        }
        return results;
    }

    private List<Creditor> fetch(Creditor criteria) throws CreditorDaoException {
        // Retrieve creditor data from the database
        List<Creditor> results = null;
        try {
            results = this.client.retrieveList(criteria);
            if (results == null) {
                return null;
            }
        } catch (DatabaseException e) {
            throw new CreditorDaoException(e);
        }
        return results;
    }

    /**
     * Fetches creditor/contact combined data from both the local database and a
     * remote data source which the matching process is driven by the results of
     * the local fetch. The remote data source is accessed via a web service.
     * 
     * @param criteria
     *            an instance of {@link CreditorDto} containing the selection
     *            criteria that is used by both the local and remote queries.
     * @return List of {@link CreditorDto} or null when no data is found
     * @throws CreditorDaoException
     */
    private List<CreditorDto> fetchLocalFirst(CreditorDto criteria)
            throws CreditorDaoException {
        // Gather creditor criteria
        Creditor ormCred = null;
        if (criteria != null) {
            ormCred = new Creditor();
            if (criteria.getCreditorId() > 0) {
                ormCred.addCriteria(Creditor.PROP_CREDITORID,
                        criteria.getCreditorId());
            }
            if (criteria.getContactId() > 0) {
                ormCred.addCriteria(Creditor.PROP_BUSINESSID,
                        criteria.getContactId());
            }
            if (criteria.getAccountNo() != null) {
                ormCred.addCriteria(Creditor.PROP_ACCOUNTNUMBER,
                        criteria.getAccountNo());
            }
            if (criteria.getCreditorTypeId() > 0) {
                ormCred.addCriteria(Creditor.PROP_CREDITORTYPEID,
                        criteria.getCreditorTypeId());
            }
        }

        // Retrieve creditor local data
        List<Creditor> localResults = this.fetch(ormCred);
        if (localResults == null || localResults.isEmpty()) {
            return null;
        }
        // Obtain list of business id to be used for the web service call.
        List<String> busIdList = new ArrayList<String>();
        for (Creditor item : localResults) {
            busIdList.add(String.valueOf(item.getBusinessId()));
        }
        criteria.setContactIdList(busIdList);

        // invoke web service to obtain common contact info from an outside
        // application for each business id.
        Map<Integer, SubsidiaryContactInfoDto> remoteResults;
        try {
            remoteResults = this.fetch((SubsidiaryContactInfoDto) criteria);
        } catch (Exception e) {
            remoteResults = null;
        }
        // merge the two result sets.
        List<CreditorDto> mergedCreditors = this.mergeAndSortResults(
                localResults, remoteResults);
        return mergedCreditors;
    }

    /**
     * Fetches creditor/contact combined data from both the local database and a
     * remote data source which the matching process is driven by the results of
     * the remote fetch. The remote data source is accessed via a web service.
     * 
     * @param criteria
     *            an instance of {@link CreditorDto} containing the selection
     *            criteria that is used by both the local and remote queries.
     * @return List of {@link CreditorDto} or null when no data is found
     * @throws CreditorDaoException
     */
    private List<CreditorDto> fetchWebServiceFirst(CreditorDto criteria)
            throws CreditorDaoException {
        // invoke web service to obtain common contact info from an outside
        // application for each business id.
        Map<Integer, SubsidiaryContactInfoDto> remoteResults;
        try {
            remoteResults = this.fetch((SubsidiaryContactInfoDto) criteria);
        } catch (Exception e) {
            remoteResults = null;
        }

        // Add list business id's as selection criteria to build an "IN" clause.
        Creditor ormCriteria = new Creditor();
        // Only build "IN" clause for business id's when the remote result set
        // is not empty. Otherwise, return null since selection criteria for
        // remote query was not met.
        if (remoteResults != null && !remoteResults.isEmpty()) {
            List<String> busIdList = new ArrayList<String>();
            Iterator<Integer> iter = remoteResults.keySet().iterator();
            while (iter.hasNext()) {
                Integer busId = iter.next();
                busIdList.add(busId.toString());
            }
            String busArray[] = new String[busIdList.size()];
            busIdList.toArray(busArray);
            ormCriteria.addInClause(Creditor.PROP_BUSINESSID, busArray);
        }
        else {
            return null;
        }

        // Query the local database using the business id's retrieved remotely
        List<Creditor> localResults = this.fetch(ormCriteria);

        // merge the two result sets.
        List<CreditorDto> mergedCreditors = this.mergeAndSortResults(
                localResults, remoteResults);
        return mergedCreditors;
    }

    private List<CreditorDto> mergeAndSortResults(List<Creditor> localResults,
            Map<Integer, SubsidiaryContactInfoDto> remoteResults) {
        if (localResults == null) {
            return null;
        }
        List<CreditorDto> mergedCreditors = new ArrayList<CreditorDto>();
        for (Creditor cust : localResults) {
            SubsidiaryContactInfoDto contact = null;
            if (remoteResults != null) {
                contact = remoteResults.get(cust.getBusinessId());
                if (contact == null) {
                    continue;
                }
            }
            else {
                // Continue to build creditor DTO when contact data is not
                // available
                contact = Rmt2SubsidiaryDtoFactory
                        .createSubsidiaryInstance(null);
            }

            CreditorDto newCust = Rmt2SubsidiaryDtoFactory
                    .createCreditorInstance(cust, null);
            newCust.setContactName(contact.getContactName() == null ? "Unavailable"
                    : contact.getContactName());
            newCust.setContactPhone(contact.getContactPhone() == null ? "Unavailable"
                    : contact.getContactPhone());
            newCust.setContactFirstname(contact.getContactFirstname() == null ? "Unavailable"
                    : contact.getContactFirstname());
            newCust.setContactLastname(contact.getContactLastname() == null ? "Unavailable"
                    : contact.getContactLastname());
            newCust.setContactExt(contact.getContactExt() == null ? "Unavailable"
                    : contact.getContactExt());
            newCust.setTaxId(contact.getTaxId() == null ? "Unavailable"
                    : contact.getTaxId());
            newCust.setAddrId(contact.getAddrId());
            newCust.setAddr1(contact.getAddr1() == null ? "Unavailable"
                    : contact.getAddr1());
            newCust.setAddr2(contact.getAddr2() == null ? "Unavailable"
                    : contact.getAddr2());
            newCust.setAddr3(contact.getAddr3() == null ? "Unavailable"
                    : contact.getAddr3());
            newCust.setAddr4(contact.getAddr4() == null ? "Unavailable"
                    : contact.getAddr4());
            newCust.setCity(contact.getCity() == null ? "Unavailable" : contact
                    .getCity());
            newCust.setState(contact.getState() == null ? "Unavailable"
                    : contact.getState());
            newCust.setZip(contact.getZip());
            newCust.setZipext(contact.getZipext());
            newCust.setShortName(contact.getShortName() == null ? "Unavailable"
                    : contact.getShortName());
            mergedCreditors.add(newCust);
        }

        // return null if no creditors are found.
        if (mergedCreditors.size() == 0) {
            return null;
        }

        // Sort the list by name
        SubsidiaryComparator comp = new SubsidiaryComparator();
        Collections.sort(mergedCreditors, comp);
        comp = null;
        return mergedCreditors;
    }

    /**
     * Associate a transaction history item to a creditor subsidiary account.
     * 
     * @param creditorXact
     *            an instnace of {@link SubsidiaryXactHistoryDto}
     * @return the total number of items associated with the subsidiary account.
     * @throws SubsidiaryDaoException
     */
    @Override
    public int maintain(SubsidiaryXactHistoryDto creditorXact)
            throws SubsidiaryDaoException {
        if (creditorXact == null) {
            throw new CreditorDaoException(
                    "Input creditor subsidiary transaction item object is invalid or null");
        }
        CustomerActivity ca = SubsidiaryDaoFactory.createCustomerActivity(
                creditorXact.getSubsidiaryId(), creditorXact.getXactId(),
                creditorXact.getActivityAmount());
        UserTimestamp ut = RMT2Date.getUserTimeStamp(this.getDaoUser());
        ca.setDateCreated(ut.getDateCreated());
        ca.setDateUpdated(ut.getDateCreated());
        ca.setUserId(ut.getLoginId());
        ca.setIpCreated(ut.getIpAddr());
        ca.setIpUpdated(ut.getIpAddr());

        try {
            int rc = this.client.insertRow(ca, true);
            return rc;
        } catch (DatabaseException e) {
            throw new CreditorDaoException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dao.subsidiary.CreditorDao#maintain(org.dto.CreditorDto)
     */
    @Override
    public int maintain(CreditorDto cred) throws CreditorDaoException {
        Creditor orm = SubsidiaryDaoFactory.createRmt2OrmCreditorBean(cred);
        int rc = 0;
        if (orm.getCreditorId() <= 0) {
            rc = this.insert(orm);
        }
        else {
            rc = this.update(orm);
        }
        return rc;
    }

    private int insert(Creditor cred) throws CreditorDaoException {
        // Handle user update timestamps
        try {
            UserTimestamp ut = RMT2Date.getUserTimeStamp(this.getDaoUser());
            cred.setDateCreated(ut.getDateCreated());
            cred.setDateUpdated(ut.getDateCreated());
            cred.setUserId(ut.getLoginId());
            cred.setIpCreated(ut.getIpAddr());
            cred.setIpUpdated(ut.getIpAddr());
        } catch (Exception e) {
            throw new CreditorDaoException(e);
        }

        // Perform the actual insert of creditor.
        try {
            int newKey = this.client.insertRow(cred, true);
            cred.setCreditorId(newKey);
            return newKey;
        } catch (DatabaseException e) {
            throw new CreditorDaoException(e);
        }
    }

    private int update(Creditor cred) throws CreditorDaoException {
        // Handle user update timestamps
        try {
            UserTimestamp ut = RMT2Date.getUserTimeStamp(this.getDaoUser());
            cred.setDateUpdated(ut.getDateCreated());
            cred.setUserId(ut.getLoginId());
            cred.setIpUpdated(ut.getIpAddr());
        } catch (Exception e) {
            throw new CreditorDaoException(e);
        }

        // Perform the actual update of creditor.
        try {
            cred.addCriteria(Creditor.PROP_CREDITORID, cred.getCreditorId());
            int rc = this.client.updateRow(cred);
            return rc;
        } catch (DatabaseException e) {
            throw new CreditorDaoException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dao.subsidiary.CreditorDao#delete(int)
     */
    @Override
    public int delete(int credId) throws CreditorDaoException {
        Creditor criteria = new Creditor();
        try {
            criteria.addCriteria(Creditor.PROP_CREDITORID, credId);
            int rc = this.client.deleteRow(criteria);
            return rc;
        } catch (DatabaseException e) {
            throw new CreditorDaoException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dao.subsidiary.SubsidiaryDao#fetchDomain(org.dto.SubsidiaryDto)
     */
    @Override
    public List<CreditorDto> fetchDomain(SubsidiaryDto criteria)
            throws SubsidiaryDaoException {
        // Gather creditor criteria
        Creditor ormCred = null;
        if (criteria != null) {
            ormCred = new Creditor();
            if (criteria.getSubsidiaryId() > 0) {
                ormCred.addCriteria(Creditor.PROP_CREDITORID,
                        criteria.getSubsidiaryId());
            }
            if (criteria.getContactId() > 0) {
                ormCred.addCriteria(Creditor.PROP_BUSINESSID,
                        criteria.getContactId());
            }
            if (criteria.getAccountNo() != null) {
                ormCred.addCriteria(Creditor.PROP_ACCOUNTNUMBER,
                        criteria.getAccountNo());
            }
            if (criteria instanceof CreditorDto) {
                if (((CreditorDto) criteria).getCreditorTypeId() > 0) {
                    ormCred.addCriteria(Creditor.PROP_CREDITORTYPEID,
                            ((CreditorDto) criteria).getCreditorTypeId());
                }
            }
        }

        // Retrieve creditor local data
        List<Creditor> localResults = this.fetch(ormCred);
        return this.mergeAndSortResults(localResults, null);
    }

}