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

import java.util.HashMap;
import java.util.Map;
import org.atid.petrinet.Arc;
import org.atid.petrinet.CompositeActivity;
import org.atid.petrinet.PetriNet;
import org.atid.petrinet.SimpleActivity;
import org.atid.petrinet.ReferenceSimpleActivity;
import org.atid.util.CollectionTools;
import org.atid.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class ReplaceCompositeActivityLooseMethodCommand implements Command {

    private CompositeActivity compositeActivity;
    private CompositeActivity storedCompositeActivity;
    private PetriNet petriNet;
    private Command replaceCompositeActivity;

    public ReplaceCompositeActivityLooseMethodCommand(CompositeActivity compositeActivity, CompositeActivity storedCompositeActivity, PetriNet petriNet) {
        this.compositeActivity = compositeActivity;
        this.storedCompositeActivity = storedCompositeActivity;
        this.petriNet = petriNet;
        replaceCompositeActivity = new ReplaceCompositeActivityCommand(compositeActivity, storedCompositeActivity, petriNet);
    }

    public void execute() {
        setArcMultiplicitiesToOne(compositeActivity);
        replaceCompositeActivity.execute();
        restoreArcMultiplicities(compositeActivity);
    }

    public void undo() {
        setArcMultiplicitiesToOne(compositeActivity);
        replaceCompositeActivity.undo();
        restoreArcMultiplicities(compositeActivity);
    }

    public void redo() {
        setArcMultiplicitiesToOne(compositeActivity);
        replaceCompositeActivity.redo();
        restoreArcMultiplicities(compositeActivity);
    }

    private Map<SimpleActivity, Integer> outFlows = new HashMap<SimpleActivity, Integer>();
    private Map<SimpleActivity, Integer> inFlows = new HashMap<SimpleActivity, Integer>();

    private void setArcMultiplicitiesToOne(CompositeActivity compositeActivity) {
        outFlows.clear();
        inFlows.clear();
        for (ReferenceSimpleActivity referenceSimpleActivity : compositeActivity.getReferenceSimpleActivitys()) {
            Arc outArc = CollectionTools.getFirstElement(referenceSimpleActivity.getConnectedArcs(true));
            if (outArc != null) {
                int outFlow = outArc.getMultiplicity();
                outFlows.put(referenceSimpleActivity.getConnectedSimpleActivity(), outFlow);
            }
            for (Arc arc : referenceSimpleActivity.getConnectedArcs(true)) {
                arc.setMultiplicity(1);
            }
            Arc inArc = CollectionTools.getFirstElement(referenceSimpleActivity.getConnectedArcs(false));
            if (inArc != null) {
                int inFlow = inArc.getMultiplicity();
                inFlows.put(referenceSimpleActivity.getConnectedSimpleActivity(), inFlow);
            }
            for (Arc arc : referenceSimpleActivity.getConnectedArcs(false)) {
                arc.setMultiplicity(1);
            }
        }
    }

    private void restoreArcMultiplicities(CompositeActivity compositeActivity) {
        for (ReferenceSimpleActivity referenceSimpleActivity : compositeActivity.getReferenceSimpleActivitys()) {
            SimpleActivity simpleActivity = referenceSimpleActivity.getConnectedSimpleActivity();
            if (simpleActivity != null) {
                for (Arc arc : referenceSimpleActivity.getConnectedArcs(true)) {
                    Integer multiplicity = outFlows.get(simpleActivity);
                    if (multiplicity == null) {
                        multiplicity = inFlows.get(simpleActivity);
                    }
                    arc.setMultiplicity(multiplicity);
                }
                for (Arc arc : referenceSimpleActivity.getConnectedArcs(false)) {
                    Integer multiplicity = inFlows.get(simpleActivity);
                    if (multiplicity == null) {
                        multiplicity = outFlows.get(simpleActivity);
                    }
                    arc.setMultiplicity(multiplicity);
                }
            }
        }
    }
}
