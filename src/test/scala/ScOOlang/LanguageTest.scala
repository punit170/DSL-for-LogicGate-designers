package ScOOlang

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

//import ScOOlang.main.*
//import ScOOlang.main.LogicGates.*
import lang.*
import lang.constructs.*
import scala.collection.immutable.List

class LanguageTest extends AnyFlatSpec with Matchers {
  behavior of "ScOOlang for logical expressions!"
  //#1
  it should "Assign and test the NOT gate evaluation" in {
    Assign(LogicGate("NotGate"), NOT(Value(true))).eval
    TestGate("NotGate", false)
  }
  //#2
  it should "Assign and test the OR gate evaluation" in {
    Assign(LogicGate("OrGate"), OR(Value(true), Value(false))).eval
    TestGate("OrGate", true)
  }
  //#3
  it should "Assign and test the AND gate evaluation" in {
    Assign(LogicGate("AndGate"), AND(Value(true), Value(false))).eval
    TestGate("AndGate", false)
  }
  //#4
  it should "Assign and test the NOR gate evaluation" in {
    Assign(LogicGate("NorGate"), NOR(Value(true), Value(false))).eval
    TestGate("NorGate", false)
  }
  //#5
  it should "Assign and test the NAND gate evaluation" in {
    Assign(LogicGate("NANDGate"), NAND(Value(true), Value(true))).eval
    TestGate("NANDGate", false)
  }
  //#6
  it should "Assign and test the XOR gate evaluation" in {
    Assign(LogicGate("XorGate"), XOR(Value(true), Value(false))).eval
    TestGate("XorGate", true)
  }
  //#7
  it should "Assign and test the XNOR gate evaluation" in {
    Assign(LogicGate("XNorGate"), XNOR(Value(true), Value(false))).eval
    TestGate("XNorGate", false)
  }
  //#8
  it should "abide by the De Morgan's law" in {
    Assign(LogicGate("logicGate1"), NOT(OR(Value(true), Value(false)))).eval
    Assign(LogicGate("logicGate2"), AND(NOT(Value(true)), NOT(Value(false)))).eval
    assert(LogicGate("logicGate1").eval  == LogicGate("logicGate2").eval)
  }
  //#9
  it should "create a logic gate named logicGate3" in {
    Assign(LogicGate("logicGate3"), NOT(XOR(Input("A"), Input("B")))).eval
    GateTable.contains("logicGate3") shouldBe true
  }
  //#10
  it should "change a gate bound to name LogicGate4" in {
    //Scope(LogicGate("logicGate4"), List(Assign(Input("A"), Value(true)), Assign(Input("B"), Value(false)))).eval
    Assign(LogicGate("logicGate4"), OR(Input("A"), Input("B"))).eval
    Assign(LogicGate("logicGate4"), AND(Input("A"), Input("B"))).eval
    GateTable("logicGate4") shouldBe AND(Input("A"), Input("B"))
  }
  //#11
  it should "create inputs for logicGate5" in {
    Scope(LogicGate("logicGate5"), List(Assign(Input("A"), Value(true)), Assign(Input("B"), Value(true)))).eval
    (EnvironmentTableMap("logicGate5").contains("A") && EnvironmentTableMap("logicGate5").contains("B")) shouldBe true
  }
  //#12
  it should "obey the ShefferStroke's axiom" in {
    Assign(LogicGate("ShefferStroke"), NAND(NAND(NAND(Input("A"), Input("B")), Input("C")), NAND(NAND(NAND(Input("A"), Input("C")), Input("A")), Input("A")))).eval
    Scope(LogicGate("ShefferStroke"), List(Assign(Input("A"), Value(true)), Assign(Input("B"), Value(true)), Assign(Input("C"), Value(false)))).eval
    assert(TestGate("ShefferStroke", getInputVal("ShefferStroke","C")))
  }
  //#13
  it should "obey the Edward Vermilye Huntington's axiom" in {
    Assign(LogicGate("EVH"), OR(NOT(OR(NOT(Input("X")), Input("Y"))), NOT(OR(NOT(Input("X")), NOT(Input("Y")))))).eval
    Scope(LogicGate("EVH"), List(Assign(Input("X"), Value(true)), Assign(Input("Y"), Value(false)))).eval
    assert(TestGate("EVH", getInputVal("EVH", "X")))
  }
  //#14
  it should "obey the Meredith's ShefferStroke axiom" in {
    Assign(LogicGate("Meredith'sShefferStroke"), NAND(NAND(Input("X"), Input("X")), NAND(Input("X"), Input("Y")))).eval
    Scope(LogicGate("Meredith'sShefferStroke"), List(Assign(Input("X"), Value(true)), Assign(Input("Y"), Value(false)))).eval
    assert(TestGate("Meredith'sShefferStroke", getInputVal("Meredith'sShefferStroke", "X")))
  }
  //#15
  it should "obey the 1-basis axiom" in {
    Assign(LogicGate("PQ1basis"), NOT(OR(NOT(OR(NOT(OR(Input("X"), Input("Y"))), Input("Z"))), NOT(OR(Input("X"), NOT(OR(NOT(Input("Z")), NOT(OR(Input("Z"), Input("U")))))))))).eval
    Scope(LogicGate("PQ1basis"), List(Assign(Input("X"), Value(false)), Assign(Input("Y"), Value(false)), Assign(Input("Z"), Value(false)), Assign(Input("U"), Value(false)))).eval
    assert(TestGate("PQ1basis", getInputVal("PQ1basis", "Z")))
  }
  //#16
  it should "throw an error for an Undefined Logic Gate being evaluated" in {
    assertThrows[Exception](LogicGate("logicGate5").eval)
  }
  //#17
  it should "throw an error for assigning an Input outside Scope of some defined LogicGate" in {
    assertThrows[Exception](Assign(Input("A"), Value(true)).eval)
  }

