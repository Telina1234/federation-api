package mg.federation.federationapi.dto;

public class FinancialAccount {

    private String id;
    private String type; // CASH, MOBILE, BANK
    private double amount;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}