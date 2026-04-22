package mg.federation.federationapi.dto;

public class ParrainDTO {

    private int membreId;
    private int collectiviteId;
    private String relation;

    public int getMembreId() {
        return membreId;
    }

    public void setMembreId(int membreId) {
        this.membreId = membreId;
    }

    public int getCollectiviteId() {
        return collectiviteId;
    }

    public void setCollectiviteId(int collectiviteId) {
        this.collectiviteId = collectiviteId;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
}