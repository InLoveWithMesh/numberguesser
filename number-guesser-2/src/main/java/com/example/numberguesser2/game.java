package com.example.numberguesser2;
import lombok.Data;
import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
@Getter
@Data
public class game
{
    private int generateRandomNumber(int min, int max){
        return (int)(Math.random()*((max-min)+1))+min;
    }
    private int randomNumber;
    private int upperborder = 1000;
    private int lowerborder = 0;
    private String requestlog = "";
    private int requestnumber = 0;
    public User gameOwner;

    public game(User pGameOwner)
    {
        randomNumber = generateRandomNumber(0,1000);
        this.gameOwner = pGameOwner;
    }

    public void updateRequestnumber(){requestnumber++;}
    public void updateRequestlog(String newRequestlog){requestlog = requestlog + newRequestlog;}

}
