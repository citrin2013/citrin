import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.io.*;


public class Lexer {

	private final int IGNORE_CHAR = -1;

	//public String token = null;
	private int lastIndex = 0;
	//private token_type type = token_type.TEMP;
	private char prog[] = null;
	public int index = 0;
	ArrayList<Integer> listOfEndlines;
	//public keyword tok = null;

	private String operators[] = { /* Commands must be entered in lowercase in this table?*/
			"new",
			"delete",
			"not_eq",
			"and",
			"or",
			"compl",
			"bitand",
			"bitor",
			"xor"
			};

	public commands table[] = { /* Commands must be entered in lowercase in this table?*/
			new commands("if", keyword.IF), 
			new commands("else", keyword.ELSE),
			new commands("for", keyword.FOR),
			new commands("do", keyword.DO),
			new commands("while", keyword.WHILE),
			new commands("char", keyword.CHAR),
			new commands("short", keyword.SHORT),
			new commands("int", keyword.INT),
			new commands("return", keyword.RETURN),
			new commands("end", keyword.END),
			new commands("float", keyword.FLOAT),
			new commands("bool", keyword.BOOL),
			new commands("double", keyword.DOUBLE),
			new commands("void", keyword.VOID),
			new commands("", keyword.END) /* mark end of table */
			};

	synchronized void loadSourceFile( String fileName ){

		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			StringBuffer str = new StringBuffer();
			String line = br.readLine();
			while (line != null)
			{
				str.append(line);
				str.append("\n");
				line = br.readLine();
			}
			br.close();
			prog = str.toString().toCharArray();
		} catch (IOException e){
			e.printStackTrace();
		}

