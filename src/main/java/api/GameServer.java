package api;

import java.rmi.RemoteException;

public interface GameServer extends java.rmi.Remote{
    public boolean login(String name,Gamer gamer) throws RemoteException;
    //注册新玩家 若有重复名字 则拒绝

    public void getOtherLogined(Gamer gamer) throws RemoteException;

    public int getWhoId(String name) throws RemoteException;

    public void logout(String name ,int gamerID) throws RemoteException;
    //用户退出

    public void ready(int gamerID, boolean readyready) throws RemoteException;
    //用户发送准备情况 true：准备完成 false：取消准备  3个人发送该指令则游戏开始

    public void drawCard(int gamerID) throws RemoteException;
    //用户请求摸牌（阶段6-1）

    public void throwCard(int gamerID,int chooseCard,int throwCard) throws RemoteException;
    //用户请求弃置牌（阶段6-1-2）

    public void turnCard(int gamerID) throws RemoteException;
    //用户请求翻小早川牌（阶段6-2）

    public void joinGame(int gamerID) throws RemoteException;
    //用户请求加入赌局（阶段8-1）

    public void pass(int gamerID) throws RemoteException;
    //用户请求pass


}