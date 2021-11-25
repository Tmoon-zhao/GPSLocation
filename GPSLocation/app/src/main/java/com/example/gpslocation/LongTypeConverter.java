package com.example.gpslocation;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class LongTypeConverter {

    Gson gson = new Gson();

    @TypeConverter
    public List<Long> stringToSomeObjectsList(String data){
        if(data == null){
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Long>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public String someObjectListToString(List<Long> someObjects){
        return gson.toJson(someObjects);
    }
}
