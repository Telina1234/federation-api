package mg.federation.federationapi.dto;

public class CreateMembershipFee {

    private String eligibleFrom;
    private String frequency;
    private double amount;
    private String label;

    public String getEligibleFrom() {
        return eligibleFrom;
    }

    public void setEligibleFrom(String eligibleFrom) {
        this.eligibleFrom = eligibleFrom;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}