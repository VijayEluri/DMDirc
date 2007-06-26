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

package com.dmdirc.resourcemanager;

import com.dmdirc.logger.ErrorLevel;
import com.dmdirc.logger.Logger;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Provides an easy way to access files inside a jar.
 */
public final class JarResourceManager extends ResourceManager {
    
    /** Zipfile instance. */
    private final JarFile jarFile;
    
    /** Entries list. */
    private final List<String> entries;
    
    /**
     * Instantiates JarResourceManager.
     * 
     * @param fileName Filename of the jar to load
     * @throws IOException Throw when the jar fails to load
     */
    protected JarResourceManager(final String fileName) throws IOException {
        super();
        
        this.jarFile = new JarFile(fileName);
        entries = new ArrayList<String>();
        final Enumeration<? extends JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            entries.add(jarEntries.nextElement().getName());
        }
    }
    
    /** {@inheritDoc} */
    public byte[] getResourceBytes(final String resource) {
        final ZipEntry jarEntry = jarFile.getEntry(resource);
        BufferedInputStream inputStream;
        
        
        if (jarEntry == null) {
            return new byte[0];
        }
        
        if (jarEntry.isDirectory()) {
            return new byte[0];
        }
        
        final byte[] bytes = new byte[(int) jarEntry.getSize()];
        
        try {
            inputStream =
                    new BufferedInputStream(jarFile.getInputStream(jarEntry));
        } catch (IOException ex) {
            return new byte[0];
        }
        
        try {
            inputStream.read(bytes);
        } catch (IOException ex) {
            return new byte[0];
        }
        
        try {
            inputStream.close();
        } catch (IOException ex) {
            Logger.userError(ErrorLevel.LOW, "Unable to close stream");
        }
        
        return bytes;
    }
    
    /** {@inheritDoc} */
    public InputStream getResourceInputStream(final String resource) {
        final JarEntry jarEntry = (JarEntry) jarFile.getEntry(resource);
        
        if (jarEntry == null) {
            return null;
        }
        
        try {
            return jarFile.getInputStream(jarEntry);
        } catch (IOException ex) {
            return null;
        }
        
    }
    
    /** {@inheritDoc} */
    public Map<String, byte[]> getResourcesStartingWithAsBytes(
            final String resourcesPrefix) {
        final Map<String, byte[]> resources = new HashMap<String, byte[]>();
        
        for (String entry : entries) {
            if (entry.startsWith(resourcesPrefix)) {
                resources.put(entry, getResourceBytes(entry));
            }
        }
        
        return resources;
    }
    
    /** {@inheritDoc} */
    public Map<String, InputStream> getResourcesStartingWithAsInputStreams(
            final String resourcesPrefix) {
        final Map<String, InputStream> resources =
                new HashMap<String, InputStream>();
        
        for (String entry : entries) {
            if (entry.startsWith(resourcesPrefix)) {
                resources.put(entry, getResourceInputStream(entry));
            }
        }
        
        return resources;
    }
}

