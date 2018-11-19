package com.example.xkfeng.mycat.Model;

import java.io.Serializable;

public class Gradle implements Serializable {
    private String version;
    private String buildTime;
    private boolean current;
    private boolean snapshot;
    private boolean nightly;
    private boolean releaseNightly;
    private boolean activeRc;
    private String rcFor;
    private String milestoneFor;
    private boolean broken;
    private String downloadUrl;
    private String checksumUrl;

    public String getVersion() {
        return version;
    }

    public String getBuildTime() {
        return buildTime;
    }

    public boolean isCurrent() {
        return current;
    }

    public boolean isSnapshot() {
        return snapshot;
    }

    public boolean isNightly() {
        return nightly;
    }

    public boolean isReleaseNightly() {
        return releaseNightly;
    }

    public boolean isActiveRc() {
        return activeRc;
    }

    public String getRcFor() {
        return rcFor;
    }

    public String getMilestoneFor() {
        return milestoneFor;
    }

    public boolean isBroken() {
        return broken;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getChecksumUrl() {
        return checksumUrl;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setBuildTime(String buildTime) {
        this.buildTime = buildTime;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public void setSnapshot(boolean snapshot) {
        this.snapshot = snapshot;
    }

    public void setNightly(boolean nightly) {
        this.nightly = nightly;
    }

    public void setReleaseNightly(boolean releaseNightly) {
        this.releaseNightly = releaseNightly;
    }

    public void setActiveRc(boolean activeRc) {
        this.activeRc = activeRc;
    }

    public void setRcFor(String rcFor) {
        this.rcFor = rcFor;
    }

    public void setMilestoneFor(String milestoneFor) {
        this.milestoneFor = milestoneFor;
    }

    public void setBroken(boolean broken) {
        this.broken = broken;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public void setChecksumUrl(String checksumUrl) {
        this.checksumUrl = checksumUrl;
    }
}
