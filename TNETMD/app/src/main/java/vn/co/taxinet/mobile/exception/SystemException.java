/**
 * Copyright(C) 2014, Transport Information Network Company.
 * TaxiNet:
 *  Taxi Network System
 *
 * Record of change:
 * Date          Version   Modifier   Change    			Reason
 * 2014-12-01    1.0       Dev        Create structure		First creation
 */

package vn.co.taxinet.mobile.exception;



/**
 * Represent a system exception in the application (when a Throwable is caught).
 * Automatically log an ERROR message to log file when created.
 */
public class SystemException extends TNException {
	private static final long serialVersionUID = 2972125851605200670L;

    public SystemException() {
        super();
        this.logException();
    }

    /**
     * @param location
     */
    public SystemException(String location) {
        super(location);
        this.logException();
    }

    /**
     * @param location
     * @param throwable
     */
    public SystemException(String location, Throwable throwable) {
        super(location, throwable);
        this.logException();
    }

    /**
     * @param location
     * @param description
     */
    public SystemException(String location, String description) {
        super(location, description);
        this.logException();
    }

    /**
     * Log an ERROR message whenever a new exception is created.
     */
    private void logException() {
    }
}