# Project Title: DSL-Boolean-Gates
# Project Description: 
This is a domain specific language implemention for creating and evaluating expressions to implement logic gates (both simple and complex), and creating user defined classes and their objects. This is the second phase of the project implementation, wherein one can describe boolean gates, evaluate them, create classes with Fields, Constructor, and Methods, provide Access Modifiers for Fields and Methods, create objects for classes, Invoke Fields and Methods using Objects, Extend a childclass from parentclass, Substitute parentclass object by childclass object or substitute objects of same class type. This project is build using Scala(version 3.2.0) which is completely an object-oriented and functional programming language. 

# How to install and run the project
Requirements: [Java Development Toolkit (JDK)](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html) and [Simple Build Toolkit (SBT)](https://www.scala-sbt.org/1.x/docs/index.html)

Installation of SBT: (for Windows)
-Install JDK 8 or 11;
-Download [MSI Installer](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Windows.html);

Run Project:
1. From command propmt, navigate to the project directory (use cd command to change directory)
2. Start the sbt shell: $sbt
3. Compile the main directory scala code: sbt:project> compile
4. Run the project: sbt:project> run
5. To run scalatests: sbt:project> test
(TO get help sbt:project> help)

# How to use the project

## Basic commands: 
- NOT, negation or complement - which receives one input and returns true when that input is false ("not");
- AND or conjunction - true when all inputs are true ("both");
- OR or disjunction - true when any input is true ("either")l;
- XOR or exclusive disjunction - true when one of its inputs is true and the other is false ("not equal");
- NAND or Sheffer stroke - true when it is not the case that all inputs are true ("not both");
- NOR or logical nor - true when none of the inputs are true ("neither");
- XNOR or logical equality - true when both inputs are the same ("equal");

## Value
Value is required to give any boolean values. Either to use a boolean value (true or false) in some logic gate expression or to assign boolean values to Input variables, Value() needs to be used.

//Using boolean values inside a gate <br>
**And(Value(true), Value(false)).eval**

//Boolean values *true* and *false* cannot be given directly and doing so will generate error.
//This will not work and generate error<br>
**And(true, false).eval**

## Assign
Assign() can be for three purposes: 
1. To Assign a user-defined logic name to some boolean expression. 

//The operator Assign takes the specification of the logic gate and assigns it to a variable named logicGate1.<br> 
**Assign(LogicGate("logicGate1"), NOT(OR(Input("A"), Input("B")))).eval**

2. To Assign User-defined Input Variables to some Value(boolean) inside the scope of some user-defined logic gate.

//specifying inputs for this logic gate ("logicGate1") inside its own scope<br>
**Scope(LogicGate("logicGate1"), List(Assign(Input("A"), Value(true)), Assign(Input("B"), Value(false)))).eval**

//Or, the above inputs can be deinfed separately for the logic gate "logicGate1" as<br>
**Scope(LogicGate("logicGate1"), List(Assign(Input("A"), Value(true))).eval**
**Scope(LogicGate("logicGate1"), List(Assign(Input("B"), Value(false))).eval**

3.  To Assign User-defined Field Variables of a class to some Value(boolean) inside the constructor or method of a class.

(i) //The operator Assign assigns the Field of the class to boolean evaluation of a logicgate either via constructor of via a method invocation on some object of the class.<br> 
**ClassDef("Class1", List(Field("X")), Constructor(List(Assign(Field("X"), Value(false)))), List(Method("m1", List(Assign(Field("X"), NOT(Field("X")))), List()))).eval**

(ii) // The operator Assign assigns values of parameters passed to a method of a class both during class definition and method invocation.<br>
**ClassDef("Class1", List(Field("X")), Constructor(List(Assign(Field("X"), Value(false)))), List(Method("m1", List(Assign(Field("X"), NOT(Field("X"))), Input("x")), List(Assign(Input("x"), Value(false)))))).eval**<br><br>

**Variable("obj1").InvokeMethod("m1", List(Assign(Input("x"), Value(true))))**

4. To Assign User-defined ConstantField Variables of an Interface to some Value(boolean) while declaring an interface.<br>
**InterfaceDef("myInterface", List(ConstantField("X"), ConstantField("Y")), List(Assign(ConstantField("X"), Value(false)), Assign(ConstantField("Y"), NOT(Value(true)))), List(AbstractMethod("f1"), AbstractMethod("f2"))).eval**<br><br>

## LogicGate

LogicGate() are required to create and evaluate some user defined logic gate. It takes a user defined string as a parameter which is the user-defined name for the logic gate.

//Creating a logic gate<br>
**Assign(LogicGate("NotGate"), NOT(Input("A"))).eval**

// Evaluating a created logic gate<br>
**LogicGate("NotGate").eval**

//Evaluating a logic gate which is not created will give an error<br>
**LogicGate("someGate").eval**

## Input

Input() can be used in user-defined logic gates to create user-defined input variable name

//"A" and "B" two inputs exist for logicGate1. <br>
**Assign(LogicGate("logicGate1"), NOT(OR(Input("A"), Input("B")))).eval**

// To set Input values for some logic gate we need to use a scope of that logic gate<br>
// setting boolean value for input "A" for "logicGate1"  <br>
**Scope(LogicGate("logicGate1"), List(Assign(Input("A"), Value(true))).eval**

Input() can also be used inside method parameter list during class definition of method Invocation<br><br>
See example in the **Assign** section<br>


## Scope

"Scope()" will help to filter out current scope in order to assign input variables for respective logic gates

As a first argument it will take a parameter of type LogicGate(nameofthisgate: String) and second parameter as a list of expressions which should be either another Scope expression or Assignment Expression.

//Assigning input values for logic gate "logicGate1"<br>
**Scope(LogicGate("logicGate1"), List(Assign(Input("A"), Value(true)), Assign(Input("B"), Value(false)))).eval**

OR

//Scope() can be called inside the list<br>
//Assigning input values for some logic gate "logicGate1" with A -> true, B -> false and for a logic gate "logicGate2" with X -> false <br>
**Scope(LogicGate("logicGate1"), List(Assign(Input("A"), Value(true)), Assign(Input("B"), Value(false)), Scope(LogicGate("logicGate2"), List(Assign(Input("X"), Value(false)))))).eval**

//For expressions in the list argument of Scope which are not Assign or Scope type, just evaluations will be performed to test if those expressions are valid<br>
//This Scope will just evaluate the expression given inside the list.<br>
**Scope(LogicGate("logicGate1"), List(NOT(Value(true))).eval**

## TestGate
TestGate is a function that takes 2 parameters - 1) name of user defined logic gate 2) a boolean value either "true" or "false"
TestGate(gateName, BooleanValue) can be used to test is a Logic gate defined with name gateName evaluates to the provided BooleanValue

**TestGate("AndGate1", true)** 


## getInputVal
getInputVal() can be used to get value of an already defined input value for a logic gate. It is a useful function to fetch input values without having to worry about remembering what values of input was set

getInputVal() takes two paramters, nameOfLogicGate: String and nameOfdesiredInput: String.

//This will return value of Input variable "A" for "logicGate1"<br>
**getInputVal("logicGate1", "A")**

If the logic gate does not exist error "Input value invocation for an undefined logic Gate" is thrown.<br>
If the required input varible for that logic gate does exists an error "Variable does not exist!" is thrown.

  ### Note
  Any boolean expressions, LogicGate, Assign or Scope must be evaluated using ".eval" at the end

## ClassDef
ClassDef() creates a user defined class. It takes 4 parameters- 1) a user defined string for class name, 2) a list of Field, 3) a Constructor, 4) a list of Method

