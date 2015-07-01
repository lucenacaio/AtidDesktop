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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class ElementCloner {

    private PetriNet petriNet;
    private HashMap<Element, Element> clonedElements = new HashMap<Element, Element>();

    public ElementCloner(PetriNet petriNet) {
        this.petriNet = petriNet;
    }

    public static <E extends Element> Set<E> getClones(Collection<E> elements, PetriNet petriNet) {
        Set<E> clones = new HashSet<E>();
        ElementCloner elementCloner = new ElementCloner(petriNet);
        for (E element : elements) {
            clones.add(elementCloner.getClone(element));
        }
        return clones;
    }

    public static <E extends Element> E getClone(E element, PetriNet petriNet) {
        ElementCloner elementCloner = new ElementCloner(petriNet);
        return elementCloner.getClone(element);
    }

    @SuppressWarnings(value = "unchecked")
    private <E extends Element> E getClone(E element) {
       if (clonedElements.containsKey(element)) {
            return (E) clonedElements.get(element);
        }
        Element clone;
        if (element instanceof SimpleActivity) {
            clone = cloneSimpleActivity((SimpleActivity) element);
        } else if (element instanceof Transition) {
            clone = cloneTransition((Transition) element);
        } else if (element instanceof Arc) {
            clone = cloneArc((Arc) element);
        } else if (element instanceof CompositeActivity) {
            clone = cloneCompositeActivity((CompositeActivity) element);
        } else if (element instanceof Repository)  {
            clone = cloneRepository((Repository) element);
        } else if (element instanceof Event) {
            clone = cloneEvent((Event) element);
        } else if (element instanceof ReferenceSimpleActivity) {
            clone = cloneReferenceSimpleActivity((ReferenceSimpleActivity) element);
        } else if (element instanceof ReferenceArc) {
            clone = cloneReferenceArc((ReferenceArc) element);
        } else if (element == null) {
            return null;
        } else {
            throw new RuntimeException("unknown Element");
        }
        clonedElements.put((Element) element, clone);
        return (E) clone;
    }

    private SimpleActivity cloneSimpleActivity(SimpleActivity simpleActivity) {
        SimpleActivity newSimpleActivity = new SimpleActivity();
        newSimpleActivity.setCenter(simpleActivity.getCenter());
        petriNet.getNodeSimpleIdGenerator().setUniqueId(newSimpleActivity);
        newSimpleActivity.setLabel(simpleActivity.getLabel());
        newSimpleActivity.setStatic(simpleActivity.isStatic());
        petriNet.getInitialMarking().setTokens(newSimpleActivity, petriNet.getInitialMarking().getTokens(simpleActivity));
        return newSimpleActivity;
    }

    private Transition cloneTransition(Transition transition) {
        Transition newTransition = new Transition();
        newTransition.setCenter(transition.getCenter());
        petriNet.getNodeSimpleIdGenerator().setUniqueId(newTransition);
        newTransition.setLabel(transition.getLabel());
        return newTransition;
    }

    private Arc cloneArc(Arc arc) {
               Arc newArc = null;
        if(arc.getArcType().equals("simpleActivityToTransition")){
            newArc = new Arc(
                getClone(arc.getSimpleActivityNode(arc.getArcType())),
                getClone(arc.getTransition(arc.getArcType())),
                true
            );
        }else if(arc.getArcType().equals("transitionToSimpleActivity")){
            newArc = new Arc(
                getClone(arc.getTransition(arc.getArcType())),
                getClone(arc.getSimpleActivityNode(arc.getArcType())),
                true
            );
        }else if(arc.getArcType().equals("compositeActivityToTransition")){
            newArc = new Arc(
                getClone(arc.getCompositeActivityNode(arc.getArcType())),
                getClone(arc.getTransition(arc.getArcType())),
                true
            );
        }else if(arc.getArcType().equals("transitionToCompositeActivity")){
            newArc = new Arc(
                getClone(arc.getTransition(arc.getArcType())),
                getClone(arc.getCompositeActivityNode(arc.getArcType())),
                true
            );
        }else if(arc.getArcType().equals("eventToTransition")){
            newArc = new Arc(
                getClone(arc.getEvent(arc.getArcType())),
                getClone(arc.getTransition(arc.getArcType())),
                true
            );
        }else if(arc.getArcType().equals("simpleActivityToRepository")){
            newArc = new Arc(
                getClone(arc.getSimpleActivityNode(arc.getArcType())),
                getClone(arc.getRepositoryNode(arc.getArcType())),
                true
            );
        }else if(arc.getArcType().equals("repositoryToSimpleActivity")){
            newArc = new Arc(
                getClone(arc.getRepositoryNode(arc.getArcType())),
                getClone(arc.getSimpleActivityNode(arc.getArcType())),
                true
            );
        }
        newArc.setBreakPoints(arc.getBreakPoints());
        newArc.setMultiplicity(arc.getMultiplicity());
        newArc.setType(arc.getType());
        return newArc;
    }
    
    

    private ReferenceSimpleActivity cloneReferenceSimpleActivity(ReferenceSimpleActivity referenceSimpleActivity) {
        ReferenceSimpleActivity newReferenceSimpleActivity = new ReferenceSimpleActivity(
                getClone(referenceSimpleActivity.getConnectedSimpleActivityNode())
        );
        petriNet.getNodeSimpleIdGenerator().setUniqueId(newReferenceSimpleActivity);
        newReferenceSimpleActivity.setCenter(referenceSimpleActivity.getCenter());
        return newReferenceSimpleActivity;
    }

    private ReferenceArc cloneReferenceArc(ReferenceArc referenceArc) {
        ReferenceArc newReferenceArc = new ReferenceArc(
                getClone(referenceArc.getSimpleActivityNode()),
                getClone(referenceArc.getCompositeActivity())
        );
        newReferenceArc.setBreakPoints(referenceArc.getBreakPoints());
        return newReferenceArc;
    }

    private CompositeActivity cloneCompositeActivity(CompositeActivity compositeActivity) {
        CompositeActivity newCompositeActivity = new CompositeActivity();
        newCompositeActivity.setCenter(compositeActivity.getCenter());
        petriNet.getNodeSimpleIdGenerator().setUniqueId(newCompositeActivity);
        newCompositeActivity.setLabel(compositeActivity.getLabel());
        for (Element element : compositeActivity.getElements()) {
            newCompositeActivity.addElement(getClone(element));
        }
        return newCompositeActivity;
    }
    
    private Repository cloneRepository(Repository repository) {
        Repository newRepository = new Repository();
        newRepository.setCenter(repository.getCenter());
        petriNet.getNodeSimpleIdGenerator().setUniqueId(newRepository);
        newRepository.setLabel(repository.getLabel());
        return newRepository;
    }

    private Event cloneEvent(Event event) {
        Event newEvent = new Event();
        newEvent.setCenter(event.getCenter());
        petriNet.getNodeSimpleIdGenerator().setUniqueId(newEvent);
        newEvent.setLabel(event.getLabel());
        return newEvent;
    }
}
