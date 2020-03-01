package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.Set;
import static java.util.stream.Collectors.toList;


@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")

    private long id;
    private String userName;
    private String password;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<Score> gameScores;

    public Player() {
    }
    public Player(String userName, String password)
    {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public void addGamePlayer(GamePlayer player) {
        player.setPlayer(this);
        gamePlayers.add(player);
    }

    public void addGameScore(Score player) {
        player.setPlayer(this);
        gameScores.add(player);
    }

    public Set<Score> getGameScores() {
        return gameScores;
    }

    public void setGameScores(Set<Score> gameScores) {
        this.gameScores = gameScores;
    }

    public String toString() {
        return userName + id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}



