/*******************************************************************************
 *  Copyright 2008 Scott Stanchfield.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * Contributors:
 *   Based on the ANTLR parser generator by Terence Parr, http://antlr.org
 *   Ric Klaren <klaren@cs.utwente.nl>
 *******************************************************************************/
package com.javadude.antxr;

import com.javadude.antxr.collections.AST;

/** Common AST node implementation */
public class CommonAST extends BaseAST {
	private static final long serialVersionUID = 1L;
	int ttype = Token.INVALID_TYPE;
    String text;


    /** Get the token text for this node */
    @Override
    public String getText() {
        return text;
    }

    /** Get the token type for this node */
    @Override
    public int getType() {
        return ttype;
    }

    @Override
    public void initialize(int t, String txt) {
        setType(t);
        setText(txt);
    }

    @Override
    public void initialize(AST t) {
        setText(t.getText());
        setType(t.getType());
    }

    public CommonAST() {
		// nothing
    }

    public CommonAST(Token tok) {
        initialize(tok);
    }

    @Override
    public void initialize(Token tok) {
        setText(tok.getText());
        setType(tok.getType());
    }

    /** Set the token text for this node */
    @Override
    public void setText(String text_) {
        text = text_;
    }

    /** Set the token type for this node */
    @Override
    public void setType(int ttype_) {
        ttype = ttype_;
    }
}
