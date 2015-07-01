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
package org.atid.editor.commands;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.atid.petrinet.Arc;
import org.atid.petrinet.ArcEdge;
import org.atid.petrinet.CompositeActivity;
import org.atid.petrinet.Element;
import org.atid.petrinet.ElementCloner;
import org.atid.petrinet.PetriNet;
import org.atid.petrinet.SimpleActivity;
import org.atid.petrinet.SimpleActivityNode;
import org.atid.petrinet.ReferenceSimpleActivity;
import org.atid.petrinet.Transition;
import org.atid.util.CollectionTools;
import org.atid.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class ReplaceCompositeActivityCommand implements Command {

    private CompositeActivity compositeActivity;
    private CompositeActivity storedCompositeActivity;
    private List<Element> previousElements;
    private List<Element> newElements = new LinkedList<Element>();
    private Set<Command> deleteReferenceArcCommands = new HashSet<Command>();
    private PetriNet petriNet;

    public ReplaceCompositeActivityCommand(CompositeActivity compositeActivity, CompositeActivity storedCompositeActivity, PetriNet petriNet) {
        this.petriNet = petriNet;
        this.compositeActivity = compositeActivity;
        CompositeActivity clonedCompositeActivity = ElementCloner.getClone(storedCompositeActivity, petriNet);
        petriNet.getNodeLabelGenerator().setLabelsToReplacedCompositeActivity(clonedCompositeActivity);
        this.storedCompositeActivity = clonedCompositeActivity;

    }

    private Set<ReferenceSimpleActivity> getReferenceSimpleActivitys(CompositeActivity compositeActivity) {
        Set<ReferenceSimpleActivity> referenceSimpleActivitys = new HashSet<ReferenceSimpleActivity>();
        for (Element element : compositeActivity.getElements()) {
            if (element instanceof ReferenceSimpleActivity) {
                ReferenceSimpleActivity referenceSimpleActivity = (ReferenceSimpleActivity) element;
                referenceSimpleActivitys.add(referenceSimpleActivity);
            }
        }
        return referenceSimpleActivitys;
    }

    private Set<ReferenceSimpleActivity> getWeakEquivalents(ReferenceSimpleActivity referenceSimpleActivity, Set<ReferenceSimpleActivity> referenceSimpleActivitys) {
        Set<ReferenceSimpleActivity> weakEquivalents = new HashSet<ReferenceSimpleActivity>();
        for (ReferenceSimpleActivity anotherReferenceSimpleActivity : referenceSimpleActivitys) {
            if (referenceSimpleActivity != anotherReferenceSimpleActivity) {
                if (weakEquivalentReferenceSimpleActivitys(referenceSimpleActivity, anotherReferenceSimpleActivity)) {
                    weakEquivalents.add(anotherReferenceSimpleActivity);
                }
            }
        }
        return weakEquivalents;
    }

    private boolean isWeakEquivalentWithAnotherFrom(ReferenceSimpleActivity referenceSimpleActivity, Set<ReferenceSimpleActivity> referenceSimpleActivitys) {
        return !getWeakEquivalents(referenceSimpleActivity, referenceSimpleActivitys).isEmpty();
    }

    private boolean isStrongEquivalentWithAllWeakEquivalents(ReferenceSimpleActivity referenceSimpleActivity, Set<ReferenceSimpleActivity> referenceSimpleActivitys) {
        Set<ReferenceSimpleActivity> weakEquivalents = getWeakEquivalents(referenceSimpleActivity, referenceSimpleActivitys);
        for (ReferenceSimpleActivity weakEquivalent : weakEquivalents) {
            if (!strongEquivalentReferenceSimpleActivitys(referenceSimpleActivity, weakEquivalent)) {
                return false;
            }
        }
        return true;
    }

    private boolean areAllStrongEquivalent(Set<ReferenceSimpleActivity> referenceSimpleActivitys) {
        ReferenceSimpleActivity someEquivalent = CollectionTools.getFirstElement(referenceSimpleActivitys);
        for (ReferenceSimpleActivity referenceSimpleActivity : referenceSimpleActivitys) {
            if (!strongEquivalentReferenceSimpleActivitys(referenceSimpleActivity, someEquivalent)) {
                return false;
            }
        }
        return true;
    }

    private void connect(ReferenceSimpleActivity storedReferenceSimpleActivity, ReferenceSimpleActivity previousReference, Set<ReferenceSimpleActivity> resolved) {
        storedReferenceSimpleActivity.setConnectedSimpleActivity(previousReference.getConnectedSimpleActivityNode());
        resolved.add(storedReferenceSimpleActivity);
        resolved.add(previousReference);
    }

    private ReferenceSimpleActivity getNewReferenceSimpleActivity(SimpleActivityNode referencedSimpleActivityNode, ReferenceSimpleActivity model) {
        ReferenceSimpleActivity newReferenceSimpleActivity = new ReferenceSimpleActivity(referencedSimpleActivityNode);
        petriNet.getNodeSimpleIdGenerator().setUniqueId(newReferenceSimpleActivity);
        newReferenceSimpleActivity.setCenter(referencedSimpleActivityNode.getCenter().x - compositeActivity.getCenter().x,
                referencedSimpleActivityNode.getCenter().y - compositeActivity.getCenter().y);
        for (Arc arc : model.getConnectedArcs()) { //TODO: !!! what if there are ReferenceArcs?
            Arc newArc = new Arc(newReferenceSimpleActivity, arc.getTransition(), arc.isSimpleActivityToTransition());
            newElements.add(newArc);
        }
        newElements.add(newReferenceSimpleActivity);
        return newReferenceSimpleActivity;
    }

    public void execute() {
        Set<ReferenceSimpleActivity> previousReferenceSimpleActivitys = getReferenceSimpleActivitys(compositeActivity);
        Set<ReferenceSimpleActivity> storedReferenceSimpleActivitys = getReferenceSimpleActivitys(storedCompositeActivity);
        Set<ReferenceSimpleActivity> resolved = new HashSet<ReferenceSimpleActivity>();

        for (ReferenceSimpleActivity storedReferenceSimpleActivity : storedReferenceSimpleActivitys) {
            if (!isWeakEquivalentWithAnotherFrom(storedReferenceSimpleActivity, storedReferenceSimpleActivitys)) {
                Set<ReferenceSimpleActivity> weakEquivalentsFromPrevious = getWeakEquivalents(storedReferenceSimpleActivity, previousReferenceSimpleActivitys);
                if (weakEquivalentsFromPrevious.size() == 1) {
                    ReferenceSimpleActivity previousWeakEquivalent = CollectionTools.getFirstElement(getWeakEquivalents(storedReferenceSimpleActivity, previousReferenceSimpleActivitys));
                    connect(storedReferenceSimpleActivity, previousWeakEquivalent, resolved);
                }
            }
        }

        for (ReferenceSimpleActivity storedReferenceSimpleActivity : storedReferenceSimpleActivitys) {
            if (!resolved.contains(storedReferenceSimpleActivity)) {
                Set<ReferenceSimpleActivity> storedEquivalents = getWeakEquivalents(storedReferenceSimpleActivity, storedReferenceSimpleActivitys);
                storedEquivalents.add(storedReferenceSimpleActivity);

                Set<ReferenceSimpleActivity> previousEquivalents = getWeakEquivalents(storedReferenceSimpleActivity, previousReferenceSimpleActivitys);

                if (areAllStrongEquivalent(storedEquivalents) && areAllStrongEquivalent(previousEquivalents)) {

                    for (ReferenceSimpleActivity previousEquivalent : previousEquivalents) {
                        if (!resolved.contains(previousEquivalent)) {
                            ReferenceSimpleActivity storedEquivalent = CollectionTools.getFirstElementNotIn(storedEquivalents, resolved);

                            if (storedEquivalent == null) {
                                ReferenceSimpleActivity someStoredEquivalent = CollectionTools.getFirstElement(storedEquivalents);
                                storedEquivalent = getNewReferenceSimpleActivity(previousEquivalent.getConnectedSimpleActivityNode(), someStoredEquivalent);
                            }
                            connect(storedEquivalent, previousEquivalent, resolved);
                        }
                    }
                }
            }
        }

        Set<ReferenceSimpleActivity> unresolvedStored = CollectionTools.getElementsNotIn(storedReferenceSimpleActivitys, resolved);
        Set<ReferenceSimpleActivity> unresolvedPrevious = CollectionTools.getElementsNotIn(previousReferenceSimpleActivitys, resolved);
        if (areAllStrongEquivalent(unresolvedStored) && areAllStrongEquivalent(unresolvedPrevious)
                && unresolvedStored.size() >= 1 && unresolvedPrevious.size() >= 1) {
            for (ReferenceSimpleActivity unresolvedOnePrevious : unresolvedPrevious) {
                ReferenceSimpleActivity unresolvedOneStored = CollectionTools.getFirstElementNotIn(unresolvedStored, resolved);

                if (unresolvedOneStored == null) {
                    ReferenceSimpleActivity someUnresolvedStored = CollectionTools.getFirstElement(unresolvedStored);
                    unresolvedOneStored = getNewReferenceSimpleActivity(unresolvedOnePrevious.getConnectedSimpleActivityNode(), someUnresolvedStored);
                }
                connect(unresolvedOneStored, unresolvedOnePrevious, resolved);
            }
        }

        for (ReferenceSimpleActivity previousReferenceSimpleActivity : previousReferenceSimpleActivitys) {
            if (!resolved.contains(previousReferenceSimpleActivity)) {
                deleteReferenceArcCommands.add(new DeleteReferenceArcCommand(previousReferenceSimpleActivity.getReferenceArc()));
            }
        }
        for (Element element : storedCompositeActivity.getElements()) {
            if (element instanceof ReferenceSimpleActivity) {
                ReferenceSimpleActivity referenceSimpleActivity = (ReferenceSimpleActivity) element;
                if (resolved.contains(referenceSimpleActivity)) {
                    newElements.add(referenceSimpleActivity);
                }
            } else {
                newElements.add(element);
            }
        }
        for (ReferenceSimpleActivity storedReferenceSimpleActivity : storedReferenceSimpleActivitys) {
            if (!resolved.contains(storedReferenceSimpleActivity)) {
                SimpleActivity simpleActivity = new SimpleActivity();
                petriNet.getNodeSimpleIdGenerator().setUniqueId(simpleActivity);
                simpleActivity.setCenter(storedReferenceSimpleActivity.getCenter().x, storedReferenceSimpleActivity.getCenter().y);
                for (ArcEdge arc : storedReferenceSimpleActivity.getConnectedArcEdges()) {
                    arc.setSimpleActivityNode(simpleActivity);
                }
                simpleActivity.setLabel("?!");
                newElements.add(simpleActivity);
            }
        }
        for (Element element : storedCompositeActivity.getElements()) { //TODO: clone
            element.setParentCompositeActivity(compositeActivity);
        }
        previousElements = compositeActivity.getElementsCopy();
        compositeActivity.setElements(newElements);
        for (Command deleteReferenceArcCommand : deleteReferenceArcCommands) {
            deleteReferenceArcCommand.execute();
        }
    }

    public void undo() {
        compositeActivity.setElements(previousElements);
        for (Command deleteReferenceArcCommand : deleteReferenceArcCommands) {
            deleteReferenceArcCommand.undo();
        }
    }

    public void redo() {
        compositeActivity.setElements(newElements);
        for (Command deleteReferenceArcCommand : deleteReferenceArcCommands) {
            deleteReferenceArcCommand.redo();
        }
    }

    private boolean weakEquivalentReferenceSimpleActivitys(ReferenceSimpleActivity referenceSimpleActivity1, ReferenceSimpleActivity referenceSimpleActivity2) {
        Set<Transition> resolved = new HashSet<Transition>();
        for (Transition transition1 : referenceSimpleActivity1.getConnectedTransitionsRecursively()) {
            for (Transition transition2 : referenceSimpleActivity2.getConnectedTransitionsRecursively()) {
                if (!resolved.contains(transition1) && !resolved.contains(transition2)) {
                    if (equivalentPlaceTransitionRelation(referenceSimpleActivity1, transition1, referenceSimpleActivity2, transition2)) {
                        resolved.add(transition1);
                        resolved.add(transition2);
                    }
                }
            }
        }
        if (resolved.containsAll(referenceSimpleActivity1.getConnectedTransitionsRecursively()) && resolved.containsAll(referenceSimpleActivity2.getConnectedTransitionsRecursively())) {
            return true;
        }
        return false;
    }

    private boolean strongEquivalentReferenceSimpleActivitys(ReferenceSimpleActivity referenceSimpleActivity1, ReferenceSimpleActivity referenceSimpleActivity2) {
        return weakEquivalentReferenceSimpleActivitys(referenceSimpleActivity1, referenceSimpleActivity2)
                && referenceSimpleActivity1.getConnectedTransitionNodes().containsAll(referenceSimpleActivity2.getConnectedTransitionNodes());
    }

    private boolean equivalentPlaceTransitionRelation(SimpleActivityNode simpleActivityNode1, Transition transition1, SimpleActivityNode simpleActivityNode2, Transition transition2) {
        Arc arc1pTt = simpleActivityNode1.getConnectedArc(transition1, true);
        Arc arc1tTp = simpleActivityNode1.getConnectedArc(transition1, false);
        Arc arc2pTt = simpleActivityNode2.getConnectedArc(transition2, true);
        Arc arc2tTp = simpleActivityNode2.getConnectedArc(transition2, false);
        return equivalentArcs(arc1pTt, arc2pTt) && equivalentArcs(arc1tTp, arc2tTp);
    }

    private boolean equivalentArcs(Arc arc1, Arc arc2) {
        if (arc1 == null && arc2 == null) {
            return true;
        } else if (arc1 == null || arc2 == null) {
            return false;
        } else if (arc1.getMultiplicity() == arc2.getMultiplicity()) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Replace Composite Activity";
    }

}
