// Created by Eric B. 09.03.2022 17:50
package de.ericzones.bytesystem.collectives.bytefeature.moderation;

import de.ericzones.bytesystem.ByteSystem;
import de.ericzones.bytesystem.collectives.bytefeature.Feature;
import de.ericzones.bytesystem.collectives.bytefeature.FeatureType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ModerationFeature extends Feature {

    private boolean nameCheck, switchCheck;
    private int switchComplainsLimit;
    private long switchDelay;
    private List<Integer> ignoredGroups;
    private List<String> forbiddenNicknames;

    public ModerationFeature(int botId, boolean enabled, boolean nameCheck, boolean switchCheck, int switchComplainsLimit, long switchDelay, List<Integer> ignoredGroups, List<Object> forbiddenNicknames) {
        super(botId, FeatureType.MODERATION, enabled);
        this.nameCheck = nameCheck;
        this.switchCheck = switchCheck;
        this.switchComplainsLimit = switchComplainsLimit;
        this.switchDelay = switchDelay;
        this.ignoredGroups = ignoredGroups;
        this.forbiddenNicknames = forbiddenNicknames.stream().map(object -> Objects.toString(object, null)).collect(Collectors.toList());
    }

    public boolean isNameCheck() {
        return nameCheck;
    }

    public void setNameCheck(boolean nameCheck) {
        this.nameCheck = nameCheck;
        ByteSystem.getByteFeatureManager().updateModeration(ModerationSetting.NAMECHECK, nameCheck);
    }

    public boolean isSwitchCheck() {
        return switchCheck;
    }

    public void setSwitchCheck(boolean switchCheck) {
        this.switchCheck = switchCheck;
        ByteSystem.getByteFeatureManager().updateModeration(ModerationSetting.SWITCHCHECK, switchCheck);
    }

    public int getSwitchComplainsLimit() {
        return switchComplainsLimit;
    }

    public void setSwitchComplainsLimit(int switchComplainsLimit) {
        this.switchComplainsLimit = switchComplainsLimit;
        ByteSystem.getByteFeatureManager().updateModeration(ModerationSetting.SWITCH_COMPLAINS_LIMIT, switchComplainsLimit);
    }

    public long getSwitchDelay() {
        return switchDelay;
    }

    public void setSwitchDelay(long switchDelay) {
        this.switchDelay = switchDelay;
        ByteSystem.getByteFeatureManager().updateModeration(ModerationSetting.SWITCH_DELAY, switchDelay);
    }

    public List<Integer> getIgnoredGroups() {
        return ignoredGroups;
    }

    public void addIgnoredGroup(int groupId) {
        this.ignoredGroups.add(groupId);
        ByteSystem.getByteFeatureManager().addFeatureIgnoreGroup(FeatureType.MODERATION, groupId);
    }

    public void removeIgnoredGroup(int groupId) {
        this.ignoredGroups.remove((Object)groupId);
        ByteSystem.getByteFeatureManager().removeFeatureIgnoreGroup(FeatureType.MODERATION, groupId);
    }

    public List<String> getForbiddenNicknames() {
        return forbiddenNicknames;
    }

    public void addForbiddenNickname(String nickname) {
        this.forbiddenNicknames.add(nickname);
        ByteSystem.getByteFeatureManager().addModerationFeatureContent(ModerationSetting.FORBIDDEN_NICKNAME, nickname);
    }

    public void removeForbiddenNickname(String nickname) {
        this.forbiddenNicknames.remove(nickname);
        ByteSystem.getByteFeatureManager().removeModerationFeatureContent(ModerationSetting.FORBIDDEN_NICKNAME, nickname);
    }

    public boolean isConfigured() {
        if(switchComplainsLimit == -1 || switchDelay == -1)
            return false;
        return true;
    }

}
