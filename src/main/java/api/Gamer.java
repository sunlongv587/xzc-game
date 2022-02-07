package api;

import java.rmi.RemoteException;

import server.OpeMsg;
import server.PointBand;
import server.OpeMsg;

public interface Gamer extends java.rmi.Remote {
    public void receiveEnter(String name,int gamerID) throws RemoteException;
    //用户加入
    public void receiveExit(String name,int gamerID) throws RemoteException;
    //用户离开

    public void reReady(int gamerID,boolean readyready) throws RemoteException;
    //接收信息 其他用户准备情况

    public void keepGoing(int stage) throws RemoteException;
    //服务器要求用户进行操作 stage =1 阶段1 stage 2 = 阶段2


    //public void receiveCard()
    //接收卡片（摸牌的牌、其他玩家的开牌、其他玩家弃置的牌）


    public void reOpe1FromOther(OpeMsg msg) throws RemoteException;
    //接收信息（其他玩家的阶段6的操作）
    public void reOpe2FromOther(int gamerID,int inorout) throws RemoteException;
    //接收信息（其他玩家的阶段8的操作）


    public void theOmiCard(int cardPoints) throws RemoteException;
    //小早川牌（阶段3、阶段6-2）接收该信息后替换小早川牌

    public void reTheOriCard(int cardPoints) throws RemoteException;
    //接收信息 （底牌）

    public void reTheNewCard(int cardPoints) throws RemoteException;
    //步骤6-1新摸到的牌

    public void reThrowCardFromOther(int gamerID,int cardPoints) throws RemoteException;
    //接收信息（其他玩家的弃置牌 步骤6-2）

    public void reOpenCardFromOther(int gamerID,int cardPoints) throws RemoteException;
    //接收其他玩家的开牌（阶段9）

    public void rePointFromServer(int winnerid,int meney) throws RemoteException;
    //包含第一次计算和第二次计算（阶段9）
    //展示胜利界面在客户端进行
    //包括硬币计算也在客户端进行

    public void serverStop() throws RemoteException;
    //当有玩家退出、掉线。其他情况 立即终止游戏





}

