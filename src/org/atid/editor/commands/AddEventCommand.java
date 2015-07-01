/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.atid.editor.commands;

import org.atid.petrinet.CompositeActivity;
import org.atid.petrinet.Event;
import org.atid.petrinet.PetriNet;
import org.atid.util.Command;

/**
 *
 * @author caiolucena
 */
public class AddEventCommand implements Command{

    private CompositeActivity compositeActivity;
    private int x, y;
    private Event createdEvent;
    private PetriNet petriNet;

    public AddEventCommand(CompositeActivity currentCompositeActivity, int x, int y, PetriNet petriNet){
        this.compositeActivity = currentCompositeActivity;
        this.x = x;
        this.y = y;
        this.petriNet = petriNet;
    }
    
    
    public void execute() {
        createdEvent = new Event();
        createdEvent.setCenter(x, y);
        petriNet.getNodeSimpleIdGenerator().setUniqueId(createdEvent);
        petriNet.getNodeLabelGenerator().setLabelToNewlyCreatedNode(createdEvent);
        compositeActivity.addElement(createdEvent);
    }

    
    public void undo() {
        new DeleteElementCommand(createdEvent).execute();
    }

    
    public void redo() {
        compositeActivity.addElement(createdEvent);
    }
    
    @Override
    public String toString(){
        return "Add Event";
    }
}
