package org.atid.editor;

import java.awt.Color;
import java.awt.Point;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import org.atid.util.GraphicsTools;

/**
 *
 * @author caiolucena
 */
public class LoadFrame extends JWindow{
    
    private JProgressBar progressBar;
    private final String[] args;
    
    public LoadFrame(final String[] args){
       
        this.args = args;
        
        int width = this.getToolkit().getDefaultToolkit().getScreenSize().width;
        int heigth = this.getToolkit().getDefaultToolkit().getScreenSize().height;
        int z = 2;
        int x = (width - 521) / z;
        int y = (heigth - 265) / z;
        
        JLabel img = new JLabel();
        img.setIcon(GraphicsTools.getIcon("loadImageAtid.png"));
        img.setLocation(new Point(0, 0));
        img.setSize(521,247);
    
        
        this.setLayout(null);
        this.add(img);
        this.setLocation(new Point(x, y));
        this.setSize(521,265);
        this.setVisible(true);
        
        progressBar = new JProgressBar();
        progressBar.setBounds(0,245,521,20);
        progressBar.setStringPainted(true);
        this.add(progressBar);
        
        new Thread(){
            public void run(){
                for(int i=0; i < 101; i++){
                    try{
                        progressBar.setValue(i);
                        sleep(50);
                    } catch(InterruptedException el){
                        el.printStackTrace();
                    }
                }
                dispose();
                new HomeFrame(args);
                
            }
        }.start();
        
    }
    
}
