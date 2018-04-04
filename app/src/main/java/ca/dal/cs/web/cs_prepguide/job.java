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
    String matchSkillNo=" ";

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


}
