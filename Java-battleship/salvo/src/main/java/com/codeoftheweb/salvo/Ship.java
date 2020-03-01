package com.codeoftheweb.salvo;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Set;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayers;

    private String shipType;
    private ArrayList<String> location;

    public Ship() { }

    public Ship(GamePlayer gamePlayers, String shipType, ArrayList<String> location) {
        this.gamePlayers = gamePlayers;
        this.shipType = shipType;
        this.location = location;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public GamePlayer getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(GamePlayer gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public String getShipType() {
        return shipType;
    }

    public void setShipType(String shipType) {
        this.shipType = shipType;
    }

    public ArrayList<String> getLocation() {
        return location;
    }

    public void setLocation(ArrayList<String> location) {
        this.location = location;
    }
    public GamePlayer getGamePlayer() {
        return gamePlayers;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayers = gamePlayer;
    }
}
