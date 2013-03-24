import java.lang.Double;


//TODO: ST stb = (sta, sta), stc; would call comma op but ST stb = sta, sta, stc; would repeatedly declare. 
//Same issue with functions
//maybe eval_exp needs to either enter after , operator or before it based on context...

public class ExpressionEvaluator {

	private Interpreter interpreter = null;
	private Lexer lexer = null;	
	private Lexer.Token token;
	private boolean checkOnly = false;

	public static final String EQ = "==";
	public static final String LT = "<";	
	public static final String LE = "<=";
	public static final String GT = ">";
	public static final String GE = ">=";
	public static final String NE = "!=";

	//private constructor to prevent using default constructer
	@SuppressWarnings("unused")
	private ExpressionEvaluator(){
	}

	public ExpressionEvaluator(Interpreter i){
		interpreter = i;
		lexer = interpreter.lexer;
	}

	//entry point into parser
	public var_type eval_exp(boolean commasAreDelimiters)  throws StopException, SyntaxError {
	  checkOnly = false;
	  var_type value = null;
	  token = lexer.get_token();
	  if(token.value==null) {
		interpreter.sntx_err("No expression");
		return new var_type();
	  }
	  if(commasAreDelimiters)
		  value = eval_exp1();
	  else
		  value = eval_exp0();
	  lexer.putback(); /* return last token read to input stream */
	  return value;
	}

	// process comma operator
	private var_type eval_exp0()  throws StopException, SyntaxError {
	  var_type result;
	  result = eval_exp1();;
	  String op = token.value;
	  while(op.equals(",")){
		  token = lexer.get_token();
		  result = eval_exp1();
		  op = token.value;
	  }
	  return result;
	}


	//process throw operator, not implemented
	private var_type eval_exp1() throws StopException, SyntaxError {
		var_type result;
		result = eval_exp2();
		return result;
	}

	// process an assignment expression
	private var_type eval_exp2()  throws StopException, SyntaxError {
	  var_type result;

	  result = eval_exp3();
	  String op = token.value;
	  if(!(op.equals("=") || op.equals("+=") || op.equals("-=") || op.equals("*=") || op.equals("/=")
			  || op.equals("%=") || op.equals("<<=") || op.equals(">>=") || op.equals("&=") || op.equals("^=")
			  || op.equals("|=") ))
		  return result;

	  //otherwise it is an assignment expression
	  token = lexer.get_token();
	  var_type rhs = eval_exp2();

	  if(!result.lvalue){
		  interpreter.sntx_err("Left hand side of "+op+" must be an lvalue");
	  }
	  if(checkOnly){
		  switch(op.charAt(0)){
		  case '%':
		  case '<':
		  case '>':
		  case '&':
		  case '^':
		  case '|':
		  keyword type = var_type.getPromotionForBinaryOp(result.v_type, rhs.v_type);
		  if(type == keyword.DOUBLE || type == keyword.FLOAT)
			  interpreter.sntx_err("Operator "+op+" can only take integral types as operands");
		  }
		  return result;
	  }


	  switch(op.charAt(0)){
	  case '=':
		  result.assignVal(rhs);
		  break;
	  case '+':
		  result.assignVal(result.add(rhs));
		  break;
	  case '-':
		  result.assignVal(result.sub(rhs));
		  break;
	  case '*':
		  result.assignVal(result.mul(rhs));
		  break;
	  case '/':
		  result.assignVal(result.div(rhs));
		  break;
	  case '%':
		  result.assignVal(result.mod(rhs));
		  break;
	  case '<':
		  result.assignVal(result.bitBinaryOp("<<", rhs));
		  break;
	  case '>':
		  result.assignVal(result.bitBinaryOp(">>", rhs));
		  break;
	  case '&':
		  result.assignVal(result.bitBinaryOp("&", rhs));
		  break;
	  case '^':
		  result.assignVal(result.bitBinaryOp("^", rhs));
		  break;
	  case '|':
		  result.assignVal(result.bitBinaryOp("|", rhs));
		  break;
	  }
	  interpreter.printVarVal(result);

	  return result;
	}


