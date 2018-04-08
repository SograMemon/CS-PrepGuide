package ca.dal.cs.web.cs_prepguide;

import java.util.ArrayList;

/**
 * Created by vamshikrishnamoogala on 2018-03-21.
 */


/**
 * User Model for storing details about the user
 */
public class User {

    /**
     * User Details
     */
    private String name;
    private String id;
    private String email;
    private String imageUrl;
    private ArrayList<String> skills;
    private ArrayList<String> bookmarks;

    /**
     * Empty Constructor
     */
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        // to retrieve details from Firebase
    }


    /**
     * Constructor
     */
    public User(String name, String id, String email, String imageUrl, ArrayList<String> skills, ArrayList<String> bookmarks) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.imageUrl = imageUrl;
        this.skills = skills;
        this.bookmarks = bookmarks;
    }

    /**
     * Getters and Setters
     */
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


    /**
     * Method to add a skill to the user skills
     */
    public void addSkilltoUser(String skill) {
        this.skills.add(skill);
    }

    /**
     * Method to add a bookmark to the user bookmarks
     */
    public void addBookmarksToUser(String postId) {
        this.bookmarks.add(postId);
    }

    /**
     * toString Method
     */
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
