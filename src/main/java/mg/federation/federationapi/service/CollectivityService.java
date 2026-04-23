package mg.federation.federationapi.service;

import mg.federation.federationapi.dto.Collectivity;
import mg.federation.federationapi.dto.CollectivityIdentity;
import mg.federation.federationapi.dto.CreateCollectivity;
import mg.federation.federationapi.dto.FinancialAccount;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CollectivityService {

    private Map<String, Collectivity> collectivities = new HashMap<>();
    private Map<String, List<FinancialAccount>> accounts = new HashMap<>();

    private int counter = 1;

    public Collectivity getById(String id) {

        Collectivity c = collectivities.get(id);

        if (c == null) {
            throw new RuntimeException("Collectivity not found");
        }

        return c;
    }


    public List<FinancialAccount> getAccounts(String id) {

        if (!collectivities.containsKey(id)) {
            throw new RuntimeException("Collectivity not found");
        }

        return accounts.getOrDefault(id, new ArrayList<>());
    }


    public List<Collectivity> create(List<CreateCollectivity> requests) {

        List<Collectivity> result = new ArrayList<>();

        for (CreateCollectivity r : requests) {

            if (!r.isFederationApproval()) {
                throw new RuntimeException("Federation approval required");
            }

            if (r.getStructure() == null) {
                throw new RuntimeException("Structure required");
            }

            Collectivity c = new Collectivity();

            String id = "C" + counter++;
            c.setId(id);
            c.setLocation(r.getLocation());

            List<FinancialAccount> defaultAccounts = new ArrayList<>();

            FinancialAccount cash = new FinancialAccount();
            cash.setId("A" + id + "-1");
            cash.setType("CASH");
            cash.setAmount(0);

            defaultAccounts.add(cash);

            accounts.put(id, defaultAccounts);

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


        if (req == null || req.getName() == null || req.getNumber() == null) {
            throw new RuntimeException("Name and number required");
        }


        if (c.getName() != null || c.getNumber() != null) {
            throw new RuntimeException("Identity already assigned");
        }

        for (Collectivity existing : collectivities.values()) {

            if (existing.getName() != null &&
                    existing.getName().equals(req.getName())) {
                throw new RuntimeException("Name already exists");
            }

            if (existing.getNumber() != null &&
                    existing.getNumber().equals(req.getNumber())) {
                throw new RuntimeException("Number already exists");
            }
        }

        c.setName(req.getName());
        c.setNumber(req.getNumber());

        return c;
    }
}