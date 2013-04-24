import java.util.ArrayList;
import java.util.List;


public class var_type{

	public String var_name;
	public keyword v_type;
	public Number value;
	public boolean lvalue;
	public String scope; //TODO: currently unused, will require more thought
	public Symbol memberOf = null; //TODO:
	public int address = -1;
	public boolean constant;
	
	//array data
	public var_type array_type;
	public ArrayList<Integer> bounds;
	public ArrayList<Symbol> data;

	var_type(){
		var_name = null;
		v_type = null;
		lvalue = false;
		address = -1;
		array_type = null;
		constant = false;
		memberOf = null;
		data = null;
	}
	var_type(var_type rhs){
		if(rhs.var_name != null){
			var_name = new String(rhs.var_name);				
		}
		v_type = rhs.v_type;
		value = rhs.value;
		lvalue = rhs.lvalue;
		address = rhs.address;
		array_type = rhs.array_type;
		bounds = rhs.bounds;
		constant = rhs.constant;
		memberOf = rhs.memberOf;
		data = rhs.data;
	}

	//TODO: THIS FUNCTION WONT WORK WITH CLASSES
	public static keyword getPromotionForBinaryOp(keyword type1, keyword type2) throws SyntaxError {
		if(type1 == keyword.ARRAY || type2 == keyword.ARRAY){
			sntx_err("Math operations on pointers have not been implemented");
			return null;
		}
		else if(type1 == keyword.DOUBLE || type2 == keyword.DOUBLE)
			return keyword.DOUBLE;
		else if(type1 == keyword.FLOAT || type2 == keyword.FLOAT)
			return keyword.FLOAT;
		else if(type1 == keyword.INT || type2 == keyword.INT)
			return keyword.INT;
		else if(type1 == keyword.SHORT || type2 == keyword.SHORT)
			return keyword.INT;
		else if(type1 == keyword.CHAR || type2 == keyword.CHAR)
			return keyword.INT;
		else if(type1 == keyword.BOOL || type2 == keyword.BOOL)
			return keyword.INT;
		else{
			sntx_err("operations on these types not defined");
			return null;
		}
	}

	public var_type add(var_type rhs) throws SyntaxError{
		var_type v = new var_type();
		keyword returnType = getPromotionForBinaryOp(v_type, rhs.v_type);
		if(isNumber() && rhs.isNumber()){

			if(returnType == keyword.DOUBLE){
				v.v_type = keyword.DOUBLE;
				v.value = value.doubleValue()+rhs.value.doubleValue();
			}
			else if(returnType == keyword.FLOAT){
				v.v_type = keyword.FLOAT;
				v.value = value.floatValue()+rhs.value.floatValue();
			}
			else if(returnType == keyword.INT){
				v.v_type = keyword.INT;
				v.value = value.intValue()+rhs.value.intValue();
			}
		}
		v.lvalue = false;
		v.constant = constant && rhs.constant;
		return v;
	}


public var_type sub(var_type rhs) throws SyntaxError{
		var_type v = new var_type();
		keyword returnType = getPromotionForBinaryOp(v_type, rhs.v_type);
		if(isNumber() && rhs.isNumber()){
			if(returnType == keyword.DOUBLE){
				v.v_type = keyword.DOUBLE;
				v.value = value.doubleValue()-rhs.value.doubleValue();
			}
			else if(returnType == keyword.FLOAT){
				v.v_type = keyword.FLOAT;
				v.value = value.floatValue()-rhs.value.floatValue();
			}
			else if(returnType == keyword.INT){
				v.v_type = keyword.INT;
				v.value = value.intValue()-rhs.value.intValue();
			}
		}
		v.lvalue = false;
		v.constant = constant && rhs.constant;
		return v;
	}

	public var_type mul(var_type rhs) throws SyntaxError{
		var_type v = new var_type();
		keyword returnType = getPromotionForBinaryOp(v_type, rhs.v_type);
		if(isNumber() && rhs.isNumber()){
			if(returnType == keyword.DOUBLE){
				v.v_type = keyword.DOUBLE;
				v.value = value.doubleValue()*rhs.value.doubleValue();
			}
			else if(returnType == keyword.FLOAT){
				v.v_type = keyword.FLOAT;
				v.value = value.floatValue()*rhs.value.floatValue();
			}
			else if(returnType == keyword.INT){
				v.v_type = keyword.INT;
				v.value = value.intValue()*rhs.value.intValue();
			}
		}
		v.lvalue = false;
		v.constant = constant && rhs.constant;
		return v;
	}

