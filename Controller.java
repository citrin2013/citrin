import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;


public class Controller {

	int highlightedIndex1,highlightedIndex2;
	
	private Console console;
	private String interpretation = "";
	private boolean interpreting = false;
	private Interpreter interpreter = null;
	private guiPanel guiPanel;
	
	public Controller(Console c, guiPanel g){
		console = c;
		guiPanel = g;
	}
	
	public synchronized boolean checkContinueRun(){
		return true;
	}
	
	public synchronized void consoleOut(String message){
		 if(console!=null){
			 interpretation+=message;
			 final String messageCopy = new String(message);
			 EventQueue.invokeLater(new Runnable() { 
				 @Override
				 public void run() {
					 console.append(messageCopy);
					 console.setCaretPosition(console.getDocument().getLength());
					 console.requestFocusInWindow();
				 }
			 });
		 }
		 else{
			 System.out.println(message);
		 }
			 
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

		if(guiPanel!=null){
			SwingUtilities.invokeLater(new Runnable(){
				@Override 
				public void run() {
					guiPanel.clearHighlight();
				}
			});
		}
		
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
	    		    console.setDocument(doc);
	    	  }
	    	});
	}
	
	/*public void setActiveSectionOfCode(int index1, int index2){
		final int i1 = index1, i2 = index2;
		if(guiPanel!=null){
			SwingUtilities.invokeLater(new Runnable(){
				@Override 
				public void run() {
					guiPanel.clearMostRecentHighlight();
					guiPanel.HighlightSection(i1, i2);
					guiPanel.centerOnPosition(i1);
				}
			});
		}
	}*/
	
	public void setActiveLineOfCode(int lineNum, final Color color){
		final int line = lineNum;
		if(guiPanel!=null){
			SwingUtilities.invokeLater(new Runnable(){
				@Override 
				public void run() {
					guiPanel.clearMostRecentHighlight();
					guiPanel.HighlightLine(line, color);
					guiPanel.centerOnLine(line);
				}
			});
		}
	}
	
	public void highlightConditional(final int lineNum, final boolean isTrue){
		final int line = lineNum;
		if(guiPanel!=null){
			SwingUtilities.invokeLater(new Runnable(){
				@Override 
				public void run() {
					guiPanel.clearMostRecentHighlight();
					if(isTrue==true)
						guiPanel.HighlightLine(line, Color.GREEN);
					else
						guiPanel.HighlightLine(line, Color.RED);
					guiPanel.centerOnLine(line);
				}
			});
		}
	}
	
	public void addActiveLineOfCode(int lineNum, final Color color){
		final int line = lineNum;
		if(guiPanel!=null){
			SwingUtilities.invokeLater(new Runnable(){
				@Override 
				public void run() {
					guiPanel.HighlightLine(line, color);
					guiPanel.centerOnLine(line);
				}
			});
		}
	}
	
	public void removeLastHighlightAndSetFocus(int lineNum){
		final int line = lineNum;
		if(guiPanel!=null){
			SwingUtilities.invokeLater(new Runnable(){
				@Override 
				public void run() {
					guiPanel.clearMostRecentHighlight();
					guiPanel.centerOnLine(line);
				}
			});
		}
	}
	
	public void addSteps(int s){
		interpreter.addSteps(s);
	}
	
	public void continueRun(){
		interpreter.continueRun();
	}
	
	public void runToBreak(int line){
		interpreter.runToBreak(line);
	}
	
	public synchronized void stopInterpreter(){
		if(interpreting){
			if(guiPanel!=null){
				SwingUtilities.invokeLater(new Runnable(){
					@Override 
					public void run() {
						guiPanel.clearHighlight();
					}
				});
			}
			
			interpreter.stop();
		}	
	}
}