	// process ternary operator (precedence 15 right to left)
	private var_type eval_exp3()  throws StopException, SyntaxError {
	  var_type conditional, result;
	  String op;

	  result = eval_exp4();
	  op = token.value;
	  if(!op.equals("?"))
		  return result;

	  //set conditional
	  conditional = new var_type();
	  conditional.v_type = keyword.BOOL;
	  conditional.assignVal(result);
	  result = new var_type();


	  //Read in next token
	  token = lexer.get_token();

	  //Save context
	  boolean tempCheck = checkOnly;
	  Lexer.Token tempToken = token.clone();
	  int tempIndex = lexer.index;

	  //Find return type of ternary operator-START
	  //Get type of both sides
	  checkOnly = true;
	  var_type ifTrue = eval_exp0();
	  op = token.value;
	  if(!op.equals(":"))
		  interpreter.sntx_err("Expecting : for ternary operator ?:");
	  token = lexer.get_token();

	  //save location/token of beginning of third term
	  int indexBeforeTerm3 = lexer.index;
	  Lexer.Token tokenBeforeTerm3 = token.clone();

	  //eval term 3
	  var_type ifFalse = eval_exp2(); //exp2 since right side can be an assignment expression

	  // if both are primitive types
	  if(ifTrue.isNumber() && ifFalse.isNumber() ){
		  result.v_type = var_type.getPromotionForBinaryOp(ifTrue.v_type, ifFalse.v_type);
		  result.lvalue = ifTrue.lvalue && ifFalse.lvalue;
	  }
	  else{
		  //check if either can be converted to other. result must have same type.
		  interpreter.sntx_err("Ternary operator has not had classes implemented yet");
	  }

	  // if only checking type we are done
	  if(tempCheck){
		  checkOnly = tempCheck;
		  return result;		  
	  }

	  //Find return type of ternary operator-END

	  if(conditional.value.intValue()==1){
		  //Restore context
		  lexer.index = tempIndex;
		  token = tempToken.clone();
		  checkOnly = tempCheck;

		  //eval middle term
		  ifTrue = eval_exp0(); //can be arbitrary middle term

		  //checkonly on third term
		  token = lexer.get_token();
		  checkOnly = true;
		  ifFalse=eval_exp3();
		  checkOnly = tempCheck;

		  result.assignVal(ifTrue);
	  }
	  else{
		  //Restore context to before 3rd term
		  lexer.index = indexBeforeTerm3;
		  token = tokenBeforeTerm3.clone();
		  ifFalse = eval_exp3();
		  result.assignVal(ifFalse);
	  }

	  return result;
	}

	// process logical OR (precedence 14 left to right)
	private var_type eval_exp4()  throws StopException, SyntaxError {
	  var_type result;
	  var_type partial_value;
	  String op;

	  result = eval_exp5();
	  op = token.value;
	  var_type bool1 = new var_type();
	  bool1.v_type = keyword.BOOL;

	  //if not primitive or right hand side not primitive need to do something

	  while(op.equals("||")){
			result.v_type = keyword.BOOL;
			token = lexer.get_token();
			//save context
			boolean tempCheck = checkOnly;
			Lexer.Token tempToken = token.clone();
			checkOnly = true;
			int tempIndex = lexer.index;

			partial_value = eval_exp5();
			checkOnly = tempCheck;

			if(checkOnly){
				result = result.getReturnTypeFromBinaryOp(op, partial_value);
			}
			else{
				bool1.assignVal(result);
				if(bool1.value.intValue()==1){
					result.value = 1;
				}
				else{
					lexer.index = tempIndex;
					token = tempToken.clone();
					partial_value = eval_exp5();
					bool1.assignVal(partial_value);
					result.value = bool1.value;
				}
			}

			op = token.value;
		  }


	  return result;
	}


	// process logical AND (precedence 13 left to right)
	private var_type eval_exp5()  throws StopException, SyntaxError {
	  var_type result;
	  var_type partial_value;
	  String op;

	  result = eval_exp6();
	  op = token.value;
	  var_type bool1 = new var_type();
	  bool1.v_type = keyword.BOOL;

	  //if not primitive or right hand side not primitive need to do something

	  while(op.equals("&&")){
			result.v_type = keyword.BOOL;
			token = lexer.get_token();
			//save context
			boolean tempCheck = checkOnly;
			Lexer.Token tempToken = token.clone();
			checkOnly = true;
			int tempIndex = lexer.index;

			partial_value = eval_exp6();
			checkOnly = tempCheck;

			if(checkOnly){
				result = result.getReturnTypeFromBinaryOp(op, partial_value);
			}
			else{
				bool1.assignVal(result);
				if(bool1.value.intValue()==0){
					result.value = 0;
				}
				else{
					lexer.index = tempIndex;
					token = tempToken.clone();
					partial_value = eval_exp6();
					bool1.assignVal(partial_value);
					result.value = bool1.value;
				}
			}

			op = token.value;
		  }


	  return result;
	}