//Creates a class Class1 with Fields X, Constructor, Methods m1<br>
**ClassDef("Class1", List(Field("X")), Constructor(List(Assign(Field("X"), Value(false)))), List(Method("m1", List(AND(Field("X"), Input("x"))), List(Assign(Input("x"), Value(false)))))).eval**


## ClassName
To refer a Class. ClassName("class1") refers to a class named "class1"

//It is used to differentiate in Classes and Interfaces while using Extend()<br>
**Extend(ClassName("childClass"), ClassName("parentClass")).eval**<br>

## Field()
Field() can be used in user-defined classes to create user-defined field variable names.

//"X" and "Y" two Fields exist for Class1. <br>
**ClassDef("Class1", List(Field("X"), Field("Y")), Constructor(List(Assign(Field("X"), Value(false)), Assign(Field("Y"), Value(true)))), List(Method("m1", List(Assign(Field("X"), NOT(Field("X"))), Input("x")), List(Assign(Input("x"), Value(false)))), Method("m2", List(AND(Field("X"), Field("Y"))), List(Assign(Input("y"), Value(true)))))).eval**

## Constructor()
Constructor() takes a list of LogicGate expressions and changes Field values of a Class for Assign expressions

// Constructor Assigns X->false, Y->true. Constructor gets invoked only when an object is created for the class.<br>
**ClassDef("Class1", List(Field("X"), Field("Y")), Constructor(List(Assign(Field("X"), Value(false)), Assign(Field("Y"), Value(true)))), List(Method("m1", List(Assign(Field("X"), NOT(Field("X"))), Input("x")), List(Assign(Input("x"), Value(false)))), Method("m2", List(AND(Field("X"), Field("Y"))), List(Assign(Input("y"), Value(true)))))).eval**<br>

