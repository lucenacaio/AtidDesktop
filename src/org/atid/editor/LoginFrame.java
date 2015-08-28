package org.atid.editor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import org.atid.util.GraphicsTools;

public class LoginFrame extends JWindow {

    private final String[] args;
    public LoginFrame(String[] args) {
        this.args = args;
        
        int width = this.getToolkit().getDefaultToolkit().getScreenSize().width;
        int heigth = this.getToolkit().getDefaultToolkit().getScreenSize().height;
        int z = 2;
        int x = (width - 521) / z;
        int y = (heigth - 325) / z;
        this.setLocation(new Point(x, y));
        this.setSize(521,265);
        this.setVisible(true);
        
       lblLogin = new javax.swing.JPanel();
        lblUser = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        btSignIn = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();
        txtUser.setEnabled(true);
        txtUser.setEditable(true);



        lblLogin.setBorder(javax.swing.BorderFactory.createTitledBorder("ATID - Sign in"));
        lblLogin.setToolTipText("");

        lblUser.setText("User:");

        lblPassword.setText("Password:");

        javax.swing.GroupLayout pDadosLayout = new javax.swing.GroupLayout(lblLogin);
        lblLogin.setLayout(pDadosLayout);
        pDadosLayout.setHorizontalGroup(
            pDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pDadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblUser)
                    .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPassword)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pDadosLayout.setVerticalGroup(
            pDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pDadosLayout.createSequentialGroup()
                .addComponent(lblUser)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblPassword)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        btSignIn.setText("Sign in");
        btSignIn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btSignIn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bOKMouseClicked(evt);
            }
        });

        btCancel.setText("Cancel");
        btCancel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btCancel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btCancelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblLogin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(51, Short.MAX_VALUE)
                .addComponent(btSignIn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(118, 118, 118)
                .addComponent(btCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(128, 128, 128))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btSignIn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        
    }
    
     private void bOKMouseClicked(java.awt.event.MouseEvent evt) {
        new RootPflow(args);
        this.dispose();
    }

    private void btCancelMouseClicked(java.awt.event.MouseEvent evt) {
        this.dispose();
        new HomeFrame(args);
    }
     private javax.swing.JButton btCancel;
    private javax.swing.JButton btSignIn;
    private javax.swing.JLabel lblUser;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JPanel lblLogin;
    private javax.swing.JTextField txtUser;
    private javax.swing.JPasswordField txtPassword;

    
}
