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

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.event.*;
import org.atid.editor.actions.AboutAction;
import org.atid.editor.actions.AddConditionAction;
import org.atid.editor.actions.ArcSelectToolAction;
import org.atid.editor.actions.CloseCompositeActivityAction;
import org.atid.editor.actions.CompositeActivitySelectToolAction;
import org.atid.editor.actions.CopyAction;
import org.atid.editor.actions.CutAction;
import org.atid.editor.actions.DeleteAction;
import org.atid.editor.actions.EventSelectToolAction;
import org.atid.editor.actions.ExportAction;
import org.atid.editor.actions.ImportAction;
import org.atid.editor.actions.NewFileAction;
import org.atid.editor.actions.OpenCompositeActivityAction;
import org.atid.editor.actions.OpenFileAction;
import org.atid.editor.actions.PasteAction;
import org.atid.editor.actions.QuitAction;
import org.atid.editor.actions.RedoAction;
import org.atid.editor.actions.ReplaceCompositeActivityAction;
import org.atid.editor.actions.RepositorySelectToolAction;
import org.atid.editor.actions.SaveAction;
import org.atid.editor.actions.SaveCompositeActivityAsAction;
import org.atid.editor.actions.SaveFileAsAction;
import org.atid.editor.actions.SelectAllAction;
import org.atid.editor.actions.SelectionSelectToolAction;
import org.atid.editor.actions.SetArcInhibitoryAction;
import org.atid.editor.actions.SetArcResetAction;
import org.atid.editor.actions.SetLabelAction;
import org.atid.editor.actions.SetSimpleActivityStaticAction;
import org.atid.editor.actions.SetTokensAction;
import org.atid.editor.actions.SimpleActivitySelectToolAction;
import org.atid.editor.actions.TokenSelectToolAction;
import org.atid.editor.actions.TransitionSelectToolAction;
import org.atid.editor.actions.UndoAction;
import org.atid.editor.actions.algorithms.CoverabilityGraph;
import org.atid.editor.canvas.Canvas;
import org.atid.editor.canvas.Selection;
import org.atid.editor.canvas.SelectionChangedListener;
import org.atid.editor.filechooser.EpsFileType;
import org.atid.editor.filechooser.FileType;
import org.atid.editor.filechooser.FileTypeException;
import org.atid.editor.filechooser.PflowFileType;
import org.atid.editor.filechooser.PngFileType;
import org.atid.petrinet.Arc;
import org.atid.petrinet.CompositeActivity;
import org.atid.petrinet.CompositeActivityNode;
import org.atid.petrinet.Document;
import org.atid.petrinet.Element;
import org.atid.petrinet.EventNode;
import org.atid.petrinet.Marking;
import org.atid.petrinet.ReferenceSimpleActivity;
import org.atid.petrinet.RepositoryNode;
import org.atid.petrinet.Role;
import org.atid.petrinet.SimpleActivityNode;
import org.atid.petrinet.Transition;
import org.atid.petrinet.TransitionNode;
import org.atid.util.CollectionTools;
import org.atid.util.GraphicsTools;
import org.atid.util.ListEditor;

