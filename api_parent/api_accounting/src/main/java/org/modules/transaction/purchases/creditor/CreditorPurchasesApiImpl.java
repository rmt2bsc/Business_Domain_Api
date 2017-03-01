package org.modules.transaction.purchases.creditor;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.dao.transaction.purchases.creditor.CreditorPurchasesDao;
import org.dao.transaction.purchases.creditor.CreditorPurchasesDaoException;
import org.dao.transaction.purchases.creditor.CreditorPurchasesDaoFactory;
import org.dto.CreditorDto;
import org.dto.XactCreditChargeDto;
import org.dto.XactDto;
import org.dto.XactTypeItemActivityDto;
import org.dto.adapter.orm.transaction.purchases.creditor.Rmt2CreditChargeDtoFactory;
import org.modules.subsidiary.CreditorApi;
import org.modules.subsidiary.CreditorApiException;
import org.modules.subsidiary.SubsidiaryApiFactory;
import org.modules.transaction.AbstractXactApiImpl;
import org.modules.transaction.XactApiException;
import org.modules.transaction.XactConst;

import com.NotFoundException;
import com.api.persistence.DaoClient;
import com.api.persistence.DatabaseException;
import com.util.RMT2Date;
import com.util.RMT2String;

/**
 * Api Implementation of CreditorPurchasesApi that manages creditor purchase
 * transactions.
 * 
 * @author Roy Terrell
 * 
 */
