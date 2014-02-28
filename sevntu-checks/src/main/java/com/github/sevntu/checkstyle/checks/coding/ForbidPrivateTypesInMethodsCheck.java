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
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <p>
 * Private inner types(Classes, enums) should NOT be used as return types or
 * parameters in public method. We need to ignore such types when used as return
 * types if they extend or implement something (usually public interface) so
 * clients of such classes could keep them by the public interface.
 * </p>
 * <p>
 * For example: public class Example { public void getWarning1(C1 c) { // no
 * code }
 *
 * public C1 getUsefulReturn() { return new C1(); }
 *
 * private class C1 { } }
 * </p>
 *
 * @author <a href="mailto:radsaggi-thecoder@yahoo.in">Radsaggi</a>
 */
public class ForbidPrivateTypesInMethodsCheck extends Check {

    /**
     * A key is pointing to the warning message about use of private type
     * members as method return.
     *
     */
    protected static final String METHOD_RETURN = "method.return.private";

    /**
     * A key is using to build a warning message about use of private type
     * members in method parameters.
     *
     */
    protected static final String METHOD_PARAMETRS = "method.parameters.private";

    /**
     * A key is pointing to the warning message about use of private type
     * members as method return. Gives a suitable suggestion as well.
     *
     */
    protected static final String METHOD_RETURN_SUGGEST = "method.return.private.suggest";

    /**
     * A list contains all MethodDef instances derived from METHOD_DEF DetailAST
     * nodes that have been already visited by check.
     *
     */
    private final Set<MethodDef> methods;

    /**
     * A list contains all ClassEnumDef instances derived from CLASS_DEF or
     * ENUM_DEF DetailAST nodes that have been already visited by check.
     *
     */
    private final Set<ClassEnumDef> classes_enums;

    /**
     * A list contains all InterfaceDef derived from INTERFACE_DEF DetailAST
     * nodes that have been already visited by check.
     *
     */
    private final Set<InterfaceDef> interfaces;

    public ForbidPrivateTypesInMethodsCheck() {
        methods = new LinkedHashSet<MethodDef>();
        classes_enums = new LinkedHashSet<ClassEnumDef>();
        interfaces = new LinkedHashSet<InterfaceDef>();
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[]{
            TokenTypes.METHOD_DEF,
            TokenTypes.CLASS_DEF,
            TokenTypes.ENUM_DEF,
            TokenTypes.INTERFACE_DEF
        };
    }

    @Override
    public void visitToken(DetailAST aDetailAST) {

        DetailAST modifiers = aDetailAST.findFirstToken(TokenTypes.MODIFIERS);

        switch (aDetailAST.getType()) {
            case TokenTypes.METHOD_DEF:
                if (!hasModifier(modifiers, TokenTypes.LITERAL_PRIVATE)) {
                    methods.add(new MethodDef(aDetailAST));
                }
                break;
            case TokenTypes.CLASS_DEF:
            case TokenTypes.ENUM_DEF:
                if (hasModifier(modifiers, TokenTypes.LITERAL_PRIVATE)) {
                    classes_enums.add(new ClassEnumDef(aDetailAST));
                }
                break;
            case TokenTypes.INTERFACE_DEF:
                if (hasModifier(modifiers, TokenTypes.LITERAL_PRIVATE)) {
                    interfaces.add(new InterfaceDef(aDetailAST));
                }
                break;
        }
    }