	// process bitwise or (precedence 12 left to right)
	private var_type eval_exp6()  throws StopException, SyntaxError {
	  var_type result;
	  var_type partial_value;
	  String op;

	  result = eval_exp7();
	  op = token.value;

	  while(op.equals("|")){
			token = lexer.get_token();
			partial_value = eval_exp7();
			if(checkOnly){
				result = result.getReturnTypeFromBinaryOp(op, partial_value);
			}
			else{
				result = result.bitBinaryOp(op, partial_value);
			}

			op = token.value;
		  }


	  return result;
	}


	// process bitwise xor (precedence 11 left to right)
	private var_type eval_exp7()  throws StopException, SyntaxError {
	  var_type result;
	  var_type partial_value;
	  String op;

	  result = eval_exp8();
	  op = token.value;

	  while(op.equals("^")){
			token = lexer.get_token();
			partial_value = eval_exp8();
			if(checkOnly){
				result = result.getReturnTypeFromBinaryOp(op, partial_value);
			}
			else{
				result = result.bitBinaryOp(op, partial_value);				
			}

			op = token.value;
		  }


	  return result;
	}

	// process bitwise and (precedence 10 left to right)
	private var_type eval_exp8()  throws StopException, SyntaxError {
	  var_type result;
	  var_type partial_value;
	  String op;

	  result = eval_exp9();
	  op = token.value;

	  while(op.equals("&")){
			token = lexer.get_token();
			partial_value = eval_exp9();
			if(checkOnly){
				result = result.getReturnTypeFromBinaryOp(op, partial_value);
			}
			else{
				result = result.bitBinaryOp(op, partial_value);	
			}

			op = token.value;
		  }

	  return result;
	}

	// process equality comparisons (precedence 9 left to right)
	private var_type eval_exp9()  throws StopException, SyntaxError {
	  var_type result;
	  var_type partial_value;
	  String op;

	  result = eval_exp10();
	  op = token.value;

	  while(op.equals("==") || op.equals("!=")) {

		  token = lexer.get_token();
		  partial_value = eval_exp10();
		  if(checkOnly){
			  result.v_type = keyword.BOOL;
		  }
		  else{
			  result = result.relationalOperator(partial_value, op);			  
		  }

		  op = token.value;
	  }

	  return result;
	}

	// process comparisons (precedence 8 left to right)
	private var_type eval_exp10()  throws StopException, SyntaxError {
	  var_type result;
	  var_type partial_value;
	  String op;

	  result = eval_exp11();
	  op = token.value;
	  while( op.equals("<") || op.equals("<=") || op.equals(">") || op.equals(">=") ){

		  token = lexer.get_token();
		  partial_value = eval_exp11();
		  if(checkOnly){
			  result.v_type = keyword.BOOL;
		  }
		  else{
			  result = result.relationalOperator(partial_value, op);			  
		  }
		  op = token.value;

	  }

	  return result;
	}


	// bit shifts (precedence 7 left to right)
	// TODO: need a setting for shifts or stream operations
	private var_type eval_exp11()  throws StopException, SyntaxError {
	  var_type result, partial_value;
	  String op = "";

	  result = eval_exp12();
	  op = token.value;
	  while(op.equals(">>") || op.equals("<<") ){
		token = lexer.get_token();
		partial_value = eval_exp12();
		if(checkOnly){
			result = result.getReturnTypeFromBinaryOp(op, partial_value);
			return result;
		}
		result = result.bitBinaryOp(op, partial_value);

		op = token.value;
	  }
	  return result;
	}


	// add or subtract two terms (precedence 6 left to right)
	private var_type eval_exp12()  throws StopException, SyntaxError {
	  var_type result, partial_value;
	  String op = "";

	  result = eval_exp13();
	  op = token.value;
	  while(op.equals("+") || op.equals("-") ){
		token = lexer.get_token();
		partial_value = eval_exp13();
		if(op.equals("-")){
			if(checkOnly){
				result = result.getReturnTypeFromBinaryOp(op, partial_value);
			}
			else{
				result = result.sub(partial_value);					
			}
		}
		if(op.equals("+")){
			if(checkOnly){
				result = result.getReturnTypeFromBinaryOp(op, partial_value);
			}
			else{
				result = result.add(partial_value);	
			}
		}
		op = token.value;
	  }
	  return result;
	}


