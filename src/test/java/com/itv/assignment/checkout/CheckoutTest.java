package com.itv.assignment.checkout;

import com.itv.assignment.checkout.model.PricingRule;
import com.itv.assignment.checkout.service.CheckoutService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


public class CheckoutTest {

    private Checkout underTest;
    private CheckoutService checkoutServiceMock;

    @Before
    public void setUp() {

        checkoutServiceMock = Mockito.mock(CheckoutService.class);

        PricingRule pricingRule = new PricingRule("A", 50L, 125L, 3);
        when(checkoutServiceMock.findPricingRule("A")).thenReturn(pricingRule);

        underTest = new Checkout(checkoutServiceMock);
    }

    @Test
    public void addItem_shouldAddItemWithPositiveValue() {

        when(checkoutServiceMock.getTotalPriceDelta(anyList(), any(PricingRule.class), eq(false))).thenReturn(50L);
        underTest.addItem("A");
        long result = underTest.addItem("A");
        assertEquals(100L, result);
    }

    @Test
    public void addItem_shouldAddItemWithNegativeValue() {

        when(checkoutServiceMock.getTotalPriceDelta(anyList(), any(PricingRule.class), eq(false))).thenReturn(100L);
        underTest.addItem("A");
        when(checkoutServiceMock.getTotalPriceDelta(anyList(), any(PricingRule.class), eq(false))).thenReturn(-75L);
        long result = underTest.addItem("A");
        assertEquals(25L, result);
    }

    @Test
    public void removeItem_shouldRemoveItemWithPositiveValue() {

        when(checkoutServiceMock.getTotalPriceDelta(anyList(), any(PricingRule.class), eq(false))).thenReturn(50L);
        when(checkoutServiceMock.getTotalPriceDelta(anyList(), any(PricingRule.class), eq(true))).thenReturn(-50L);
        underTest.addItem("A");
        underTest.addItem("A");
        long result = underTest.removeItem("A");
        assertEquals(50L, result);
    }

    @Test
    public void removeItem_shouldRemoveItemWithNegativeValue() {

        when(checkoutServiceMock.getTotalPriceDelta(anyList(), any(PricingRule.class), eq(false))).thenReturn(100L);
        underTest.addItem("A");
        when(checkoutServiceMock.getTotalPriceDelta(anyList(), any(PricingRule.class), eq(true))).thenReturn(15L);
        long result = underTest.removeItem("A");
        assertEquals(115L, result);
    }

    @Test
    public void finishTransaction_shouldReturnFinalTotal() {
        when(checkoutServiceMock.getTotalPriceDelta(anyList(), any(PricingRule.class), eq(false))).thenReturn(50L);
        underTest.addItem("A");
        long finalTotal = underTest.finishTransaction();
        assertEquals(50, finalTotal);
    }

    @Test
    public void finishTransaction_shouldResetTotal() {
        when(checkoutServiceMock.getTotalPriceDelta(anyList(), any(PricingRule.class), eq(false))).thenReturn(50L);
        underTest.addItem("A");
        long finalTotal = underTest.finishTransaction();
        assertEquals(50, finalTotal);
        long newTotal = underTest.finishTransaction();
        assertEquals(0, newTotal);
    }

}
