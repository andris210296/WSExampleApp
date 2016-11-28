package com.wsexampleapp.wsexampleapp;

/**
 * Created by andris on 17/10/2016.
 */
 import java.text.SimpleDateFormat;
 import java.util.Date;

public class Informacao {

    private long keyInfo;
    private String stringInfo;
    private int intInfo;
    private Date dateInfo;
    private double doubleInfo;

    public Informacao(long keyInfo,String stringInfo, int intInfo, Date dateInfo, double doubleInfo) {
        this.setKeyInfo(keyInfo);
        this.stringInfo = stringInfo;
        this.intInfo = intInfo;
        this.dateInfo = dateInfo;
        this.doubleInfo = doubleInfo;

    }

    public Informacao() {

    }

    @Override
    public String toString() {
        // Retorno que facilita o ArrayAdapter
        String dataString = new SimpleDateFormat("dd/MM/yyyy").format(getDateInfo());

        return "Key: "+getKeyInfo()+" String: "+getStringInfo() +
                "\n Int: "+getIntInfo() +" Date: "+ dataString+" Double: "+getDoubleInfo();
    }

    public String getStringInfo() {
        return stringInfo;
    }

    public void setStringInfo(String stringInfo) {
        this.stringInfo = stringInfo;
    }

    public int getIntInfo() {
        return intInfo;
    }

    public void setIntInfo(int intInfo) {
        this.intInfo = intInfo;
    }

    public Date getDateInfo() {
        return dateInfo;
    }

    public void setDateInfo(Date dateInfo) {
        this.dateInfo = dateInfo;
    }

    public double getDoubleInfo() {
        return doubleInfo;
    }

    public void setDoubleInfo(double doubleInfo) {
        this.doubleInfo = doubleInfo;
    }

    public long getKeyInfo() {
        return keyInfo;
    }

    public void setKeyInfo(long keyInfo) {
        this.keyInfo = keyInfo;
    }

}

