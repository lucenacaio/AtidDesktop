/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.atid.editor.commands;

import java.util.List;
import javax.swing.JOptionPane;
import org.atid.compiler.LexicalAnalyzer;
import org.atid.editor.Atid;
import org.atid.petrinet.Transition;
import org.atid.util.Command;

/**
 *
 * @author caio
 */
public class AddCondition implements Command {

    private static  Transition t;
    private static String condition;
    private static AddCondition instance = new AddCondition();

    private AddCondition() {
    }

    public static AddCondition getInstance(){
        return instance;
    }
    
    @Override
    public void execute() {
        if (condition.equals("") && condition != null) {
            t.setRest(false);
            t.setCondition(condition);
            Atid.getRoot().getDrawingBoard().repaint();
        } else if (!condition.equals("")) {
            t.setRest(true);
            t.setCondition(condition);
            JOptionPane.showMessageDialog(Atid.getRoot().getParentFrame(), analiser(condition).toString());
            Atid.getRoot().getDrawingBoard().repaint();
        }

    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void redo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setTransition(Transition transition) {
        this.t = transition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
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
