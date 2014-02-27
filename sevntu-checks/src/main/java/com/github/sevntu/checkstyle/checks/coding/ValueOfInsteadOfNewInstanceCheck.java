////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2012  Oliver Burn
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////
package com.github.sevntu.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This check prevents the creation of new objects for boxing of primitive 
 * data type into objects and encourages usage of Number.valueOf function.
 * </p>
 * <p>
 * For example: new Integer(x) ==> Integer.valueOf(x),
 * and similarly for any expression.
 * </p>
 * @author <a href="mailto:radsaggi-thecoder@yahoo.in">Radsaggi</a>
 */
public class ValueOfInsteadOfNewInstanceCheck extends Check
{
	
    private static final String[] WRAPPER_CLASSES = {"Byte", "Double", "Float", "Integer", "Long", "Short"};
    
    /**
     * A key is pointing to the warning message about use of private type
     * members as method return. Gives a suitable suggestion as well.
     *
     */
    protected static final String MESSAGE_KEY = "wrapper.instantiation.need.optimization";

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.LITERAL_NEW };
    }

    @Override
    public void visitToken(DetailAST aDetailAST)
    {
	DetailAST classNode = aDetailAST.getFirstChild();
        if (isWrapperType(classNode)) {
            log(aDetailAST.getLineNo(), MESSAGE_KEY,
                    classNode.getText());
        }
    }

    /**
     * <p>
     * Return true, if current expression is an instantiation of 
     * a wrapper class.
     * </p>
     * @param classNode - the class node to check
     * @return - boolean variable
     */
    private static boolean isWrapperType(DetailAST classNode)
    {
        return java.util.Arrays.binarySearch(WRAPPER_CLASSES, classNode.getText()) > -1;
    }
    
}
