
package binarizer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class UI {
    
    JFrame mainW;
    Dimension size = new Dimension(800, 500);
    Dimension conSize = new Dimension(150, 50);
    Dimension textSize = new Dimension(100, 20);
    Dimension minSize = new Dimension(50, 50);
    JButton im1;
    JButton im2;
    
    JButton load;
    JButton binarize;
    
    JPanel mainPan;
    JPanel centerPan;
    JPanel controlPan;
    
    JPanel gaussBorder;
    JCheckBox gaussBlur;
    
    JPanel closeBorder;
    JCheckBox close;
    
    JPanel threshholdBorder;
    JTextField threshold;
    
    JPanel threshAutoBorder;
    JCheckBox threshAuto;
    
    JPanel results;
    JPanel martBorder;
    JLabel mart;
    JPanel ferrBorder;
    JLabel ferr;
    
    String path;
    String filename;
    Boolean gaussCheck;
    Boolean closeCheck;
    Boolean autoCheck;
    
    
    UI(){
        mainPan = new JPanel();
        centerPan = new JPanel();
        controlPan = new JPanel();
        results = new JPanel();
        
        //======================================================================
        
        im1 = new JButton();
        im2 = new JButton();
        
        //======================================================================
        
        load = new JButton("Załaduj...");
        controlPan.add(load);
        
        //======================================================================
        
        binarize = new JButton("Binaryzuj");
        controlPan.add(binarize);
        
        //======================================================================
        
        gaussBlur = new JCheckBox();
        gaussBorder = new JPanel();
        gaussBorder.setBorder(new TitledBorder("Rozmycie Gaussa"));
        gaussBorder.add(gaussBlur);
        gaussBorder.setPreferredSize(conSize);
        controlPan.add(gaussBorder);
        
        //======================================================================
        
        close = new JCheckBox();
        closeBorder = new JPanel();
        closeBorder.setBorder(new TitledBorder("Zamknięcie"));
        closeBorder.add(close);
        closeBorder.setPreferredSize(conSize);
        controlPan.add(closeBorder);
        
        //======================================================================
        
        threshold = new JTextField();
        threshold.setPreferredSize(textSize);
        threshholdBorder = new JPanel();
        threshholdBorder.setBorder(new TitledBorder("Próg"));
        threshholdBorder.add(threshold);
        threshholdBorder.setPreferredSize(conSize);
        controlPan.add(threshholdBorder);
        
        //======================================================================
        
        threshAuto = new JCheckBox();
        threshAutoBorder = new JPanel();
        threshAutoBorder.setBorder(new TitledBorder("Auto"));
        threshAutoBorder.add(threshAuto);
        threshAutoBorder.setPreferredSize(minSize);
        controlPan.add(threshAutoBorder);
        
        //======================================================================

        ferr = new JLabel("0%");
        ferr.setSize(textSize);
        ferrBorder = new JPanel();
        ferrBorder.setBorder(new TitledBorder("Ferryt"));
        ferrBorder.setPreferredSize(conSize);
        ferrBorder.add(ferr);
        results.add(ferrBorder);
        
        //======================================================================

        mart = new JLabel("0%");
        mart.setSize(textSize);
        martBorder = new JPanel();
        martBorder.setBorder(new TitledBorder("Martenzyt"));
        martBorder.setPreferredSize(conSize);
        martBorder.add(mart);
        results.add(martBorder);
        
        //======================================================================
        
        centerPan.setLayout(new GridLayout(1, 2));
        centerPan.add(im1);
        centerPan.add(im2);
        
        //======================================================================
        
        mainPan.setLayout(new BorderLayout());
        mainPan.add(centerPan, BorderLayout.CENTER);
        mainPan.add(controlPan, BorderLayout.SOUTH);
        mainPan.add(results, BorderLayout.NORTH);
        
        //======================================================================
        
        mainW = new JFrame("Binaryzator");
        mainW.setSize(size);
        mainW.setVisible(true);
        mainW.setLocationRelativeTo(null);
        
        //======================================================================
        
        mainW.add(mainPan);
        
        mainW.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    void process(Boolean gauss, Boolean auto, Boolean close, String path, int thresholVal, String filename){
    
                //BINARYZACJA
                Mat source = Imgcodecs.imread(""+path, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                Mat destination = new Mat(source.rows(),source.cols(),source.type());
                
                if(gauss)
                    Imgproc.GaussianBlur(source, source, new Size(5,5), 0);
                
                if(auto)
                    Imgproc.threshold(source, destination, 0, 255, Imgproc.THRESH_BINARY_INV+Imgproc.THRESH_OTSU);
                else
                    Imgproc.threshold(source, destination, thresholVal, 255, Imgproc.THRESH_BINARY_INV);

                //EROZJA
                if(close){
                    source = destination;

                    int erosion_size = 1;
                    int dilation_size = 1;

                    Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2*erosion_size + 1, 2*erosion_size+1));
                    Imgproc.erode(source, destination, element);

                    //DYLATACJA
                    source = destination;

                    Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*dilation_size + 1, 2*dilation_size+1));
                    Imgproc.dilate(source, destination, element1);
                    
                }
                
                float all = destination.cols() * destination.rows();
                float nonZeros = Core.countNonZero(destination);
                
                float ferrite = nonZeros/all;
                float martenzite = 1 - ferrite;
                
                ferr.setText("" + ferrite*100 + "%");
                mart.setText("" + martenzite*100 + "%");
                
                System.out.println("" + martenzite);
                
                Imgcodecs.imwrite("Converted/" + filename + ".png", destination);
                im2.setIcon(null);
                  
        try {
            BufferedImage bufImg = ImageIO.read(new File("Converted/" + filename + ".png"));
            im2.setIcon(new ImageIcon(bufImg));
        } catch (IOException ex) {
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    void buttonControl(){
        
        load.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("."));
                
                if(chooser.showOpenDialog(mainW) == JFileChooser.APPROVE_OPTION){
                    path = "" + chooser.getSelectedFile();
                    filename = "" + chooser.getSelectedFile().getName();
                    JOptionPane.showMessageDialog(mainW, "Obraz załadowany.");
                    im1.setIcon(new ImageIcon(path));
                    im2.setIcon(null);
                }
            }
        });
        
        //======================================================================
        
        binarize.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                im2.setIcon(null);
                gaussCheck = gaussBlur.isSelected();
                autoCheck = threshAuto.isSelected();
                closeCheck = close.isSelected();
                
                int thresholdVal = 0;
                
                if(!threshold.getText().equals(""))
                    thresholdVal = Integer.parseInt(threshold.getText());
                process(gaussCheck, autoCheck, closeCheck, path, thresholdVal, filename);
            }
        });
    
        threshAuto.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if(threshAuto.isSelected()){
                    threshold.setEditable(false);
                }else
                    threshold.setEditable(true);
            }
        });
        
    }
    
}