	// multiply or divide two factors (precedence 5 left to right)
	private var_type eval_exp13()  throws StopException, SyntaxError {
	  var_type result, partial_value;
	  String op;

	  result = eval_exp14 ();
	  op = token.value;
	  //TODO cant use char at here
	  while(op.equals("*") || op.equals("/") || op.equals("%") ){		
		token = lexer.get_token();
		partial_value = eval_exp14();
		if(checkOnly){
			result = result.getReturnTypeFromBinaryOp(op, partial_value);
			return result;
		}
		if(op.equals("*"))
			result = result.mul(partial_value);
		else if(op.equals("/"))
			result = result.div(partial_value);
		else if(op.equals("%"))
			result = result.mod(partial_value);

		op = token.value;
	  }

	  return result;
	}


	// evaluate precedence 4 left to right
	private var_type eval_exp14()  throws StopException, SyntaxError {
	  var_type result, partial_value;
	  String op = "";

	  result = eval_exp15();
	  op = token.value;
	  while(op.equals(".*") || op.equals("->*") ){
		interpreter.sntx_err("operator "+op+" not implemented yet");
		token = lexer.get_token();
		op = token.value;
	  }
	  return result;
	}

	//evaluate precedence 3 right to left
	private var_type eval_exp15()  throws StopException, SyntaxError {
	  var_type result;

	  //TODO cant use charAt here
	  String op = null;
	  if(token.value.equals("+") || token.value.equals("-") || token.value.equals("++") || token.value.equals("--")
			  || token.value.equals("!") || token.value.equals("~") || token.value.equals("*") 
			  || token.value.equals("&") || token.value.equals("new") || token.value.equals("delete")){
		op = token.value;
		token = lexer.get_token();
	  }

	  if(op!=null)
		  result = eval_exp15(); //if there was an op need to recursively evaluate since associativity is right to left
	  else
		  result = eval_exp16();

	  if(op==null) return result;

	  //if(checkOnly) only need to simplify work from overloaded operators

	  if( op.equals("-") ){
		  result = result.unaryMinus();
	  }
	  else if( op.equals("+") ){
		  result = result.unaryPlus();
	  }
	  else if( op.equals("++") ){ 
		  if(!result.lvalue)
			  interpreter.sntx_err("Argument of ++ operator must be an lvalue");
		  result = result.prefixIncrement();
		  if(!checkOnly)
			  interpreter.printVarVal(result);
	  }
	  else if( op.equals("--") ){
		  if(!result.lvalue)
			  interpreter.sntx_err("Argument of ++ operator must be an lvalue");
		  result = result.prefixDecrement();
		  if(!checkOnly)
			  interpreter.printVarVal(result);
	  }
	  else if( op.equals("!") ){
		  result = result.logicalNot();
	  }
	  else if( op.equals("~") ){
		  result = result.bitwiseNot();
	  }
	  else if( op.equals("*") ){
		  interpreter.sntx_err("pointers have not been implemented");
	  }
	  else if( op.equals("&") ){
		  //check if lvalue, check to make sure it has not been cast.
		  interpreter.sntx_err("Address of operator has not been implemented");
	  }
	  else if( op.equals("new") ){
		  interpreter.sntx_err("pointers have not been implemented");
	  }
	  else if( op.equals("delete") ){
		  interpreter.sntx_err("pointers have not been implemented");
	  }

	  return result;
	}

	//evaluate precedence 2 left to right
	//TODO: function calls should be in this precedence rather than in atom??
	private var_type eval_exp16() throws StopException, SyntaxError {
		var_type result;
		result = eval_exp17();
		String op = token.value;

		  //TODO: handle classes differently, not required to be lvalues...

		  while(op.equals("++") || op.equals("--") || op.equals("[") || op.equals(".") 
				  || op.equals("->")){		
				if(op.equals("++")){
					if(!result.lvalue)
						interpreter.sntx_err("Argument of ++ operator must be an lvalue");
					result = result.suffixIncrement();
					interpreter.printVarVal(result);
				}
				else if(op.equals("--")){
					if(!result.lvalue)
						interpreter.sntx_err("Argument of -- operator must be an lvalue");
					result = result.suffixDecrement();
					interpreter.printVarVal(result);
				}
				else if(op.equals("[")){
					interpreter.sntx_err("arrays have not been implemented");
				}
				else if(op.equals(".")){
					interpreter.sntx_err("classes have not been implemented");
				}
				token = lexer.get_token();
				op = token.value;
			  }

		return result;
	}

