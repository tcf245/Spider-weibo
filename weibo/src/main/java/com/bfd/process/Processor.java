package com.bfd.process;

import com.bfd.Tasks;
import com.google.gson.Gson;

/**
 * Created by tcf24 on 2016/10/21.
 */
public abstract class Processor {
    private Tasks.Type type;
    public Gson gson = new Gson();
    public abstract void process(Tasks task) throws InterruptedException;


    public Tasks.Type getType() {
        return type;
    }

    public void setType(Tasks.Type type) {
        this.type = type;
    }
}
