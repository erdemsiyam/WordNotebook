package com.erdemsiyam.memorizeyourwords.util;

public enum WordSortType {
    MostCorrectlySelected("En çok doğru seçilenler",0),MostIncorrectlySelected("En çok yanlış seçilenler",1),
    StrangeAZ("Yabancı A-Z",2),StrangeZA("Yabancı Z-A",3),
    ExplainAZ("Ana Dil A-Z",4),ExplainZA("Ana Dil Z-A",5);
    public String key;
    public int value;
    WordSortType(String key, int value){this.key = key;this.value = value;}
    public static String[] getKeysAsStringArray(){
        String[] values = new String[ExamWordType.values().length];
        for(int i = 0; i < ExamWordType.values().length; i++){
            values[i] = ExamWordType.values()[i].key;
        }
        return values;
    }
    public static WordSortType getTypeByValue(int val){
        for (WordSortType type : values()) {
            if (type.value == val)
                return type;
        }
        return null;
    }
}
