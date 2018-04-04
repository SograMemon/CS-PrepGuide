package ca.dal.cs.web.cs_prepguide;

import java.util.ArrayList;

/**
 * Created by vamshikrishnamoogala on 2018-04-01.
 */

public class Post {
    private String postContent;
    private String postId;
    private String postLink;
    private String postName;

    public ArrayList<Comment> getPostComments() {
        return postComments;
    }

    public void setPostComments(ArrayList<Comment> postComments) {
        this.postComments = postComments;
    }

    private ArrayList<Comment> postComments = new ArrayList<>();

    public Post(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Post(String postContent) {
        this.postContent = postContent;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostContent() {
        return postContent;
    }
    public String getPostLink() {
        return postLink;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }
    public void setPostLink(String postLink) {
        this.postLink = postLink;
    }

    public ArrayList<Comment> getComments() {
        return postComments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.postComments = comments;
    }

    public void addComment(Comment comment){
        this.postComments.add(comment);
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    @Override
    public String toString() {
        return "Post{" +
                "postContent='" + postContent + '\'' +
                ", postId='" + postId + '\'' +
                ", comments=" + postComments +
                '}';
    }
}
