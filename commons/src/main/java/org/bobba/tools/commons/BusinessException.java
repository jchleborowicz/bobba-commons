package org.bobba.tools.commons;

/**
 * Intended for errors which are shown to the user. Constructor argument is a code of error message which will be
 * interpretted by
 */
public class BusinessException extends RuntimeException {

    private final String businessMessageCode;

    public BusinessException(String businessMessageCode) {
        super(businessMessageCode);
        this.businessMessageCode = businessMessageCode;
    }

    public String getBusinessMessageCode() {
        return businessMessageCode;
    }

}
