package ca.dal.cs.web.cs_prepguide;

import android.view.LayoutInflater;

/**
 * Created by SM on 2/28/18.
 */

public class job {

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
    Float distance;

    public job(){

    }
    public job(String jobId,String jobTitle, String jobStream, String jobCompany, String jobType, String jobSkills){
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
    public void setMatchSkillNo(String matchSkillNo){
        this.matchSkillNo=matchSkillNo;
    }
    public String getMatchSkill(){return matchSkillNo;}

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

    public void setDistance(Float distance) {
        this.distance = distance;
    }

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
