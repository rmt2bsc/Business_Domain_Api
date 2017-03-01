package org.dao.mapping.orm.rmt2;


import java.util.Date;
import java.io.*;
import com.api.persistence.db.orm.OrmBean;
import com.SystemException;


/**
 * Peer object that maps to the av_genre database table/view.
 *
 * @author auto generated.
 */
public class AvGenre extends OrmBean {




	// Property name constants that belong to respective DataSource, AvGenreView

/** The property name constant equivalent to property, GenreId, of respective DataSource view. */
  public static final String PROP_GENREID = "GenreId";
/** The property name constant equivalent to property, Description, of respective DataSource view. */
  public static final String PROP_DESCRIPTION = "Description";



	/** The javabean property equivalent of database column av_genre.genre_id */
  private int genreId;
/** The javabean property equivalent of database column av_genre.description */
  private String description;



	// Getter/Setter Methods

/**
 * Default constructor.
 */
  public AvGenre() throws SystemException {
	super();
 }
/**
 * Sets the value of member variable genreId
 */
  public void setGenreId(int value) {
    this.genreId = value;
  }
/**
 * Gets the value of member variable genreId
 */
  public int getGenreId() {
    return this.genreId;
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
 * Stubbed initialization method designed to implemented by developer.

 */
  public void initBean() throws SystemException {}
}