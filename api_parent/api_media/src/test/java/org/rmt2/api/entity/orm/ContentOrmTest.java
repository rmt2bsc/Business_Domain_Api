package org.rmt2.api.entity.orm;

import org.dao.mapping.orm.rmt2.Content;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rmt2.api.document.DocumentMediaMockDataFactory;

/**
 * Test of Content ORM class
 * 
 * @author roy.terrell
 *
 */
public class ContentOrmTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testToString() {
        Content o1 = DocumentMediaMockDataFactory.createOrmContent(1000, 101, "tmp/",
                "image.jpg", 1024, 5555, "Media");
        String val = o1.toString();
        System.out.println(val);
        Assert.assertNotNull(val);
    }

    @Test
    public void testEquality() {
        boolean result = false;
        Content o1 = new Content();
        Content o2 = null;

        result = o1.equals(o2);
        Assert.assertFalse(result);

        o1 = DocumentMediaMockDataFactory.createOrmContent(1000, 101, "tmp/",
                "image.jpg", 1024, 5555, "Media");
        o2 = new Content();
        result = o1.equals(o2);
        Assert.assertFalse(result);
        
        o2.setContentId(1000);
        result = o1.equals(o2);
        Assert.assertFalse(result);
        
        o2.setMimeTypeId(101);
        result = o1.equals(o2);
        Assert.assertFalse(result);
        
        o2.setFilepath("tmp/");
        result = o1.equals(o2);
        Assert.assertFalse(result);
        
        o2.setFilename("image.jpg");
        result = o1.equals(o2);
        Assert.assertFalse(result);
        
        o2.setSize(1024);
        result = o1.equals(o2);
        Assert.assertFalse(result);
        
        o2.setProjectId(5555);
        result = o1.equals(o2);
        Assert.assertFalse(result);
        
        o2.setAppCode("Media");
        result = o1.equals(o2);
        Assert.assertFalse(result);
        
        o2.setModuleCode("ModuleCode");
        result = o1.equals(o2);
        Assert.assertFalse(result);
        
        o2.setTextData("TextData");
        result = o1.equals(o2);
        Assert.assertFalse(result);
        
        o2.setImageData("ImageData".getBytes());
        result = o1.equals(o2);
        Assert.assertTrue(result);
    }

    @Test
    public void testHashCode() {
        Content o1 = DocumentMediaMockDataFactory.createOrmContent(1000, 101, "tmp/",
                "image.jpg", 1024, 5555, "Media");
        Content o2 = DocumentMediaMockDataFactory.createOrmContent(1000, 101, "tmp/",
                "image.jpg", 1024, 5555, "Media");
        Assert.assertTrue(o1.equals(o2) && o2.equals(o1));
        Assert.assertEquals(o1.hashCode(), o2.hashCode());
    }
}