    @Override
    public void finishTree(DetailAST aRootAST) {
        super.finishTree(aRootAST);

        //Check super types of all private interfaces defined in class
        for (InterfaceDef i : interfaces) {
            checkSuper(i, new SuperYielder<InterfaceDef>() {
                @Override
                public DetailAST getSuper(InterfaceDef t) {
                    return t.extendsNode.getFirstChild();
                }
            }, interfaces);
        }

        //Check super types of all private classes or enums defined in class
        for (ClassEnumDef i : classes_enums) {
            checkSuper(i, new SuperYielder<ClassEnumDef>() {
                @Override
                public DetailAST getSuper(ClassEnumDef t) {
                    return t.extendsNode.getFirstChild();
                }
            }, classes_enums);
        }

        //Check implemented interfaces of all private 
        //classes or enums defined in class
        for (ClassEnumDef i : classes_enums) {
            i.processed = false;
            checkSuper(i, new SuperYielder<ClassEnumDef>() {
                @Override
                public DetailAST getSuper(ClassEnumDef t) {
                    return t.implementsNode.getFirstChild();
                }
            }, interfaces);
        }

        for (MethodDef m : methods) {
            DetailAST returnExp = m.returnNode.findFirstToken(TokenTypes.IDENT);
            if (returnExp != null) {
                //Check the return type against all private types
                ReferenceTypeDef r;
                r = findMatch(classes_enums, returnExp.getText());
                if (r == null) {
                    r = findMatch(interfaces, returnExp.getText());
                }

                if (r != null) {
                    if (r.substitute.isEmpty()) {
                        log(returnExp.getLineNo(), METHOD_RETURN,
                                returnExp.getText());
                    } else {
                        log(returnExp.getLineNo(), METHOD_RETURN_SUGGEST,
                                returnExp.getText(), r.getSubstitute());
                    }
                }
            }

            DetailAST paramExp = m.parametersNode.getFirstChild();
            while (paramExp != null) {
                DetailAST paramType = paramExp.findFirstToken(TokenTypes.TYPE).getFirstChild();

                //Check the parameters against all private types
                ReferenceTypeDef r;
                r = findMatch(classes_enums, paramType.getText());
                if (r == null) {
                    r = findMatch(interfaces, paramType.getText());
                }

                if (r != null) {
                    log(paramType.getLineNo(), METHOD_PARAMETRS,
                            paramType.getText());
                }

                paramExp = paramExp.getNextSibling();
                if (paramExp != null && paramExp.getType() == TokenTypes.COMMA) {
                    paramExp = paramExp.getNextSibling();
                }
            }
        }
    }

    /**
     * <p>
     * Return true, if current Modifier has specified modifier in subtree.
     * </p>
     *
     * @param modifiersNode - the class node to check
     * @param modifier - the modifier to check
     * @return - boolean variable
     */
    private static boolean hasModifier(DetailAST modifiersNode, int modifier) {
        return modifiersNode.findFirstToken(modifier) != null;
    }

    /**
     * <p>
     * An encapsulation for the clause that must be checked by the checkSuper
     * method.
     * </p>
     *
     * @param <T> - the type of data it accepts
     */
    private static interface SuperYielder<T extends ReferenceTypeDef> {

        public DetailAST getSuper(T t);
    }

    /**
     * <p>
     * Recursively checks the private classes and interfaces if they have super
     * defined within the same class. For public super definitions, add a
     * suggestion tag to the message.
     * </p>
     *
     * @param node - the class node to check
     * @param yielder - the interface that encapsulates whether extends or
     * implements clause is checked
     * @param set - the set of super types it is checked against
     */
    private static <T extends ReferenceTypeDef, V extends ReferenceTypeDef>
            void checkSuper(T node, SuperYielder<T> yielder, Set<V> set) {
        if (node.processed) {
            return;
        }

        DetailAST zuper = yielder.getSuper(node);

        while (zuper != null) {

            //Find if the super is public or private
            V match = findMatch(set, zuper.getText());

            if (match != null) {
                //The super is private
                if (!match.processed) {
                    //since we will check the interfaces first 
                    //the following should never create a problem
                    if (node.getClass().isInstance(match)) {
                        checkSuper((T) match, yielder, set);
                    } //else There is a problem
                }
                if (!match.substitute.isEmpty()) {
                    node.substitute += match.substitute;
                }
            } else {
                //The super is public
                node.substitute += zuper.getText() + ", ";
            }

            zuper = zuper.getNextSibling();
        }

        node.processed = true;
    }

    /**
     * <p>
     * Find a match in the given set based on the nameNode
     * </p>
     *
     * @param set - the set to find the match in
     * @param text - the name text to look for
     * @return - the matched element or null if not found
     */
    private static <T extends ReferenceTypeDef> T findMatch(Set<T> set, String text) {
        for (T i : set) {
            if (i.nameNode.getText().equals(text)) {
                return i;
            }
        }
        return null;
    }

