package com.erdemsiyam.memorizeyourwords.util;

public class ConfuseTempModel implements Comparable<ConfuseTempModel>{
    public long strangeId;
    public int times;
    public ConfuseTempModel(long strangeId, int times){this.strangeId=strangeId;this.times=times;}
    @Override
    public int compareTo(ConfuseTempModel o) {
        return (this.times>o.times)?-1:(this.times<o.times)?1:0;
    }
}