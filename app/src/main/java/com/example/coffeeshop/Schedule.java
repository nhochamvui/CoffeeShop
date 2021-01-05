package com.example.coffeeshop;

import java.io.Serializable;
import java.util.ArrayList;

public class Schedule implements Serializable {
    public final static int SCHEDULE_OPEN = 1;
    public final static int SCHEDULE_CLOSE = 0;
    private String _id;
    private String date;
    private String time;
    private String action;
    private Sewer sewer;
    public Schedule(){

    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAction() {
        switch (Integer.parseInt(this.action)){
            case SCHEDULE_OPEN:
                return "Open";
            case SCHEDULE_CLOSE:
                return "CLose";
            default: break;
        }
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Sewer getSewer() {
        return sewer;
    }

    public void setSewer(Sewer sewer) {
        this.sewer = sewer;
    }
}
