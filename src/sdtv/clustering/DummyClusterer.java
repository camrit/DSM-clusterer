/*
 * DummyClusterer.java
 * Clusters a DSM by doing absolutely no clustering at all.
 */

package sdtv.clustering;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Erik
 */
public class DummyClusterer implements Clusterer {
    
    static public final String ID = "DummyClusterer";
  
   /** Just puts each module in its own cluster.
    * Returns a set of sets of integers that contain one number
    */
   public Set<Set<Integer>> clusterSoftwareDSM(double[][] input) {
       Set<Set<Integer>> result = new HashSet<Set<Integer>>();
       for (int i = 0; i < input.length; i++) {
           Set<Integer> thisone = new HashSet<Integer>();
           thisone.add(new Integer(i));
           result.add(thisone);
       } 
       return result;
   }
   
   /** Nothing to do here */
   public double[][] clusterDeveloperDSM(double[][] input) {
       return input;
   }
   
   
   public String toString() {
       return ID;
   }
}
