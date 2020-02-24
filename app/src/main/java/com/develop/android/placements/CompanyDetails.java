package com.develop.android.placements;

import java.util.HashMap;

public class CompanyDetails {
    String companyName;
    String filter;
    HashMap<String,String> jd,ec;
    public CompanyDetails(String companyName, String filter, HashMap<String, String> jd, HashMap<String, String> ec) {
        this.companyName = companyName;
        this.filter = filter;
        this.jd = jd;
        this.ec = ec;
    }

    public CompanyDetails() {
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public HashMap<String, String> getJd() {
        return jd;
    }

    public void setJd(HashMap<String, String> jd) {
        this.jd = jd;
    }

    public HashMap<String, String> getEc() {
        return ec;
    }

    public void setEc(HashMap<String, String> ec) {
        this.ec = ec;
    }

}