	public var_type div(var_type rhs) throws SyntaxError{
		var_type v = new var_type();
		keyword returnType = getPromotionForBinaryOp(v_type, rhs.v_type);
		if(isNumber() && rhs.isNumber()){
			if(rhs.value.doubleValue()==0)
				run_err("divide by 0");
			else if(returnType == keyword.DOUBLE){
				v.v_type = keyword.DOUBLE;
				v.value = value.doubleValue()/rhs.value.doubleValue();
			}
			else if(returnType == keyword.FLOAT){
				v.v_type = keyword.FLOAT;
				v.value = value.floatValue()/rhs.value.floatValue();
			}
			else if(returnType == keyword.INT){
				v.v_type = keyword.INT;
				v.value = value.intValue()/rhs.value.intValue();
			}
		}
		v.lvalue = false;
		v.constant = constant && rhs.constant;
		return v;
	}

	public var_type mod(var_type rhs) throws SyntaxError{
		var_type v = new var_type();
		keyword returnType = getPromotionForBinaryOp(v_type, rhs.v_type);
		if(isNumber() && rhs.isNumber()){
			if(returnType == keyword.DOUBLE || returnType == keyword.FLOAT){
				sntx_err("mod uses integral types only");
			}
			if(rhs.value.intValue()==0)
				run_err("divide by 0");
			else if(returnType == keyword.INT){
				v.v_type = keyword.INT;
				v.value = value.intValue()%rhs.value.intValue();
			}
		}
		v.lvalue = false;
		v.constant = constant && rhs.constant;
		return v;
	}


	public var_type bitBinaryOp(String op, var_type rhs) throws SyntaxError{
		var_type v = new var_type();
		keyword returnType = getPromotionForBinaryOp(v_type, rhs.v_type);
		if(isNumber() && rhs.isNumber()){
			if(returnType == keyword.DOUBLE || returnType == keyword.FLOAT){
				sntx_err("Bitwise operator " + op + " cannot take floating point types");
			}
			else if(returnType == keyword.INT){
				v.v_type = keyword.INT;
				if(op.equals("<<"))
					v.value = value.intValue()<<rhs.value.intValue();
				else if(op.equals(">>"))
					v.value = value.intValue()>>rhs.value.intValue();
				else if(op.equals("&"))
					v.value = value.intValue()&rhs.value.intValue();
				else if(op.equals("^"))
					v.value = value.intValue()^rhs.value.intValue();
				else if(op.equals("|"))
					v.value = value.intValue()|rhs.value.intValue();
			}
		}
		v.lvalue = false;
		v.constant = constant && rhs.constant;
		return v;
	}

	public var_type relationalOperator(var_type rhs, String op) throws SyntaxError{
		var_type v = new var_type();
		keyword returnType = getPromotionForBinaryOp(v_type, rhs.v_type);
		if(isNumber() && rhs.isNumber()){
			if(returnType == keyword.DOUBLE || returnType == keyword.FLOAT){
				v.v_type = keyword.BOOL;
				double val1 = value.doubleValue();
				double val2 = rhs.value.doubleValue();

				if(op.equals("==")){
					v.value = (val1 == val2)? 1 : 0;
				}
				else if(op.equals("<")){
					v.value = (val1 < val2)? 1 : 0;
				}
				else if(op.equals("<=")){
					v.value = (val1 <= val2)? 1 : 0;
				}
				else if(op.equals(">")){
					v.value = (val1 > val2)? 1 : 0;
				}
				else if(op.equals(">=")){
					v.value = (val1 >= val2)? 1 : 0;
				}
				else if(op.equals("!=")){
					v.value = (val1 != val2)? 1 : 0;
				}

			}
			else if(returnType == keyword.INT){
				v.v_type = keyword.BOOL;
				int val1 = value.intValue();
				int val2 = rhs.value.intValue();

				if(op.equals("==")){
					v.value = (val1 == val2)? 1 : 0;
				}
				else if(op.equals("<")){
					v.value = (val1 < val2)? 1 : 0;
				}
				else if(op.equals("<=")){
					v.value = (val1 <= val2)? 1 : 0;
				}
				else if(op.equals(">")){
					v.value = (val1 > val2)? 1 : 0;
				}
				else if(op.equals(">=")){
					v.value = (val1 >= val2)? 1 : 0;
				}
				else if(op.equals("!=")){
					v.value = (val1 != val2)? 1 : 0;
				}
			}
		}
		v.lvalue = false;
		v.constant = constant && rhs.constant;
		return v;
	}