  //#18
  it should "create a class named Class1" in {
    ClassDef("Class1", List(Field("X"), Field("Y")), Constructor(List(Assign(Field("X"), Value(false)), Assign(Field("Y"), Value(true)))), List(Method("m1", List(Assign(Field("X"), NOT(Field("X"))), Input("x")), List(Assign(Input("x"), Value(false)))), Method("m2", List(AND(Field("X"), Field("Y"))), List(Assign(Input("y"), Value(true)))))).eval
    ClassMap.contains("Class1") shouldBe true
  }
  //#19
  it should "set fields (X,Y) and method(m1,m2) of Class2 as Public" in {
    ClassDef("Class2", List(Field("X"), Field("Y")), Constructor(List(Assign(Field("X"), Value(false)), Assign(Field("Y"), Value(true)))), List(Method("m1", List(Assign(Field("X"), NOT(Field("X"))), Input("x")), List(Assign(Input("x"), Value(false)))), Method("m2", List(AND(Field("X"), Field("Y"))), List(Assign(Input("y"), Value(true)))))).eval
    Public("Class2", List("X", "Y"), List("m1", "m2")).eval
    publicMap("Class2")(0).contains("X") shouldBe true
    publicMap("Class2")(0).contains("Y") shouldBe true
    publicMap("Class2")(1).contains("m1") shouldBe true
    publicMap("Class2")(1).contains("m2") shouldBe true
  }
  //#20
  it should "create an object named c1 of Class3 and invoke constructor to assign new field values" in {
    ClassDef("Class3", List(Field("X"), Field("Y")), Constructor(List(Assign(Field("X"), Value(false)), Assign(Field("Y"), Value(true)))), List(Method("m1", List(Assign(Field("X"), NOT(Field("X"))), Input("x")), List(Assign(Input("x"), Value(false)))), Method("m2", List(AND(Field("X"), Field("Y"))), List(Assign(Input("y"), Value(true)))))).eval
    Public("Class3", List("X", "Y"), List("m1", "m2")).eval
    NewObject("Class3", Variable("o3")).eval
    ObjectMap("Class3").contains("o3") shouldBe true
    ObjectTable.contains("o3") shouldBe true
    ObjectMap("Class3")("o3").field("X") shouldBe false
    ObjectMap("Class3")("o3").field("Y") shouldBe true
  }
  //#21
  it should "Invoke method m1 of object c1 and return last expression value = param x's value passed" in {
    ClassDef("Class4", List(Field("X"), Field("Y")), Constructor(List(Assign(Field("X"), Value(false)), Assign(Field("Y"), Value(true)))), List(Method("m1", List(Assign(Field("X"), NOT(Field("X"))), Input("x")), List(Assign(Input("x"), Value(false)))), Method("m2", List(AND(Field("X"), Field("Y"))), List(Assign(Input("y"), Value(true)))))).eval
    Public("Class4", List("X", "Y"), List("m1", "m2")).eval
    NewObject("Class4", Variable("o4")).eval
    Variable("o4").InvokeMethod("m1", List(Assign(Input("x"), Value(true)))) shouldBe true
  }
  //#22
  it should "Invoke field public X, protected Y and throw error on invoking field private Z of object c1" in {
    ClassDef("Class5", List(Field("X"), Field("Y"), Field("Z")), Constructor(List(Assign(Field("X"), Value(true)), Assign(Field("Y"), Value(true)), Assign(Field("Z"), Value(true)))), List(Method("m1", List(Assign(Field("X"), NOT(Field("X"))), NOT(Input("x"))), List(Assign(Input("x"), Value(false)))), Method("m2", List(AND(Field("X"), Field("Y"))), List(Assign(Input("y"), Value(true)))))).eval
    Public("Class5", List("X"), List("m1", "m2")).eval
    Protected("Class5",  List("Y"), List()).eval
    Private("Class5",  List("Z"), List()).eval

    NewObject("Class5", Variable("o5")).eval
    Variable("o5").InvokeField("X") shouldBe true //As set by constructor
    Variable("o5").InvokeField("Y") shouldBe true //As set by constructor
    assertThrows[Exception](Variable("o5").InvokeField("Z"))
  }
  //#23
  it should "extend parentClass and call method in parentClass from childclass object" in {

    ClassDef("childClass", List(Field("X"), Field("Y")), Constructor(List(Assign(Field("X"), Value(false)), Assign(Field("Y"), Value(true)))), List(Method("m1", List(Assign(Field("X"), NOT(Field("X"))), Input("x")), List(Assign(Input("x"), Value(false)))), Method("m2", List(AND(Field("X"), Field("Y"))), List(Assign(Input("y"), Value(true)))))).eval
    ClassDef("parentClass", List(Field("A"), Field("B"), Field("C")), Constructor(List(Assign(Field("A"), Value(true)), Assign(Field("B"), Value(false)))), List(Method("n1", List(Assign(Field("A"), NOT(Field("A"))), Input("a")), List(Assign(Input("a"), Value(false)))), Method("n2", List(NOT(Value(false))), List(Assign(Input("b"), Value(false)))), Method("n3", List(NOT(Field("A"))), List()))).eval
    Public("childClass", List("X", "Y"), List("m1", "m2")).eval
    Protected("parentClass", List("A", "B"), List("n1", "n2")).eval
    Extend(ClassName("childClass"), ClassName("parentClass")).eval
    NewObject("childClass", Variable("co")).eval
    NewObject("parentClass", Variable("po")).eval
    Variable("co").InvokeMethod("n1", List(Assign(Input("a"), Value(true)))) shouldBe true
  }
  //#24
  it should "call method in parentClass's parentClass A from object of class C " in {
    ClassDef("C", List(Field("X")), Constructor(List(Assign(Field("X"), Value(false)))), List(Method("c1", List(Field("X")), List()))).eval
    ClassDef("B", List(Field("Y")), Constructor(List(Assign(Field("Y"), Value(false)))), List(Method("b1", List(Field("Y")), List()))).eval
    ClassDef("A", List(Field("Z")), Constructor(List(Assign(Field("Z"), Value(true)))), List(Method("a1", List(Field("Z")), List()))).eval
    Public("C", List("X"), List("c1")).eval
    Protected("B", List("Y"), List("b1")).eval
    Public("A", List("Z"), List("a1")).eval
    Extend(ClassName("C"), ClassName("B")).eval
    Extend(ClassName("B"), ClassName("A")).eval
    NewObject("C", Variable("c")).eval
    Variable("c").InvokeMethod("a1", List()) shouldBe true
  }
  //#25
  it should "access field of parent class in function of childclass" in {
    ClassDef("childClass2", List(Field("X")), Constructor(List(Assign(Field("X"), Value(false)))), List(Method("m2", List(OR(Field("X"), Field("Y"))), List()))).eval
    ClassDef("parentClass2", List(Field("Y")), Constructor(List(Assign(Field("Y"), Value(false)))), List(Method("m1", List(Field("Y")), List()))).eval
    Public("childClass2", List("X"), List("m2")).eval
    Protected("parentClass2", List("Y"), List("m1")).eval
    Extend(ClassName("childClass2"), ClassName("parentClass2")).eval

    NewObject("childClass2", Variable("childObject2")).eval
    Variable("childObject2").InvokeMethod("m2", List()) shouldBe false
  }
  //#26
  it should "call overrided function of child class when object of parent class is assigned to object of child class" in {
    ClassDef("childClass1", List(Field("X")), Constructor(List(Assign(Field("X"), Value(true)))), List(Method("m1", List(Field("X")), List()))).eval
    ClassDef("parentClass1", List(Field("Y")), Constructor(List(Assign(Field("Y"), Value(false)))), List(Method("m1", List(Field("Y")), List()))).eval
    Public("childClass1", List("X"), List("m1")).eval
    Protected("parentClass1", List("Y"), List("m1")).eval
    Extend(ClassName("childClass1"), ClassName("parentClass1")).eval

    NewObject("childClass1", Variable("childObject1")).eval
    NewObject("parentClass1", Variable("parentObject1")).eval
    SubstituteObject(Variable("parentObject1"), Variable("childObject1")).eval

    Variable("parentObject1").InvokeMethod("m1", List()) shouldBe true

  }
  //#27
  it should "call obj1 function method when obj2  is substituted with obj1" in {
    ClassDef("Class6", List(Field("X")), Constructor(List(Assign(Field("X"), Value(true)))), List(Method("m1", List(Assign(Field("X"), NOT(Field("X")))), List()))).eval
    Public("Class6", List("X"), List("m1")).eval
    NewObject("Class6", Variable("obj6_1")).eval
    NewObject("Class6", Variable("obj6_2")).eval
    Variable("obj6_2").InvokeField("X") shouldBe true //as assigned by constructor

    Variable("obj6_1").InvokeMethod("m1", List()) //inverts value of Field X for obj1
    SubstituteObject(Variable("obj6_2"), Variable("obj6_1")).eval //obj2 now refers to obj1

    Variable("obj6_2").InvokeField("X") shouldBe false
  }
  //#28
  it should "create an abstract class named absClass1" in {
    AbstractClassDef("absClass1", List(Field("X")), Constructor(List(Assign(Field("X"), Value(true)))), List(Method("abc1", List(Value(true)), List())), List(AbstractMethod("f"))).eval
    ClassMap.contains("absClass1") shouldBe true
    AbstractClassMap.contains("absClass1") shouldBe true
  }
  //#29
  it should "throw an error for defining an abstract class without any abstract method" in {
    assertThrows[Exception](AbstractClassDef("absClass2", List(Field("X")), Constructor(List(Assign(Field("X"), Value(true)))), List(Method("abc2", List(Value(true)), List())), List()).eval)
  }
  //#30
  it should "extend abstract Class absClass2 from a concrete class absChildClass2, call method in absClass2 , call overridden abstract method of absClass2, Invoke Filed Y from absChildClass2 object" in {

    AbstractClassDef("absClass2", List(Field("Y")), Constructor(List(Assign(Field("Y"), Value(true)))), List(Method("f", List(Input("x")), List(Assign(Input("x"), Value(false))))), List(AbstractMethod("g"))).eval
    Public("absClass2", List("Y"), List("f")).eval
    ClassDef("absChildClass2", List(), Constructor(List()), List(Method("g", List(Value(false)), List()))).eval
    Public("absChildClass2", List(), List("g")).eval
    Extend(ClassName("absChildClass2"), ClassName("absClass2")).eval
    NewObject("absChildClass2", Variable("absChildObj2")).eval

    Variable("absChildObj2").InvokeMethod("f", List(Assign(Input("x"), Value(true)))) shouldBe true
    Variable("absChildObj2").InvokeMethod("g", List()) shouldBe false
    Variable("absChildObj2").InvokeField("Y") shouldBe true

  }
  //#31
  it should "extend an abstract class absClass4 from abstract class absClass3, extend a concrete class absChildClass 4 from absClass 4, invoke abstract methods from absChildClass object" in {
    AbstractClassDef("absClass3", List(Field("X")), Constructor(List(Assign(Field("X"), Value(true)))), List(Method("abc1", List(Value(true)), List())), List(AbstractMethod("f"))).eval
    AbstractClassDef("absClass4", List(Field("Y")), Constructor(List(Assign(Field("Y"), Value(true)))), List(Method("f", List(Value(false)), List())), List(AbstractMethod("g"))).eval
    Public("absClass3", List("X"), List("abc1")).eval
    Public("absClass4", List("Y"), List("f")).eval
    Extend(ClassName("absClass4"), ClassName("absClass3")).eval

    ClassDef("absChildClass4", List(), Constructor(List()), List(Method("g", List(Value(true)), List()))).eval
    Public("absChildClass4", List(), List("g")).eval
    Extend(ClassName("absChildClass4"), ClassName("absClass4")).eval
    NewObject("absChildClass4", Variable("absChildObj4")).eval

    Variable("absChildObj4").InvokeMethod("f", List()) shouldBe false
    Variable("absChildObj4").InvokeMethod("g", List()) shouldBe true
  }
  //#32
  it should "create an interface class named myInterface1" in {
    InterfaceDef("myInterface1", List(ConstantField("X")), List(Assign(ConstantField("X"), Value(true))),List(AbstractMethod("f1"))).eval
    InterfaceMap.contains("myInterface1") shouldBe true
  }
  //#33
  it should "throw an error for implementing an interface without overriding all its functions" in {
    InterfaceDef("myInterface2", List(ConstantField("X")), List(Assign(ConstantField("X"), Value(true))),List(AbstractMethod("f1"), AbstractMethod("f2"))).eval
    ClassDef("iClass1", List(), Constructor(List()), List(Method("g1", List(Value(false)), List()), Method("f1", List(Input("x")), List(Assign(Input("x"), Value(false)))))).eval
    Public("iClass1", List(), List("f1", "g1")).eval
    assertThrows[Exception](Implements("iClass1", "myInterface2").eval)

  }
  //#34
  it should "throw an error for implementing an interface by another interface, extending a class from an interface, extending an interface from a class" in {
    InterfaceDef("myInterface3", List(ConstantField("X")), List(Assign(ConstantField("X"), Value(true))), List(AbstractMethod("f1"))).eval
    InterfaceDef("myInterface4", List(ConstantField("Y")), List(Assign(ConstantField("Y"), Value(false))),List(AbstractMethod("g1"), AbstractMethod("g2"))).eval
    ClassDef("iClass2", List(), Constructor(List()), List(Method("g1", List(Value(false)), List()), Method("f1", List(Input("x")), List(Assign(Input("x"), Value(false)))))).eval
    Public("iClass2", List(), List("f1", "g1")).eval

    assertThrows[Exception](Implements("myInterface3", "myInterface4").eval)
    assertThrows[Exception](Extend(ClassName("iClass2"), InterfaceName("myInterface3")).eval)
    assertThrows[Exception](Extend(InterfaceName("myInterface3"), ClassName("iClass2")).eval)
  }
  //#35
  it should "call overridden methods of interface myInterface4 and its super interface myInterface5 from object of class iClass3 that implements myInterface4" in{
    InterfaceDef("myInterface5", List(ConstantField("X")), List(Assign(ConstantField("X"), Value(false))), List(AbstractMethod("f1"))).eval
    InterfaceDef("myInterface6", List(ConstantField("Y")), List(Assign(ConstantField("Y"), Value(false))),List(AbstractMethod("f2"))).eval
    Extend(InterfaceName("myInterface5"), InterfaceName("myInterface6")).eval
    ClassDef("iClass3", List(), Constructor(List()), List(Method("f1", List(OR(Input("x"), ConstantField("X"))), List(Assign(Input("x"), Value(false)))), Method("f2", List(Value(false)), List()))).eval
    Public("iClass3", List(), List("f1", "f2")).eval
    Implements("iClass3", "myInterface5").eval
    NewObject("iClass3", Variable("iClass3Obj")).eval
    Variable("iClass3Obj").InvokeMethod("f1", List(Assign(Input("x"), Value(true)))) shouldBe true
    Variable("iClass3Obj").InvokeMethod("f2", List()) shouldBe false
  }
  //#36
  it should "Invoke Interface constant field from class object that implements Interface if that field does not exist in the class" in {
    InterfaceDef("myInterface7", List(ConstantField("X")), List(Assign(ConstantField("X"), Value(true))), List(AbstractMethod("f1"))).eval
    ClassDef("iClass4", List(), Constructor(List()), List(Method("f1", List(OR(Input("a"), ConstantField("X"))), List(Assign(Input("a"), Value(false)))), Method("f2", List(Value(false)), List()))).eval
    Public("iClass4", List(), List("f1", "f2")).eval
    Implements("iClass4", "myInterface7").eval

    NewObject("iClass4", Variable("iClass4Obj")).eval
    Variable("iClass4Obj").InvokeField("X") shouldBe true
  }
  //#37
  it should "call overridden abstract method f3 in base class that extends an abstract class which inturn implements an interface" in{
    InterfaceDef("myInterface8", List(ConstantField("X"), ConstantField("Y")), List(Assign(ConstantField("X"), Value(false)), Assign(ConstantField("Y"), NOT(Value(true)))), List(AbstractMethod("f1"), AbstractMethod("f2"))).eval
    AbstractClassDef("absClass5", List(Field("X")), Constructor(List(Assign(Field("X"), Value(false)))), List(Method("f1", List(OR(Field("X") ,Input("a"))), List(Assign(Input("a"), Value(true)))), Method("f2", List(Value(false)), List())), List(AbstractMethod("f3"))).eval
    Public("absClass5", List("X"), List("f1","f2")).eval
    Implements("absClass5", "myInterface8").eval
    ClassDef("absChildClass5", List(Field("Y")), Constructor(List(Assign(Field("Y"), Value(false)))), List(Method("f3", List(OR(OR(OR(Field("X"), ConstantField("X")), Field("Y")), Input("a"))), List(Assign(Input("a"), Value(false)))))).eval
    Public("absChildClass5", List("Y"), List("f3")).eval
    Extend(ClassName("absChildClass5"), ClassName("absClass5")).eval
    NewObject("absChildClass5", Variable("absChildObj5")).eval
    Variable("absChildObj5").InvokeMethod("f3", List(Assign(Input("a"), Value(true)))) shouldBe true
  }
  //#38
  it should "throw an error when trying to implement an interface by another interface" in{
    InterfaceDef("myInterface8", List(ConstantField("X")), List(Assign(ConstantField("X"), Value(false))), List(AbstractMethod("f1"))).eval
    InterfaceDef("myInterface9", List(ConstantField("Y")), List(Assign(ConstantField("Y"), Value(false))), List(AbstractMethod("f2"))).eval
    assertThrows[Exception](Implements("myInterface8", "myInterface8").eval)
  }
  //#39
  it should "throw an error if cyclic inheritance exists" in{
    ClassDef("Class7", List(Field("W")), Constructor(List(Assign(Field("X"), Value(true)))), List(Method("w1", List(Assign(Field("W"), NOT(Field("W")))), List()))).eval
    ClassDef("Class8", List(Field("X")), Constructor(List(Assign(Field("X"), Value(false)))), List(Method("x1", List(Assign(Field("X"), NOT(Field("X")))), List()))).eval
    ClassDef("Class9", List(Field("Y")), Constructor(List(Assign(Field("X"), Value(true)))), List(Method("y1", List(Assign(Field("Y"), NOT(Field("Y")))), List()))).eval
    Extend(ClassName("Class8"), ClassName("Class7")).eval
    Extend(ClassName("Class9"), ClassName("Class8")).eval
    assertThrows[Exception](Extend(ClassName("Class7"), ClassName("Class7")).eval)
    assertThrows[Exception](Extend(ClassName("Class7"), ClassName("Class8")).eval)
    assertThrows[Exception](Extend(ClassName("Class7"), ClassName("Class9")).eval)

    InterfaceDef("myInterface9", List(ConstantField("W"), ConstantField("X")), List(Assign(ConstantField("X"), Value(false)), Assign(ConstantField("W"), NOT(Value(true)))), List(AbstractMethod("f1"), AbstractMethod("f2"))).eval
    InterfaceDef("myInterface10", List(ConstantField("Y"), ConstantField("Z")), List(Assign(ConstantField("Z"), Value(false)), Assign(ConstantField("Y"), NOT(Value(true)))), List(AbstractMethod("f1"), AbstractMethod("f2"))).eval
    InterfaceDef("myInterface11", List(ConstantField("U"), ConstantField("V")), List(Assign(ConstantField("V"), Value(false)), Assign(ConstantField("U"), NOT(Value(true)))), List(AbstractMethod("f1"), AbstractMethod("f2"))).eval
    Extend(InterfaceName("myInterface10"), InterfaceName("myInterface9")).eval
    Extend(InterfaceName("myInterface11"), InterfaceName("myInterface10")).eval
    assertThrows[Exception](Extend(InterfaceName("myInterface9"), InterfaceName("myInterface11")).eval)

  }
  //#40
  it should "implement two or more different interfaces that declare methods with exactly the same signatures by a concrete class" in {
    InterfaceDef("myInterface12", List(), List(), List(AbstractMethod("f1"), AbstractMethod("f2"))).eval
    InterfaceDef("myInterface13", List(), List(), List(AbstractMethod("f1"), AbstractMethod("f2"))).eval
    ClassDef("iClass5", List(), Constructor(List()), List(Method("f1", List(Value(true)), List()), Method("f2", List(Value(false)), List()))).eval
    Public("iClass5", List(), List("f1", "f2")).eval
    Implements("iClass5", "myInterface12").eval
    Implements("iClass5", "myInterface13").eval
    NewObject("iClass5", Variable("iClass5obj")).eval
    Variable("iClass5obj").InvokeMethod("f1", List()) shouldBe true
    Variable("iClass5obj").InvokeMethod("f2", List()) shouldBe false
  }
  //#41
  it should "allow an abstract class that inherit from another abstract class and implement interfaces where all interfaces and the abstract class have methods with the same signatures" in{
    InterfaceDef("myInterface14", List(), List(), List(AbstractMethod("f1"), AbstractMethod("f2"))).eval
    AbstractClassDef("absClass6", List(), Constructor(List()), List(), List(AbstractMethod("f1"), AbstractMethod("f2"))).eval
    AbstractClassDef("absClass7", List(), Constructor(List()), List(Method("f1", List(Value(true)), List()), Method("f2", List(Value(false)), List())), List(AbstractMethod("f3"))).eval
    Public("absClass7", List(), List("f1", "f2")).eval
    Implements("absClass7", "myInterface14").eval
    Extend(ClassName("absClass7"), ClassName("absClass6")).eval
    ClassDef("absChildClass7", List(), Constructor(List()), List(Method("f3", List(Value(true)), List()))).eval
    Public("absChildClass7", List(), List("f3")).eval
    Extend(ClassName("absChildClass7"), ClassName("absClass7")).eval
    NewObject("absChildClass7", Variable("absChildObj7")).eval
    Variable("absChildObj7").InvokeMethod("f1", List()) shouldBe true
    Variable("absChildObj7").InvokeMethod("f2", List()) shouldBe false
    Variable("absChildObj7").InvokeMethod("f3", List()) shouldBe true
  }
  //#42
  it should "allow an abstract class to inherit from a concrete class" in{
    ClassDef("Class10", List(), Constructor(List()), List(Method("f1", List(Value(true)), List()))).eval
    Public("Class10", List(), List("f1")).eval
    AbstractClassDef("absClass8", List(), Constructor(List()), List(), List(AbstractMethod("f2"))).eval
    Extend(ClassName("absClass8"), ClassName("Class10")).eval
    ClassDef("absChildClass8", List(), Constructor(List()), List(Method("f2", List(Value(false)), List()))).eval
    Public("absChildClass8", List(), List("f2")).eval
    Extend(ClassName("absChildClass8"), ClassName("absClass8")).eval
    NewObject("absChildClass8", Variable("absChildObj8")).eval
    Variable("absChildObj8").InvokeMethod("f1", List()) shouldBe true
    Variable("absChildObj8").InvokeMethod("f2", List()) shouldBe false
  }
  //#43
  it should "check condition for IF and run the thenClause" in{
    IF(CheckEqual(NOT(Value(false)), NOT(NOT(Value(true)))), List(Assign(LogicGate("NotGate1"), NOT(Input("A"))), Assign(LogicGate("AndGate1"), AND(Input("A"), Input("B")))), List(Assign(LogicGate("OrGate1"), OR(Input("A"), Input("B"))))).eval
    Scope(LogicGate("NotGate1"), List(Assign(Input("A"), Value(false)))).eval
    Scope(LogicGate("AndGate1"), List(Assign(Input("A"), Value(true)), Assign(Input("B"), Value(false)))).eval

    LogicGate("NotGate1").eval shouldBe true
    LogicGate("AndGate1").eval shouldBe false

    //OrGate1 should not exist
    GateTable.contains("OrGate1") shouldBe false
  }
  //#44
  it should "check condition for IF and run the elseClause" in {
    IF(CheckEqual(NOT(Value(false)), NOT(NOT(Value(false)))), List(Assign(LogicGate("NotGate2"), NOT(Input("A")))), List(Assign(LogicGate("OrGate2"), OR(Input("A"), Input("B"))), Assign(LogicGate("AndGate2"), AND(Input("A"), Input("B"))))).eval
    Scope(LogicGate("OrGate2"), List(Assign(Input("A"), Value(true)), Assign(Input("B"), Value(false)))).eval
    Scope(LogicGate("AndGate2"), List(Assign(Input("A"), Value(true)), Assign(Input("B"), Value(false)))).eval

    LogicGate("OrGate2").eval shouldBe true
    LogicGate("AndGate2").eval shouldBe false

    //thenClause should be skipped and NotGate2 should not exist
    GateTable.contains("NotGate2") shouldBe false
  }
  //#45
  it should "create an exception class and Handle exception construct with only its try block running" in{
    ExceptionClassDef("ExceptionClass1").eval
    HandleException("ExceptionClass1", Try(List(Assign(LogicGate("NotGate3"), NOT(Input("A"))), Assign(LogicGate("OrGate3"), OR(Input("A"), Input("B"))))), Catch(List(Assign(LogicGate("AndGate3"), AND(Input("A"), Input("B")))))).eval

    Scope (LogicGate("NotGate3"), List(Assign(Input("A"), Value(true)))).eval
    Scope (LogicGate("OrGate3"), List(Assign(Input("A"), Value(true)), Assign(Input("B"), Value(false)))).eval

    LogicGate ("NotGate3").eval shouldBe false
    LogicGate ("OrGate3").eval shouldBe true

    // Error since catch block did not run and AndGate3 was not created
    assertThrows[Exception](LogicGate("AndGate3").eval)
  }
  //#46
  it should "throw an error for handling an exception of a non-existing exception class" in{
    assertThrows[Exception](HandleException("non_existing_class", Try(List(Assign(LogicGate("NotGate"), NOT(Input("A"))), Assign(LogicGate("OrGate"), OR(Input("A"), Input("B"))))), Catch(List(Assign(LogicGate("AndGate"), AND(Input("A"), Input("B")))))).eval)
  }
  //#47
  it should "Handle an exception with error thrown in the try block, reason printed, and catch block executed" in{
    ExceptionClassDef("ExceptionClass2").eval
    HandleException("ExceptionClass2", Try(List(IF(CheckEqual(AND(Value(true), Value(false)), OR(Value(true), Value(false))), List(Assign(LogicGate("NotGate4"), NOT(Input("A")))), List(Assign(LogicGate("AndGate4"), AND(Value(true), Value(true))), ThrowException("ExceptionClass2", "Check failed!"), Assign(LogicGate("OrGate4"), OR(Value(true), Value(false))))))), Catch(List(Assign(LogicGate("NotGate5"), NOT(Value(false)))))).eval

    println(GateTable)

    //since IF condition results in false so thenClause not executed
    assertThrows[Exception](LogicGate("NotGate4").eval)

    //elseClause of IF executes
    LogicGate("AndGate4").eval shouldBe true
    //since expressions between ThrowException and Catch Block are skipped
    assertThrows[Exception](LogicGate("OrGate4").eval)

    //since Catch Block executes
    LogicGate("NotGate5").eval shouldBe true
  }
  //#48
  it should "handle nested try and catch blocks and run catch block for ExceptionClass4" in {
    ExceptionClassDef("ExceptionClass3").eval
    ExceptionClassDef("ExceptionClass4").eval
    HandleException("ExceptionClass3", Try(List(IF(CheckEqual(Value(true), NOT(Value(true))), List(ThrowException("ExceptionClass3", "ExceptionClass3 testing")), List(HandleException("ExceptionClass4", Try(List(ThrowException("ExceptionClass4", "Nested ExcpetionClass4 testing"))), Catch(List(Assign(LogicGate("AndGate5"), AND(Value(true), Value(true)))))))))), Catch(List(Assign(LogicGate("OrGate5"), OR(Value(false), Value(true)))))).eval

    //since IF in outer try fails and elseClause is run
    // inner try throws exception of Class4 which is handled by inner catch and NotGate6 created
    LogicGate("AndGate5").eval shouldBe true

    //since outer try doesn't throws error, outer catch not run
    assertThrows[Exception](LogicGate("OrGate5").eval)
  }
  //#49
  it should "partially evaluate a simple expression if no inputs are given" in{
    Assign(LogicGate("NotGate6"), NOT(Input("A"))).eval
    assert(LogicGate("NotGate6").eval == NOT(Input("A")))

    Assign(LogicGate("OrGate6"), OR(Input("B"), Input("C"))).eval
    Scope(LogicGate("OrGate6"), List(Assign(Input("B"), Value(false)))).eval
    assert(LogicGate("OrGate6").eval == OR(Value(false), Input("C")))
  }
  //#50
  it should "completely evaluate a partially evaluated simple expression when all its inputs are provided" in {
    Assign(LogicGate("AndGate6"), AND(Input("A"), Input("B"))).eval
    Scope(LogicGate("AndGate6"), List(Assign(Input("A"), Value(true)))).eval
    //AndGate6 should evaluate partially since only one input ("A") is provided
    assert(LogicGate("AndGate6").eval == AND(Value(true), Input("B")))
    Scope(LogicGate("AndGate6"), List(Assign(Input("B"), Value(false)))).eval
    //AndGate6 should evaluate completely since the second input ("B") was provided
    assert(LogicGate("AndGate6").eval == false)
  }
  //#51
  it should "completely evaluate a partially evaluated complex expression when all its inputs are provided" in {
    Assign(LogicGate("composite1"), AND(Input("A"), OR(Input("B"), Input("C")))).eval
    Scope(LogicGate("composite1"), List(Assign(Input("A"), Value(false)), Assign(Input("B"), Value(true)))).eval

    //composite1 should evaluate partially since Input "C" is not provided
    assert(LogicGate("composite1").eval == AND(Value(false), OR(Value(true), Input("C"))))

    Scope(LogicGate("composite1"), List(Assign(Input("C"), Value(true)))).eval

    //composite1 should evaluate completely since Input "C" is provided
    assert(LogicGate("composite1").eval == false)
  }
  //#52
  it should "use monadic function MAP() to simplify a given expression" in {
    Assign(LogicGate("composite2"), AND(Input("A"), OR(Input("B"), Input("C")))).eval
    Scope(LogicGate("composite2"), List(Assign(Input("B"), Value(true)))).eval

    //evaluating without using MAP()
    //assert(LogicGate("composite2").eval == AND(Input("A"), OR(Value(true), Input("C"))))

    //evaluating with after using transformer function func
    val func1: constructs => constructs = {
      case OR(_, Value(true)) => Value(true)
      case OR(Value(true), _) => Value(true)
      case expr => expr
    }

    val x = LogicGate("composite2").MAP(func1)
    assert(x == AND(Input("A"), Value(true)))
  }
  //#53
  it should "use monadic function MAP() to simplify a very complex expression" in {
    Assign(LogicGate("complexGate1"), AND(Input("A"), OR(NOR(Input("B"), NAND(NOT(Input("C")), Input("D"))), Input("E")))).eval
    Scope(LogicGate("complexGate1"), List(Assign(Input("C"), Value(true)), Assign(Input("E"), Value(false)))).eval

    //evaluating with after using transformer function func
    val func2: constructs => constructs = {
      case OR(_, Value(true)) => Value(true)
      case OR(Value(true), _) => Value(true)
      case AND(Value(false), _) => Value(false)
      case AND(_, Value(false)) => Value(false)
      case NOR(_, Value(true)) => Value(false)
      case NOR(Value(true), _) => Value(false)
      case NAND(Value(false), _) => Value(true)
      case NAND(_, Value(false)) => Value(true)
      case expr => expr
    }
    val x = LogicGate("complexGate1").MAP(func2)
    assert(x == Value(false))
  }
  //#54
  it should "partially evaluate an IF block in the case the condition passed to the IF block partially evaluates" in {
    //Assigning 3 logicGates
    Assign(LogicGate("NotGate7"), NOT(Input("A"))).eval
    Assign(LogicGate("OrGate7"), OR(Input("B"), Value(true))).eval
    Assign(LogicGate("AndGate7"), AND(Value(false), Input("C"))).eval

    //transformation function
    val func3: constructs => constructs = {
      case OR(_, Value(true)) => Value(true)
      case OR(Value(true), _) => Value(true)
      case AND(Value(false), _) => Value(false)
      case AND(_, Value(false)) => Value(false)
      case expr => expr
    }

    // Assigning an IF statement to ifBlock1
    Assign(LogicGate("ifBlock1"), IF(CheckEqual(LogicGate("NotGate7"), LogicGate("OrGate7"))
      , List(LogicGate("AndGate7"), Assign(LogicGate("gateInsideThenBlock1"), AND(Value(false), Value(true))))
      , List(NOT(LogicGate("NotGate7")), Assign(LogicGate("gateInsideElseBlock1"), OR(Value(false), Value(true)))))).eval

    //Storing the simplifiedIf1 to simplifiedIFBlock1
    val simplifiedIf1 = LogicGate("ifBlock1").MAP(func3)
    Assign(LogicGate("simplifiedIfBlock1"), simplifiedIf1).eval

    // Only logic gate expressions and user defined LogicGate inside IF block - including condition, thenClause & ElseClause - are simplified
    // Other constructs e.g. Assign are not executed
    assert(LogicGate("simplifiedIfBlock1").eval == IF(CheckEqual(NOT(Input("A")), Value(true))
      , List(Value(false), Assign(LogicGate("gateInsideThenBlock1"), AND(Value(false), Value(true)))),
      List(NOT(NOT(Input("A"))), Assign(LogicGate("gateInsideElseBlock1"), OR(Value(false), Value(true)))))
    )

    //Assignments inside IF block are not made when IF block is simplified, therefore evaluation of gates will throw Exception
    assertThrows[Exception](LogicGate("gateInsideThenBlock1").eval)
    assertThrows[Exception](LogicGate("gateInsideElseBlock1").eval)

  }
  //#55
  it should "completely evaluate a partially evaluated IF expression when all its condition are provided" in{
    Assign(LogicGate("NotGate8"), NOT(Input("A"))).eval
    Assign(LogicGate("OrGate8"), OR(Input("B"), Value(true))).eval
    Assign(LogicGate("AndGate8"), AND(Value(false), Input("C"))).eval

    Assign(LogicGate("ifBlock2"), IF(CheckEqual(LogicGate("NotGate8"), LogicGate("OrGate8"))
      , List(LogicGate("AndGate8"), Assign(LogicGate("gateInsideThenBlock2"), AND(Value(false), Value(true))))
      , List(LogicGate("NotGate8"), Assign(LogicGate("gateInsideElseBlock2"), OR(Value(false), Value(true)))))).eval

    //transformation function
    val func3: constructs => constructs = {
      case OR(_, Value(true)) => Value(true)
      case OR(Value(true), _) => Value(true)
      case AND(Value(false), _) => Value(false)
      case AND(_, Value(false)) => Value(false)
      case expr => expr
    }

    val simplifiedIf2 = LogicGate("ifBlock2").MAP(func3)
    assert(simplifiedIf2 == IF(CheckEqual(NOT(Input("A")),Value(true))
                                  ,List(Value(false), Assign(LogicGate("gateInsideThenBlock2"), AND(Value(false), Value(true))))
                                  ,List(NOT(Input("A")), Assign(LogicGate("gateInsideElseBlock2"), OR(Value(false), Value(true))))
                                  )
          )

    Assign(LogicGate("simplifiedIfBlock2"), simplifiedIf2).eval

    //Assigning remaining input value to the simplified if block and evaluating it
    Scope(LogicGate("simplifiedIfBlock2"), List(Assign(Input("A"), Value(false)))).eval
    LogicGate("simplifiedIfBlock2").eval

    // since IF condition results in true, thenBlock should be executed
    LogicGate("gateInsideThenBlock2").eval shouldBe false
    assertThrows[Exception](LogicGate("gateInsideElseBlock2").eval)

  }
  //#56
  it should "throw an exception if TestGate is used to compare evaluation of some logicgate to a boolean value, which can only be evaluated partially " in{
    Assign(LogicGate("OrGate8"), OR(Input("A"), Input("B"))).eval
    Scope(LogicGate("OrGate8"), List(Assign(Input("A"), Value(false)))).eval

    //Exception - "LogicGate evaluated only partially!"
    assertThrows[Exception](TestGate("OrGate8", true))
  }
}