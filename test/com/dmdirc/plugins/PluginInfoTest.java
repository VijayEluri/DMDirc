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

package com.dmdirc.plugins;

import com.dmdirc.Main;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

public class PluginInfoTest {
    
    private PluginInfo pi;

    @Test
    public void testCheckMinimum() throws PluginException {
        pi = new PluginInfo("moo", false);
        
        assertTrue(pi.checkMinimumVersion("5", 6));
        assertTrue(pi.checkMinimumVersion("5", 5));
        assertTrue(pi.checkMinimumVersion("0", 17));
        assertTrue(pi.checkMinimumVersion("100", 0));
        assertTrue(pi.checkMinimumVersion("0", 0));
        assertFalse(pi.checkMinimumVersion("abc", 6));
        assertFalse(pi.checkMinimumVersion("7", 6));
    }
    
    @Test
    public void testCheckMaximim() throws PluginException {
        pi = new PluginInfo("moo", false);
        
        assertTrue(pi.checkMaximumVersion("6", 6));
        assertTrue(pi.checkMaximumVersion("7", 6));
        assertTrue(pi.checkMaximumVersion("0", 6));
        assertTrue(pi.checkMaximumVersion("6", 0));
        assertTrue(pi.checkMaximumVersion("0", 0));
        assertTrue(pi.checkMaximumVersion("", 17));
        assertFalse(pi.checkMaximumVersion("abc", 6));
        assertFalse(pi.checkMaximumVersion("7", 10));
    }
    
    @Test
    public void testOS() throws PluginException {
        pi = new PluginInfo("moo", false);
        
        assertTrue(pi.checkOS("windows", "windows", "xp", "x86"));
        assertFalse(pi.checkOS("windows", "linux", "2.6.2.11", "x86"));
        assertTrue(pi.checkOS("windows:xp|98|3\\.1", "windows", "xp", "x86"));
        assertFalse(pi.checkOS("windows:xp|98|3\\.1", "windows", "vista", "x86"));
        assertFalse(pi.checkOS("windows:xp|98|3\\.1", "linux", "2.6.2.11", "x86"));
        assertTrue(pi.checkOS("windows:xp|98|3\\.1:.86", "windows", "xp", "x86"));
        assertFalse(pi.checkOS("windows:xp|98|3\\.1:.86", "windows", "xp", "mips"));
        assertFalse(pi.checkOS("windows:xp|98|3\\.1:.86", "windows", "vista", "x86"));
        assertFalse(pi.checkOS("windows:xp|98|3\\.1:.86", "linux", "2.6.2.11", "x86"));        
    }
    
    @Test @Ignore
    public void testLoad() throws PluginException {
        Main.setConfigDir(new File(getClass().getResource("testplugin.jar").getFile()).getParent());
        PluginInfo pi = new PluginInfo("testplugin.jar");
        assertEquals("Author <em@il>", pi.getAuthor());
        assertEquals("Friendly", pi.getFriendlyVersion());
        assertEquals("Description goes here", pi.getDescription());
        assertEquals("randomname", pi.getName());
        assertEquals("Friendly name", pi.getNiceName());
        assertEquals(3, pi.getVersion());
    }
    
    @Test @Ignore
    public void testUpdate() throws PluginException, IOException {
        final File dir = new File(File.createTempFile("dmdirc-plugin-test", null).getParentFile(),
                "dmdirc-plugin-test-folder");
        final File pluginDir = new File(dir, "plugins");
        
        dir.deleteOnExit();
        pluginDir.mkdirs();
        
        new File(pluginDir, "test.jar").createNewFile();
        new File(pluginDir, "test.jar.update").createNewFile();
        
        Main.setConfigDir(dir.getAbsolutePath());
        System.out.println(Main.getConfigDir());
        System.out.println(PluginManager.getPluginManager().getDirectory());
        new PluginInfo("test.jar", false);
        
        assertTrue(new File(pluginDir, "test.jar").exists());
        assertFalse(new File(pluginDir, "test.jar.update").exists());
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(PluginInfoTest.class);
    }
}