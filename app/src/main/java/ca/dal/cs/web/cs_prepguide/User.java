package ca.dal.cs.web.cs_prepguide;

import java.util.ArrayList;

/**
 * Created by vamshikrishnamoogala on 2018-03-21.
 */

public class User {

    private String name;
    private String id;
    private String email;
    private String imageUrl;
    private ArrayList<String> skills;
    private ArrayList<String> bookmarks;

    public User(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String id, String email, String imageUrl, ArrayList<String> skills, ArrayList<String> bookmarks) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.imageUrl = imageUrl;
        this.skills = skills;
        this.bookmarks = bookmarks;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ArrayList<String> getSkills() {
        return this.skills;
    }

    public void setSkills(ArrayList<String> skills) {
        this.skills = skills;
    }

    public ArrayList<String> getBookmarks() {
        return this.bookmarks;
    }

    public void setBookmarks(ArrayList<String> bookmarks) {
        this.bookmarks = bookmarks;
    }

    public void addSkilltoUser(String skill){
        this.skills.add(skill);
    }

//    public void removeSkillFromUser(String skill){
//        this.skills.remove(this.skills.indexOf(skill));
//        this.skills.add(skill);
//    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", skills=" + skills +
                ", bookmarks=" + bookmarks +
                '}';
    }
}
