import java.util.ArrayList;
import java.util.List;


  public class var_type{
	  
	  public String var_name;
	  public keyword v_type;
	  public Number value;
	  public String scope; //TODO: currently unused, will require more thought
	  public var_type memberOf = null; //TODO:
	  public static final String EQ = "==";
	  public static final String LT = "<";	
	  public static final String LE = "<=";
	  public static final String GT = ">";
	  public static final String GE = ">=";
	  public static final String NE = "!=";
	  
	  
	  var_type(){
		  var_name = null;
		  v_type = null;
	  }
	  var_type(var_type rhs){
		  if(rhs.var_name != null){
			  var_name = new String(rhs.var_name);			  
		  }
		  v_type = rhs.v_type;
		  value = rhs.value;
	  }
	  
	  //TODO: THIS FUNCTION WONT WORK WITH CLASSES
	  public keyword getBinaryOpReturnType(keyword type1, keyword type2) throws SyntaxError {
		  if(type1 == keyword.DOUBLE || type2 == keyword.DOUBLE)
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
		  if(isNumber() && rhs.isNumber()){
			  keyword returnType = getBinaryOpReturnType(v_type, rhs.v_type);
			  
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
		  return v;
	  }
	  

	public var_type sub(var_type rhs) throws SyntaxError{
		  var_type v = new var_type();
		  if(isNumber() && rhs.isNumber()){
			  keyword returnType = getBinaryOpReturnType(v_type, rhs.v_type);
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
		  else{
			  //TODO: check if there is a subtraction operator
		  }
		  return v;
	  }
	  
	  public var_type mul(var_type rhs) throws SyntaxError{
		  var_type v = new var_type();
		  if(isNumber() && rhs.isNumber()){
			  keyword returnType = getBinaryOpReturnType(v_type, rhs.v_type);
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
		  return v;
	  }
	  
	  public var_type div(var_type rhs) throws SyntaxError{
		  var_type v = new var_type();
		  if(isNumber() && rhs.isNumber()){
			  keyword returnType = getBinaryOpReturnType(v_type, rhs.v_type);
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
		  return v;
	  }
	  
	  public var_type mod(var_type rhs) throws SyntaxError{
		  var_type v = new var_type();
		  if(isNumber() && rhs.isNumber()){
			  keyword returnType = getBinaryOpReturnType(v_type, rhs.v_type);
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
		  return v;
	  }
	  
	  public var_type relationalOperator(var_type rhs, String op) throws SyntaxError{
		  var_type v = new var_type();
		  if(isNumber() && rhs.isNumber()){
			  keyword returnType = getBinaryOpReturnType(v_type, rhs.v_type);
			  if(returnType == keyword.DOUBLE || returnType == keyword.FLOAT){
				  v.v_type = keyword.BOOL;
				  double val1 = value.doubleValue();
				  double val2 = rhs.value.doubleValue();
				  
				  if(op.equals(EQ)){
					  v.value = (val1 == val2)? 1 : 0;
				  }
				  else if(op.equals(LT)){
					  v.value = (val1 < val2)? 1 : 0;
				  }
				  else if(op.equals(LE)){
					  v.value = (val1 <= val2)? 1 : 0;
				  }
				  else if(op.equals(GT)){
					  v.value = (val1 > val2)? 1 : 0;
				  }
				  else if(op.equals(GE)){
					  v.value = (val1 >= val2)? 1 : 0;
				  }
				  else if(op.equals(NE)){
					  v.value = (val1 != val2)? 1 : 0;
				  }
				  
			  }
			  else if(returnType == keyword.INT){
				  v.v_type = keyword.BOOL;
				  int val1 = value.intValue();
				  int val2 = rhs.value.intValue();
				  
				  if(op.equals(EQ)){
					  v.value = (val1 == val2)? 1 : 0;
				  }
				  else if(op.equals(LT)){
					  v.value = (val1 < val2)? 1 : 0;
				  }
				  else if(op.equals(LE)){
					  v.value = (val1 <= val2)? 1 : 0;
				  }
				  else if(op.equals(GT)){
					  v.value = (val1 > val2)? 1 : 0;
				  }
				  else if(op.equals(GE)){
					  v.value = (val1 >= val2)? 1 : 0;
				  }
				  else if(op.equals(NE)){
					  v.value = (val1 != val2)? 1 : 0;
				  }
			  }
		  }
		  return v;
	  }
	  
	  public var_type unaryMinus(){
		  var_type v = new var_type();
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
		  return v;
	  }
	  
	  
	  void assignVal(var_type rhs){
		  if(v_type == keyword.INT) value = rhs.value.intValue();
		  if(v_type == keyword.SHORT) value = rhs.value.shortValue();		  
		  if(v_type == keyword.CHAR) value = (rhs.value.intValue()%256+256)%256;
		  if(v_type == keyword.BOOL){
			  if(rhs.v_type == keyword.FLOAT || rhs.v_type == keyword.DOUBLE)
				  value = (rhs.value.doubleValue() == 0)? 0: 1;
			  else if(rhs.v_type == keyword.INT || rhs.v_type == keyword.SHORT || 
					  rhs.v_type == keyword.CHAR || rhs.v_type == keyword.BOOL)
				  value = (rhs.value.intValue() == 0)? 0: 1;
		  }
		  if(v_type == keyword.FLOAT) value = rhs.value.floatValue();
		  if(v_type == keyword.DOUBLE) value = rhs.value.doubleValue();
	  }
	  
	  var_type suffixIncrement() throws SyntaxError{
		  var_type result = new var_type(this);
		  var_type v1 = new var_type();
		  v1.v_type = keyword.INT;
		  v1.value = 1;
		  this.assignVal(this.add(v1));
		  return result;
	  }

	  var_type suffixDecrement() throws SyntaxError{
		  var_type result = new var_type(this);
		  var_type v1 = new var_type();
		  v1.v_type = keyword.INT;
		  v1.value = -1;
		  this.assignVal(this.add(v1));
		  return result;
	  }
	  
	  boolean isNumber(){
		  return (v_type == keyword.INT || v_type == keyword.FLOAT || v_type == keyword.DOUBLE || 
				  v_type == keyword.CHAR || v_type == keyword.BOOL);
	  }
	  
	  boolean canConvertTo(keyword type){
		  //TODO temp function... needs to be changed if classes are added
		  return true;
	  }
	  
	  boolean canAssign(var_type v2){
		  //TODO only works on primitive types for now
		  return true;
	  }

	  boolean canEvalOp(String op, var_type rhs){
		  //TODO only works on primitive types for now
		  return true;
	  }
	  var_type getReturnTypeFromBinaryOp(String op, var_type rhs) throws SyntaxError{
		  var_type v = new var_type();
		  
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
		  
		  return v;
	  }
	  
	  var_type getReturnTypeFromUnaryOp(String op){
		  var_type v = new var_type();
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
	  
	  
	  public void sntx_err(String s) throws SyntaxError {
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
	  
	  //public List<var_type> data; //use this for classes?
  }
  
  /*public class primitive_var extends var_type {
	  public Number value;
  }*/ //maybe ill use this