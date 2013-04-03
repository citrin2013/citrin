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
import javax.swing.JTable;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

public class DataDisplay extends JPanel implements CitrinObserver { // implements Observer {

	boolean DEBUG = true;

	// Data for Table
	private String[] columnNames = {
		"name",
		"value"
	};
	private Object[][] data = { 
		// {},
		// { "a", 1 }, 
	};

	// Table
	// JTable table = new JTable(); 
	List<JTable> tables = new ArrayList<JTable>();

	// -----------------------------------------------------------------------
	// Constructor

	/** Instantiate a table within a pane with the specified dimension.
	 */
	public DataDisplay(Dimension dim)
	{

		JTable table = new JTable();
		table.setModel( new DefaultTableModel(data,columnNames) );
		tables.add( table );

		JPanel jpanel = new JPanel( new GridLayout(1,0) );

		// config
		table.setPreferredScrollableViewportSize(dim);
        table.setFillsViewportHeight(true);

		//
		// debug
		//
		// Print contents of the table on click
		//

		// for (Object tab : tables ) {
		// 	tab = (JTable) tab;
		// 	if (DEBUG) {
		// 		tab.addMouseListener(new MouseAdapter() {
		// 			public void mouseClicked(MouseEvent e) {
		// 				printDebugData(tab);
		// 			}
		// 		});
		// 	}
		// }

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        add(scrollPane);

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
				System.out.println("TODO : DataDiaplay::update() on scopePushedInParallel");
			}
			else if ( e == SymbolTableEvent.scopePushedAsChild ) {
				System.out.println("TODO : DataDiaplay::update() on scopePushedAsChild");
			}
			else if ( e == SymbolTableEvent.scopePopped ) {
				System.out.println("TODO : DataDiaplay::update() on scopePopped");
			}
			else if ( e == SymbolTableEvent.symbolInserted ) {
				Symbol s = stab.getInsertedSymbol();
				SymbolLocation loc = s.getLocation();
				var_type v = s.getData();

				final Object[][] row = { { v.var_name, v.value} }; 
				JTable table = tables.get( tables.size() - 1 );
				DefaultTableModel tmodel = (DefaultTableModel) table.getModel();
				tmodel.addRow(row[0]);

			}
			else if ( e == SymbolTableEvent.symbolAssignedNewValue) {
				System.out.println("TODO : DataDiaplay::update() on symbolAssignedNewValue");
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

		//Create and set up the window.
		JFrame frame = new JFrame("DataDisplay Driver");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Create and set up the content pane.
		dd.setOpaque(true); //content panes must be opaque
		frame.setContentPane(dd);

		//Display the window.
		frame.pack();
		frame.setVisible(true);
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

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(stab, display);
            }
        });

		//
		// The changes made below to the symbol table should be automatically
		// reflected on data display.
		//
		SymbolLocation loc = new SymbolLocation(1,1);
		var_type v = new var_type();
		v.var_name = "first_variable";
		v.value = 1;
		Symbol s = new Symbol(loc, v);

		// sleep
		Thread.sleep(1000);

		// First push
		stab.pushSymbol(s);

		// sleep
		Thread.sleep(1000);

		// Second push
		v.var_name = "second_variable";
		v.value = 2;
		s = new Symbol(loc, v);
		stab.pushSymbol(s);


	}


}
