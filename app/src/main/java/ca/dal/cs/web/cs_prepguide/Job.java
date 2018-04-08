package ca.dal.cs.web.cs_prepguide;

import android.view.LayoutInflater;

/**
 * Created by SM on 2/28/18.
 */
/**
 * Job Model for storing details about the Job
 */

public class Job {

    /**
     * Job Details
     */
    String jobId;
    String jobTitle;
    String jobStream;
    String jobCompany;
    String jobType;
    String jobSkills;
    String jobFilter;
    String matchSkillNo=" ";
    Double jobLatitude;
    Double jobLongitude;
    String jobPostId;
    Float distance;

    /**
     * Empty Constructor
     */

    public Job(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        // to retrieve details from Firebase
    }

    /**
     * Constructor
     */
    public Job(String jobId,String jobTitle, String jobStream, String jobCompany, String jobType, String jobSkills){
        this.jobCompany= jobCompany;
        this.jobTitle= jobTitle;
        this.jobStream= jobStream;
        this.jobType= jobType;
        this.jobId= jobId;
        this.jobSkills=jobSkills;
    }

    public String getJobType() {
        return jobType;
    }

    public String getJobId() {
        return jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getJobStream() {
        return jobStream;
    }

    public String getJobCompany() {
        return jobCompany;
    }
    public String getJobSkills() {
        return jobSkills;
    }

    /**
     * Method to set the number of skills in the job that match with user's skill
     */
    public void setMatchSkillNo(String matchSkillNo){
        this.matchSkillNo=matchSkillNo;
    }
    /**
     * Method returns no of matched skills
     */
    public String getMatchSkill(){return matchSkillNo;}

    /**
     * Getters and Setters
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setJobStream(String jobStream) {
        this.jobStream = jobStream;
    }

    public void setJobCompany(String jobCompany) {
        this.jobCompany = jobCompany;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public Double getJobLatitude() {
        return jobLatitude;
    }

    public void setJobLatitude(Double jobLatitude) {
        this.jobLatitude = jobLatitude;
    }

    public Double getJobLongitude() {
        return jobLongitude;
    }

    public void setJobLongitude(Double jobLongitude) {
        this.jobLongitude = jobLongitude;
    }

    public Float getDistance() {
        return distance;
    }

    public String getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(String jobPostId) {
        this.jobPostId = jobPostId;
    }

    /**
     * Method to compute distance
     */
    public void setDistance(Float distance) {
        this.distance = distance;
    }


    /**
     * Method to set the filter being used and accordingly decide which parameter no of matched skills or distance to compute
     */
    public void setFilter(String filterVal){
        if(filterVal.equalsIgnoreCase("location")){
            this.jobFilter="Distance from current location: "+this.distance+" km";
        }else if(filterVal.equalsIgnoreCase("skills")){
            this.jobFilter="No. of matched skills: "+this.matchSkillNo;
        }else{
            this.jobFilter=" ";
        }
    }
    public String getJobFilter(){
        return this.jobFilter;
    }

    /**
     * toString Method
     */
    @Override
    public String toString() {
        return "job{" +
                "jobId='" + jobId + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", jobStream='" + jobStream + '\'' +
                ", jobCompany='" + jobCompany + '\'' +
                ", jobType='" + jobType + '\'' +
                ", jobLatitude=" + jobLatitude +
                ", jobLongitude=" + jobLongitude +
                ", distance=" + distance +
                '}';
    }
}
