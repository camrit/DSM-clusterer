/*
 * Interface for diagram displaying panels
 */

package sdtv.gui.diagram;

import java.util.Set;
import javax.swing.JFrame;
import sdtv.common.Constants;
import sdtv.common.Messages;

public abstract class DiagramFrame extends JFrame implements Constants, Messages {
    
    public DiagramFrame() {
        super(DIAGRAMFRAMETITLE);
    }
    
    /** Returns a JPanel containing a diagram based in input DSMs and the software module cluster set */
    public abstract void drawDiagram(double[][] sdsm, double[][] ddsm, Set<Set<Integer>> clusters);
    
    /** Returns the name of the current DiagramPanel */
    public abstract String toString();
    
    /*
     * Inits the DiagramFrame
     * should not be called on instantiated but when something is going to be drawn
     */
    public void init() {
               setSize(DIAGRAMFRAMEWIDTH, DIAGRAMFRAMEHEIGHT);
               setResizable(false);
               setVisible(true);
    }
    
}
