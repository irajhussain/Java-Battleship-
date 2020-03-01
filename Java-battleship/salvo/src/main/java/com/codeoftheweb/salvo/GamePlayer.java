package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "narrative")
    @GenericGenerator(name = "native", strategy = "native")

    private long id;
    private Date creationDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(mappedBy="gamePlayers", fetch=FetchType.EAGER)
    Set<Ship> playerShips;

    @OneToMany(mappedBy="gamePlayers", fetch=FetchType.EAGER)
    Set<Salvo> playerSalvos;

    public GamePlayer() {
        creationDate = new Date();
    }
    public GamePlayer(Game game, Player player) {
        this.game = game;
        this.player = player;
        this.creationDate = new Date();
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Player getPlayer() {
        return player;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }

    public Set<Ship> getPlayerShips() {
        return playerShips;
    }
    public void setPlayerShips(Set<Ship> playerShips) {
        this.playerShips = playerShips;
    }

    public Set<Salvo> getPlayerSalvos() {
        return playerSalvos;
    }
    public void setPlayerSalvos(Set<Salvo> playerSalvos) {
        this.playerSalvos = playerSalvos;
    }

    public String toString() {
        String dateCreation;
        String pattern = "MM/dd/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        return dateCreation = df.format(creationDate);
        //LocalDate date = LocalDate.parse("2011-08-03");
    }
}
