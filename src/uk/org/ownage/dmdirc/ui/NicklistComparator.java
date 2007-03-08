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

package uk.org.ownage.dmdirc.ui;

import java.util.Comparator;
import uk.org.ownage.dmdirc.parser.ChannelClientInfo;

/**
 * Compares nicklist entries to each other, for sorting purposes
 */
public class NicklistComparator implements Comparator<ChannelClientInfo> {
    private boolean sortByMode = true;
    private boolean sortByCase = false;
    
    /**
     * Creates a new instance of NicklistComparator
     * @param sortByMode sorts by channel mode of the user
     * @param sortByCase sorts by nickname case
     */
    public NicklistComparator(boolean sortByMode, boolean sortByCase) {
        this.sortByMode = sortByMode;
        this.sortByCase = sortByCase;
    }
    
    /**
     * Compares two ChannelClient objects based on the settings the comparator was
     * initialised with
     * @param client1 the first client to be compared
     * @param client2 the second client to be compared
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
     */
    public int compare(ChannelClientInfo client1, ChannelClientInfo client2) {
        String nickname1 = client1.getNickname();
        String nickname2 = client2.getNickname();
        
        if (sortByMode) {
            if (client1.getImportantModeValue() > client2.getImportantModeValue()) {
                return -1;
            } else if (client1.getImportantModeValue() < client2.getImportantModeValue()) {
                return 1;
            }
        }
        
        if (sortByCase) {
            return nickname1.compareTo(nickname2);
        } else {
            return nickname1.compareToIgnoreCase(nickname2);
        }
    }
}
