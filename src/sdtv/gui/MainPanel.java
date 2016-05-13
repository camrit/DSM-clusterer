/*
 * MainPanel.java
 *
 * Created on 3 maart 2007, 15:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sdtv.gui;

import sdtv.tools.*;
import sdtv.common.Constants;
import sdtv.common.Messages;
import sdtv.common.Configuration;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import sdtv.clustering.*;
import sdtv.gui.diagram.DiagramFrame;
import sdtv.tools.DSMTools;

/**
 *
 * @author Erik
 */
public class MainPanel extends JFrame implements Constants, Messages, Configuration, Observer {
    
    private DataController datacontroller;
    
    private Container cp;
    private JPanel topPanel, controlPanel, contentPanel, statusPanel;
    private JMenuBar menubar;
    private JMenu filemenu, helpmenu;
    private JMenuItem exit, about;
    private JButton newDiagramButton, generateDiagramButton, showDiagramButton;
    private JComboBox clustererSelect, diagramStyleSelect;
    private JLabel status;
    
    /** Creates a new instance of MainPanel */
    public MainPanel() {
        super(MAINPANELTITLE);
        datacontroller = new DataController();
        datacontroller.addObserver(this);
        init();
    }
    
    /** Initializes the gui by placing all kinds of things on the screen and
     * defining what they do */
    public void init() {
        cp = getContentPane();
        cp.setBackground(MAINPANELBACKGROUNDCOLOR);
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        contentPanel = new JPanel();
        contentPanel.setLayout(new FlowLayout());
        contentPanel.setBackground(CONTENTPANELBACKGROUNDCOLOR);
        
        cp.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
        
        filemenu = new JMenu(FILEMENUTITLE);
        exit = new JMenuItem(FILEEXITTEXT);
        exit.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent ev) {
                System.exit(0);
            }
        });
        filemenu.add(exit);
                     
        helpmenu = new JMenu(HELPMENUTITLE);
        about = new JMenuItem(HELPABOUTTEXT);
        about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                JOptionPane.showMessageDialog(null, ABOUTTEXT, DIALOG_INFO, JOptionPane.INFORMATION_MESSAGE);
            }
        });
        helpmenu.add(about);
        
        menubar = new JMenuBar();
        menubar.add(filemenu);
        menubar.add(helpmenu);
        
        topPanel = new JPanel(new BorderLayout());
        topPanel.add(menubar, BorderLayout.NORTH);
        
   
        controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(2,4));
        controlPanel.setBackground(CONTROLPANELCOLOR);
        
        newDiagramButton = new JButton(NEWDGBUTTONTEXT);
        newDiagramButton.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent ev) {               
                contentPanel.removeAll();
                contentPanel.repaint();
                datacontroller.reset();
                startMatrixInputPanel();
            }
        });
      
        
        clustererSelect = new JComboBox(CLUSTERMODULELIST);
        clustererSelect.setEditable(false);
        
        diagramStyleSelect = new JComboBox(DIAGRAMPANELLIST);
        diagramStyleSelect.setEditable(false);
        
        
        generateDiagramButton = new JButton(GENDIAGRAMBUTTONTEXT);
        generateDiagramButton.setEnabled(false);
        generateDiagramButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                Clusterer c = (Clusterer)clustererSelect.getSelectedItem();
                sdtv.tools.DSMTools.readTasksClusteredCost();
                double starttime = System.currentTimeMillis();
                datacontroller.setClusters(c.clusterSoftwareDSM(datacontroller.getSDSM()));
                status.setText(status.getText() + " (" + CLUSTERTIME + " " + ((System.currentTimeMillis() - starttime) / 1000) + "s)");
                generateDiagramButton.setEnabled(false);
               
            }
        });
        
        
        showDiagramButton = new JButton(DISPDIAGRAMBUTTONTEXT);
        showDiagramButton.setEnabled(false);
        showDiagramButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                DiagramFrame diagramFrame = (DiagramFrame)diagramStyleSelect.getSelectedItem();
                diagramFrame.drawDiagram(datacontroller.getSDSM(), datacontroller.getDDSM(), datacontroller.getClusters());            
               
                diagramFrame.setVisible(true);
            }
            
        });
            
        
        status = new JLabel(DEFAULTSTATUS);
        
        controlPanel.add(new JLabel(""));
        controlPanel.add(new JLabel(CLUSTERSELECTLABEL));
        controlPanel.add(new JLabel(""));
        controlPanel.add(new JLabel(DIAGRAMSELECTLABEL));
        
        controlPanel.add(new JLabel(""));
        
        controlPanel.add(newDiagramButton);
        controlPanel.add(clustererSelect);
        controlPanel.add(generateDiagramButton);
        controlPanel.add(diagramStyleSelect);
        controlPanel.add(showDiagramButton);
        
        statusPanel = new JPanel();
        statusPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(STATUSPANELCOLOR);
        statusPanel.add(status);
        
        topPanel.add(controlPanel, BorderLayout.CENTER);
        cp.add(topPanel, BorderLayout.NORTH);
        cp.add(statusPanel, BorderLayout.SOUTH);
       
        
        setSize(MAINPANELWIDTH, MAINPANELHEIGHT);
        setVisible(true);
    }
    
    /** What should happen when one clicks the New Diagram button? */
    public void startMatrixInputPanel() {
        new MatrixInputPanel(datacontroller);
    }
    
    /** Changes the status JLabel content */
    public void setStatus(String msg) {
        status.setText(msg);
    }
    
    /** Called when something happens to the datacontroller
     * Figures out what it was and updates GUI accordingly
     */
    public void update(Observable o, Object arg) {
   
       DataController d = (DataController)o;
       if (arg != null && arg instanceof String)
            setStatus((String)arg);
       if (d.isReady() && !d.isClustered()) {
            contentPanel.removeAll();
            contentPanel.repaint(); 
            contentPanel.add(new MatrixDisplayPanel(d.getSDSM()));
            contentPanel.add(new MatrixDisplayPanel(d.getDDSM()));
            generateDiagramButton.setEnabled(true);    
            contentPanel.repaint(); 
            this.repaint(); 
       } else if (d.isReady() && d.isClustered()) {
           contentPanel.removeAll();
           contentPanel.repaint();
           ClusteredMatrixDisplayPanel cmd = new ClusteredMatrixDisplayPanel(d.getSDSM(),d.getClusters());
           
           // force the screen to be updated before writing the image
           //setSize(10000,10000);
           contentPanel.add(cmd);
           contentPanel.repaint();
           repaint();
           setVisible(true);
          
           DSMTools.calcClusterDeps(d.getClusters(), d.getSDSM());
           
           // Writes the image containing the contents of the JTable to a file
           new ImageProducer(cmd, "testImage");
           
           //setSize(MAINPANELWIDTH, MAINPANELHEIGHT);
           //repaint();
           //setVisible(true);
           
           showDiagramButton.setEnabled(true);
           
       } else {
            generateDiagramButton.setEnabled(false);
            showDiagramButton.setEnabled(false);
       }
    }
}