	//TODO: function calls should have a lower precedence
	//evaluate scope resolution
	//TRY to put :: into atom
	private var_type eval_exp17() throws StopException, SyntaxError {
		var_type result = null;
		if(token.value.equals("::")){
			result = atomOf(result);//temporary;
			interpreter.sntx_err("Operator :: has not been implemented");
		}
		result = eval_exp18();
		return result;
	}

	// evaluate parenthesized expressions
	private var_type eval_exp18()  throws StopException, SyntaxError {
	  var_type result;
	  //TODO cant use charAt here
	  if((token.value.charAt(0) == '(')) {
		token = lexer.get_token();
		result = eval_exp0(); // get subexpression
		if(!token.value.equals(")")) interpreter.sntx_err("expected ) before " + token.value+" token");
		token = lexer.get_token();
	  }
	  else
		result = atom();

	  return result;
	}

	private var_type atom() throws StopException, SyntaxError {
	  //int i=0;
	  var_type value = new var_type();
	  switch(token.type){
		case IDENTIFIER:
		  /*TODO:
		  //i = internal_func(token);
		  if(i != -1){ //call std library function
		  value  = (intern_func[i].p)();

		  }
		  else*/ 
			//check if function
			if(interpreter.isUserFunc(token.value)) { // call user defined function
				  if(checkOnly){
					  value = interpreter.checkCall(token.value);
				  }
				  else{
					  value = interpreter.call(token.value);				  
				  }

				 // if value is not a reference
				 value.lvalue = false;
			}
		  else value = interpreter.find_var(token.value); // get var's value
		  value.lvalue = true;
		  token = lexer.get_token();
		  return value;

		case NUMBER: // is a numeric constant
		  if(token.value.indexOf('.') == -1){ // is an integer
			  value.value = Integer.parseInt(token.value);	 
			  value.v_type = keyword.INT;
		  }
		  else{
			  value.value = Double.parseDouble(token.value);
			  value.v_type = keyword.DOUBLE;
		  }
		  value.lvalue = false;
		  token = lexer.get_token();
		  return value;

		case CHAR: //check if character constant
			value.v_type = keyword.CHAR;
			value.value = (int) token.value.charAt(1);
			token = lexer.get_token();
			value.lvalue = false;
			return value;

		case OPERATOR:
			interpreter.sntx_err("Expected primary expression before "+token.value);
			break;

		case DELIMITER: 
		  //process empty expression return 0 TODO: verify this works 
		  //TODO cant use charAt here
		  if(token.value.charAt(0) == ')') return value; 
		  else interpreter.sntx_err("Bad delimiter: '" +token.value+ "' in expression");
		default:
		  interpreter.sntx_err("Unkown Error, CITRIN might not recognize this: "+token.value);
		  }
	  return value;
	  }

	// used when .func() or .mem or similar
	private var_type atomOf(var_type c) throws StopException, SyntaxError {
		  //int i=0;
		  var_type value = new var_type();
		  interpreter.sntx_err("classes have not been implemented, so this operation is prohibited");
		  return value;
	}

	//entry point into parser
	public var_type check_expr(boolean commasAreDelimiters) throws SyntaxError {
	  checkOnly = true;
	  var_type value = null;
	  token = lexer.get_token();
	  if(token.value==null) {
		throw new SyntaxError("No Expression", lexer.getLineNum(), lexer.getColumnNum());
	  }
	  try {
		if(commasAreDelimiters)
			value = eval_exp1();
		else
			value = eval_exp0();
	} catch (StopException e) {
		// Stop Exceptions cant be thrown during syntax check so this is an error
		e.printStackTrace();
	} catch (SyntaxError e) {
		throw new SyntaxError(e.toString(), lexer.getLineNum(), lexer.getColumnNum());
	}
	  lexer.putback(); /* return last token read to input stream */
	  return value;
	}

}
