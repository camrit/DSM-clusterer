/*
 * MacCormackClusterer.java
 *
 * Created on 4 april 2007, 20:48
 *
 * Uses MacCormacks marginal cost change formula and Fernandez' algorithm
 * should be faster then Fernandez' thing
 */

package sdtv.clustering;

import sdtv.tools.*;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.io.IOException;

/**
 *
 * @author Erik
 */
public class MacCormackClusterer implements Clusterer {
    
    public static final String ID = "MacCormackClusterer";
    
    private int pow_CC = 2; // The cluster size penalty, default=2
    private int stable_limit = 50; // number of unsuccesful cost reductions before ending.
    private int maxNumberOfClusters = 12;
    private String inputFile;

    public static final int THRESHOLD = 0;
    
    private double totalcost;
    protected double[][] costmatrix;
    
   
    public MacCormackClusterer(){
    	try{
    		BufferedReader inRead = new BufferedReader(new FileReader("sdtv.ini"));
    		pow_CC = Integer.parseInt(inRead.readLine());
    		stable_limit = Integer.parseInt(inRead.readLine());
    		maxNumberOfClusters = Integer.parseInt(inRead.readLine());
    		inputFile = inRead.readLine();
    		//outputStream = new PrintStream(new FileOutputStream("corePeriphery.out", true));
    	}
    	catch(IOException ex){
    		System.out.println("sdtv.ini was not found, using default values.");
                System.out.println(stable_limit);
    	}
    }
    
    public Set<Set<Integer>> defaultClustering() {
    	try {
    		return clusterSoftwareDSM(DSMTools.readSDSM(inputFile));
    	} catch (Exception e) {
    		System.out.println("Couldnt start clutserer " + e.getMessage());
    		return null;
    	}
    }
    
    public Set<Set<Integer>> clusterSoftwareDSM(double[][] input) {
        System.out.println("MacCormackClusterer starting");
        
        List<List<Integer>> clusters = setInitialClusters(input);
        boolean stable = false;
        int attempt = 1;
        int maxfailed = 0;
        
        int count = 0;
        // remember the last (input.length/10) tcc decreased in this array
        int lastDecreasedLength = (int)(input.length) / 4;
        double[] lastDecreased = new double[lastDecreasedLength];
        int lastDecreasedIndex = 0;
        double totalRecentDecrease = 0;
       
        //System.out.println("initial clusters:");
        //  for (List<Integer> cluster: clusters)
        //    System.out.println(cluster);
       
          totalcost = calcInitialTotalCost(input, clusters);
       
        // if recent decrease goes below this, stop clustering
        double costLimit = 200;
        
        DSMTools.printToFile("Initial total cost: " + totalcost);
        System.out.println("Now busy with main clustering process...");
        while (!stable) { // continue trying to optimize while this more or less has results..
            
            //select random module from random cluster
            int sourceCluster = (int)(Math.random() * clusters.size());
            int sourceClusterIndex = (int)(Math.random() * clusters.get(sourceCluster).size());
            
                     
            // find highest bid
            double bestBid = 0;
            int bestCluster = 0;
            for (int i = 0; i < clusters.size(); i++) {
                if (i != sourceCluster) {
                    double costReduction = calcCostReduction(input, clusters, sourceCluster, i, sourceClusterIndex);
                    //System.out.println(" bid from " + i + " for " + clusters.get(sourceCluster).get(sourceClusterIndex) + ": " + costReduction);
                    if (costReduction < bestBid) {
                        bestBid = costReduction;
                        bestCluster = i;
                    }
                }
             }
           
             lastDecreased[lastDecreasedIndex] = Math.abs(bestBid);
             lastDecreasedIndex = (lastDecreasedIndex + 1) % lastDecreasedLength;
             if (bestBid < THRESHOLD) {
            	 //System.out.println("highest bid is " + bestBid);
                 //System.out.println("moving element " + clusters.get(sourceCluster).get(sourceClusterIndex) + " from " + sourceCluster + " to " + bestCluster);
                 totalcost = totalcost + bestBid;
                 clusters.get(bestCluster).add(clusters.get(sourceCluster).get(sourceClusterIndex));
                 clusters.get(sourceCluster).remove(sourceClusterIndex);
                 clusters = cleanup(clusters);
                 //System.out.println("clusters:");
                 //for (List<Integer> cluster: clusters)
                     //System.out.println(cluster);
                 
                 attempt = 1;
             } else {
            	totalRecentDecrease = 0;       	
            	for (int x = 0; x < lastDecreasedLength; x++) {
            		totalRecentDecrease += lastDecreased[x];
            		//System.out.print(lastDecreased[x] + " ");
            	}
            	 
            
            	if (totalRecentDecrease < (costLimit)/100 && (count > lastDecreasedLength)) {
                    stable = true;
            	} else {
                    attempt++;
                    if (attempt > maxfailed) {
                        maxfailed = attempt;
                        
                    }
                }
             }
            
             if (count % 100 == 0)
             	System.out.println("totalclost " + totalcost + ", TRC: " + totalRecentDecrease + ", <1 to stop: " + (totalRecentDecrease / costLimit));
             
             count++;
        }
        System.out.println("Done with Clustering! ");
        DSMTools.printToFile("Resulting total cost: " + totalcost);
        //outputStream.println("Total clustered cost: " + totalcost);
        return buildResultSet(clusters);
    }
    
