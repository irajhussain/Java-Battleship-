package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@RestController
@RequestMapping("/api")
public class SalvoController {
    @Autowired
    private GameRepository gamerepo;
    @Autowired
    private GamePlayerRepository gameplayerrepo;
    @Autowired
    private PlayerRepository playerrepo;
    @Autowired
    private ScoreRepository scorerepo;
    @Autowired
    private ShipRepository shiprepo;
    @Autowired
    private SalvoRepository salvorepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping("/games")
    public List getGames(Authentication authentication) {
        Player player = null;
        Map<String, Object> playerList = new LinkedHashMap<String, Object>();
        List mainList = new ArrayList();
        if (authentication == null) {
            playerList.put("current_player", null);
        } else {
            player = playerrepo.findByUserName(authentication.getName());
            playerList.put("current_player", makePlayer(player));
        }
        mainList.add(playerList);

        List<Game> games = gamerepo.findAll();
        for (Game game : games) {
            Map<String, Object> gameList = new LinkedHashMap<String, Object>();
            Long id = game.getId();
            Date created = game.getCreationDate();
            gameList.put("id", id);
            gameList.put("created", created);

            Set<GamePlayer> gamers = game.gamePlayers;
            List gamerList = new ArrayList();
            if (gamers.isEmpty()) {
                gameList.put("gameplayers", null);
            }
            for (GamePlayer gp : gamers) {
                gamerList.add(makeGamePlayer(gp));
                gameList.put("gameplayers", gamerList);
            }
            mainList.add(gameList);
        }
        return mainList;
    }

    @RequestMapping("/playersview")
    public List getPlayers() {
        Player player = null;
        List mainList = new ArrayList();

        List<Player> players = playerrepo.findAll();
        for (Player p : players) {
            Map<String, Object> PlayerList = new LinkedHashMap<String, Object>();
            Long id = p.getId();
            String name = p.getUserName();
            PlayerList.put("id", id);
            PlayerList.put("username", name);
            mainList.add(PlayerList);
        }
        return mainList;
    }

    private Object makeGamePlayer(GamePlayer gamer) {
        Map<String, Object> gamerList = new LinkedHashMap<String, Object>();
        gamerList.put("id", gamer.getId());
        gamerList.put("player", makePlayer(gamer.getPlayer()));

        return gamerList;
    }

    private Object makePlayer(Player player) {
        Map<String, Object> playerList = new LinkedHashMap<String, Object>();
        playerList.put("id", player.getId());
        playerList.put("name", player.getUserName());
        return playerList;
    }

    @RequestMapping("/gameview/{id}")
    public Map<String, Object> getGameInfo(@PathVariable Long id) {
      /*  Player player2= CurrentPlayer(authentication);
        if (player2==null){
            return null;
        }*/
        GamePlayer gp = gameplayerrepo.getOne(id);
        Map<String, Object> gamePlayerList = new LinkedHashMap<String, Object>();

        Game game = gp.getGame();
        gamePlayerList.put("id", game.getId());
        gamePlayerList.put("date", game.getCreationDate());

        Set<GamePlayer> gamers = game.gamePlayers;
        List gamerList = new ArrayList();
        if (gamers.isEmpty()) {
            gamePlayerList.put("gameplayers", null);
        } else {
            for (GamePlayer gp2 : gamers) {
                gamerList.add(makeGamePlayer(gp2));
                gamePlayerList.put("gameplayers", gamerList);
            }
        }
        Set<Ship> ships = gp.getPlayerShips();
        List shiplist = new ArrayList();
        if (ships.isEmpty()) {
            gamePlayerList.put("ships", null);
        } else {
            for (Ship s : ships) {
                shiplist.add(makeShip(s));
            }
            gamePlayerList.put("ships", shiplist);
        }

        Set<Salvo> salvos = gp.getPlayerSalvos();
        List salvoList = new ArrayList();
        if (salvos.isEmpty()) {
            gamePlayerList.put("salvos", null);
        } else {
            for (Salvo sa : salvos) {
                salvoList.add(makeSalvo(sa));
            }
            gamePlayerList.put("salvos", salvoList);
        }
        Set<Score> scores = gp.getPlayer().gameScores;
        gamePlayerList.put("score", (makeScoreDto(scores)));
        return gamePlayerList;
    }

