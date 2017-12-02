package com.example.lenovo.funnygrasshopper;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ExampleUnitTest {

    @Test
    public void GrassHopperTest() throws Exception {
        GrassHopper grass = new GrassHopper();
        grass.key_create("Keyy");
        String txt ="Hello my friend\0";
        grass.Parsing(txt);
        assertEquals(grass.getKey()[3], "\u0015ö,´D÷s\u0018ýJ5%j±\f\u0088");
        grass.encrypt();
        assertEquals(grass.real(), "¨x&ä>È¢á\u001FcÞá@ßbù");
        grass.decrypt();
        String txt2 = grass.real();
        assertEquals(txt, txt2);

    }


}