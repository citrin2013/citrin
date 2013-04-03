import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.io.FileWriter.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import javax.swing.undo.AbstractUndoableEdit;

public class guiPanel extends JPanel	implements ActionListener {

	// C++ source file this application has the focus on currently
	// When tab focus changes this variable should refer to the file the tab contains
	String currentCppSourceFile;

	private static final long serialVersionUID = 1L;

	//JMenuItems for File
	// private JMenuItem newFile;
	// private JMenuItem openFile;
	// private JMenuItem saveFile;
	// private JMenuItem saveAsFile;
	// private JMenuItem run;
	// private JMenuItem exit;

	//Menu items for Edit
	private JMenuItem undo;
	private JMenuItem redo;
	private JButton pasteButton;


//	private JTextArea area;

	private Controller controller;

	//TODO: need functionality to keep track of what action was just done

	//Menu Items for Help
	private JMenuItem tutorial;

	
	//constructor
	public guiPanel(){

		JMenuBar topBar = new JMenuBar(); //menu bar to hold the menus

		JMenu fileMenu = new JMenu("File");
		JMenu editMenu = new JMenu("Edit");
		//JMenu styleMenu = new JMenu("Style");
		JMenu helpMenu = new JMenu("Help");
		JMenu runMenu = new JMenu("Run");

		
		//menu items for the menu "File"
		// newFile = new JMenuItem("New");
		// openFile = new JMenuItem("Open");
		// saveFile = new JMenuItem("Save");
		// saveAsFile = new JMenuItem("Save As");
		// run = new JMenuItem("Run");
		// exit = new JMenuItem("Exit");

		//menu items for "Edit"
		redo = new JMenuItem("Redo");
		undo = new JMenuItem("Undo");

		//help menu
		tutorial = new JMenuItem("Tutorial");

		//add the menus to the menu bar
		topBar.add(fileMenu);
		topBar.add(editMenu);
		topBar.add(runMenu);
		//topBar.add(styleMenu);
		topBar.add(helpMenu);

		//add the menu items to File
		// fileMenu.add(newFile);
		// fileMenu.addSeparator(); //separator between menu items
		// fileMenu.add(openFile);
		// fileMenu.addSeparator();
		// fileMenu.add(saveFile);
		// fileMenu.addSeparator();
		// fileMenu.add(saveAsFile);
		// fileMenu.addSeparator();
		// fileMenu.add(run);
		// fileMenu.addSeparator();
		// fileMenu.add(exit);


		helpMenu.add(tutorial);

		JPanel	myPanel = new JPanel();

		JTextArea area = new JTextArea();
		JTextArea area2 = new JTextArea();
		JTextArea area3 = new JTextArea();
		//set up the area to be put into the scroll pane
		area.setColumns(30);
		area.setRows(10);
		area.setEditable(true);

		JToolBar editToolBar = new JToolBar();

		//add edit buttons to menu
		// TODO: get undo/redo working, need undomanager and listner; look at swing.undo
		editMenu.add(undo);
		editMenu.add(redo);
		editMenu.addSeparator();
		editMenu.add(area.getActionMap().get(DefaultEditorKit.cutAction));
		editMenu.add(area.getActionMap().get(DefaultEditorKit.copyAction));
		editMenu.add(area.getActionMap().get(DefaultEditorKit.pasteAction));
		editMenu.addSeparator();
		editMenu.add(area.getActionMap().get(DefaultEditorKit.selectAllAction));

		// Make buttons look nicer
		Action a;
		a = area.getActionMap().get(DefaultEditorKit.cutAction);
		a.putValue(Action.SMALL_ICON, new ImageIcon("cut.gif"));
		a.putValue(Action.NAME, "Cut");

		a = area.getActionMap().get(DefaultEditorKit.copyAction);
		a.putValue(Action.SMALL_ICON, new ImageIcon("copy.gif"));
		a.putValue(Action.NAME, "Copy");

		a = area.getActionMap().get(DefaultEditorKit.pasteAction);
		a.putValue(Action.SMALL_ICON, new ImageIcon("paste.gif"));
		a.putValue(Action.NAME, "Paste");

		a = area.getActionMap().get(DefaultEditorKit.selectAllAction);
		a.putValue(Action.NAME, "Select All");

		Action copyAction = new DefaultEditorKit.CopyAction();
		copyAction.putValue(Action.SMALL_ICON, new ImageIcon("copy.gif"));
		copyAction.putValue(Action.NAME, ""); 

		Action pasteAction = new DefaultEditorKit.PasteAction();
		pasteAction.putValue(Action.SMALL_ICON, new ImageIcon("paste.gif"));
		pasteAction.putValue(Action.NAME, "");

		Action cutAction = new DefaultEditorKit.CutAction();
		cutAction.putValue(Action.SMALL_ICON, new ImageIcon("cut.gif"));
		cutAction.putValue(Action.NAME, "");

		// To Do: Get style menu working
		/*Action action = new StyledEditorKit.BoldAction();
		action.putValue(Action.NAME, "Bold");
		styleMenu.add(action); 

		action = new StyledEditorKit.ItalicAction();
		action.putValue(Action.NAME, "Italic");
		styleMenu.add(action);

		action = new StyledEditorKit.UnderlineAction();
		action.putValue(Action.NAME, "Underline");
		styleMenu.add(action);*/

		JTabbedPane program = new JTabbedPane();
		JTabbedPane tabbedPane2 = new JTabbedPane();
		JTabbedPane tabbedPane3 = new JTabbedPane();

		area2.setColumns(30);
		area2.setRows(10);
		area2.setEditable(true);

		area3.setColumns(30);
		area3.setRows(10);
		area3.setEditable(false);

		Console console = new Console();
		console.setEditable(false);
		Editor editor = new Editor();

		JScrollPane scrollPane = new JScrollPane(editor);
		
		//add line numbers to editor
		TextLineNumber tln = new TextLineNumber(editor);
		scrollPane.setRowHeaderView(tln);
		
		//add line numbers to editor
	//	TextLineNumber tln = new TextLineNumber(editor);
	//	scrollPane.setRowHeaderView(tln);
		
		JScrollPane scrollPane2 = new JScrollPane(console);

		JScrollPane scrollPane3 = new JScrollPane(area3);

		//tabbedPane.addTab("Program", null);
		program.add("Program", scrollPane);
	//	program.setName("*Program");

		//tabbedPane2.addTab("Console", null);
		tabbedPane2.add("Console", scrollPane2);

		//tabbedPane2.add("copy.gif", new ImageIcon("copy.gif"), 0);

		//tabbedPane3.addTab("States", null);
		tabbedPane3.add("States", scrollPane3);

		fileMenu.add(new OpenAction(editor));
		fileMenu.addSeparator();
		fileMenu.add(new SaveAction("Save", editor, true));
		fileMenu.add(new SaveAction("SaveAs", editor, false));
		fileMenu.addSeparator();
		fileMenu.add(new ExitAction());

		runMenu.add(new RunAllAction(console)); 
		runMenu.add(new StepAction(console));
		runMenu.add(new RunToBreakpointAction(console));

		JButton open = new JButton(new OpenActionButton(editor));
		open.setToolTipText("Open");
		
		//buttons for cut, copy, paste
		JButton cut = new JButton(cutAction);
		cut.setToolTipText("Cut (Ctrl+X)");
		JButton copy = new JButton(copyAction);
		copy.setToolTipText("Copy (Ctrl+C)");
		JButton paste =  new JButton(pasteAction);
		paste.setToolTipText("Paste (Ctrl+V)");
		
		//button to stop the interpreter
		Action stopAction = new StopRunAction();
		JButton stopRunButton = new JButton(stopAction);
		
		//button for runAll
		Action runAllAction = new RunAllActionButton();
		JButton runAll = new JButton(runAllAction);
		runAll.setToolTipText("Run All (F11)");
		runAllAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("F11"));
		runAll.getActionMap().put("runAll", runAllAction);
		runAll.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put((KeyStroke) runAllAction.getValue(Action.ACCELERATOR_KEY), "runAll")
;
		//button for runStep
		Action runStepAction = new StepActionButton();
		JButton runStep = new JButton(runStepAction);
		runStep.setToolTipText("Run Step (F1)");
		runStepAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("F1"));
		runStep.getActionMap().put("runStep", runStepAction);
		runStep.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put((KeyStroke) runStepAction.getValue(Action.ACCELERATOR_KEY), "runStep")
