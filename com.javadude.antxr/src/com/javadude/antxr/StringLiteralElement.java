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

class StringLiteralElement extends GrammarAtom {
    // atomText with quotes stripped and escape codes processed
    protected String processedAtomText;


    public StringLiteralElement(Grammar g, Token t, int autoGenType) {
        super(g, t, autoGenType);
        if (!(g instanceof LexerGrammar)) {
            // lexer does not have token types for string literals
            TokenSymbol ts = grammar.tokenManager.getTokenSymbol(atomText);
            if (ts == null) {
                g.antxrTool.error("Undefined literal: " + atomText, grammar.getFilename(), t.getLine(), t.getColumn());
            }
            else {
                tokenType = ts.getTokenType();
            }
        }
        line = t.getLine();

        // process the string literal text by removing quotes and escaping chars
        // If a lexical grammar, add the characters to the char vocabulary
        processedAtomText = new String();
        for (int i = 1; i < atomText.length() - 1; i++) {
            char c = atomText.charAt(i);
            if (c == '\\') {
                if (i + 1 < atomText.length() - 1) {
                    i++;
                    c = atomText.charAt(i);
                    switch (c) {
                        case 'n':
                            c = '\n';
                            break;
                        case 'r':
                            c = '\r';
                            break;
                        case 't':
                            c = '\t';
                            break;
                    }
                }
            }
            if (g instanceof LexerGrammar) {
                ((LexerGrammar)g).charVocabulary.add(c);
            }
            processedAtomText += c;
        }
    }

    @Override
    public void generate() {
        grammar.generator.gen(this);
    }

    @Override
    public Lookahead look(int k) {
        return grammar.theLLkAnalyzer.look(k, this);
    }
}
