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
package org.atid.petrinet.xml;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.TransformerException;
import org.atid.petrinet.Arc;
import org.atid.petrinet.CompositeActivity;
import org.atid.petrinet.Document;
import org.atid.petrinet.Event;
import org.atid.petrinet.Marking;
import org.atid.petrinet.Node;
import org.atid.petrinet.SimpleActivity;
import org.atid.petrinet.SimpleActivityNode;
import org.atid.petrinet.ReferenceArc;
import org.atid.petrinet.ReferenceSimpleActivity;
import org.atid.petrinet.Repository;
import org.atid.petrinet.Transition;
import org.atid.util.Xslt;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class DocumentImporter {

    private XmlDocument xmlDocument;
    private IdToXmlObject idToXmlObject;

    public Document readFromFile(File file) throws JAXBException, FileNotFoundException, IOException {
        JAXBContext ctx = JAXBContext.newInstance(XmlDocument.class);
        Unmarshaller u = ctx.createUnmarshaller();
        FileInputStream fileInputStream = new FileInputStream(file);
        xmlDocument = (XmlDocument) u.unmarshal(fileInputStream);
        fileInputStream.close();
        idToXmlObject = new IdToXmlObject(xmlDocument);
        return getDocument();
    }

    public Document readFromFileWithXslt(File file, InputStream xslt) throws JAXBException, IOException, TransformerException {
        if (xslt == null) {
            return readFromFile(file);
        }
        JAXBContext ctx = JAXBContext.newInstance(XmlDocument.class);
        Unmarshaller u = ctx.createUnmarshaller();
        File transformedXml = Xslt.transformXml(file, xslt, File.createTempFile("atid-import", null));
        xmlDocument = (XmlDocument) u.unmarshal(transformedXml);
        idToXmlObject = new IdToXmlObject(xmlDocument);
        transformedXml.delete(); //delete temp file
        return getDocument();
    }

    private Document getDocument() {
        Document document = new Document();
        CompositeActivity rootCompositeActivity = getNewCompositeActivity(xmlDocument.rootCompositeActivity);
        document.petriNet.setRootCompositeActivity(rootCompositeActivity);
        constructInitialMarkingRecursively(document.petriNet.getInitialMarking(), xmlDocument.rootCompositeActivity);

        document.petriNet.getNodeSimpleIdGenerator().fixFutureUniqueIds();
        document.petriNet.getNodeSimpleIdGenerator().ensureNumberIds();
        document.petriNet.getNodeLabelGenerator().fixFutureUniqueLabels();

        return document;
    }

    private Object getObjectFromId(String id) {
        return getObject(idToXmlObject.getXmlObject(id));
    }

    private Map<Object, Object> cachedObjects = new HashMap<Object, Object>();

    private Object getObject(Object xmlObject) {
       if (cachedObjects.containsKey(xmlObject)) {
            return cachedObjects.get(xmlObject);
        }
        Object object = null;
        if (xmlObject instanceof XmlArc) {
            object = getNewArc((XmlArc) xmlObject);
        }
        if (xmlObject instanceof XmlSimpleActivity) {
            object = getNewSimpleActivity((XmlSimpleActivity) xmlObject);
        }
        if (xmlObject instanceof XmlTransition) {
            object = getNewTransition((XmlTransition) xmlObject);
        }
        if (xmlObject instanceof XmlReferenceSimpleActivity) {
            object = getNewReferenceSimpleActivity((XmlReferenceSimpleActivity) xmlObject);
        }
        if (xmlObject instanceof XmlCompositeActivity) {
            object = getNewCompositeActivity((XmlCompositeActivity) xmlObject);
        }
        if (xmlObject instanceof XmlReferenceArc) {
            object = getNewReferenceArc((XmlReferenceArc) xmlObject);
        }
        if (xmlObject instanceof XmlEvent) {
            object = getNewEvent((XmlEvent) xmlObject);
        }
        if (xmlObject instanceof XmlRepository) {
            object = getNewRepository((XmlRepository) xmlObject);
        }

        if (object != null) {
            cachedObjects.put(xmlObject, object);
        }
        return object;
    }

    private CompositeActivity getNewCompositeActivity(XmlCompositeActivity xmlCompositeActivity) {
        CompositeActivity compositeActivity = new CompositeActivity();
        compositeActivity.setId(xmlCompositeActivity.id);
        compositeActivity.setLabel(xmlCompositeActivity.label);
        compositeActivity.setCenter(xmlCompositeActivity.x, xmlCompositeActivity.y);
        for (XmlArc xmlArc : xmlCompositeActivity.arcs) {
            compositeActivity.addElement((Arc) getObject(xmlArc));
        }
        for (XmlSimpleActivity xmlSimpleActivity : xmlCompositeActivity.simpleActivitys) {
            compositeActivity.addElement((SimpleActivity) getObject(xmlSimpleActivity));
        }
        for (XmlTransition xmlTransition : xmlCompositeActivity.transitions) {
            compositeActivity.addElement((Transition) getObject(xmlTransition));
        }
        for (XmlEvent xmlEvent : xmlCompositeActivity.events) {
            compositeActivity.addElement((Event) getObject(xmlEvent));
        }
        for (XmlRepository xmlRepository : xmlCompositeActivity.repositorys) {
            compositeActivity.addElement((Repository) getObject(xmlRepository));
        }
        for (XmlReferenceSimpleActivity xmlReferenceSimpleActivity : xmlCompositeActivity.referenceSimpleActivitys) {
            compositeActivity.addElement((ReferenceSimpleActivity) getObject(xmlReferenceSimpleActivity));
        }
        for (XmlReferenceArc xmlReferenceArc : xmlCompositeActivity.referenceArcs) {
            compositeActivity.addElement((ReferenceArc) getObject(xmlReferenceArc));
        }
        for (XmlCompositeActivity xmlSubCompositeActivity : xmlCompositeActivity.compositeActivitys) {
            compositeActivity.addElement((CompositeActivity) getObject(xmlSubCompositeActivity));
        }
        return compositeActivity;
    }

    private void constructInitialMarkingRecursively(Marking marking, XmlCompositeActivity xmlCompositeActivity) {
        for (XmlSimpleActivity xmlSimpleActivity : xmlCompositeActivity.simpleActivitys) {
            marking.setTokens((SimpleActivityNode) getObject(xmlSimpleActivity), xmlSimpleActivity.tokens);
        }
        for (XmlCompositeActivity xmlSubCompositeActivity : xmlCompositeActivity.compositeActivitys) {
            constructInitialMarkingRecursively(marking, xmlSubCompositeActivity);
        }
    }

    private Arc getNewArc(XmlArc xmlArc) {
        Node source = (Node) getObjectFromId(xmlArc.sourceId);
        Node destination = (Node) getObjectFromId(xmlArc.destinationId);
        Arc arc = new Arc(source, destination);
        arc.setType(xmlArc.type);
        List<Point> breakPoints = new LinkedList<Point>();
        for (XmlPoint xmlPoint : xmlArc.breakPoints) {
            breakPoints.add(new Point(xmlPoint.x, xmlPoint.y));
        }
        arc.setBreakPoints(breakPoints);
        return arc;
    }

    private SimpleActivity getNewSimpleActivity(XmlSimpleActivity xmlSimpleActivity) {
        SimpleActivity simpleActivity = new SimpleActivity();
        simpleActivity.setId(xmlSimpleActivity.id);
        simpleActivity.setLabel(xmlSimpleActivity.label);
        simpleActivity.setStatic(xmlSimpleActivity.isStatic);
        simpleActivity.setCenter(xmlSimpleActivity.x, xmlSimpleActivity.y);
        return simpleActivity;
    }

    private Transition getNewTransition(XmlTransition xmlTransition) {
        Transition transition = new Transition();
        transition.setId(xmlTransition.id);
        transition.setLabel(xmlTransition.label);
        transition.setCenter(xmlTransition.x, xmlTransition.y);
        return transition;
    }

    private ReferenceSimpleActivity getNewReferenceSimpleActivity(XmlReferenceSimpleActivity xmlReference) {
        SimpleActivityNode connectedSimpleActivityNode = (SimpleActivityNode) getObjectFromId(xmlReference.connectedSimpleActivityId);
        ReferenceSimpleActivity referenceSimpleActivity = new ReferenceSimpleActivity(connectedSimpleActivityNode);
        referenceSimpleActivity.setId(xmlReference.id);
        referenceSimpleActivity.setCenter(xmlReference.x, xmlReference.y);
        return referenceSimpleActivity;
    }

    private ReferenceArc getNewReferenceArc(XmlReferenceArc xmlReferenceArc) {
        SimpleActivityNode simpleActivityNode = (SimpleActivityNode) getObjectFromId(xmlReferenceArc.simpleActivityId);
        CompositeActivity compositeActivity = (CompositeActivity) getObjectFromId(xmlReferenceArc.compositeActivityId);
        ReferenceArc referenceArc = new ReferenceArc(simpleActivityNode, compositeActivity);

        List<Point> breakPoints = new LinkedList<Point>();
        for (XmlPoint xmlPoint : xmlReferenceArc.breakPoints) {
            breakPoints.add(new Point(xmlPoint.x, xmlPoint.y));
        }
        referenceArc.setBreakPoints(breakPoints);
        return referenceArc;
    }

        private Repository getNewRepository(XmlRepository xmlRepository) {
        Repository repository = new Repository();
        repository.setId(xmlRepository.id);
        repository.setLabel(xmlRepository.label);
        repository.setCenter(xmlRepository.x, xmlRepository.y);
        return repository;
    }
        
    private Event getNewEvent(XmlEvent xmlEvent) {
        Event event = new Event();
        event.setId(xmlEvent.id);
        event.setLabel(xmlEvent.label);
        event.setCenter(xmlEvent.x, xmlEvent.y);
        return event;
    }
    
}