	public var_type unaryMinus() throws SyntaxError{
		var_type v = new var_type();
		if(v_type==keyword.ARRAY){
			sntx_err("Math operations on pointers have not been implemented");
		}
		
		if(isNumber()){
			if(v_type == keyword.DOUBLE){
				v.v_type = keyword.DOUBLE;
				v.value = -value.doubleValue();
			}
			else if(v_type == keyword.INT || v_type == keyword.CHAR || v_type == keyword.BOOL || v_type == keyword.SHORT){
				v.v_type = keyword.INT;
				v.value = -value.intValue();
			}
		}
		v.lvalue = false;
		v.constant = constant;
		return v;
	}

	public var_type unaryPlus() throws SyntaxError{
		var_type v = new var_type();
		
		if(v_type==keyword.ARRAY){
			sntx_err("Math operations on pointers have not been implemented");
		}
		
		if(isNumber()){
			v = new var_type(this);
		}
		v.lvalue = false;
		v.constant = constant;
		return v;
	}


	void assignVal(var_type rhs) throws SyntaxError{
		if(v_type == keyword.INT) value = rhs.value.intValue();
		else if(v_type == keyword.SHORT) value = rhs.value.shortValue();			
		else if(v_type == keyword.CHAR) value = (rhs.value.intValue()%256+256)%256;
		else if(v_type == keyword.BOOL){
			if(rhs.v_type == keyword.FLOAT || rhs.v_type == keyword.DOUBLE)
				value = (rhs.value.doubleValue() == 0)? 0: 1;
			else if(rhs.v_type == keyword.INT || rhs.v_type == keyword.SHORT || 
					rhs.v_type == keyword.CHAR || rhs.v_type == keyword.BOOL)
				value = (rhs.value.intValue() == 0)? 0: 1;
		}
		else if(v_type == keyword.FLOAT) value = rhs.value.floatValue();
		else if(v_type == keyword.DOUBLE) value = rhs.value.doubleValue();
		else if(v_type==keyword.ARRAY){
			sntx_err("ISO C++ forbids assignments to arrays");
		}
	}

	var_type suffixIncrement() throws SyntaxError{
		if(v_type==keyword.ARRAY){
			sntx_err("ISO C++ forbids assignments to arrays");
		}
		var_type result = new var_type(this);
		var_type v1 = new var_type();
		v1.v_type = keyword.INT;
		v1.value = 1;
		this.assignVal(this.add(v1));
		result.lvalue = false;
		return result;
	}

	var_type suffixDecrement() throws SyntaxError{
		if(v_type==keyword.ARRAY){
			sntx_err("ISO C++ forbids assignments to arrays");
		}
		var_type result = new var_type(this);
		var_type v1 = new var_type();
		v1.v_type = keyword.INT;
		v1.value = 1;
		this.assignVal(this.sub(v1));
		result.lvalue = false;
		return result;
	}

	//TODO: rewrite this without using a shortcut
	var_type prefixIncrement() throws SyntaxError{
		if(v_type==keyword.ARRAY){
			sntx_err("ISO C++ forbids assignments to arrays");
		}
		var_type v1 = new var_type();
		v1.v_type = keyword.INT;
		v1.value = 1;
		this.assignVal(this.add(v1));
		var_type result = new var_type(this);
		return result;
	}

	var_type prefixDecrement() throws SyntaxError{
		if(v_type==keyword.ARRAY){
			sntx_err("ISO C++ forbids assignments to arrays");
		}
		var_type v1 = new var_type();
		v1.v_type = keyword.INT;
		v1.value = 1;
		this.assignVal(this.sub(v1));
		var_type result = new var_type(this);
		return result;
	}

	var_type logicalNot(){
		var_type v = new var_type();
		if(isNumber()){
			v.v_type = keyword.DOUBLE;
			if(value.doubleValue() == 0)
				v.value = 1;
			else
				v.value = 0;
		}
		v.lvalue = false;
		v.constant = constant;
		return v;
	}


	var_type bitwiseNot() throws SyntaxError{
		var_type v = new var_type();
		
		if(v_type==keyword.ARRAY){
			sntx_err("Math operations on pointers have not been implemented");
		}
		
		if(isNumber()){
			if(v_type == keyword.INT || v_type == keyword.SHORT || v_type == keyword.CHAR || v_type == keyword.BOOL ){
				v.v_type = keyword.INT;
				v.value = ~value.intValue();
			}
			else{
				sntx_err("bitwise operations on floating point types is not defined");
			}
		}
		v.lvalue = false;
		v.constant = constant;
		return v;
	}

