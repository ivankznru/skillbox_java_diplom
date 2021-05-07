package com.gh4biz.devpub.model;

public enum ModerationStatus {
    NEW,
    ACCEPTED,
    DECLINED;

    public static ModerationStatus getByStatus(String status) {
        if (status.equals("pending")) {
            return ModerationStatus.NEW;
        }
        if (status.equals("declined")) {
            return ModerationStatus.DECLINED;
        }
        if (status.equals("published")) {
            return ModerationStatus.ACCEPTED;
        }
        return ModerationStatus.NEW;
    }
}
