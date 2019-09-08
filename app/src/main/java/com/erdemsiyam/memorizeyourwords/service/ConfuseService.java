package com.erdemsiyam.memorizeyourwords.service;

import android.content.Context;

import com.erdemsiyam.memorizeyourwords.database.MyDatabase;
import com.erdemsiyam.memorizeyourwords.entity.Confuse;

import java.util.List;

public final class ConfuseService {
    public static void addConfuse(Context context, Long id, Long targetId){
        Confuse confuse = MyDatabase.getMyDatabase(context).getConfuseDAO().getConfuse(id,targetId);
        if(confuse != null){
            MyDatabase.getMyDatabase(context).getConfuseDAO().increaseTimes(confuse.getId());
        }else{
            Confuse confuseNew = new Confuse();
            confuseNew.setWordId(id);
            confuseNew.setWrongWordId(targetId);
            confuseNew.setTimes(1);
            MyDatabase.getMyDatabase(context).getConfuseDAO().insertConfuse(confuseNew);
        }
    }
    public static List<Confuse> getAllConfuse(Context context){
        return MyDatabase.getMyDatabase(context).getConfuseDAO().getAllConfuse();
    }
    public static List<Confuse> getConfusesWordsByFirstId(Context context, Long wordId){
        return MyDatabase.getMyDatabase(context).getConfuseDAO().getConfusesWordsByFirstId(wordId);
    }
    public static List<Confuse> getConfusesWordsBySecondId(Context context, Long wordId){
        return MyDatabase.getMyDatabase(context).getConfuseDAO().getConfusesWordsBySecondId(wordId);
    }
    public static void deleteAllConfuse(Context context){
        MyDatabase.getMyDatabase(context).getConfuseDAO().deleteAllConfuse();
    }
}
