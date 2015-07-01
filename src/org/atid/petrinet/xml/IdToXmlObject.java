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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class IdToXmlObject {

    private XmlDocument xmlDocument;

    public IdToXmlObject(XmlDocument xmlDocument) {
        this.xmlDocument = xmlDocument;
    }

    private Map<String, Object> map = new HashMap<String, Object>();

    public Object getXmlObject(String id) {
        if (id.equals(null)) {
            return null;
        }
        if (id.equals("")) {
            return null;
        }
        if (map.containsKey(id)) {
            return map.get(id);
        }
        Object xmlObject = getXmlObjectFromXmlCompositeActivity(id, xmlDocument.rootCompositeActivity);
        if (xmlObject != null) {
            map.put(id, xmlObject);
        }
        return xmlObject;
    }

 private Object getXmlObjectFromXmlCompositeActivity(String id, XmlCompositeActivity xmlCompositeActivity) {
        for (XmlSimpleActivity xmlSimpleActivity : xmlCompositeActivity.simpleActivitys) {
            if (xmlSimpleActivity.id.equals(id)) {
                return xmlSimpleActivity;
            }
        }
        for (XmlTransition xmlTransition : xmlCompositeActivity.transitions) {
            if (xmlTransition.id.equals(id)) {
                return xmlTransition;
            }
        }
        for (XmlRepository xmlRepository : xmlCompositeActivity.repositorys){
            if(xmlRepository.id.equals(id)) {
                return xmlRepository;
            }
        }
        for (XmlEvent xmlEvent : xmlCompositeActivity.events) {
            if(xmlEvent.id.equals(id)){
                return xmlEvent;
            }
        }
        for (XmlReferenceSimpleActivity xmlReference : xmlCompositeActivity.referenceSimpleActivitys) {
            if (xmlReference.id.equals(id)) {
                return xmlReference;
            }
        }
        for (XmlCompositeActivity xmlSubCompositeActivity : xmlCompositeActivity.compositeActivitys) {
            if (xmlSubCompositeActivity.id.equals(id)) {
                return xmlSubCompositeActivity;
            }
            Object xmlObject = getXmlObjectFromXmlCompositeActivity(id, xmlSubCompositeActivity);
            if (xmlObject != null) {
                return xmlObject;
            }
        }
        return null;
    }

}
