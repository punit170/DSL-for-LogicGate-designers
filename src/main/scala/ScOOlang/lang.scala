package ScOOlang

import ScOOlang.lang.constructs.LogicGate

import scala.collection.immutable
import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Map, Stack}
//import scala.util.control


object lang:
  //Table to store bindings of logicGateName variables to their respective LogicGate
  private[ScOOlang] val GateTable: mutable.Map[String, constructs] = mutable.Map[String, constructs]()

  //Table to store bindings of logicGateName variables to their respective scope declarations. Scopes are in turn represented by a Map, binding input variables to boolean values
  //Also stores bindings of parameter passed to a method of a class with key = ("className" + "methodName")
  private[ScOOlang] val EnvironmentTableMap: mutable.Map[String, mutable.Map[String, constructs]] = mutable.Map[String, mutable.Map[String, constructs]]()

  // to store transformer functions applied to MAP() and keep track of transformer function in the current scope
  private[ScOOlang] val monadicFuncStack: mutable.Stack[constructs => constructs] = mutable.Stack[constructs => constructs]()

  //logicGateStack - to contain LogicGateNames and help sort the current scope bindings
  //classStack - to contain classNames and help resolve the current class name
  //objectStack- to contain objectNames and help resolve the current object name
  //interfaceStack - to contain interfaceNames and help resolve the current interface name
  //exceptionStack - to contain interfaceNames and help resolve the current exception name
  private[ScOOlang] val logicGateStack, classStack, objectStack, interfaceStack, exceptionStack: mutable.Stack[String] = mutable.Stack[String]()

  //case class to combine Map of fields, List of LogicGates for contructor, List of method Maps, and a reference for a super class
  private[ScOOlang] case class tempClass(field: mutable.Map[String, Boolean|constructs], constructor: List[constructs], methods: mutable.Map[String, List[constructs]], ParentClass: Array[tempClass])

  // Environment Map to Store Classes
  private[ScOOlang] val ClassMap: mutable.Map[String, tempClass] = mutable.Map[String, tempClass]()
  // Environment Map to Objects of defined classes
  private[ScOOlang] val ObjectMap: mutable.Map[String, mutable.Map[String, tempClass]] = mutable.Map[String, mutable.Map[String, tempClass]]()

  //ObjectTable: Binds an objectName to its ClassName
  //InheritanceTable: Binds an childClassName to its parentClassName
  //ImplementsTable: Binds an className to the interfaceName that it implements
  //ExcpetionClassTable: Binds an ExceptionClassName to it reason (defined null until reason is updated by ThrowException)
  private[ScOOlang] val ObjectTable, InheritanceTable, ImplementsTable, ExceptionClassTable: mutable.Map[String, String] =  mutable.Map[String, String]()

  // Maps for access modifiers of classes: Array[0]: For List of Fields, Array[1]: For List of Methods
  private[ScOOlang] val publicMap, privateMap, protectedMap: mutable.Map[String, Array[List[String]]] = mutable.Map[String, Array[List[String]]]()

  // Map to store overridden function names of a Class's Object
  private[ScOOlang] val VirtualDispatchTable: mutable.Map[String, (String, ListBuffer[String])] =mutable.Map[String, (String, ListBuffer[String])]()

  // Map to store abstract methods of abstract classes
  private[ScOOlang] val AbstractClassMap: mutable.Map[String, ListBuffer[String]] = mutable.Map[String, ListBuffer[String]]()

  // Sets to track abstract methods(to be implemented) and concrete methods for a concrete class
  private[ScOOlang] val concreteMethodSet, abstractMethodSet, tempSet: mutable.Set[String] = mutable.Set[String]()

  // container to store interface as a combination of constant fields, abstract methods, and reference of a super interface
  private[ScOOlang] case class tempInterface(field: mutable.Map[String, Boolean|constructs], methods: List[String], ParentInterface: Array[tempInterface])

  // Environment Map to store Interfaces
  private[ScOOlang] val InterfaceMap: mutable.Map[String, tempInterface] = mutable.Map[String, tempInterface]()

  //Map to store ExceptionClass and its HandleException block. Map[ExceptionClassName -> Array[TryBlockExprs, CatchBlockExprs]]
  private[ScOOlang] val ExceptionMap: mutable.Map[String, (List[constructs], List[constructs])] = mutable.Map[String, (List[constructs], List[constructs])]()


  //Defining data types and their evaluations for the language
  enum constructs:
    case Input(iName: String)
    case Value(v: Boolean)
    case LogicGate(logicName: String)
    case NOT(i1: constructs)
    case AND(i1: constructs, i2: constructs)
    case OR(i1: constructs, i2: constructs)
    case XOR(i1: constructs, i2: constructs)
    case NAND(i1: constructs, i2: constructs)
    case NOR(i1: constructs, i2: constructs)
    case XNOR(i1: constructs, i2: constructs)

    // To create logic gate variables or to create input variable bindings of some scope
    case Assign(varGate: LogicGate | Input | Field | ConstantField, gate: constructs)

    // To filter out current scope in order to assign input variables for respective logic gates
    case Scope(gate: LogicGate, exprs: List[constructs])

    // datatype for a Field of a Class
    case Field(fieldName: String)

    // datatype for a Method of a Class- mName: Name of the method, exprs: List of expressions inside the method, params: List of parameters the method takes
    case Method(mName: String, exprs: List[constructs], params: List[Assign])

    // Constructor of a Class- exprs: List of expressions the constructor takes
    case Constructor(exprs: List[constructs])

    // datatype for Inheritance- parameter1 = child class parameter2 = parent class
    case Extend(childClassName: ClassName | InterfaceName, parentClassName: ClassName | InterfaceName)

    // data type to define a class with user-defined name
    case ClassDef(className: String, fields: List[Field], constructor: Constructor, methods: List[Method])

    // To refer a Class
    case ClassName(className: String)

    // data type to create a new object of an already defined class
    case NewObject(className: String, v: Variable)

    // data type to refer an object of a class
    case Variable(objName: String)

    // datatypes for access modifiers. These set access for methods and fields.
    case Public(className: String, fieldNameList: List[String], methodNameList: List[String])
    case Private(className: String, fieldNameList: List[String], methodNameList: List[String])
    case Protected(className: String, fieldNameList: List[String], methodNameList: List[String])

    // data type to change binding of an already created object. obj1: new Object, obj2: object whose binding is to be changed
    // either both objects should be of the same class type OR class type of obj1 should be a subtype of obj2
    case SubstituteObject(obj1: Variable, obj2: Variable)

    // to refer an abstract method inside an Abstract Class
    case AbstractMethod(mName: String)

    //data type to define an abstract class
    case AbstractClassDef(className: String, fields: List[Field], constructor: Constructor, concreteMethods: List[Method], abstractMethods: List[AbstractMethod])

    // data type to refer to a field of an interface
    case ConstantField(fName: String)

    //data type to define an interface
    case InterfaceDef(iName: String, cFields: List[ConstantField], assigns: List[Assign], abstractMethods: List[AbstractMethod])

    //data type to refer an interface
    case InterfaceName(interfaceName: String)

    //data type to implement an interface by a class
    case Implements(className: String, interfaceName: String)

    //data type for an IF construct with a check condition, a thenClause, and an elseClause
    case IF(condition: CheckEqual, thenClause: List[constructs], elseClause: List[constructs])

    //data type to declare an Exception Class
    case ExceptionClassDef(exClassName: String)

    // data type to throw an exception. Paramerter1 - Exception class name, Parameter2- reason for the thrown exception
    case ThrowException(exClassName: String, reason: String)

    // data type to define a try block which takes a list of expressions
    case Try(exprs: List[constructs])

    // data type to define a catch block which takes a list of expressions
    case Catch(exprs: List[constructs])

    //data type to handle an exception similar to a try statement(not a try block) in other languages, which takes exception class name, try block, and a catch block
    case HandleException(exClassName: String, tryBlock: Try, catchBlock: Catch)

    // data type to check equality of evaluation results of two Logic gates. Returns true if evaluation matches
    case CheckEqual(i1: constructs, i2: constructs)

    // Function to evaluate data types
    def eval: Boolean | constructs = this match
      //Fetches input variable for some defined logic gate value from EnvironmentTable
      case Input(iName) =>
        if logicGateStack.nonEmpty && EnvironmentTableMap.contains(logicGateStack.top) && EnvironmentTableMap(logicGateStack.top).contains(iName) then
          EnvironmentTableMap(logicGateStack.top)(iName) match {
                case Value(v) => v
                case lg: LogicGate =>
                  val gateEval = lg.eval
                  gateEval match {
                    case bv: Boolean => bv
                    case _ => lg
                  }
              }
        else
          this
      /*throw new Exception("No bindings exist!")*/

      case Value(v) => v

      //Evaluates boolean result of a defined logic gate
      case LogicGate(logicName) =>
        if checkForLogic(logicName) then
          logicGateStack.push(logicName)
          val res = GateTable(logicName).eval
          logicGateStack.pop()
          res
        else
          false

      //Following cases evaluate respective boolean gates
      case NOT(i1) =>
        val res = i1.eval
        res match
          case eBool: Boolean => !eBool
          case e: constructs => NOT(e)

      case AND(i1, i2) =>
        val res1 = i1.eval
        val res2 = i2.eval
        (res1, res2) match
          case (e1Bool: Boolean, e2Bool: Boolean) => e1Bool & e2Bool
          case (e1Bool: Boolean, e2: constructs) => AND(Value(e1Bool), e2)
          case (e1: constructs, e2Bool: Boolean) => AND(e1, Value(e2Bool))
          case (e1: constructs, e2: constructs) => AND(e1, e2)

      case OR(i1, i2) =>
        val res1 = i1.eval
        val res2 = i2.eval
        (res1, res2) match
          case (e1Bool: Boolean, e2Bool: Boolean) => e1Bool | e2Bool
          case (e1Bool: Boolean, e2: constructs) => OR(Value(e1Bool), e2)
          case (e1: constructs, e2Bool: Boolean) => OR(e1, Value(e2Bool))
          case (e1: constructs, e2: constructs) => OR(e1, e2)

      case XOR(i1, i2) =>
        val res1 = i1.eval
        val res2 = i2.eval
        (res1, res2) match
          case (e1Bool: Boolean, e2Bool: Boolean) => e1Bool ^ e2Bool
          case (e1Bool: Boolean, e2: constructs) => XOR(Value(e1Bool), e2)
          case (e1: constructs, e2Bool: Boolean) => XOR(e1, Value(e2Bool))
          case (e1: constructs, e2: constructs) => XOR(e1, e2)

      case NAND(i1, i2) => NOT(AND(i1, i2)).eval
      case NOR(i1, i2) => NOT(OR(i1, i2)).eval
      case XNOR(i1, i2) => NOT(XOR(i1, i2)).eval

      //Creates bindings for logicgate variable, input variable, or field variables
      case Assign(varGate, gate) =>
        val s = varGate.evalName
        varGate match
          // creating a logic gate variable if first argument is LogicGate type
          case varGate: LogicGate =>
            if GateTable.contains(varGate.evalName) then
              GateTable -= s
            GateTable += s -> gate

          // creating a input variable for the current logic gate if first argument is Input type
          case varGate: Input =>
            if logicGateStack.nonEmpty then

              val inputValPair: (Value|LogicGate|Null) = {
                gate match{
                  case v: Value => v
                  case lg: LogicGate => lg
                  case _ =>
                    val gateEval = gate.eval
                    gateEval match{
                      case boolVal: Boolean => Value(boolVal)
                      null
                    }
                }
              }

              if inputValPair != null then
                val tempLogicName = logicGateStack.top
                if EnvironmentTableMap.contains(tempLogicName) then
                  val tempMap: mutable.Map[String, constructs] = EnvironmentTableMap(tempLogicName)

                  tempMap += varGate.evalName -> inputValPair
                  EnvironmentTableMap -= tempLogicName
                  EnvironmentTableMap += tempLogicName -> tempMap

                else
                  val tempMap: mutable.Map[String, constructs] = mutable.Map[String, constructs]()

                  tempMap += s -> inputValPair
                  EnvironmentTableMap += tempLogicName -> tempMap

            else
              throw new Exception("Input assignment must be done within a scope of some logic gate!")

          // changing bindings of field variables of some class via class constructor OR class method call
          case varGate: Field =>
            if classStack.nonEmpty then
              val curr_ClassName = classStack.top
              val curr_ObjectName = objectStack.top
              val fMap = ObjectMap(curr_ClassName)(curr_ObjectName).field
              if fMap.contains(varGate.evalName) then
                fMap(varGate.evalName) = gate.eval
              else
                throw new Exception("No Field(s) exists!")
            else
              throw new Exception("Field assignment must be done within a class constructor or a member function!")

          case varGate: ConstantField =>
            if interfaceStack.nonEmpty then
              val curr_interface = interfaceStack.top
              val constantFieldMap = InterfaceMap(curr_interface).field
              if constantFieldMap.contains(varGate.evalName) then
                constantFieldMap(varGate.evalName) = gate.eval
              else
                constantFieldMap += varGate.evalName -> gate.eval
        false

      case Scope(gate, exprs) =>
        // evaluating list of expressions for current logic gate
        for (expr <- exprs)
          expr match
            // For Assign, update stack to current logic gate
            case e: Assign =>
              logicGateStack.push(gate.evalName)
              e.eval
              logicGateStack.pop()

            case _ => expr.eval
        false

      // To fetch a Field value from ObjectMap
      case Field(fieldName) =>
        val fMap = ObjectMap(classStack.top)(objectStack.top).field
        if fMap.contains(fieldName) then
          fMap(fieldName)
        else
          val res = getParentField(classStack.top, fieldName)
          if ObjectTable.contains("_T_") then
            ObjectMap(getClassName("_T_")) -= "_T_"
            ObjectTable -= "_T_"
          res

      // data type to evaluate ConstantField of some interface
      case ConstantField(fName) =>
        getInterfaceField(classStack.top, fName)

      // evaluates list of expressions inside the constructor
      case Constructor(exprs) =>
        for (expr <- exprs)
          expr.eval
        false

      // function evaluation when called from an object of a class
      case Method(mName: String, exprs: List[constructs], paramList: List[Assign]) =>
        //updating parameter values passed to the function
        val scopeName = classStack.top + mName
        if EnvironmentTableMap.contains(scopeName) then
          Scope(LogicGate(scopeName),paramList).eval
        //evaluating all expressions and returning the last expression value
        val i = exprs.iterator.slice(0, exprs.length-1)
        while(i.hasNext)
          i.next.eval
        exprs.last.eval

      //  Defines a new class in class map- aggregating field map, constructor list, methods' map
      case ClassDef(className, fields, constructor, methods) =>

        //field Map
        val fieldMap = mutable.Map[String, Boolean|constructs]()
        for (field <- fields)
          fieldMap += field.evalName -> false

        //constructor list
        val cList = constructor.getList

        //methods' Map
        val methodMap = mutable.Map[String, List[constructs]]()
        for (method <- methods)
          val mName = method.evalName
          val mList = method.getList
          methodMap += mName -> mList

        //methods' parameters evalutaion
        for (method <- methods)
          val assignList = method._3
          val scopeName = className + method._1
          Scope(LogicGate(scopeName), assignList).eval

        //ParentReference
        val parentRef = new Array[tempClass](1)
        parentRef(0) = null

        val newObj = tempClass(fieldMap, cList, methodMap, parentRef)
        ClassMap += className -> newObj
        false

      // Creates a new object of class className and binds it to user-defined name passed in datatype Variable
      case NewObject(className: String, v: Variable) =>
        if AbstractClassMap.contains(className) && !tempSet.contains("_T_") then
          throw new Exception("Cannot instantiate an abstract class!")

        checkForObject(v.evalName)
        classStack.push(checkForClass(className))
        val objName = v.evalName

        val newFieldMap = ClassMap(className).field.clone()
        val newConstructorList = ClassMap(className).constructor
        val newFunctionList = ClassMap(className).methods.clone()
        val tempParentClass = ClassMap(className).ParentClass

        val tempObj = tempClass(newFieldMap, newConstructorList, newFunctionList, tempParentClass)

        if ObjectMap.contains(className) then
          ObjectTable += objName -> className
          ObjectMap(className) += objName -> tempObj
        else
          val tempMap: mutable.Map[String, tempClass] = mutable.Map[String, tempClass]()
          tempMap += objName -> tempObj
          ObjectMap += className -> tempMap
          ObjectTable += objName -> className

        objectStack.push(objName)
        Constructor(tempObj.constructor).eval
        objectStack.pop
        classStack.pop
        true

      // To define access for fields and methods of some class as Public
      case Public(className, fieldNameList, methodNameList) =>
        publicMap += className -> permitForAccessModifier(className, fieldNameList, methodNameList)
        false
      // To define access for fields and methods of some class as Private
      case Private(className, fieldNameList, methodNameList) =>
        privateMap += className -> permitForAccessModifier(className, fieldNameList, methodNameList)
        false
      //// To define access for fields and methods of some class as Protected
      case Protected(className, fieldNameList, methodNameList) =>
        protectedMap += className -> permitForAccessModifier(className, fieldNameList, methodNameList)
        false

      // Defining Inheritance. Extend can be used either to extend a parent class from a child class OR to extend a parent interface from a child interface
      // If parameters are of type ClassName() - Class cName extends Class pName.cName- child class name, pName- parent class name
      // If parameters are of type InterfaceName() - Interface cName extends Interface pName.cName- child interface name, pName- parent interface name
      case Extend(childClassName, parentClassName) =>
        val cName = childClassName.evalName
        val pName = parentClassName.evalName
        if checkForCircularInheritance(cName, pName) then throw new Exception("Cyclic Inheritance not allowed!")
        childClassName match
          case _: ClassName =>
            parentClassName match
              case _: ClassName =>
                if ClassMap(checkForClass(cName)).ParentClass(0) != null then
                  throw new Exception("Multiple inheritance not allowed!")
                else
                  if AbstractClassMap.contains(pName) && !AbstractClassMap.contains(cName) then
                    if !checkForAbstractClass(cName, pName) then
                      throw new Exception("Some abstract Class Methods are undefined!")
                  ClassMap(cName).ParentClass(0) = ClassMap(checkForClass(pName))
                  InheritanceTable(cName) = pName
                false
              case _: InterfaceName =>
                throw new Exception("Class cannot extend an interface!")

          case _: InterfaceName =>
            parentClassName match
              case _: ClassName =>
                throw new Exception("Interface cannot extend a Class!")
              case _: InterfaceName =>
                if InterfaceMap(cName).ParentInterface(0) == null then
                  InterfaceMap(cName).ParentInterface(0) = InterfaceMap(checkForInterface(pName))
                  InheritanceTable += cName -> pName
                else
                  throw new Exception("Multiple interface extensions not allowed!")
                false

      // Changes bindings of obj1 to now refer to obj2
      case SubstituteObject(obj1, obj2) =>
        val obj1Name = obj1.evalName
        val obj2Name = obj2.evalName
        val obj1ClassName = getClassName(obj1Name)
        val obj2ClassName = getClassName(obj2Name)

        // If obj2 is of same class type as obj1
        if obj2ClassName == obj1ClassName then
          ObjectMap(obj1ClassName) -= obj1Name
          ObjectMap(obj1ClassName) += obj1Name ->  ObjectMap(obj2ClassName)(obj2Name)
          println(obj1Name + " now equals " + obj2Name)
          true
        // If obj1 extends obj2
        else if checkForInheritance(obj2ClassName, obj1ClassName) then
          val childClass = obj2ClassName
          val parentClass = obj1ClassName
          val parentObject = obj1Name
          val childObject = obj2Name

          for (parentMethod <- ObjectMap(parentClass)(parentObject).methods)
            for (childMethod <- ObjectMap(childClass)(childObject).methods)
              if childMethod._1 == parentMethod._1 then
                if VirtualDispatchTable.contains(parentObject) then
                  VirtualDispatchTable(parentObject)._2 += childMethod._1
                else
                  val tempList: ListBuffer[String] = ListBuffer[String]()
                  tempList += childMethod._1
                  VirtualDispatchTable += parentObject -> (childObject, tempList)
          true
        else
          throw new Exception("Incompatible objects!")

      //  Defines a normal class and binds abstract methods to the abstract class name in the abstract class map
      case AbstractClassDef(className, fields, constructor, concreteMethods, abstractMethods) =>
        ClassDef(className, fields, constructor, concreteMethods).eval

        val abstractMethodList: ListBuffer[String] = ListBuffer[String]()

        if abstractMethods.isEmpty then
          throw new Exception("Abstract Class should contain atleast one abstract method!")
        for (abstractMethod <- abstractMethods)
          abstractMethodList += abstractMethod.evalName

        AbstractClassMap += className -> abstractMethodList
        false

      //Defines a new interface in interface map- aggregating constant field map, constructor list, abstract methods' list
      //assigns- a list of Assign() to set bind the value of constant fields
      case InterfaceDef(interfaceName, _, assigns,abstractMethods) =>

        //ConstantFieldsMap
        val cFieldMap: mutable.Map[String, Boolean|constructs] = mutable.Map[String, Boolean|constructs]()

        //abstractMethodList
        val abstractMethodList: ListBuffer[String] = ListBuffer[String]()
        for (abstractMethod <- abstractMethods)
          abstractMethodList += abstractMethod.evalName

        //ParentInterface reference
        val parentRef = new Array[tempInterface](1)
        parentRef(0) = null

        //interface container
        val tempI: tempInterface = tempInterface(cFieldMap, abstractMethodList.toList, parentRef)

        //Create binding in interface Map
        InterfaceMap += interfaceName -> tempI

        // Assign Constant Fields in the Interface
        interfaceStack.push(interfaceName)
        for (assign <- assigns)
          assign.eval
        interfaceStack.pop()

        false

      //Defining implementation of an interface by a class
      case Implements(className, interfaceName) =>
        // creating a set of all concrete methods of the class
        concreteMethodSet ++= ClassMap(checkForClass(className)).methods.keys.toSet
        // creating a set of all abstract methods of the interface and checking that abstract method set should be subset of concrete method set
        if checkForInterfaceMethods(interfaceName) then
          ImplementsTable += className -> interfaceName
        else
          throw new Exception("Some interface methods are undefined!")
        false

      // defines an if condition.
      // If condition is true it runs thenClause, else it runs elseClause
      // In case condition is only partially evaluated, thenClause and elseClause are also partially evaluated
      case IF(condition, thenClause, elseClause) =>
        condition.eval match
          case cr: Boolean =>
            if cr then
              for (expr <- thenClause)
              //Evaluate only if exception is not thrown, else skip evaluation till catch block
                if exceptionStack.nonEmpty then
                  if ExceptionClassTable(exceptionStack.top) == null then
                    expr.eval
                else
                  expr.eval
              true
            else
              for (expr <- elseClause)
                if exceptionStack.nonEmpty then
                  if ExceptionClassTable(exceptionStack.top) == null then
                    expr.eval
                else
                  expr.eval
              false
          case _ => this


      // declare a user-defined exception class
      case ExceptionClassDef(exClassName) =>
        if !ExceptionClassTable.contains(exClassName) then
          ExceptionClassTable += exClassName -> null
        else
          throw new Exception("Similar named Exception Class already exists!")
        false

      // define an exception handler for an already declared exception class and execute try and catch blocks
      case HandleException(exClassName, tryBlock, catchBlock) =>
        if !ExceptionMap.contains(exClassName) then
          ExceptionMap += exClassName -> (tryBlock.exprs, catchBlock.exprs)
          exceptionStack.push(exClassName)
          tryBlock.eval
          catchBlock.eval
        else
          throw new Exception("Exception Class already handled!")
        false

      // updates reason for an exception class indicating an exception has been thrown
      case ThrowException(exClassName, reason)  =>
        ExceptionClassTable(exClassName) = reason
        false

      // evaluating expressions sequentially inside a try block unless a ThrowException is executed
      case Try(exprs) =>
        for (expr<-exprs)
          if  ExceptionClassTable(exceptionStack.top) == null then
            expr.eval
        false

      //  evaluating expressions of catch block in case a ThrowException is executed in the corresponding try block
      case Catch(exprs) =>
        if ExceptionClassTable(exceptionStack.top) !=null then
          for (expr <- exprs)
            expr.eval
          println("Exception Caught!")
          println("ExceptionType: " + exceptionStack.top)
          println("Reason: " + ExceptionClassTable(exceptionStack.top))

          exceptionStack.pop()
        false

      // Checks equality of evaluation of two logic gates i1 and i2. Return true if the evaluation results are same, else returns false
      // Incase evaluation of i1 or i2 is only partial, return the partially evaluated expression
      case CheckEqual(i1, i2) =>
        //if i1.eval == i2.eval then true else false
        val i1Res = i1.eval
        val i2Res = i2.eval
        (i1Res,i2Res) match
          case (r1: Boolean, r2: Boolean) => if (r1 == r2) true else false
          case (r1Bool: Boolean,r2: constructs) => CheckEqual(Value(r1Bool),r2)
          case (r1: constructs,r2Bool: Boolean) => CheckEqual(r1,Value(r2Bool))
          case (r1: constructs,r2: constructs) => CheckEqual(r1,r2)

      case _ => false

    // To invoke method of some object
    def InvokeMethod(mName: String, paramList: List[Assign]): Boolean = this match
      case Variable(v) =>
        // If parent object is substituted by a child object then call overridden function of the child object
        if VirtualDispatchTable.contains(v) && VirtualDispatchTable(v)._2.contains(mName)then
          //println("Overrided!" + "Calling Object " + VirtualDispatchTable(v)._1 + "'s method" + mName)
          println("Calling overrided function!")
          Variable(VirtualDispatchTable(v)._1).InvokeMethod(mName, paramList)

        // if method exists in the current class, invoke it according to access modifier
        else
          val curr_className = getClassName(v)
          classStack.push(curr_className)
          objectStack.push(v)
          logicGateStack.push(curr_className+mName)
          if ImplementsTable.contains(curr_className) then
            interfaceStack.push(ImplementsTable(curr_className))

          if checkIfPublic(curr_className, mName, "Method") || checkIfProtected(curr_className, mName, "Method") then
            val mList = ObjectMap(curr_className)(v).methods(mName)
            val res = Method(mName, mList,paramList).eval
            classStack.pop
            objectStack.pop
            logicGateStack.pop
            interfaceStack.empty
            res match
              case v: Boolean => v
              case _ => throw new Exception

          // if method exists in some parent class
          else
            val mList = getParentMethod(curr_className, mName)
            val res = Method(mName, mList,paramList).eval
            if ObjectTable.contains("_T_") then
              ObjectMap(getClassName("_T_")) -= "_T_"
              ObjectTable -= "_T_"
            classStack.empty
            objectStack.empty
            logicGateStack.empty
            interfaceStack.empty
            res match
              case v: Boolean => v
              case _ => throw new Exception
      case _ => throw new Exception("InvokeMethod can only be called on Variable() objects!")

    // To invoke field of some object
    def InvokeField(fName: String): Boolean = this match
      case Variable(v) =>
        val curr_className = getClassName(v)
        classStack.push(curr_className)
        objectStack.push(v)

        // if field exists in the current class, fetch it according to access modifier
        if checkIfPublic(curr_className, fName, "Field") || checkIfProtected(curr_className, fName, "Field") then
          val res = Field(fName).eval
          classStack.pop
          objectStack.pop
          res match
            case v: Boolean => v
            case _ => throw new Exception

        // try to find and fetch field in some parent class. Else find and fetch it from some interface implemented by current class or any super classes
        else
          val res = {
            try
            {
              getParentField(curr_className, fName)
            }
            catch
            {
              case _: Exception => getInterfaceField(curr_className, fName)
            }
          }
          if ObjectTable.contains("_T_") then
            ObjectMap(getClassName("_T_")) -= "_T_"
            ObjectTable -= "_T_"
          classStack.empty
          objectStack.empty
          res

      case _ => throw new Exception("InvokeField can only be called on Variable() objects!")

    // Returns class name of some object
    def getClassName(objName: String): String = {
      ObjectTable.getOrElse(objName, throw new Exception("Undefined object!"))
    }

    // to evaluate names of certain data types
    def evalName: String = this match
      case Input(iName) => iName
      case LogicGate(logicName) => logicName
      case Field(fieldName) => fieldName
      case Variable(objName) => objName
      case Method(mName,_,_) => mName
      case AbstractMethod(mName) => mName
      case ConstantField(fName) => fName
      case ClassName(cName) => cName
      case InterfaceName(iName) => iName
      case _ => null

    // To return list of logicGate expressions
    def getList: List[constructs] = this match
      case Constructor(exprs) => exprs
      case Method(_, exprs,_) => exprs
      case _ => null

    //monadic function that simplifies boolean expression
    def MAP(f: constructs => constructs): constructs =
      monadicFuncStack.push(f)
      this match
        case LogicGate(logicName) =>
          val expr = GateTable.getOrElse(logicName, throw new Exception("undefined LogicGate"))
          logicGateStack.push(logicName)
          val res = expr.MAP(f)
          logicGateStack.pop()
          res
        case input: Input =>
          input.eval match
            case iBool: Boolean => Value(iBool)
            case i: constructs => i
        case NOT(i1) =>
          val res = NOT(f(i1.MAP(f)))
          res.eval match
            case eBool: Boolean => Value(eBool)
            case _ => f(res)
        case AND(i1, i2) =>
          val res = AND(f(i1.MAP(f)), f(i2.MAP(f)))
          res.eval match
            case eBool: Boolean => Value(eBool)
            case _ => f(res)
        case OR(i1, i2) =>
          val res = OR(f(i1.MAP(f)), f(i2.MAP(f)))
          res.eval match
            case eBool: Boolean => Value(eBool)
            case _ => f(res)
        case NAND(i1, i2) =>
          val res = NAND(f(i1.MAP(f)), f(i2.MAP(f)))
          res.eval match
            case eBool: Boolean => Value(eBool)
            case _ => f(res)
        case NOR(i1, i2) =>
          val res = NOR(f(i1.MAP(f)), f(i2.MAP(f)))
          res.eval match
            case eBool: Boolean => Value(eBool)
            case _ => f(res)
        case XOR(i1, i2) =>
          val res = XOR(f(i1.MAP(f)), f(i2.MAP(f)))
          res.eval match
            case eBool: Boolean => Value(eBool)
            case _ => f(res)
        case XNOR(i1, i2) =>
          val res = XNOR(f(i1.MAP(f)), f(i2.MAP(f)))
          res.eval match
            case eBool: Boolean => Value(eBool)
            case _ => f(res)

        case CheckEqual(i1, i2) =>
          CheckEqual(i1.MAP(f), i2.MAP(f))
        case IF(condition, thenClause, elseClause) =>
          condition.MAP(f) match
            case c: CheckEqual =>
              val updatedThenClause, updatedElseClause: ListBuffer[constructs] = ListBuffer[constructs]()
              for (expr <- thenClause)
                updatedThenClause += expr.MAP(f)
              for (expr <- elseClause)
                updatedElseClause += expr.MAP(f)
              IF(c, updatedThenClause.toList, updatedElseClause.toList)
            case _ => this

        case _ => this


    def applyMapFunc(i: constructs): constructs =
      if monadicFuncStack.isEmpty then i else monadicFuncStack.top(i)

    // to check if gate being evaluated exists
    def checkForLogic(logicName: String): Boolean =
      GateTable.getOrElse(logicName, throw new Exception("Undefined gate being evaluated!"))
      true

    // to check if a class already exists
    def checkForClass(className: String): String = {
      if ClassMap.contains(className) then
        className
      else
        throw new Exception("Undefined Class!")
    }

    // to check if a named object already exists
    def checkForObject(objectName: String): Unit = {
      if ObjectTable.contains(objectName) then
        println(objectName)
        println()
        for (i <- ClassMap)
          println(i)

        for (i <- ObjectMap)
          for (j <- ObjectMap(i._1))
            println(j)
        throw new Exception("Similar named object already exists!")
    }

    // to check and return interface name if it already exists
    def checkForInterface(iName: String): String = {
      if InterfaceMap.contains(iName) then
        iName
      else
        throw new Exception("Undefined Interface!")
    }

    // to check if access modifier can be applied to the field or method of a class
    def permitForAccessModifier(className: String, fields: List[String], methods: List[String]): Array[List[String]] =
      // throw error if field or method passed does not exist
      for (field <- fields)
        if !ClassMap(className).field.contains(field) then throw new Exception("No such field(s) exists!")
      for (method <- methods)
        if !ClassMap(className).methods.contains(method) then throw new Exception("No such method exists!")

      // check for field
      for (field <- fields)
        if checkIfPublic(className, field, "Field") then
          throw new Exception("Field already declared as public")
        else if checkIfPrivate(className, field, "Field") then
          throw new Exception("Field already declared as private")
        else if checkIfProtected(className, field, "Field") then
          throw new Exception("Field already declared as protected")

      // check for methods
      for (method <- methods)
        if checkIfPublic(className, method, "Method") then
          throw new Exception("Method already declared as public")
        else if checkIfPrivate(className, method, "Method") then
          throw new Exception("Method already declared as public")
        else if checkIfProtected(className, method, "Method") then
          throw new Exception("Method already declared as public")
      // return array with index1- field list and index2- method list
      val members: Array[List[String]] = Array(fields, methods)
      members

    // check if a field or method has access modifier defined Public
    //memberName- Name of the field or Method, memberType = "Field"|"Method"
    def checkIfPublic(className: String, memberName: String, memberType: String): Boolean =
      if memberType == "Field" then
        if publicMap.contains(className) && publicMap(className)(0).contains(memberName) then
          true
        else
          false
      else if memberType == "Method" then
        if publicMap.contains(className) && publicMap(className)(1).contains(memberName) then
          true
        else
          false
      else
        false

    // check if a field or method has access modifier defined Protected
    //memberName- Name of the field or Method, memberType = "Field"|"Method"
    def checkIfProtected(className: String, memberName: String, memberType: String): Boolean =
      if memberType == "Field" then
        if protectedMap.contains(className) && protectedMap(className)(0).contains(memberName) then
          true
        else
          false
      else if memberType == "Method" then
        if protectedMap.contains(className) && protectedMap(className)(1).contains(memberName) then
          true
        else
          false
      else
        false

    // check if a field or method has access modifier defined Private
    //memberName- Name of the field or Method, memberType = "Field"|"Method"
    def checkIfPrivate(className: String, memberName: String, memberType: String): Boolean =
      if memberType == "Field" then
        if privateMap.contains(className) && privateMap(className)(0).contains(memberName) then
          true
        else
          false
      else if memberType == "Method" then
        if privateMap.contains(className) && privateMap(className)(1).contains(memberName) then
          true
        else
          false
      else
        false

    // Check if class childClass inherits from class parentClass
    def checkForInheritance(childClass: String, parentClass: String): Boolean =
      if InheritanceTable.contains(childClass) then
        if InheritanceTable(childClass) == parentClass then
          true
        else
          false
      else
        false

    def checkForCircularInheritance(childClass: String, parentClass: String): Boolean =
      if childClass == parentClass then
        true
      else if InheritanceTable.contains(parentClass) then
        if InheritanceTable(parentClass) == childClass then
          true
        else
          checkForCircularInheritance(childClass, InheritanceTable(parentClass))
      else
        false


    // Check if all abstract methods of an abstract class and its abstract super classes are overridden in the concrete child class
    def checkForAbstractClass(cName: String, pName: String): Boolean =
      if cName == null then
        if abstractMethodSet.subsetOf(concreteMethodSet) then
          abstractMethodSet.clear()
          concreteMethodSet.clear()
          println("All abstract methods of the abstract class successfully overridden!")
          return true
        else
          abstractMethodSet.clear()
          concreteMethodSet.clear()
          return false
      for (MethodName <- ClassMap(cName).methods.keys)
        concreteMethodSet += MethodName
      if AbstractClassMap.contains(cName) then
        for (abstractMethodName <- AbstractClassMap(cName))
          abstractMethodSet += abstractMethodName
      checkForAbstractClass(pName, InheritanceTable.getOrElse(pName, null))

    // check if all abstract methods of an interface and its super interfaces are overridden in the class implementing the interface
    def checkForInterfaceMethods(iName: String): Boolean =
      if iName == null then
        println(concreteMethodSet)
        println(abstractMethodSet)
        if abstractMethodSet.subsetOf(concreteMethodSet) then
          abstractMethodSet.clear()
          concreteMethodSet.clear()
          return true
        else
          return false
      abstractMethodSet ++= InterfaceMap(iName).methods.toSet
      checkForInterfaceMethods(InheritanceTable.getOrElse(iName, null))


    // To get a method's list of expressions from parent class if that method does not exist in current class
    def getParentMethod(cName: String, mName: String): List[constructs] =
      val tempPClassRef = ClassMap(cName).ParentClass(0)
      val pName = InheritanceTable.getOrElse(cName, "")
      if tempPClassRef == null then
        throw new Exception("No such methods exist! or method is neither public nor protected!")
      else if tempPClassRef.methods.contains(mName) && (checkIfPublic(pName, mName, "Method") || checkIfProtected(pName, mName, "Method")) then
        tempSet += "_T_"
        NewObject(pName, Variable("_T_")).eval
        tempSet.clear()
        classStack.push(pName)
        objectStack.push("_T_")
        logicGateStack.push(pName+mName)
        if ImplementsTable.contains(pName) then interfaceStack.push(ImplementsTable(pName))
        tempPClassRef.methods(mName)
      else
        getParentMethod(pName, mName)

    // To get value of a field  from parent class if that field does not exist in current class
    def getParentField(cName: String, fName: String): Boolean =
      val tempPClassRef = ClassMap(cName).ParentClass(0)
      val pName = InheritanceTable.getOrElse(cName, "")
      if tempPClassRef == null then
        throw new Exception("No such field exists! or field is neither public nor protected!")
      else if tempPClassRef.field.contains(fName) && (checkIfPublic(pName, fName, "Field") || checkIfProtected(pName, fName, "Field")) then
        tempSet += "_T_"
        NewObject(pName, Variable("_T_")).eval
        tempSet.clear()
        classStack.push(pName)
        objectStack.push("_T_")
        //val res = tempPClassRef.field(fName)
        val res = ObjectMap(classStack.top)(objectStack.top).field(fName)
        classStack.pop()
        objectStack.pop()
        res match
          case v: Boolean => v
          case _ => throw new Exception
      else
        getParentField(pName, fName)

    // Fetch a constant field from interface or its super interfaces
    def getInterfaceField(cName: String, fName: String): Boolean =
      if ImplementsTable.contains(cName) && InterfaceMap(ImplementsTable(cName)).field.contains(fName)then
        println("Invoking interface constant field!")
        InterfaceMap(ImplementsTable(cName)).field(fName) match
          case v: Boolean => v
          case _ => throw new Exception
      else if InheritanceTable.contains(cName) then
        getInterfaceField(InheritanceTable(cName), fName)
      else
        throw new Exception("No such field exists! or field is neither public nor protected!")


  def TestGate(gateName: String, checkVal: Boolean): Boolean =
    val gateEval = constructs.LogicGate(gateName).eval
    gateEval match
      case v: Boolean =>
        if v == checkVal then
          true
        else
          false
      case _ => throw new Exception("LogicGate evaluated only partially!")

  // to get input variable value for a defined logic gate
  def getInputVal(logicName: String, varName: String): Boolean = {
    if EnvironmentTableMap.contains(logicName) then
      val fetchedInputValue = EnvironmentTableMap(logicName).getOrElse(varName, throw new Exception("Variable does not exist!")) match{
        case constructs.Value(boolVal) => boolVal
        case _ => throw new Exception(s"Undefined inputs for input $logicName-$varName!")
      }
      fetchedInputValue
    else
      throw new Exception("Input value invocation for an undefined logic Gate")
  }

  //main function
  /*@main def runBooleanLanguage: Any =
    import LogicGates.*

    println(GateTable)

    println(ExceptionClassTable)

    println(ExceptionMap)

    println(ImplementsTable)

    println(InterfaceMap)

    for (i <- EnvironmentTableMap)
      println(i)

    for (i <- VirtualDispatchTable)
      println(i)

    for(i <- ClassMap)
      println(i)

    for (i <- ObjectMap)
      for(j <- ObjectMap(i._1))
        println(j)

    println()
    println("PUBLIC:")
    for(i <- publicMap)
      for(j <- publicMap(i._1))
        print(i._1)
        print(":")
        println(j)

    println()
    println("PRIVATE:")
    for (i <- privateMap)
      for (j <- privateMap(i._1))
        print(i._1)
        print(":")
        println(j)

    println()
    println("PROTECTED:")
    for (i <- protectedMap)
      for (j <- protectedMap(i._1))
        print(i._1)
        print(":")
        println(j)
  */