/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.atid.petrinet;

/**
 *
 * @author Atylla
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.atid.util.CachedGraphics2D;

/**
 * Represents a subnet or net in Petri net
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class CompositeActivity extends SimpleActivityNode {

    private List<Element> elements = new LinkedList<Element>();
    private Point viewTranslation = new Point(0, 0);
     
    public void writeToFile(File file) throws FileNotFoundException, IOException {
        FileOutputStream fileOut = new FileOutputStream(file);
        ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
        objOut.writeObject(this);
        fileOut.close();
    }

    @SuppressWarnings("unchecked")
    public static CompositeActivity readFromFile(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
        CompositeActivity storedCompositeActivity = null;
        FileInputStream fileIn = new FileInputStream(file);
        ObjectInputStream objIn = new ObjectInputStream(fileIn);
        storedCompositeActivity = (CompositeActivity) objIn.readObject();
        fileIn.close();
        return storedCompositeActivity;
    }

    public Point getViewTranslation() {
        return new Point(viewTranslation);
    }

    public void setViewTranslation(Point viewTranslation) {
        this.viewTranslation = new Point(viewTranslation);
    }

    public void setViewTranslationToCenter() {
        int centerX = Math.round((float) getBounds().getCenterX());
        int centerY = Math.round((float) getBounds().getCenterY());
        Point center = new Point(-centerX, -centerY);
        setViewTranslation(center);
    }

    public void setViewTranslationToCenterRecursively() {
        setViewTranslationToCenter();
        for (CompositeActivity compositeActivity : getCompositeActivitysRecursively()) {
            compositeActivity.setViewTranslationToCenter();
        }
    }

    public List<Element> getElements() {
        return Collections.unmodifiableList(elements);
    }

    public List<Element> getElementsCopy() {
        return new LinkedList<Element>(elements);
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
        for (Element element : elements) {
            element.setParentCompositeActivity(this);
        }
    }

    public Element getElementByXY(int x, int y) {
        for (int i = elements.size() - 1; i >= 0; i--) { // Check elements from front to back.
            Element element = (Element) elements.get(i);
            if (element.containsPoint(x, y)) {
                return element;
            }
        }
        return null;
    }

    public Set<CompositeActivity> getCompositeActivitys() {
        Set<CompositeActivity> compositeActivitys = new HashSet<CompositeActivity>();
        for (Element element : getElements()) {
            if (element instanceof CompositeActivity) {
                compositeActivitys.add((CompositeActivity) element);
            }
        }
        return compositeActivitys;
    }

    public void addElement(Element element) {
        element.setParentCompositeActivity(this);
        if (element instanceof ReferenceArc || element instanceof ArcEdge) {
            elements.add(0, element); //background
        } else {
            elements.add(element);
        }
    }

    public void removeElement(Element element) {
        elements.remove(element);
    }

    public void removeElements() {
        elements.clear();
    }

    public void addAll(Set<Element> elements) {
        for (Element element : elements) {
            addElement(element);
        }
    }

    public void removeAll(Set<Element> elements) {
        for (Element element : elements) {
            removeElement(element);
        }
    }

    public Set<ArcEdge> getArcEdges() {
        Set<ArcEdge> arcs = new HashSet<ArcEdge>();
        for (Element element : elements) {
            if (element instanceof ArcEdge) {
                ArcEdge arc = (ArcEdge) element;
                arcs.add(arc);
            }
        }
        return arcs;
    }

    public Set<Arc> getArcs() {
        Set<Arc> arcs = new HashSet<Arc>();
        for (Element element : elements) {
            if (element instanceof Arc) {
                Arc arc = (Arc) element;
                arcs.add(arc);
            }
        }
        return arcs;
    }

    public Set<ReferenceArc> getReferenceArcs() {
        Set<ReferenceArc> referenceArcs = new HashSet<ReferenceArc>();
        for (Element element : elements) {
            if (element instanceof ReferenceArc) {
                ReferenceArc referenceArc = (ReferenceArc) element;
                referenceArcs.add(referenceArc);
            }
        }
        return referenceArcs;
    }

    public ArcEdge getArcEdge(SimpleActivityNode simpleActivityNode, TransitionNode transitionNode, boolean simpleActivityToTransition) {
        for (Element element : getElements()) {
            if (element instanceof ArcEdge) {
                ArcEdge arcEdge = (ArcEdge) element;
                if(!arcEdge.eventToTransition() && !arcEdge.isRepositoryToSimpleActivity() && !arcEdge.isSimpleActivityToRepository()){
                    if (arcEdge.getSimpleActivityNode() == simpleActivityNode && arcEdge.getTransitionNode() == transitionNode && arcEdge.isSimpleActivityToTransition() == simpleActivityToTransition) {
                        return arcEdge;
                    }
                }
            }
        }
        return null;
    }
    
    public ArcEdge getArcEdge(SimpleActivityNode simpleActivityNode, RepositoryNode repositoryNode,boolean repositoryToSimpleActivity){
        for(Element element : getElements()){
            if(element instanceof ArcEdge){
                ArcEdge arcEdge = (ArcEdge) element;
                if(arcEdge.getRepositoryNode() == repositoryNode && arcEdge.getSimpleActivityNode() == simpleActivityNode && arcEdge.isRepositoryToSimpleActivity() == repositoryToSimpleActivity){
                    return arcEdge;
                }
            }
        }
        return null;
    }
    
    public ArcEdge getArcEdge(EventNode eventNode, TransitionNode transitionNode, boolean eventToTransition){
        for (Element element : getElements()) {
            if (element instanceof ArcEdge) {
                ArcEdge arcEdge = (ArcEdge) element;
                if(arcEdge.eventToTransition())
                if (arcEdge.getEventNode() == eventNode && arcEdge.getTransitionNode() == transitionNode && arcEdge.eventToTransition() == eventToTransition) {
                    return arcEdge;
                }
            }
        }
        return null;        
    }

    public Set<Transition> getTransitions() {
        Set<Transition> transitions = new HashSet<Transition>();
        for (Element element : getElements()) {
            if (element instanceof Transition) {
                transitions.add((Transition) element);
            }
        }
        return transitions;
    }

    public Set<SimpleActivity> getSimpleActivitys() {
        Set<SimpleActivity> simpleActivitys = new HashSet<SimpleActivity>();
        for (Element element : getElements()) {
            if (element instanceof SimpleActivity) {
                simpleActivitys.add((SimpleActivity) element);
            }
        }
        return simpleActivitys;
    }

    public Set<Node> getNodes() {
        Set<Node> nodes = new HashSet<Node>();
        for (Element element : getElements()) {
            if (element instanceof Node) {
                nodes.add((Node) element);
            }
        }
        return nodes;
    }

    public Set<Node> getNodesRecursively() {
        Set<Node> nodes = new HashSet<Node>();
        for (Element element : elements) {
            if (element instanceof CompositeActivity) {
                CompositeActivity compositeActivity = (CompositeActivity) element;
                nodes.addAll(compositeActivity.getNodesRecursively());
            }
            if (element instanceof Node) {
                Node node = (Node) element;
                nodes.add(node);
            }
        }
        return nodes;
    }

    public Set<SimpleActivity> getSimpleActivitysRecursively() {
        Set<SimpleActivity> simpleActivitys = new HashSet<SimpleActivity>();
        for (Element element : elements) {
            if (element instanceof CompositeActivity) {
                CompositeActivity compositeActivity = (CompositeActivity) element;
                simpleActivitys.addAll(compositeActivity.getSimpleActivitysRecursively());
            } else if (element instanceof SimpleActivity) {
                SimpleActivity simpleActivity = (SimpleActivity) element;
                simpleActivitys.add(simpleActivity);
            }
        }
        return simpleActivitys;
    }

    public Set<ReferenceSimpleActivity> getReferenceSimpleActivitys() {
        Set<ReferenceSimpleActivity> referenceSimpleActivitys = new HashSet<ReferenceSimpleActivity>();
        for (Element element : elements) {
            if (element instanceof ReferenceSimpleActivity) {
                ReferenceSimpleActivity referenceSimpleActivity = (ReferenceSimpleActivity) element;
                referenceSimpleActivitys.add(referenceSimpleActivity);
            }
        }
        return referenceSimpleActivitys;
    }

    public Set<ReferenceSimpleActivity> getReferenceSimpleActivitysRecursively() {
        Set<ReferenceSimpleActivity> referenceSimpleActivitys = new HashSet<ReferenceSimpleActivity>();
        for (Element element : elements) {
            if (element instanceof CompositeActivity) {
                CompositeActivity compositeActivity = (CompositeActivity) element;
                referenceSimpleActivitys.addAll(compositeActivity.getReferenceSimpleActivitysRecursively());
            } else if (element instanceof ReferenceSimpleActivity) {
                ReferenceSimpleActivity referenceSimpleActivity = (ReferenceSimpleActivity) element;
                referenceSimpleActivitys.add(referenceSimpleActivity);
            }
        }
        return referenceSimpleActivitys;
    }

    public Set<Transition> getTransitionsRecursively() {
        Set<Transition> transitions = new HashSet<Transition>();
        for (Element element : elements) {
            if (element instanceof CompositeActivity) {
                CompositeActivity compositeActivity = (CompositeActivity) element;
                transitions.addAll(compositeActivity.getTransitionsRecursively());
            } else if (element instanceof Transition) {
                Transition transition = (Transition) element;
                transitions.add(transition);
            }
        }
        return transitions;
    }
    public Set<Event> getEventsRecursively() {
        Set<Event> events = new HashSet<Event>();
        for (Element element : elements) {
            if (element instanceof EventNode) {
                Event event = (Event) element;
                events.add(event);
            } 
        }
        return events;
    }
    public Set<Repository> getRepositorysRecursively() {
        Set<Repository> repositorys = new HashSet<Repository>();
        for (Element element : elements) {
            if (element instanceof Repository) {
                Repository repository = (Repository) element;
                repositorys.add(repository);
            } 
        }
        return repositorys;
    }
    
    public Set<CompositeActivity> getCompositeActivitysRecursively() {
        Set<CompositeActivity> compositeActivitys = new HashSet<CompositeActivity>();
        for (Element element : elements) {
            if (element instanceof CompositeActivity) {
                CompositeActivity compositeActivity = (CompositeActivity) element;
                compositeActivitys.add(compositeActivity);
                compositeActivitys.addAll(compositeActivity.getCompositeActivitysRecursively());
            }
        }
        return compositeActivitys;
    }
    /*
     @Override
     public void draw(Graphics g, DrawingOptions drawingOptions) {
     g.setColor(Color.white);
     g.fillRect(getStart().x, getStart().y, getWidth(), getHeight());
     g.setColor(color);
     g.drawRect(getStart().x, getStart().y, getWidth() - 1, getHeight() - 1);
     int rectanglesGap = 5;
     g.drawRect(getStart().x + rectanglesGap, getStart().y + rectanglesGap, getWidth() - 1 - 2 * rectanglesGap, getHeight() - 1 - 2 * rectanglesGap);
     drawLabel(g);
     }
     */

    @Override
    public void draw(Graphics g, DrawingOptions drawingOptions) {
        drawSimpleActivityBackground(g);
        drawSimpleActivityBorder(g);
        drawLabel(g);

    }

    protected void drawSimpleActivityBackground(Graphics g) {
        g.setColor(Color.white);
        g.fillOval(getStart().x, getStart().y, getWidth(), getHeight());
    }

    protected void drawSimpleActivityBorder(Graphics g) {
        g.setColor(color);
        g.drawOval(getStart().x, getStart().y, getWidth(), getHeight());
        g.drawOval(getStart().x + 5, getStart().y + 5, getWidth() - 10, getHeight() - 10);
        g.drawOval(getStart().x + 10, getStart().y + 10, getWidth() - 20, getHeight() - 20);
    }

    public Rectangle getBounds() {
        Rectangle bounds = null;

        for (Element element : elements) {
            if (bounds == null) {
                bounds = new Rectangle(element.getStart().x, element.getStart().y, element.getWidth(), getHeight());
            }
            bounds.add(element.getStart().x, element.getStart().y);
            bounds.add(element.getEnd().x, element.getEnd().y);
            bounds.add(element.getStart().x, element.getEnd().y);
            bounds.add(element.getEnd().x, element.getStart().y);
            if (element instanceof Edge) {
                Edge edge = (Edge) element;
                for (Point breakPoint : edge.getBreakPoints()) {
                    bounds.add(breakPoint);
                }
            }
        }
        if (bounds == null) {
            bounds = new Rectangle();
        }
        bounds.width++;
        bounds.height++;
        return bounds;
    }

    public Rectangle getBoundsRecursively() {
        return getBoundsRecursively(this);
    }

    private Rectangle getBoundsRecursively(CompositeActivity compositeActivity) {
        Rectangle bounds = compositeActivity.getBounds();
        for (Element element : compositeActivity.elements) {
            if (element instanceof CompositeActivity) {
                CompositeActivity subCompositeActivity = (CompositeActivity) element;
                Rectangle subCompositeActivityBounds = getBoundsRecursively(subCompositeActivity);
                subCompositeActivityBounds.translate(subCompositeActivity.getCenter().x, subCompositeActivity.getCenter().y);
                bounds = bounds.createUnion(subCompositeActivityBounds).getBounds();
            }
        }
        return bounds;
    }

    public Node getNodeByLabel(String label) {
        for (Element element : elements) {
            if (element instanceof Node) {
                Node node = (Node) element;
                if (label.equals(node.getLabel())) {
                    return node;
                }
            }
        }
        return null;
    }

    public Node getNodeById(String id) {
        for (Element element : elements) {
            if (element instanceof Node) {
                Node node = (Node) element;
                if (id.equals(node.getId())) {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * Returns an preview image of the subnet with specified marking. Scale
     * image: image.getScaledInstance(preferredWidth, preferredHeight,
     * Image.SCALE_SMOOTH) Save image: ImageIO.write(image, "png", file);
     */
    public BufferedImage getPreview(Marking marking) {
       CachedGraphics2D cachedGraphics = new CachedGraphics2D();
        cachedGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        DrawingOptions drawingOptions = new DrawingOptions();
        drawingOptions.setMarking(marking);
        marking.getLock().readLock().lock();
        try {
            for (Element element : getElements()) {
                element.draw(cachedGraphics, drawingOptions);
            }
        } finally {
            marking.getLock().readLock().unlock();
        }
        Rectangle bounds = cachedGraphics.getIntegerBounds();
        int width = bounds.width;
        int height = bounds.height;
        width = Math.max(width, 270);
        height = Math.max(height, 200);
        BufferedImage bufferedImage = new BufferedImage(width+200, height+300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D imageGraphics = (Graphics2D) bufferedImage.getGraphics();
        imageGraphics.fillRect(0, -70, width+500, height + 300); // paint the background white
        imageGraphics.translate(-bounds.x+10, -bounds.y+ 10);
        cachedGraphics.applyToGraphics(imageGraphics);
        return bufferedImage;
    }

    @Override
    public CompositeActivity getClone() {
        CompositeActivity compositeActivity = (CompositeActivity) super.getClone();
        compositeActivity.viewTranslation = this.viewTranslation.getLocation();

        compositeActivity.elements = new LinkedList<Element>();
        for (Element element : this.getElements()) {
            compositeActivity.addElement(element.getClone());
        }
        return compositeActivity;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public void setStatic(boolean isStatic) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
