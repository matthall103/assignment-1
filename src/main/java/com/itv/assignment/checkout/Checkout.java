package com.itv.assignment.checkout;


import com.itv.assignment.checkout.model.PricingRule;
import com.itv.assignment.checkout.service.CheckoutService;
import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class Checkout {

    private List<String> items = new ArrayList<>();
    private long total;

    private final CheckoutService checkoutService;

    public Checkout(@Nonnull CheckoutService checkoutService) {
        this.checkoutService = requireNonNull(checkoutService);
    }

    /**
    * add Item to shopping list and return new total;
    **/
    public long addItem(@Nonnull String sku) {
        requireNonNull(sku);

        //get correct pricing rule for scanned item.
        PricingRule pricingRule;
        try {
            pricingRule = checkoutService.findPricingRule(sku);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return total;
        }

        items.add(sku);
        long totalDelta;
        try {

            totalDelta = checkoutService.getTotalPriceDelta(items, pricingRule, false);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage() + ", no items have been added");
            // if price change cannot be determined remove item from list
            items.remove(sku);
            return total;
        }
        //add price change to total and return
        return total += totalDelta;
    }

    /**
     * remove Item from shopping list and return new total;
     **/
    public long removeItem(@Nonnull String sku) {
        requireNonNull(sku);

        //if list is empty don't try and remove anything
        if (items.size() == 0) {
            return total;
        }

        //get correct pricing rule for scanned item.
        PricingRule pricingRule;
        try {
            pricingRule = checkoutService.findPricingRule(sku);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return total;
        }

        long totalDelta;
        try {
            //get total price delta
            totalDelta = checkoutService.getTotalPriceDelta(items, pricingRule, true);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage() + ", no items have been removed");
            return total;
        }

        //remove Item from list
        items.remove(sku);
        // add price change to total and return
        return total += totalDelta;
    }

    private PricingRule getPricingRule(String sku) {
        PricingRule pricingRule;
        try {
            pricingRule = checkoutService.findPricingRule(sku);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return pricingRule;
    }

    /**
     * finalise transaction, return final total and reset list and total
     * */
    public long finishTransaction() {
        long finalTotal = total;
        reset();
        return finalTotal;
    }

    private void reset() {
        items.clear();
        total = 0;
    }
}
