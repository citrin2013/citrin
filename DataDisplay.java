/** Overview.
 * 
 * This is a data display panel designed to show C++ variables. 
 * This class is like the observer in Observer Pattern with the observable
 * being SymbolTable / Interpreter but not limited to them. If this observer
 * needs to listen to other observable, then update() method should accomodate
 * the new observable.
 *
 * Manifest 
 *
 *	DataDisplay
 *
 *		update() is CitrinObserver Method
 *
 *		main() is driver
 *
 * Note
 * 
 * The reason existing Java Observer class is not being used in this class is
 * that SymbolNotifier needs to extend from SymbolTable and cannot inherite
 * from Java Observable. The workaround for that is creating CitrinObservable
 * mix it in to SymbolTableNotifier. Since Java Observer class do not understand
 * CitrinObservable, CitrinObserver is made to work with CitrinObservable.
 *
 */


// TODO 
//
// Implement all the update() on all the events of SymbolTableEvent.
//
// make this implement CitrinObservable
//
// make overview 
//
// Integrate this to CITRIN
//
// dim shadowed symbol
//
// click on symbol and jump to its location
//
// show other variable info like in var_type
// 
// fix debug print 
//

import java.lang.Thread;
import javax.swing.JTable;
import java.util.Observer;
import java.util.*;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.JComponent;
import javax.swing.JTextArea;

import javax.swing.Box;
import javax.swing.JLabel;
import java.awt.BorderLayout;

public class DataDisplay extends JPanel implements CitrinObserver { // implements Observer {

	boolean DEBUG = true;

	// Data for Table
	private String[] columnNames = {
		"name",
		"value"
	};
	private Object[][] data = {};

	private Dimension dim;

	// TODO 
	// make a class that contains pair of table and its container instead of
	// a parallel list. This is error prone.
	private List<JTable> tables = new ArrayList<JTable>();
	private List<JComponent> tableContainers = new ArrayList<JComponent>();
	private int tableCount = 0;

	// -----------------------------------------------------------------------
	// Constructor