;
		
		//button for save
		Action saveAction = new SaveActionButton("", editor, true);
		saveAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control S"));
		JButton save = new JButton(saveAction);
		save.getActionMap().put("save", saveAction);
		save.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put((KeyStroke) saveAction.getValue(Action.ACCELERATOR_KEY), "save")
;
		save.setToolTipText("Save (Ctrl+S)");

		editToolBar.add(open);
		editToolBar.add(cut);
		editToolBar.add(copy);
		editToolBar.add(paste);
		editToolBar.add(runAll);
		editToolBar.add(runStep);
		editToolBar.add(save);
		editToolBar.add(stopRunButton);
		

		JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabbedPane3, tabbedPane2);

		splitPane2.setOneTouchExpandable(true);
		splitPane2.setDividerLocation(200);

		// JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, splitPane2);
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, program, splitPane2);

		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(450);

		//Provide minimum sizes for the two components in the split pane
		Dimension minimumSize = new Dimension(200, 10);
		scrollPane.setMinimumSize(minimumSize);
		scrollPane2.setMinimumSize(minimumSize);
		scrollPane3.setMinimumSize(new Dimension(10, 10));

		myPanel.setLayout(new BorderLayout());
		myPanel.setPreferredSize(new Dimension(500, 600));
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(800, 650));


		myPanel.add("Center",splitPane);
		myPanel.add("North", editToolBar);
		//myPanel.setLocation(0, 0); //This line doesn't seem to be necessary.
		add("Center", myPanel);
		add("North", topBar);

		//setLocation(0,0); //This line doesn't seem to be necessary.

		controller = new Controller(console);	

	}


	private static void createAndShowGUI(){
		// Create the container	
		JFrame frame = new JFrame("CITRIN", null);

		// Quit the application when this frame is closed:
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and add the content
		guiPanel panel = new guiPanel();
		frame.setLayout(new BorderLayout());
		frame.add(panel);

		// Display the window
		frame.pack(); // adapt the frame size to its content
		frame.setLocation(300, 20); // in pixels
		frame.setVisible(true); // Now the frame will appear on screen
		// Quit the application when this frame is closed:
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//add a window listener
		frame.addWindowListener(new WindowAdapter(){
			//save the user preferences as the window closes
			public void windowClosing(WindowEvent e){

				System.exit(0);
			}
		});

	}

	public static void main(String args[]){

		// Set the look and feel to that of the system
		try { 
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() ); 
		}
		catch ( Exception e ) { 
			System.err.println( e ); 
		}

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
			});


	}

	/*
	public void  saveFileAs() throws IOException	{
			String saveFileName = JOptionPane.showInputDialog("Save File As");
			System.out.print(saveFileName);
	}
	*/


	/*@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == exit){
			System.exit(0);
		}
	}*/


