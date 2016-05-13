/*
 * Configuration.java
 * Contains the application's configuration parameters.
 */

package sdtv.common;

import sdtv.clustering.*;
import sdtv.gui.diagram.DiagramFrame;
import sdtv.gui.diagram.HegemanDiagramFrame;

public interface Configuration {
    
   // Clusterer implementations attached to this application;
   public static final Clusterer[] CLUSTERMODULELIST = new Clusterer[] { 
       new DummyClusterer(), 
       new FernandezClusterer(), 
       new FernandezMathClusterer(), 
       new MacCormackClusterer()
   };
   
   // DiagramPanel implementations attached to this appliation
   public static final DiagramFrame[] DIAGRAMPANELLIST = new DiagramFrame[] {
       new HegemanDiagramFrame()   
   };
   
   // Delimiter when reading input DSMs from text files
   public static final String INPUTDELIMITER = ",";

}
