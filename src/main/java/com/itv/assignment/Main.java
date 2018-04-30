package com.itv.assignment;


import com.itv.assignment.checkout.commandLine.CheckoutInterface;

public class Main {

    private static String pricingRulesPath;

    public static void main(String[] args) {

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-pricing-rules") && args[i+1] != null) {
                pricingRulesPath = args[i+1];
            }
        }

        CheckoutInterface checkoutInterface = new CheckoutInterface(pricingRulesPath);
        checkoutInterface.run();
    }
}
