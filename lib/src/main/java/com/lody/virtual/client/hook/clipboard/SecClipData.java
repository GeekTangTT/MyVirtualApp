package com.lody.virtual.client.hook.clipboard;

import java.io.Serializable;

public class SecClipData implements Serializable{
    public String clipData="";

    public void ClipString(){}

    public void setClipData(String clipData){
        this.clipData=clipData;

    }

    public String getClipData(){
        return clipData;
    }

}
