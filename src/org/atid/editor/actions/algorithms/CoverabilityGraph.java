package org.atid.editor.actions.algorithms;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;
import javax.swing.JOptionPane;
import org.atid.editor.Atid;
import org.atid.petrinet.CompositeActivityNode;
import org.atid.petrinet.Element;
import org.atid.petrinet.FiringSequence;
import org.atid.petrinet.Marking;
import org.atid.petrinet.PetriNet;
import org.atid.petrinet.SimpleActivityNode;
import org.atid.petrinet.Transition;
import org.atid.petrinet.TransitionNode;

public class CoverabilityGraph extends AbstractAction{
    
    private PetriNet petriNet;
    private List<CompositeActivityNode> compositeActivity;
    private List<SimpleActivityNode> activities;
    private List<Transition> transitions;
    private StringBuilder sb = new StringBuilder();

    
    public CoverabilityGraph(PetriNet petriNet){
        this.petriNet = petriNet;
        String name = "Coverability Graph";
        putValue(NAME, name);
        putValue(SHORT_DESCRIPTION, name);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
            sb = new StringBuilder();
            sb.append("Converability Graph");
            loadAllLists();
            generateGraph();
            JOptionPane.showMessageDialog(Atid.getRoot().getParentFrame(), sb.toString()
            ,"Information", JOptionPane.INFORMATION_MESSAGE 
            );
           
    }
        
    public void generateGraph(){
        Marking marking = org.atid.editor.Atid.getRoot().getCurrentMarking().getPetriNet().getInitialMarking().getPetriNet().getInitialMarking();
        sb.append(marking);
        
        try {
            for(FiringSequence t : marking.getAllFiringSequencesRecursively()){
    
                for(Transition s : t){
                    if (marking.isEnabled(s) && !s.isFire() ) {
                        marking.fire(s);
                        generateGraph();
                        marking.undoFire(s);
                    }
                }
            } 
        } catch (Exception ex) {
            Logger.getLogger(CoverabilityGraph.class.getName()).log(Level.SEVERE, null, ex);
        }         
    }
    
    private void loadAllLists(){
        activities = new ArrayList<SimpleActivityNode>();
        transitions = new ArrayList<Transition>();
        compositeActivity = new ArrayList<CompositeActivityNode>();
        
        for(Element element : org.atid.editor.Atid.getRoot().getDocument().petriNet.getRootCompositeActivity().getElements()){
            if(element instanceof SimpleActivityNode){
                activities.add((SimpleActivityNode) element);
            }else if(element instanceof TransitionNode){
                Transition t = (Transition) element;
                t.setFire(false);
                transitions.add(t);
            } else if(element instanceof CompositeActivityNode){
                compositeActivity.add((CompositeActivityNode) element);
            }
        }
        if(petriNet.getCurrentCompositeActivity().getElements().size() < 5 && (activities.size() < 3 || transitions.size() < 2)){
             JOptionPane.showMessageDialog(Atid.getRoot().getParentFrame(), "Invalid Subnet", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
