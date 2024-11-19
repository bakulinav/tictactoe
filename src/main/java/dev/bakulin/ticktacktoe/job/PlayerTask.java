package dev.bakulin.ticktacktoe.job;


import dev.bakulin.ticktacktoe.client.model.Side;
import dev.bakulin.ticktacktoe.service.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@Profile("player")
public class PlayerTask {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    final Player player;

    AtomicBoolean sessionInAction = new AtomicBoolean(false);
    AtomicReference<String> currentSession = new AtomicReference<>();

    public PlayerTask(Player player) {
        this.player = player;
    }

    @Scheduled(fixedDelay = 5000L, initialDelay = 5000L)
    public void findAndPlay() {
        log.info("Run check at {}", dateFormat.format(new Date()));
        if (sessionInAction.get()) {
            // make a move
            log.info("Game in action. Make a move");

            boolean completeGame = player.makeMove(currentSession.get());

            if (completeGame) {
                currentSession.set(null);
                sessionInAction.set(false);
            }
        } else {
            // init game
            log.info("Game not active. Init one");
            String session = player.initGameplay(Side.CROSS);
            currentSession.set(session);
            log.info("Session is activated: {}", session);
            sessionInAction.compareAndSet(false, true);
        }

    }
}