    public double[][] clusterDeveloperDSM(double[][] input) {
       return input;
    }
    
    protected List<List<Integer>> setInitialClusters(double[][] input) {
        System.out.print("Setting initial clusters (number of clusters=" + maxNumberOfClusters + ")...");
        List<List<Integer>> result = new ArrayList<List<Integer>>(); 
        
        for (int i = 0; i < maxNumberOfClusters ; i++)
        	result.add(new ArrayList<Integer>());
          
        for (int i = 0; i < input.length; i++) // for all software modules...
           result.get(i % maxNumberOfClusters).add(new Integer(i));
       System.out.println("done!");
       return result;
    }
    
    protected double calcCostReduction(double[][] input, List<List<Integer>> clusters, int source, int dest, int elem) {
        double result = 0;
        int N = input.length; // the size of the DSM
        int n = clusters.get(dest).size() + 1; // dest cluster size after move
        int m = clusters.get(source).size(); // source cluster size before move
        double factor;
        
        //In to Out in source cluster
        factor = (0 - Math.pow(m,pow_CC) + Math.pow(N, pow_CC));
        for (int i = 0; i < m; i++) {
            result += input[clusters.get(source).get(i)][clusters.get(source).get(elem)] * factor;
            result += input[clusters.get(source).get(elem)][clusters.get(source).get(i)] * factor;
        }
        // System.out.print(" IO " + result);
        
        // Out to in
        factor = (0 - Math.pow(N, pow_CC) + Math.pow(n, pow_CC));
        for (int i = 0; i < n-1; i++) {
            result += input[clusters.get(source).get(elem)][clusters.get(dest).get(i)] * factor;
            result += input[clusters.get(dest).get(i)][clusters.get(source).get(elem)] * factor;
        }
        // System.out.print(" OI " + result);
             
        
        // source cluster dependencies
        factor = (0 - Math.pow(m, pow_CC) + Math.pow(m-1, pow_CC));
        for (int i = 0; i < m; i++)
            for (int j = 0; j < m; j++) {   
                if (clusters.get(source).get(i) != elem && clusters.get(source).get(j) != elem)
                    result += input[clusters.get(source).get(i)][clusters.get(source).get(j)] * factor;
        }
        // System.out.print(" SRC-D " + result);
        
        // dest cluster dependencies
        factor = (0-Math.pow(n-1,pow_CC) + Math.pow(n, pow_CC));
        for (int i = 0; i < n-1; i++)
            for (int j = 0; j < n-1; j++)
                result += input[clusters.get(dest).get(i)][clusters.get(dest).get(j)] * factor;
        //System.out.print(" DST-D " + result); 
        
        return result;
    }
    
    protected Set<Set<Integer>> buildResultSet(List<List<Integer>> clusters) {
       Set<Set<Integer>> result = new HashSet<Set<Integer>>();
       for (List<Integer> a: clusters) {
           Set<Integer> thisone = new HashSet<Integer>();
           for (Integer b: a)
               thisone.add(new Integer(b));
           result.add(thisone);
       }
       return result;
    }
    
    protected List<List<Integer>> cleanup(List<List<Integer>> clusters) {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        for (int i = 0; i < clusters.size(); i++)
            if (clusters.get(i).size() > 0)
                result.add(clusters.get(i));
        return result;
    }
    
    protected double calcInitialTotalCost(double[][] input, List<List<Integer>> clusters) {
        System.out.print("Calculating initial total cost...");
        totalcost = 0; // no cost found yet (duh).
        costmatrix = new double[input.length][input.length];
        double thiscost;
        for (int i = 0; i < input.length; i++) { // for all modules
           for (int j = i+1; j < input.length; j++) { // again for all modules
               
               // find the size of the smallest cluster that contain i and j
               // or use the DSM size if no such cluster exists.
               int size = input.length; // initialize size to the total DSM size
               for (List<Integer> cluster: clusters) // if a cluster exists...
                   if (cluster.contains(i) && cluster.contains(j)) ///...that contains i and j...
                       if (cluster.size() < size) // and if it's size < current found size
                           size = cluster.size(); // set size to that clusters' size.

               thiscost = (input[i][j] + input[j][i]) * Math.pow(size, pow_CC);
               costmatrix[i][j] = thiscost;
               costmatrix[j][i] = thiscost;
               totalcost += thiscost;
               
          }
       }
        System.out.println("done!");
       return totalcost;
    }
    

    
    
    
   public String toString() {
       return ID;
   }
 
           
}


