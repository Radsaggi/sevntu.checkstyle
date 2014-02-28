package com.github.sevntu.checkstyle.checks.coding;


public class InputForbidPrivateTypesInMethodsCheck {

    public void getWarning1(C1 c) {
        // no code 
    }

    public C1 getUsefulReturn() {
        return new C1();
    }

    private class C1 {
    }
}
