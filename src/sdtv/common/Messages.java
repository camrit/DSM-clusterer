/*
 * Messages.java
 *
 * This interface specifies all constants for
 * messages that are shown on-screen in the application
 */

package sdtv.common;

public interface Messages {
    
    // Identification
    static public final String APPNAME = "DSM Clusterer";
    static public final String APPVERSION = "0.5";
    static public final String COPYRIGHT = "(c) 2007";
    static public final String AUTHOR = "J.H.Hegeman (j.h.hegeman@student.utwente.nl)";
    
    // General
    static public final String OKBUTTONTEXT = "OK";
    static public final String CANCELBUTTONTEXT = "Cancel";
    static public final String ABOUTTEXT = APPNAME + "\nVersion " + APPVERSION + "\n" + COPYRIGHT + " " + AUTHOR;
    
    // Panel titles
    static public final String MAINPANELTITLE = APPNAME;
    static public final String MATRIXINPUTPANELTITLE = "DSM Input";
    
    // Menu item titles
    static public final String FILEMENUTITLE = "File";
    static public final String FILEEXITTEXT = "Exit";
    static public final String FILENEWDGTEXT ="New Diagram...";
    static public final String DIAGRAMMENUTITLE = "Diagram";
    static public final String HELPMENUTITLE = "Help";
    static public final String HELPABOUTTEXT = "About";
    
    // Button and label titles
    static public final String NEWDGBUTTONTEXT  = "New Diagram...";
    static public final String GENDIAGRAMBUTTONTEXT = "Start Clusterer";
    static public final String DISPDIAGRAMBUTTONTEXT = " Display Diagram";
    static public final String RANDOMBUTTONTEXT = "Random input...";
    static public final String CLUSTERSELECTLABEL = "Select clusterer";
    static public final String DIAGRAMSELECTLABEL = "Select diagram style";
    
    // Status messages
    static public final String DEFAULTSTATUS = "Ready (no input)";
    static public final String DSMSLOADEDSTATUS = "Ready, DSMs loaded.";
    static public final String DSMSCLUSTEREDSTATUS = "Ready, DSMs clustered.";
    static public final String MODULES = "modules";
    static public final String DEVELOPERS = "developers";
    static public final String CLUSTERS = "clusters";
    static public final String CLUSTERTIME = "clustering time:";
    
    // Matrix Input screen
    static public final String SSDSMLABELTEXT = "Software DSM file ";
    static public final String SDDSMLABELTEXT = "Developer DSM file";
    static public final String SSBROWSEBUTTONTEXT = "Browse...";
    static public final String SDBROWSEBUTTONTEXT = "Browse...";
    static public final String INPUTFILEERROR = "Cannot read DSM data from this file";
    static public final String NOTANINTEGER = "Input is not a positive integer";
    static public final String RANDOMINPUTSIZE = "Random Matrix Dimension (integer):";
    static public final String RANDOMINPUT = "[random input]";
    static public final String JUNGRANDOMRADIOBUTTON = "JUNG random input";
    static public final String MATHRANDOMRADIOBUTTON = "Math.random() input";
    static public final String RANDOMINPUTTITLE = "Select Random Input Parameters";
    
    
    // Dialog titles
    static public final String DIALOG_INPUTFILEERROR = "Input file error";
    static public final String DIALOG_INPUTERROR = "Input error";
    static public final String DIALOG_MODULELOADERROR = "Module load error";
    static public final String DIALOG_OPENFILE = "Select file";
    static public final String DIALOG_INFO = "Info";
    
    // Diagram frame
    static public final String DIAGRAMFRAMETITLE = "Diagram Displayer";
    
    // Exception handling messages
    static public final String DSMINPUTEXCEPTIONDEFAILT = "Unspecified error while loading DSM";
    static public final String CLUSTERERLOADERROR = "Unable to load clusterer module";
}
