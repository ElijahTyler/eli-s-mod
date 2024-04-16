package net.devuser.elismod.block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.max;

public class LangtonTopLevelRules {
    public class MiddleLevelRules {
        private int key;
        private HashMap<String, Integer> rules;

        public MiddleLevelRules(int key) {
            this.key = key;
        }

        public void addRule(String neighbors, int cellState) {
            rules.put(neighbors, cellState);
        }

        public int reference(String neighbors) {
            return rules.getOrDefault(neighbors, 0);
        }
    }



    private HashMap<Integer, MiddleLevelRules> topLevelRules;

    public LangtonTopLevelRules() {
        topLevelRules = new HashMap<>();
    }

    public void addRuleSet(Integer key, MiddleLevelRules awesomewarrior) {
        topLevelRules.put(key, awesomewarrior);
    }

    public MiddleLevelRules returnRuleSet(Integer key) {
        return topLevelRules.get(key);
    }

    public int reference(Integer key, String neighbors) {
        // only passing in one string for neighbors, parsing is done later

        if (neighbors.length() != 4) {
            throw new IllegalArgumentException("block must have 4 neighbors");
        }

        // have to try all four permutations of neighbors
        Set<String> neighborSet = new HashSet<>();
        String doubled = neighbors + neighbors;
        for (int i = 0; i < 4; i++) {
            neighborSet.add(doubled.substring(i, i + 4));
        }

        MiddleLevelRules rulesToCheck = returnRuleSet(key);

        int cellState = 0;

        for (String neighborsInSet : neighborSet) {
            cellState = max(cellState, rulesToCheck.reference(neighborsInSet));
        }

        return cellState;
    }
}