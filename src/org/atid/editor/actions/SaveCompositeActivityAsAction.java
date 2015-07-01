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

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.atid.editor.Root;
import org.atid.editor.filechooser.FileChooserDialog;
import org.atid.editor.filechooser.FileTypeException;
import org.atid.editor.filechooser.PflowxFileType;
import org.atid.petrinet.CompositeActivity;
import org.atid.petrinet.Document;
import org.atid.petrinet.PetriNet;
import org.atid.util.GraphicsTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class SaveCompositeActivityAsAction extends AbstractAction {

    private Root root;

    public SaveCompositeActivityAsAction(Root root) {
        this.root = root;
        String name = "Save Composite Activity as...";
        putValue(NAME, name);
        putValue(SHORT_DESCRIPTION, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("atid/savesubnetas.gif"));
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        if (root.getClickedElement() instanceof CompositeActivity) {
            CompositeActivity compositeActivity = (CompositeActivity) root.getClickedElement();

            FileChooserDialog chooser = new FileChooserDialog();

            String compositeActivityLabel = compositeActivity.getLabel();
            if (compositeActivityLabel != null && !compositeActivityLabel.equals("")) {
                chooser.setSelectedFile(new File(chooser.getCurrentDirectory().getAbsolutePath() + "/" + compositeActivityLabel));
            }

            chooser.addChoosableFileFilter(new PflowxFileType());
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setCurrentDirectory(root.getCurrentDirectory());
            chooser.setDialogTitle("Save composite Activity as...");

            if (chooser.showSaveDialog(root.getParentFrame()) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();

                if (!file.exists() || JOptionPane.showConfirmDialog(root.getParentFrame(), "Selected file exists. Overwrite?") == JOptionPane.YES_OPTION) {
                    try {
                        exportCompositeActivity(compositeActivity, file);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(root.getParentFrame(), ex.getMessage());
                    }
                }
            }
            root.setCurrentDirectory(chooser.getCurrentDirectory());
        }
    }

    private void exportCompositeActivity(CompositeActivity compositeActivity, File file) throws FileTypeException {
        Document document = new Document();
        PetriNet petriNet = document.petriNet;
        petriNet.setRootCompositeActivity(compositeActivity);
        new PflowxFileType().save(document, file);
    }
}