// ----------------------------------------------------------------------------
// Callback Classes 
//
// TODO : 
//
//		- NewFile Action
//		- Error checking such as making sure the file exists before saving
//
// ToConsider : 
//
//		 SaveAction and OpenAction should  resides in a different class since
//		 those actions should be more related to some editing session type class
//		 rather than this main application class
//
//		 All these functions are using currentCppSourceFile. Hard to keep track
//		 of if multiple editing session should be implemented.

	// An action to simply exit the app
	class ExitAction extends AbstractAction {

		public ExitAction() {
			super("Exit");
		}

		public void actionPerformed(ActionEvent ev) {
				System.exit(0);
		}

	}

	// An action that saves the document to a file : Supports Save and SaveAs actoins 
	class SaveActionButton extends AbstractAction {

		// PossibleBugSource : Saving to class member variable currentCppSourceFile

		JTextComponent textComponent;
		boolean saveToCurrentFile;

		// label				... label to show on the view
		// textComponent		... the view and model that keeps and show the text data
		// saveToCurrentFile	... false => prompts the user for the file, true => save to curent file
		public SaveActionButton(String label, JTextComponent textComponent, boolean saveToCurrentFile) {
			super("", new ImageIcon("saved.gif"));
			this.textComponent = textComponent;
			this.saveToCurrentFile = saveToCurrentFile;
			
		}

		public void actionPerformed(ActionEvent ev) {
			File file;
			if ( ! saveToCurrentFile ) {
			JFileChooser chooser = new JFileChooser();
			if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION)
				return;
			file = chooser.getSelectedFile();
			if (file == null)
				return;
			} 
			else {
				file = new File(currentCppSourceFile);
			}

			FileWriter writer = null;
			try {
				writer = new FileWriter(file);
				textComponent.write(writer);
			} catch (IOException ex) {
			JOptionPane.showMessageDialog(null,
				"File Not Saved", "ERROR", JOptionPane.ERROR_MESSAGE);
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException x) {
					}
				}
			}
		}

	}

	// An action that saves the document to a file : Supports Save and SaveAs actoins 
	class SaveAction extends AbstractAction {

		// PossibleBugSource : Saving to class member variable currentCppSourceFile

		JTextComponent textComponent;
		boolean saveToCurrentFile;

		// label				... lable to show on the view
		// textComponent		... the view and model that keeps and show the text data
		// saveToCurrentFile	... false => prompts the user for the file, true => save to curent file
		public SaveAction(String label, JTextComponent textComponent, boolean saveToCurrentFile) {
			super(label, new ImageIcon("saved.gif"));
			this.textComponent = textComponent;
			this.saveToCurrentFile = saveToCurrentFile;
		}

		public void actionPerformed(ActionEvent ev) {
			
			File file;
			if ( ! saveToCurrentFile ) {
			JFileChooser chooser = new JFileChooser();
			if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION)
				return;
			file = chooser.getSelectedFile();
			if (file == null)
				return;
			} 
			else {
				file = new File(currentCppSourceFile);
			}

			FileWriter writer = null;
			try {
				writer = new FileWriter(file);
				textComponent.write(writer);
			} catch (IOException ex) {
			JOptionPane.showMessageDialog(null,
				"File Not Saved", "ERROR", JOptionPane.ERROR_MESSAGE);
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException x) {
					}
				}
			}
		}
	}

	// An action that opens an existing file
	class OpenAction extends AbstractAction {
		JTextComponent textComponent;

		// textComponent ... this action opens a file into this textComponent
		public OpenAction(JTextComponent textComponent) {
			super("Open", new ImageIcon("open.gif"));
			this.textComponent = textComponent;
		}

		// Query user for a filename and attempt to open and read the file into
		// the text component.
		public void actionPerformed(ActionEvent ev) {
			JFileChooser chooser = new JFileChooser();
			if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
			return;
			File file = chooser.getSelectedFile();
			if (file == null)
			return;

			FileReader reader = null;
			try {
				reader = new FileReader(file);
				textComponent.read(reader, null);
				currentCppSourceFile = file.getPath();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(null,
				"File Not Found", "ERROR", JOptionPane.ERROR_MESSAGE);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException x) {
					}
					//Set text component to focus
					textComponent.requestFocus();
					
					//hack to update line numbers
					textComponent.setCaretPosition(textComponent.getDocument().getLength());
					textComponent.setCaretPosition(0);
				}
			}
		}
	}
	
	// An action that opens an existing file
	class OpenActionButton extends AbstractAction {
		JTextComponent textComponent;

		// textComponent ... this action opens a file into this textComponent
		public OpenActionButton(JTextComponent textComponent) {
			super("", new ImageIcon("open.gif"));
			this.textComponent = textComponent;
		}

		// Query user for a filename and attempt to open and read the file into
		// the text component.
		public void actionPerformed(ActionEvent ev) {
			JFileChooser chooser = new JFileChooser();
			if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
			return;
			File file = chooser.getSelectedFile();
			if (file == null)
			return;

			FileReader reader = null;
			try {
				reader = new FileReader(file);
				textComponent.read(reader, null);
				currentCppSourceFile = file.getPath();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(null,
				"File Not Found", "ERROR", JOptionPane.ERROR_MESSAGE);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException x) {
					}
				}
				//Set text component to focus
				textComponent.requestFocus();
				
				//hack to update line numbers
				textComponent.setCaretPosition(textComponent.getDocument().getLength());
				textComponent.setCaretPosition(0);
			}
		}
	}

	// An action that runs the interpreter on all the contents of the current cpp source file
	class RunAllAction extends AbstractAction {
		JTextComponent display;
		String interpretation = "";

		public RunAllAction(JTextComponent display) {
			super("RunAll", new ImageIcon("RunAll.gif"));
			this.display = display;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Interpreter i;
			// run interpreter on currentCppSourceFile

			// Start interpreter in new thread
			// TODO: hmmmm is this the best way
			synchronized(controller){
				if(!controller.isInterpreting()){
					controller.clearConsole();
					controller.setInterpretingStart();
					new Thread(i = new Interpreter(controller,currentCppSourceFile,-1)).start();
					controller.addInterpreter(i);
				}
				else{
					controller.runToBreak();
				}
			}
		}
	}


	// An action that runs the interpreter on all the contents of the current cpp source file
