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
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.TransformerException;
import org.atid.petrinet.Arc;
import org.atid.petrinet.CompositeActivity;
import org.atid.petrinet.Document;
import org.atid.petrinet.Element;
import org.atid.petrinet.Event;
import org.atid.petrinet.Marking;
import org.atid.petrinet.SimpleActivity;
import org.atid.petrinet.ReferenceArc;
import org.atid.petrinet.ReferenceSimpleActivity;
import org.atid.petrinet.Repository;
import org.atid.petrinet.Role;
import org.atid.petrinet.Transition;
import org.atid.util.Xslt;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class DocumentExporter {

    private XmlDocument xmlDocument = new XmlDocument();

    public DocumentExporter(Document document, Marking marking) {

        marking.getLock().readLock().lock();
        try {

            xmlDocument.rootCompositeActivity = getXmlCompositeActivity(marking.getPetriNet().getRootCompositeActivity(), marking);
        } finally {
            marking.getLock().readLock().unlock();
        }

        Rectangle bounds = marking.getPetriNet().getRootCompositeActivity().getBoundsRecursively();
        xmlDocument.left = bounds.x;
        xmlDocument.top = bounds.y;
    }

    public void writeToFile(File file) throws FileNotFoundException, JAXBException {
        JAXBContext ctx = JAXBContext.newInstance(XmlDocument.class);
        Marshaller m = ctx.createMarshaller();
        m.setProperty("jaxb.formatted.output", true);
        m.marshal(xmlDocument, new FileOutputStream(file));
    }

    public void writeToFileWithXslt(File file, InputStream xslt) throws FileNotFoundException, JAXBException, IOException, TransformerException {
        if (xslt == null) {
            writeToFile(file);
            return;
        }
        JAXBContext ctx = JAXBContext.newInstance(XmlDocument.class);
        Marshaller m = ctx.createMarshaller();
        m.setProperty("jaxb.formatted.output", true);
        File tempFile = File.createTempFile("atid-export", null);
        m.marshal(xmlDocument, new FileOutputStream(tempFile));
        Xslt.transformXml(tempFile, xslt, file);
        tempFile.delete(); // delete temp file
    }

    private XmlCompositeActivity getXmlCompositeActivity(CompositeActivity compositeActivity, Marking initialMarking) {
        XmlCompositeActivity xmlCompositeActivity = new XmlCompositeActivity();
        xmlCompositeActivity.id = compositeActivity.getId();
        xmlCompositeActivity.label = compositeActivity.getLabel();
        xmlCompositeActivity.x = compositeActivity.getCenter().x;
        xmlCompositeActivity.y = compositeActivity.getCenter().y;
        for (Element element : compositeActivity.getElements()) {
            if (element instanceof CompositeActivity) {
                xmlCompositeActivity.compositeActivitys.add(getXmlCompositeActivity((CompositeActivity) element, initialMarking));
            } else if (element instanceof Transition) {
                xmlCompositeActivity.transitions.add(getXmlTransition((Transition) element));
            } else if (element instanceof ReferenceSimpleActivity) {
                xmlCompositeActivity.referenceSimpleActivitys.add(getXmlReferenceSimpleActivity((ReferenceSimpleActivity) element));
            } else if (element instanceof SimpleActivity) {
                xmlCompositeActivity.simpleActivitys.add(getXmlSimpleActivity((SimpleActivity) element, initialMarking));
            }else if(element instanceof Event){
                xmlCompositeActivity.events.add(getXmlEvent((Event) element));
            } else if(element instanceof Repository){
                xmlCompositeActivity.repositorys.add(getXmlRepository((Repository) element));
            } else if (element instanceof ReferenceArc) {
                xmlCompositeActivity.referenceArcs.add(getXmlReferenceArc((ReferenceArc) element));
            } else if (element instanceof Arc) {
                xmlCompositeActivity.arcs.add(getXmlArc((Arc) element));
            }
        }
        return xmlCompositeActivity;
    }

    private XmlSimpleActivity getXmlSimpleActivity(SimpleActivity simpleActivity, Marking initialMarking) {
        XmlSimpleActivity xmlSimpleActivity = new XmlSimpleActivity();
        xmlSimpleActivity.id = simpleActivity.getId();
        xmlSimpleActivity.x = simpleActivity.getCenter().x;
        xmlSimpleActivity.y = simpleActivity.getCenter().y;
        xmlSimpleActivity.isStatic = simpleActivity.isStatic();
        xmlSimpleActivity.label = simpleActivity.getLabel();
        xmlSimpleActivity.tokens = initialMarking.getTokens(simpleActivity);
        return xmlSimpleActivity;
    }
    
    private XmlEvent getXmlEvent(Event event){
        XmlEvent xmlEvent = new XmlEvent();
        xmlEvent.id = event.getId();
        xmlEvent.x = event.getCenter().x;
        xmlEvent.y = event.getCenter().y;
        xmlEvent.label = event.getLabel();
        return xmlEvent;
    }
    
    private XmlRepository getXmlRepository(Repository repository){
        XmlRepository xmlRepository = new XmlRepository();
        xmlRepository.id = repository.getId();
        xmlRepository.x = repository.getCenter().x;
        xmlRepository.y = repository.getCenter().y;
        xmlRepository.label = repository.getLabel();
        return xmlRepository;
    }

    private XmlTransition getXmlTransition(Transition transition) {
        XmlTransition xmlTransition = new XmlTransition();
        xmlTransition.id = transition.getId();
        xmlTransition.x = transition.getCenter().x;
        xmlTransition.y = transition.getCenter().y;
        xmlTransition.label = transition.getLabel();
        return xmlTransition;
    }

    private XmlArc getXmlArc(Arc arc) {
        XmlArc xmlArc = new XmlArc();
        xmlArc.type = arc.getType();
        xmlArc.sourceId = arc.getSource().getId();
        xmlArc.destinationId = arc.getDestination().getId();

        if (arc.getSource() instanceof ReferenceSimpleActivity) {
            ReferenceSimpleActivity referenceSimpleActivity = (ReferenceSimpleActivity) arc.getSource();
            xmlArc.realSourceId = referenceSimpleActivity.getConnectedSimpleActivity().getId();
        } else {
            xmlArc.realSourceId = xmlArc.sourceId;
        }
        if (arc.getDestination() instanceof ReferenceSimpleActivity) {
            ReferenceSimpleActivity referenceSimpleActivity = (ReferenceSimpleActivity) arc.getDestination();
            xmlArc.realDestinationId = referenceSimpleActivity.getConnectedSimpleActivity().getId();
        } else {
            xmlArc.realDestinationId = xmlArc.destinationId;
        }

        List<Point> breakPoints = arc.getBreakPoints();
        for (Point point : breakPoints) {
            XmlPoint xmlPoint = new XmlPoint();
            xmlPoint.x = point.x;
            xmlPoint.y = point.y;
            xmlArc.breakPoints.add(xmlPoint);
        }
        return xmlArc;
    }

    private XmlReferenceSimpleActivity getXmlReferenceSimpleActivity(ReferenceSimpleActivity referenceSimpleActivity) {
        XmlReferenceSimpleActivity xmlReferenceSimpleActivity = new XmlReferenceSimpleActivity();
        xmlReferenceSimpleActivity.id = referenceSimpleActivity.getId();
        xmlReferenceSimpleActivity.x = referenceSimpleActivity.getCenter().x;
        xmlReferenceSimpleActivity.y = referenceSimpleActivity.getCenter().y;
        xmlReferenceSimpleActivity.connectedSimpleActivityId = referenceSimpleActivity.getConnectedSimpleActivityNode().getId();
        return xmlReferenceSimpleActivity;
    }

    private XmlReferenceArc getXmlReferenceArc(ReferenceArc referenceArc) {
        XmlReferenceArc xmlReferenceArc = new XmlReferenceArc();
        xmlReferenceArc.simpleActivityId = referenceArc.getSimpleActivityNode().getId();
        xmlReferenceArc.compositeActivityId = referenceArc.getCompositeActivity().getId();

        List<Point> breakPoints = referenceArc.getBreakPointsCopy();
        if (!referenceArc.isSimpleActivityToTransition()) {
            Collections.reverse(breakPoints);
        }
        for (Point point : breakPoints) {
            XmlPoint xmlPoint = new XmlPoint();
            xmlPoint.x = point.x;
            xmlPoint.y = point.y;
            xmlReferenceArc.breakPoints.add(xmlPoint);
        }
        return xmlReferenceArc;
    }
}
