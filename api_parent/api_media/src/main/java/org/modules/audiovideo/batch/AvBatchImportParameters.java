package org.modules.audiovideo.batch;

/**
 * Class for managing audio video batch import request parameters.
 * 
 * @author appdev
 *
 */
public class AvBatchImportParameters {

    private String serverName;
    private String shareName;
    private String rootPath;
    private String path;
    private String importFilePath;

    public AvBatchImportParameters() {

    }

    /**
     * Get the server name
     * 
     * @return the serverName
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Set the server name.
     * 
     * @param serverName
     *            the serverName to set. Do not include leading and ending "/"
     *            characters
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * Get the share name.
     * 
     * @return the shareName
     */
    public String getShareName() {
        return shareName;
    }

    /**
     * Set the share name.
     * 
     * @param shareName
     *            the shareName to set. Do not include leading and ending "/"
     *            characters.
     */
    public void setShareName(String shareName) {
        this.shareName = shareName;
    }

    /**
     * Get the Root Path
     * 
     * @return the rootPath
     */
    public String getRootPath() {
        return rootPath;
    }

    /**
     * Set the root path.
     * 
     * @param rootPath
     *            the rootPath to set. Do not include leading and ending "/"
     *            characters.
     */
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    /**
     * Get the relative path.
     * 
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Set the relative path
     * 
     * @param path
     *            the path to set. Do not include leading and ending "/"
     *            characters.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Calculates and returns the absolute path of audio/video resources based
     * on the properties provided by this class.
     * 
     * @return String
     */
    public String getAbsolutePath() {
        StringBuilder buf = new StringBuilder();
        if (this.serverName != null) {
            buf.append("//");
            buf.append(this.serverName);
        }
        if (this.shareName != null) {
            buf.append("/");
            buf.append(this.shareName);
        }
        if (this.rootPath != null) {
            buf.append("/");
            buf.append(this.rootPath);
        }
        if (this.path != null) {
            if (buf.length() > 0) {
                buf.append("/");
            }
            buf.append(this.path);
        }
        return buf.toString();
    }

    /**
     * @return the importFilePath
     */
    public String getImportFilePath() {
        return importFilePath;
    }

    /**
     * @param importFilePath
     *            the importFilePath to set
     */
    public void setImportFilePath(String importFilePath) {
        this.importFilePath = importFilePath;
    }
}
