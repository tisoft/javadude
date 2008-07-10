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

/**A Stream of Token objects fed to the parser from a Tokenizer that can
 * be rewound via mark()/rewind() methods.
 * <p>
 * A dynamic array is used to buffer up all the input tokens.  Normally,
 * "k" tokens are stored in the buffer.  More tokens may be stored during
 * guess mode (testing syntactic predicate), or when LT(i>k) is referenced.
 * Consumption of tokens is deferred.  In other words, reading the next
 * token is not done by conume(), but deferred until needed by LA or LT.
 * <p>
 *
 * @see com.javadude.antxr.Token
 * @see com.javadude.antxr.Tokenizer
 * @see com.javadude.antxr.TokenQueue
 */


public class TokenBuffer {

    // Token source
    protected TokenStream input;

    // Number of active markers
    int nMarkers = 0;

    // Additional offset used when markers are active
    int markerOffset = 0;

    // Number of calls to consume() since last LA() or LT() call
    int numToConsume = 0;

    // Circular queue
    TokenQueue queue;

    /** Create a token buffer */
    public TokenBuffer(TokenStream input_) {
        input = input_;
        queue = new TokenQueue(1);
    }

    /** Reset the input buffer to empty state */
    public void reset() {
        nMarkers = 0;
        markerOffset = 0;
        numToConsume = 0;
        queue.reset();
    }

    /** Mark another token for deferred consumption */
    public void consume() {
        numToConsume++;
    }

    /** Ensure that the token buffer is sufficiently full */
    private void fill(int amount) throws TokenStreamException {
        syncConsume();
        // Fill the buffer sufficiently to hold needed tokens
        while (queue.nbrEntries < amount + markerOffset) {
            // Append the next token
            queue.append(input.nextToken());
        }
    }

    /** return the Tokenizer (needed by ParseView) */
    public TokenStream getInput() {
        return input;
    }

    /** Get a lookahead token value */
    public int LA(int i) throws TokenStreamException {
        fill(i);
        return queue.elementAt(markerOffset + i - 1).getType();
    }

    /** Get a lookahead token */
    public Token LT(int i) throws TokenStreamException {
        fill(i);
        return queue.elementAt(markerOffset + i - 1);
    }

    /**Return an integer marker that can be used to rewind the buffer to
     * its current state.
     */
    public int mark() {
        syncConsume();
//System.out.println("Marking at " + markerOffset);
//try { for (int i = 1; i <= 2; i++) { System.out.println("LA("+i+")=="+LT(i).getText()); } } catch (ScannerException e) {}
        nMarkers++;
        return markerOffset;
    }

    /**Rewind the token buffer to a marker.
     * @param mark Marker returned previously from mark()
     */
    public void rewind(int mark) {
        syncConsume();
        markerOffset = mark;
        nMarkers--;
//System.out.println("Rewinding to " + mark);
//try { for (int i = 1; i <= 2; i++) { System.out.println("LA("+i+")=="+LT(i).getText()); } } catch (ScannerException e) {}
    }

    /** Sync up deferred consumption */
    private void syncConsume() {
        while (numToConsume > 0) {
            if (nMarkers > 0) {
                // guess mode -- leave leading tokens and bump offset.
                markerOffset++;
            }
            else {
                // normal mode -- remove first token
                queue.removeFirst();
            }
            numToConsume--;
        }
    }
}
