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
package com.javadude.antxr.debug;

public class SyntacticPredicateEvent extends GuessingEvent {
	private static final long serialVersionUID = 1L;
	public SyntacticPredicateEvent(Object source) {
		super(source);
	}
	public SyntacticPredicateEvent(Object source, int type) {
		super(source, type);
	}
	@Override
    /** This should NOT be called from anyone other than ParserEventSupport! */
	void setValues(int type, int guessing) {
		super.setValues(type, guessing);
	}
	@Override
    public String toString() {
		return "SyntacticPredicateEvent [" + getGuessing() + "]";
	}
}
