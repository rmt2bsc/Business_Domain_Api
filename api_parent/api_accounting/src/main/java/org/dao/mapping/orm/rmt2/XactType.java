package org.dao.mapping.orm.rmt2;


import java.util.Date;
import java.io.*;
import com.api.util.assistants.EqualityAssistant;
import com.api.util.assistants.HashCodeAssistant;
import com.api.persistence.db.orm.OrmBean;
import com.SystemException;


/**
 * Peer object that maps to the xact_type database table/view.
 *
 * @author auto generated.
 */
public class XactType extends OrmBean {




	// Property name constants that belong to respective DataSource, XactTypeView

/** The property name constant equivalent to property, XactTypeId, of respective DataSource view. */
  public static final String PROP_XACTTYPEID = "XactTypeId";
/** The property name constant equivalent to property, XactCatgId, of respective DataSource view. */
  public static final String PROP_XACTCATGID = "XactCatgId";
/** The property name constant equivalent to property, Description, of respective DataSource view. */
  public static final String PROP_DESCRIPTION = "Description";
/** The property name constant equivalent to property, Code, of respective DataSource view. */
  public static final String PROP_CODE = "Code";
/** The property name constant equivalent to property, ToMultiplier, of respective DataSource view. */
  public static final String PROP_TOMULTIPLIER = "ToMultiplier";
/** The property name constant equivalent to property, FromMultiplier, of respective DataSource view. */
  public static final String PROP_FROMMULTIPLIER = "FromMultiplier";
/** The property name constant equivalent to property, ToAcctTypeId, of respective DataSource view. */
  public static final String PROP_TOACCTTYPEID = "ToAcctTypeId";
/** The property name constant equivalent to property, ToAcctCatgId, of respective DataSource view. */
  public static final String PROP_TOACCTCATGID = "ToAcctCatgId";
/** The property name constant equivalent to property, FromAcctTypeId, of respective DataSource view. */
  public static final String PROP_FROMACCTTYPEID = "FromAcctTypeId";
/** The property name constant equivalent to property, FromAcctCatgId, of respective DataSource view. */
  public static final String PROP_FROMACCTCATGID = "FromAcctCatgId";
/** The property name constant equivalent to property, HasSubsidiary, of respective DataSource view. */
  public static final String PROP_HASSUBSIDIARY = "HasSubsidiary";



	/** The javabean property equivalent of database column xact_type.xact_type_id */
  private int xactTypeId;
/** The javabean property equivalent of database column xact_type.xact_catg_id */
  private int xactCatgId;
/** The javabean property equivalent of database column xact_type.description */
  private String description;
/** The javabean property equivalent of database column xact_type.code */
  private String code;
/** The javabean property equivalent of database column xact_type.to_multiplier */
  private int toMultiplier;
/** The javabean property equivalent of database column xact_type.from_multiplier */
  private int fromMultiplier;
/** The javabean property equivalent of database column xact_type.to_acct_type_id */
  private int toAcctTypeId;
/** The javabean property equivalent of database column xact_type.to_acct_catg_id */
  private int toAcctCatgId;
/** The javabean property equivalent of database column xact_type.from_acct_type_id */
  private int fromAcctTypeId;
/** The javabean property equivalent of database column xact_type.from_acct_catg_id */
  private int fromAcctCatgId;
/** The javabean property equivalent of database column xact_type.has_subsidiary */
  private int hasSubsidiary;



