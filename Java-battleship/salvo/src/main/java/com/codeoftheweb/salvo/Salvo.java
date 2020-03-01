package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;

@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name="native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayers;

    private int turnNumber;
    private ArrayList<String> locations;

    public Salvo() {
    }
    public Salvo(GamePlayer gamePlayers, int turnNumber, ArrayList<String> locations) {
        this.gamePlayers = gamePlayers;
        this.turnNumber = turnNumber;
        this.locations = locations;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayers;
    }
    public void setGamePlayer(GamePlayer gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public int getTurnNumber() {
        return turnNumber;
    }
    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }

    public ArrayList<String> getLocations() {
        return locations;
    }
    public void setLocations(ArrayList<String> locations) {
        this.locations = locations;
    }
}
