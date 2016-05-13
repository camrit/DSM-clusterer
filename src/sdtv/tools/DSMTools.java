/*
 * DSMTools.java
 * Contains general tools to work with DSMs (non-clustering)
 */


package sdtv.tools;

import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import edu.uci.ics.jung.algorithms.GraphMatrixOperations;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.random.generators.SimpleRandomGenerator;
import sdtv.clustering.MacCormackClusterer;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import sdtv.common.Configuration;
import java.util.*; 


/**
 *
 * @author Erik
 */
public class DSMTools implements Configuration {
   
   // this defines the edges-vertices relationship in the Jung random generator
   // #edges = #vertices * DSM size^2 divided by this value
   // 5 seems to optimally represent a reasonable software architecture
   static public final int JUNGDEPENDENCYDIVIDER = 10;
   
    
   /** Reads a Software DSM from a file
    *@param ssFileName the name of the SDSM file
    */ 
   static public double[][] readSDSM(String ssFileName) throws DSMInputException {
      
       try {
            FileReader f = new FileReader(ssFileName);
            BufferedReader in = new BufferedReader(f);

            String line = in.readLine();
            int count = 0;
            Scanner sc = new Scanner(line);
            sc.useDelimiter(INPUTDELIMITER);
            while (sc.hasNext()) {
                sc.next();
                count++; // determine matrix dimensions
            }

            double[][] result = new double[count][count];

            int linenum = 0;
            int colnum;
            while (line != null && !line.equals("")) {
                colnum = 0;
                sc = new Scanner(line);
                sc.useDelimiter(INPUTDELIMITER);
                while (sc.hasNext()) {
                    result[linenum][colnum] = Double.parseDouble(sc.next());
                    colnum++;
                }
                line = in.readLine();
                linenum++;
            }
            
            return result;
        } catch (FileNotFoundException e) {
            throw new DSMInputException("File not found: " + e.getMessage());
        } catch (IOException f) {
            throw new DSMInputException("IO Error " + f.getMessage());
        }
    }
   
   /** Reads a Developer DSM from a file
    *@param sdFileName the name of the SDSM file
    */ 
    static public double[][] readDDSM(String sdFileName) throws DSMInputException {
      try {
            FileReader f = new FileReader(sdFileName);
            BufferedReader in = new BufferedReader(f);

            String line = in.readLine();
            int count = 0;
            Scanner sc = new Scanner(line);
            sc.useDelimiter(INPUTDELIMITER);
            while (sc.hasNext()) {
                sc.next();
                count++; // determine matrix dimensions
            }
            List<Double[]> rowlist = new ArrayList<Double[]>();
            while (line != null && !line.equals("")) {
                int colcount = 0;
                Double[] currline = new Double[count];
                sc = new Scanner(line);
                sc.useDelimiter(INPUTDELIMITER);
                while (sc.hasNext()) {
                    currline[colcount] = Double.parseDouble(sc.next());
                    colcount++;
                }
                rowlist.add(currline);
                line = in.readLine();
            }
            
            double[][] result = new double[rowlist.size()][count];
            
            for (int i = 0; i < rowlist.size(); i++)
                for (int j = 0; j < count; j++)
                    result[i][j] = rowlist.get(i)[j];
            
            
            return result;
            
          } catch (FileNotFoundException e) {
            throw new DSMInputException("File not found: " + e.getMessage());
        } catch (IOException f) {
            throw new DSMInputException("IO Error " + f.getMessage());
        }
    
    }
    
