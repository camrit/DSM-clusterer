/*
 * FernandezClusterer.java
 * Clusterer implementation that uses Fernandez' algorithm
 */

package sdtv.clustering;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FernandezClusterer implements Clusterer {
    
    // Constants
    public static final int POW_CC = 1; // The cluster size penalty, default=2
    public static final int POW_BID = 0; // Cluster size penalty for bidding, default=2
    public static final int POW_DEP = 2; // Emphasis on high interaction during bidding, def=2
    public static final int MAXCLUSTERSIZE = 10; // Maximum Cluster Size;
    public static final int STABLE_LIMIT = 20; // number of unsuccesful cost reductions before ending.
    public static final int RAND_ACCEPT = 20; // 1 out of (this times DSM size) times continue even without improvement 
    public static final int RAND_BID = 1; // 1 out of (this times DSM size) a higher bid is ignored
    
    public static final String ID = "FernandezClusterer";
    
    protected double totalCost;
    
    // Public methods
    public Set<Set<Integer>> clusterSoftwareDSM(double[][] input) {
       
        //definitions
       List<List<Integer>> clusters;   // This list of clusters
       boolean stable; // is the clustering stable?
       double [][] output; // the result of this procedure
       int noChangeAttempts = 0;
       
       //init: assign each module to it's own cluster
       clusters = new ArrayList<List<Integer>>(); // init the cluster list
       for (int i = 0; i < input.length; i++) { // for all software modules...
           List<Integer> newCluster = new ArrayList<Integer>();
           newCluster.add(new Integer(i));
           clusters.add(newCluster); // ...create a cluster containing this module.
       }
       //init: calculate initial total coordination costs, set other vars to initial values
       totalCost = calcInitialTotalCost(input, clusters);
       stable = false;
       
       //START OF LOOP
       while (!stable) {
           
           //delete empty clusters and clusters contained in another
           clusters = cleanup(clusters);
           
           //pick random module
           int randomModule = (int)(Math.random() * input.length);
         
           
           //calculate bids and find highest bidding cluster
           double highBid = 0;
           List<Integer> highCluster = null;
           List<Integer> oldCluster = null;
           
           for (List<Integer> cluster: clusters) { // for each cluster
               if (cluster.size() < MAXCLUSTERSIZE) {
                   double bid = bid(clusters, input, cluster, randomModule);
                   if (bid > highBid && Math.random() > (1 / (RAND_BID * input.length))) {
                       highBid = bid;
                       highCluster = cluster;
                   }
               }
               
           }
           //System.out.println("found hc" + highCluster + " for module " + randomModule);
           for (int i = 0; i < clusters.size(); i++) {
               if (clusters.get(i).contains((Integer)randomModule) && highCluster != null) {
                   clusters.get(i).remove((Integer)randomModule);
                   oldCluster = clusters.get(i);
               }
           }
           //System.out.println("found oldcluster " + oldCluster + " for module " + randomModule);
           //assign randomly selected cluster to highest bidder
           
           if (highCluster != null) {
               highCluster.add(randomModule);
               
               //System.out.println("clusters:");
               //for (List<Integer> cluster : clusters)
               //    System.out.println(cluster);
               
               double newTotalCost = calcTotalCost(input, clusters);

               //System.out.println("total cost: " + newTotalCost + " (old: " + totalCost + ")");
               if (newTotalCost >= totalCost) {
                    if (Math.random() > (1 / (RAND_ACCEPT * input.length))) { // usually undo change
                         highCluster.remove((Integer)randomModule);
                         oldCluster.add((Integer)randomModule);
                    }
                    noChangeAttempts++;

               } else {
                   noChangeAttempts = 0;
                   totalCost = newTotalCost;
               }

               if (noChangeAttempts > STABLE_LIMIT)
                   stable = true;
           }
        }
        
       Set<Set<Integer>> result = new HashSet<Set<Integer>>();
       for (List<Integer> a: clusters) {
           Set<Integer> thisone = new HashSet<Integer>();
           for (Integer b: a)
               thisone.add(new Integer(b));
           result.add(thisone);
       }
       return result;
   }
   
   public double[][] clusterDeveloperDSM(double[][] input) {
       return input;
   }
   
   public String toString() {
       return ID;
   }
   
   
   //Aux methods
   
   protected double calcInitialTotalCost(double[][] input, List<List<Integer>> clusters) {
       return calcTotalCost(input, clusters);
   }
   
   /** Calculates the Totale Coordination Costs, given a clustering */
   protected double calcTotalCost(double[][] input, List<List<Integer>> clusters) {
       int totalcost = 0; // no cost found yet (duh).
        for (int i = 0; i < input.length; i++) { // for all modules
           for (int j = 0; j < input.length; j++) { // again for all modules
               
                // find the size of the smallest cluster that contain i and j
               // or use the DSM size if no such cluster exists.
               int size = input.length; // initialize size to the total DSM size
               for (List<Integer> cluster: clusters) // if a cluster exists...
                   if (cluster.contains(i) && cluster.contains(j)) ///...that contains i and j...
                       if (cluster.size() < size) // and if it's size < current found size
                           size = cluster.size(); // set size to that clusters' size.

               totalcost += (input[i][j] + input[j][i]) * Math.pow(size, POW_CC);
           
           
          }
       }
       return totalcost;
   }
   
   /** Cleanup cluster list by removing empty clusters and clusters
    * that reside in another.  */
   private List<List<Integer>> cleanup(List<List<Integer>> clusters) {
       List<List<Integer>> result = new ArrayList<List<Integer>>();
       
       // Only add a cluster to the result if it is:
       // - not empty
       // - not contained in another cluster
       for (int i = 0; i < clusters.size(); i++) { // for all modules
           List<Integer> currentCluster = clusters.get(i);
           boolean valid = true;
           if (currentCluster.size() == 0) // empty cluster found, this is not valid
               valid = false;
           else { // process nonempty cluster
               int j = 0;
               while (valid && j < clusters.size()) { // check for each other cluster if it contains this one
                    if (i != j) {
                        boolean contained = true;
                        List<Integer> containmentCheckCluster = clusters.get(j);
                        int k = 0;
                        while (contained && k < currentCluster.size()) {
                            if (!containmentCheckCluster.contains(currentCluster.get(k)))
                                contained = false;
                            k++;
                        }
                        if (contained) // if another cluster contains this one declare it obsolete
                            valid = false;     
                    }
                    j++;
               }                
           }
           if (valid) // if a cluster has survived the algorithm above, add it to result
               result.add(clusters.get(i));
           
       }
       return result; 
       
   }
   
   /** Calculates a bid from cluster 'cluster' for module 'randomModule' */
   private double bid(List<List<Integer>> clusters, double[][] input, List<Integer> cluster, int randomModule) {
       double result = 0; //
       for (int j = 0; j < input.length; j++) {
           double dsmscore;
           if (cluster.contains(j)) {
                dsmscore = input[randomModule][j] + input[j][randomModule];
                dsmscore = Math.pow(dsmscore, POW_DEP);
           }
           else {
                dsmscore = 0;
           }
           result += dsmscore / (Math.pow(cluster.size(), POW_BID));
           
       }
       return result;
   }
   
   private void say(String msg) {
       System.out.println(msg);
   }
   
   private String clustersToString(List<List<Integer>> input) {
       StringBuffer result = new StringBuffer();
       for (List<Integer> a: input) {
           boolean first = true;
           for (Integer b: a) {
               if (!first) result.append(", ");
               result.append(b);
               first = false;
           }
           result.append("\n");
       }
       return result.toString();
   }
   
}
