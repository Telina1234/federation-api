package mg.federation.federationapi.dto;

import java.util.List;

public class AdmissionRequest {

    private String nom;
    private String prenom;
    private String telephone;
    private String email;

    private int collectiviteId;

    private List<ParrainDTO> parrains;

    private double fraisAdhesion;
    private double cotisationAnnuelle;


    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCollectiviteId() {
        return collectiviteId;
    }

    public void setCollectiviteId(int collectiviteId) {
        this.collectiviteId = collectiviteId;
    }

    public List<ParrainDTO> getParrains() {
        return parrains;
    }

    public void setParrains(List<ParrainDTO> parrains) {
        this.parrains = parrains;
    }

    public double getFraisAdhesion() {
        return fraisAdhesion;
    }

    public void setFraisAdhesion(double fraisAdhesion) {
        this.fraisAdhesion = fraisAdhesion;
    }

    public double getCotisationAnnuelle() {
        return cotisationAnnuelle;
    }

    public void setCotisationAnnuelle(double cotisationAnnuelle) {
        this.cotisationAnnuelle = cotisationAnnuelle;
    }
}