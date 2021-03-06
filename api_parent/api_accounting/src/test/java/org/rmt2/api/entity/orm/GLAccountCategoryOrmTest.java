package org.rmt2.api.entity.orm;

import org.dao.mapping.orm.rmt2.GlAccountCategory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rmt2.api.AccountingMockDataFactory;

public class GLAccountCategoryOrmTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testToString() {
        GlAccountCategory o1 = AccountingMockDataFactory.createMockOrmGlAccountCategory(100, 1, "Category1");
        String val = o1.toString();
        System.out.println(val);
        Assert.assertNotNull(val);
    }

    @Test
    public void testEquality() {
        boolean result = false;
        GlAccountCategory o1 = new GlAccountCategory();
        GlAccountCategory o2 = null;

        result = o1.equals(o2);
        Assert.assertFalse(result);

        o1 = AccountingMockDataFactory.createMockOrmGlAccountCategory(100, 1, "Category1");
        o2 = new GlAccountCategory();
        result = o1.equals(o2);
        Assert.assertFalse(result);
        
        o2.setAcctTypeId(1);
        result = o1.equals(o2);
        Assert.assertFalse(result);
        
        o2.setAcctCatgId(100);
        result = o1.equals(o2);
        Assert.assertFalse(result);
        
        o2.setDescription("Category1");
        result = o1.equals(o2);
        Assert.assertTrue(result);
    }

    @Test
    public void testHashCode() {
        GlAccountCategory o1 = AccountingMockDataFactory.createMockOrmGlAccountCategory(100, 1, "Category1");
        GlAccountCategory o2 = AccountingMockDataFactory.createMockOrmGlAccountCategory(100, 1, "Category1");
        Assert.assertTrue(o1.equals(o2) && o2.equals(o1));
        Assert.assertEquals(o1.hashCode(), o2.hashCode());
    }
}
