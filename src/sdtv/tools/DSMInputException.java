/*
 * DSMInputException.java
 * This Exception is thrown when somethings's wrong with the DSM input files
 */


package sdtv.tools;

import sdtv.common.Messages;

public class DSMInputException extends Exception implements Messages {
    
   
    /** Creates a new instance of DSMInputException */
    public DSMInputException() {
        super(DSMINPUTEXCEPTIONDEFAILT);
    }
    
    public DSMInputException(String message) {
        super(message);
    }
    
}
