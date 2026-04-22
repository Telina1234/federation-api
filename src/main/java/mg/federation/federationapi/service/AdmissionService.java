package mg.federation.federationapi.service;

import mg.federation.federationapi.dto.AdmissionRequest;
import mg.federation.federationapi.dto.ParrainDTO;
import org.springframework.stereotype.Service;

@Service
public class AdmissionService {

    public String admettre(AdmissionRequest request) {


        if (request.getParrains() == null || request.getParrains().size() < 2) {
            return "Refus: minimum 2 parrains requis";
        }
        int memeCollectivite = 0;
        int autres = 0;


        for (ParrainDTO p : request.getParrains()) {
            if (p.getCollectiviteId() == request.getCollectiviteId()) {
                memeCollectivite++;
            } else {
                autres++;
            }
        }
        if (memeCollectivite < autres) {
            return "Refus: parrains insuffisants de la même collectivité";
        }

        double total = request.getFraisAdhesion() + request.getCotisationAnnuelle();

        return "Admission validée. Total payé = " + total + " MGA";
    }
}