package com.eleks.academy.whoami.core.impl;

import com.eleks.academy.whoami.core.Turn;
import com.eleks.academy.whoami.core.exception.GameStateException;
import com.eleks.academy.whoami.core.exception.PlayerNotFoundException;
import com.eleks.academy.whoami.core.exception.TurnException;
import com.eleks.academy.whoami.enums.GameStatus;
import com.eleks.academy.whoami.enums.PlayerState;
import com.eleks.academy.whoami.enums.QuestionAnswer;
import com.eleks.academy.whoami.model.request.CharacterSuggestion;
import com.eleks.academy.whoami.model.request.Message;
import com.eleks.academy.whoami.model.response.PlayerDetails;
import com.eleks.academy.whoami.model.response.TurnDetails;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.eleks.academy.whoami.enums.Constants.PLAYER_NOT_FOUND;

public class PersistentGame {

    private final String id;
    private final List<PersistentPlayer> players;
    private final int maxPlayers;
    private GameStatus gameStatus = GameStatus.WAITING_FOR_PLAYERS;
    private final List<PersistentPlayer> winners = new LinkedList<>();
    private Turn turn;
    private final HistoryChat history = new HistoryChat();
    private int playersLeft;
    private final List<QuestionAnswer> playersAnswers = new ArrayList<>();

    /**
     * Creates a new game (game room) and makes a first enrolment turn by a current player
     * so that he won't have to enroll to the game he created
     *
     * @param hostPlayer player to initiate a new game
     */
    public PersistentGame(String hostPlayer, Integer maxPlayers) {
        this.id = String.format("%d-%d",
                Instant.now().toEpochMilli(),
                Double.valueOf(Math.random() * 999).intValue());
        this.players = new ArrayList<>(maxPlayers);
        this.maxPlayers = maxPlayers;
        this.players.add(new PersistentPlayer(hostPlayer, id, "Player-1"));
        this.turn = new TurnImpl(players);
        this.playersLeft = maxPlayers;
    }

