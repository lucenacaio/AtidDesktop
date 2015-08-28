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
import java.util.Collections;
import java.util.Stack;

/**
 * PetriNet class stores reference to the root subnet and manages a view of
 * currently opened subnet in form of a stack. Default view is only the root
 * subnet opened. Opening and closing subnets does not influence anything other
 * and serves only for informational purposes.
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class PetriNet {

    private Stack<CompositeActivity> openedCompositeActivitys = new Stack<CompositeActivity>();
    private CompositeActivity rootCompositeActivity;
    private Marking initialMarking = new Marking(this);
    private NodeSimpleIdGenerator nodeSimpleIdGenerator = new NodeSimpleIdGenerator(this);
    private NodeLabelGenerator nodeLabelGenerator = new NodeLabelGenerator(this);

    /**
     * Constructor. Creates a new Petri net with empty root subnet.
     */
    public PetriNet() {
        clear();
    }
    
    private static SimpleActivityNode newBegin = null;
    private static SimpleActivityNode oldBegin = null;

    public static SimpleActivityNode getOldBegin() {
        return oldBegin;
    }

    public static void setOldBegin(SimpleActivityNode oldBegin) {
        PetriNet.oldBegin = oldBegin;
    }

    public static SimpleActivityNode getNewBegin() {
        return newBegin;
    }

    public static void setNewBegin(SimpleActivityNode begin) {
        PetriNet.newBegin = begin;
    }

    /**
     * Returns the root subnet of the Petri net. It is the only commonly used
     * method.
     *
     * @return the root subnet of the Petri net
     */
    public CompositeActivity getRootCompositeActivity() {
        return rootCompositeActivity;
    }

    /**
     * Replaces the root subnet with a different one and thus destroys old
     * reference. Currently useful only for DocumentImporter.
     *
     * @param rootSubnet a subnet to replace with
     */
    public void setRootCompositeActivity(CompositeActivity rootCompositeActivity) {
        this.rootCompositeActivity = rootCompositeActivity;
    }

    /**
     * Determines whether the are no opened subnets except the root subnet
     *
     * @return true if only root subnet is opened otherwise false
     */
    public boolean isCurrentCompositeActivityRoot() {
        return getCurrentCompositeActivity() == getRootCompositeActivity();
    }

    /**
     * Returns currenly opened subnet.
     *
     * @return currenly opened subnet
     */
    public CompositeActivity getCurrentCompositeActivity() {
        return openedCompositeActivitys.peek();
    }

    /**
     * Replaces the root subnet with empty one and resets view so that opened
     * subnet is the new root subnet.
     */
    public void clear() {
        rootCompositeActivity = new CompositeActivity();
        resetView();
    }

    /**
     * Resets view, so that currently opened subnet is root subnet.
     */
    public void resetView() {
        openedCompositeActivitys.clear();
        openedCompositeActivitys.add(rootCompositeActivity);
    }

    /**
     * Opens a subnet. Changes view, so that specified subnet is currently
     * opened. The specified subnet must be directly nested in currenly opened
     * subnet.
     *
     * @param subnet subnet to be opened
     */
    public void openCompositeActivity(CompositeActivity compositeActivity) {
        openedCompositeActivitys.push(compositeActivity);
    }

    /**
     * Closes currenly opened subnet, so that parent subnet becomes next opened.
     */
    public void closeCompositeActivity() {
        if (!isCurrentCompositeActivityRoot()) {
            openedCompositeActivitys.pop();
        }
    }

    /**
     * Returns a ordered collection of currently opened subnets, i.e. a path to
     * the currently opened subnet.
     *
     * @return collection of opened subnets
     */
    public Collection<CompositeActivity> getOpenedCompositeActivitys() {
        return Collections.unmodifiableCollection(openedCompositeActivitys);
    }

    public Marking getInitialMarking() {
        return initialMarking;
    }

    @Deprecated
    public void setInitialMarking(Marking initialMarking) {
        this.initialMarking = initialMarking;
    }

    public NodeSimpleIdGenerator getNodeSimpleIdGenerator() {
        return nodeSimpleIdGenerator;
    }

    public NodeLabelGenerator getNodeLabelGenerator() {
        return nodeLabelGenerator;
    }

    public boolean hasStaticSimpleActivity() {
        for (SimpleActivity simpleActivity : getRootCompositeActivity().getSimpleActivitysRecursively()) {
            if (simpleActivity.isStatic()) {
                return true;
            }
        }
        return false;
    }
}
