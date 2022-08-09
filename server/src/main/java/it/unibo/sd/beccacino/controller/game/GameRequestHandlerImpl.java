package it.unibo.sd.beccacino.controller.game;

import it.unibo.sd.beccacino.Game;
import it.unibo.sd.beccacino.GameRequest;
import it.unibo.sd.beccacino.Player;
import it.unibo.sd.beccacino.ResponseCode;
import org.bson.BsonValue;
import org.bson.Document;

public class GameRequestHandlerImpl implements GameRequestHandler {
    private final GameStub gameStub;
    private final GameUtil gameUtil;

    public GameRequestHandlerImpl(GameStub gameStub) {
        this.gameStub = gameStub;
        this.gameUtil = new GameUtilImpl();
    }

    @Override
    public void handleRequest(GameRequest request) {
        switch (request.getRequestType()) {
            case ("start") -> this.startGameRequestHandler(request);
            case ("briscola") -> this.setBriscolaRequestHandler(request);
            case ("play") -> this.makePlayRequestHandler(request);
            default -> this.gameStub.sendGameErrorResponse(ResponseCode.ILLEGAL_REQUEST, request.getRequestingPlayer(), "");
        }
    }

    private void startGameRequestHandler(GameRequest request) {
        if (gameUtil.doesLobbyExists(request.getLobby().getId())) {
            if (this.gameUtil.isLobbyFull(request)) {
                if (this.gameUtil.isPlayerLobbyLeader(request)) {
                    Document emptyGameDocument = this.gameUtil.createNewGame(request);
                    BsonValue insertResponse = this.gameUtil.insertGame(emptyGameDocument);
                    String createdGameID = insertResponse.asObjectId().getValue().toString();
                    if (!createdGameID.equals("")) {
                        Game createdGame = this.gameUtil.getGameById(createdGameID);
                        this.gameUtil.removeLobby(request.getLobby().getId());
                        this.gameStub.sendGameResponse(createdGame, ResponseCode.START_OK);
                    } else {
                        this.gameStub.sendGameErrorResponse(ResponseCode.START_ERROR, request.getRequestingPlayer(), "");
                    }
                } else {
                    this.gameStub.sendGameErrorResponse(ResponseCode.PERMISSION_DENIED, request.getRequestingPlayer(), "");
                }
            } else {
                this.gameStub.sendGameErrorResponse(ResponseCode.START_ERROR, request.getRequestingPlayer(), "");
            }
        } else {
            this.gameStub.sendGameErrorResponse(ResponseCode.ILLEGAL_REQUEST, request.getRequestingPlayer(), "");
        }
    }

    private void setBriscolaRequestHandler(GameRequest request) {
        Game game = this.gameUtil.getGameById(request.getGameId());
        Player requestingPlayer = request.getRequestingPlayer();
        if (this.gameUtil.isPlayerCurrentPlayer(game, requestingPlayer)) {
            if (!this.gameUtil.isBriscolaSet(game)) {
                boolean operationSuccessful = this.gameUtil.setBriscola(request);
                if (operationSuccessful) {
                    Game updatedGame = this.gameUtil.getGameById(request.getGameId());
                    this.gameStub.sendGameResponse(updatedGame, ResponseCode.BRISCOLA_OK);
                } else {
                    // TODO if something goes wrong, should we return null or the 'old' game?
                    this.gameStub.sendGameErrorResponse(ResponseCode.FAIL, requestingPlayer, game.getId());
                }
            } else {
                this.gameStub.sendGameErrorResponse(ResponseCode.ILLEGAL_REQUEST, requestingPlayer, game.getId());
            }
        } else {
            this.gameStub.sendGameErrorResponse(ResponseCode.PERMISSION_DENIED, requestingPlayer, game.getId());
        }
    }

    private void makePlayRequestHandler(GameRequest request) {
        Game game = this.gameUtil.getGameById(request.getGameId());
        if (this.gameUtil.isPlayerCurrentPlayer(game, request.getRequestingPlayer())) {
            if (this.gameUtil.isBriscolaSet(game)) {
                if (this.gameUtil.isCardPlayable(request)) {
                    boolean operationSuccessful = this.gameUtil.makePlay(request);
                    if (operationSuccessful) {
                        this.gameUtil.updateCurrentPlayer(request.getGameId());
                        this.gameUtil.computeWinnerAndSetNextPlayer(request.getGameId());
                        Game updatedGame = this.gameUtil.getGameById(request.getGameId());
                        this.gameStub.sendGameResponse(updatedGame, ResponseCode.PLAY_OK);
                    } else {
                        this.gameStub.sendGameErrorResponse(ResponseCode.FAIL, request.getRequestingPlayer(), game.getId());
                    }
                } else {
                    this.gameStub.sendGameErrorResponse(ResponseCode.ILLEGAL_REQUEST, request.getRequestingPlayer(), game.getId());
                }
            } else {
                this.gameStub.sendGameErrorResponse(ResponseCode.ILLEGAL_REQUEST, request.getRequestingPlayer(), game.getId());
            }
        } else {
            this.gameStub.sendGameErrorResponse(ResponseCode.PERMISSION_DENIED, request.getRequestingPlayer(), game.getId());
        }
    }
}
