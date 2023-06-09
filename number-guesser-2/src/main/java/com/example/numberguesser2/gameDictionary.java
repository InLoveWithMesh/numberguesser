package com.example.numberguesser2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import java.util.HashMap;
import java.util.Map;
public class gameDictionary
{
    public Map<String, game> dictionary;

    public gameDictionary() {dictionary = new HashMap<>();}

    public void addGame(String key, game pGame) {dictionary.put(key, pGame);}

    public game getGame(String key) {return dictionary.get(key);}

    public void removeGame(String key) {dictionary.remove(key);}
    public boolean containsGame(String key) {return dictionary.containsKey(key);}
    public int size() {return dictionary.size();}
}