**Note:** By default Fields are assigned to value False


## Method()
Method() takes a 3 parameters- 1)A string for Method Name, 2) a list of LogicGate expressions 3) a list of parameters<br>
Method() returns boolean value equal to the last expression evaluation result

//Method m1 takes a paramter x, expr1: inverts value of existing Field(X), expr2: return value of parameter x<br>
**ClassDef("Class1", List(Field("X"), Field("Y")), Constructor(List(Assign(Field("X"), Value(false)), Assign(Field("Y"), Value(true)))), List(Method("m1", List(Assign(Field("X"), NOT(Field("X"))), Input("x")), List(Assign(Input("x"), Value(false)))))).eval**<br>


## NewObject()
NewObject() creates a new object of a class. It takes 2 parameters: 1) class name: String 2) data type Variable(objectName)

// Creates new Object "o1" of Class1. When o1 is created, consrtructor of this class is automatically called. <br>
**NewObject("Class1", Variable("o1")).eval**

## Variable()
Variable() is used to refer to an object of a class. InvokeMethod() and InvokeField() can be called on Variable.

//Invoking a method "m1" using object "o1"<br>
**Variable("o1").InvokeMethod("m1", List(Assign("x"), Value(true)))**<br>

//Invoking a field "X" using object "o1"<br>
**Variable("o1").InvokeField("X")**

## Access Modifiers
**NOTE:** By default no access modifier is applied to any field or method. Access modifiers need to be explicitly defined by the user usingthe following data types

### Public()
Public() defines access for fields and methods of some class as Public. It Takes 3 parameters- 1) class name 2) List of fields to be defined Public, 3)List of methods to be defined Public

// Defines Field X and Method m1 as Public<br>
**Public("Class1", List("X"), List("m1")).eval**

### Protected()
Protected() defines access for fields and methods of some class as Protected. It Takes 3 parameters- 1) class name 2) List of fields to be defined Protected, 3)List of methods to be defined Protected

// Defines Field X and Method m1 as Protected<br>
**Protected("Class1", List("X"), List("m1")).eval**

### Private()
Private() defines access for fields and methods of some class as Private. It Takes 3 parameters- 1) class name 2) List of fields to be defined Private, 3)List of methods to be defined Private

// Defines Field X and Method m1 as Private<br>
**Private("Class1", List("X"), List("m1")).eval**

## Extend()
Extend() is a datatype for Inheritance. It takes 2 parameters 1)child className Or child InterfaceName 2)parent className Or parent InterfaceName

1. If parameters are of type ClassName() - Class cName extends Class pName.cName- child class name, pName- parent class name<br>
// childClass extends parentClass<br>
**Extend(ClassName("childClass"), ClassName("parentClass")).eval**<br>
//child class inherits all public and protected fields and methods of the parent class and utilize them using InvokeMethod() or InvokeField() functions<br>

2. If parameters are of type InterfaceName() - Interface cName extends Interface pName.cName- child interface name, pName- parent interface name<br>
// childInterface extends parentInterface<br>
**Extend(InterfaceName("childInterface"), InterfaceName("parentInterface")).eval**<br>
//child interface inherits all constant fields and abstract methods of parent interface and any class that implements child interface will need to implement all abstract methods of that interface and its super interfaces.<br>

## SubstituteObject()
SubstituteObject() can be used to change binding of an already created object. It takes 2 parameters: 1) obj1: Object whose binding is to be changed, obj2: obj1 now refers to obj2<br>
### NOTE: either both objects should be of the same class type OR class type of obj1 should be a subtype of obj2<br>

