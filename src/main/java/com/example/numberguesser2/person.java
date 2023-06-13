package com.example.numberguesser2;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import java.util.Objects;
import java.util.*;

@Getter
@Data
@Entity
@Table(name = "person")
public class person {

    @Column(name = "name")
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy="gameOwner",cascade = CascadeType.ALL)
    @Column(name = "Games")
    private List<game> games = new ArrayList<>();

    //@Column(name = "CurrentGame")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "current_game_id")
    private game currentGame;



    public person() {}

    public person(String pName){
        this.name = pName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, games);
    }

    public void addGame(game pGame){
        games.add(pGame);
        this.currentGame = pGame;
    }

    public boolean hasRunningGame(){
        if(currentGame == null || currentGame.isHit()){
            return false;
        }
        return true;
    }
}