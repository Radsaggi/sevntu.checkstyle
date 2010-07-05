package com.puppycrawl.tools.checkstyle.checks.naming;

import java.util.ArrayList;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class InnerClassCheck extends Check {
	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.CLASS_DEF, TokenTypes.VARIABLE_DEF,
				TokenTypes.METHOD_DEF };
	}

	public boolean firstInternalClass = false;
	public boolean rootClass = true;
	public ArrayList<Boolean> isFirstInternalClass = new ArrayList<Boolean>();
	public ArrayList<Integer> countVarMethInInternClass = new ArrayList<Integer>();
	public int countInternalClass = 0;

	@Override
	public void visitToken(DetailAST ast) {
		int countVarMeth = 0;
		if (rootClass) {
			rootClass = false;
			isFirstInternalClass.add(false);
			countVarMeth = ast.findFirstToken(TokenTypes.OBJBLOCK)
					.getChildCount(TokenTypes.VARIABLE_DEF)
					+ ast.findFirstToken(TokenTypes.OBJBLOCK).getChildCount(
							TokenTypes.METHOD_DEF);
			countVarMethInInternClass.add(countVarMeth);
			return;
		}

		if ((ast.getType() == TokenTypes.VARIABLE_DEF)
				&& (ast.getParent().getType() == TokenTypes.SLIST)) {
			return;
		}

		if ((!isFirstInternalClass.get(countInternalClass) && (countVarMethInInternClass
				.get(countInternalClass) > 0))
				&& (ast.getType() == (TokenTypes.VARIABLE_DEF) || ast.getType() == (TokenTypes.METHOD_DEF))) {
			countVarMethInInternClass.set(countInternalClass,
					countVarMethInInternClass.get(countInternalClass) - 1);
			if (countVarMethInInternClass.get(countInternalClass) == 0) {
				countVarMethInInternClass.remove(countInternalClass);
				isFirstInternalClass.remove(countInternalClass);
				countInternalClass--;
			}
			return;
		}

		if (ast.getType() == (TokenTypes.CLASS_DEF)) {
			firstInternalClass = true;
			isFirstInternalClass.set(countInternalClass, firstInternalClass);
			countVarMeth = ast.findFirstToken(TokenTypes.OBJBLOCK)
					.getChildCount(TokenTypes.VARIABLE_DEF)
					+ ast.findFirstToken(TokenTypes.OBJBLOCK).getChildCount(
							TokenTypes.METHOD_DEF);
			countVarMethInInternClass.add(countVarMeth);
			isFirstInternalClass.add(false);
			countInternalClass++;
			return;
		}

		if ((ast.getType() == (TokenTypes.VARIABLE_DEF) || ast.getType() == (TokenTypes.METHOD_DEF))) {
			// error
			log(ast.getLineNo(),
					"Fields and methods should be before inner classes");
		}
	}
}