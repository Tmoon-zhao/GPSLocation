package com.example.gpslocation;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ListIntegerConverter {

    Gson gson = new Gson();

    @TypeConverter
    public List<List<Integer>> stringToSomeObjectsList(String data){
        if(data == null){
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<List<Integer>>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public String someObjectListToString(List<List<Integer>> someObjects){
        return gson.toJson(someObjects);
    }
}
