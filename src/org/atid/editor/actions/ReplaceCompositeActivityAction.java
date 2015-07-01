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
package org.atid.editor.actions;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.atid.editor.Root;
import org.atid.editor.commands.ReplaceCompositeActivitysCommand;
import org.atid.editor.commands.ReplaceCompositeActivitysLooseMethodCommand;
import org.atid.editor.filechooser.FileChooserDialog;
import org.atid.editor.filechooser.FileTypeException;
import org.atid.editor.filechooser.PflowxFileType;
import org.atid.petrinet.CompositeActivity;
import org.atid.petrinet.Document;
import org.atid.petrinet.Element;
import org.atid.petrinet.PetriNet;
import org.atid.util.GraphicsTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class ReplaceCompositeActivityAction extends AbstractAction {

    private Root root;

    public ReplaceCompositeActivityAction(Root root) {
        this.root = root;
        String name = "Replace Composite Activity(s)...";
        putValue(NAME, name);
        putValue(SHORT_DESCRIPTION, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("atid/replacesubnet.gif"));
        setEnabled(false);
    }

    private JCheckBox looseMethodCheckBox = new JCheckBox("Use loose method");

    public void actionPerformed(ActionEvent e) {
        if (root.getClickedElement() instanceof CompositeActivity || !root.getSelection().getCompositeActivitys().isEmpty()) {
            FileChooserDialog chooser = new FileChooserDialog();
            chooser.addChoosableFileFilter(new PflowxFileType());
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setCurrentDirectory(root.getCurrentDirectory());
            chooser.setDialogTitle("Choose Composite Activity");

            chooser.getSidebar().add(looseMethodCheckBox, BorderLayout.SOUTH);

            if (chooser.showDialog(root.getParentFrame(), "Choose") == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try {
                    CompositeActivity storedCompositeActivity = importCompositeActivity(file);

                    Set<CompositeActivity> selectedCompositeActivitys = new HashSet<CompositeActivity>();
                    for (Element element : root.getSelectedElementsWithClickedElement()) {
                        if (element instanceof CompositeActivity) {
                            selectedCompositeActivitys.add((CompositeActivity) element);
                        }
                    }

                    Set<CompositeActivity> previousCompositeActivitys = root.getDocument().petriNet.getCurrentCompositeActivity().getCompositeActivitys();

                    PetriNet petriNet = root.getDocument().petriNet;

                    if (looseMethodCheckBox.isSelected()) {
                        root.getUndoManager().executeCommand(new ReplaceCompositeActivitysLooseMethodCommand(selectedCompositeActivitys, storedCompositeActivity, petriNet));
                    } else {
                        root.getUndoManager().executeCommand(new ReplaceCompositeActivitysCommand(selectedCompositeActivitys, storedCompositeActivity, petriNet));
                    }

                    Set<CompositeActivity> createdElements = root.getDocument().petriNet.getCurrentCompositeActivity().getCompositeActivitys();
                    createdElements.removeAll(previousCompositeActivitys);

                    root.getSelection().clear();
                    root.getSelection().getElements().addAll(createdElements);
                    root.getSelection().selectionChanged();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(root.getParentFrame(), ex.getMessage());
                }
            }
            root.setCurrentDirectory(chooser.getCurrentDirectory());
        }
    }

    private CompositeActivity importCompositeActivity(File file) throws FileTypeException {
        CompositeActivity compositeActivity = null;
        Document document = new PflowxFileType().load(file);
        compositeActivity = document.petriNet.getRootCompositeActivity();
        return compositeActivity;
    }
}
