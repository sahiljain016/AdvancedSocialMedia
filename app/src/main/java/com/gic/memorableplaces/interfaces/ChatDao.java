package com.gic.memorableplaces.interfaces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.gic.memorableplaces.DataModels.Chat;

import java.util.List;

@Dao
public
interface ChatDao {
    String sChatUID = "";

    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void InsertNewChat(Chat chat);

    @Query("SELECT * FROM chats WHERE chat_uid = (:ChatUID) ORDER BY date_messaged DESC LIMIT 20")
    List<Chat> Get20LatestChats(String ChatUID);

    @Query("DELETE FROM chats WHERE chat_uid = :ChatUID")
    void DeleteChatTable(String ChatUID);

    @Query("SELECT * FROM chats")
    List<Chat> GetAllChats();

    @Query("DELETE FROM chats WHERE chat_uid = :ChatUID AND chat_id = :ChatID")
    void DeleteSingleEntry(String ChatUID, String ChatID);

    @Query("UPDATE chats SET anchor_id = :anchorID WHERE chat_uid =:chatUID AND chat_id = :MessageID")
    void updateAnchor(String anchorID, String chatUID, String MessageID);

    @Query("SELECT * FROM chats WHERE chat_uid = :chatUID AND anchor_id IS NOT NULL AND anchor_id != \"\"")
    List<Chat> GetAnchors( String chatUID);

    @Query("SELECT * FROM chats WHERE chat_uid = (:chatUID) AND date_messaged > (:LastMessageEpoch) ORDER BY date_messaged LIMIT 10")
    List<Chat> GetNext10Chats(String chatUID, long LastMessageEpoch);

    @Query("SELECT * FROM chats WHERE chat_uid = (:ChatUID) AND date_messaged < (:LastMessageEpoch) ORDER BY date_messaged DESC LIMIT 10")
    List<Chat> Load10MoreChats(String ChatUID, long LastMessageEpoch);

}
