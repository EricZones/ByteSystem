// Created by Eric B. 01.03.2022 19:46
package de.ericzones.bytesystem.collectives.bytefeature;

public enum FeatureType {

    CHANNEL("Channel"),
    SUPPORT("Support"),
    MODERATION("Moderation"),
    PUNISH("Punish");

    private FeatureType(String name){
        this.name = name;
    }

    private final String name;

    public String getName() {
        return name;
    }

    public static FeatureType getFeature(String name) {
        for(FeatureType current : FeatureType.values()) {
            if (current.getName().equalsIgnoreCase(name)) return current;
        }
        return null;
    }

}
