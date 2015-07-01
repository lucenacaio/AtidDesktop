/*
 * Copyright (C) 2008-2010 Martin Riesz <riesz.martin at gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.atid.petrinet;

import java.util.Collection;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class NodeLabelGenerator {

    private int nextUniqueSimpleActivityNumber = 1;
    private int nextUniqueTransitionNumber = 1;
    private int nextUniqueCompositeActivityNumber = 1;
    private int nextUniqueEventNumber = 1;
    private int nextUniqueRespositoryNumber = 1;

    private PetriNet petriNet;

     
    
    

    public NodeLabelGenerator(PetriNet petriNet) {
        this.petriNet = petriNet;
    }

    private void setUniqueLabel(Node node) {
        String label = "";
        if (node instanceof SimpleActivity) {
            label = getPrefix(node) + Integer.toString(nextUniqueSimpleActivityNumber++);
        } else if (node instanceof Transition) {
            label = getPrefix(node) + Integer.toString(nextUniqueTransitionNumber++);
        } else if (node instanceof CompositeActivity) {
            label = getPrefix(node) + Integer.toString(nextUniqueCompositeActivityNumber++);
        } else if (node instanceof ReferenceSimpleActivity) {
            throw new RuntimeException("Why would anyone want to label a ReferenceSimpleActivity?");
        } else if(node instanceof EventNode){
            label = getPrefix(node) + Integer.toString(nextUniqueEventNumber++);
        } else if(node instanceof RepositoryNode){
            label = getPrefix(node) + Integer.toString(nextUniqueRespositoryNumber++);
        } else {
            throw new RuntimeException("Node which is neither SimpleActivity nor Transition nor Composite Activity nor ReferenceSimpleActivity.");
        }
        node.setLabel(label);
    }

    private String getPrefix(Node node) {
        String prefix;
        if (node instanceof SimpleActivity) {
            prefix = "p";
        } else if (node instanceof Transition) {
            prefix = "t";
        } else if (node instanceof CompositeActivity) {
            prefix = "s";
        } else if(node instanceof EventNode){
            prefix = "e";
        } else if(node instanceof RepositoryNode){
            prefix = "r";
        } else if (node instanceof ReferenceSimpleActivity) {
            throw new RuntimeException("Why would anyone want to label a ReferenceSimpleActivity?");
        } else {
            throw new RuntimeException("Node which is neither SimpleActivity nor Transition nor Composite Activity nor ReferenceSimpleActivity.");
        }
        return prefix;
    }

    public void setLabelToNewlyCreatedNode(Node node) {
		setUniqueLabel(node);
    }

    public void setLabelsToPastedContent(Collection<Element> elements) {
        for (Element element : elements) {
            if (element instanceof Node && !(element instanceof ReferenceSimpleActivity)) {
				setUniqueLabel((Node)element);
            }
        }
    }

    public void setLabelsToReplacedCompositeActivity(CompositeActivity compositeActivity) {
        for (Node node : compositeActivity.getNodesRecursively()) {
            if (!(node instanceof ReferenceSimpleActivity)) {
				if (isNodeAutolabeled(node)) {
					setUniqueLabel(node);
				}
            }
        }
    }


    public void cloneLabel(Node newNode, Node oldNode) {
		if (isNodeAutolabeled(oldNode)) {
			setUniqueLabel(newNode);
		}
		else {
        newNode.setLabel(oldNode.getLabel());
		}
    }

    private boolean isNodeAutolabeled(Node node) {
        return node.getLabel().matches("^" + getPrefix(node) + "[0-9]+$");
    }

    public void fixFutureUniqueLabels() {
        int maxSimpleActivityNumber = 0;
        int maxTransitionNumber = 0;
        int maxCompositeActivityNumber = 0;
        int maxEventNumber = 0;
        int maxRepositoryNumber = 0;
        
        for(Event event: petriNet.getRootCompositeActivity().getEventsRecursively()){
            String eventLabel = event.getLabel();
            if(eventLabel != null && eventLabel.startsWith(getPrefix(event))){
                try{
                    int eventNumber = Integer.parseInt(eventLabel.substring(1));
                    if(eventNumber > maxEventNumber){
                        maxEventNumber = eventNumber;
                    }
                } catch(NumberFormatException ex){
                    //do nothing
                }
            }
        }    
        for(Repository repository: petriNet.getRootCompositeActivity().getRepositorysRecursively()){
            String repositoryLabel = repository.getLabel();
            if(repositoryLabel != null && repositoryLabel.startsWith(getPrefix(repository))){
                try{
                    int repositoryNumber = Integer.parseInt(repositoryLabel.substring(1));
                    if(repositoryNumber > maxEventNumber){
                        maxEventNumber = repositoryNumber;
                    }
                } catch(NumberFormatException ex){
                    //do nothing
                }
            }
        }    
        
        for (SimpleActivity simpleActivity : petriNet.getRootCompositeActivity().getSimpleActivitysRecursively()) {
            String simpleActivityLabel = simpleActivity.getLabel();
            if (simpleActivityLabel != null && simpleActivityLabel.startsWith(getPrefix(simpleActivity))) {
                try {
                    int simpleActivityNumber = Integer.parseInt(simpleActivityLabel.substring(1));
                    if (simpleActivityNumber > maxSimpleActivityNumber) {
                        maxSimpleActivityNumber = simpleActivityNumber;
                    }
                } catch (NumberFormatException ex) {
                    //do nothing
                }
            }
        }
        for (Transition transition : petriNet.getRootCompositeActivity().getTransitionsRecursively()) {
            String transitionLabel = transition.getLabel();
            if (transitionLabel != null && transitionLabel.startsWith(getPrefix(transition))) {
                try {
                    int transitionNumber = Integer.parseInt(transitionLabel.substring(1));
                    if (transitionNumber > maxTransitionNumber) {
                        maxTransitionNumber = transitionNumber;
                    }
                } catch (NumberFormatException ex) {
                    //do nothing
                }
            }
        }
        for (CompositeActivity compositeActivity : petriNet.getRootCompositeActivity().getCompositeActivitysRecursively()) {
            String compositeActivityLabel = compositeActivity.getLabel();
            if (compositeActivityLabel != null && compositeActivityLabel.startsWith(getPrefix(compositeActivity))) {
                try {
                    int compositeActivityNumber = Integer.parseInt(compositeActivityLabel.substring(1));
                    if (compositeActivityNumber > maxCompositeActivityNumber) {
                        maxCompositeActivityNumber = compositeActivityNumber;
                    }
                } catch (NumberFormatException ex) {
                    //do nothing
                }
            }
        }
        nextUniqueSimpleActivityNumber = maxSimpleActivityNumber + 1;
        nextUniqueTransitionNumber = maxTransitionNumber + 1;
        nextUniqueCompositeActivityNumber = maxCompositeActivityNumber + 1;
        nextUniqueEventNumber = maxEventNumber + 1;
        nextUniqueRespositoryNumber = maxRepositoryNumber + 1; 
    }
}