// parentObject will now be referenced by the childobject and calling smilar named function from the parentObject will call its overrided function in childobject<br>
**SubstituteObject(Variable("parentObject"), Variable("childObject")).eval**<br>

// obj1 will now refer to obj2<br>
**SubstituteObject(Variable("obj1"), Variable("obj2")).eval**<br>


## AbstractClassDef
AbstractClassDef() creates a user defined class. It takes 5 parameters- 1) a user defined string for abstract class name, 2) a list of Field, 3) a Constructor, 4) a list of concrete Methods, 5) a list of abstract methods

// defines an abstract class absClass with Field X, Constructor, concrete method f1 & f2, and abstrct method f3<br>
**AbstractClassDef("absClass", List(Field("X")), Constructor(List(Assign(Field("X"), Value(false)))), List(Method("f1", List(OR(Field("X") ,Input("a"))), List(Assign(Input("a"), Value(true)))), Method("f2", List(Value(false)), List())), List(AbstractMethod("f3"))).eval**

## InterfaceDef
InterfaceDef() creates a user defined interface. It takes 4 parameters- 1) a user defined string for interface name, 2) a list of constant fields, 3) a Constructor, 4) a list of assigns to set value of the constant fields, 5) a list of abstract methods

// defines an interface myInterface with constant Field X & Y,and abstrct methods f1 & f2<br>
**InterfaceDef("myInterface", List(ConstantField("X"), ConstantField("Y")), List(Assign(ConstantField("X"), Value(false)), Assign(ConstantField("Y"), NOT(Value(true)))), List(AbstractMethod("f1"), AbstractMethod("f2"))).eval**

## ConstantField
ConstantField() can be used in user-defined interfaces to create user-defined constant fields variable names.

//"X" and "Y" are two constant Fields that exist for myInterface. <br>
**InterfaceDef("myInterface", List(ConstantField("X"), ConstantField("Y")), List(Assign(ConstantField("X"), Value(false)), Assign(ConstantField("Y"), NOT(Value(true)))), List(AbstractMethod("f1"), AbstractMethod("f2"))).eval**

## InterfaceName
To refer to an Interface. Interface("interface") refers to an interface named "interface1"

//It is used to differentiate in Classes and Interfaces while using Extend()<br>
**Extend(InterfaceName("interface1"), InterfaceName("interface2")).eval**<br>

## Implements
Implements() is a datatype for Implementation of an interface by a class. It takes 2 parameters 1)class name 2)interface name

// class1 implements interface1<br>
**Implements("class1", "interface1").eval**

## IF 
If() is a data type for selecting which block of code will be excuted based on a condition. It checks a condition and if the condition is true it runs the thenClause, else it runs the elseClause<br>
It takes 3 parameters: 1) CheckEqual, 2) a List of Expressions for thenClause, 3) a List of Expressions for elseClause<br>

//check condition results in false and elseClause will be run<br> 
**IF(CheckEqual(NOT(Value(false)), NOT(NOT(Value(false)))), List(Assign(LogicGate("NotGate2"), NOT(Input("A")))), List(Assign(LogicGate("OrGate2"), OR(Input("A"), Input("B"))), Assign(LogicGate("AndGate2"), AND(Input("A"), Input("B"))))).eval**

## ExceptionClassDef
ExceptionClassDef declare a user-defined exception class. It takes only one argument- exception class name.

// defines an Exception Class named "ExceptionClass1"<br>
**ExceptionClassDef("ExceptionClass1").eval**

## HandleException
HandleException defines an exception handler for an already declared exception class and executes the try & catch blocks. It takes 3 paramters: 1) exception class name, 2) a Try(), 3) a Catch.

// Defines an exception handler for class "ExceptionClass1". Try block runs. If condition inside Try block results false and its elseClause runs executing the first expression which creates a logic gate AndGate4 and the second expression which throws an error. Since error is thrown third statement of elseClause is skipped and Catch block expressions are executed.<br><br>
**HandleException("ExceptionClass1", Try(List(IF(CheckEqual(Value(true), NOT(Value(true))), List(Assign(LogicGate("NotGate4"), NOT(Input("A")))), List(Assign(LogicGate("AndGate4"), AND(Value(true), Value(true))), ThrowException("ExceptionClass1", "Check failed!"), Assign(LogicGate("OrGate4"), OR(Value(true), Value(false))))))), Catch(List(Assign(LogicGate("NotGate5"), NOT(Value(false)))))).eval**


