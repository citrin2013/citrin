import javax.swing.*;
import javax.swing.text.*;

public class Console extends JTextArea {
	Document doc; 

	public Console() { 
		super(); 
		this.setFont(UIManager.getDefaults().getFont("TextField.font"));
	}

	public Console(Document doc) { 
		this.doc = doc;
	}

}
