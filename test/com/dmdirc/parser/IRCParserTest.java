/*
 * Copyright (c) 2006-2007 Chris Smith, Shane Mc Cormack, Gregory Holmes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.dmdirc.parser;

import com.dmdirc.parser.callbacks.CallbackNotFoundException;
import com.dmdirc.parser.callbacks.interfaces.IAwayState;

import org.junit.Test;
import static org.junit.Assert.*;

public class IRCParserTest {
    
    @Test
    public void testIssue042() {
        boolean res = false;
        
        try {
            final IRCParser myParser = new IRCParser();
            myParser.getCallbackManager().addCallback("non-existant",new IAwayState() {
                public void onAwayState(IRCParser tParser, boolean currentState, String reason) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            });
        } catch (CallbackNotFoundException ex) {
            res = true;
        }
        
        assertTrue("addCallback() should throw exception for non-existant callbacks", res);
    }
    
    @Test
    public void testTokeniser() {
        final IRCParser myParser = new IRCParser();
        
        final String line1 = "a b c d e";
        final String line2 = "a b c :d e";
        final String line3 = ":a b:c :d e";
        
        final String[] res1 = myParser.tokeniseLine(line1);
        final String[] res2 = myParser.tokeniseLine(line2);
        final String[] res3 = myParser.tokeniseLine(line3);
        
        arrayEquals(res1, new String[]{"a", "b", "c", "d", "e"});
        arrayEquals(res2, new String[]{"a", "b", "c", "d e"});
        arrayEquals(res3, new String[]{":a", "b:c", "d e"});
    }
    
    private void arrayEquals(final String[] a1, final String[] a2) {
        assertEquals(a1.length, a2.length);
        
        for (int i = 0; i < a1.length; i++) {
            assertEquals(a1[i], a2[i]);
        }
    }
    
}
