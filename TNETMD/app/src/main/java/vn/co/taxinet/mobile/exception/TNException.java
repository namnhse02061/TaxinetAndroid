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
 * Base exception of all TNS-specific exception classes to provide some common
 * attributes such as the location and system exception (if exists).
 */
public class TNException extends Exception {
	private static final long serialVersionUID = 4907332684307180702L;
	private String location = null;
	private String code = null;
    /**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	private Throwable throwable = null;

    public TNException() {
        super();
    }
    /**
     * @param location
     * @param throwable
     */
    public TNException(String aCode, String location) {
        super(aCode);
        this.code  = aCode;
        this.location = location;
    }
    /**
     * @param location
     * @param throwable
     */
    public TNException(String aCode, String location, Throwable throwable) {
        super(throwable.getMessage());
        this.code  = aCode;
        this.location = location;
        this.throwable = throwable;
    }

    /**
     * @param location
     * @param throwable
     */
    public TNException(String location, Throwable throwable) {
        super(throwable.getMessage());
        this.location = location;
        this.throwable = throwable;
    }

    /**
     * @param location
     * @param description
     */
    public TNException(String aCode, String location, String description) {
        super(description);
        this.code = aCode;
        this.location = location;
    }

    /**
     * @param description
     */
    public TNException(String aCode) {
        super(aCode);
        this.code = aCode;
    }

    /**
     * @return String
     */
    public String getLocation() {
        return location;
    }

    /**
     * @return java.lang.Throwable
     */
    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * @return String
     */
    public String getLocationMessage() {
        return location + ": " + getMessage();
    }
    @Override
	public String toString() {
    	if(throwable != null) {
    		return code + ":" + throwable.toString();
    	} else {
    		return code; 	
    	}
	}
}