		listOfEndlines = new ArrayList<Integer>(); 
		for(int i=0;i<prog.length;i++){
			if(prog[i] =='\n' || prog[i] =='\r')
				listOfEndlines.add(i);
		}

	}


	/* Get a token. */
	//TODO make sure wont go outside boundary
	//TODO CHANGE so that chars have '' around them and strings ""
	public Token get_token() throws SyntaxError {

		//token_type type = token_type.TEMP; //replaced with global
		Token token = new Token();

		int temp;

		lastIndex = index;

		/* skip over white space and comments */
		boolean done = false;
		while(!done){
			done = true;
			//skip white space
			while(index<prog.length && Character.isWhitespace(prog[index])){
				index++;			
			}

			// skip comments
			if( index+1 < prog.length && prog[index] == '/' && prog[index+1] == '*'){
				done = false;
				index += 2;
				while (index+1 < prog.length && (prog[index]!='*' || prog[index+1]!='/') )
					index++;
				index+=2;
			}

		}

		if(index >= prog.length) { /* end of file */
			token.value = "";
			token.key = keyword.FINISHED;
			token.type = token_type.DELIMITER;
			return (token);
		}

		if( "{}".indexOf( prog[index] )>=0 ) { /* block delimiters */
			token.value = Character.toString(prog[index]);
			index++;
			token.type = token_type.BLOCK;
			return (token);
		}

		if( "!<>=+-*^/%&|".indexOf(prog[index])>=0 ) { /* is an operator */
			token.type = token_type.OPERATOR;
			StringBuffer buf = new StringBuffer();
			buf.append(prog[index]);
			index++;
			//check for end of file
			if(index>=prog.length){
				token.value = buf.toString();
				return token;
			}
			switch(buf.charAt(0)) {

			case '<': 
				if(prog[index] == '<'){
					buf.append('<');
					index++;
				}
				break;
			case '>': 
				if(prog[index] == '>'){
					buf.append('>');
					index++;
				}  
				break;
			case '+': 
				if(prog[index] == '+'){
					buf.append('+');
					index++;
					token.value = buf.toString();
					return token;
				}
				break;
			case '-': 
				if(prog[index] == '-'){
					buf.append('-');
					index++;
					token.value = buf.toString();
					return token;
				}
				else if(prog[index] == '>'){
					buf.append('>');
					index++;
					if(prog[index] == '*'){
						buf.append('*');
						index++;
					}
					token.value = buf.toString();
					return token;
				}
				break;
			case '&': 
				if(prog[index] == '&'){
					buf.append('&');
					index++;
					token.value = buf.toString();
					return token;
				}  
				break;
			}
			//check for end of file
			if(index>=prog.length){
				token.value = buf.toString();
				return token;
			}

			if(prog[index] == '='){
				buf.append('=');
				index++;
			}
			token.value = buf.toString();
			return (token);	
		}

		if( ",:.~?".indexOf(prog[index])>=0 ){ /* operator */
			StringBuffer buf = new StringBuffer();
			buf.append(prog[index]);
			index++;

			if(index>=prog.length){
				token.value = buf.toString();
				return token;
			}
			if(buf.charAt(0) == '.'){
				if(prog[index] == '*')
					buf.append('*');
				index++;
			}

			token.value = buf.toString();
			token.type = token_type.OPERATOR;
			return (token);
			}

		if(prog[index]=='"') { /* quoted string */
			index++;
			StringBuffer buf = new StringBuffer();

			buf.append('"');		//start with quote
			while(index<prog.length && prog[index]!='"' && prog[index]!='\r' && prog[index]!='\n'){

			if(prog[index]=='\\'){ //check for escape char
				index++;
				temp = getEscapeCharacter();
				if(temp!=IGNORE_CHAR)
					buf.append((char)temp);
			}
			else{
				buf.append(prog[index]);
			}
			index++;
			}
			if( index>=prog.length || prog[index] != '"' ) 
			sntx_err("Missing terminating \" ");//call syntax error

			buf.append('"'); //add quote to finish string
			index++;
			token.value = buf.toString();
			token.type = token_type.STRING;
			return (token);
		}

		//TODO: multi character constants have only charAt(0) used, this is handled by atom()...
		if(prog[index]=='\'') { /* character bounded by ' */
			index++;
			StringBuffer buf = new StringBuffer();
			buf.append('\''); //start with single quote
			while(index<prog.length && prog[index]!='\'' && prog[index]!='\r' && prog[index]!='\n'){

			if(prog[index]=='\\'){ //check for escape char
				index++;
				temp = getEscapeCharacter();
				if(temp!=IGNORE_CHAR)
					buf.append((char)temp);
			}
			else{
				buf.append(prog[index]);
			}
				index++;
			}
			if(index >= prog.length || prog[index] != '\'' ) 
				sntx_err("Missing terminating ' ");//call syntax error

			buf.append('\''); //finish with single quote

			index++;
			token.value = buf.toString();
			token.type = token_type.CHAR;
			return (token);
			}

		if( ";'()".indexOf(prog[index])>=0 ){ /* delimiter */
			token.value = Character.toString(prog[index]);
			index++;
			token.type = token_type.DELIMITER;
			return (token);
			}


		if(Character.isDigit(prog[index]) || 
				(prog[index]=='.' && Character.isDigit(prog[index+1]) ) ) { /* number */
			int numDecimals = 0;
			StringBuffer buf = new StringBuffer();
			while( index<prog.length && Character.isDigit(prog[index]) || prog[index]=='.' ){
			buf.append(prog[index]);
			if(prog[index]=='.')
				numDecimals++;
			if(numDecimals > 1) sntx_err("Too many decimals in number");
			index++;
			}
			token.value = buf.toString();
			token.type = token_type.NUMBER;
			return (token);
		}

		if(Character.isLetter(prog[index])) { /* var or command */
			StringBuffer buf = new StringBuffer();
			while(Character.isLetterOrDigit(prog[index]) || prog[index] == '_'){
			buf.append(prog[index]);
			index++;
			}
			token.value = buf.toString();
			token.type = token_type.TEMP;
		}


		/* see if a string is an operator*/
		if(token.type==token_type.TEMP) {
			if(isOperatorString(token.value)){
				token.type = token_type.OPERATOR;
				return token;
			}
			}


		/* see if a string is a command or a variable */
		if(token.type==token_type.TEMP) {
			token.key = look_up(token.value); /* convert to internal rep */
			if(token.key != null) token.type = token_type.KEYWORD; /* is a keyword */
			else token.type = token_type.IDENTIFIER;
		}

		// if token is still null something is wrong
		if(token.value==null){
			index++;
			sntx_err("Unkown token: " + prog[index-1]);
		}
		return token;

	}

	//function to handle escape secuences, 
	//index should be pointing to the character after the '\'
	//http://en.cppreference.com/w/cpp/language/escape
	private int getEscapeCharacter() throws SyntaxError{
		if(index>=prog.length){
			sntx_err("incomplete string or character");
			return IGNORE_CHAR;
		}
		switch(prog[index]){
		case '\\': return '\\';
		case '\'': return '\'';
		case '\"': return '\"';
		case 'a': return (char) 7;
		case 'b': return '\b';
		case 'f': return '\f';
		case 'n': return '\n';
		case '?': return '?'; 

		case 'N':
		case 'u':
		case 'U': sntx_err("CITRIN does not support unicode"); return IGNORE_CHAR;

		case 'r': return '\r';
		case 't': return '\t';
		case 'v': return (char) 13;

		case '\n':
		case '\r': return IGNORE_CHAR;


		default: 
			if(Character.isDigit(prog[index])){
				//octal escape
				int j=0;
				StringBuffer octalStr = new StringBuffer(); 

				//form string of digits
				for(j=0; (j<3) && (index+j<prog.length); ++j ){
					if(!Character.isDigit(prog[index+j]))
						break;
					else
						octalStr.append(prog[index+j]);
				}

				String oct = octalStr.toString();

				//check for ints that aren't octal
				if(oct.indexOf('9') >= 0 || oct.indexOf('8') >= 0){
					sntx_err("\number must be an octal number");
				}

				//parse string to int
				int octal = Integer.parseInt(oct.toString(), 8);
				if(octal>256)
					sntx_err("Octal char out of range");
				index += j-1;
				return (char) octal;
			}

			else if(prog[index]=='x'){
				//hexadecimal escape
				index++;
				int j=0;
				StringBuffer hexStr = new StringBuffer(); 
				char temp;

				//form string of digits
				for(j=0; (j<2) && (index+j<prog.length); ++j ){
					temp = Character.toLowerCase(prog[index+j]);
					if(!(Character.isDigit(temp) || ('a'<=temp && 'f'>=temp) ) )
						break;
					else
						hexStr.append(temp);
				}

				if(j == 0){
					sntx_err("\\x used with no hex digits");
					return IGNORE_CHAR;
				}
				//parse string to int
				int hex = Integer.parseInt(hexStr.toString(), 16);
				index += j-1;
				return (char) hex;
			}

			else{
				sntx_err("Unrecognized escape character");
			}
		}

		return IGNORE_CHAR;
	}

	private keyword look_up(String key) {
		int i=-1;
		key = new String(key.toLowerCase());
		for(i = 0; i<table.length ;i++){
			if(key.equals(table[i].command)) return table[i].tok;
		}
		return null; // unkown command
	}

	private boolean isOperatorString(String key) {
		int i=-1;
		key = new String(key.toLowerCase());
		for(i = 0; i<operators.length ;i++){
			if(key.equals(operators[i])) return true;
		}
		return false; // unkown command
	}

	public void sntx_err(String s) throws SyntaxError {
		throw new SyntaxError(s,getLineNum(),getColumnNum());

	}

	public void putback(){
		index = lastIndex;
	}

	public synchronized int getLineNum(){
		int n=0;
		while(n<listOfEndlines.size() && listOfEndlines.get(n)<=index){
			n++;
		}
		n--; //to get n before index

		return n+2; //return +1 since start at line 1, and +1 since previous endline is on the line before index
	}

	public synchronized int getColumnNum(){
		int n=0;
		while(n<listOfEndlines.size() && listOfEndlines.get(n)<=index){
			n++;
		}
		n--;

		return index-listOfEndlines.get(n);

	}

	public class commands {
		public commands(String c, keyword t){
			command = c;
			tok = t;
		}
		public String command;
		public keyword tok;
	}

	public class Token {
		String value = null;
		token_type type = null;
		keyword key = null;

		//TODO temporary override to remind myself to call token.value.equals, not token.equals
		@Override
		public boolean equals(Object other){
			System.out.println("you called token.equals, instead of token.value.equals");
			int a = 1/0;
			return a==0;
		}

		@Deprecated
		// Just for SymbolTableDriver for SymbolTable demo.
		// This function should never be used elsewhere.
		public SymbolData toSymbol()
		{
			// System.out.println("this.key : "+ this.key);

			SymbolData sym = null;
			if ( this.key == keyword.BOOL ) {
				sym = new Bool( true);
			}
			else if (this.key == keyword.INT) {
				sym = new Int( 1);
			}
			else if (this.key == keyword.CHAR) {
				sym = new Char( 'a' );
			}
			else if (this.key == keyword.SHORT) {
				sym = new Short( (short)1 );
			}
			else if (this.key == keyword.FLOAT) {
				sym = new Float( (float)1.0 );
			}
			else if (this.key == keyword.DOUBLE) {
				sym = new Double( 2.0 );
			}
			else {
				sym = new DebugSymbol( "DEBUG" ) ;
			}
			return sym;
		}

		public void print(PrintStream ps) 
		{
			ps.print("<");
			ps.print(value+",");
			ps.print(type+",");
			ps.print(key);
			ps.print(">");
			ps.println();
		}
		
	}