    private Object makeScoreDto(Set<Score> scores) {
        Map<String, Object> scoreList = new LinkedHashMap<String, Object>();
        Double total = 0.0;
        Integer win = 0;
        Integer lose = 0;
        Integer draw = 0;
        if (scores.size() == 0) {
            scoreList.put("total", total);
            scoreList.put("win", win);
            scoreList.put("draw", draw);
            scoreList.put("loser", lose);
        } else {
            for (Score score : scores) {
                total = total + score.getScore();
                if (score.getScore() == 1.0) {
                    win = win + 1;
                } else if (score.getScore() == 0.5) {
                    draw = draw + 1;
                } else {
                    lose = lose + 1;
                }
                scoreList.put("total", total);
                scoreList.put("win", win);
                scoreList.put("draw", draw);
                scoreList.put("loser", lose);
            }
        }
        return scoreList;
    }

    private Map<String, Object> makeShip(Ship ship) {
        Map<String, Object> shipList = new LinkedHashMap<String, Object>();
        shipList.put("type", ship.getShipType());
        shipList.put("locations", ship.getLocation());
        return shipList;
    }

    private Map<String, Object> makeSalvo(Salvo salvo) {
        Map<String, Object> salvoList = new LinkedHashMap<String, Object>();
        salvoList.put("turn", salvo.getTurnNumber());
        salvoList.put("player", salvo.getGamePlayer().getId());
        salvoList.put("locations", salvo.getLocations());
        return salvoList;
    }

    public Player CurrentPlayer(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        return playerrepo.findByUserName(authentication.getName());
    }

