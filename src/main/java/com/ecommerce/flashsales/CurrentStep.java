package com.ecommerce.flashsales;

public enum CurrentStep {
    CLIENTREQUESTING("0-Client Requesting"),
    THROTTING("1-Request Throtting"),
    SAFEPROTECTOR("2-Safe Protector Service"),
    POLICYCONTROLLER("3-Policy Controller Service"),
    INVENTORYMANAGER("4-Inventory Manager Service"),
    SHOPPINGCART("5-Shopping Cart Service"),
    ALLSTEPS("5-All Steps");

    private String msgBody;

    CurrentStep(String msgBody) {
        this.msgBody = msgBody;
    }

    public String msgBody() {
        return msgBody;
    }
}
