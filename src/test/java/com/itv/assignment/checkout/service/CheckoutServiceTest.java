package com.itv.assignment.checkout.service;


import com.itv.assignment.checkout.model.PricingRule;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CheckoutServiceTest {

    private CheckoutService underTest;

    @Before
    public void setUp() {

        Set<PricingRule> pricingRules = new HashSet<>();

        PricingRule pricingRule1 = new PricingRule("A", 50, 130L, 3);
        pricingRules.add(pricingRule1);
        PricingRule pricingRule2 = new PricingRule("B", 30, 45L, 2);
        pricingRules.add(pricingRule2);
        PricingRule pricingRule3 = new PricingRule("C", 20);
        pricingRules.add(pricingRule3);
        PricingRule pricingRule4 = new PricingRule("D", 15);
        pricingRules.add(pricingRule4);

        underTest = new CheckoutService(pricingRules);
    }

    @Test
    public void findPricingRule_shouldReturnCorrectPricingRule(){

        PricingRule pricingRuleResult = underTest.findPricingRule("A");

        assertEquals(50, pricingRuleResult.getUnitPrice());
        assertEquals(Long.valueOf(130), pricingRuleResult.getMultiBuyPrice());
        assertEquals(Integer.valueOf(3), pricingRuleResult.getMultiBuyLimit());
    }


    @Test
    public void findPricingRule_shouldThrowRuntimeExceptionIfPricingRuleNotFound(){

        try {
            underTest.findPricingRule("Z");
            fail("Expected runtime exception but none encountered");
        } catch (RuntimeException e) {
            assertEquals("Could not find pricing rule for item with sku: Z", e.getMessage());
        }
    }

    @Test
    public void getTotalPriceDelta_shouldReturnUnitPriceIfNotAtMultiBuyThreshold() {

        PricingRule pricingRule = new PricingRule("X", 50, 75L, 3);

        long result = underTest.getTotalPriceDelta(Collections.singletonList("X"), pricingRule, false);
        assertEquals(50L, result);
    }

    @Test
    public void getTotalPriceDelta_shouldReturnNegativeUnitPriceIfNotAtMultiBuyThresholdAndIsRemoval() {

        PricingRule pricingRule = new PricingRule("X", 50, 75L, 3);

        long result = underTest.getTotalPriceDelta(Collections.singletonList("X"), pricingRule, true);
        assertEquals(-50L, result);
    }

    @Test
    public void getTotalPriceDelta_shouldReturnNegativeUnitPriceIfItemIfQualifierNotMet() {
        PricingRule pricingRule = new PricingRule("X", 50, 75L, 3);

        long result = underTest.getTotalPriceDelta(Arrays.asList("X", "X"), pricingRule, true);
        assertEquals(-50L, result);

        long result2 = underTest.getTotalPriceDelta(Arrays.asList("X", "X", "X", "X"), pricingRule, true);
        assertEquals(-50L, result2);
    }

    @Test
    public void getTotalPriceDelta_shouldReturnPositiveTotalDeltaIfQualifierMetForItemRemoval() {
        PricingRule pricingRule = new PricingRule("X", 50, 75L, 3);

        long result = underTest.getTotalPriceDelta(Arrays.asList("X", "X", "X"), pricingRule, true);
        assertEquals(25L, result);
    }

    @Test
    public void getTotalPriceDelta_shouldReturnNegativeTotalDeltaIfQualifierMetForItemRemoval() {
        PricingRule pricingRule = new PricingRule("X", 50, 130L, 3);

        long result = underTest.getTotalPriceDelta(Arrays.asList("X", "X", "X"), pricingRule, true);
        assertEquals(-30L, result);
    }

    /**
     * if multiBuy price is more than price of ((multiBuyLimit - 1) * unitPrice)
     **/
    @Test
    public void getTotalPriceDelta_shouldReturnPositivePriceDeltaIfAtMultiBuyThreshold() {
        PricingRule pricingRule = new PricingRule("X", 50, 125L, 3);
        List<String> items = Arrays.asList("X", "X", "X");

        long result = underTest.getTotalPriceDelta(items, pricingRule, false);
        assertEquals(25L, result);
    }

    /**
     * if multiBuy price is less than price of ((multiBuyLimit - 1) * unitPrice)
     **/
    @Test
    public void getTotalPriceDelta_shouldReturnNegativePriceDeltaIfAtMultiBuyThreshold() {
        PricingRule pricingRule = new PricingRule("X", 50, 75L, 3);
        List<String> items = Arrays.asList("X", "X", "X");

        long result = underTest.getTotalPriceDelta(items, pricingRule, false);
        assertEquals(-25L, result);
    }

    /**
     * if multiBuy price is equal to the price of ((multiBuyLimit - 1) * unitPrice)
     **/
    @Test
    public void getTotalPriceDelta_shouldReturnZeroPriceDeltaIfAtMultiBuyThreshold() {
        PricingRule pricingRule = new PricingRule("X", 50, 100L, 3);
        List<String> items = Arrays.asList("X", "X", "X");

        long result = underTest.getTotalPriceDelta(items, pricingRule, false);
        assertEquals(0, result);
    }

    @Test
    public void getTotalPriceDelta_shouldReturnUnitPriceIfMultiBuyPriceIsNull() {
        PricingRule pricingRule = new PricingRule("X", 50, 125L, 3);

        long result = underTest.getTotalPriceDelta(Collections.singletonList("X"), pricingRule, false);
        assertEquals(50L, result);
    }

    @Test
    public void getTotalPriceDelta_shouldReturnNegativeUnitPriceIfMultiBuyPriceIsNullAndItemRemoval() {
        PricingRule pricingRule = new PricingRule("X", 50, 125L, 3);

        long result = underTest.getTotalPriceDelta(Collections.singletonList("X"), pricingRule, true);
        assertEquals(-50L, result);
    }

}
