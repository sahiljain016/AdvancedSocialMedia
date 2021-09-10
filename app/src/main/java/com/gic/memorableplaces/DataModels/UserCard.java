package com.gic.memorableplaces.DataModels;

import java.util.HashMap;

public class UserCard {

    private String age;
    private String birth_date;
    private String class_representative;
    private String college_year;
    private String gender;
    private String pronoun_preferred;
    private String zodiac_sign;


    public UserCard()  {

    }

    public UserCard(String age, String birth_date, String class_representative, String college_year, String gender,
                    String pronoun_preferred, String zodiac_sign) {
        this.age = age;
        this.birth_date = birth_date;
        this.class_representative = class_representative;
        this.college_year = college_year;
        this.gender = gender;
        this.pronoun_preferred = pronoun_preferred;
        this.zodiac_sign = zodiac_sign;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getClass_representative() {
        return class_representative;
    }

    public void setClass_representative(String class_representative) {
        this.class_representative = class_representative;
    }

    public String getCollege_year() {
        return college_year;
    }

    public void setCollege_year(String college_year) {
        this.college_year = college_year;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPronoun_preferred() {
        return pronoun_preferred;
    }

    public void setPronoun_preferred(String pronoun_preferred) {
        this.pronoun_preferred = pronoun_preferred;
    }

    public String getZodiac_sign() {
        return zodiac_sign;
    }

    public void setZodiac_sign(String zodiac_sign) {
        this.zodiac_sign = zodiac_sign;
    }

    @Override
    public String toString() {
        return "UserCard{" +
                "age='" + age + '\'' +
                ", birth_date='" + birth_date + '\'' +
                ", class_representative='" + class_representative + '\'' +
                ", college_year='" + college_year + '\'' +
                ", gender='" + gender + '\'' +
                ", pronoun_preferred='" + pronoun_preferred + '\'' +
                ", zodiac_sign='" + zodiac_sign + '\'' +
                '}';
    }
}