## ThrowException
ThrowException() throws an exception along with a reason. It takes 2 parameters: 1) name of exception class, 2) reason for the exception

//Throws exception of type "ExceptionClass1" with reason "Check failed!"<br>
**ThrowException("ExceptionClass1", "Check failed!")**


## Try
Try() takes a list of expression. It should be used inside the HandleException() data type. Evaluation of a try block executes all the expressions passed to it  sequentially, unless a ThrowException is executed.

// Try block used inside a HandleException() type, with a list of 3 expressions passed to it as parameters<br> 
**HandleException("ExceptionClass1", Try(List(IF(CheckEqual(Value(true), NOT(Value(true))), List(Assign(LogicGate("NotGate4"), NOT(Input("A")))), List(Assign(LogicGate("AndGate4"), AND(Value(true), Value(true))), ThrowException("ExceptionClass1", "Check failed!"), Assign(LogicGate("OrGate4"), OR(Value(true), Value(false))))))), Catch(List(Assign(LogicGate("NotGate5"), NOT(Value(false)))))).eval**

## Catch
Catch() takes a list of expression. It should be used inside the HandleException() data type. Evaluation of a catch block executes all the expressions passed to it in case a ThrowException is executed in the corresponding try block.

// Catch block used inside a HandleException() type, with a list of 1 Assign expression passed to it as parameters<br> 
**HandleException("ExceptionClass1", Try(List(IF(CheckEqual(Value(true), NOT(Value(true))), List(Assign(LogicGate("NotGate4"), NOT(Input("A")))), List(Assign(LogicGate("AndGate4"), AND(Value(true), Value(true))), ThrowException("ExceptionClass1", "Check failed!"), Assign(LogicGate("OrGate4"), OR(Value(true), Value(false))))))), Catch(List(Assign(LogicGate("NotGate5"), NOT(Value(false)))))).eval**

## CheckEqual
CheckEqual() takes 2 parameters- 1)logic gate 1, 2) logic gate 2. It checks equality of evaluation of two the logic gates. It return true if the evaluation results are same, else it returns false.

//since evaluation of AND(Value(true), Value(false)) and OR(Value(true), Value(false)) is not same, it return false<br>
**CheckEqual(AND(Value(true), Value(false)), OR(Value(true), Value(false))).eval**

//it is used inside IF() as well<br>
**IF(CheckEqual(NOT(Value(false)), NOT(NOT(Value(false)))), List(Assign(LogicGate("NotGate2"), NOT(Input("A")))), List(Assign(LogicGate("OrGate2"), OR(Input("A"), Input("B"))), Assign(LogicGate("AndGate2"), AND(Input("A"), Input("B"))))).eval**

## MAP
MAP() is a monadic function that takes an optimizing transformer function and apply them to boolean exressions in order to simplify boolean expression. To use MAP() for partial evaluation a transformer function should be passed to it. Example of a transformer function is given as below:<br>

For e.g. 
val transformingFunc =  LogicGates => LogicGates = {<br>
      case OR(_, Value(true)) => Value(true)<br>
      case OR(Value(true), _) => Value(true)<br>
      case expr => expr<br>
    }<br>
    
    The above function which transform any boolean expression of the form OR(_, Value(true)) or OR(Value(true), _) to Value(true). Apart from these two, the above transformer function will simply return the expression as it was.  
    
**AND(Input("A"), OR(Input("B"), Input("C")).MAP(transformingFunc)**<br><br>
This will result in a simplified expression i.e. <b>AND(Input("A"), Value(true))</b>

## eval

eval is a function applied on the data types of the language to evaluate those data types. When applied on boolean expressions, simple like - NOT, AND, OR, NOR, NAND, XOR and XNOR or complex that combine simple boolean expressions, eval will either evaluate the entire boolean expression if possible, otherwise it will return the logicgate itself.

**AND(Value(true), Value(false)).eval** will result in **false**

**Assign(LogicGate("AndGate"), AND(Input("A"), Input("B"))).eval**
**LogicGate("AndGate").eval** will result in **AND(Input("A"), Input("B"))** if inputs values are missing

In order to partially evaluate some expression, MAP() should be used.


HW5 Functionality Answeres:
1) Changed **eval** method's return type to Boolean | LogicGates
2) Defined monadic function MAP() which operates over the following constructs-<br>
   * LogicGate()- Fetches gate from environment table 
   * Input()- Fetches Input value from environment table 
   * NOT()/OR()/AND()/NOR()/NAND()/XOR()/XNOR() - recursively applies the transformer function passed to the inputs of these gates, and then to the entire gate
   * MAP() does not alter any remaining language constructs
