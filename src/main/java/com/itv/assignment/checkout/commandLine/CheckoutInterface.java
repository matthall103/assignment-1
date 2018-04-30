package com.itv.assignment.checkout.commandLine;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.itv.assignment.checkout.Checkout;
import com.itv.assignment.checkout.model.PricingRule;
import com.itv.assignment.checkout.service.CheckoutService;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class CheckoutInterface {

    private String pricingRulesFilePath;

    public CheckoutInterface(@Nonnull String pricingRulesFilePath) {
        this.pricingRulesFilePath = requireNonNull(pricingRulesFilePath);
    }

    public void run() {

        CheckoutService checkoutService = new CheckoutService(extractPricingRulesFromFile(pricingRulesFilePath));
        Checkout checkout = new Checkout(checkoutService);
        System.out.println("Starting new Checkout");

        String input = null;
        Scanner scanner = new Scanner(System.in);
        while (!"exit".equals(input)) {

            System.out.println("Starting new transaction");

            while (!":q".equals(input)) {
                long total = 0;

                System.out.println("Please enter sku: ");
                input = scanner.nextLine();

                if (input.equals("update")) {
                    System.out.println("Please enter new file path (leave blank for default): ");
                    String updatedPath = scanner.nextLine();

                    if(updatedPath != null && !updatedPath.equals("")) {
                        checkoutService.updatePricingRules(extractPricingRulesFromFile(updatedPath));
                    } else {
                        checkoutService.updatePricingRules(extractPricingRulesFromFile(pricingRulesFilePath));
                    }

                    continue;

                } else if (input.equals(":r")) {
                    System.out.println("Please enter sku for item to return: ");
                    String removal = scanner.nextLine();
                    total = checkout.removeItem(removal);

                } else if (!input.equals("") && !input.equals(":q") && !input.equals("update")) {
                    total = checkout.addItem(input);
                }

                System.out.println("New total is: " + total);
            }

            long finalTotal = checkout.finishTransaction();
            System.out.println("The final total comes to: " + finalTotal + ". Press enter to start new transaction, type exit to quit");
            input = scanner.nextLine();

            while(!input.equals("") && !input.equals("exit")) {
                System.out.println("Please press enter to start a new transaction or type exit to quit");
                input = scanner.nextLine();
            }
        }
    }

    private Set<PricingRule> extractPricingRulesFromFile(@Nonnull String filePath) {
        requireNonNull(filePath);
        Set<PricingRule> pricingRules = new HashSet<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            pricingRules = mapper.readValue(new File(filePath),
                    mapper.getTypeFactory().constructCollectionType(Set.class, PricingRule.class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Long> map = pricingRules.stream()
                .collect(Collectors.groupingBy(PricingRule::getSku, Collectors.counting()));

        if (map.values().stream().anyMatch(count -> count > 1)) {
            throw new RuntimeException("Pricing rules contain multiple rules for the same SKU");
        }

        return pricingRules;
    }
}
