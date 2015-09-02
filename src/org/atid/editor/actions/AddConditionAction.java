/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.atid.editor.actions;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import static javax.swing.Action.ACCELERATOR_KEY;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.atid.compiler.LexicalAnalyzer;
import org.atid.editor.Atid;
import org.atid.editor.Root;
import org.atid.petrinet.PetriNet;
import org.atid.petrinet.Transition;
import org.atid.petrinet.TransitionNode;
import org.atid.util.CachedGraphics2D;
import org.atid.util.GraphicsTools;

/**
 *
 * @author caio
 */
public class AddConditionAction extends AbstractAction{

    private Root root;
       
    public AddConditionAction(Root root) {
        this.root = root;
        String name = "Condition";
        putValue(NAME, name);
        putValue(SHORT_DESCRIPTION, name);
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        
        if(root.getClickedElement() != null){
            if(root.getClickedElement() instanceof Transition){
                Transition transition = (Transition) root.getClickedElement();
                
                String condition = JOptionPane.showInputDialog(root.getParentFrame(),"Condition: ", transition.getCondition());
                if(condition.equals("") && condition != null){
                    transition.setRest(false);
                    transition.setCondition(condition);
                    Atid.getRoot().getDrawingBoard().repaint();
                }
                else if(!condition.equals("")){
                    transition.setRest(true);
                    transition.setCondition(condition);
                    JOptionPane.showMessageDialog(root.getParentFrame(), analiser(condition).toString());
                    Atid.getRoot().getDrawingBoard().repaint();
                }
           }
        }
        
        
    }
    
    public StringBuilder analiser(String condition){
        StringBuilder sb = new StringBuilder();
        LexicalAnalyzer lexico = new LexicalAnalyzer();
        lexico.analyze(condition);
        
        List<String> lista = lexico.getTokens();
        
        for (String token : lista){
            
            sb.append(token + "\n");
        }
        return sb;
    }
}