3) Optimizing transformers examples are mentioned in Scala tests.
    E.g.     
    val func: LogicGates => LogicGates = {<br>
      case OR(_, Value(true)) => Value(true)<br>
      case OR(Value(true), _) => Value(true)<br>
      case AND(Value(false), _) => Value(false)<br>
      case AND(_, Value(false)) => Value(false)<br>
      case NOR(_, Value(true)) => Value(false)<br>
      case NOR(Value(true), _) => Value(false)<br>
      case NAND(Value(false), _) => Value(true)<br>
      case NAND(_, Value(false)) => Value(true)<br>
      case expr => expr<br>
    } <br>
    This function transformers all the given cases to it to logic gate Value(boolean_value).
    
4) Scala tests for partial evaluation are added to the LangugaeTest.scala file

# ERRORS
Following Error will be thrown for corresponding situations:
1. "Undefined gate being evaluated!"- If logic gate being evaluated does not exists
    **LogicGate("someGate").eval**
    
2. "Input to logic gate must be a logic gate and cannot be an Assign"- If Assign() is used inside a boolean expression and is evaluated
    **NOT(Assign(LogicGate("logicGate1"), NOT(Value(true)))).eval**
    
3. "Input to logic gate must be a logic gate and cannot be a Scope"- If Scope() is used inside a boolean expression and is evaluated
    **NOT(Scope(LogicGate("logicGate1"), List(Assing(Input("A"), Value(true))))).eval**
    
4.  Input assignment must be done within a scope of some logic gate!- If trying to Assign an Input without Scope of some logicGate
    **Assign(Input("A"), Value(true)).eval**

5. "No Field(s) exists!" - Trying to assign or evaluate a non-existing field of a class from its constructor or its method

6. "Field assignment must be done within a class constructor or a member function!"- If a field is tried to be assigned outside of a class contructor or class method

7. "Multiple inheritance not allowed!"- If a childclass which already extends a parentclass tries to extend another class 

8. "Incompatible objects!"- If an object is tried to be substituted by another object which neither belongs to its same class or its parent class

9. "InvokeMethod can only be called on Variable() objects!"- If InvokeMethod() function is called data type apart from "Variable"

10. "InvokeField can only be called on Variable() objects!"- If InvokeField() function is called data type apart from "Variable"

11. "Undefined object!"- Use some undefined object

12. "Undefined gate being evaluated!"- Use some undefined logicGate

13. "Undefined Class!"- Use some undefined class

14. "Similar named object already exists!"- Create an object with similar name to an already existing object

15. "No such field(s) exists!"- Assign access modifier to non-existing fields of a class

16. "No such method exists!"- Assign access modifier to non-existing methods of a class

17. "Field already declared as public" - Assign access modifier to fields that have already been assigned an access modifier
<br>"Field already declared as protected"
<br>"Field already declared as private"

18. "Method already declared as public" - Assign access modifier to methods that have already been assigned an access modifier
<br>"Method already declared as protected"
<br>"Method already declared as private"

19. "No such methods exist! or method is neither public nor protected!" - Invoke a method which does not exist or is not declared public/protected

20. "No such field exists! or field is neither public nor protected!"-  Accessing a field which does not exist or is not declared public/protected 

21. "Undefined Interface!" - Extend an undefined interface

22. "Interface cannot extend a Class!" - Extend a class from an interface

23. "Class cannot extend an interface!" - Extend a interface from a class

24. "Some abstract Class Methods are undefined!" - All abstract methods of an abstract class are not overridden by the child class that extends the abstract class

25. "Cyclic Inheritance not allowed!" - cyclic inheritance is detected

26. "Cannot instantiate an abstract class!" - Instantiating an abstract class     

27. "Similar named Exception Class already exists!" - Create an exception class with similar name to an already existing exception class

28. "Exception Class already handled!" - Handling an exception of an exception class type, whose HandleException was already defined before.   

29. "LogicGate evaluated only partially!" - if TestGate(gateName, boolean) is used to compare evaluation of some logicgate to a boolean value, which can only be evaluated partially 
