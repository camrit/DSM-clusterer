/*
 * FernandezMathClusterer.java
 */

package sdtv.clustering;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class FernandezMathClusterer implements Clusterer {
   
   public static final String ID = "FernandezMathClusterer";
   public static final int CL = 8; // max number of clusters.
   public static final int POW_CC = 2; // exponent in function to minimiza
   public static final int M = 10000000; // just a number > input DSM size
   
   
   private int cls;
   private double[][] input;
   private List<Set<Integer>> clusters;
   private double minCost;
   
    /** Clusters the software module dependencies */
   public Set<Set<Integer>> clusterSoftwareDSM(double[][] input) {
       this.cls = input.length; // define max cluster size
       this.input = input;
       this.minCost = Double.MAX_VALUE;
       
       for (int cnum = 1; cnum <= CL; cnum++) {
            permutate(0, createEmptyClusters(cnum));
       }
           
       //System.out.println(minCost);
       //System.out.println(clusters);
       
       Set<Set<Integer>> result = new HashSet<Set<Integer>>();
       for (Set<Integer> item: clusters)
           result.add(item);
       
       
       return result;
   }
   
   private List<Set<Integer>> createEmptyClusters(int noc) {
       List<Set<Integer>> result = new ArrayList<Set<Integer>>();
       for (int i = 0; i < noc; i ++)
           result.add(new HashSet<Integer>());
       return result;
   }
   
   /** This createa all possible clustering options and checks what they cost.
    * This an extremely expensive procedure with will only work for small input sizes 
    * (the problem is in NP and eats an exponentiel amount of space/time)
    */
   private void permutate(int startindex, List<Set<Integer>> clusters) {
       if (startindex == input.length) {
           checkMin(calcTotalCosts(clusters), clusters);
       } else {
           for (int i = 0; i < clusters.size(); i++) {
               Integer intobj = new Integer(startindex);
               clusters.get(i).add(intobj);
               permutate(startindex + 1, copyClusters(clusters));
               clusters.get(i).remove(intobj);
           }
               
       }
   }
   
   private List<Set<Integer>> copyClusters(List<Set<Integer>> sourceList) {
       List<Set<Integer>> result = new ArrayList<Set<Integer>>();
       for (Set<Integer> src: sourceList) {
           Set<Integer> newitem = new HashSet<Integer>();
           for (Integer srcitem: src)
               newitem.add(srcitem);
           result.add(newitem);
       }
       return result;
       
   }
   
   private void checkMin(double val, List<Set<Integer>> clusters) {
       if (val < minCost) {
           minCost = val;
           this.clusters = clusters;
       }
   }
   
   
   
   /** Clusters the developer-software module dependencies */
   public double[][] clusterDeveloperDSM(double[][] input) {
       return input;
   }
   
    
   /** 
    * Returns a textual representation of a Clusterer 
    * @ensure result != null
    */
   public String toString() {
       return ID;
   }
   
   // Aux methods
   
   /** Calculates the total cost of the currenct clustering
    * Java implementation of the formula on p51 in Fernandez' paper
    */
   protected double calcTotalCosts(List<Set<Integer>> clusters) {
       //System.out.println("calcTotalCost for");
       //for (Set<Integer> a: clusters)
       //    System.out.println(a);
       
       double result = 0;
       double noclusterfactor = Math.pow(input.length, POW_CC);
       for (int i = 0; i < input.length; i++) { // for each software module...
           for (int j = i+1; j < input.length; j++) { // for each software module...
                
                int clusterindex = 0; 
                int clustersize = 0;
                boolean clusterfound = false;
                while (!clusterfound && clusterindex < clusters.size()) {
                    Set<Integer> cluster = clusters.get(clusterindex);
                    if (cluster.contains(i) && cluster.contains(j)) {
                        clustersize = cluster.size();
                        clusterfound = true;
                    }
                    clusterindex++;
                }
                if (clusterfound)
                    result += (input[i][j] + input[j][i]) * Math.pow(clustersize, POW_CC);
                else
                    result += (input[i][j] + input[j][i]) * noclusterfactor;
       
            }
       }
       //System.out.println(result);
       return result;
   }

   
   
}
