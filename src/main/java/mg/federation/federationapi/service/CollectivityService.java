package mg.federation.federationapi.service;

import mg.federation.federationapi.dto.Collectivity;
import mg.federation.federationapi.dto.CollectivityIdentity;
import mg.federation.federationapi.dto.CreateCollectivity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CollectivityService {

    private Map<String, Collectivity> collectivities = new HashMap<>();
    private int counter = 1;

    public List<Collectivity> create(List<CreateCollectivity> requests) {

        List<Collectivity> result = new ArrayList<>();

        for (CreateCollectivity r : requests) {

            if (!r.isFederationApproval()) {
                throw new RuntimeException("Federation approval required");
            }

            Collectivity c = new Collectivity();

            String id = "C" + counter++;
            c.setId(id);
            c.setLocation(r.getLocation());

            collectivities.put(id, c);
            result.add(c);
        }

        return result;
    }

    public Collectivity assignIdentity(String id, CollectivityIdentity req) {

        Collectivity c = collectivities.get(id);

        if (c == null) {
            throw new RuntimeException("Collectivity not found");
        }

        if (c.getName() != null && c.getNumber() != null) {
            throw new RuntimeException("Already assigned");
        }

        for (Collectivity existing : collectivities.values()) {

            if (existing.getName() != null &&
                    existing.getName().equals(req.getName())) {
                throw new RuntimeException("Name exists");
            }

            if (existing.getNumber() != null &&
                    existing.getNumber().equals(req.getNumber())) {
                throw new RuntimeException("Number exists");
            }
        }

        c.setName(req.getName());
        c.setNumber(req.getNumber());

        return c;
    }
}