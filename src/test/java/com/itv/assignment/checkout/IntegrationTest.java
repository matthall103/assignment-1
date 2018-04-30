package com.itv.assignment.checkout;

import com.itv.assignment.checkout.model.PricingRule;
import com.itv.assignment.checkout.service.CheckoutService;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class IntegrationTest {

    private Checkout checkout;

    @Before
    public void setUp() {

        Set<PricingRule> pricingRules = new HashSet<>();

        PricingRule pricingRule1 = new PricingRule("A", 50, 130L, 3);
        pricingRules.add(pricingRule1);
        PricingRule pricingRule2 = new PricingRule("B", 30, 45L, 2);
        pricingRules.add(pricingRule2);
        PricingRule pricingRule3 = new PricingRule("C", 20, 20L, 2);
        pricingRules.add(pricingRule3);
        PricingRule pricingRule4 = new PricingRule("D", 15, 10L, 2);
        pricingRules.add(pricingRule4);

        CheckoutService checkoutService = new CheckoutService(pricingRules);
        checkout = new Checkout(checkoutService);
    }

    @Test
    public void integrationTest_shouldAddItems() {

        long total = checkout.addItem("A");
        assertEquals( 50L, total);

        total = checkout.addItem("B");
        assertEquals( 80L, total);

        total = checkout.addItem("C");
        assertEquals( 100L, total);

        total = checkout.addItem("D");
        assertEquals( 115L, total);
    }

    @Test
    public void integrationTest_shouldAddItemsAtMultiBuyPriceSequentially() {

        checkout.addItem("A");
        checkout.addItem("A");
        long total = checkout.addItem("A");
        assertEquals(130L, total);
    }

    @Test
    public void integrationTest_shouldAddItemsAtMultiBuyPriceOutOfOrder() {

        checkout.addItem("A");
        checkout.addItem("D");
        checkout.addItem("A");

        long total = checkout.addItem("A");

        assertEquals(145L, total);
    }

    @Test
    public void integrationTest_shouldAddItemsAtMultiBuyPriceAtTwiceLimit() {

        checkout.addItem("A");
        checkout.addItem("A");
        checkout.addItem("A");
        checkout.addItem("A");
        checkout.addItem("A");

        long total = checkout.addItem("A");

        assertEquals(260L, total);
    }

    @Test
    public void integrationTest_shouldAddMultipleItemsAtMultiBuy() {

        checkout.addItem("A");
        checkout.addItem("B");
        checkout.addItem("A");
        long total = checkout.addItem("B");
        assertEquals(145L, total);

        total = checkout.addItem("A");
        assertEquals(175L, total);
    }

    @Test
    public void integrationTest_shouldReduceTotalIfMultiBuyPriceCheaperThanUnitPrice() {
        long total = checkout.addItem("D");
        assertEquals(15L, total);
        long newTotal = checkout.addItem("D");
        assertEquals(10L, newTotal);
    }

    @Test
    public void integrationTest_shouldNotChangeTotalIfMultiBuyPriceEqualToUnitPrice() {
        long total = checkout.addItem("C");
        assertEquals(20L, total);
        long newTotal = checkout.addItem("C");
        assertEquals(20L, newTotal);
    }

    @Test
    public void integrationTest_shouldRemoveItems() {

        checkout.addItem("A");
        checkout.addItem("B");
        checkout.addItem("C");
        checkout.addItem("D");

        long total = checkout.removeItem("D");
        assertEquals(100, total);
        total = checkout.removeItem("C");
        assertEquals(80, total);
        total = checkout.removeItem("B");
        assertEquals(50, total);
        total = checkout.removeItem("A");
        assertEquals(0, total);
    }

    @Test
    public void integrationTest_shouldRemoveItemsAtMultiBuyPriceSequentially() {

        checkout.addItem("A");
        checkout.addItem("A");
        checkout.addItem("A");
        long total = checkout.removeItem("A");
        assertEquals(100L, total);
    }

    @Test
    public void integrationTest_shouldRemoveItemsAtMultiBuyPriceOutOfOrder() {

        checkout.addItem("A");
        checkout.addItem("A");
        checkout.addItem("A");
        checkout.addItem("B");
        checkout.addItem("C");
        long total = checkout.removeItem("A");
        assertEquals(150L, total);
    }

    @Test
    public void integrationTest_shouldRemoveMultipleItemsAtMultiBuy() {

        checkout.addItem("A");
        checkout.addItem("B");
        checkout.addItem("A");
        checkout.addItem("B");
        checkout.addItem("A");
        long totalA = checkout.removeItem("A");
        assertEquals(145L, totalA);

        long totalB = checkout.removeItem("B");
        assertEquals(130L, totalB);
    }

    @Test
    public void integrationTest_shouldIncreaseTotalIfMultiBuyPriceCheaperThanUnitPriceAndRemoval() {
        checkout.addItem("D");
        long total = checkout.addItem("D");
        assertEquals(10L, total);

        long newTotal = checkout.removeItem("D");
        assertEquals(15L, newTotal);
    }

    @Test
    public void integrationTest_shouldNotChangeTotalIfMultiBuyPriceEqualToUnitPriceAndRemoval() {
        checkout.addItem("C");
        long total = checkout.addItem("C");
        assertEquals(20L, total);
        long newTotal = checkout.removeItem("C");
        assertEquals(20L, newTotal);
    }
}


