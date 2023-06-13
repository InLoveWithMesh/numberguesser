package com.example.numberguesser2;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


@Getter
@Data
@Entity
@Table(name = "game")
public class game
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    private int randomNumber;
    private int upperborder = 1000;
    private int lowerborder = 0;
    private boolean hit = false;

    @Column(length = 1000)
    private List<String> requestlog = new ArrayList<>();
    //private byte[] requestlog;


    //@ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @ManyToOne
    @JoinColumn(name="person_id", nullable=false)
    @JsonIgnore
    public person gameOwner;

    public game() {}

    public game(person pGameOwner)
    {
        randomNumber = (int) (Math.random() * ((1000) + 1));
        this.gameOwner = pGameOwner;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId, randomNumber, upperborder, lowerborder, hit, requestlog);
    }

    public void updateRequestlog(int newRequestlog){
        requestlog.add(Integer.toString(newRequestlog));
    }

    public int getRequestnumber (){
        if (requestlog == null){
            return 0;
        }
        return requestlog.size();
    }

    public String getRequestlog2() {
        StringBuilder rlog = new StringBuilder();
        if (getRequestnumber() == 0){
            return rlog.toString();
        }
        for (int i=0;  i<getRequestnumber(); i++){
            rlog.append(i + 1).append(". Guessed Number: ").append(requestlog.get(i)).append("\n");
        }
        return rlog.toString();
    }

}