	// Getter/Setter Methods

/**
 * Default constructor.
 */
  public XactType() throws SystemException {
	super();
 }
/**
 * Sets the value of member variable xactTypeId
 */
  public void setXactTypeId(int value) {
    this.xactTypeId = value;
  }
/**
 * Gets the value of member variable xactTypeId
 */
  public int getXactTypeId() {
    return this.xactTypeId;
  }
/**
 * Sets the value of member variable xactCatgId
 */
  public void setXactCatgId(int value) {
    this.xactCatgId = value;
  }
/**
 * Gets the value of member variable xactCatgId
 */
  public int getXactCatgId() {
    return this.xactCatgId;
  }
/**
 * Sets the value of member variable description
 */
  public void setDescription(String value) {
    this.description = value;
  }
/**
 * Gets the value of member variable description
 */
  public String getDescription() {
    return this.description;
  }
/**
 * Sets the value of member variable code
 */
  public void setCode(String value) {
    this.code = value;
  }
/**
 * Gets the value of member variable code
 */
  public String getCode() {
    return this.code;
  }
/**
 * Sets the value of member variable toMultiplier
 */
  public void setToMultiplier(int value) {
    this.toMultiplier = value;
  }
/**
 * Gets the value of member variable toMultiplier
 */
  public int getToMultiplier() {
    return this.toMultiplier;
  }
/**
 * Sets the value of member variable fromMultiplier
 */
  public void setFromMultiplier(int value) {
    this.fromMultiplier = value;
  }
/**
 * Gets the value of member variable fromMultiplier
 */
  public int getFromMultiplier() {
    return this.fromMultiplier;
  }
/**
 * Sets the value of member variable toAcctTypeId
 */
  public void setToAcctTypeId(int value) {
    this.toAcctTypeId = value;
  }
/**
 * Gets the value of member variable toAcctTypeId
 */
  public int getToAcctTypeId() {
    return this.toAcctTypeId;
  }
/**
 * Sets the value of member variable toAcctCatgId
 */
  public void setToAcctCatgId(int value) {
    this.toAcctCatgId = value;
  }
/**
 * Gets the value of member variable toAcctCatgId
 */
  public int getToAcctCatgId() {
    return this.toAcctCatgId;
  }
/**
 * Sets the value of member variable fromAcctTypeId
 */
  public void setFromAcctTypeId(int value) {
    this.fromAcctTypeId = value;
  }
/**
 * Gets the value of member variable fromAcctTypeId
 */
  public int getFromAcctTypeId() {
    return this.fromAcctTypeId;
  }
/**
 * Sets the value of member variable fromAcctCatgId
 */
  public void setFromAcctCatgId(int value) {
    this.fromAcctCatgId = value;
  }
/**
 * Gets the value of member variable fromAcctCatgId
 */
  public int getFromAcctCatgId() {
    return this.fromAcctCatgId;
  }
/**
 * Sets the value of member variable hasSubsidiary
 */
  public void setHasSubsidiary(int value) {
    this.hasSubsidiary = value;
  }
/**
 * Gets the value of member variable hasSubsidiary
 */
  public int getHasSubsidiary() {
    return this.hasSubsidiary;
  }

@Override
public boolean equals(Object obj) {
   if (this == obj) {
      return true;
   }
   if (obj == null) {
      return false;
   }
   if (getClass() != obj.getClass()) {
      return false;
   }
   final XactType other = (XactType) obj; 
   if (EqualityAssistant.notEqual(this.xactTypeId, other.xactTypeId)) {
      return false;
   }
   if (EqualityAssistant.notEqual(this.xactCatgId, other.xactCatgId)) {
      return false;
   }
   if (EqualityAssistant.notEqual(this.description, other.description)) {
      return false;
   }
   if (EqualityAssistant.notEqual(this.code, other.code)) {
      return false;
   }
   if (EqualityAssistant.notEqual(this.toMultiplier, other.toMultiplier)) {
      return false;
   }
   if (EqualityAssistant.notEqual(this.fromMultiplier, other.fromMultiplier)) {
      return false;
   }
   if (EqualityAssistant.notEqual(this.toAcctTypeId, other.toAcctTypeId)) {
      return false;
   }
   if (EqualityAssistant.notEqual(this.toAcctCatgId, other.toAcctCatgId)) {
      return false;
   }
   if (EqualityAssistant.notEqual(this.fromAcctTypeId, other.fromAcctTypeId)) {
      return false;
   }
   if (EqualityAssistant.notEqual(this.fromAcctCatgId, other.fromAcctCatgId)) {
      return false;
   }
   if (EqualityAssistant.notEqual(this.hasSubsidiary, other.hasSubsidiary)) {
      return false;
   }
   return true; 
} 

@Override
public int hashCode() {
   return HashCodeAssistant.combineHashCodes(HashCodeAssistant.hashObject(this.xactTypeId),
               HashCodeAssistant.hashObject(this.xactCatgId),
               HashCodeAssistant.hashObject(this.description),
               HashCodeAssistant.hashObject(this.code),
               HashCodeAssistant.hashObject(this.toMultiplier),
               HashCodeAssistant.hashObject(this.fromMultiplier),
               HashCodeAssistant.hashObject(this.toAcctTypeId),
               HashCodeAssistant.hashObject(this.toAcctCatgId),
               HashCodeAssistant.hashObject(this.fromAcctTypeId),
               HashCodeAssistant.hashObject(this.fromAcctCatgId),
               HashCodeAssistant.hashObject(this.hasSubsidiary));
} 

@Override
public String toString() {
   return "XactType [xactTypeId=" + xactTypeId + 
          ", xactCatgId=" + xactCatgId + 
          ", description=" + description + 
          ", code=" + code + 
          ", toMultiplier=" + toMultiplier + 
          ", fromMultiplier=" + fromMultiplier + 
          ", toAcctTypeId=" + toAcctTypeId + 
          ", toAcctCatgId=" + toAcctCatgId + 
          ", fromAcctTypeId=" + fromAcctTypeId + 
          ", fromAcctCatgId=" + fromAcctCatgId + 
          ", hasSubsidiary=" + hasSubsidiary  + "]";
}

/**
 * Stubbed initialization method designed to implemented by developer.

 */
  public void initBean() throws SystemException {}
}