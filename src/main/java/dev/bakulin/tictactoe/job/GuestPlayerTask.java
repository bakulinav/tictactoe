package dev.bakulin.tictactoe.job;

import dev.bakulin.tictactoe.model.Actor;
import dev.bakulin.tictactoe.service.Player;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@Profile("player")
public class GuestPlayerTask {

    final Player player;

    AtomicBoolean gameInAction = new AtomicBoolean(false);
    AtomicReference<String> currentSession = new AtomicReference<>();

    public GuestPlayerTask(Player player) {
        this.player = player;
    }

    @Scheduled(fixedDelayString = "10", initialDelay = 5L, timeUnit= TimeUnit.SECONDS)
    public void findAndPlay() {
        try {
            if (gameInAction.get()) {
                // make a move
                log.debug("Game in action. Take turn");

                boolean completeGame = player.takeTurn(currentSession.get());

                if (completeGame) {
                    currentSession.set(null);
                    gameInAction.set(false);
                }
            } else {
                // init game
                log.debug("Game is not active. Create one");


                    String session = player.initGameplay(Actor.CROSS);
                    currentSession.set(session);
                    log.info("Session is activated: {}", session);
                    gameInAction.compareAndSet(false, true);

            }
        } catch (RuntimeException ex) {
            log.error("Fail to init the game: {}", ex.getMessage());
        }
    }

    @PostConstruct
    void afterConstruct() {
        log.info("Guest player is running");
    }
}
