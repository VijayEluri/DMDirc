/*
 * Copyright (c) 2006-2008 Chris Smith, Shane Mc Cormack, Gregory Holmes
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

package com.dmdirc.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows reading and writing to a plain text file via a list of lines.
 * 
 * @author chris
 */
public class TextFile {
    
    /** The file we're dealing with. */
    private File file;
    
    /** The input stream we're dealing with. */
    private InputStream is;
    
    /**
     * Creates a new instance of TextFile for the specified file.
     * 
     * @param filename The file to be read/written
     */
    public TextFile(final String filename) {
        file = new File(filename);
    }
    
    /**
     * Creates a new instance of TextFile for the specified URI.
     * 
     * @param uri The URI of the file
     */
    public TextFile(final URI uri) {
        file = new File(uri);
    }
    
    /**
     * Creates a new instance of TextFile for an input stream.
     * 
     * @param is The input stream to read from
     */
    public TextFile(final InputStream is) {
        this.is = is;
    }
    
    /**
     * Retrieves the contents of the file as a list of lines.
     * 
     * @return A list of lines in the file
     * @throws IOException if an I/O exception occurs
     */
    public List<String> getLines() throws IOException {
        final BufferedReader reader = new BufferedReader(
                file == null ? new InputStreamReader(is) : new FileReader(file));
        final List<String> res = new ArrayList<String>();
        
        String line;
        
        while ((line = reader.readLine()) != null) {
            res.add(line);
        }
        
        reader.close();
        
        return res;
    }
    
    /**
     * Writes the specified list of lines to the file.
     * 
     * @param lines The lines to be written
     * @throws IOException if an I/O exception occurs
     */
    public void writeLines(final List<String> lines) throws IOException {
        if (file == null) {
            throw new UnsupportedOperationException("Cannot write to TextFile "
                    + "opened with an InputStream");
        }
        
        final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
        
        writer.close();
    }

}
