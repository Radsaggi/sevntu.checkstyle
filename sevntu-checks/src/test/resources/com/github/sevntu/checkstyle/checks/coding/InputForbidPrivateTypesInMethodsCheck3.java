 
package com.github.sevntu.checkstyle.checks.coding;

import java.io.Serializable;


public class InputForbidPrivateTypesInMethodsCheck3 {

    public void getWarning(C1 c) {
        // no code 
    }

    public C1 getUsefulReturn1() {
        return new C2();
    }
    
    public String getWarning(String z, C2 c) {
        return z;
    }

    public C2 getUsefulReturn2() {
        return new C2();
    }
    
    public void getWarning(C3 c, int a) {
        // no code 
    }

    public C3 getUsefulReturn3(String x) {
        return new C3();
    }

    private interface C1 extends Serializable {
    }
    
    private class C2 implements C1{
    }
    
    private class C3 extends Exception {
    }
}
