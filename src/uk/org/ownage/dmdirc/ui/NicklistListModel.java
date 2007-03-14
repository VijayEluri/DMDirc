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

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.AbstractListModel;

import uk.org.ownage.dmdirc.Config;
import uk.org.ownage.dmdirc.parser.ChannelClientInfo;

/**
 * Stores and provides means to modify nicklist data for a channel.
 */
public final class NicklistListModel extends AbstractListModel {
    
    /**
     * A version number for this class. It should be changed whenever the class
     * structure is changed (or anything else that would prevent serialized
     * objects being unserialized with the new class).
     */
    private static final long serialVersionUID = 1;
    
    /**
     * stores the nicknames to be shown in this list.
     */
    private ArrayList<ChannelClientInfo> nicknames;
    
    /**
     * Creates a new empty model.
     */
    public NicklistListModel() {
        nicknames = new ArrayList<ChannelClientInfo>();
    }
    
    /**
     * Creates a new model and initiliases it with the data provided.
     * @param newNicknames list of nicknames used for initialisation
     */
    public NicklistListModel(final ArrayList<ChannelClientInfo> newNicknames) {
        this.nicknames = newNicknames;
        this.sort();
    }
    
    /**
     * Returns the size of the current nicklist.
     * @return nicklist size
     */
    public int getSize() {
        return nicknames.size();
    }
    
    /**
     * Returns the element at the specified place in the nicklist.
     * @param index index of nick required
     * @return nicklist entry requested
     */
    public ChannelClientInfo getElementAt(final int index) {
        return nicknames.get(index);
    }
    
    /**
     * Sorts the nicklist based on settings in the Config, only needed if
     * the sort.
     * method changes
     */
    public void sort() {
        boolean sortByMode = true;
        boolean sortByCase = false;
        if (Config.hasOption("ui", "sortByMode")) {
            sortByMode = Config.getOption("ui", "sortByMode").equals("true");
        }
        if (Config.hasOption("ui", "sortByCase")) {
            sortByCase = Config.getOption("ui", "sortByCase").equals("true");
        }
        Collections.sort(nicknames,
                new NicklistComparator(sortByMode, sortByCase));
        this.fireContentsChanged(this, 0, nicknames.size());
    }
    
    /**
     * Replaces the entire nicklist with the arraylist specified.
     * @param clients replacement nicklist
     * @return boolean success
     */
    public boolean replace(final ArrayList<ChannelClientInfo> clients) {
        boolean returnValue = false;
        
        nicknames.clear();
        returnValue = nicknames.addAll(clients);
        
        this.sort();
        
        return returnValue;
    }
    
    /**
     * Adds the specified client to the nicklist.
     * @param client client to add to the nicklist
     * @return boolean success
     */
    public boolean add(final ChannelClientInfo client) {
        boolean returnValue = false;
        
        returnValue = nicknames.add(client);
        
        this.sort();
        
        return returnValue;
    }
    
    /**
     * Removes the specified client from the nicklist.
     * @param client client to remove
     * @return boolean success
     */
    public boolean remove(final ChannelClientInfo client) {
        boolean returnValue;
        returnValue = nicknames.remove(client);
        this.fireIntervalRemoved(this, 0, nicknames.size());
        return returnValue;
    }
    
    /**
     * Removes the specified index from the nicklist.
     * @param index index to remove
     * @return ChannelClientInfo client removed
     */
    public ChannelClientInfo remove(final int index) {
        ChannelClientInfo returnValue;
        returnValue = nicknames.remove(index);
        this.fireIntervalRemoved(this, 0, nicknames.size());
        return returnValue;
    }
    
    /**
     *Fires the model changed event forcing the model to re-render.
     */
    public void rerender() {
        this.fireContentsChanged(this, 0, nicknames.size());
    }
}
