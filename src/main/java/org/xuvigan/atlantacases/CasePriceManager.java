package org.xuvigan.atlantacases;

public class CasePriceManager {
    public static int getCasePrice(int caseId) {
        // code to retrieve the price of the case with the given ID from a database or configuration file
        int price = readPriceFromConfig(caseId);
        return price;
    }

    private static int readPriceFromConfig(int caseId) {
        return caseId;
    }
}