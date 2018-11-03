package com.example.sayyad.exotest.Player.Model;

import java.util.ArrayList;
import java.util.List;

public class ClipList {

    private List<Clip> clips;

    public ClipList() {
        clips = new ArrayList<>();
    }

    public List<Clip> getClips() {
        return clips;
    }

    public void setClips(List<Clip> clips) {
        this.clips = clips;
    }

    public void reversTime(long duration) {
        long prev;
        List<Clip> newClips = new ArrayList<>();
        newClips.add(new Clip("R",0,clips.get(0).getStartTime()));
        prev = clips.get(0).getStopTime();

        for (int i = 1; i < clips.size() ; i++) {
            newClips.add(new Clip("R",prev,clips.get(i).getStartTime()));
            prev = clips.get(i).getStopTime();
        }

        newClips.add(new Clip("R",prev,duration));
        setClips(newClips);
    }

    @Override
    public String toString() {
        return "ClipList{" +
                "clips=" + clips +
                '}';
    }
}
