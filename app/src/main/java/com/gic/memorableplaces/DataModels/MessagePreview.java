package com.gic.memorableplaces.DataModels;

import java.util.Map;

public class MessagePreview {

    private String latest_message;
    private String latest_message_id;
    private String chat_uid;
    private String latest_sender_id;
    private String sender_public_key;
    private String other_user_uid;
    private String other_pp_url;
    private String string_date;
    private String other_display_name;
    private String isSeen;
    private String is_message_pinned;
    private Boolean isOnline;
    private long pending_unseen_messages;
    private Map<String, String> latest_date_messaged;

    public MessagePreview() {

    }

    public MessagePreview(String latest_message, long latest_epoch_gmt_milliseconds_long, String latest_message_id,
                          String chat_uid, String latest_sender_id, String sender_public_key, String other_user_uid,
                          String other_pp_url, String other_display_name, String isSeen, String is_message_pinned,
                          boolean isOnline, long pending_unseen_messages, Map<String, String> latest_date_messaged) {
        this.latest_message = latest_message;
        this.latest_message_id = latest_message_id;
        this.chat_uid = chat_uid;
        this.latest_sender_id = latest_sender_id;
        this.sender_public_key = sender_public_key;
        this.other_user_uid = other_user_uid;
        this.other_pp_url = other_pp_url;
        this.other_display_name = other_display_name;
        this.isSeen = isSeen;
        this.is_message_pinned = is_message_pinned;
        this.isOnline = isOnline;
        this.pending_unseen_messages = pending_unseen_messages;
        this.latest_date_messaged = latest_date_messaged;
    }

    public MessagePreview(String latest_message, String latest_message_id,
                          String latest_sender_id, String sender_public_key, String other_user_uid,
                          String isSeen, String is_message_pinned, String chat_uid, String string_date,
                          long pending_unseen_messages) {
        this.latest_message = latest_message;
        this.latest_message_id = latest_message_id;
        this.latest_sender_id = latest_sender_id;
        this.sender_public_key = sender_public_key;
        this.other_user_uid = other_user_uid;
        this.isSeen = isSeen;
        this.is_message_pinned = is_message_pinned;
        this.chat_uid = chat_uid;
        this.string_date = string_date;
        this.pending_unseen_messages = pending_unseen_messages;
    }

    public MessagePreview(String latest_message, String latest_message_id,
                          String latest_sender_id, String sender_public_key, String other_user_uid,
                          String isSeen, String is_message_pinned, String chat_uid, Map<String, String> latest_date_messaged,
                          long pending_unseen_messages) {
        this.latest_message = latest_message;
        this.latest_message_id = latest_message_id;
        this.latest_sender_id = latest_sender_id;
        this.sender_public_key = sender_public_key;
        this.other_user_uid = other_user_uid;
        this.isSeen = isSeen;
        this.is_message_pinned = is_message_pinned;
        this.chat_uid = chat_uid;
        this.latest_date_messaged = latest_date_messaged;
        this.pending_unseen_messages = pending_unseen_messages;
    }

    public MessagePreview(String latest_message, String latest_message_id,
                          String latest_sender_id, String sender_public_key, String other_user_uid,
                          String isSeen, String is_message_pinned, String chat_uid,

                          long pending_unseen_messages) {
        this.latest_message = latest_message;
        this.latest_message_id = latest_message_id;
        this.latest_sender_id = latest_sender_id;
        this.sender_public_key = sender_public_key;
        this.other_user_uid = other_user_uid;
        this.isSeen = isSeen;
        this.is_message_pinned = is_message_pinned;
        this.chat_uid = chat_uid;
        this.pending_unseen_messages = pending_unseen_messages;
    }

    public String getLatest_message() {
        return latest_message;
    }

    public void setLatest_message(String latest_message) {
        this.latest_message = latest_message;
    }

    public String getLatest_message_id() {
        return latest_message_id;
    }

    public void setLatest_message_id(String latest_message_id) {
        this.latest_message_id = latest_message_id;
    }

    public String getChat_uid() {
        return chat_uid;
    }

    public void setChat_uid(String chat_uid) {
        this.chat_uid = chat_uid;
    }

    public String getLatest_sender_id() {
        return latest_sender_id;
    }

    public void setLatest_sender_id(String latest_sender_id) {
        this.latest_sender_id = latest_sender_id;
    }

    public String getSender_public_key() {
        return sender_public_key;
    }

    public void setSender_public_key(String sender_public_key) {
        this.sender_public_key = sender_public_key;
    }

    public String getOther_user_uid() {
        return other_user_uid;
    }

    public void setOther_user_uid(String other_user_uid) {
        this.other_user_uid = other_user_uid;
    }

    public String getOther_pp_url() {
        return other_pp_url;
    }

    public void setOther_pp_url(String other_pp_url) {
        this.other_pp_url = other_pp_url;
    }

    public String getString_date() {
        return string_date;
    }

    public void setString_date(String string_date) {
        this.string_date = string_date;
    }

    public String getOther_display_name() {
        return other_display_name;
    }

    public void setOther_display_name(String other_display_name) {
        this.other_display_name = other_display_name;
    }

    public String getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(String isSeen) {
        this.isSeen = isSeen;
    }

    public String getIs_message_pinned() {
        return is_message_pinned;
    }

    public void setIs_message_pinned(String is_message_pinned) {
        this.is_message_pinned = is_message_pinned;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public long getPending_unseen_messages() {
        return pending_unseen_messages;
    }

    public void setPending_unseen_messages(long pending_unseen_messages) {
        this.pending_unseen_messages = pending_unseen_messages;
    }

    public Map<String, String> getLatest_date_messaged() {
        return latest_date_messaged;
    }

    public void setLatest_date_messaged(Map<String, String> latest_date_messaged) {
        this.latest_date_messaged = latest_date_messaged;
    }

    @Override
    public String toString() {
        return "MessagePreview{" +
                "latest_message='" + latest_message + '\'' +
                ", latest_message_id='" + latest_message_id + '\'' +
                ", chat_uid='" + chat_uid + '\'' +
                ", latest_sender_id='" + latest_sender_id + '\'' +
                ", sender_public_key='" + sender_public_key + '\'' +
                ", other_user_uid='" + other_user_uid + '\'' +
                ", other_pp_url='" + other_pp_url + '\'' +
                ", string_date='" + string_date + '\'' +
                ", other_display_name='" + other_display_name + '\'' +
                ", isSeen='" + isSeen + '\'' +
                ", is_message_pinned='" + is_message_pinned + '\'' +
                ", isOnline=" + isOnline +
                ", pending_unseen_messages=" + pending_unseen_messages +
                ", latest_date_messaged=" + latest_date_messaged +
                '}';
    }
}
