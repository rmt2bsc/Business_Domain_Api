package org.modules.audiovideo.batch;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dao.audiovideo.AudioVideoDao;
import org.dao.audiovideo.AudioVideoDaoConstants;
import org.dao.audiovideo.AudioVideoDaoException;
import org.dao.audiovideo.AudioVideoDaoFactory;
import org.dao.mapping.orm.rmt2.AvArtist;
import org.dao.mapping.orm.rmt2.AvProject;
import org.dao.mapping.orm.rmt2.AvTracks;
import org.dto.ArtistDto;
import org.dto.GenreDto;
import org.dto.ProjectDto;
import org.dto.TracksDto;
import org.dto.adapter.orm.Rmt2MediaDtoFactory;
import org.modules.MediaConstants;
import org.modules.audiovideo.AudioVideoApiException;
import org.modules.audiovideo.AudioVideoFactory;
import org.modules.audiovideo.AvCombinedProjectBean;
import org.modules.audiovideo.AvProjectDataValidationException;
import org.modules.audiovideo.AvTrackDataValidationException;
import org.modules.audiovideo.MP3ApiInstantiationException;
import org.modules.audiovideo.MP3Reader;
import org.modules.audiovideo.Mp3ReaderIdentityNotConfiguredException;

import com.RMT2Constants;
import com.SystemException;
import com.api.BatchFileException;
import com.api.foundation.AbstractTransactionApiImpl;
import com.api.foundation.TransactionApi;
import com.api.messaging.email.EmailMessageBean;
import com.api.messaging.email.smtp.SmtpApi;
import com.api.messaging.email.smtp.SmtpFactory;
import com.api.persistence.DatabaseException;
import com.util.RMT2Date;
import com.util.RMT2File;

/**
 * An file loader implementation of {@link AudioVideoBatchFileProcessorApi}
 * which extracts meta data from audio/video files and imports the data to
 * various tables in the database.
 * <p>
 * The tables targeted for the data import are <i>av_artist</i>,
 * <i>av_project</i>, and <i>av_trackes</i>.
 * 
 * @author Roy Terrell
 * 
 */
