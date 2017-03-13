package com.tashariko.filereader;

import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by tashariko on 07/03/17.
 */

public class ListModel implements Serializable, Comparable<ListModel>{

    public boolean show=false;
    public String listTitle="";
    public String word="";
    public Integer cnt=0;

    public Integer getCnt() {
        return cnt;
    }

    @Override
    public int compareTo(@NonNull ListModel model) {
        return getCnt().compareTo(model.getCnt());
    }
}
