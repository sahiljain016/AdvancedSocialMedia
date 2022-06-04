package com.gic.memorableplaces.DataModels;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "filter_privacy_details")
public class FilterPrivacyDetails {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "dummy_pk")
    private Integer dummy_pk = 0;
    @ColumnInfo(name = "age_birthdate_p")
    private boolean ab_p;
    @ColumnInfo(name = "gender_pronouns_p")
    private boolean gp_p;
    @ColumnInfo(name = "general_details_p")
    private boolean gd_p;
    @ColumnInfo(name = "college_year_p")
    private boolean cy_p;
    @ColumnInfo(name = "society_p")
    private boolean sic_p;
    @ColumnInfo(name = "titles_p")
    private boolean tp_p;
    @ColumnInfo(name = "hobbies_p")
    private boolean h_p;
    @ColumnInfo(name = "video_games_p")
    private boolean vg_p;
    @ColumnInfo(name = "music_p")
    private boolean mu_p;
    @ColumnInfo(name = "books_p")
    private boolean bo_p;
    @ColumnInfo(name = "movie_p")
    private boolean mo_p;
    @ColumnInfo(name = "loc_p")
    private boolean dd_p;


    public FilterPrivacyDetails() {

    }


    public FilterPrivacyDetails(@NonNull Integer dummy_pk, boolean ab_p, boolean gp_p, boolean gd_p, boolean cy_p, boolean sic_p, boolean tp_p, boolean h_p, boolean vg_p, boolean mu_p, boolean bo_p, boolean mo_p, boolean dd_p) {
        this.dummy_pk = dummy_pk;
        this.ab_p = ab_p;
        this.gp_p = gp_p;
        this.gd_p = gd_p;
        this.cy_p = cy_p;
        this.sic_p = sic_p;
        this.tp_p = tp_p;
        this.h_p = h_p;
        this.vg_p = vg_p;
        this.mu_p = mu_p;
        this.bo_p = bo_p;
        this.mo_p = mo_p;
        this.dd_p = dd_p;
    }

    @NonNull
    public Integer getDummy_pk() {
        return dummy_pk;
    }

    public void setDummy_pk(@NonNull Integer dummy_pk) {
        this.dummy_pk = dummy_pk;
    }

    public boolean isAb_p() {
        return ab_p;
    }

    public void setAb_p(boolean ab_p) {
        this.ab_p = ab_p;
    }

    public boolean isGp_p() {
        return gp_p;
    }

    public void setGp_p(boolean gp_p) {
        this.gp_p = gp_p;
    }

    public boolean isGd_p() {
        return gd_p;
    }

    public void setGd_p(boolean gd_p) {
        this.gd_p = gd_p;
    }

    public boolean isCy_p() {
        return cy_p;
    }

    public void setCy_p(boolean cy_p) {
        this.cy_p = cy_p;
    }

    public boolean isSic_p() {
        return sic_p;
    }

    public void setSic_p(boolean sic_p) {
        this.sic_p = sic_p;
    }

    public boolean isTp_p() {
        return tp_p;
    }

    public void setTp_p(boolean tp_p) {
        this.tp_p = tp_p;
    }

    public boolean isH_p() {
        return h_p;
    }

    public void setH_p(boolean h_p) {
        this.h_p = h_p;
    }

    public boolean isVg_p() {
        return vg_p;
    }

    public void setVg_p(boolean vg_p) {
        this.vg_p = vg_p;
    }

    public boolean isMu_p() {
        return mu_p;
    }

    public void setMu_p(boolean mu_p) {
        this.mu_p = mu_p;
    }

    public boolean isBo_p() {
        return bo_p;
    }

    public void setBo_p(boolean bo_p) {
        this.bo_p = bo_p;
    }

    public boolean isMo_p() {
        return mo_p;
    }

    public void setMo_p(boolean mo_p) {
        this.mo_p = mo_p;
    }

    public boolean isDd_p() {
        return dd_p;
    }

    public void setDd_p(boolean dd_p) {
        this.dd_p = dd_p;
    }

    @Override
    public String toString() {
        return "FilterPrivacyDetails{" +
                "dummy_pk=" + dummy_pk +
                ", age_birthdate_p=" + ab_p +
                ", gender_pronouns_p=" + gp_p +
                ", general_details_p=" + gd_p +
                ", college_year_p=" + cy_p +
                ", society_p=" + sic_p +
                ", titles_p=" + tp_p +
                ", hobbies_p=" + h_p +
                ", video_games_p=" + vg_p +
                ", music_p=" + mu_p +
                ", books_p=" + bo_p +
                ", movie_p=" + mo_p +
                ", loc_p=" + dd_p +
                '}';
    }
}
