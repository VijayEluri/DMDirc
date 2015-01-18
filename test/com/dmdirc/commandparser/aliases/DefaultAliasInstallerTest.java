/*
 * Copyright (c) 2006-2015 DMDirc Developers
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

package com.dmdirc.commandparser.aliases;

import com.dmdirc.DMDircMBassador;
import com.dmdirc.events.AppErrorEvent;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.nio.file.attribute.PosixFilePermissions.asFileAttribute;
import static java.nio.file.attribute.PosixFilePermissions.fromString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefaultAliasInstallerTest {

    @Mock private DMDircMBassador eventBus;

    private FileSystem fs;
    private Path path;

    private DefaultAliasInstaller installer;

    @Before
    public void setup() {
        fs = Jimfs.newFileSystem(Configuration.unix().toBuilder()
                .setAttributeViews("posix").build());
        path = fs.getPath("aliases.yml");

        installer = new DefaultAliasInstaller(path, eventBus);
    }

    @After
    public void tearDown() throws IOException {
        fs.close();
    }

    @Test
    public void testNeedsMigrationIfFileDoesntExist() {
        assertTrue(installer.needsMigration());
    }

    @Test
    public void testNeedsMigrationIfFileExist() throws IOException {
        Files.createFile(path);
        assertFalse(installer.needsMigration());
    }

    @Test
    public void testCopiesDefaultAliases() throws IOException {
        installer.migrate();
        assertTrue(Files.exists(path));
        verify(eventBus, never()).publish(any());
    }

    @Test
    public void testRaisesErrorIfCannotCopyDefaultAliases() throws IOException {
        Files.createFile(path, asFileAttribute(fromString("r--r--r--")));
        installer.migrate();
        verify(eventBus).publish(isA(AppErrorEvent.class));
    }

}