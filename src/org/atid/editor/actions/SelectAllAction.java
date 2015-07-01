package org.atid.editor.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import org.atid.editor.Atid;
import org.atid.editor.canvas.Selection;
import org.atid.petrinet.Element;
import org.atid.petrinet.PetriNet;

/**
 *
 * @author matmas
 */
public class SelectAllAction extends AbstractAction {

    public SelectAllAction() {
        String name = "Select All";
        putValue(NAME, name);
        putValue(SHORT_DESCRIPTION, name);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl A"));
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PetriNet petriNet = Atid.getRoot().getDocument().getPetriNet();

        Selection selection = Atid.getRoot().getSelection();
        selection.clear();
        selection.addAll(petriNet.getCurrentCompositeActivity().getElements());

        Atid.getRoot().refreshAll();
    }

//	@Override
//	public boolean shouldBeEnabled() {
//		PetriNet petriNet = Atid.getRoot().getDocument().getPetriNet();
//		return !petriNet.isEmpty();
//	}
}