    @RequestMapping("/leaderboard")
    public Map<String, Object> getGameResults() {
        List<Player> players = playerrepo.findAll();
        Map<String, Object> wholeList = new LinkedHashMap<String, Object>();
        List<Map<String, Object>> playersall = new ArrayList<>();

        for (Player player : players) {
            Map<String, Object> playerList = new LinkedHashMap<String, Object>();
            playerList.put("name", player.getUserName());

            Set<Score> scores = player.gameScores;
            Double total = 0.0;
            Integer win = 0;
            Integer lose = 0;
            Integer draw = 0;

            if (scores.size() == 0) {
                playerList.put("total", total);
                playerList.put("win", win);
                playerList.put("draw", draw);
                playerList.put("loser", lose);
            } else {
                for (Score score : scores) {
                    total = total + score.getScore();
                    if (score.getScore() == 1.0) {
                        win = win + 1;
                    } else if (score.getScore() == 0.5) {
                        draw = draw + 1;
                    } else {
                        lose = lose + 1;
                    }
                    playerList.put("total", total);
                    playerList.put("win", win);
                    playerList.put("draw", draw);
                    playerList.put("loser", lose);
                }
            }
            playersall.add(playerList);
        }
        Collections.sort(playersall, new Comparator<Map<String, Object>>() {
            public int compare(Map<String, Object> m1, Map<String, Object> m2) {
                return ((Double) m2.get("total")).compareTo((Double) m1.get("total"));
            }
        });
        wholeList.put("player", playersall);
        return wholeList;
    }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<String> createUser(@RequestParam String username, @RequestParam String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("No name given", HttpStatus.FORBIDDEN);
        }
        Player player = playerrepo.findByUserName(username);
        if (player != null) {
            return new ResponseEntity<>("Name already used", HttpStatus.CONFLICT);
        }
        playerrepo.save(new Player(username, passwordEncoder.encode(password)));
        return new ResponseEntity<>("Name added", HttpStatus.CREATED);
    }

    @RequestMapping(path = "/newGame", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        Player player = CurrentPlayer(authentication);
        if (player == null) {
            return new ResponseEntity<>(makeMap("error", "No user logged in"), HttpStatus.FORBIDDEN);
        } else {
            Game game = new Game();
            gamerepo.save(game);
            GamePlayer gamePlayer = new GamePlayer(game, player);
            gameplayerrepo.save(gamePlayer);
            return new ResponseEntity<>(makeMap("link", "game.html?gg=" + game.getId() + "&gp=" + gamePlayer.getId()), HttpStatus.CREATED);
        }
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    @RequestMapping(path = "/joinGame", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGames(Authentication authentication, @RequestParam Long gameId) {
        Player player = CurrentPlayer(authentication);
        if (player == null) {
            return new ResponseEntity<>(makeMap("error", "no user found"), HttpStatus.FORBIDDEN);
        }
        Game game = gamerepo.getOne(gameId);
        Set<GamePlayer> gamer = game.gamePlayers;

        if (gamer == null) {
            GamePlayer gamePlayer = new GamePlayer(game, player);
            gameplayerrepo.save(gamePlayer);
            return new ResponseEntity<>(makeMap("link", "game.html?gg=" + gameId + "&gp=" + gamePlayer.getId()), HttpStatus.CREATED);
        }
        for (GamePlayer gp : gamer) {
            if (gp.getPlayer().getUserName() == authentication.getName()) {
                return new ResponseEntity<>(makeMap("link", "game.html?gg=" + gameId + "&gp=" + gp.getId()), HttpStatus.CREATED);
            }
        }
        if (gamer != null && gamer.size() >= 2) {
            return new ResponseEntity<>(makeMap("error", "game is full"), HttpStatus.FORBIDDEN);
        }

        GamePlayer gamePlayer = new GamePlayer(game, player);
        gameplayerrepo.save(gamePlayer);
        return new ResponseEntity<>(makeMap("link", "game.html?gg=" + gameId + "&gp=" + gamePlayer.getId()), HttpStatus.CREATED);
    }

    @RequestMapping(path = "/addShips/{gpId}", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addShip(Authentication authentication, @PathVariable Long gpId, @RequestParam String shipClass, @RequestParam ArrayList locations, @RequestParam String direction) {
        ArrayList<String> shipTypes = new ArrayList<>(
                Arrays.asList("Carrier", "Battleship", "Submarine", "Destroyer", "Patrolboat"));
        ArrayList<String> numbers = new ArrayList<>(
                Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
        ArrayList<String> letters = new ArrayList<>(
                Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j"));
        ArrayList<String> directions = new ArrayList<>(
                Arrays.asList("horizontal", "vertical"));
        Map<String, Integer> shipLengths = new LinkedHashMap<String, Integer>();
        shipLengths.put("Carrier", 5);
        shipLengths.put("Battleship", 4);
        shipLengths.put("Submarine", 3);
        shipLengths.put("Destroyer", 3);
        shipLengths.put("Patrolboat", 2);
        Player player = CurrentPlayer(authentication);
        List<GamePlayer> gamers = gameplayerrepo.findAll();
        /*if (gpId > gamers.size()) {
            return new ResponseEntity<>(makeMap("error","no gameplayer exists"),HttpStatus.FORBIDDEN);
        }*/
        GamePlayer gamePlayer = gameplayerrepo.getOne(gpId);

        Player player1 = gamePlayer.getPlayer();
        Set<Ship> ships = gamePlayer.playerShips;
        /*for (Ship ship : ships) {
            if (shipTypes.contains(ship.getShipType())) {
                shipTypes.remove(ship.getShipType());
            }
        }
        if (player == null) {
            return new ResponseEntity<>(makeMap("error","No user logged in"),HttpStatus.FORBIDDEN);
        }
        if (player1 != player) {
            return new ResponseEntity<>(makeMap("error","Not this users game"),HttpStatus.FORBIDDEN);
        }*/
        if (ships != null && ships.size() > 5) {
            return new ResponseEntity<>(makeMap("error", "Ships Already Full"), HttpStatus.FORBIDDEN);
        }

        boolean decision = false;
        for (Integer i = 0; i < shipTypes.size(); i++) {
            if (shipTypes.get(i).equals(shipClass)) {
                decision = true;
            }
        }
        if (decision == false) {
            return new ResponseEntity<>(makeMap("error", "wrong ship type or ship already added"), HttpStatus.FORBIDDEN);
        }

        if (shipLengths.get(shipClass).intValue() != locations.size()) {
            return new ResponseEntity<>(makeMap("error", "Wrong size of ship" + locations.size()), HttpStatus.FORBIDDEN);
        }

        ArrayList<String> shipLocations = new ArrayList<>();
        for (Ship ship : ships) {
            for (Integer i = 0; i < ship.getLocation().size(); i++) {
                shipLocations.add(ship.getLocation().get(i));
            }
        }
        for (Integer i = 0; i < locations.size(); i++) {
            for (Integer j = 0; j < shipLocations.size(); j++) {
                if (shipLocations.get(j).equals(locations.get(i))) {
                    return new ResponseEntity<>(makeMap("error", "This Place Already Full"), HttpStatus.FORBIDDEN);
                }
            }
        }
        if (directions.contains(direction) == false) {
            return new ResponseEntity<>(makeMap("error", "No valid direction"), HttpStatus.FORBIDDEN);
        }
        for (Integer i = 0; i < locations.size(); i++) {
            String letter = locations.get(i).toString().substring(0, 1);
            String number = locations.get(i).toString().substring(1);
            if (letters.contains(letter) == false || numbers.contains(number) == false) {
                return new ResponseEntity<>(makeMap("error", "wrong coordinates"), HttpStatus.FORBIDDEN);
            }
        }
        Ship shipp = new Ship(gamePlayer, shipClass, locations);
        shiprepo.save(shipp);

        return new ResponseEntity<>(makeShip(shipp), HttpStatus.CREATED);
    }

    @RequestMapping(path = "/modShips/{gpId}", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> modShip(Authentication authentication, @PathVariable Long gpId, @RequestParam String shipClass, @RequestParam ArrayList locations, @RequestParam String direction) {
        ArrayList<String> shipTypes = new ArrayList<>(
                Arrays.asList("Carrier", "Battleship", "Submarine", "Destroyer", "Patrolboat"));
        ArrayList<String> numbers = new ArrayList<>(
                Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
        ArrayList<String> letters = new ArrayList<>(
                Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j"));
        ArrayList<String> directions = new ArrayList<>(
                Arrays.asList("horizontal", "vertical"));
        Map<String, Integer> shipLengths = new LinkedHashMap<String, Integer>();
        shipLengths.put("Carrier", 5);
        shipLengths.put("Battleship", 4);
        shipLengths.put("Submarine", 3);
        shipLengths.put("Destroyer", 3);
        shipLengths.put("Patrolboat", 2);
        Player player = CurrentPlayer(authentication);
        List<GamePlayer> gamers = gameplayerrepo.findAll();
        /*if (gpId > gamers.size()) {
            return new ResponseEntity<>(makeMap("error","no gameplayer exists"),HttpStatus.FORBIDDEN);
        }*/
        GamePlayer gamePlayer = gameplayerrepo.getOne(gpId);

        Player player1 = gamePlayer.getPlayer();
        Set<Ship> ships = gamePlayer.playerShips;
        for (Ship ship : ships) {
            if (shipTypes.contains(ship.getShipType())) {
                shipTypes.remove(ship.getShipType());
                gamePlayer.playerShips.remove(ship.getShipType());
            }
        }
        /*if (player == null) {
            return new ResponseEntity<>(makeMap("error","No user logged in"),HttpStatus.FORBIDDEN);
        }
        if (player1 != player) {
            return new ResponseEntity<>(makeMap("error","Not this users game"),HttpStatus.FORBIDDEN);
        }*/
        if (ships != null && ships.size() <= 0) {
            return new ResponseEntity<>(makeMap("error", "No Ships Already!"), HttpStatus.FORBIDDEN);
        }

        boolean decision = false;
        for (Integer i = 0; i < shipTypes.size(); i++) {
            if (shipTypes.get(i).equals(shipClass)) {
                decision = true;
            }
        }
        if (decision == true) {
            return new ResponseEntity<>(makeMap("error", "wrong ship type or ship not added"), HttpStatus.FORBIDDEN);
        }

        if (shipLengths.get(shipClass).intValue() != locations.size()) {
            return new ResponseEntity<>(makeMap("error", "Wrong size of ship" + locations.size()), HttpStatus.FORBIDDEN);
        }

        ArrayList<String> shipLocations = new ArrayList<>();
        for (Ship ship : ships) {
            for (Integer i = 0; i < ship.getLocation().size(); i++) {
                shipLocations.add(ship.getLocation().get(i));
            }
        }
        for (Integer i = 0; i < locations.size(); i++) {
            for (Integer j = 0; j < shipLocations.size(); j++) {
                if (shipLocations.get(j).equals(locations.get(i))) {
                    return new ResponseEntity<>(makeMap("error", "This Place Already Full"), HttpStatus.FORBIDDEN);
                }
            }
        }
        if (directions.contains(direction) == false) {
            return new ResponseEntity<>(makeMap("error", "No valid direction"), HttpStatus.FORBIDDEN);
        }
        for (Integer i = 0; i < locations.size(); i++) {
            String letter = locations.get(i).toString().substring(0, 1);
            String number = locations.get(i).toString().substring(1);
            if (letters.contains(letter) == false || numbers.contains(number) == false) {
                return new ResponseEntity<>(makeMap("error", "wrong coordinates"), HttpStatus.FORBIDDEN);
            }
        }
        Ship shipp = new Ship(gamePlayer, shipClass, locations);
        shiprepo.save(shipp);

        return new ResponseEntity<>(makeShip(shipp), HttpStatus.CREATED);
    }

    @RequestMapping(path = "/addSalvos/{gpId}/{turn}", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addSalvo(Authentication authentication, @PathVariable Long gpId, @RequestParam ArrayList locations, @PathVariable Integer turn) {
        Player player = CurrentPlayer(authentication);
        List<GamePlayer> gamers = gameplayerrepo.findAll();
        /*if (gpId > gamers.size()) {
            return new ResponseEntity<>(makeMap("error","no gameplayer exists"),HttpStatus.FORBIDDEN);
        }*/
        GamePlayer gamePlayer = gameplayerrepo.getOne(gpId);
        Player player1 = gamePlayer.getPlayer();
        Game game = gamePlayer.getGame();
        Set<GamePlayer> list = game.gamePlayers;
        GamePlayer opponent = null;
        if (list.size() != 2) {
            return new ResponseEntity<>(makeMap("error", "No opponent wait for this guy to fire"), HttpStatus.FORBIDDEN);
        }
        for (GamePlayer player2 : list) {
            if (player2 != gamePlayer) {
                opponent = player2;
            }
        }
        Set<Salvo> opponentSalvos = opponent.playerSalvos;
        Set<Ship> opponentShips = opponent.playerShips;
        Set<Ship> yourShips = gamePlayer.playerShips;
        Integer salvoSize = getShipStatus(yourShips, opponentSalvos);
        Integer oppSalvoSize = getShipStatus(opponentShips, gamePlayer.playerSalvos);
        if (player == null) {
            return new ResponseEntity<>(makeMap("error", "No user logged in"), HttpStatus.FORBIDDEN);
        }
        if (player1 != player) {
            return new ResponseEntity<>(makeMap("error", "Not this users game"), HttpStatus.FORBIDDEN);
        }
        if (opponent == null) {
            return new ResponseEntity<>(makeMap("error", "no opponent!"), HttpStatus.FORBIDDEN);
        }
        if (opponentShips.size() < 1) {
            return new ResponseEntity<>(makeMap("error", "your opponent haven't finished placing ship yet "), HttpStatus.FORBIDDEN);
        }
        if (yourShips.size() < 1) {
            return new ResponseEntity<>(makeMap("error", "First add ships"), HttpStatus.FORBIDDEN);
        }
        Set<Salvo> playerSalvos = gamePlayer.playerSalvos;
        Integer yourLastTurn = getLastTurn(playerSalvos);
        Integer oppenentLastTurn = getLastTurn(opponentSalvos);
        if (locations.size() > 2) {//salvoSize&& turn>oppenentLastTurn) {
            return new ResponseEntity<>(makeMap("error", "you can only send 2 salvos at a time"), HttpStatus.FORBIDDEN);
        }
        ArrayList<String> salvoLocations = new ArrayList<>();
        for (Salvo salvo : playerSalvos) {
            for (Integer i = 0; i < salvo.getLocations().size(); i++) {
                salvoLocations.add(salvo.getLocations().get(i));
            }
        }
        if (oppSalvoSize == 0) {
            return new ResponseEntity<>(makeMap("error", "game already finished"), HttpStatus.FORBIDDEN);
        }
        /*if (turn - yourLastTurn != 1) {
            return new ResponseEntity<>(makeMap("error","Turn number problem"),HttpStatus.FORBIDDEN);
        }*/
        if (turn - oppenentLastTurn > 1) {
            return new ResponseEntity<>(makeMap("error", "wait for other player to fire " + turn + "---" + oppenentLastTurn), HttpStatus.FORBIDDEN);
        }
        for (Integer i = 0; i < locations.size(); i++) {
            for (Integer j = 0; j < salvoLocations.size(); j++) {
                if (locations.get(i).equals(salvoLocations.get(j))) {
                    return new ResponseEntity<>(makeMap("error", "already fired there" + locations.get(i) + " " + salvoLocations.get(j)), HttpStatus.FORBIDDEN);
                }
            }
        }
        Salvo salvo = new Salvo(gamePlayer, turn, locations);
        salvorepo.save(salvo);
        return new ResponseEntity<>(makeSalvo(salvo), HttpStatus.CREATED);
    }

    public Integer getShipStatus(Set<Ship> ships, Set<Salvo> salvoes) {
        if (ships == null || salvoes == null) {
            return 5;
        }

        List<Map> opponentList = new ArrayList<>();
        Integer shipsLeft = 5;
        for (Ship ship : ships) {
            Integer count = 0;
            Integer turn = 0;
            Integer maxTurn = 0;

            for (Integer i = 0; i < ship.getLocation().size(); i++) {
                for (Salvo salvo : salvoes) {
                    for (Integer j = 0; j < salvo.getLocations().size(); j++) {
                        if (ship.getLocation().get(i).equals(salvo.getLocations().get(j))) {
                            count = count + 1;
                            turn = salvo.getTurnNumber();
                            if (turn > maxTurn) {
                                maxTurn = turn;
                            }
                        }
                    }
                }
            }
            Map<String, Object> shippie = new LinkedHashMap<>();
            if (count == ship.getLocation().size()) {
                shipsLeft = shipsLeft - 1;
                shippie.put("shipsLeft", shipsLeft);
                shippie.put(ship.getShipType(), "sunk");
                shippie.put("turn", maxTurn);
            } else {
                shippie.put("shipsLeft", shipsLeft);
                shippie.put("turn", turn);
            }
            opponentList.add(shippie);
        }
        return shipsLeft;
    }

    public Integer getLastTurn(Set<Salvo> salvoes) {
        Integer lastTurn = 0;
        if (salvoes == null) {
            return 0;
        } else {
            for (Salvo salvo : salvoes) {
                if (lastTurn < salvo.getTurnNumber()) {
                    lastTurn = salvo.getTurnNumber();
                }
            }
        }
        return lastTurn;
    }

    @RequestMapping(path = "/addScores/{gameId}", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addScores(Authentication authentication, @PathVariable Long gameId, @RequestParam Long winnerId, @RequestParam Long LoserId) {
        Game game = gamerepo.getOne(gameId);
        Date gamedate = game.getCreationDate();
        Player winner = null, loser = null;
        Set<GamePlayer> players = game.getGamePlayers();
        Set<Score> scores = game.getGameScores();
        Integer done = 0;
        for (GamePlayer gp : players) {
            if (gp.getId() == winnerId) {
                winner = gp.getPlayer();
                Score score = new Score(winner, game, gamedate, 1);
                scorerepo.save((score));
                done++;
            } else if (gp.getId() == LoserId) {
                loser = gp.getPlayer();
                Score loserscore = new Score(loser, game, gamedate, 0);
                scorerepo.save(loserscore);
                done++;
            }
        }
        if (done == 2) {
            return new ResponseEntity<>(makeMap("success", "Winner and losers scored"), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(makeMap("error", "Not valid information"), HttpStatus.FORBIDDEN);
    }
}

