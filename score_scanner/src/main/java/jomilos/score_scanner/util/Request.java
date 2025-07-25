package jomilos.score_scanner.util;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Request {
    public String url;
    public String university;
    public String specialization = "";
    public String userid = "";

    @JsonIgnore
    public University universityType;
}
