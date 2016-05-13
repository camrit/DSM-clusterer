/*
 * MatrixInputPanel.java
 * Displays a dialog where two filenames can be provided
 * These are the software and developer DSMs for this application
 */

package sdtv.gui;

import sdtv.common.Constants;
import sdtv.common.Messages;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import sdtv.tools.DSMTools;

public class MatrixInputPanel extends JFrame implements Constants, Messages {
    
    private DataController datacontroller;
    
    private Container cp;
    private JLabel ssLabel, sdLabel;
    private JButton ssBrowse, sdBrowse, okButton, cancelButton, randomButton;
    private JTextField ssText, sdText;
    private JPanel files, control;
    private boolean randomInput;
    
    /** Creates a new instance of MatrixInputPanel */
    public MatrixInputPanel(DataController datacontroller) {
        super(MATRIXINPUTPANELTITLE);
        this.datacontroller = datacontroller;
        this.randomInput = false;
        init();
    }
    
    /** Initializes the GUI */
    public void init() {
        cp = getContentPane();
              
        files = new JPanel();
        files.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        ssText = new JTextField("", FILENAMETEXTFIELDSIZE);
        sdText = new JTextField("", FILENAMETEXTFIELDSIZE);
        
        ssLabel = new JLabel(SSDSMLABELTEXT);
        sdLabel = new JLabel(SDDSMLABELTEXT);
        ssBrowse = new JButton(SSBROWSEBUTTONTEXT);
        ssText.setText("c:/PhD/Work/Programs/NetworkTest/sdtv/src/callGraphMatrixS.out");
        ssBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                ssText.setText(selectFile());
            }
        });
        sdBrowse = new JButton(SDBROWSEBUTTONTEXT);
        sdText.setText("c:/PhD/Work/Programs/NetworkTest/sdtv/src/callGraphMatrixD.out");
        
        sdBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                sdText.setText(selectFile());
            }
        });
      
        
        files.add(ssLabel); files.add(ssText); files.add(ssBrowse);
        files.add(sdLabel); files.add(sdText); files.add(sdBrowse);
        
        cp.add(files, BorderLayout.CENTER);
        
        control = new JPanel();
        control.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        randomButton = new JButton(RANDOMBUTTONTEXT);
        randomButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent ev) {
               new RandomInputPanel();
           } 
        });
        
        okButton = new JButton(OKBUTTONTEXT); // OK button checks input and returns if OK
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                boolean success = randomInput || datacontroller.loadData(ssText.getText(), sdText.getText());
                if (success) {
                    datacontroller.confirmLoad();
                    MatrixInputPanel.this.dispose();
                    DSMTools.calcPropagationCost(datacontroller.getSDSM());
                    DSMTools.calcPropagationCost(datacontroller.getDDSM());
                }
                else
                    JOptionPane.showMessageDialog(null, INPUTFILEERROR + "\n" + datacontroller.getError(), DIALOG_INPUTFILEERROR, JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton = new JButton(CANCELBUTTONTEXT);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                MatrixInputPanel.this.dispose();
            }
        });
        
        control.add(randomButton); control.add(cancelButton); control.add(okButton);
        cp.add(control, BorderLayout.SOUTH);
        
        setResizable(false);
        setSize(MATRIXINPUTPANELWIDTH, MATRIXINPUTPANELHEIGHT);
        this.setLocation(MATRIXINPUTPANELX,MATRIXINPUTPANELY);
        setVisible(true);
    }
    
    /** Browse Dialog */
    public String selectFile() {
        JFileChooser fc = new JFileChooser ();
        fc.setDialogTitle(DIALOG_OPENFILE);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setCurrentDirectory(new File("."));
        
        int result = fc.showOpenDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
          return "";
        } else if (result == JFileChooser.APPROVE_OPTION) {
          return fc.getSelectedFile().getPath();
        } else {
          return "";
        }
     
    }
    
    class RandomInputPanel extends JFrame {
        
        RandomInputPanel() {
            super(RANDOMINPUTTITLE);
            this.setSize(300,130);
            this.setLocation(325,325);
            this.setLayout(new FlowLayout());
            this.add(new JLabel(RANDOMINPUTSIZE));
            final JTextField input = new JTextField(""+5,5);
            this.add(input);
            ButtonGroup randSelect = new ButtonGroup();
            final JRadioButton mathrandom = new JRadioButton();
            final JRadioButton jungrandom = new JRadioButton();
            jungrandom.setSelected(true);
            randSelect.add(mathrandom);
            randSelect.add(jungrandom);
            JButton ok = new JButton(OKBUTTONTEXT);
            ok.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    try {
                        int size = Integer.parseInt(input.getText());
                        if (size < 1)
                            throw new NumberFormatException();
                        
                        randomInput = true;
                        if (jungrandom.isSelected())
                            datacontroller.setSDSM(DSMTools.randomJungDSM(size));
                        else
                            datacontroller.setSDSM(DSMTools.randomDSM(size));
                        
                        double[][] ddsm = new double[1][size];
                        for (int i = 0; i < size; i++)
                            ddsm[0][i] = 1.0;
                        datacontroller.setDDSM(ddsm);
                        ssText.setText(RANDOMINPUT);
                        sdText.setText(RANDOMINPUT);
                        ssBrowse.setEnabled(false);
                        sdBrowse.setEnabled(false);
                        RandomInputPanel.this.dispose();
                    } catch (NumberFormatException e) {
                           JOptionPane.showMessageDialog(null, NOTANINTEGER, DIALOG_INPUTERROR, JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            this.add(mathrandom);
            this.add(new JLabel(MATHRANDOMRADIOBUTTON));
            this.add(jungrandom);
            this.add(new JLabel(JUNGRANDOMRADIOBUTTON));
            this.add(ok);
            this.setVisible(true);
        }
        
    }
    
    
}
