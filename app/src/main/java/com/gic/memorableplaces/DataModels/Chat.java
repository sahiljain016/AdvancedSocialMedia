package com.gic.memorableplaces.DataModels;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Map;

@Entity(tableName = "chats", indices = {@Index(value = {"anchor_id"}, unique = true)})
public class Chat {

    /*public static final String BASETABLE_NAME = "chats_";
    public static final String BASETABLE_NAME_PLACEHOLDER = ":tablename:";
    public static final String BASETABLE_CREATE_SQL = "CREATE TABLE IF NOT EXISTS "
            + BASETABLE_NAME_PLACEHOLDER +
            "(" +
             "chat_id INTEGER PRIMARY KEY," +
             "message TEXT,"+
             "sender TEXT,"+
             "is_gradient INTEGER NOT NULL,"+
             "receivers TEXT,"+
             "date_messaged INTEGER,"+
             "bubble_color TEXT)";*/

    @ColumnInfo(name = "message")
    private String message;
    @ColumnInfo(name = "sender")
    private String sender;
    @ColumnInfo(name = "is_gradient")
    private boolean isGradient;
    @ColumnInfo(name = "receivers")
    private String receivers;
    @Ignore
    private String string_date;
    @ColumnInfo(name = "date_messaged")
    private Long epoch;
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "chat_id")
    private String chat_id;

    @ColumnInfo(name = "anchor_id")
    private String anchor_id;

    @ColumnInfo(name = "chat_uid")
    private String chat_uid;
    @Ignore
    private String isSeen;
    @ColumnInfo(name = "bubble_color")
    private String BubbleColor;
    @Ignore
    private Map<String, String> date_messaged;


    public Chat() {

    }

    @Ignore
    public Chat(String message, String sender, boolean isGradient, String receivers,
                String string_date, @NonNull String chatid, String isSeen, String bubbleColor, Map<String, String> date_messaged) {
        this.message = message;
        this.sender = sender;
        this.isGradient = isGradient;
        this.receivers = receivers;
        this.string_date = string_date;
        chat_id = chatid;
        this.isSeen = isSeen;
        BubbleColor = bubbleColor;
        this.date_messaged = date_messaged;
    }

    @Ignore
    public Chat(String message, String sender, boolean isGradient, String receivers, @NonNull String chatid, String isSeen,
                String bubbleColor, Map<String, String> date_messaged) {
        this.message = message;
        this.sender = sender;
        this.isGradient = isGradient;
        this.receivers = receivers;
        chat_id = chatid;
        this.isSeen = isSeen;
        BubbleColor = bubbleColor;
        this.date_messaged = date_messaged;
    }

    public Chat(String message, String sender, boolean isGradient, String receivers, @NonNull String chatid, String ChatUID,
                String bubbleColor, Long epoch) {
        this.message = message;
        this.sender = sender;
        this.isGradient = isGradient;
        this.chat_uid = ChatUID;
        this.receivers = receivers;
        chat_id = chatid;
        BubbleColor = bubbleColor;
        this.epoch = epoch;
    }

    @Ignore
    public Chat(String message, String sender, boolean isGradient, String receivers, @NonNull String chatid, String isSeen,
                String bubbleColor) {
        this.message = message;
        this.sender = sender;
        this.isGradient = isGradient;
        this.receivers = receivers;
        chat_id = chatid;
        this.isSeen = isSeen;
        BubbleColor = bubbleColor;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public boolean isGradient() {
        return isGradient;
    }

    public void setGradient(boolean gradient) {
        isGradient = gradient;
    }

    public String getReceivers() {
        return receivers;
    }

    public void setReceivers(String receivers) {
        this.receivers = receivers;
    }

    @Ignore
    public String getString_date() {
        return string_date;
    }

    @Ignore
    public void setString_date(String string_date) {
        this.string_date = string_date;
    }

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    @Ignore
    public String getIsSeen() {
        return isSeen;
    }

    @Ignore
    public void setIsSeen(String isSeen) {
        this.isSeen = isSeen;
    }

    public String getChat_uid() {
        return chat_uid;
    }

    public void setChat_uid(String chat_uid) {
        this.chat_uid = chat_uid;
    }

    public String getBubbleColor() {
        return BubbleColor;
    }

    public void setBubbleColor(String bubbleColor) {
        BubbleColor = bubbleColor;
    }

    @Ignore
    public void setDate_messaged(Map<String, String> date_messaged) {
        this.date_messaged = date_messaged;
    }

    public String getAnchor_id() {
        return anchor_id;
    }

    public void setAnchor_id(String anchor_id) {
        this.anchor_id = anchor_id;
    }

    public Long getEpoch() {
        return epoch;
    }

    public void setEpoch(Long epoch) {
        this.epoch = epoch;
    }

    @Ignore
    public Map<String, String> getDate_messaged() {
        return date_messaged;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "message='" + message + '\'' +
                ", sender='" + sender + '\'' +
                ", isGradient=" + isGradient +
                ", receivers='" + receivers + '\'' +
                ", string_date='" + string_date + '\'' +
                ", epoch=" + epoch +
                ", chat_id='" + chat_id + '\'' +
                ", anchor_id='" + anchor_id + '\'' +
                ", chat_uid='" + chat_uid + '\'' +
                ", isSeen='" + isSeen + '\'' +
                ", BubbleColor='" + BubbleColor + '\'' +
                ", date_messaged=" + date_messaged +
                '}';
    }

    /* @Ignore
    public static Long insertRow(SupportSQLiteDatabase sdb, String tableName, Chat chat) {
        ContentValues cv = new ContentValues();
        cv.put("chat_id", chat.getChat_id());
        cv.put("message", chat.getMessage());
        cv.put("sender", chat.getSender());
        cv.put("is_gradient",  chat.isGradient());
        cv.put("receivers", chat.getReceivers());
        cv.put("date_messaged", chat.getEpoch());
        cv.put("bubble_color", chat.getBubbleColor());
        return sdb.insert(tableName, OnConflictStrategy.IGNORE, cv);
    }*/

}
