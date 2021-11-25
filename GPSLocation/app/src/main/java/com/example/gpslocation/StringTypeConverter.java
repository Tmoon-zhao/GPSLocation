package com.example.gpslocation;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class StringTypeConverter {

    Gson gson = new Gson();

    @TypeConverter
    public List<String> stringToSomeObjectsList(String data){
        if(data == null){
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<String>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public String someObjectListToString(List<String> someObjects){
        return gson.toJson(someObjects);
    }
}
