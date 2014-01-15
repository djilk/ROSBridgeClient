/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jilk.ros.rosbridge.implementation;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

// temporary - for tests
import com.jilk.ros.rosbridge.operation.*;
import com.jilk.ros.message.*;
import static com.jilk.ros.rosbridge.implementation.JSON.toJSON;


/**
 *
 * @author david_000
 */
public class JSONTest {
    
    public JSONTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void mainTest() {
        
        // This just runs the test, it doesn't have any assertions!
        
        Registry<Class> registry = new Registry<Class>();
       
        TestClass t = new TestClass();
        t.myInt = 99;
        t.myLong = 100000000;
        t.myDouble = 3.14159;
        t.myString = "Test String";
        t.myTS = new TestSubClass();
        t.myTS.mySubString = "Test Sub String";
        t.myTS.myBooleanArray = new boolean[] {false, true};
        t.myTS.myBoolean2DArray = new boolean[][] {new boolean[] {true, true}, new boolean[] {false, false}};
        System.out.println("Test Class");
        t.print();
        System.out.println("Test Class as JSON");
        String json = toJSON(t);
        System.out.println(json);

        Operation.initialize(registry);
        Message.register(Clock.class, registry.get(Message.class));
        registry.register(Publish.class, "/clock", Clock.class);
        Publish p = new Publish();
        p.topic = "/clock";
        Clock c = new Clock();
        c.clock = new TimePrimitive();
        c.clock.secs = 1000;
        c.clock.nsecs = 999999999;
        p.msg = c;
        
        System.out.println("Publish Operation");
        p.print();
        json = toJSON(p);
        System.out.println("Publish Operation as JSON");
        System.out.println(json);
        
        System.out.println("Publish Operation converted back from JSON");
        Message p2 = Operation.toOperation(json, registry);
        p2.print();
        
        Message.register(TestMessage.class, registry.get(Message.class));
        registry.register(CallService.class, "/srv", TestMessage.class);
        TestMessage t1 = new TestMessage();
        t1.testName = "Test Name";
        t1.testInt = 77;
        t1.testDouble = 2.71828;
        t1.testShortArray = new short[] {1, 2, 3, 4};
        CallService cs = new CallService("/srv", t1);
        
        System.out.println("CallService Operation");
        cs.print();
        json = toJSON(cs);

        System.out.println("CallService Operation as JSON");
        System.out.println(json);

        System.out.println("CallService Operation converted back from JSON");
        Message cs2 = Operation.toOperation(json, registry);
        cs2.print();        
    }
    
    public static class TestClass extends Message {
        public long myInt;
        public long myLong;
        public double myDouble;
        public String myString;
        public TestSubClass myTS;
    }
    
    public static class TestSubClass {
        public String mySubString;
        public boolean[] myBooleanArray;
        public boolean[][] myBoolean2DArray;
    }    
        
}