/**
 * This class is the main point of the application.
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class RootPflow implements Root, WindowListener, ListSelectionListener, SelectionChangedListener {

    private static final String APP_NAME = "ATID";
    private static final String APP_VERSION = "0.70";

    public RootPflow(String[] args) {
        Atid.setRoot(this);

        loadPreferences();
        selection.setSelectionChangedListener(this);
        
        /// menu lateral esquerdo


        setupMainFrame();
        mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setupFrameIcons();

        if (args.length == 1) {
            String filename = args[0];
            File file = new File(filename);
            FileType fileType = FileType.getAcceptingFileType(file, FileType.getAllFileTypes());
            try {
                Document document = fileType.load(file);
                this.setDocument(document);
                this.setCurrentFile(file); // TODO: make it DRY with OpenFileAction
                this.setModified(false);
                this.setCurrentDirectory(file.getParentFile());
                canvas.repaint();
            } catch (FileTypeException ex) {
                Logger.getLogger(RootPflow.class.getName()).log(Level.INFO, null, ex);
            }
        }
    }

    private static final String CURRENT_DIRECTORY = "current_directory";

    private void loadPreferences() {
        Preferences preferences = Preferences.userNodeForPackage(this.getClass());
        setCurrentDirectory(new File(preferences.get(CURRENT_DIRECTORY, System.getProperty("user.home"))));
    }

    private void savePreferences() {
        Preferences preferences = Preferences.userNodeForPackage(this.getClass());
        preferences.put(CURRENT_DIRECTORY, getCurrentDirectory().toString());
    }

    // Undo manager - per tab
    protected UndoAction undo = new UndoAction(this);
    protected RedoAction redo = new RedoAction(this);
    protected UndoManager undoManager = new UndoManager(this, undo, redo);

    @Override
    public UndoManager getUndoManager() {
        return undoManager;
    }

    // Current directory - per application
    protected File currentDirectory;

    @Override
    public File getCurrentDirectory() {
        return currentDirectory;
    }

    @Override
    public void setCurrentDirectory(File currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    // Main frame - per application
    protected MainFrame mainFrame = new MainFrame(getNewWindowTitle());

    @Override
    public Frame getParentFrame() {
        return mainFrame;
    }

    // Document - per tab
    protected Document document = new Document();

    @Override
    public Document getDocument() {
        return document;
    }

    @Override
    public void setDocument(Document document) {
        this.document = document;
        getDocument().petriNet.resetView();
        getUndoManager().eraseAll();
        refreshAll();
    }

    // Clicked element - per tab
    protected Element clickedElement = null;

    @Override
    public Element getClickedElement() {
        return clickedElement;
    }

    @Override
    public void setClickedElement(Element clickedElement) {
        this.clickedElement = clickedElement;
        enableOnlyPossibleActions();
    }

    // Selection - per tab
    protected Selection selection = new Selection();

    @Override
    public Selection getSelection() {
        return selection;
    }

    @Override
    public void selectionChanged() {
        enableOnlyPossibleActions();
    }

    // Selection + clicked element
    @Override
    public Set<Element> getSelectedElementsWithClickedElement() {
        Set<Element> selectedElements = new HashSet<Element>();
        selectedElements.addAll(getSelection().getElements());
        selectedElements.add(getClickedElement());
        return selectedElements;
    }

    // List editor - per tab
    protected ListEditor<Role> roleEditor; //TODO

    @Override
    public void valueChanged(ListSelectionEvent e) {
        enableOnlyPossibleActions();
        repaintCanvas();
    }

    //per tab
    @Override
    public void selectTool_Select() {
        select.setSelected(true);
        canvas.activeCursor = Cursor.getDefaultCursor();
        canvas.setCursor(canvas.activeCursor);
        repaintCanvas();
    }

    @Override
    public boolean isSelectedTool_Select() {
        return select.isSelected();
    }

    @Override
    public void selectTool_SimpleActivity() {
        simpleActivity.setSelected(true);
        canvas.activeCursor = GraphicsTools.getCursor("atid/canvas/place.png", new Point(16, 16));
        canvas.setCursor(canvas.activeCursor);
        repaintCanvas();
    }
    
    @Override
    public void selectTool_Event()  {
        event.setSelected(true);
        canvas.activeCursor = GraphicsTools.getCursor("atid/canvas/event.png", new Point(16,16));
        canvas.setCursor(canvas.activeCursor);
        repaintCanvas();
    }
    
    @Override
    public boolean isSelectTool_Event(){
        return event.isSelected();
    }
    
    @Override
    public void selectTool_Repository() {
        repository.setSelected(true);
        canvas.activeCursor = GraphicsTools.getCursor("atid/canvas/repository.png", new Point(16,16));
        canvas.setCursor(canvas.activeCursor);
        repaintCanvas();
    }
    
    @Override
    public boolean isSelectedTool_Repository(){
        return repository.isSelected();
    }
    
    @Override
    public boolean isSelectedTool_SimpleActivity() {
        return simpleActivity.isSelected();
    }

    @Override
    public void selectTool_Transition() {
        transition.setSelected(true);
        canvas.activeCursor = GraphicsTools.getCursor("atid/canvas/transition.gif", new Point(16, 16));
        canvas.setCursor(canvas.activeCursor);
        repaintCanvas();
    }

    @Override
    public boolean isSelectedTool_Transition() {
        return transition.isSelected();
    }

    
    @Override
    public void selectTool_CompositeActivity() {
        compositeActivity.setSelected(true);
        canvas.activeCursor = GraphicsTools.getCursor("atid/canvas/compositeactivity.gif", new Point (16,16));
                canvas.setCursor(canvas.activeCursor);
        repaintCanvas();
    }

    @Override
    public boolean isSelectedTool_CompositeActivity() {
        return compositeActivity.isSelected();
    }
    
    @Override
    public void selectTool_Arc() {
        arc.setSelected(true);
        canvas.activeCursor = GraphicsTools.getCursor("atid/canvas/arc.gif", new Point(0, 0));
        canvas.setCursor(canvas.activeCursor);
        repaintCanvas();
    }

    @Override
    public boolean isSelectedTool_Arc() {
        return arc.isSelected();
    }

    @Override
    public void selectTool_Token() {
        token.setSelected(true);
        canvas.activeCursor = GraphicsTools.getCursor("atid/canvas/token_or_fire.gif", new Point(16, 0));
        canvas.setCursor(canvas.activeCursor);
        repaintCanvas();
    }

    @Override
    public boolean isSelectedTool_Token() {
        return token.isSelected();
    }

    

    @Override
    public JPopupMenu getSimpleActivityPopup() {
        return simpleActivityPopup;
    }

    @Override
    public JPopupMenu getTransitionPopup() {
        return transitionPopup;
    }

    @Override
    public JPopupMenu getArcEdgePopup() {
        return arcEdgePopup;
    }

    @Override
    public JPopupMenu getSubnetPopup() {
        return subnetPopup;
    }

    @Override
    public JPopupMenu getCanvasPopup() {
        return canvasPopup;
    }

    //per tab
    protected Canvas canvas = new Canvas(this);
    protected DrawingBoard drawingBoard = new DrawingBoard(canvas);

    protected JPopupMenu simpleActivityPopup;
    protected JPopupMenu repositoryPopup;
    protected JPopupMenu eventPopup;
    protected JPopupMenu transitionPopup;
    protected JPopupMenu arcEdgePopup;
    protected JPopupMenu subnetPopup;
    protected JPopupMenu canvasPopup;

    //per application
    protected JToggleButton select, simpleActivity, transition, arc, token, compositeActivity, event, repository ;
    protected Action setLabel, setTokens, delete;
    protected Action replaceSubnet;
    
    protected Action saveSubnetAs;
    protected Action cutAction, copyAction, pasteAction, selectAllAction, addCondition;

    //per application
    protected Action openCompositeActivity;
    protected Action closeCompositeActivity;

    @Override
    public void openCompositeActivity() {
        openCompositeActivity.actionPerformed(null);
    }

    @Override
    public void closeCompositeActivity() {
        closeCompositeActivity.actionPerformed(null);
    }

    @Override
    public void refreshAll() {
        canvas.repaint();
        enableOnlyPossibleActions();

    }

    @Override
    public void repaintCanvas() {
        canvas.repaint();
    }

    protected void enableOnlyPossibleActions() {
        boolean isDeletable = clickedElement != null
                && !(clickedElement instanceof ReferenceSimpleActivity)
                || !selection.isEmpty()
                && !CollectionTools.containsOnlyInstancesOf(selection.getElements(), ReferenceSimpleActivity.class);
        boolean isCutable = isDeletable;
        boolean isCopyable = isCutable;
        boolean isPastable = !clipboard.isEmpty();
        boolean isSimpleActivityNode = clickedElement instanceof SimpleActivityNode;
        boolean isArc = clickedElement instanceof Arc;
        boolean isTransitionNode = clickedElement instanceof TransitionNode;
        boolean isTransition = clickedElement instanceof Transition;
        boolean isCompositeActivity = clickedElement instanceof CompositeActivity;
        boolean isCompositeActivityNode = clickedElement instanceof CompositeActivityNode;
        boolean areCompositeActivitys = !selection.getCompositeActivitys().isEmpty();
        boolean isEventNode = clickedElement instanceof EventNode;
        boolean isRepositoryNode = clickedElement instanceof RepositoryNode;

        boolean areTransitionNodes = !selection.getTransitionNodes().isEmpty();
        boolean areTransitions = !selection.getTransitions().isEmpty();
        boolean isParent = !document.petriNet.isCurrentCompositeActivityRoot();
        boolean isPtoT = false;

        if (isArc) {
            Arc test;
            test = (Arc) clickedElement;
            isPtoT = test.isSimpleActivityToTransition();
        }

        cutAction.setEnabled(isCutable);
        copyAction.setEnabled(isCopyable);
        pasteAction.setEnabled(isPastable);
        selectAllAction.setEnabled(true);
        delete.setEnabled(isDeletable);
        if(isCompositeActivityNode) setTokens.setEnabled(isCompositeActivityNode);
        else setTokens.setEnabled(isSimpleActivityNode);
        setLabel.setEnabled(isSimpleActivityNode || isCompositeActivityNode || isRepositoryNode || isEventNode);
        replaceSubnet.setEnabled(isCompositeActivity || areCompositeActivitys);
        saveSubnetAs.setEnabled(isCompositeActivity);
        addCondition.setEnabled(isTransition);
        openCompositeActivity.setEnabled(isCompositeActivity);
        closeCompositeActivity.setEnabled(isParent);
        undo.setEnabled(getUndoManager().isUndoable());
        redo.setEnabled(getUndoManager().isRedoable());

    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        quitApplication();
    }

    /**
     * Terminates the application
     */
    @Override
    public void quitApplication() {
        if (!this.isModified()) {
            quitNow();
        }
        mainFrame.setState(Frame.NORMAL);
        mainFrame.setVisible(true);
        int answer = JOptionPane.showOptionDialog(
                this.getParentFrame(),
                "Any unsaved changes will be lost. Really quit?",
                "Quit",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                new String[]{"Quit", "Cancel"},
                "Cancel");
        if (answer == JOptionPane.YES_OPTION) {
            quitNow();
        }
    }

    private void quitNow() {
        savePreferences();
        System.exit(0);
    }

    protected JToolBar toolBar = new JToolBar();

    protected void setupFrameIcons() {
        List<Image> icons = new LinkedList<Image>();
        icons.add(GraphicsTools.getBufferedImage("icon16.png"));
        icons.add(GraphicsTools.getBufferedImage("icon32.png"));
        icons.add(GraphicsTools.getBufferedImage("icon48.png"));
        mainFrame.setIconImages(icons);
    }

    protected void setupMainFrame() {
        List<FileType> openSaveFiletypes = new LinkedList<FileType>();
        openSaveFiletypes.add(new PflowFileType());
        List<FileType> importFiletypes = new LinkedList<FileType>();

        List<FileType> exportFiletypes = new LinkedList<FileType>();

        exportFiletypes.add(new EpsFileType());
        exportFiletypes.add(new PngFileType());

        Action newFile = new NewFileAction(this);
        Action openFile = new OpenFileAction(this, openSaveFiletypes);
        Action saveFile = new SaveAction(this, openSaveFiletypes);
        Action saveFileAs = new SaveFileAsAction(this, openSaveFiletypes);
        Action importFile = new ImportAction(this, importFiletypes);
        Action exportFile = new ExportAction(this, exportFiletypes);
        Action coverabilityGraph = new CoverabilityGraph(this.getDocument().getPetriNet());
        Action quit = new QuitAction(this);
        setLabel = new SetLabelAction(this);
        setTokens = new SetTokensAction(this);        

        openCompositeActivity = new OpenCompositeActivityAction(this);
        closeCompositeActivity = new CloseCompositeActivityAction(this);
        delete = new DeleteAction(this);

        cutAction = new CutAction(this);
        copyAction = new CopyAction(this);
        pasteAction = new PasteAction(this);
        selectAllAction = new SelectAllAction();
        addCondition =  new AddConditionAction(this);

        Action selectTool_SelectionAction = new SelectionSelectToolAction(this);
        Action selectTool_SimpleActivityAction = new SimpleActivitySelectToolAction(this);
        Action selectTool_TransitionAction = new TransitionSelectToolAction(this);
        
        Action selectTool_CompositeActivity = new CompositeActivitySelectToolAction(this);  
        
        Action selectTool_ArcAction = new ArcSelectToolAction(this);
        Action selectTool_TokenAction = new TokenSelectToolAction(this);
        Action selectTool_Event = new EventSelectToolAction(this);
        Action selectTool_Repository = new RepositorySelectToolAction(this);
        saveSubnetAs = new SaveCompositeActivityAsAction(this);
        replaceSubnet = new ReplaceCompositeActivityAction(this);

        select = new JToggleButton(selectTool_SelectionAction);
        select.setSelected(true);
        simpleActivity = new JToggleButton(selectTool_SimpleActivityAction);
        transition = new JToggleButton(selectTool_TransitionAction);
        event = new JToggleButton(selectTool_Event);
        repository = new JToggleButton(selectTool_Repository);
        
        compositeActivity = new JToggleButton(selectTool_CompositeActivity);
        
        arc = new JToggleButton(selectTool_ArcAction);
        token = new JToggleButton(selectTool_TokenAction);

        select.setText("");
        simpleActivity.setText("");
        transition.setText("");
        compositeActivity.setText("");
        arc.setText("");
        token.setText("");
        event.setText("");
        repository.setText("");

        ButtonGroup drawGroup = new ButtonGroup();
        drawGroup.add(select);
        drawGroup.add(simpleActivity);
        drawGroup.add(transition);
        drawGroup.add(compositeActivity);
        //drawGroup.add(event);
        drawGroup.add(repository);
        drawGroup.add(arc);
        drawGroup.add(token);

        //Barra barra com as imagens
        toolBar.setFloatable(false);

        toolBar.add(newFile);
        toolBar.add(openFile);
        toolBar.add(saveFile);
        //toolBar.add(importFile);
        //toolBar.add(exportFile);
        toolBar.addSeparator();

        toolBar.add(cutAction);
        toolBar.add(copyAction);
        toolBar.add(pasteAction);
        toolBar.addSeparator();

        toolBar.add(undo);
        toolBar.add(redo);
        toolBar.add(delete);
        toolBar.addSeparator();
        toolBar.add(select);
        toolBar.add(simpleActivity);
        toolBar.add(transition);
       // TO DO fazer com que o evento possa permitir a geração do gráfo
        //toolBar.add(event);
        toolBar.add(repository);
        
        toolBar.add(compositeActivity);
        
        toolBar.add(arc);
        toolBar.addSeparator();
        toolBar.add(token);
        toolBar.addSeparator();
        toolBar.add(closeCompositeActivity);
        toolBar.add(openCompositeActivity);
        
        // Menu principal
        JMenuBar menuBar = new JMenuBar();
        mainFrame.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        menuBar.add(fileMenu);

        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic('E');
        menuBar.add(editMenu);

        JMenu drawMenu = new JMenu("Draw");
        drawMenu.setMnemonic('D');
        menuBar.add(drawMenu);

//        JMenu elementMenu = new JMenu("Element");
//        elementMenu.setMnemonic('l');
//        menuBar.add(elementMenu);

        JMenu subnetMenu = new JMenu("Subnet");
        subnetMenu.setMnemonic('S');
        menuBar.add(subnetMenu);

        //asus 2012 algorithms menu
        JMenu algorithmsMenu = new JMenu("Algorithms");
        algorithmsMenu.setMnemonic('A');
        menuBar.add(algorithmsMenu);

        //asus 2012 algorithms submenu items
        algorithmsMenu.add(new CoverabilityGraph(Atid.getRoot().getDocument().getPetriNet()));
        
        JMenu configMenu = new JMenu("Settings");
        configMenu.setMnemonic('T');
        //menuBar.add(configMenu);
        
        
        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(new AboutAction(this));
        menuBar.add(helpMenu);

        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.add(saveFileAs);
        fileMenu.add(exportFile);
        fileMenu.addSeparator();
        fileMenu.add(quit);

        editMenu.add(undo);
        editMenu.add(redo);
        editMenu.addSeparator();
        editMenu.add(cutAction);
        editMenu.add(copyAction);
        editMenu.add(pasteAction);
        editMenu.add(selectAllAction);
        editMenu.add(delete);

//        elementMenu.add(setLabel);
        
        drawMenu.add(selectTool_SelectionAction);
        drawMenu.addSeparator();
        drawMenu.add(selectTool_SimpleActivityAction);
        drawMenu.add(selectTool_TransitionAction);
        //drawMenu.add(selectTool_Event);
        drawMenu.add(selectTool_Repository);
        drawMenu.add(selectTool_CompositeActivity);
        drawMenu.add(selectTool_ArcAction);
        drawMenu.add(selectTool_TokenAction);

        subnetMenu.add(openCompositeActivity);
        subnetMenu.add(closeCompositeActivity);
        subnetMenu.add(replaceSubnet);
        subnetMenu.add(saveSubnetAs);

        simpleActivityPopup = new JPopupMenu();
        simpleActivityPopup.add(setLabel);
        simpleActivityPopup.addSeparator();
        simpleActivityPopup.add(cutAction);
        simpleActivityPopup.add(copyAction);
        simpleActivityPopup.add(delete);
        
        repositoryPopup = new JPopupMenu();
        repositoryPopup.add(setLabel);
        repositoryPopup.addSeparator();
        repositoryPopup.add(cutAction);
        repositoryPopup.add(copyAction);
        repositoryPopup.add(delete);

        eventPopup = new JPopupMenu();
        eventPopup.add(setLabel);
        eventPopup.addSeparator();
        eventPopup.add(cutAction);
        eventPopup.add(copyAction);
        eventPopup.add(delete);

        transitionPopup = new JPopupMenu();
        transitionPopup.add(cutAction);
        transitionPopup.add(copyAction);
        transitionPopup.add(delete);
        //transitionPopup.add(addCondition);

        Font boldFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);

        canvasPopup = new JPopupMenu();
        canvasPopup.add(closeCompositeActivity).setFont(boldFont);
        canvasPopup.add(pasteAction);

        subnetPopup = new JPopupMenu();
        subnetPopup.add(openCompositeActivity).setFont(boldFont);
        subnetPopup.add(setLabel);
        subnetPopup.add(replaceSubnet);
        subnetPopup.add(saveSubnetAs);
        subnetPopup.addSeparator();
        subnetPopup.add(cutAction);
        subnetPopup.add(copyAction);
        subnetPopup.add(delete);

        arcEdgePopup = new JPopupMenu();
        arcEdgePopup.add(delete);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        splitPane.setDividerSize(6);
        splitPane.setOneTouchExpandable(true);
        splitPane.setRightComponent(drawingBoard);
        splitPane.setDividerLocation(120);

        mainFrame.add(splitPane, BorderLayout.CENTER);
        mainFrame.add(toolBar, BorderLayout.NORTH);

        mainFrame.addWindowListener(this);
        mainFrame.setLocation(50, 50);
        mainFrame.setSize(800, 650);
        mainFrame.setVisible(true);
    }

    public Marking getCurrentMarking() {
        return org.atid.editor.Atid.getRoot().getDocument().petriNet.getInitialMarking();
    }

    public void setCurrentMarking(Marking currentMarking) {
    }

    protected LocalClipboard clipboard = new LocalClipboard();

    public LocalClipboard getClipboard() {
        return clipboard;
    }

    private boolean isModified = false;

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean isModified) {
        this.isModified = isModified;
        mainFrame.setTitle(getNewWindowTitle());
    }

    private String getNewWindowTitle() {
        String windowTitle = "";
        if (getCurrentFile() != null) {
            windowTitle += getCurrentFile().getName();
        } else {
            windowTitle += "Untitled";
        }
        if (isModified()) {
            windowTitle += " [modified]";
        }
        windowTitle += " - " + getAppShortName();
        return windowTitle;
    }

    private File currentFile = null;

    public File getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(File currentFile) {
        this.currentFile = currentFile;
        mainFrame.setTitle(getNewWindowTitle());
    }

    public String getAppShortName() {
        return APP_NAME;
    }

    public String getAppLongName() {
        return APP_NAME + ", version " + APP_VERSION;
    }

    @Override
    public DrawingBoard getDrawingBoard() {
        return drawingBoard;
    }

}
