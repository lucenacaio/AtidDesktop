package org.atid.editor.commands;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Set;
import org.atid.petrinet.CompositeActivity;
import org.atid.petrinet.Element;
import org.atid.petrinet.PetriNet;
import org.atid.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class PasteCommand implements Command {

    private CompositeActivity compositeActivity;
    private Set<Element> elements;

    public PasteCommand(Set<Element> elements, CompositeActivity currentCompositeActivity, PetriNet petriNet) {
        this.compositeActivity = currentCompositeActivity;
        this.elements = elements;
        petriNet.getNodeLabelGenerator().setLabelsToPastedContent(elements);

        Point translation = calculateTranslatioToCenter(elements, currentCompositeActivity);
        for (Element element : elements) {
            element.moveBy(translation.x, translation.y);
        }
    }

    public void execute() {
        compositeActivity.addAll(elements);
    }

    public void undo() {
        compositeActivity.removeAll(elements);
    }

    public void redo() {
        execute();
    }

    @Override
    public String toString() {
        return "Paste";
    }

    private Point calculateTranslatioToCenter(Set<Element> elements, CompositeActivity currentCompositeActivity) {
        Point viewTranslation = currentCompositeActivity.getViewTranslation();
        CompositeActivity tempCompositeActivity = new CompositeActivity();
        tempCompositeActivity.addAll(elements);
        Rectangle bounds = tempCompositeActivity.getBounds();

        Point result = new Point();
        result.translate(Math.round(-(float) bounds.getCenterX()), Math.round(-(float) bounds.getCenterY()));
        result.translate(-viewTranslation.x, -viewTranslation.y);
        return result;
    }

}
