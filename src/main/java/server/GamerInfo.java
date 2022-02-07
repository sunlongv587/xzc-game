package server;


import api.Gamer;

public class GamerInfo {
    private String name;
    //玩家用户名

    private int gamerID;
    //玩家唯一id

    private Gamer gamer;
    //远程客户端对象

    public GamerInfo(String name, int gamerID,Gamer gamer) {
        setName(name);
        setGamerID(gamerID);
        setGamer(gamer);
    }

    public String getName() {
        return name;
    }



    public void setName(String name) {
        this.name = name;
    }

    public int getGamerID() {
        return gamerID;
    }

    public void setGamerID(int gamerID) {
        this.gamerID = gamerID;
    }


    public Gamer getGamer() {
        return gamer;
    }

    public void setGamer(Gamer gamer) {
        this.gamer = gamer;
    }

}