    /** Creates a random matrix using Math.random() */
    static public double[][] randomDSM(int size) {
        double[][] result = new double[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {
                if (i == j)
                    result[i][j] = 0.0;
                else if (Math.random() > 0.33)
                    result[i][j] = 0.0;
                else
                    result[i][j] = Math.round(Math.random() * 10);
            }
        return result;
        
    }
    
    /** Creates a random matrix using the JUNG library */
     static public double[][] randomJungDSM(int size) {
        double[][] result = new double[size][size];
        SimpleRandomGenerator srg = new SimpleRandomGenerator(size, (int)(Math.random() * size * (size-1) / JUNGDEPENDENCYDIVIDER));
        Graph artg = (Graph)(srg.generateGraph());
        SparseDoubleMatrix2D matrix = GraphMatrixOperations.graphToSparseMatrix(artg);
        for (int i=0; i < size; i++)
            for (int j = 0; j < size; j++)
                result[i][j] = matrix.getQuick(i,j);
        
        return result;
        
    }
     
    /** Calculates the dependencies between clusters */
    public static Map<Integer,Map<Integer, Double>> calcClusterDeps(Set<Set<Integer>> clusters, double[][] input) {
     // result is a map of integers to map of integers to doubles
     // this maps the cluster indices to (clusterindex, cost)
     System.out.println("calculating cluster dependencies...");
    	
	    Map<Integer,Map<Integer, Double>> result = new HashMap<Integer,Map<Integer, Double>>();
	    
	    int i = 0;
	    for (Set<Integer> cluster1 : clusters) {
	    	int j = 0;
	    	Map<Integer, Double> existing = null;
	    	for (Set<Integer> cluster2 : clusters) {
	    		existing = result.get(i);
	    		if (existing == null)
	    			existing = new HashMap<Integer, Double>();  		
	    		existing.put(new Integer(j), calcDep(cluster1, cluster2, input));   				
	    		j++;
	    		result.put(i, existing);
	    	}
	    	
	    	i++;
	    }
	    
	    for (Map.Entry<Integer,Map<Integer,Double>> a : result.entrySet()) {
	    	System.out.println("results for cluster " + a.getKey());
	    	for (Map.Entry<Integer,Double> b : a.getValue().entrySet())
	    		System.out.println("cluster " + b.getKey() + " - cost: " + b.getValue());
	    }
	    
	    
	    return result;
    }
    
    // Calculates the dependency between two clusters.
    private static double calcDep(Set<Integer> c1, Set<Integer> c2, double[][] input) {
	  	double result = 0;
    	for (Integer i : c1)
    		for (Integer j : c2)
    			result += input[i][j];
	
    	return result;
	
    }
    
    public static double calcPropagationCost(double [][] a) {
    	int j = 0,
    	 	k =0;
    	 	
       	System.out.println("Calculating propagation cost...");
       	//Setting the main diagonal to zero (no self loops)
       	for(j = 0; j < a.length; j++){
       		if(a[j][j] > 0)
       			a[j][j] = 0;
       	}
       	
       	for (j = 0; j < a.length; j++) {
			for (k = 0; k < a.length; k++) {
				if((a[j][k]>0) && (a[k][j]>0))
					if(j<k)
						a[j][k]=0;
			}
       	}
    	double result = 0.0;
    	double [][] sum = new double[a.length][a.length];
    	double [][] b = new double[a.length][a.length];
    	for (j = 0; j < b.length; j++) {
			for (k = 0; k < b.length; k++) {
				if (j==k)
					sum[j][k] =1.0;
				else
					sum[j][k] =0.0;
				b[j][k]=a[j][k];
			}
		}
    	int counter = 0;
    	do{
    		
    		result = 0.0;
    		for (j = 0; j < b.length; j++) {
    			for (k = 0; k < b.length; k++) {
    				sum[j][k] += b[j][k];
    				result += b[j][k];
    			}
    		}
    		b = matrixPower(a, b);
    		result /= b.length;
    		counter++;
    		System.out.println("done " + counter + " multiplications...");
    	}while(result >0);
    	
    	result = 0.0;
    	for (j = 0; j < b.length; j++) {
			for (k = 0; k < b.length; k++) {
				result += sum[j][k];
			}
    	}
    	printToFile("finished, Propogration Cost = " + result);
    	return result;
    }
    
    private static double[][] matrixPower(double[][] a, double[][]b) {
    	double[][] result = new double[a.length][b.length];
    	
    	for (int i = 0; i < a.length; i++) {
    		for (int j = 0; j < a.length; j++) {
    			for (int k = 0; k < b.length; k++) {
    				result[i][j] += a[i][k]*b[k][j];
    			}
    		}
    	}
    	return result;
    }
    static public Set<Set<Integer>> jungDSM(SparseDoubleMatrix2D matrix) {
        double[][] tempmatrix = new double[matrix.rows()][matrix.columns()];
        
        for (int i=0; i < matrix.rows(); i++)
            for (int j = 0; j < matrix.columns(); j++)
                tempmatrix[i][j] = matrix.getQuick(i,j);
        
        MacCormackClusterer mcc = new MacCormackClusterer();
        Set<Set<Integer>> result = mcc.clusterSoftwareDSM(tempmatrix);
        
        return result;
        
    }
    
    public static double readTasksClusteredCost() {
    	try {
    		BufferedReader input =  new BufferedReader(new FileReader("taskfile.ini"));
    		String filename1 = input.readLine();
    		String filename2 = input.readLine();
    		input.close();  		
    		return calcDevClusteredCost(readDDSM(filename1), readDDSM(filename2));  	
    	
    	} catch (Exception e) {
    		System.err.println ("error reading task file " + e.getMessage());
    		return 0;
    	}
    }
    public static double readSDPropogationCost() {
    	try {
    		BufferedReader input =  new BufferedReader(new FileReader("taskfile.ini"));
    		String filename1 = input.readLine();
    		input.close();     		
    		return calcPropagationCost(readDDSM(filename1));  	
    	
    	} catch (Exception e) {
    		System.err.println ("error reading task file " + e.getMessage());
    		return 0;
    	}
    }
    public static double readDDPropogationCost() {
    	try {
    		BufferedReader input =  new BufferedReader(new FileReader("taskfile.ini"));
    		String filename1 = input.readLine();
    		String filename2 = input.readLine();
    		input.close();    		
    		return calcPropagationCost(readDDSM(filename2));  	
    	
    	} catch (Exception e) {
    		System.err.println ("error reading task file " + e.getMessage());
    		return 0;
    	}
    }
    
    public static double calcDevClusteredCost(double[][] devrel, double[][] tasks ) {
    	double result = 0.0;
    	printToFile("Developer matrix height:" + devrel.length + "  Developer matrix breadth:"+ devrel[0].length);
    	printToFile("Task matrix height:" + tasks.length + "  Task matrix breadth:"+ tasks[0].length);
    	for (int i = 0; i < devrel.length; i++) {
    		for (int j = 0; j < devrel.length; j++) {
    			if(devrel[i][j] > 0){
    				boolean sameCluster = false;
        			for (int k = 0 ; k < tasks[0].length; k++) {
        				if ( tasks[i][k] > 0 && tasks[j][k] >0)
        					sameCluster = true;
        			}
        			if (sameCluster)
        				result += devrel[i][j] / 2; // Should actually be cluster size
        			else
        				result += devrel[i][j];	
    			}
    		}
    	}
    	printToFile("Cluster Cost of the developers: " + result);
    	return result;
    }
    public static void printToFile (String printString){
    	PrintStream pStream = null;

 	   	try{
 	   		pStream = new PrintStream(new FileOutputStream("Output.log", true));
 	   		pStream.println(printString);
 	   	}
 	   	catch(Exception ex){
 	   		ex.printStackTrace();
 	   		System.err.println(ex.getMessage());
 	   	}
    }
        
}