class AvFileMetaDataLoaderApiImpl extends AbstractTransactionApiImpl implements AvBatchFileProcessorApi, 
        TransactionApi {

    private static Logger logger = Logger.getLogger(AvFileMetaDataLoaderApiImpl.class);
    
    private static Integer MP3_READER_IMPL_TO_USE;

    private File resourcePath;

    private List<String> fileErrorMsg;

    private Date startTime;

    private Date endTime;

    protected int successCnt;

    protected int errorCnt;

    protected int nonAvFileCnt;

    protected int totCnt;

    private int expectedFileCount;
    
    private AudioVideoDao avDao;

    /**
     * Creates a MetaDataFileLoaderApiImpl that does no point to a source
     * directory for batch.
     * 
     * @throws BatchFileProcessException
     */
    protected AvFileMetaDataLoaderApiImpl() throws BatchFileProcessException {
        super();
        return;
    }

    /**
     * Creates a MetaDataFileLoaderApiImpl pointing to the source directory for
     * batch processing audio/video files.
     * <p>
     * User is responsible for providing the directory where batch processing
     * starts.
     * 
     * @param dirPath
     *            the complete path where to start processing audio/video files
     * @throws BatchFileProcessException
     */
    protected AvFileMetaDataLoaderApiImpl(String dirPath) throws BatchFileProcessException {
        super(MediaConstants.APP_NAME);
        this.initConnection(dirPath);
        logger.info("Audio/Video batch processor is initialized.");
        logger.info("Audio/Video batch processing witll begin at this location: " + this.resourcePath.getAbsolutePath());
    }

    /**
     * Setup connection for an arbitrary external datasource which the
     * configuration is known at implementation.
     * 
     * @param dirPath
     *            a String representing the directory path process audio/video
     *            files
     * 
     * @throws BatchFileProcessException
     */
    public void initConnection(Object dirPath) throws BatchFileProcessException {
        if (dirPath == null) {
            this.msg = "The root directory path is invalid or null";
            AvFileMetaDataLoaderApiImpl.logger.error(this.msg);
            throw new InvalidBatchRootDirectoryException(this.msg);
        }
        if (!(dirPath instanceof String)) {
            this.msg = "The root directory path must be of String datatype";
            AvFileMetaDataLoaderApiImpl.logger.error(this.msg);
            throw new InvalidBatchRootDirectoryException(this.msg);
        }
        
        // Verify that the starting resource path is a directory
        File testFile = new File(dirPath.toString());
        if (RMT2File.verifyDirectory(testFile) != RMT2File.FILE_IO_EXIST) {
            this.msg = testFile + " is required to be a directory for Audio Video Batch process";
            throw new AvSourceNotADirectoryException(this.msg);
        }
        this.resourcePath = testFile;
        this.successCnt = 0;
        this.errorCnt = 0;
        this.totCnt = 0;
        this.nonAvFileCnt = 0;
        this.fileErrorMsg = new ArrayList<String>();
        
        // Setup DAO
        AudioVideoDaoFactory f = new AudioVideoDaoFactory();
        this.avDao = f.createRmt2OrmDaoInstance();
        this.avDao.setDaoUser(this.getApiUser());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.api.BatchFileProcessor#close()
     */
    @Override
    public void close() {
        return;
    }

    /**
     * Creates an audoi/video library (audio_video and audio_video_tracks
     * tables) from the files stored locally or remotely. Returns the total
     * number of tracks successfull processed. Data from the tables audio_video
     * and audio_video_tracks are deleted before processing artist's
     * directories.
     * 
     * @return the total count of media resources processed.
     * @throws AudioVideoException
     * @throws AvBatchValidationException
     */
    public int processBatch() throws BatchFileProcessException {
        this.startTime = new Date();
        AudioVideoDaoFactory f = new AudioVideoDaoFactory();
        AudioVideoDao dao = f.createRmt2OrmDaoInstance();
        dao.setDaoUser(this.getApiUser());

        // Begin process all files
        try {
            this.expectedFileCount = RMT2File.getDirectoryListingCount(this.resourcePath);
            this.msg = "Audio/Video Batch Update process started [" + this.expectedFileCount + " files discovered]...";
            this.processDirectory(this.resourcePath, null);
            return this.totCnt;
        } catch (BatchFileProcessException e) {
            this.msg = "A batch file error occurred";
            throw new BatchFileProcessException(this.msg, e);
        } catch (Mp3ReaderIdentityNotConfiguredException e) {
            this.msg = "An error occurred trying to identify the MP3Reader implemetation to use";
            throw new BatchFileProcessException(this.msg, e);
        } catch (Exception e) {
            this.msg = "An unknown error was discovered";
            throw new BatchFileProcessException(this.msg, e);
        } finally {
            dao.close();
            dao = null;
            this.endTime = new Date();
            
            logger.info("Batch start time: " + startTime.toString());
            logger.info("Batch end time: " + endTime.toString());
            logger.info("Total Media Files Processed: " + this.totCnt);
            logger.info("Total Media Files Successfully Processed: " + this.successCnt);
            logger.info("Total Media Files Unsuccessfully Processed: " + this.errorCnt);
            logger.info("Total Non-Audio/Video Files encountered: " + this.nonAvFileCnt);
            logger.info("End Audio-Video Update");

            // Send batch report via SMTP
            try {
                this.sendAvBatchReport();
            } catch (AvBatchReportException e) {
                logger.error("Audio/Video Batch Report Failed...See Log for details");
            }
        }

    }

    /**
     * Process all the high level audio/video artist directories.
     * 
     * @param mediaResource
     *            an instance of {@link File} which is required to point to the
     *            directory holding the media files to be processed.
     * @return Always returns an {@link Integer} object which equals "1".
     * @throws BatchFileProcessException
     */
    @Override
    public Object processDirectory(File mediaResource, Object parent) throws BatchFileProcessException {
        File mediaList[];
        int itemCount = 0;
        File mediaFile = null;
        try {
            mediaList = mediaResource.listFiles();
            itemCount = mediaList.length;
            for (int ndx = 0; ndx < itemCount; ndx++) {
                if (mediaList[ndx].isDirectory()) {
                    // Make recursive call to process next level
                    this.processDirectory(mediaList[ndx], mediaList[ndx]);
                }
                if (mediaList[ndx].isFile()) {
                    mediaFile = mediaList[ndx];
                    this.processSingleFile(mediaFile, parent);
                }
            }
            return 1;
        } catch (SecurityException e) {
            throw new BatchFileProcessException("A problem with security was discovered while processing file, "
                            + mediaFile.getAbsolutePath(), e);
        } catch (MP3ApiInstantiationException e) {
            throw new BatchFileProcessException("A problem instantiating MP3 library was discovered while processing file, "
                            + mediaFile.getAbsolutePath(), e);
        }
    }

    /**
     * Initiates the media file data extraction process.
     * 
     * @param mediaFile
     *            An instance of {@link File} which points to the media file to
     *            be processed
     * @return Always returns a instance of {@link Integer} which equals "1".
     * @throws MP3ApiInstantiationException
     * @throws BatchFileProcessException
     */
    @Override
    public Object processSingleFile(File mediaFile, Object parent) throws BatchFileProcessException {
        String pathName;
        AvCombinedProjectBean avb;

        File parentDirectory = (File) parent;
        pathName = mediaFile.getPath();
        logger.log(Level.DEBUG, "Processing File: " + pathName);
        try {
            avb = this.extractFileMetaData(mediaFile);
            if (avb != null) {
                this.addAudioVideoFileData(avb, parentDirectory);
            }
            this.successCnt++;
        } catch (AvInvalidSourceFileException e) {
            this.fileErrorMsg.add(this.buildFileErrorMessage(pathName, "Invalid Source File", e.getMessage()));
            this.errorCnt++;
        } catch (AvProjectDataValidationException e) {
            this.fileErrorMsg.add(this.buildFileErrorMessage(pathName, "Project Data Not Valid", e.getMessage()));
            this.errorCnt++;
        } catch (AvTrackDataValidationException e) {
            this.fileErrorMsg.add(this.buildFileErrorMessage(pathName, "Track Data Not Valid", e.getMessage()));
            this.errorCnt++;
        } catch (SystemException e) {
            this.fileErrorMsg.add(this.buildFileErrorMessage(pathName, "System Error", e.getMessage()));
            this.errorCnt++;
        } catch (AvFileExtractionException e) {
            this.fileErrorMsg.add(this.buildFileErrorMessage(pathName, "MetaData Extraction Error", e.getMessage()));
            this.errorCnt++;
        } catch (AudioVideoApiException e) {
            this.fileErrorMsg.add(this.buildFileErrorMessage(pathName, "General API Error", e.getMessage()));
            this.errorCnt++;
            e.printStackTrace();
        } catch (DatabaseException e) {
            this.fileErrorMsg.add(this.buildFileErrorMessage(pathName, "Database Access Error", e.getMessage()));
            this.errorCnt++;
        } finally {
            this.totCnt++;
        }
        return 1;
    }

    /**
     * Create an instance of MP3Reader based on the implementation selected in
     * configuration.
     * 
     * @param mp3Source
     * @return an instance of {@link MP3Reader}
     * @throws MP3ApiInstantiationException
     */
    protected MP3Reader getMp3ReaderInstance(File mp3Source) {
        // Determine the implementation to use for MP3Reader 
        if (AvFileMetaDataLoaderApiImpl.MP3_READER_IMPL_TO_USE == null) {
            String val;
            try {
                val = this.getConfig().getProperty(MediaConstants.MP3_READER_TO_USE_CONFIG_KEY);
                AvFileMetaDataLoaderApiImpl.MP3_READER_IMPL_TO_USE = Integer.valueOf(val);
            }
            catch (NumberFormatException e) {
                this.msg = "The configuration was not setup to identify the MP3 reader implementation to use for this API";
                throw new Mp3ReaderIdentityNotConfiguredException(this.msg, e);
            }
            catch (Exception e) {
                this.msg = "A general error occurred attempting to read configuration for MP3 reader implementation";
                throw new Mp3ReaderIdentityNotConfiguredException(this.msg, e);
            }
        }
        
        // Create MP3Reader based on selected implementation
        MP3Reader api = null;
        switch (AvFileMetaDataLoaderApiImpl.MP3_READER_IMPL_TO_USE) {
            case MediaConstants.MP3_READER_IMPL_ENTAGGED:
                api = AudioVideoFactory.createEntaggedId3Instance(mp3Source);
                break;
            case MediaConstants.MP3_READER_IMPL_ID3MP3WMV:
                api = AudioVideoFactory.createId3mp3WmvInstance(mp3Source);
                break;
            case MediaConstants.MP3_READER_IMPL_JID3:
                api = AudioVideoFactory.createJID3Mp3Instance(mp3Source);
                break;
            case MediaConstants.MP3_READER_IMPL_MYID3:
                api = AudioVideoFactory.createMyId3Instance(mp3Source);
                break;
            default:
                this.msg = "An invalid MP3 reader implementation code was specified in configuration: " + AvFileMetaDataLoaderApiImpl.MP3_READER_IMPL_TO_USE;
                throw new Mp3ReaderIdentityNotConfiguredException(this.msg);
        }
        return api;
    }
    
    
    /**
     * Reads the tag data from the media file, <i>sourceFile</i>, and packages
     * the data in an instance of AvCombinedProjectBean.
     * <p>
     * Afterwards, initiates the process of updating the database with tag data.
     * 
     * @param sourceFile
     *            the audio/video file to extract data from.
     * @return an instance of {@link AvCombinedProjectBean}
     * @throws AudioVideoDaoException
     */
    @Override
    public AvCombinedProjectBean extractFileMetaData(File sourceFile) throws AudioVideoDaoException {
        if (sourceFile == null) {
            this.msg = "Unable to extract meta data from audio/video file - The source file is invalid or null";
            throw new AvInvalidSourceFileException(this.msg);
        }

        AvCombinedProjectBean avb = new AvCombinedProjectBean();
        AvArtist ava = avb.getAva();
        AvProject av = avb.getAv();
        AvTracks avt = avb.getAvt();

        // Get file name with complet path
        String mediaPath = sourceFile.getPath();
        this.msg = "Extracting meta data from: " + mediaPath;
        logger.info(this.msg);

        // Get the appropriate MP3Reader implementation
        // MP3Reader mp3 = AudioVideoDaoFactory.createJid3mp3WmvApi(sourceFile);
        // MP3Reader mp3 = AudioVideoDaoFactory.createJID3Mp3Api(sourceFile);
        // MP3Reader mp3 = AudioVideoDaoFactory.createMyId3Api(sourceFile);
//        MP3Reader mp3 = AudioVideoDaoFactory.createEntaggedId3Instance(sourceFile);
        
        MP3Reader mp3 = this.getMp3ReaderInstance(sourceFile);
        if (mp3 == null) {
            return null;
        }

        String fileExt = RMT2File.getFileExt(mediaPath);
        if (fileExt.equalsIgnoreCase(".wmv") 
                || fileExt.equalsIgnoreCase(".mp4")
                || fileExt.equalsIgnoreCase(".avi")
                || fileExt.equalsIgnoreCase(".mpg")) {
            av.setProjectTypeId(AudioVideoDaoConstants.PROJ_TYPE_ID_VIDEO);
            av.setMediaTypeId(AudioVideoDaoConstants.MEDIA_TYPE_DVD);
        }
        else {
            // We are assuming that this is an audio file...may need to eliminate dangerous assumption.
            av.setProjectTypeId(AudioVideoDaoConstants.PROJ_TYPE_ID_AUDIO);
            av.setMediaTypeId(AudioVideoDaoConstants.MEDIA_TYPE_CD);
        }

        try {
            // Get Artist
            ava.setName(mp3.getArtist());

            // Get Album
            av.setTitle(mp3.getAlbum());

            // Get Track Number
            avt.setTrackNumber(mp3.getTrack());

            // Get Track Title
            avt.setTrackTitle(mp3.getTrackTitle());

            // Get comments.
            String comments = mp3.getComment();
            // Make data adjustments in the event we are dealing with a Various
            // Artists type album.
            if (comments != null && comments.contains(AudioVideoDaoConstants.VARIOUS_ARTIST_TOKEN)) {
                avt.setComments(comments);
                ava.setName(AudioVideoDaoConstants.VARIOUS_ARTIST_NAME);
            }

            // Get Genre
            avb.setGenre(mp3.getGenre());

            // Get Year Released
            av.setYear(mp3.getYear());

            // Get Recording Time
            List<Integer> list = mp3.getDuration();
            if (list != null && list.size() > 0) {
                avt.setTrackHours(list.get(0));
                avt.setTrackMinutes(list.get(1));
                avt.setTrackSeconds(list.get(2));
            }

            // Get Disc Number
            int discNo = mp3.getDiscNumber();
            if (discNo >= 1) {
                avt.setTrackDisc(String.valueOf(discNo));    
            }

            // Capture the media file location data
            String pathOnly = RMT2File.getFilePathInfo(mediaPath);
            avt.setLocPath(pathOnly);
            av.setContentPath(pathOnly);

            // Set File name
            String fileName = sourceFile.getName();
            avt.setLocFilename(fileName);

            // Initialized Ripped flag
            av.setRipped(mediaPath.indexOf(AudioVideoDaoConstants.DIRNAME_NON_RIPPED) > -1 ? 0 : 1);
            return avb;
        } catch (Exception e) {
            this.msg = "Audio/Video file extraction error for " + mediaPath;
            logger.error(this.msg, e);
            throw new AvFileExtractionException(this.msg, e);
        }
    }
    
    /**
     * Combines the efforts of adding artist, project, and all project tracks to
     * the tables, <i>av_artist</i>, <i>av_project</i>, and <i>av_tracks</i>,
     * respectively, under a single transaction.
     * <p>
     * The update sequence mandates that the <i>av_artist</i>,
     * <i>av_project</i>, and <i>av_tracks</i> tables are inserted into the
     * order stipulated. For eachtable encounterd, a SQL insert is performed
     * when the tables's primary key is equal to zero. When the primary key id
     * is greater than zero, an SQL update is applied.
     * 
     * 
     * @param avProj
     *            an instance of {@link AvCombinedProjectBean}
     * @param parentDirectory
     *            the directory containing the information gathered for
     *            <i>avProj<i>.
     * @return The total number of tracks added for the artist's project.
     * @throws AudioVideoApiException
     */
    protected int addAudioVideoFileData(AvCombinedProjectBean avProj, File parentDirectory) throws AudioVideoApiException {
        AvArtist artist = avProj.getAva();
        AvProject project = avProj.getAv();
        AvTracks track = avProj.getAvt();

        // Process artist
        int artistId = this.insertArtistFromFile(artist);
        project.setArtistId(artistId);
        
        // Process Project/Album
        int projectId = this.insertProjectFromFile(project, avProj.getGenre(), parentDirectory);
        track.setProjectId(projectId);
        
        // Process track
        this.insertTrackFromFile(track);
        return projectId;
    }
    
    private int insertArtistFromFile(AvArtist artist) throws AudioVideoApiException {
        ArtistDto artistDto = Rmt2MediaDtoFactory.getAvArtistInstance(artist);
        this.validateArtist(artistDto);
        int artistId = 0;
        
        // Check if artist exists
        try {
            if (artist.getArtistId() == 0) {
                ArtistDto artistCriteria = Rmt2MediaDtoFactory.getAvArtistInstance(null);
                artistCriteria.setName(artistDto.getName());
                List<ArtistDto> a = this.avDao.fetchArtist(artistCriteria);
                if (a != null && a.size() == 1) {
                    artistId = a.get(0).getId();
                    artistDto.setId(artistId);
                    artist.setArtistId(artistId);
                }
            }    
        }
        catch (AudioVideoDaoException e) {
            throw new AudioVideoApiException("Unable to verify artist existence by artist name: " + artistDto.getName(), e); 
        }
        
        // Since artist does not exist, add it.
        try {
            if (artist.getArtistId() == 0) {
                artistId = this.avDao.maintainArtist(artistDto);
            }
        }
        catch (AudioVideoDaoException e) {
            throw new AudioVideoApiException("Unable to create artist: " + artistDto.getName(), e); 
        }
        return artistId;
    }

    private int insertProjectFromFile(AvProject project, String genreName, File parentDirectory) throws AudioVideoApiException {
        int artistId = project.getArtistId();
        ProjectDto projectDto = Rmt2MediaDtoFactory.getAvProjectInstance(project);
        int projectId = 0;
        project.setArtistId(artistId);
        projectDto.setArtistId(artistId);
        this.validateProject(projectDto);

        GenreDto genreCriteria = Rmt2MediaDtoFactory.getAvGenreInstance(null);
        genreCriteria.setDescription(genreName);
        List<GenreDto> g = this.avDao.fetchGenre(genreCriteria);
        int genreId = AudioVideoDaoConstants.UNKNOWN_GENRE;
        if (g != null && g.size() == 1) {
            genreId = g.get(0).getUid();
        }
        project.setGenreId(genreId);
        projectDto.setGenreId(genreId);
        
        // Verify if project already exists
        if (project.getProjectId() == 0) {
            ProjectDto projCriteria = Rmt2MediaDtoFactory.getAvProjectInstance(null);
            
            // Verify the existence of project by artist/project title
            projCriteria.setTitle(projectDto.getTitle());
            projCriteria.setArtistId(artistId);
            List<ProjectDto> p = null;
            try {
                p = this.avDao.fetchProject(projCriteria);    
            }
            catch (AudioVideoDaoException e) {
                throw new AudioVideoApiException(
                        "Unable to verify project/album existence by artist id and title ["
                                + artistId + ", " + projectDto.getTitle() + "]", e);
            }
            if (p != null && p.size() == 1) {
                projectId = p.get(0).getProjectId();
                project.setProjectId(projectId);
                projectDto.setProjectId(projectId);
            }
            else {
                // Verify the existence of project by project title/content path
                // (This is typically for albums with multiple artists).
                projCriteria = Rmt2MediaDtoFactory.getAvProjectInstance(null);
                projCriteria.setTitle(projectDto.getTitle());
                projCriteria.setContentPath(projectDto.getContentPath());
                try {
                    p = this.avDao.fetchProject(projCriteria);    
                }
                catch (AudioVideoDaoException e) {
                    throw new AudioVideoApiException(
                            "Unable to verify project/album existence by title and project content path ["
                                    + projectDto.getTitle() + ", "
                                    + projectDto.getContentPath() + "]", e);
                }
                if (p != null && p.size() == 1) {
                    projectId = p.get(0).getProjectId();
                    project.setProjectId(projectId);
                    projectDto.setProjectId(projectId);
                }
            }
        }
        
        try {
            if (projectId == 0) {
                // Create project/album
                projectId = this.avDao.maintainProject(projectDto);
            }
            else {
                this.avDao.maintainProject(projectDto);
            }    
        }
        catch (AudioVideoDaoException e) {
            throw new AudioVideoApiException("Unable to create/modify project/album: " + project.getTitle(), e); 
        }
        return projectId;
    }

    private int insertTrackFromFile(AvTracks track) throws AudioVideoApiException {
        TracksDto trackDto = Rmt2MediaDtoFactory.getAvTrackInstance(track);
        int trackId = 0;
        trackDto.setProjectId(track.getProjectId());
        this.validateTarck(trackDto);

        // Verify that track does not exists for this project
        try {
            if (track.getTrackId() == 0) {
                TracksDto criteria = Rmt2MediaDtoFactory.getAvTrackInstance(null);
                criteria.setProjectId(track.getProjectId());
                criteria.setTrackTitle(track.getTrackTitle());
                List<TracksDto> t = this.avDao.fetchTrack(criteria);
                if (t != null && t.size() == 1) {
                    trackId = t.get(0).getTrackId();
                }
            }
        }
        catch (AudioVideoDaoException e) {
            throw new AudioVideoApiException("Unable to verify track existence: " + track.getTrackTitle(), e); 
        }
       
        try {
            if (trackId == 0) {
                // Create new track
                trackId = this.avDao.maintainTrack(trackDto);
                track.setTrackId(trackId);
                trackDto.setTrackId(trackId);
            }
            else {
                // Update existing track
                this.avDao.maintainTrack(trackDto);
            }    
        }
        catch (AudioVideoDaoException e) {
            throw new AudioVideoApiException("Unable to create/modify track: " + track.getTrackTitle(), e); 
        }
        return trackId;
    }
    
    /**
     * Verifies if <i>artist</i> is valid.
     * <p>
     * The artist object cannot be null and the artist's name is required to
     * have a value.
     * 
     * @param artist
     *            an instance of {@link ArtistDto}
     * @throws AvProjectDataValidationException
     *             a validation rule fails
     */
    protected void validateArtist(ArtistDto artist) throws AvProjectDataValidationException {
        if (artist == null) {
            this.msg = "Artist object is invalid or null";
            throw new AvProjectDataValidationException(this.msg);
        }
        if (artist.getName() == null) {
            throw new AvProjectDataValidationException("Artist name is required");
        }
        return;
    }
    
    /**
     * Verifies that a project object is valid.
     * <p>
     * <i>proj</i> must be a valid instance, and the following properties are
     * required to have values: artist id, project type id, and title.
     * 
     * @param proj
     *            an instance of {@link ProjectDto}
     * @throws AvProjectDataValidationException
     *             a validation rule fails
     */
    protected void validateProject(ProjectDto proj) throws AvProjectDataValidationException {
        if (proj == null) {
            this.msg = "Project object is invalid or null";
            throw new AvProjectDataValidationException(this.msg);
        }
        if (proj.getArtistId() <= 0) {
            this.msg = "Artist id is required";
            throw new AvProjectDataValidationException(this.msg);
        }
        if (proj.getProjectTypeId() < 1 || proj.getProjectTypeId() > 2) {
            this.msg = "Project Type id is required";
            throw new AvProjectDataValidationException(this.msg);
        }
        if (proj.getTitle() == null || proj.getTitle().length() <= 0) {
            this.msg = "Title id is required";
            throw new AvProjectDataValidationException(this.msg);
        }
    }

    /**
     * Verifies that the tracks object is valid for database updates.
     * <p>
     * A track object is considered valid for database updates when it is not
     * null, track title has a value, and track number is greater than zero.
     * 
     * @param track
     *            an instance of {@link TracksDto}
     * @throws AvProjectDataValidationException
     *             a validation rule fails
     */
    protected void validateTarck(TracksDto track) throws AvProjectDataValidationException {
        if (track == null) {
            this.msg = "Track object is invalid or null";
            throw new AvProjectDataValidationException(this.msg);
        }
        if (track.getTrackTitle() == null) {
            this.msg = "Track title is required";
            throw new AvProjectDataValidationException(this.msg);
        }
        if (track.getTrackNumber() <= 0) {
            this.msg = "Track number must be a numeric greater than zero";
            throw new AvProjectDataValidationException(this.msg);
        }
        return;
    }
    /**
     * 
     * @param fileName
     * @param msgCatg
     * @param msg
     * @return
     */
    private String buildFileErrorMessage(String fileName, String msgCatg, String msg) {
        StringBuffer errMsg = new StringBuffer();
        errMsg.append("File: ");
        errMsg.append(fileName);
        errMsg.append("\n");
        errMsg.append("Error Category: ");
        errMsg.append(msgCatg);
        errMsg.append("\n");
        errMsg.append("Cause: ");
        if (msg == null) {
            msg = "Unknown";
        }
        errMsg.append(msg);
        errMsg.append("\n");

        String m = errMsg.toString();
        System.out.println("Error ===> " + m);
        logger.log(Level.ERROR, "Error ===> " + m);
        return m;
    }

    /**
     * Creates a report detailing the success or failure of all the files
     * processed and transmits the report via SMTP to the user designated as the
     * application's email recipient.
     * 
     * @throws AvBatchReportException
     *             problem occurred creaing or sending file drop report via
     *             SMTP.
     */
    private void sendAvBatchReport() throws AvBatchReportException {
        StringBuffer body = new StringBuffer();

        // Attempt to obtain From email address from the application's property
        // pool which is loaded at server start up.
        String fromAddr = null;
        try {
            fromAddr = this.getConfig().getProperty(MediaConstants.BATCH_FILE_IMPORT_REPORT_EMAIL);
        } catch (Exception e) {
            this.msg = "Unable to obtain the recipient's email address from AppParms.properties needed to send batch report";
            throw new AvBatchReportException(this.msg, e);
        }
        if (fromAddr == null) {
            this.msg = "Unable to send batch report due to the recipient's email address is not available.   Check configuation.";
            throw new AvBatchReportException(this.msg);
        }
        String toAddr = fromAddr;
        String subject = this.getConfig().getProperty(MediaConstants.BATCH_FILE_IMPORT_REPORT_SUBJECT);

        body.append("This is a report of the results of the Audio/Video Batch File Import process\n");
        body.append("Start Time: ");
        body.append(RMT2Date.formatDate(this.startTime, "MM-dd-yyyy HH:mm:ss.S"));
        body.append("\n");
        body.append("End Time: ");
        body.append(RMT2Date.formatDate(this.endTime, "MM-dd-yyyy HH:mm:ss.S"));
        body.append("\n\n\n");

        body.append("Total Media Files Processed: " + this.totCnt);
        body.append("\n");
        body.append("Total Media Files Successfully Processed: " + this.successCnt);
        body.append("\n");
        body.append("Total Media Files Unsuccessfully Processed: " + this.errorCnt);
        body.append("\n\n\n");

        if (this.fileErrorMsg.size() > 0) {
            body.append("Detail report of files that failed during this batch process");
            body.append("\n");
            body.append("=============================================================");
            body.append("\n");
        }
        else {
            body.append("All audio/video files processed successfully!");
            body.append("\n");
        }
        int count = 0;
        // Add details about each file that was processed
        for (String msg : this.fileErrorMsg) {
            count++;
            body.append(count);
            body.append(".  ");
            body.append(msg);
            body.append("\n");
        }

        // Setup bean that represents the email message.
        EmailMessageBean bean = new EmailMessageBean();
        bean.setFromAddress(fromAddr);

        // You can optionally enter multiple email addresses separated by commas
        bean.setToAddress(toAddr);
        bean.setSubject(subject);
        bean.setBody(body.toString(), EmailMessageBean.TEXT_CONTENT);

        // Declare and initialize SMTP api and allow the system to discover SMTP
        // host
        SmtpApi api = SmtpFactory.getSmtpInstance();
        // Send simple email to its intended destination
        try {
            api.sendMessage(bean);
            // Close the service.
            api.close();
        } catch (Exception e) {
            throw new AvBatchReportException(e);
        }
        return;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.api.BatchFileProcessor#getFileListing()
     */
    public List<String> getFileListing() {
        throw new UnsupportedOperationException(RMT2Constants.MSG_METHOD_NOT_SUPPORTED);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.api.BatchFileProcessor#initConnection()
     */
    public void initConnection() throws BatchFileException {
        throw new UnsupportedOperationException(RMT2Constants.MSG_METHOD_NOT_SUPPORTED);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.api.BatchFileProcessor#initConnection(java.lang.String,
     * java.lang.String)
     */
    public void initConnection(String arg0, String arg1)
            throws BatchFileException {
        throw new UnsupportedOperationException(RMT2Constants.MSG_METHOD_NOT_SUPPORTED);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.api.BatchFileProcessor#processFiles(java.util.List)
     */
    public Object processFiles(List<String> arg0, Object parent)
            throws BatchFileException {
        throw new UnsupportedOperationException(RMT2Constants.MSG_METHOD_NOT_SUPPORTED);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.api.BatchFileProcessor#processSingleFile(java.lang.String)
     */
    public Object processSingleFile(String arg0, Object parent)
            throws BatchFileException {
        throw new UnsupportedOperationException(RMT2Constants.MSG_METHOD_NOT_SUPPORTED);
    }

}