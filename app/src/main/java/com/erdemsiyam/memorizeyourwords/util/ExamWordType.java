package com.erdemsiyam.memorizeyourwords.util;

public enum ExamWordType {
    All("All",0),Learned("Learned",1),Marked("Marked",2),NotLearned("Not Learned",3);
    public String key;
    public int value;
    ExamWordType(String key, int value){this.key = key;this.value = value;}
    public static String[] getKeysAsStringArray(){
        String[] values = new String[ExamWordType.values().length];
        for(int i = 0; i < ExamWordType.values().length; i++){
            values[i] = ExamWordType.values()[i].key;
        }
        return values;
    }
    public static ExamWordType getTypeByValue(int val){
        for (ExamWordType type : values()) {
            if (type.value == val)
                return type;
        }
        return null;
    }
}
