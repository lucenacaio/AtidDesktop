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
package org.atid.editor;

import java.awt.Frame;
import java.io.File;
import java.util.Set;
import javax.swing.JPopupMenu;
import org.atid.petrinet.Document;
import org.atid.petrinet.Element;
import org.atid.petrinet.Marking;
import org.atid.petrinet.Role;
import org.atid.editor.canvas.Selection;
import org.atid.util.ListEditor;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public interface Root {

    public DrawingBoard getDrawingBoard();

    public UndoManager getUndoManager();

    public File getCurrentDirectory();

    public void setCurrentDirectory(File currentDirectory);

    public Frame getParentFrame();

    public Document getDocument();

    public void setDocument(Document document);

    public Element getClickedElement();

    public void setClickedElement(Element clickedElement);

    public Selection getSelection();

    public Set<Element> getSelectedElementsWithClickedElement();

    public void selectTool_Select();

    public boolean isSelectedTool_Select();

    public void selectTool_SimpleActivity();
    
    public void selectTool_Event();
    
    public boolean isSelectTool_Event();
    
    public void selectTool_Repository();
    
    public boolean isSelectedTool_Repository();

    public boolean isSelectedTool_SimpleActivity();

    public void selectTool_Transition();

    public boolean isSelectedTool_Transition();
    
    public void selectTool_CompositeActivity();

    public boolean isSelectedTool_CompositeActivity();

    public void selectTool_Arc();

    public boolean isSelectedTool_Arc();

    public void selectTool_Token();

    public boolean isSelectedTool_Token();

    public JPopupMenu getSimpleActivityPopup();

    public JPopupMenu getTransitionPopup();

    public JPopupMenu getArcEdgePopup();

    public JPopupMenu getSubnetPopup();

    public JPopupMenu getCanvasPopup();

    public void openCompositeActivity();

    public void closeCompositeActivity();

    public void refreshAll();

    public void repaintCanvas();

    public void quitApplication();

    public Marking getCurrentMarking();

    public void setCurrentMarking(Marking currentMarking);

    public LocalClipboard getClipboard();

    public boolean isModified();

    public void setModified(boolean isModified);

    public File getCurrentFile();

    public void setCurrentFile(File currentFile);

    public String getAppShortName();

    public String getAppLongName();
}
