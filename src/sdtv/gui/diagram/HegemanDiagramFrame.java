/*
 * HegemanDiagramFrame.java
 *
 * Created on 14 april 2007, 17:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sdtv.gui.diagram;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import sdtv.common.Constants;

/**
 *
 * @author Erik
 */
public class HegemanDiagramFrame extends DiagramFrame implements Constants {
    
    // Defines the title of this diagram style
    static public final String ID = "HegemanDiagramStyle";
    
    // Defines the size (relative to frame) of the Inner circle (for developers)
    static public final int INNERCIRCLERADIUSDIVIDER = 4;
    static public final int DEVDIM = 10;
    
    private GridBagConstraints gbc;
    
    
    public void drawDiagram(double[][] sdsm, double[][] ddsm, Set<Set<Integer>> clusters) {
         init();  
         ImageIcon[] developerIcons = new ImageIcon[ddsm.length];
         this.gbc = new GridBagConstraints();
         getContentPane().setLayout(new GridBagLayout());
         int innerCircleCenterx = DIAGRAMFRAMEWIDTH / 2;
         int innerCircleCentery = DIAGRAMFRAMEHEIGHT / 2;  
         int n = ddsm.length;
         int innerCircleRadius;
         if (DIAGRAMFRAMEWIDTH > DIAGRAMFRAMEHEIGHT)
            innerCircleRadius = DIAGRAMFRAMEWIDTH / INNERCIRCLERADIUSDIVIDER;
         else
            innerCircleRadius = DIAGRAMFRAMEWIDTH / INNERCIRCLERADIUSDIVIDER;
         
         
         double anglePerDeveloper = 2 * Math.PI / (double)n;       
         System.out.println(n);
         System.out.println(anglePerDeveloper);
         for (int i = 0; i < developerIcons.length; i++) {
             //in a 360 deg circle position everyone 200 pix from center
             double thisAngle = anglePerDeveloper * (i + 1);
             
             double x  = ((double)innerCircleCenterx + (double)innerCircleRadius * Math.cos(thisAngle));
             double y  = ((double)innerCircleCentery + (double)innerCircleRadius * Math.sin(thisAngle));
           
             gbc.gridx = (int)x;
             gbc.gridy = (int)y;
             
             JLabel imagelabel = new JLabel();
             imagelabel.setIcon(DEVELOPERICON);
             getContentPane().add(imagelabel);
             
             System.out.println(x);
             System.out.println(y);
          
             
             setVisible(true);
         }
         
     
         
    }
    
    public String toString() {
        return ID;
    }
   
    
}
