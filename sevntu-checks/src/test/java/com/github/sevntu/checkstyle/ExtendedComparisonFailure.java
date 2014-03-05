/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.sevntu.checkstyle;

import org.junit.ComparisonFailure;

/**
 *
 * @author ashutosh
 */
public class ExtendedComparisonFailure extends ComparisonFailure{
    private final String printMessage;

    public ExtendedComparisonFailure(ComparisonFailure f, String printMessage) {
        super("", f.getExpected(), f.getActual());
        this.printMessage = printMessage;
    }

    @Override
    public String getMessage() {
        return printMessage;
    }
    
}
