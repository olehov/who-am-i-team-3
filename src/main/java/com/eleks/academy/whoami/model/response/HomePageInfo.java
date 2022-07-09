package com.eleks.academy.whoami.model.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("games/all-players-count")
@AllArgsConstructor
@NoArgsConstructor
public class HomePageInfo {

    private int playersOnline;

    @PostMapping
    public void changePlayersOnline(int playersOnline){
        this.playersOnline = playersOnline;
    }

    @GetMapping
    public int playersOnlineInfo(){
        return this.playersOnline;
    }
}
