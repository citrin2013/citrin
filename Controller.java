import java.awt.EventQueue;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;


public class Controller {

	private Console console;
	private String interpretation = "";
	private boolean interpreting = false;
	private Interpreter interpreter = null;
	
	public Controller(Console c){
		console = c;
	}
	
	public synchronized boolean checkContinueRun(){
		return true;
	}
	
	public synchronized void consoleOut(String message){
		interpretation+=message;

		 EventQueue.invokeLater(new Runnable() { 
		   @Override
		   public void run() {
			    Document doc = null;
			    doc = new PlainDocument();
			    try { 
			      doc.insertString(0, interpretation, null);
			    } catch ( BadLocationException badloc ) {
			      System.out.println( "Bad offset requested : " + badloc.offsetRequested() );
			    }
			    console.setDocument(doc);
		   }
		 });
	}
	
	public synchronized boolean isInterpreting(){
		return interpreting;
	}
	
	public synchronized void addInterpreter(Interpreter i){
		interpreter = i;
	}
	
	public synchronized void setInterpretingDone(){
		interpreting = false;
		interpreter = null;
	}
	
	public synchronized void setInterpretingStart(){
		interpreting = true;
	}
	
	public synchronized void clearConsole(){
		interpretation = "";

	    EventQueue.invokeLater(new Runnable() { 
	    	  @Override
	    	  public void run() {
	    		    Document doc = null;
	    		    doc = new PlainDocument();
	    		    try { 
	    		      doc.insertString(0, interpretation, null);
	    		    } catch ( BadLocationException badloc ) {
	    		      System.out.println( "Bad offset requested : " + badloc.offsetRequested() );
	    		    }
	    		    console.setDocument(doc);
	    	  }
	    	});
	}
	
	public void addSteps(int s){
		interpreter.addSteps(s);
	}
	
	public void runToBreak(){
		interpreter.runToBreak();
	}
	
	public synchronized void stopInterpreter(){
		if(interpreting){
			interpreter.stop();
		}	
	}
}