class RunAllActionButton extends AbstractAction {

	String interpretation = "";	
	 public RunAllActionButton(){
		super("", new ImageIcon("runAll.gif"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Interpreter i;
		// run interpreter on currentCppSourceFile

		// Start interpreter in new thread
		// TODO: hmmmm is this the best way
		synchronized(controller){
			if(!controller.isInterpreting()){
				controller.clearConsole();
				controller.setInterpretingStart();
				new Thread(i = new Interpreter(controller,currentCppSourceFile,-1)).start();
				controller.addInterpreter(i);
			}
			else{
				controller.runToBreak();
			}
		}
	}

}


	class StepAction extends AbstractAction {
		JTextComponent display;
		String interpretation = "";

		public StepAction(JTextComponent display) {
			super("Step", new ImageIcon("step.gif"));
			this.display = display;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// run interpreter on currentCppSourceFile

			Interpreter i;
			synchronized(controller){
				if(!controller.isInterpreting()){
					controller.clearConsole();
					controller.setInterpretingStart();
					new Thread(i = new Interpreter(controller,currentCppSourceFile,1)).start();
					controller.addInterpreter(i);
				}
				else{
					controller.addSteps(1);
				}
			}

		}

	}
	
	class StepActionButton extends AbstractAction {
		String interpretation = "";
		public StepActionButton(){
			super("", new ImageIcon("step.gif"));
		}

		@Override
		public void actionPerformed(ActionEvent e){
			Interpreter i;
			//run interpreter on currentCppSourceFile

			//Start interpreter in new thread
			synchronized(controller){
				if(!controller.isInterpreting()){
					controller.clearConsole();
					controller.setInterpretingStart();
					new Thread(i=new Interpreter(controller, currentCppSourceFile,1)).start();
					controller.addInterpreter(i);
				}
				else{
					controller.addSteps(1);
				}
			}
		}
	}


	class RunToBreakpointAction extends AbstractAction {
		JTextComponent display;
		String interpretation = "";

		public RunToBreakpointAction(JTextComponent display) {
			super("Run to Breakpoint");
			this.display = display;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// run interpreter on currentCppSourceFile
			int steps;
			JOptionPane dialog = new JOptionPane();
			int input = Integer.parseInt(dialog.showInputDialog("Enter number of steps to run"));
			input = input-1;
			Interpreter i;
			synchronized(controller){
					controller.clearConsole();
					controller.setInterpretingStart();
					new Thread(i = new Interpreter(controller,currentCppSourceFile,1)).start();
					controller.addInterpreter(i);
					controller.addSteps(input);
			}

		}

	}
	
	class StopRunAction extends AbstractAction {


		public StopRunAction() {
			super("", new ImageIcon("stop.gif"));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			//stop interpreter

			synchronized(controller){
					controller.stopInterpreter();
			}

		}

	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
	/*	// TODO Auto-generated method stub 
		ImageIcon t = new ImageIcon("saved.gif");
		if(e.getSource() == saveButton){
		saveButton.setIcon(t);
		//saveButton.add(saveButton, new ImageIcon("saved.gif"));
		}*/
	}

}
