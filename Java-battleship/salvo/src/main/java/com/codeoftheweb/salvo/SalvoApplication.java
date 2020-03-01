package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.*;

import java.util.Date;

import static java.util.Calendar.HOUR;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {

        SpringApplication.run(SalvoApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CommandLineRunner initData(PlayerRepository repository, GameRepository gamerepository, GamePlayerRepository gameplayerrepository, ShipRepository shiprepository, SalvoRepository salvorepository, ScoreRepository scores) {
        return (args) -> {
            Player p1 = new Player("j.bauer@ctu.gov", passwordEncoder().encode("42"));
            repository.save(p1);
            Player p2 = new Player("c.obrian@ctu.gov", passwordEncoder().encode("24"));
            repository.save(p2);
            Player p3 = new Player("kim_bauer@gmail.com", passwordEncoder().encode("kb"));
            repository.save(p3);
            Player p4 = new Player("t.almeida@ctu.gov", passwordEncoder().encode("mole"));
            repository.save(p4);

            Game g1 = new Game();
            gamerepository.save(g1);
            Game g2 = new Game();
            Date date2 = g2.getCreationDate();
            Date newDate2 = Date.from(date2.toInstant().plusSeconds(3600));
            g2.setCreationDate(newDate2);
            gamerepository.save(g2);
            Game g3 = new Game();
            Date date3 = g3.getCreationDate();
            Date newDate3 = Date.from(date3.toInstant().plusSeconds(7200));
            g3.setCreationDate(newDate3);
            gamerepository.save(g3);
            Game g4 = new Game();
            gamerepository.save(g4);
            Game g5 = new Game();
            gamerepository.save(g5);

            GamePlayer gp1 = new GamePlayer(g1, p1);
            gameplayerrepository.save(gp1);
            GamePlayer gp2 = new GamePlayer(g1, p2);
            gameplayerrepository.save(gp2);
            GamePlayer gp3 = new GamePlayer(g3, p4);
            gameplayerrepository.save(gp3);
            GamePlayer gp4 = new GamePlayer(g2, p1);
            gameplayerrepository.save(gp4);
            GamePlayer gp5 = new GamePlayer(g3, p2);
            gameplayerrepository.save(gp5);
            GamePlayer gp6 = new GamePlayer(g4, p3);
            gameplayerrepository.save(gp6);
            GamePlayer gp7 = new GamePlayer(g4, p2);
            gameplayerrepository.save(gp7);

            ArrayList<String> shipLocations = new ArrayList<String>(
                    Arrays.asList("a1", "a2", "a3"));
            String sname = "Carrier";
            Ship s1 = new Ship(gp1, sname, shipLocations);
            shiprepository.save(s1);

            shipLocations = new ArrayList<String>(
                    Arrays.asList("b1", "b2", "b3", "b4"));
            sname = "Battleship";
            Ship s2 = new Ship(gp1, sname, shipLocations);
            shiprepository.save(s2);

            shipLocations = new ArrayList<String>(
                    Arrays.asList("c1", "d1", "e1"));
            sname = "Submarine";
            Ship s3 = new Ship(gp2, sname, shipLocations);
            shiprepository.save(s3);

            shipLocations = new ArrayList<String>(
                    Arrays.asList("e1", "e2", "e3"));
            sname = "Destroyer";
            Ship s4 = new Ship(gp2, sname, shipLocations);
            shiprepository.save(s4);

            shipLocations = new ArrayList<String>(
                    Arrays.asList("f1", "g1", "h1"));
            sname = "Patrolboat";
            Ship s5 = new Ship(gp3, sname, shipLocations);
            shiprepository.save(s5);

            ArrayList<String> salvoLocations = new ArrayList<String>(
                    Arrays.asList("a1"));
            Salvo sa1 = new Salvo(gp1, 1, salvoLocations);
            salvorepository.save(sa1);

            salvoLocations = new ArrayList<String>(
                    Arrays.asList("b1"));
            Salvo sa0 = new Salvo(gp2, 1, salvoLocations);
            salvorepository.save(sa0);

            salvoLocations = new ArrayList<String>(
                    Arrays.asList("c1"));
            Salvo sa2 = new Salvo(gp1, 2, salvoLocations);
            salvorepository.save(sa2);

            salvoLocations = new ArrayList<String>(
                    Arrays.asList("f1"));
            Salvo sa23 = new Salvo(gp2, 2, salvoLocations);
            salvorepository.save(sa23);

            salvoLocations = new ArrayList<String>(
                    Arrays.asList("e1"));
            Salvo sa3 = new Salvo(gp3, 1, salvoLocations);
            salvorepository.save(sa3);

            salvoLocations = new ArrayList<String>(
                    Arrays.asList("f3"));
            Salvo sa4 = new Salvo(gp4, 4, salvoLocations);
            salvorepository.save(sa4);

            salvoLocations = new ArrayList<String>(
                    Arrays.asList("g2"));
            Salvo sa5 = new Salvo(gp5, 5, salvoLocations);
            salvorepository.save(sa5);

            Date d1 = new Date();
            Score sc1 = new Score(p1, g1, d1, 1.0);
            scores.save(sc1);
            Score sc5 = new Score(p2, g1, d1, 0.0);
            scores.save(sc5);
            Score sc2 = new Score(p1, g2, d1, 0.0);
            scores.save(sc2);
            Score sc3 = new Score(p4, g3, d1, 0.5);
            scores.save(sc3);
            Score sc6 = new Score(p2, g3, d1, 0.5);
            scores.save(sc6);
            Score sc4 = new Score(p2, g4, d1, 0.0);
            scores.save(sc4);
            Score sc7 = new Score(p3, g4, d1, 1.0);
            scores.save(sc7);
        };
    }
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
    @Autowired
    PlayerRepository playerRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(username -> {
            Player player = playerRepository.findByUserName(username);
            System.out.println(username);
            if (player != null) {
                return new User(player.getUserName(), player.getPassword(),
                        AuthorityUtils.createAuthorityList("USER"));
            } else {
                throw new UsernameNotFoundException("Unknown user: " + username);
            }
        });
    }
}

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/games").permitAll()
                .antMatchers("/api/leaderboard").permitAll()
                .antMatchers("/web/gameview.html").permitAll()
                .antMatchers("/web/games.html").permitAll()
                .antMatchers("/web/index.html").permitAll()
                .antMatchers("/web/games.js").permitAll()
                .antMatchers("/api/players").permitAll()
                .antMatchers("/api/playersview").permitAll()
                .antMatchers("/api/login").permitAll()
                .antMatchers("/api/logout").permitAll()
                .antMatchers("/api/addShips/*").permitAll()
                .antMatchers("/api/game_view/*").hasAnyAuthority("USER")
                //.antMatchers("/api/addShips/*").hasAnyAuthority("USER")
                .antMatchers("/rest/*").permitAll()
                .anyRequest().fullyAuthenticated()
                .and()
                .formLogin()
                .usernameParameter("name")
                .passwordParameter("pwd")
                .loginPage("/api/login");
        http.logout().logoutUrl("/api/logout");
        http.csrf().disable();
        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED));
        http.formLogin().successHandler((request, response, authentication) -> clearAuthenticationAttribute(request));
        http.formLogin().failureHandler((request, response, exception) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED));
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }

    private void clearAuthenticationAttribute(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}
