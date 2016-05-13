/*
 * DataController.java
 * keeps data vars for the GUI
 */ 
package sdtv.gui;

import java.util.Observable;
import java.util.Set;
import sdtv.tools.DSMInputException;
import sdtv.tools.DSMTools;
import sdtv.common.Messages;


public class DataController extends Observable implements Messages {
    
    private double[][] sdsm;
    private double[][] ddsm;
    private String error;
    private boolean ready, clustered;
    private Set<Set<Integer>> clusters;
    
    /** Creates a new instance of DataController */
    public DataController() {
        ready = false;
        clustered = false;
    }
 
    /** Loads DSM text files 
     * @param ssFileName the name of the file containing the Software DSM
     * @param sdFileName the name of the file containing the Developer DSM
     * @require ssFileName != null && sdFileName != null
     */
    public boolean loadData(String ssFileName, String sdFileName) {
        try {
            sdsm = DSMTools.readSDSM(ssFileName);
            ddsm = DSMTools.readDDSM(sdFileName);     
            ready = true;
            return ready;
        } catch (DSMInputException e) {
            this.error = e.getMessage();
            return false;
        }
        
        
    }
    
    public void confirmLoad() {
       if (ready) {
        setChanged();
        notifyObservers(DSMSLOADEDSTATUS + " (" + sdsm.length + " " + MODULES + ", " + ddsm.length + " " + DEVELOPERS + ")");
       }
    }
    
    /**
     * Sets the clusters for the SDSM
     * @param input The set of set of SDSM indices that form clusters
     */
    public void setClusters(Set<Set<Integer>> input) {
        clusters = input;
        clustered = true;
        setChanged();
        notifyObservers(DSMSCLUSTEREDSTATUS + " (" + sdsm.length + " " + MODULES + " in " + input.size() + " " + CLUSTERS + ", " + ddsm.length + " " + DEVELOPERS + ")");
  
    }
    
    /** Returns the cluster set for this datacontroller */
    public Set<Set<Integer>> getClusters() {
        return clusters;
    }
    
    /** Returns the error from this datacontroller if there has been one (else null) */
    public String getError() {
        return this.error;
    }
    
    /** Are the DSMs loaded? */
    public boolean isReady() {
        return ready;
    }
    
    /** Are the DSMS clustered? */
    public boolean isClustered() {
        return clustered;
    }
    
    /** Returns the Software DSM */
    public double[][] getSDSM() {
        return this.sdsm;
    }
    
    /** Returns the Developer DSM */
    public double[][] getDDSM() {
        return this.ddsm;
    }

    /** Sets the software DSM */
    public void setSDSM(double[][] sdsm) {
        this.sdsm = sdsm;
        if (this.sdsm != null && this.ddsm != null)
            ready = true;
        else
            ready = false;
    }
    
    /** Sets the Developer DSM */
    public void setDDSM(double[][] ddsm) {
        this.ddsm = ddsm;
         if (this.sdsm != null && this.ddsm != null)
            ready = true;
         else
             ready = false;
    }
     
    /** Resets the datacontroller. Clears the DSMs and sets the controller to non-ready*/
    public void reset() {
        this.sdsm = null;
        this.ddsm = null;
        this.ready = false;
        this.clustered = false;
    }
}