	/** Instantiate a table within a pane with the specified dimension.
	 */
	public DataDisplay(Dimension dim)
	{
		// Set dimenstion of the table
		this.dim = dim;

		// Add Title
		Box box1 = Box.createHorizontalBox();
		box1.add(new JLabel("Variables"));
		add(box1, BorderLayout.NORTH);

		// JTextArea area3 = new JTextArea();
		// area3.setColumns(30);
		// area3.setRows(10);
		// area3.setEditable(false);
		// JScrollPane scrollPane3 = new JScrollPane(area3);
		// add("States", scrollPane3);

		// TODO
		// push scope for global scope. ugly hack.
		//
		// In the SymbolTable class global scope
		// is pushed in its constructor without using its internal
		// push*Scope() function, so SymbolTableEvent.scopePushed* is not
		// propagated into this observer, since observer is added only after
		// the construction of SymbolTable via SymbolTableNotifier::addObserver()
		//
		addScopeTable();

		//
		// debug
		//
		// Print contents of the table on click
		//
		for (final JTable tab : tables ) {
			if (DEBUG) {
				tab.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						printDebugData(tab);
					}
				});
			}
		}
	}

	void setDimension(Dimension dim)
	{
		this.dim = dim;
	}

	private void addScopeTable()
	{
		tableCount++;

		// Create new table for the new scope
		JTable table = new JTable();
		table.setModel( new DefaultTableModel(data,columnNames) );
		tables.add( table );
		table.setPreferredScrollableViewportSize(dim);
        table.setFillsViewportHeight(true);

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);
		tableContainers.add(scrollPane);
        //Add the scroll pane to this panel.
        add(scrollPane, BorderLayout.SOUTH);
		revalidate();
		repaint();
	}

	// -----------------------------------------------------------------------
	// Observer Method

	/** This function updates DataDisplay. 
	 *
	 * First argument is a type of notifier and second argument is an event on
	 * the notifier. The notifier should call this function to trigger udpate
	 * needed. What is updated is reporeted to this method by the two
	 * arguments, but this method decides how the udpate is reflected.
	 *
	 **/
	public void update(CitrinObservable notifier, Object arg) 
	{
		// System.out.println("DataDisplay::update called");

		// Notifier is symbol table
		if ( notifier instanceof SymbolTableNotifier ) {
			//
			// look in arg and do updte according to what notifier is
			//
			// SymbolTable should have a public method from which this class
			// can pull out data for display
			
			SymbolTableNotifier stab = (SymbolTableNotifier) notifier;

			// Multiple behavior depending on event on symbol table
			SymbolTableEvent e = (SymbolTableEvent) arg;
			if ( e == SymbolTableEvent.scopePushedInParallel) {
				// TODO : 
				// Make current scope table dimmed
				addScopeTable();

				System.out.println("DataDiaplay::update() on scopePushedInParallel");
			}
			else if ( e == SymbolTableEvent.scopePushedAsChild ) {
				addScopeTable();

				System.out.println("DataDiaplay::update() on scopePushedAsChild");
			}
			else if ( e == SymbolTableEvent.scopePopped ) {
				// TODO 
				// what if it is a parellel scope 

				// if (0<tableContainers.size()) {
				if (0<tableCount) {
					remove( tableContainers.get( tableContainers.size()-1));
					tableContainers.remove(tableContainers.size()-1);
					tables.remove(tables.size()-1);
					revalidate();
					repaint();
					tableCount--;
				}
				else {
					System.out.println("DataDiaplay::update() on scopePopped called when tableCount =< 0");
				}
				System.out.println("DataDiaplay::update() on scopePopped");
			}
			else if ( e == SymbolTableEvent.symbolInserted ) {
				Symbol s = stab.getInsertedSymbol();
				SymbolLocation loc = s.getLocation();
				var_type v = s.getData();
				
				String str = v.getDisplayVal();
				
				final Object[][] row= { { v.var_name, str} }; 
				JTable table = tables.get( tables.size() - 1 );
				DefaultTableModel tmodel = (DefaultTableModel) table.getModel();
				tmodel.addRow(row[0]);
				System.out.println("DataDiaplay::update() on symbolInserted");
			}
			else if ( e == SymbolTableEvent.symbolAssignedNewValue) {
				boolean found = false;
				Symbol sym = stab.getAssignedSymbol();
				String symName = stab.getAssignedSymbolName();
				var_type v = sym.getData();
				int index = tables.size()-1;
				
				String str = v.getDisplayVal();
				
				while(!found && index >= 0){
					DefaultTableModel tmodel = (DefaultTableModel) tables.get(index).getModel();
					for ( int i = 0; i < tmodel.getRowCount(); i++) {
						String name = tmodel.getValueAt(i, 0).toString();
						if ( name.equals( symName ) ) {
							tmodel.setValueAt( str, i, 1 );
							found = true;
						}
					}
					index--;
				}
				System.out.println("DataDiaplay::update() on symbolAssignedNewValue");
			}
			else if ( e == SymbolTableEvent.cleared) {
				while (1<tableCount) {
					remove( tableContainers.get( tableContainers.size()-1));
					tableContainers.remove(tableContainers.size()-1);
					tables.remove(tables.size()-1);
					revalidate();
					repaint();
					tableCount--;
				}
				DefaultTableModel tmodel = (DefaultTableModel) tables.get(0).getModel();
				for(int i=tmodel.getRowCount()-1;i>=0;i--){
					tmodel.removeRow(i);
				}
				
				System.out.println("DataDiaplay::update() on cleared");
			}
			else {
				throw new RuntimeException("DataDisplay::update() Invalid SymbolTableEvent Supplied");
			}

		}
		else if ( notifier instanceof Interpreter ) {
			Interpreter interpreter = (Interpreter) notifier;
			// look in arg and do something accordingly 
			System.out.println("DataDisplay::update() notified from interpreter");
			;
		}
		else {
			throw new RuntimeException("DataDisplay::update() : Invalid Observable");
		}
	}

	// private function to reflect changes on the GUI component 
	private void updateDisplay()
	{
	}

	// -----------------------------------------------------------------------
	// Debugging
	//

	// Print all the data in the table to stdout
	private void printDebugData(JTable table) {

		int numRows = table.getRowCount();
		int numCols = table.getColumnCount();
		javax.swing.table.TableModel model = table.getModel();

		System.out.println("Value of data: ");
		for (int i=0; i < numRows; i++) {
			System.out.print("    row " + i + ":");
			for (int j=0; j < numCols; j++) {
				System.out.print("  " + model.getValueAt(i, j));
			}
			System.out.println();
		}
		System.out.println("--------------------------");
	}

	// -----------------------------------------------------------------------
	// Driver Methods

	/** Create the GUI and show it.  
	 *
	 * For thread safety, this method should be invoked from the
	 * event-dispatching thread.
	 *
	 * Just for Driver.
	 * 
	 */
	private static void createAndShowGUI(SymbolTableNotifier stab, DataDisplay dd) {

	}

	/** Driver.
	 *
	 *  Runs the data display and symbol table together. 
	 *
	 *  Note the display is automatically updated 
	 *
	 */
	public static void main(String[] args) throws InterruptedException
	{
		
		// Instanticate data display and symbol table.
		//
		// After data display is added to the SymbolTableNotifier, update on
		// the symbol table will be automatically reflected .
		//
		final DataDisplay display = new DataDisplay(new Dimension(200, 70));
		final SymbolTableNotifier stab = new SymbolTableNotifier();
		stab.addObserver( (CitrinObserver)display);
		// display.setDimension( );

		// -------------------------------------------------------------------
		//

		//Create and set up the window.
		JFrame frame = new JFrame("DataDisplay Driver");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Create and set up the content pane.
		display.setOpaque(true); //content panes must be opaque
		frame.setContentPane(display);

		//Display the window.
		frame.pack();
		frame.setVisible(true);
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(stab, display);
            }
        });
		

		// -------------------------------------------------------------------
		//
		// The changes made below to the symbol table should be automatically
		// reflected on data display.
		//
		SymbolLocation loc = new SymbolLocation(1,1);
		var_type v = new var_type();
		v.var_name = "first_variable";
		v.value = 1;
		Symbol s = new Symbol(loc, v);

		// First Push to First Scope
		Thread.sleep(1000);
		stab.pushSymbol(s);

		// Second Push to Firs Scope
		Thread.sleep(1000);
		v.var_name = "second_variable";
		v.value = 2;
		s = new Symbol(loc, v);
		stab.pushSymbol(s);

		// Third Push to Second Scope
		Thread.sleep(1000);
		stab.pushFuncScope("Foo");
		v.var_name = "third variable";
		v.value = 2;
		s = new Symbol(loc, v);
		stab.pushSymbol(s);

		// frame.revalidate();
		// frame.repaint();
		display.revalidate();
		display.repaint();
		frame.pack(); 

	}


}
