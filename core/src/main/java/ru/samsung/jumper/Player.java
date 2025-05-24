package ru.samsung.jumper;

public class Player {
    String name = "Noname";
    int score;


    public void clone(Player player){
        name = player.name;
        score = player.score;
    }

    public void clear(){
        name = "Noname";
        score = 0;
    }
}