class CreditorPurchasesApiImpl extends AbstractXactApiImpl implements
        CreditorPurchasesApi {

    private static final Logger logger = Logger
            .getLogger(CreditorPurchasesApiImpl.class);

    private CreditorPurchasesDaoFactory daoFact;

    private CreditorPurchasesDao dao;

    /**
     * Creates an CreditorPurchasesApiImpl which creates a stand alone
     * connection.
     */
    public CreditorPurchasesApiImpl() {
        super();
        this.dao = this.daoFact.createRmt2OrmDao();
        this.setSharedDao(this.dao);
        return;
    }

    /**
     * Creates an CreditorPurchasesApiImpl which creates a stand alone
     * connection.
     * 
     * @param appName
     */
    public CreditorPurchasesApiImpl(String appName) {
        super();
        this.dao = this.daoFact.createRmt2OrmDao(appName);
        this.setSharedDao(this.dao);
        return;
    }

    /**
     * Creates an CreditorPurchasesApiImpl initialized with a shared connection,
     * <i>dao</i>. object.
     * 
     * @param connection
     */
    public CreditorPurchasesApiImpl(DaoClient connection) {
        super(connection);
        this.dao = this.daoFact.createRmt2OrmDao(this.getSharedDao());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modules.transaction.purchases.creditor.CreditorPurchasesApi#get(int)
     */
    @Override
    public XactCreditChargeDto get(int xactId)
            throws CreditorPurchasesApiException {
        XactCreditChargeDto criteria = Rmt2CreditChargeDtoFactory
                .createCreditChargeInstance(null, null);
        criteria.setXactId(xactId);
        List<XactCreditChargeDto> results = this.get(criteria);
        if (results == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        if (results.size() > 1) {
            buf.append("Error: Query method is expecting a single creditor transaction to be returned using transaction id, ");
            buf.append(xactId);
            buf.append(".  Instead ");
            buf.append(results.size());
            buf.append("  were returned.");
            this.msg = buf.toString();
            logger.error(this.msg);
            throw new CreditorPurchasesApiException(this.msg);
        }
        return results.get(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modules.transaction.purchases.creditor.CreditorPurchasesApi#get(java
     * .util.Date)
     */
    @Override
    public List<XactCreditChargeDto> get(Date xactDate)
            throws CreditorPurchasesApiException {
        if (xactDate == null) {
            this.msg = "Transaction date cannot be null";
            logger.error(this.msg);
            throw new CreditorPurchasesApiException(this.msg);
        }
        XactCreditChargeDto criteria = Rmt2CreditChargeDtoFactory
                .createCreditChargeInstance(null, null);
        criteria.setXactDate(xactDate);
        StringBuilder msgBuf = new StringBuilder();
        try {
            return this.get(criteria);
        } catch (Exception e) {
            msgBuf.append("Unable to retrieve creditor purchases by transaction date, ");
            msgBuf.append(RMT2Date.formatDate(xactDate, "MM-dd-yyyy"));
            this.msg = msgBuf.toString();
            logger.error(this.msg);
            throw new CreditorPurchasesApiException(this.msg, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modules.transaction.purchases.creditor.CreditorPurchasesApi#get(java
     * .lang.String)
     */
    @Override
    public List<XactCreditChargeDto> get(String criteria)
            throws CreditorPurchasesApiException {
        XactCreditChargeDto criteriaObj = Rmt2CreditChargeDtoFactory
                .createCreditChargeInstance(null, null);
        criteriaObj.setCriteria(criteria);
        StringBuilder msgBuf = new StringBuilder();
        try {
            return this.get(criteria);
        } catch (Exception e) {
            msgBuf.append("Unable to retrieve creditor purchases using custom criteria: ");
            msgBuf.append(criteria);
            this.msg = msgBuf.toString();
            logger.error(this.msg);
            throw new CreditorPurchasesApiException(this.msg, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modules.transaction.purchases.creditor.CreditorPurchasesApi#getByCreditor
     * (int)
     */
    @Override
    public List<XactCreditChargeDto> getByCreditor(int creditorId)
            throws CreditorPurchasesApiException {
        XactCreditChargeDto criteria = Rmt2CreditChargeDtoFactory
                .createCreditChargeInstance(null, null);
        criteria.setCreditorId(creditorId);
        StringBuilder msgBuf = new StringBuilder();
        try {
            return this.get(criteria);
        } catch (Exception e) {
            msgBuf.append("Unable to retrieve creditor purchases using creditor id: ");
            msgBuf.append(creditorId);
            this.msg = msgBuf.toString();
            logger.error(this.msg);
            throw new CreditorPurchasesApiException(this.msg, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modules.transaction.purchases.creditor.CreditorPurchasesApi#getByAcctNo
     * (java.lang.String)
     */
    @Override
    public List<XactCreditChargeDto> getByAcctNo(String accountNo)
            throws CreditorPurchasesApiException {
        XactCreditChargeDto criteria = Rmt2CreditChargeDtoFactory
                .createCreditChargeInstance(null, null);
        criteria.setAccountNumber(accountNo);
        StringBuilder msgBuf = new StringBuilder();
        try {
            return this.get(criteria);
        } catch (Exception e) {
            msgBuf.append("Unable to retrieve creditor purchases using creditor account number: ");
            msgBuf.append(accountNo);
            this.msg = msgBuf.toString();
            logger.error(this.msg);
            throw new CreditorPurchasesApiException(this.msg, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modules.transaction.purchases.creditor.CreditorPurchasesApi#
     * getByConfirmNo(java.lang.String)
     */
    @Override
    public List<XactCreditChargeDto> getByConfirmNo(String confirmNo)
            throws CreditorPurchasesApiException {
        XactCreditChargeDto criteria = Rmt2CreditChargeDtoFactory
                .createCreditChargeInstance(null, null);
        criteria.setXactConfirmNo(confirmNo);
        StringBuilder msgBuf = new StringBuilder();
        try {
            return this.get(criteria);
        } catch (Exception e) {
            msgBuf.append("Unable to retrieve creditor purchases using creditor transaction confirmation number: ");
            msgBuf.append(confirmNo);
            this.msg = msgBuf.toString();
            logger.error(this.msg);
            throw new CreditorPurchasesApiException(this.msg, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modules.transaction.purchases.creditor.CreditorPurchasesApi#getByReason
     * (java.lang.String)
     */
    @Override
    public List<XactCreditChargeDto> getByReason(String reason)
            throws CreditorPurchasesApiException {
        XactCreditChargeDto criteria = Rmt2CreditChargeDtoFactory
                .createCreditChargeInstance(null, null);
        criteria.setXactReason(reason);
        StringBuilder msgBuf = new StringBuilder();
        try {
            return this.get(criteria);
        } catch (Exception e) {
            msgBuf.append("Unable to retrieve creditor purchases using creditor transaction reason/source description: ");
            msgBuf.append(reason);
            this.msg = msgBuf.toString();
            logger.error(this.msg);
            throw new CreditorPurchasesApiException(this.msg, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modules.transaction.purchases.creditor.CreditorPurchasesApi#get(org
     * .dto.XactCreditChargeDto)
     */
    @Override
    public List<XactCreditChargeDto> get(XactCreditChargeDto criteria)
            throws CreditorPurchasesApiException {

        StringBuilder msgBuf = new StringBuilder();
        List<XactCreditChargeDto> results;
        try {
            results = this.dao.fetch(criteria);
            if (results == null) {
                msgBuf.append("Creditor purchase data was not found by multiple criteria object");
                logger.warn(msgBuf);
                return null;
            }
        } catch (CreditorPurchasesDaoException e) {
            msgBuf.append("Database error occurred retrieving creditor purchases by multiple criteria object");
            this.msg = msgBuf.toString();
            logger.error(this.msg);
            throw new CreditorPurchasesApiException(this.msg, e);
        }
        msgBuf.append(results.size());
        msgBuf.append(" Creditor purchase object(s) were retrieved");
        logger.info(msgBuf);
        return results;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modules.transaction.purchases.creditor.CreditorPurchasesApi#get(int)
     */
    @Override
    public List<XactTypeItemActivityDto> getItems(int xactId)
            throws CreditorPurchasesApiException {
        StringBuilder msgBuf = new StringBuilder();
        List<XactTypeItemActivityDto> results;
        try {
            results = this.dao.fetch(xactId);
            if (results == null) {
                msgBuf.append("Creditor purchase item data was not found by transaction id, ");
                msgBuf.append(xactId);
                logger.warn(msgBuf);
                return null;
            }
        } catch (CreditorPurchasesDaoException e) {
            msgBuf.append("Database error occurred retrieving creditor purchase item(s) by transaction Id, ");
            msgBuf.append(xactId);
            this.msg = msgBuf.toString();
            logger.error(this.msg);
            throw new CreditorPurchasesApiException(this.msg, e);
        }
        msgBuf.append(results.size());
        msgBuf.append(" Creditor purchase object(s) were retrieved by transaction id, ");
        msgBuf.append(xactId);
        logger.info(msgBuf);
        return results;
    }

    /**
     * Creates a new or reverses an existing creditor purchase transaction.
     * <p>
     * If the transaction id encapsulated in <i>xact</i> is 0, then a new
     * transaction is created. Otherwise, an existing transaction is reversed.
     * The creditor activity is always posted as an offset to the base
     * transaction amount.
     * 
     * @param xact
     *            An instance of {@link XactCreditChargeDto}
     * @param items
     *            A List of {@link XactTypeItemActivityDto} objects representing
     *            the transaction details.
     * @return The id of the transaction created.
     * @throws CreditorPurchasesApiException
     *             when <i>xact</i> is null, validation errors, or general
     *             database access errors.
     */
    @Override
    public int update(XactCreditChargeDto xact,
            List<XactTypeItemActivityDto> items)
            throws CreditorPurchasesApiException {

        if (xact == null) {
            this.msg = "Creditor purchase transaction object cannot be null";
            logger.error(this.msg);
            throw new CreditorPurchasesApiException(this.msg);
        }

        // Determine if we are creating or reversing the cash disbursement
        int xactId = 0;
        if (xact.getXactId() <= 0) {
            xact.setXactTypeId(XactConst.XACT_TYPE_CREDITCHARGE);
            xactId = this.createPurchase(xact, items);
        }
        else {
            xactId = this.reversePurchase(xact, items);
        }
        // Create the subsidiary entry for the creditor purchase transaction.
        try {
            super.createSubsidiaryTransaction(xact.getCreditorId(),
                    xact.getXactId(), xact.getXactAmount());
            return xactId;
        } catch (XactApiException e) {
            msg = "Unable to create subsiary entry for creditor purchase transacton";
            logger.error(msg, e);
            throw new CreditorPurchasesApiException(msg, e);
        }
    }

    /**
     * Creates a new creditor purchase transasction.
     * 
     * @param xact
     *            The transaction to be added to the database.
     * @param items
     *            An ArrayList of random objects.
     * @return The id of the new transaction.
     * @throws CreditorPurchasesApiException
     */
    protected int createPurchase(XactCreditChargeDto xact,
            List<XactTypeItemActivityDto> items)
            throws CreditorPurchasesApiException {
        try {
            int xactId = 0;
            xactId = super.update(xact, items);
            return xactId;
        } catch (XactApiException e) {
            throw new CreditorPurchasesApiException(e);
        }
    }

    /**
     * Prepends credit charge comments with a tag and assigns the transaction
     * negotiable instrument property to a masked credit card number.
     * <p>
     * If user did not input anything for the transction reason, then the method
     * is aborted which will allow postValidate to catch the error.
     * 
     * @param xact
     *            An instance of {@link XactCreditChargeDto}
     */
    @Override
    protected void preCreateXact(XactDto xact) {
        double xactAmount = 0;
        super.preCreateXact(xact);
        if (xact.getXactReason() == null || xact.getXactReason().equals("")) {
            return;
        }
        // Only modify reason for non-reversal cash receipts
        if (xact.getXactSubtypeId() == 0) {
            xact.setXactReason(xact.getXactReason());

            // Ensure that credit charge is posted to the base transaction table
            // as a negative amount.
            // xactAmount = _xact.getXactAmount() *
            // XactConst.REVERSE_MULTIPLIER;
            xactAmount = xact.getXactAmount();
            xact.setXactAmount(xactAmount);
        }
        int creditorId = 0;
        if (xact instanceof XactCreditChargeDto) {
            creditorId = ((XactCreditChargeDto) xact).getCreditorId();
        }

        // Assign last four digits of credit card number
        SubsidiaryApiFactory subFact = new SubsidiaryApiFactory();
        CreditorApi credApi = subFact.createCreditorApi(this.getSharedDao());
        try {
            CreditorDto creditor = credApi.getByCreditorId(creditorId);
            if (creditor == null) {
                this.msg = "Unable to create creditor purchase transction due to creditor's profile is not found in the database using creditor id: "
                        + creditorId;
                logger.error(this.msg);
                throw new NotFoundException();
            }
            String ccNoMask = RMT2String.maskCreditCardNumber(creditor
                    .getExtAccountNumber());
            xact.setXactNegInstrNo(ccNoMask);
        } catch (CreditorApiException e) {
            this.msg = "Unable to create creditor purchase transction due to the occurrence of a database error while attempting to fetch creditor's profile from the database using creditor id: "
                    + creditorId;
            logger.error(this.msg);
            throw new DatabaseException(this.msg, e);
        } finally {
            credApi = null;
        }
        return;
    }

    /**
     * Reverses an existing credit purchase transaction.
     * 
     * @param xact
     *            The target transaction
     * @param items
     *            A List of {@link XactTypeItemActivityDto} objects.
     * @return The id of the new transaction.
     * @throws CreditorPurchasesApiException
     *             If the transaction has already bee flagged as finalized or if
     *             a general transction error occurs.
     */
    protected int reversePurchase(XactCreditChargeDto xact,
            List<XactTypeItemActivityDto> items)
            throws CreditorPurchasesApiException {
        try {
            int xactId = 0;
            // Cannot reverse payment transaction that has been finalized
            if (!this.isTransModifiable(xact)) {
                msg = "Creditor purchase transaction cannot be reversed since it is already finalized";
                logger.error(msg);
                throw new CreditorPurchasesApiException(msg);
            }
            this.finalizeXact(xact);
            xactId = this.reverse(xact, null);
            return xactId;
        } catch (XactApiException e) {
            throw new CreditorPurchasesApiException(e);
        }
    }

    /**
     * Sets the transaction date prior to creating a transaction as a result of
     * reversing an existing creditor purchase transaction.
     * 
     * @param xact
     *            The transaction that is being reversed.
     * @param xactItems
     *            Transaction items to be reversed.
     */
    @Override
    protected void preReverse(XactDto xact,
            List<XactTypeItemActivityDto> xactItems) {
        super.preReverse(xact, xactItems);
        xact.setXactDate(new Date());
    }

    /**
     * This method checks if the creditor purchase transaction is valid and has
     * at least one line item. If successful, basic validations from the
     * ancestor are performed for <i>xact</> and <i>items</>.
     * 
     * @param xact
     *            {@link XactDto} instance.
     * @param items
     *            A List of {@link XactTypeItemActivityDto} instances.
     * @throws XactApiException
     *             When <i>xact</i> does not meet basic validation requirements,
     *             <i>items</i> is null or is empty, or basic validations fail.
     */
    @Override
    public void validate(XactDto xact, List<XactTypeItemActivityDto> items)
            throws XactApiException {
        if (items == null || items.size() == 0) {
            this.msg = "Creditor purchase transaction must contain at least one line item";
            logger.error(this.msg);
            throw new XactApiException(this.msg);
        }
        // Perform common validations
        super.validate(xact, items);
    }

    /**
     * Ensures that the base of the transaction meets general creditor purchase
     * validations.
     * <p>
     * The following validations must be satified:
     * <ul>
     * <li>Transaction date must have a value</li>
     * <li>Transaction date is a valid date</li>
     * <li>Transaction date is not greater than curent date</li>
     * <li>Transaction tender is entered</li>
     * <li>Transaction tender's negotiable instrument number is entered, if
     * applicable.</li>
     * <li>Transaction amount must be entered</li>
     * <li>Transaction reason is entered</li>
     * </ul>
     * 
     * @param xact
     *            The transaction object to be validated.
     * @throws XactException
     *             Validation error occurred.
     */
    @Override
    protected void postValidate(XactDto xact) throws XactApiException {
        super.postValidate(xact);
        java.util.Date today = new java.util.Date();

        // Verify that transaction date has a value.
        if (xact.getXactDate() == null) {
            this.msg = "Creditor Purchase transaction date is required";
            logger.error(this.msg);
            throw new XactApiException(this.msg);
        }
        // Verify that the transacton date value is valid
        if (xact.getXactDate().getTime() > today.getTime()) {
            this.msg = "Creditor Purchase transaction date cannot be in the future";
            logger.error(this.msg);
            throw new XactApiException(this.msg);
        }

        // Verify that transaction tender has a value and is valid.
        if (xact.getXactTenderId() != XactConst.TENDER_COMPANY_CREDIT
                && xact.getXactTenderId() != XactConst.TENDER_CREDITCARD) {
            this.msg = "Creditor Purchase Tender Type must be either Bank Credit Card or Finance Company Credit";
            logger.error(this.msg);
            throw new XactApiException(this.msg);
        }

        // Ensure that the source of the transction is entered.
        if (xact.getXactReason() == null || xact.getXactReason().equals("")) {
            this.msg = "Creditor purchase transaction reason/source cannot be blank...this is usually the name of the merchant or service provider";
            logger.error(this.msg);
            throw new XactApiException(this.msg);
        }
        return;
    }

}