	boolean isNumber(){
		return (v_type == keyword.INT || v_type == keyword.FLOAT || v_type == keyword.DOUBLE || 
				v_type == keyword.CHAR || v_type == keyword.BOOL );
	}

	boolean canConvertTo(keyword type){
		//TODO temp function... needs to be changed if classes are added
		if(v_type == keyword.ARRAY)
			return false;
		return true;
	}

	boolean canAssign(var_type v2){
		//TODO only works on primitive types for now
		if(v_type == keyword.ARRAY)
			return false;
		return true;
	}

	boolean canEvalOp(String op, var_type rhs){
		//TODO only works on primitive types for now
		if(v_type == keyword.ARRAY)
			return false;
		return true;
	}
	
	var_type getReturnTypeFromBinaryOp(String op, var_type rhs) throws SyntaxError{
		var_type v = new var_type();
		
		if(v_type == keyword.ARRAY){
			sntx_err("Math operations on pointers have not been implemented");
		}

		if(op.equals(">") || op.equals("<") || op.equals("==") || op.equals(">=") || op.equals("<=") || op.equals("!=")){
			v.v_type = keyword.BOOL;
			return v;
		}
		
		if(v_type==keyword.DOUBLE || rhs.v_type==keyword.DOUBLE){
			v.v_type = keyword.DOUBLE;
		}
		else if(v_type == keyword.FLOAT || rhs.v_type == keyword.FLOAT){
			v.v_type = keyword.FLOAT;  
		}
		else {
			v.v_type = keyword.INT;  
		}

		if(op.equals("%") && (v.v_type==keyword.DOUBLE || v.v_type == keyword.FLOAT)){
			sntx_err("Operator % can not take floating point types");
		}

		if( (op.equals(">>") || op.equals("<<")) && (v.v_type==keyword.DOUBLE && v.v_type==keyword.FLOAT) )
			sntx_err("Binary operator "+op+" cannot take floating point types");

		v.constant = constant && rhs.constant;
		return v;
	}

	var_type getReturnTypeFromUnaryOp(String op) throws SyntaxError{
		if(v_type == keyword.NONPRIMITIVE)
			sntx_err("unary ops not defined on classes");

		if(v_type == keyword.ARRAY){
			sntx_err("Math operations on pointers have not been implemented");
		}
		
		var_type v = new var_type();

		if(op.equals("!")){
			v.v_type = keyword.BOOL;
			return v;
		}

		if(v_type==keyword.DOUBLE){
			v.v_type = keyword.DOUBLE;
		}
		else if(v_type == keyword.FLOAT){
			v.v_type = keyword.FLOAT;  
		}
		else {
			v.v_type = keyword.INT;  
		}

		return v;
	}

	
	public String getDisplayVal(){
		if(v_type == keyword.SHORT || v_type == keyword.INT)
			return ""+value.intValue();
		else if(v_type == keyword.FLOAT || v_type == keyword.DOUBLE)
			return ""+value.doubleValue();
		else if(v_type == keyword.CHAR)
			return ""+(char)value.intValue();
		else if(v_type == keyword.BOOL)
			return value.intValue()==1? "true" : "false";
		else if(v_type == keyword.ARRAY && bounds.size() == 1){
			String str = "{" + data.get(0).data.getDisplayVal();
			for(int i=1;i<bounds.get(0);i++){
				str = str+", "+data.get(i).data.getDisplayVal();
			}
			str = str + "}";
			return str;
		}
		return null;
		
	}

	public static void sntx_err(String s) throws SyntaxError {
		throw new SyntaxError(s,-1,-1);
		}

	public void run_err(String s) throws SyntaxError {
		throw new SyntaxError(s,-1,-1);
		}

	public keyword getPromotedType(){
		if(v_type == keyword.CHAR || v_type == keyword.INT || v_type == keyword.SHORT || v_type==keyword.BOOL)
			return keyword.INT;
		if(v_type == keyword.FLOAT || v_type == keyword.DOUBLE)
			return keyword.DOUBLE;
		return v_type;
	}

	public String getName(){
		if(v_type == keyword.NONPRIMITIVE){
			//return class_type;
			return "some class name";
		}
		else{
			return v_type.toString().toLowerCase();
		}
	}

	//public ArrayList<var_type> data; //use this for classes?
}

	/*public class primitive_var extends var_type {
		public Number value;
	}*/ //maybe ill use this
