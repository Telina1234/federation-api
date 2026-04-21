package mg.federation.federationapi.dto;

import java.util.List;

public class AdmissionRequest {
    public Long collectiviteId;
    public String nom;
    public String prenom;
    public double fraisAdhesion;
    public double cotisationAnnuelle;
    public List<ParrainDTO> parrains;
}