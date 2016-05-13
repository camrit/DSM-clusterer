/*
 * Constants.java
 * Contains the applications' constant definitions
 */

package sdtv.common;

import java.awt.Color;
import javax.swing.ImageIcon;

/**
 *
 * @author Erik
 */
public interface Constants {
    
    
    
    // Main GUI panel dimensions
    static public final int MAINPANELWIDTH = 900;
    static public final int MAINPANELHEIGHT = 700;
   
    // Main GUI colors
    static public final Color MAINPANELBACKGROUNDCOLOR = Color.lightGray;
    static public final Color CONTROLPANELCOLOR = Color.CYAN;
    static public final Color STATUSPANELCOLOR = Color.CYAN;
    static public final Color CONTENTPANELBACKGROUNDCOLOR = Color.darkGray;
    
    // Matrix Input Panel dimensions
    static public final int MATRIXINPUTPANELWIDTH = 500;
    static public final int MATRIXINPUTPANELHEIGHT = 150;
    static public final int FILENAMETEXTFIELDSIZE = 20;
    static public final int MATRIXINPUTPANELX = 300;
    static public final int MATRIXINPUTPANELY = 300;
    
    // Matrix display params
    static public final int DEFAULTCELLDIM = 10;
    
    // Diagram Frame dimensions
    static public final int DIAGRAMFRAMEWIDTH = 640;
    static public final int DIAGRAMFRAMEHEIGHT = 480;
    
    // Diagram images
    static public final ImageIcon DEVELOPERICON = new ImageIcon("e:\\projects\\SDVT\\man1.jpg");
    
}
