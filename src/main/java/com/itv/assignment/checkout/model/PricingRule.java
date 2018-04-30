package com.itv.assignment.checkout.model;


import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

public class PricingRule {

    private String sku;
    private long unitPrice;
    private Long multiBuyPrice;
    private Integer multiBuyLimit;

    public PricingRule(){}

    public PricingRule(@Nonnull String sku, long unitPrice, Long multiBuyPrice, Integer multiBuyLimit) {
        this.sku = requireNonNull(sku);
        this. unitPrice = unitPrice;
        this.multiBuyPrice = multiBuyPrice;
        this.multiBuyLimit = multiBuyLimit;
    }

    public PricingRule(@Nonnull String sku, long unitPrice) {
        this.sku = requireNonNull(sku);
        this. unitPrice = unitPrice;
    }

    public String getSku() {
        return sku;
    }

    public long getUnitPrice() {
        return unitPrice;
    }

    public Long getMultiBuyPrice() {
        return multiBuyPrice;
    }

    public Integer getMultiBuyLimit() {
        return multiBuyLimit;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setUnitPrice(long unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setMultiBuyPrice(Long multiBuyPrice) {
        this.multiBuyPrice = multiBuyPrice;
    }

    public void setMultiBuyLimit(Integer multiBuyLimit) {
        this.multiBuyLimit = multiBuyLimit;
    }
}
