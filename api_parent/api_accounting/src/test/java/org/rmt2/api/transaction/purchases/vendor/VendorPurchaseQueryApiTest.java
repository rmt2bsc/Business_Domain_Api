package org.rmt2.api.transaction.purchases.vendor;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.util.List;

import org.dao.mapping.orm.rmt2.PurchaseOrder;
import org.dto.PurchaseOrderDto;
import org.dto.adapter.orm.transaction.purchaseorder.Rmt2PurchaseOrderDtoFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modules.transaction.purchases.vendor.VendorPurchasesApi;
import org.modules.transaction.purchases.vendor.VendorPurchasesApiException;
import org.modules.transaction.purchases.vendor.VendorPurchasesApiFactory;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.api.persistence.AbstractDaoClientImpl;
import com.api.persistence.db.orm.Rmt2OrmClientFactory;

/**
 * Tests creditor purchases transaction query Api.
 * 
 * @author rterrell
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, ResultSet.class })
public class VendorPurchaseQueryApiTest extends VendorPurchaseApiTestData {

    private static final String TEST_ACCOUNT_NO = "1111";
    private static final int TEST_CREDITOR_ID = 1111111;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        super.tearDown();
        return;
    }

    @Test
    public void testFetchAll_PurchaseOrders() {
        // Mock method call to get vendor purchase orders 
        PurchaseOrder mockCriteria = new PurchaseOrder();
        try {
            when(this.mockPersistenceClient.retrieveList(eq(mockCriteria)))
                    .thenReturn(this.mockPurchaseOrders);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("All vendor purchase orders fetch test case setup failed");
        }

        // Perform test
        VendorPurchasesApiFactory f = new VendorPurchasesApiFactory();
        VendorPurchasesApi api = f.createApi(mockDaoClient);
        PurchaseOrderDto criteria = Rmt2PurchaseOrderDtoFactory.createPurchaseOrderInstance(null);
        List<PurchaseOrderDto> results = null;
        try {
            results = api.getPurchaseOrder(criteria);
        } catch (VendorPurchasesApiException e) {
            e.printStackTrace();
            Assert.fail("Test failed due to unexpected exception thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertEquals(5, results.size());

    }

    @Test
    public void testFetchSingle_PurchaseOrder() {
       Assert.fail("Implement test case");
    }

    @Test
    public void testFetchPurchaseOrder_NotFound() {
        Assert.fail("Implement test case");
    }

    @Test
    public void testFetchPurchaseOrderItems() {
        Assert.fail("Implement test case");
    }
    
    @Test
    public void testFetchPurchaseOrderItems_NotFound() {
        Assert.fail("Implement test case");
    }
}