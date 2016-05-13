/*
 * MatrixDisplayPanel.java
 * a JPanel that displays a  DSM on a GUI using JLabels in a GridLayout
 */
 package sdtv.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
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

/**
 *
 * @author Erik
 */
public class MatrixDisplayPanel extends JPanel implements Constants{
    
    static public final int CELLDIM = 1; // the width and height of columns
    
    ColorRenderer colorRenderer;
    
    /** Creates a new instance of MatrixDisplayPanel
     * which is a JPanel that displays a DSM on a GUI using JLabels in a GridLayout
     * @param input the software DSM
     */
    public MatrixDisplayPanel(final double[][] input) {
          	
    	int width = input.length;
       
        
        TableModel content = new AbstractTableModel() {
            public int getColumnCount() { return input.length + 1; }
            public int getRowCount() { return input.length + 1; }
            public Object getValueAt(int row, int col) {
                if (row == 0 && col == 0)
                    return " ";          
                else if (row == 0)
                    return col-1;
                else if (col == 0)
                    return row-1;
                else {
                    if (input[row-1][col-1] > 0)
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
        }
        
        JScrollPane sp = new JScrollPane(table);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int c=0; c < table.getColumnCount(); c++) {
            TableColumn col = table.getColumnModel().getColumn(c);
            col.setPreferredWidth(DEFAULTCELLDIM);
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
        add(table);
        
        
        
    }
    
   
    

    
}
