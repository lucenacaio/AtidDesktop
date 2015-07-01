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

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class NodeIdGenerator {

    private int nextUniqueSimpleActivityNumber = 1;
    private int nextUniqueTransitionNumber = 1;
    private int nextUniqueCompositeActivityNumber = 1;
    private int nextUniqueReferenceSimpleActivityNumber = 1;

    public void setUniqueId(Node node) {
        String id;
        if (node instanceof SimpleActivity) {
            id = "s" + Integer.toString(nextUniqueSimpleActivityNumber++);
        } else if (node instanceof Transition) {
            id = "t" + Integer.toString(nextUniqueTransitionNumber++);
        } else if (node instanceof CompositeActivity) {
            id = "c" + Integer.toString(nextUniqueCompositeActivityNumber++);
        } else if (node instanceof ReferenceSimpleActivity) {
            id = "rp" + Integer.toString(nextUniqueReferenceSimpleActivityNumber++);
        } else {
            throw new RuntimeException("Node which is not SimpleActivity, Transition, Composite Activity  neither ReferenceSimpleActivity");
        }
        node.setId(id);
    }

    public void fixFutureUniqueIds(CompositeActivity rootCompositeActivity) {
        int maxSimpleActivityNumber = 0;
        int maxTransitionNumber = 0;
        int maxCompositeActivityNumber = 0;
        int maxReferenceSimpleActivityNumber = 0;

        for (SimpleActivity simpleActivity : rootCompositeActivity.getSimpleActivitysRecursively()) {
            String simpleActivityLabel = simpleActivity.getLabel();
            if (simpleActivityLabel.startsWith("s")) {
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
        for (Transition transition : rootCompositeActivity.getTransitionsRecursively()) {
            String transitionLabel = transition.getLabel();
            if (transitionLabel.startsWith("t")) {
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
        for (CompositeActivity compositeActivity : rootCompositeActivity.getCompositeActivitysRecursively()) {
            String compositeActivityLabel = compositeActivity.getLabel();
            if (compositeActivityLabel.startsWith("c")) {
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
        for (ReferenceSimpleActivity referenceSimpleActivity : rootCompositeActivity.getReferenceSimpleActivitysRecursively()) {
            String referenceSimpleActivityLabel = referenceSimpleActivity.getLabel();
            if (referenceSimpleActivityLabel.startsWith("rp")) {
                try {
                    int referenceSimpleActivityNumber = Integer.parseInt(referenceSimpleActivityLabel.substring(1));
                    if (referenceSimpleActivityNumber > maxReferenceSimpleActivityNumber) {
                        maxReferenceSimpleActivityNumber = referenceSimpleActivityNumber;
                    }
                } catch (NumberFormatException ex) {
                    //do nothing
                }
            }
        }
        nextUniqueSimpleActivityNumber = maxSimpleActivityNumber + 1;
        nextUniqueTransitionNumber = maxTransitionNumber + 1;
        nextUniqueCompositeActivityNumber = maxCompositeActivityNumber + 1;
        nextUniqueReferenceSimpleActivityNumber = maxReferenceSimpleActivityNumber + 1;
    }

}
