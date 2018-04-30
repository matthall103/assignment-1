package com.itv.assignment.checkout.service;

import com.itv.assignment.checkout.model.PricingRule;
import javax.annotation.Nonnull;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class CheckoutService {

    private Set<PricingRule> pricingRules;

    public CheckoutService(@Nonnull Set<PricingRule> pricingRules) {
        this.pricingRules = requireNonNull(pricingRules);
    }

    public PricingRule findPricingRule(@Nonnull String sku) {
        requireNonNull(sku);

        //stream through stored pricing rules to find the one that matches item being scanned
        Optional<PricingRule> pricingRuleOptional = pricingRules.stream()
                .filter(pricingRule -> pricingRule.getSku().equals(sku))
                .findAny();

        //return pricing rule or throw error
        return pricingRuleOptional
                .orElseThrow(() -> new RuntimeException("Could not find pricing rule for item with sku: " + sku));

    }

    /**
     * find the correct price change for item being added or removed.
     * @param items list of items already scanned
     * @param pricingRule matching pricing rule for item scanned
     * @param isRemoval boolean to determine if removal or addition of item to items list
     * @return the change to the total of the checkout
     * */
    public long getTotalPriceDelta(@Nonnull List<String> items, @Nonnull PricingRule pricingRule, boolean isRemoval) {
        requireNonNull(items);
        requireNonNull(pricingRule);

        //if this list is empty throw an error -> list should never be empty at this point
        if (items.size() == 0) {
            throw new RuntimeException("item list is empty");
        }

        //if the pricing rule has a multiBuy price it is assumed it will have a multiBuyLimit
        if (pricingRule.getMultiBuyPrice() != null) {

            //count number of items in list of the same type as the scanned item (list will include scanned item already)
            long numberOfItems = items.stream()
                    .filter(item -> item.equals(pricingRule.getSku()))
                    .count();

            //if the number of items mod the multiBuy limit is 0 the scanned item is a threshold item and the
            // special price must be used
            if (numberOfItems % pricingRule.getMultiBuyLimit() == 0) {

                //calculate the value of the items in the list before the threshold item was added. The difference
                //between this price and the multiBuy price is the delta.  This value is negated if removal
                long currentPrice = (pricingRule.getMultiBuyLimit() - 1) * pricingRule.getUnitPrice();
                return isRemoval ? currentPrice - pricingRule.getMultiBuyPrice() : pricingRule.getMultiBuyPrice() - currentPrice;
            }
        }

        //if the pricing rule does not have a multiBuy offer or the item is not a threshold item return the unit price.
        //this value is negated if removal;
        return isRemoval ? -pricingRule.getUnitPrice() : pricingRule.getUnitPrice();
    }

    /**
     * update the pricing rules
     * */
    public void updatePricingRules(@Nonnull Set<PricingRule> pricingRules) {
        this.pricingRules = requireNonNull(pricingRules);
    }

}