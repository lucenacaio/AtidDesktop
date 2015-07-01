/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.atid.petrinet.xml;

import javax.xml.bind.annotation.XmlElement;
/**
 *
 * @author caiolucena
 */
public class XmlRepository extends XmlNode{
    @XmlElement(name = "label")
    public String label;
}
