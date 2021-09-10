package com.gic.memorableplaces.DataModels;

public class AllUserSettings {

    private User user;
    private UserAccountSettings userAccountSettings;


    public AllUserSettings(User user, UserAccountSettings userAccountSettings) {
        this.user = user;
        this.userAccountSettings = userAccountSettings;
    }
    public AllUserSettings() {

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserAccountSettings getUserAccountSettings() {
        return userAccountSettings;
    }

    public void setUserAccountSettings(UserAccountSettings userAccountSettings) {
        this.userAccountSettings = userAccountSettings;
    }

    @Override
    public String toString() {
        return "AllUserSettings{" +
                "user=" + user +
                ", userAccountSettings=" + userAccountSettings +
                '}';
    }
}
