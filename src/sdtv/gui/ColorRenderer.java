// Source: http://forum.java.sun.com/thread.jspa?threadID=626145&messageID=3575138


package sdtv.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

public class ColorRenderer implements ActionListener
	{
		private JTable table;
		private AbstractTableModel model;
		private Map colors;
		private boolean isBlinking = true;
		private Timer timer;
		private Point location;
 
		public ColorRenderer(JTable table)
		{
			this.table = table;
			model = (AbstractTableModel)table.getModel();
			colors = new HashMap();
			location = new Point();
		}
 
		public void setBackground(Component c, int row, int column)
		{
			//  Don't override the background color of a selected cell
 
			if ( table.isCellSelected(row, column) ) return;
 
			//  The default render does not reset the background color
			//  that was set for the previous cell, so reset it here
 
			if (c instanceof DefaultTableCellRenderer)
			{
				c.setBackground( table.getBackground() );
			}
 
			//  Don't highlight this time
 
			if ( !isBlinking ) return;
 
			//  In case columns have been reordered, convert the column number
 
			column = table.convertColumnIndexToModel(column);
 
			//  Get cell color
 
			Object key = getKey(row, column);
			Object o = colors.get( key );
 
			if (o != null)
			{
				c.setBackground( (Color)o );
				return;
			}
 
			//  Get row color
 
			key = getKey(row, -1);
			o = colors.get( key );
 
			if (o != null)
			{
				c.setBackground( (Color)o );
				return;
			}
 
			//  Get column color
 
			key = getKey(-1, column);
			o = colors.get( key );
 
			if (o != null)
			{
				c.setBackground( (Color)o );
				return;
			}
 
		}
 
		public void setCellColor(int row, int column, Color color)
		{
			Point key = new Point(row, column);
			colors.put(key, color);
		}
 
		public void setColumnColor(int column, Color color)
		{
			setCellColor(-1, column, color);
		}
 
		public void setRowColor(int row, Color color)
		{
			setCellColor(row, -1, color);
		}
 
		private Object getKey(int row, int column)
		{
			location.x = row;
			location.y = column;
			return location;
		}
 
		public void startBlinking(int interval)
		{
			timer = new Timer(interval, this);
			timer.start();
		}
 
		public void stopBlinking()
		{
			timer.stop();
		}
 
		public void actionPerformed(ActionEvent e)
		{
			isBlinking = !isBlinking;
 
			Iterator it = colors.keySet().iterator();
 
			while ( it.hasNext() )
			{
				Point key = (Point)it.next();
				int row = key.x;
				int column = key.y;
 
				if (column == -1)
				{
					model.fireTableRowsUpdated(row, row);
				}
				else if (row == -1)
				{
					int rows = table.getRowCount();
 
					for (int i = 0; i < rows; i++)
					{
						model.fireTableCellUpdated(i, column);
					}
				}
				else
				{
					model.fireTableCellUpdated(row, column);
				}
			}
		}
	}