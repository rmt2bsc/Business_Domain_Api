package org.dao.mapping.orm.rmt2;


import com.SystemException;
import com.api.persistence.db.orm.OrmBean;

/**
 * Peer object that maps to the user_resource_access database table/view.
 *
 * 
 */
public class UserResourceAccess extends OrmBean {




	// Property name constants that belong to respective DataSource, UserResourceAccessView

/** The property name constant equivalent to property, RsrcAccessId, of respective DataSource view. */
  public static final String PROP_RSRCACCESSID = "RsrcAccessId";
/** The property name constant equivalent to property, GrpId, of respective DataSource view. */
  public static final String PROP_GRPID = "GrpId";
/** The property name constant equivalent to property, RsrcId, of respective DataSource view. */
  public static final String PROP_RSRCID = "RsrcId";
/** The property name constant equivalent to property, LoginId, of respective DataSource view. */
  public static final String PROP_LOGINID = "LoginId";
/** The property name constant equivalent to property, DateUpdated, of respective DataSource view. */
  public static final String PROP_DATEUPDATED = "DateUpdated";
/** The property name constant equivalent to property, DateCreated, of respective DataSource view. */
  public static final String PROP_DATECREATED = "DateCreated";
/** The property name constant equivalent to property, UserId, of respective DataSource view. */
  public static final String PROP_USERID = "UserId";



	/** The javabean property equivalent of database column user_resource_access.rsrc_access_id */
  private int rsrcAccessId;
/** The javabean property equivalent of database column user_resource_access.grp_id */
  private int grpId;
/** The javabean property equivalent of database column user_resource_access.rsrc_id */
  private int rsrcId;
/** The javabean property equivalent of database column user_resource_access.login_id */
  private int loginId;
/** The javabean property equivalent of database column user_resource_access.date_updated */
  private java.util.Date dateUpdated;
/** The javabean property equivalent of database column user_resource_access.date_created */
  private java.util.Date dateCreated;
/** The javabean property equivalent of database column user_resource_access.user_id */
  private String userId;



	// Getter/Setter Methods

/**
 * Default constructor.
 *
 * 
 */
  public UserResourceAccess() throws SystemException {
	super();
 }
/**
 * Sets the value of member variable rsrcAccessId
 *
 * 
 */
  public void setRsrcAccessId(int value) {
    this.rsrcAccessId = value;
  }
/**
 * Gets the value of member variable rsrcAccessId
 *
 * 
 */
  public int getRsrcAccessId() {
    return this.rsrcAccessId;
  }
/**
 * Sets the value of member variable grpId
 *
 * 
 */
  public void setGrpId(int value) {
    this.grpId = value;
  }
/**
 * Gets the value of member variable grpId
 *
 * 
 */
  public int getGrpId() {
    return this.grpId;
  }
/**
 * Sets the value of member variable rsrcId
 *
 * 
 */
  public void setRsrcId(int value) {
    this.rsrcId = value;
  }
/**
 * Gets the value of member variable rsrcId
 *
 * 
 */
  public int getRsrcId() {
    return this.rsrcId;
  }
/**
 * Sets the value of member variable loginId
 *
 * 
 */
  public void setLoginId(int value) {
    this.loginId = value;
  }
/**
 * Gets the value of member variable loginId
 *
 * 
 */
  public int getLoginId() {
    return this.loginId;
  }
/**
 * Sets the value of member variable dateUpdated
 *
 * 
 */
  public void setDateUpdated(java.util.Date value) {
    this.dateUpdated = value;
  }
/**
 * Gets the value of member variable dateUpdated
 *
 * 
 */
  public java.util.Date getDateUpdated() {
    return this.dateUpdated;
  }
/**
 * Sets the value of member variable dateCreated
 *
 * 
 */
  public void setDateCreated(java.util.Date value) {
    this.dateCreated = value;
  }
/**
 * Gets the value of member variable dateCreated
 *
 * 
 */
  public java.util.Date getDateCreated() {
    return this.dateCreated;
  }
/**
 * Sets the value of member variable userId
 *
 * 
 */
  public void setUserId(String value) {
    this.userId = value;
  }
/**
 * Gets the value of member variable userId
 *
 * 
 */
  public String getUserId() {
    return this.userId;
  }
/**
 * Stubbed initialization method designed to implemented by developer.

 *
 * 
 */
  public void initBean() throws SystemException {}
}