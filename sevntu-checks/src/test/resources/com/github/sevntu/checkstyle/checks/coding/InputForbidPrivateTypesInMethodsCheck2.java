 
package com.github.sevntu.checkstyle.checks.coding;


public class InputForbidPrivateTypesInMethodsCheck2 {

    public void getWarning(C1 c) {
        // no code 
    }

    public C1 getUsefulReturn1() {
        return new C1();
    }
    
    public void getWarning(C2 c) {
        // no code 
    }

    public C2 getUsefulReturn2() {
        return new C2();
    }
    
    public void getWarning(C3 c) {
        // no code 
    }

    public C3 getUsefulReturn3() {
        return new C3();
    }

    private class C1 {
    }
    
    private class C2 extends C1 {
    }
    
    private class C3 extends Exception {

    }
}
