package org.atid.editor;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import static java.lang.Thread.sleep;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import org.atid.util.GraphicsTools;

/**
 *
 * @author caiolucena
 */
public class HomeFrame extends JWindow{
    
    private JProgressBar progressBar;
    private final String[] args;
    
    public HomeFrame(final String[] args){
       
        this.args = args;
        
        int width = this.getToolkit().getDefaultToolkit().getScreenSize().width;
        int heigth = this.getToolkit().getDefaultToolkit().getScreenSize().height;
        int z = 2;
        int x = (width - 521) / z;
        int y = (heigth - 295) / z;
        
        JLabel img = new JLabel();
        img.setIcon(GraphicsTools.getIcon("loadImageAtid.png"));
        img.setLocation(new Point(0, 0));
        img.setSize(521,247);
    
        JButton bSignUp = new JButton();
        bSignUp.setText("Sign up");
        bSignUp.setBounds(250, 250, 150, 40);
        this.add(bSignUp);
        
        bSignUp.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt){
                JOptionPane.showMessageDialog(null, "Not implemented yet." );
            }
        });
        
        JButton bSignIn = new JButton();
        bSignIn.setText("Sign in");
        bSignIn.setBounds(100, 250, 150,40);
        bSignIn.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent evt){
                new LoginFrame(args);
                close();
            }
        });
        

        
        this.add(bSignIn);
        this.setLayout(null);
        this.add(img);
        this.setLocation(new Point(x, y));
        this.setSize(521,295);
        this.setVisible(true);
        

        
        
        
    }
    
    public void close(){
        this.dispose();
    }
    

    
}
