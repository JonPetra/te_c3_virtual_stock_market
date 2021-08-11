import axios from 'axios';

const http = axios.create({
    baseURL: "http://localhost:8080"
});

export default {

    getGames(token, status) {
        return http.get(`/games/${status}`,{headers: {'Authorization': `Bearer ${token}`}});
    },
    createGame(game, token){
        return http.post('/games',
        game,{headers: {'Authorization': `Bearer ${token}`}});
    },
    gameInvite(id, invite, token){
        return http.post(`/games/${id}/invite`, invite,
        {headers: {'Authorization': `Bearer ${token}`}});
    },
    getLeaderboard(gameId, token) {
        return http.get(`/games/${gameId}/leaderboard`, {headers: {'Authorization': `Bearer ${token}`}});
    },
    getPortfolioValueHistory(gameId, token) {
        console.log(gameId);
        return http.get(`/games/${gameId}/visualizations`, {headers: {'Authorization': `Bearer ${token}`}});
    }
}