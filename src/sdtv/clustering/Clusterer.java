/*
 * Clusterer.java
 * Interface for DSM clustering algorithms
 */

package sdtv.clustering;

import java.util.Set;

public interface Clusterer {
   
   /** Clusters a software module dependency matrix.
    * @require input has equal dimensions
    */
   public Set<Set<Integer>> clusterSoftwareDSM(double[][] input);
   
   /** Clusters the developer-software module dependencies */
   public double[][] clusterDeveloperDSM(double[][] input);
   
   /** 
    * Returns a textual representation of a Clusterer 
    * @ensure result != null
    */
   public String toString();
   
}
