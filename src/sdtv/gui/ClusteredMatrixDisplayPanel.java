/*
 * ClusteredMatrixDisplayPanel.java
 * a JPanel that displays a clustered DSM on a GUI using JLabels in a GridLayout
 */

package sdtv.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import sdtv.common.Constants;
import java.awt.Dimension;

/**
 *
 * @author Erik
 */
public class ClusteredMatrixDisplayPanel extends JPanel implements Constants {
    
    private ColorRenderer colorRenderer;
    private JTable result;
    
    // cluster colors. 
    public static final Color[] clustercolors = new Color[] { 
        Color.YELLOW, Color.RED, Color.GREEN, Color.BLUE,
        Color.CYAN, Color.GRAY, Color.MAGENTA, Color.ORANGE, 
        Color.PINK, Color.LIGHT_GRAY
    
    };
        
    /** Creates a new instance of ClusteredMatrixDisplayPanel */
    public ClusteredMatrixDisplayPanel(final double[][] input, Set<Set<Integer>> clusters) {
           
        List<Integer> orderBuild = new ArrayList<Integer>();
        Map<Integer, Integer> orderCluster = new HashMap<Integer, Integer>();
        
        int clusternumber = 0;
        int col, row;
        
        for (Set<Integer> cluster: clusters) {
            for (Integer clusterElement : cluster) {
                if (!orderBuild.contains(clusterElement)) {
                    orderBuild.add(clusterElement);
                    orderCluster.put(clusterElement, clusternumber);
                }
            }
            
            clusternumber++;
        }
        
        final List<Integer> order = orderBuild;
        
        TableModel content = new AbstractTableModel() {
            public int getColumnCount() { return input.length + 1; }
            public int getRowCount() { return input.length + 1; }
            public Object getValueAt(int row, int col) {
                if (row == 0 && col == 0)
                    return " ";          
                else if (row == 0)
                    return order.get(col-1);
                else if (col == 0)
                    return order.get(row-1);
                else {
                    if (input[order.get(row-1)][order.get(col-1)] > 0)
                        return "x";
                    else
                        return " ";
                }
            }
        };
        
        JTable table = new JTable(content) {
	
            public Class getColumnClass(int column) {
		return getValueAt(0, column).getClass();
            }
 
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component c = super.prepareRenderer(renderer, row, column);
		colorRenderer.setBackground(c, row, column);
		return c;
            }
        };
                
        colorRenderer = new ColorRenderer(table);
	
        if (input.length > 1) {      
            colorRenderer.setRowColor(0, Color.LIGHT_GRAY);
            colorRenderer.setColumnColor(0, Color.LIGHT_GRAY);
        };
        
        int colorIndex = 1;
        int cnum = 0;
        for (Set<Integer> cluster: clusters) {
            int s = cluster.size();
            for (int i = colorIndex; i < colorIndex + s; i++)
                for (int j = colorIndex; j < colorIndex + s; j++)
                    colorRenderer.setCellColor(i,j,clustercolors[cnum % clustercolors.length]);
            cnum++;
            colorIndex = colorIndex + s;
        }
            
        
        
        JScrollPane sp = new JScrollPane(table);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int c=0; c < table.getColumnCount(); c++) {
            TableColumn cl = table.getColumnModel().getColumn(c);
            cl.setPreferredWidth(DEFAULTCELLDIM);
        }
        for (int r=0; r < table.getRowCount(); r++) {
            table.setRowHeight(r, DEFAULTCELLDIM);
        }
        
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(true);
        table.setCellSelectionEnabled(true);
  
        table.setSelectionBackground(Color.YELLOW);
   
        
        JLabel renderer = ((JLabel)table.getDefaultRenderer(Object.class));
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
  
        setLayout(new FlowLayout());
       
        // colors cells black if there's an X in them
        
        for (int r = 0; r < table.getRowCount(); r++)
        	for (int c = 0; c < table.getColumnCount(); c++)
        		if (table.getValueAt(r, c).equals("x"))
        			colorRenderer.setCellColor(r,c,Color.BLACK);
        
        
        add(table);
        
        result = table;
        
    }
    
    public JTable getResultTable() {
    	return result;
    }
    

       
    
}
