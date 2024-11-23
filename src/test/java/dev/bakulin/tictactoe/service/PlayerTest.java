package dev.bakulin.tictactoe.service;

import dev.bakulin.tictactoe.client.GameServerClient;
import dev.bakulin.tictactoe.dto.MoveRequest;
import dev.bakulin.tictactoe.engine.TicTacToe;
import dev.bakulin.tictactoe.model.Actor;
import dev.bakulin.tictactoe.model.Game;
import dev.bakulin.tictactoe.model.GameState;
import dev.bakulin.tictactoe.model.GameStatus;
import dev.bakulin.tictactoe.model.Sides;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PlayerTest {
    @Mock
    GameServerClient gameServerClient;

    @Mock
    TicTacToe engine;

    @InjectMocks
    Player player;

    String initSession;
    @BeforeEach
    void setUp() {
        initSession = UUID.randomUUID().toString();
    }

    @Test
    void testHandleInitRemoteUnavailable() {
        doThrow(new ResourceAccessException("Service unavailable"))
                .when(gameServerClient)
                .initGameAs(Actor.CROSS);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            player.initGameplay(Actor.CROSS);
        });

        assertEquals("Fail to connect to game host", exception.getMessage());

        verify(gameServerClient, times(1)).initGameAs(eq(Actor.CROSS));
    }

    @Test
    void testInitRemoteSession() {
        Game gameResponse = new Game(initSession, new GameState(), Sides.initByGuest(Actor.CROSS));
        doReturn(gameResponse)
                .when(gameServerClient)
                .initGameAs(Actor.CROSS);

        String session = player.initGameplay(Actor.CROSS);
        assertEquals(initSession, session);

        verify(gameServerClient, times(1)).initGameAs(eq(Actor.CROSS));
    }

    @Test
    void testSessionStateRemoteUnavailable() {
        doThrow(new ResourceAccessException("Service unavailable"))
                .when(gameServerClient)
                .getState(initSession);

        try {
            player.takeTurn(initSession);
            fail("Exception expected");
        } catch (RuntimeException ex) {
            assertEquals("Fail to connect to game host", ex.getMessage());

            verify(gameServerClient, times(1)).getState(eq(initSession));
            verify(gameServerClient, times(0)).makeMove(eq(initSession), any(MoveRequest.class));
        }
    }

    @Test
    void testSessionStateNotFound() {
        doThrow(HttpClientErrorException.create("Session not Found", HttpStatus.NOT_FOUND,
                "Not found", new HttpHeaders(), null, null))
                .when(gameServerClient)
                .getState(initSession);

        boolean completed = player.takeTurn(initSession);

        assertTrue(completed);

        verify(gameServerClient, times(1)).getState(eq(initSession));
        verify(gameServerClient, times(0)).makeMove(eq(initSession), any(MoveRequest.class));
    }

    @Test
    void testSessionStateBadRequest() {
        doThrow(HttpClientErrorException.create("Session invalid", HttpStatus.BAD_REQUEST,
                "Invalid", new HttpHeaders(), null, null))
                .when(gameServerClient)
                .getState(initSession);

        try {
            player.takeTurn(initSession);
            fail("Exception expected");
        } catch (RuntimeException ex) {
            assertEquals("Fail get state", ex.getMessage());

            verify(gameServerClient, times(1)).getState(eq(initSession));
            verify(gameServerClient, times(0)).makeMove(eq(initSession), any(MoveRequest.class));
        }
    }

    @Test
    void testSessionInactive() {
        Game finished = Game.init(initSession,
                new GameState().setStatus(GameStatus.FINISHED),
                Sides.initByGuest(Actor.CROSS));
        doReturn(finished)
                .when(gameServerClient)
                .getState(initSession);

        boolean completed = player.takeTurn(initSession);

        assertTrue(completed);
        verify(gameServerClient, times(1)).getState(eq(initSession));
        verify(gameServerClient, times(0)).makeMove(eq(initSession), any(MoveRequest.class));
    }

    @Test
    void testSkipTurn() {
        Game nextByZero = Game.init(initSession,
                new GameState()
                        .setStatus(GameStatus.PLAYING)
                        .setNext(Actor.ZERO),
                Sides.initByGuest(Actor.CROSS));

        doReturn(nextByZero)
                .when(gameServerClient)
                .getState(initSession);

        boolean completed = player.takeTurn(initSession);

        assertFalse(completed);
        verify(gameServerClient, times(0)).makeMove(eq(initSession), any(MoveRequest.class));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
            PLAYING  | false
            FINISHED | true
            """)
    void testTakeTurn(String afterPlayStatus, boolean expectCompleted) {
        Sides sides = Sides.initByGuest(Actor.CROSS);

        Game nextByCross = Game.init(initSession,
                new GameState()
                        .setStatus(GameStatus.PLAYING)
                        .setNext(Actor.CROSS),
                sides);

        doReturn(nextByCross)
                .when(gameServerClient)
                .getState(initSession);


        Game afterTurn = Game.init(initSession,
                new GameState()
                        .setStatus(GameStatus.valueOf(afterPlayStatus))
                        .setNext(Actor.ZERO),
                sides);

        doReturn(afterTurn)
                .when(gameServerClient)
                .makeMove(eq(initSession), any(MoveRequest.class));

        boolean completed = player.takeTurn(initSession);

        assertEquals(expectCompleted, completed);
        verify(gameServerClient, times(1)).getState(eq(initSession));
        verify(gameServerClient, times(1)).makeMove(eq(initSession), any(MoveRequest.class));
    }
}