    /**
     * <p>
     * Return an empty DetailAST that can act as a placeholder.
     * </p>
     *
     * @return - the empty placeholder DetailAST
     */
    private static DetailAST emptyNode() {
        return new DetailAST();
    }

    /**
     * <p>
     * Object for encapsulating a public method definition.
     * </p>
     */
    private static class MethodDef {

        DetailAST returnNode, parametersNode;

        /**
         * <p>
         * Constructor that creates an object given a DetailAST node.
         * </p>
         *
         * @param aMethodNode - the METHOD_DEF type node to be encapsulated
         * @throws IllegalArgumentException - if the argument is not of desired
         * type
         */
        public MethodDef(DetailAST aMethodNode)
                throws IllegalArgumentException {
            if (aMethodNode.getType() != TokenTypes.METHOD_DEF) {
                throw new IllegalArgumentException("The argument Node to "
                        + "MethodDef should be of type METHOD_DEF.");
            }
            returnNode = aMethodNode.findFirstToken(TokenTypes.TYPE);
            parametersNode = aMethodNode.findFirstToken(TokenTypes.PARAMETERS);
        }
    }

    /**
     * <p>
     * Object for encapsulating a private reference type definition.
     * </p>
     */
    private static abstract class ReferenceTypeDef {

        DetailAST nameNode, extendsNode;
        String substitute;
        boolean processed;

        /**
         * <p>
         * Constructor that creates an object given a DetailAST node.
         * </p>
         *
         * @param aNode - the reference type node to be encapsulated
         */
        public ReferenceTypeDef(DetailAST aNode) {
            nameNode = aNode.findFirstToken(TokenTypes.IDENT);
            extendsNode = aNode.findFirstToken(TokenTypes.EXTENDS_CLAUSE);
            if (extendsNode == null) {
                extendsNode = emptyNode();
            }
            substitute = "";
            processed = false;
        }

        public String getSubstitute() {
            return substitute.substring(0, substitute.length() - 2);
        }
    }

    /**
     * <p>
     * Object for encapsulating a private interface type definition.
     * </p>
     */
    private static class InterfaceDef extends ReferenceTypeDef {

        /**
         * <p>
         * Constructor that creates an object given a DetailAST node.
         * </p>
         *
         * @param anIntfNode - the INTERFACE_DEF type node to be encapsulated
         * @throws IllegalArgumentException - if the argument is not of desired
         * type
         */
        public InterfaceDef(DetailAST anIntfNode)
                throws IllegalArgumentException {
            super(anIntfNode);
            if (anIntfNode.getType() != TokenTypes.INTERFACE_DEF) {
                throw new IllegalArgumentException("The argument Node to "
                        + "InterfaceDef should be of type INTERFACE_DEF.");
            }
        }

    }

    /**
     * <p>
     * Object for encapsulating a private inner class or enum definition.
     * </p>
     */
    private static class ClassEnumDef extends ReferenceTypeDef {

        DetailAST implementsNode;

        /**
         * <p>
         * Constructor that creates an object given a DetailAST node.
         * </p>
         *
         * @param aClassEnumDefNode - the CLASS_DEF or ENUM_DEF type node to be
         * @throws IllegalArgumentException - if the argument is not of desired
         * type encapsulated
         */
        public ClassEnumDef(DetailAST aClassEnumDefNode)
                throws IllegalArgumentException {
            super(aClassEnumDefNode);
            int type = aClassEnumDefNode.getType();
            if (type != TokenTypes.CLASS_DEF && type != TokenTypes.ENUM_DEF) {
                throw new IllegalArgumentException("The argument Node to "
                        + "ClassEnumDef should be of type CLASS_DEF or ENUM_DEF.");
            }

            implementsNode = aClassEnumDefNode.findFirstToken(TokenTypes.IMPLEMENTS_CLAUSE);
            if (implementsNode == null) {
                implementsNode = emptyNode();
            }
        }

        public String toString() {
            return implementsNode.getChildCount() > 0 ? implementsNode.getFirstChild().getText() : "null";
        }
    }

}
