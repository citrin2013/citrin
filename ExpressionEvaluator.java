
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
	public var_type eval_exp()  throws StopException, SyntaxError {
	  checkOnly = false;
	  var_type value = null;
	  token = lexer.get_token();
	  if(token.value==null) {
	    interpreter.sntx_err("No expression");
	    return new var_type();
	  }
	  value = eval_exp0();
	  lexer.putback(); /* return last token read to input stream */
	  return value;
	}

	// process an assignment expression
	private var_type eval_exp0()  throws StopException, SyntaxError {
	  var_type value, result = new var_type();
	  String temp; //holds name of var recieving assignment
	  token_type temp_tok;

	  if(token.type == token_type.IDENTIFIER) {
	    if(interpreter.is_var(token.value)) { /* if a var, see if assignment */
	      temp = new String(token.value);
	      temp_tok = token.type;
	      token = lexer.get_token();
	      if(token.value.charAt(0) == '=') { /* is an assignment */

	    	if(!interpreter.is_var(temp)){
		        throw new SyntaxError("Variable: "+temp+" has not been defined", lexer.getLineNum(), lexer.getColumnNum());
		    }
	    	token = lexer.get_token();

	        value = eval_exp0(); /* get value to assign */
	        var_type v = interpreter.find_var(temp);
	        if(!v.canAssign(value)){
		        throw new SyntaxError("a "+v.v_type.toString().toLowerCase()+" cannot be assigned a value of type "
		        		+value.v_type.toString().toLowerCase(),lexer.getLineNum(), lexer.getColumnNum());
	        }
	        
	        if(checkOnly){
	        	result.v_type = v.v_type;
	        }
	        else{
		        result = interpreter.assign_var(temp, value); /* assign the value */
	        }
	        return result;
	      }
	      else { /* not an assignment */
	    	lexer.putback(); /* restore original token */
	        token.value = new String(temp);
	        token.type = temp_tok;
	      }
	    }
	  }
	  result = eval_exp1();
	  return result;
	}
	
	
	// process relational operators
	private var_type eval_exp1()  throws StopException, SyntaxError {
	  var_type value, result = new var_type();
	  var_type partial_value;
	  String op;

	  value = eval_exp2();
	  op = token.value;
	  
	  if(op.equals(EQ) || op.equals(LT) || op.equals(LE) || 
	      op.equals(GT) || op.equals(GE) || op.equals(NE)) {

		  token = lexer.get_token();
		  partial_value = eval_exp2();
		  if(checkOnly){
			  result.v_type = keyword.BOOL;
		  }
		  else{
			  result = value.relationalOperator(partial_value, op);			  
		  }
		  
	  }
	  else{
		  result = value;
	  }
	  
	  return result;
	}

	
	
	// add or subtract two terms
	private var_type eval_exp2()  throws StopException, SyntaxError {
	  var_type value, partial_value;
	  String op = "";
	  var_type result;

	  value = eval_exp3();
	  result = new var_type(value);
	  op = token.value;
	  while(op.equals("+") || op.equals("-") ){
	    token = lexer.get_token();
	    partial_value = eval_exp3();
	    if(op.equals("-")){
	    	if(checkOnly){
	    		result = value.getReturnTypeFromBinaryOp(op, partial_value);
	    	}
	    	else{
		    	result = value.sub(partial_value);		    		
	    	}
	    }
	    if(op.equals("+")){
	    	if(checkOnly){
	    		result = value.getReturnTypeFromBinaryOp(op, partial_value);
	    	}
	    	else{
		      	result = value.add(partial_value);	
	    	}
	    }
	    op = token.value;
	  }
	  return result;
	}


	// multiply or divide two factors
	private var_type eval_exp3()  throws StopException, SyntaxError {
	  var_type value, partial_value;
	  var_type result;
	  char op;
	  
	  value = eval_exp4 ();
	  result = new var_type(value);
	  while((op=token.value.charAt(0)) == '*' || op == '/' || op == '%' ){		
	    token = lexer.get_token();
	    partial_value = eval_exp4();
	    switch(op){
	      case '*':
			if(checkOnly)
				result = value.getReturnTypeFromBinaryOp("*",value);
			else
				result = value.mul(partial_value);
	        break;
	      case '/':
			if(checkOnly)
				result = value.getReturnTypeFromBinaryOp("/",value);
			else
				result = value.div(partial_value);
	        break;
	      case '%':
			if(checkOnly)
				result = value.getReturnTypeFromBinaryOp("%",value);
			else
				result = value.mod(partial_value);
	        break;
	    }
	  }

	  return result;
	}
	
	
	//evaluate unary plus or minus
	private var_type eval_exp4()  throws StopException, SyntaxError {
	  var_type value, result;
	  char op = '\0';

	  if(token.value.charAt(0) == '+' || token.value.charAt(0) == '-'){
	    op = token.value.charAt(0);
	    token = lexer.get_token();
	  }
	  value = eval_exp5();
	  result = new var_type(value);
	  if(op!='\0')
	    if(op == '-') result = value.unaryMinus();

	  return result;
	}

	private var_type eval_exp5()  throws StopException, SyntaxError {
	  var_type value;
	  if((token.value.charAt(0) == '(')) {
	    token = lexer.get_token();
	    value = eval_exp0(); // get subexpression
	    if(token.value.charAt(0) != ')') interpreter.sntx_err("expected )");
	    token = lexer.get_token();
	  }
	  else
	    value = atom();

	  return value;
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
	      	}
	      else value = interpreter.find_var(token.value); // get var's value
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
	      token = lexer.get_token();
	      return value;

	    case CHAR: //check if character constant
	    	value.v_type = keyword.CHAR;
	    	value.value = (int) token.value.charAt(1);
	    	token = lexer.get_token();
	    	return value;
	      
	    case DELIMITER: 
	      //process empty expression return 0 TODO: verify this works 
	      if(token.value.charAt(0) == ')') return value; 
	      else interpreter.sntx_err("Bad delimiter: '" +token.value+ "' in expression");
	    default:
	      interpreter.sntx_err("Unkown Error, CITRIN might not recognize this: "+token.value);
	      }
	  return value;
	  }
	
	
	//entry point into parser
	public var_type check_expr() throws SyntaxError {
	  checkOnly = true;
	  var_type value = null;
	  token = lexer.get_token();
	  if(token.value==null) {
	    throw new SyntaxError("No Expression", lexer.getLineNum(), lexer.getColumnNum());
	  }
	  try {
		value = eval_exp0();
	} catch (StopException e) {
		// Stop Exceptions cant be thrown during syntax check
		e.printStackTrace();
	}
	  lexer.putback(); /* return last token read to input stream */
	  return value;
	}
	
}
