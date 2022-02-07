package client;

import api.Gamer;
import server.OpeMsg;

public class GamerImpl extends java.rmi.server.UnicastRemoteObject implements Gamer {

    GameClient client;

    public GamerImpl(GameClient client) throws java.rmi.RemoteException {
        this.client = client;
    }

    public void receiveEnter(String name,int gamerID) throws java.rmi.RemoteException{
        client.receiveEnter(name,gamerID);
    }
    //用户加入
    public void receiveExit(String name,int gamerID) throws java.rmi.RemoteException{
        client.receiveExit(name,gamerID);
    }
    //用户离开

    public void reReady(int gamerID,boolean readyready) throws java.rmi.RemoteException{
        client.reReady(gamerID,readyready);
    }
    //接收信息 其他用户准备情况

    public void keepGoing(int stage) throws java.rmi.RemoteException{
        client.keepGoing(stage);
    }
    //服务器要求用户进行操作 stage =1 阶段1 stage 2 = 阶段2


    //public void receiveCard()
    //接收卡片（摸牌的牌、其他玩家的开牌、其他玩家弃置的牌）


    public void reOpe1FromOther(OpeMsg msg) throws java.rmi.RemoteException{
        client.reOpe1FromOther(msg);
    }
    //接收信息（其他玩家的阶段6的操作）
    public void reOpe2FromOther(int gamerID,int inorout) throws java.rmi.RemoteException{
        client.reOpe2FromOther(gamerID,inorout);
    }
    //接收信息（其他玩家的阶段8的操作）


    public void theOmiCard(int cardPoints) throws java.rmi.RemoteException{
        client.theOmiCard(cardPoints);
    }
    //小早川牌（阶段3、阶段6-2）接收该信息后替换小早川牌

    public void reTheOriCard(int cardPoints) throws java.rmi.RemoteException{
        client.reTheOriCard(cardPoints);
    }
    //接收信息 （底牌）

    public void reTheNewCard(int cardPoints) throws java.rmi.RemoteException{
        client.reTheNewCard(cardPoints);
    }
    //步骤6-1新摸到的牌

    public void reThrowCardFromOther(int gamerID,int cardPoints) throws java.rmi.RemoteException{
        client.reThrowCardFromOther(gamerID,cardPoints);
    }
    //接收信息（其他玩家的弃置牌 步骤6-2）

    public void reOpenCardFromOther(int gamerID,int cardPoints) throws java.rmi.RemoteException{
        client.reOpenCardFromOther(gamerID,cardPoints);
    }
    //接收其他玩家的开牌（阶段9）

    public void rePointFromServer(int winnerid,int money) throws java.rmi.RemoteException{
        client.rePointFromServer(winnerid,money);
    }
    //包含第一次计算和第二次计算（阶段9）
    //展示胜利界面在客户端进行
    //包括硬币计算也在客户端进行

    public void serverStop() throws java.rmi.RemoteException{
        client.serverStop();
    }
    //当有玩家退出、掉线。其他情况 立即终止游戏


}
