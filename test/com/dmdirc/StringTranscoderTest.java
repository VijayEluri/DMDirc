/*
 * StringTranscoderTest.java
 * JUnit 4.x based test
 *
 * Created on 07 August 2007, 15:10
 */

package com.dmdirc;

import java.nio.charset.Charset;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Chris
 */
public class StringTranscoderTest {
    
    public StringTranscoderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void transcode() {
        String string = new String(new byte[]{(byte) 0xCA, (byte) 0xAE});
        
        StringTranscoder instance = new StringTranscoder(Charset.forName("UTF-8"));
        
        String res = instance.encode(string);

        byte[] result = res.getBytes();
        
        assertEquals(string, instance.decode(new String(result)));
    }
    
}