    public String getGameId() {
        return this.id;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public List<PersistentPlayer> getPLayers() {
        return players;
    }

    public GameStatus getStatus() {
        return gameStatus;
    }


    public List<PersistentPlayer> getWinnerList() {
        return winners;
    }

    /***
     *
     * @return data about current turn (current player, and other players)
     */
    public TurnDetails getTurnDetails() {
        return new TurnDetails(turn.getCurrentPlayer(), turn.getOtherPlayers());
    }

    /***
     *
     * @return PersistentPlayer whose turn is now
     */
    public PersistentPlayer getCurrentTurn() {
        return turn.getCurrentPlayer();
    }

    public List<PersistentPlayer> getOrderedPlayers() {
        return Optional.ofNullable(this.turn)
                .map(Turn::getAllPlayers)
                .orElse(this.players);
    }

    public PlayerDetails enrollToGame(String playerId) {
        PersistentPlayer player;
        if (players.stream().noneMatch((randomPlayer -> randomPlayer.getId().equals(playerId)))) {
            player = new PersistentPlayer(playerId, this.id, "Player-" + (players.size() + 1));
            players.add(player);
            this.turn = new TurnImpl(players);
            if (players.size() == maxPlayers) {
                gameStatus = GameStatus.SUGGEST_CHARACTER;
            }
            return PlayerDetails.of(player);
        } else {
            throw new GameStateException("Player already enrolled in this game!");
        }
    }

    public void suggestCharacter(String playerId, CharacterSuggestion suggestion) {
        var player = players
                .stream()
                .filter(randomPlayer -> randomPlayer.getId().equals(playerId))
                .findFirst().orElseThrow(() -> new PlayerNotFoundException("Player not found!"));
        if (!player.isSuggestStatus()) {
            player.setCharacter(suggestion.getCharacter());
            player.setNickname(suggestion.getNickname());
            player.setSuggestStatus(true);
        } else {
            throw new GameStateException("You already suggest the character");
        }
        if (players.stream().filter(PersistentPlayer::isSuggestStatus).count() == maxPlayers) {
            gameStatus = GameStatus.READY_TO_PLAY;
        }
    }

    public boolean areAllPlayersSuggested() {
        for (var player : this.players) {
            if (!player.isSuggestStatus()) {
                return false;
            }
        }
        return true;
    }

    public void startGame() {
        if (players.stream().filter(PersistentPlayer::isSuggestStatus).count() == maxPlayers) {
            assignCharacters();
            var currentPlayer = turn.getCurrentPlayer();
            currentPlayer.setPlayerState(PlayerState.ASK_QUESTION);
            players.stream()
                    .filter(randomPlayer -> !randomPlayer.getId().equals(currentPlayer.getId()))
                    .forEach(randomPlayer -> randomPlayer.setPlayerState(PlayerState.ANSWER_QUESTION));
            gameStatus = GameStatus.GAME_IN_PROGRESS;
        }
    }


    public void askQuestion(String playerId, String message) {
        // TODO: Show question
        var askingPlayer = players
                .stream()
                .filter(randomPlayer -> randomPlayer.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new PlayerNotFoundException(String.format(PLAYER_NOT_FOUND, playerId)));

        cleanPlayersValues(players);

        if (askingPlayer.getPlayerState().equals(PlayerState.ASK_QUESTION)) {
            askingPlayer.setPlayerQuestion(message);
            askingPlayer.setEnteredQuestion(true);

            this.players.stream()
                    .filter(randomPlayer -> !randomPlayer.getId().equals(askingPlayer.getId()))
                    .forEach(randomPlayer -> randomPlayer.setPlayerState(PlayerState.ANSWER_QUESTION));

            this.history.addQuestion(message, playerId);

        } else {
            throw new TurnException("Not your turn! Current turn has player: " + getCurrentTurn().getNickname());
        }
    }

    public void answerQuestion(String playerId, QuestionAnswer questionAnswer) {
        //TODO: show on screen questions and answers from history
        var askingPlayer = turn.getCurrentPlayer();
        var answeringPlayer = players
                .stream()
                .filter(randomPlayer -> randomPlayer.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new PlayerNotFoundException(String.format(PLAYER_NOT_FOUND, playerId)));

        if (askingPlayer.equals(answeringPlayer)) {
            throw new TurnException("Not your turn for answering");
        }

        if (answeringPlayer.getPlayerState().equals(PlayerState.ANSWER_QUESTION)) {
            this.playersAnswers.add(questionAnswer);
            answeringPlayer.setEnteredAnswer(true);
            answeringPlayer.setPlayerAnswer(String.valueOf(questionAnswer));

            this.history.addAnswer(questionAnswer.toString(), playerId);
        }

        if (playersAnswers.size() == this.playersLeft - 1) {
            var positiveAnswers = playersAnswers
                    .stream()
                    .filter(answer -> answer.equals(QuestionAnswer.YES) || answer.equals(QuestionAnswer.DONT_KNOW))
                    .collect(Collectors.toList());

            var negativeAnswers = playersAnswers
                    .stream()
                    .filter(answer -> answer.equals(QuestionAnswer.NO))
                    .collect(Collectors.toList());

            if (positiveAnswers.size() < negativeAnswers.size()) {
                this.turn = this.turn.changeTurn();
            }
            this.playersAnswers.clear();
            this.playersLeft = this.players.size();
        }
    }

    public void askGuessingQuestion(String playerId, Message guess) {
        var askingPlayer = players
                .stream()
                .filter(randomPlayer -> randomPlayer.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new PlayerNotFoundException(String.format(PLAYER_NOT_FOUND, playerId)));

        cleanPlayersValues(players);

        if (askingPlayer.getPlayerState().equals(PlayerState.ASK_QUESTION)) {
            askingPlayer.setPlayerState(PlayerState.GUESSING);
            askingPlayer.setPlayerQuestion(guess.getMessage());
            askingPlayer.setEnteredQuestion(true);
            askingPlayer.setGuessing(true);

            this.players.stream()
                    .filter(randomPlayer -> !randomPlayer.getId().equals(askingPlayer.getId()))
                    .forEach(randomPlayer -> randomPlayer.setPlayerState(PlayerState.ANSWER_GUESS));

            this.history.addQuestion("Guess: " + guess.getMessage(), playerId);

        } else {
            throw new TurnException("Not your turn! Current turn has player: " + getCurrentTurn().getNickname());
        }
    }

    public void answerGuessingQuestion(String playerId, QuestionAnswer askQuestion) {
        var askingPlayer = turn.getCurrentPlayer();
        var answeringPlayer = players
                .stream()
                .filter(randomPlayer -> randomPlayer.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new PlayerNotFoundException(String.format(PLAYER_NOT_FOUND, playerId)));

        if (askingPlayer.equals(answeringPlayer)) {
            throw new TurnException("Not your turn for answering");
        }

        if (answeringPlayer.getPlayerState().equals(PlayerState.ANSWER_GUESS)) {
            this.playersAnswers.add(askQuestion);
            answeringPlayer.setEnteredAnswer(true);
            answeringPlayer.setPlayerAnswer(String.valueOf(askQuestion));

            this.history.addAnswer(askQuestion.toString(), playerId);
        }

        if (playersAnswers.size() == this.playersLeft - 1) {
            var afkAnswers = playersAnswers
                    .stream()
                    .filter(answer -> answer.equals(QuestionAnswer.DONT_KNOW))
                    .collect(Collectors.toList());

            if (afkAnswers.size() != playersAnswers.size()) {
                var positiveAnswers = playersAnswers
                        .stream()
                        .filter(answer -> answer.equals(QuestionAnswer.YES))
                        .collect(Collectors.toList());

                var negativeAnswers = playersAnswers
                        .stream()
                        .filter(answer -> answer.equals(QuestionAnswer.NO))
                        .collect(Collectors.toList());

                if (positiveAnswers.size() >= negativeAnswers.size()) {
                    askingPlayer.setPlayerState(PlayerState.GAME_WINNER);
                    this.winners.add(askingPlayer);
                    deletePlayer(askingPlayer.getId());
                    if (this.players.size() == 1) {
                        players.get(0).setPlayerState(PlayerState.GAME_LOOSER);
                    }
                } else {
                    this.turn = this.turn.changeTurn();
                }
            } else {
                askingPlayer.setPlayerState(PlayerState.ASK_QUESTION);
            }
            this.playersAnswers.clear();
            this.playersLeft = this.players.size();
        }
    }

    public void deletePlayer(String playerId) {
        if (gameStatus.equals(GameStatus.GAME_IN_PROGRESS)) {
            var leavingPlayer = this.players
                    .stream()
                    .filter(player -> player.getId().equals(playerId))
                    .findFirst()
                    .orElseThrow(() -> new PlayerNotFoundException(String.format(PLAYER_NOT_FOUND, playerId)));

            if (!leavingPlayer.isEnteredAnswer() && !leavingPlayer.isEnteredQuestion() && this.turn.getCurrentPlayer().isEnteredQuestion()) {
                if (leavingPlayer.getPlayerState().equals(PlayerState.ANSWER_QUESTION)) {
                    answerQuestion(playerId, QuestionAnswer.DONT_KNOW);
                }
                if (leavingPlayer.getPlayerState().equals(PlayerState.ANSWER_GUESS)) {
                    answerGuessingQuestion(playerId, QuestionAnswer.DONT_KNOW);
                }
            }
        }
        if(!this.turn.getCurrentPlayer().isEnteredQuestion()){
            this.playersLeft = this.players.size() - 1;
        }

        this.players.removeIf(player -> player.getId().equals(playerId));
        this.turn.removePLayer(playerId);
    }


    private void assignCharacters() {
        var availableCharacters = players.stream()
                .map(PersistentPlayer::getCharacter)
                .collect(Collectors.toList());

        final var random = new Random();

        for (int i = availableCharacters.size() - 1; i >= 1; i--) {
            Collections.swap(availableCharacters, i, random.nextInt(i + 1));
        }

        for (int i = 0; i <= players.size() - 1; i++) {
            int j = 0;
            var player = players.get(i);
            if (availableCharacters.size() == 1) {
                if (player.getCharacter().equals(availableCharacters.get(j))) {
                    int randomNum = (int) (Math.random() * players.size() - 1);
                    player.setCharacter(players.get(randomNum).getCharacter());
                    players.get(randomNum).setCharacter(availableCharacters.get(j));
                } else {
                    player.setCharacter(availableCharacters.get(j));
                }
            } else {
                while (player.getCharacter().equals(availableCharacters.get(j)) && j < availableCharacters.size() - 1) {
                    j++;
                }
                player.setCharacter(availableCharacters.get(j));
            }
            availableCharacters.remove(j);
        }

    }

    private void cleanPlayersValues(List<PersistentPlayer> players) {
        players.forEach(randomPlayer -> {
            randomPlayer.setEnteredAnswer(false);
            randomPlayer.setEnteredQuestion(false);
            randomPlayer.setPlayerQuestion(null);
            randomPlayer.setPlayerAnswer(null);
        });
    }

    public HistoryChat getHistory() {
        return history;
    }

    public String getCurrentQuestion(String playerId) {
        for (var player : this.players) {
            if (player != null && player.getId().equals(playerId)) {
                if (player.getPlayerQuestion() == null) {
                    return "Player " + player.getNickname() + " didn't ask";
                }
                return player.getNickname() + " asked: " + player.getPlayerQuestion();
            }
        }
        throw new PlayerNotFoundException("Player is not found");
    }

    public String getCurrentAnswer(String playerId) {
        for (var player : this.players) {
            if (player != null && player.getId().equals(playerId)) {
                if (player.getPlayerAnswer() == null) {
                    return "Player " + player.getNickname() + " didn't answer";
                }
                return player.getNickname() + " answered: " + player.getPlayerAnswer();
            }
        }
        throw new PlayerNotFoundException("Player is not found");
    }

    public boolean inactivePlayer(String playerId) {
        var inactivePlayer = this.players
                .stream()
                .filter(player -> player.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new PlayerNotFoundException(String.format(PLAYER_NOT_FOUND, playerId)));

        if (inactivePlayer.getPlayerState().equals(PlayerState.ANSWER_QUESTION) || inactivePlayer.getPlayerState().equals(PlayerState.ANSWER_GUESS)) {
            if (this.turn.getCurrentPlayer().isEnteredQuestion()) {
                inactivePlayer.incrementInactiveCounter();

                if (this.turn.getCurrentPlayer().getPlayerState().equals(PlayerState.ASK_QUESTION)) {
                    answerQuestion(playerId, QuestionAnswer.DONT_KNOW);
                } else if (this.turn.getCurrentPlayer().getPlayerState().equals(PlayerState.GUESSING)) {
                    answerGuessingQuestion(playerId, QuestionAnswer.DONT_KNOW);
                }
            }
            return inactivePlayer.getInactiveCounter() == 3;
        }
        if(inactivePlayer.getPlayerState().equals(PlayerState.ASK_QUESTION) || inactivePlayer.getPlayerState().equals(PlayerState.GUESSING)){
            this.playersLeft = this.players.size() - 1;
        }
        return true;
    }


    public void makingWinner(String playerId) {
        var leavingPlayer = this.players
                .stream()
                .filter(player -> player.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new PlayerNotFoundException(String.format(PLAYER_NOT_FOUND, playerId)));

        if (!(leavingPlayer.getPlayerState().equals(PlayerState.GAME_WINNER))) {
            for (var player : this.players) {
                if (!player.getId().equals(playerId)) {
                    player.setPlayerState(PlayerState.GAME_WINNER);
                    this.winners.add(player);
                }
            }
        }
    }

}
