/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.atid.editor.commands;

import org.atid.petrinet.CompositeActivity;
import org.atid.petrinet.Event;
import org.atid.petrinet.PetriNet;
import org.atid.petrinet.Repository;
import org.atid.util.Command;

/**
 *
 * @author caiolucena
 */
public class AddRepositoryCommand implements Command{
    
    private CompositeActivity compositeActivity;
    private int x, y;
    private Repository createdRepository;
    private PetriNet petriNet;

    public AddRepositoryCommand(CompositeActivity currentCompositeActivity, int x, int y, PetriNet petriNet){
        this.compositeActivity = currentCompositeActivity;
        this.x = x;
        this.y = y;
        this.petriNet = petriNet;
    }
    
    
    public void execute() {
        createdRepository = new Repository();
        createdRepository.setCenter(x, y);
        petriNet.getNodeSimpleIdGenerator().setUniqueId(createdRepository);
        petriNet.getNodeLabelGenerator().setLabelToNewlyCreatedNode(createdRepository);
        compositeActivity.addElement(createdRepository);
    }

    
    public void undo() {
        new DeleteElementCommand(createdRepository).execute();
    }

    
    public void redo() {
        compositeActivity.addElement(createdRepository);
    }
    
    @Override
    public String toString(){
        return "Add Repository";
    }
}
