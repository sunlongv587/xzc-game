package server;


import api.GameServer;
import api.Gamer;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameServerImpl extends java.rmi.server.UnicastRemoteObject implements GameServer {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    private final static String BINDNAME = "GameServer";
    /**
     * 开局前数据
     * */
    int loginGamer = 0;				//当等于3时，禁止登入
    int[] gamerIDHouse = {0,0,0};   //ID仓库
    int readyGamer = 0;				//当等于3时，游戏开始
    static GameServerImpl server = null; //供客户端调用
    List gamers = new ArrayList();     //玩家列表
    List listeners = new ArrayList();	//	监听
    protected GameServerImpl() throws java.rmi.RemoteException {

    }

    public static GameServerImpl getInstance() {
        try {
            if (server == null) {
                server = new GameServerImpl();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
        return server;
    }

    /**
     * 每轮牌局数据
     * */
    int[] cardHouse = new int[15]; 	//牌库 每轮游戏自动洗牌
    int cardHousePointer;				//牌库指针 到第几张牌
    int round;						//第几轮（1-7）
    int curGamerID;					//当前玩家 id 0 1 2
    int[] joinGamer = new int[3];	//加入赌局的玩家 下标为gamerID 0为pass 1为加入
    int[] GamersCard = new int[3];	//玩家手里的牌的点数
    int omiCard;					//小早川牌



    /**
     * 函数
     * */
    public int getWhoId(String name) throws java.rmi.RemoteException{//可能不会用到
        Iterator itr = gamers.iterator();
        while(itr.hasNext()) {//遍历 找id
            GamerInfo gamerinfo2 = (GamerInfo) itr.next();
            if(gamerinfo2.getName().equals(name)){
                return gamerinfo2.getGamerID();
            }
        }
        return -1;

    }




    public boolean login(String name, Gamer g) throws java.rmi.RemoteException{
        if(loginGamer == 3) return false; // 达到最大用户登录数 拒绝登录
        int tgamerID = 0;
        for(int i = 0;i<3;i++) {
            if(gamerIDHouse[i]==0) {
                tgamerID = i;			//获取唯一id
                gamerIDHouse[i] = 1;
                break;
            }
        }

        /**
         * gamerinfo :新登录玩家
         * gamerinfo2 :除新登录玩家外已登录玩家
         * */
        GamerInfo gamerinfo = new GamerInfo(name,tgamerID,g);
        Iterator itr = gamers.iterator();
        while(itr.hasNext()) {//发送给其他玩家新登录的用户
            GamerInfo gamerinfo2 = (GamerInfo) itr.next();
            gamerinfo2.getGamer().receiveEnter(name,tgamerID);
        }




        gamers.add(gamerinfo);

        loginGamer++;
        notifyListener(gamerinfo.getName() + " 进入房间",1);
        notifyListener(String.valueOf(loginGamer),2);//更新登录人数
        round = 0;
        return true;
    }

    public void getOtherLogined(Gamer g) throws java.rmi.RemoteException{
        //发送给新登录玩家已经登录的用户



        Iterator itr = gamers.iterator();
        while(itr.hasNext()) {//发送给新登录玩家已经登录的用户
            GamerInfo gamerinfo2 = (GamerInfo) itr.next();
            String tname2 = gamerinfo2.getName();
            int tgamerID2 = gamerinfo2.getGamerID();
            g.receiveEnter(tname2,tgamerID2);

        }

    }

    public void logout(String name,int gamerID) throws java.rmi.RemoteException {
        GamerInfo u_gone = null;
        Iterator itr = null;

        synchronized(gamers) {
            for(int i = 0;i<gamers.size();i++) {
                GamerInfo gamerinfo = (GamerInfo) gamers.get(i);
                if(gamerinfo.getName().equals(name)) {
                    u_gone = gamerinfo;
                    gamers.remove(i);
                    itr = gamers.iterator();
                    break;
                }
            }
        }

        gamerIDHouse[gamerID] = 0;




        while(itr.hasNext()) {//发送给其他玩家退出登录用户
            GamerInfo gamerinfo = (GamerInfo) itr.next();
            gamerinfo.getGamer().receiveExit(name,gamerID);
        }

        loginGamer--;
        notifyListener(name + "退出房间",1);
        notifyListener(String.valueOf(loginGamer),2);//更新登录人数


    }

    public void stop() throws RemoteException, NotBoundException, MalformedURLException {
        //notifyListener(STATEMSG[1]);
        Iterator itr = gamers.iterator();
        while (itr.hasNext()) {
            GamerInfo u = (GamerInfo) itr.next();
            u.getGamer().serverStop();
        }
        java.rmi.Naming.unbind(BINDNAME);
    }

    public void ready(int gamerID, boolean readyready) throws java.rmi.RemoteException{
        if(readyready) {
            readyGamer++;

            if(readyGamer==3) {
                //round = 0;
                gameStart();
                return;
            }
        }else {
            readyGamer--;
        }

        Iterator itr = gamers.iterator();
        while(itr.hasNext()) {//发送给其他玩家准备情况
            GamerInfo gamerinfo2 = (GamerInfo) itr.next();
            gamerinfo2.getGamer().reReady(gamerID,readyready);
        }
        notifyListener(gamerID + "号玩家"+(readyready?"已准备":"取消准备"),1);
        notifyListener(String.valueOf(readyGamer),3);//更新准备人数

    }




    private void gameStart() throws java.rmi.RemoteException{
        // TODO Auto-generated method stub
        notifyListener("所有玩家均准备就绪，游戏开始",1);
        round++;				//回合数
        curGamerID = 0;			//当前玩家id

        cardHouse = washCards(); //洗牌
        cardHousePointer = 0;
        omiCard  = cardHouse [cardHousePointer++]; //确定小早川牌

        Iterator itr = gamers.iterator();
        while(itr.hasNext()) {//发送给所有玩家小早川牌
            GamerInfo gamerinfo2 = (GamerInfo) itr.next();
            gamerinfo2.getGamer().keepGoing(0);		//keepGoing 阶段0 新一轮游戏开始
            gamerinfo2.getGamer().theOmiCard(omiCard);
        }

//		try {
//			Thread.sleep(5000);//暂停5s
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//


        for(int i = 0;i<3;i++) {
            //确定各个玩家的底牌
            GamersCard[i] = cardHouse [cardHousePointer++];
        }

        Iterator itr2 = gamers.iterator();

        while(itr2.hasNext()) {//发送给所有玩家底牌
            GamerInfo gamerinfo2 = (GamerInfo) itr2.next();
            int gamerID = gamerinfo2.getGamerID();
            gamerinfo2.getGamer().reTheOriCard(GamersCard[gamerID]);
        }

        Iterator itr3 = gamers.iterator();
        while(itr3.hasNext()) {//寻找id = 0 的玩家 向他发送keepGoing
            GamerInfo gamerinfo2 = (GamerInfo) itr3.next();
            int gamerID = gamerinfo2.getGamerID();
            if(gamerID == 0) {
                gamerinfo2.getGamer().keepGoing(1); // keepGoing 阶段1
                break;
            }

        }

        //gameStart end
    }

    private int[] washCards() {   // 洗牌代码
        // TODO Auto-generated method stub
        int[] arr = {1,3,5,7,9,2,4,6,8,10,11,13,12,14,15};
        int length = 15;
        int index = length - 1;

        for(int i = 0;i < length && index > 0 ; i++) {
            int num = (new Random().nextInt(index));
            int temp = arr[num];
            arr[num] = arr[index];
            arr[index] = temp;
            index --;
        }
        return arr;
    }

    public void drawCard(int gamerID) throws java.rmi.RemoteException{
        //用户请求摸牌阶段6-1
        GamersCard[gamerID] = cardHouse[cardHousePointer++];

        Iterator itr = gamers.iterator();
        while(itr.hasNext()) {//寻找id = gamerID 的玩家 向他发送新卡片
            GamerInfo gamerinfo2 = (GamerInfo) itr.next();
            int tgamerID = gamerinfo2.getGamerID();
            if(tgamerID == gamerID) {
                gamerinfo2.getGamer().reTheNewCard(GamersCard[gamerID]);//给用户发送步骤6-1新摸到的牌
                break;
            }

        }

        //reOpe1FromOther(OpeMsg msg);//发送给其他用户的信息


        //drawCard end
    }

    public void throwCard(int gamerID,int chooseCard,int throwCard) throws java.rmi.RemoteException{
        //用户弃置牌 阶段6-1-2
        notifyListener(gamerID + "号玩家选择了"+chooseCard+"作为自己的底牌"+"将"+throwCard+"弃置了",1);
        GamersCard [gamerID] = chooseCard; //将服务器的用户的卡替换为用户选择的卡

        Iterator itr = gamers.iterator();
        while(itr.hasNext()) {//发送给其他用户的信息 gamerID所弃置的卡片
            GamerInfo gamerinfo2 = (GamerInfo) itr.next();
            int tgamerID = gamerinfo2.getGamerID();
            if(tgamerID != gamerID) {
                gamerinfo2.getGamer().reThrowCardFromOther(gamerID,throwCard);

            }

        }

        if(curGamerID == 0) {					//寻找下一个玩家
            curGamerID = 1;
        }else if(curGamerID == 1) {
            curGamerID = 2;
        }else if(curGamerID == 2) {
            curGamerID = -1;


        }

        Iterator itr2 = gamers.iterator();
        while(itr2.hasNext()) {
            GamerInfo gamerinfo2 = (GamerInfo) itr2.next();
            int tgamerID = gamerinfo2.getGamerID();
            if(tgamerID == curGamerID) {
                gamerinfo2.getGamer().keepGoing(1);//给下一个玩家发送keepGoing

            }

        }

        if (curGamerID == -1) gameContinue();



    }


    public void turnCard(int gamerID) throws java.rmi.RemoteException{
        //用户请求新小早川牌 步骤6-2
        omiCard  = cardHouse[cardHousePointer++];

        Iterator itr = gamers.iterator();
        while(itr.hasNext()) {//发送给所有玩家小早川牌
            GamerInfo gamerinfo2 = (GamerInfo) itr.next();
            gamerinfo2.getGamer().theOmiCard(omiCard);
        }

        if(curGamerID == 0) {					//寻找下一个玩家
            curGamerID = 1;
        }else if(curGamerID == 1) {
            curGamerID = 2;
        }else if(curGamerID == 2) {
            curGamerID = -1;


        }

        Iterator itr2 = gamers.iterator();
        while(itr2.hasNext()) {
            GamerInfo gamerinfo2 = (GamerInfo) itr2.next();
            int tgamerID = gamerinfo2.getGamerID();
            if(tgamerID == curGamerID) {
                gamerinfo2.getGamer().keepGoing(1);//给下一个玩家发送keepGoing

            }

        }

        if (curGamerID == -1) gameContinue();

    }

    private void gameContinue() throws java.rmi.RemoteException {
        // TODO Auto-generated method stub
        curGamerID = 0;
        Iterator itr = gamers.iterator();
        while(itr.hasNext()) {
            GamerInfo gamerinfo2 = (GamerInfo) itr.next();
            int tgamerID = gamerinfo2.getGamerID();
            if(tgamerID == curGamerID) {
                gamerinfo2.getGamer().keepGoing(2);//给下一个玩家发送keepGoing stage 2
                break;
            }
        }

    }

    public void joinGame(int gamerID) throws java.rmi.RemoteException {
        //用户加入赌局
        joinGamer[gamerID] = 1;

        //reOpe2FromOther(OpeMsg msg);//发送给其他用户的信息2
        String tex = "玩家"+gamerID+"选择加入赌局";
        notifyListener(tex,1);
        OpeMsg msg = new OpeMsg(tex,gamerID,1);

        Iterator itr2 = gamers.iterator();
        while(itr2.hasNext()) {
            GamerInfo gamerinfo3 = (GamerInfo) itr2.next();
            int tgamerID = gamerinfo3.getGamerID();
            if(tgamerID != gamerID) {
                gamerinfo3.getGamer().reOpe2FromOther(gamerID,1);//发送给其他用户的信息2
                //break;
            }
        }

        if(curGamerID == 0) {					//寻找下一个玩家
            curGamerID = 1;
        }else if(curGamerID == 1) {
            curGamerID = 2;
        }else if(curGamerID == 2) {
            curGamerID = -1;

        }
        Iterator itr = gamers.iterator();
        while(itr.hasNext()) {
            GamerInfo gamerinfo2 = (GamerInfo) itr.next();
            int tgamerID = gamerinfo2.getGamerID();
            if(tgamerID == curGamerID) {
                gamerinfo2.getGamer().keepGoing(2);//给下一个玩家发送keepGoing stage 2
                break;
            }
        }

        if (curGamerID == -1) gameOver();

    }

    public void pass(int gamerID) throws java.rmi.RemoteException {
        joinGamer[gamerID] = 0;

        if(curGamerID == 0) {					//寻找下一个玩家
            curGamerID = 1;
        }else if(curGamerID == 1) {
            curGamerID = 2;
        }else if(curGamerID == 2) {
            curGamerID = -1;

        }
        Iterator itr = gamers.iterator();
        while(itr.hasNext()) {
            GamerInfo gamerinfo2 = (GamerInfo) itr.next();
            int tgamerID = gamerinfo2.getGamerID();
            if(tgamerID == curGamerID) {
                gamerinfo2.getGamer().keepGoing(2);//给下一个玩家发送keepGoing stage 2
                break;
            }
        }

        if (curGamerID == -1) gameOver();

    }


    private void gameOver() throws java.rmi.RemoteException{
        // TODO Auto-generated method stub
        Iterator itr = gamers.iterator();
        while(itr.hasNext()) {                   //向所有玩家 发送 参与赌局的玩家的底牌
            GamerInfo gamerinfo2 = (GamerInfo) itr.next();
            int tgamerID = gamerinfo2.getGamerID();
            for(int i = 0 ;i<3;i++) {
                //遍历参与赌局玩家的数组
                if(joinGamer[i]==1) {
                    //若i参与赌局
                    //则向除i外的玩家发送其底牌
                    if(tgamerID != i) {
                        gamerinfo2.getGamer().reOpenCardFromOther(i,GamersCard[i]);
                    }
                }
            }

        }



        /**
         * 计算结果
         * 收集所有参与赌局的人的底牌
         * 找出最小的 加上 小早川牌
         * 再找出最大的 获得胜利
         *
         * */
        for(int i = 0;i<3;i++) {

            if(joinGamer[i]==0) {
                //对不参与赌局的人的牌+100;
                GamersCard[i]+=100;
            }
        }
        int min = FindMin(GamersCard);//找最小
        int minuser = 0;
        for(int i = 0;i<3;i++) {		//找最小的下标
            if(min == GamersCard[i]) {
                minuser = i;
                break;
            }
        }
        GamersCard[minuser]+=omiCard;	//最小的牌加上小早川牌
        for(int i = 0;i<3;i++) {

            if(joinGamer[i]==0) {
                //对不参与赌局的人的牌-200;
                GamersCard[i]-=200;
            }
        }

        int max = FindMax(GamersCard);//找最大
        int winner = 0;
        for(int i = 0;i<3;i++) {		//找最小的下标
            if(max == GamersCard[i]) {
                winner = i;
                break;
            }
        }

        int money = 0;
        if(round!=7) {
            money=1+joinGamer[0]+joinGamer[1]+joinGamer[2];//获得的奖金= 参与赌局的人的支出 每人1块+银行给的1块
        }else {
            money=1+(joinGamer[0]+joinGamer[1]+joinGamer[2])*2;
        }


        Iterator itr2 = gamers.iterator();
        while(itr2.hasNext()) {                   //向所有玩家 发送 本轮的最终结果
            GamerInfo gamerinfo2 = (GamerInfo) itr2.next();
            gamerinfo2.getGamer().rePointFromServer(winner,money);
        }


        //游戏终止 玩家需要重新准备
        readyGamer = 0;




    }

    private int FindMin(int[] gamersCard2) {
        // TODO Auto-generated method stub
        int l = gamersCard2.length;
        int[] ta = new int[l];
        for(int i = 0;i<l;i++) {
            ta[i] = gamersCard2[i];
        }

        for(int i = 0;i<l-1;i++) {
            for(int j = i+1;j<l;j++) {
                if(ta[i]>ta[j]) {
                    int t = ta[i];
                    ta[i] = ta[j];
                    ta[j] = t;
                }
            }
        }

        return ta[0];
    }

    private int FindMax(int[] gamersCard2) {
        // TODO Auto-generated method stub
        int l = gamersCard2.length;
        int[] ta = new int[l];
        for(int i = 0;i<l;i++) {
            ta[i] = gamersCard2[i];
        }

        for(int i = 0;i<l-1;i++) {
            for(int j = i+1;j<l;j++) {
                if(ta[i]<ta[j]) {
                    int t = ta[i];
                    ta[i] = ta[j];
                    ta[j] = t;
                }
            }
        }

        return ta[0];
    }

    public void addListener(GameServerListener listener) {
        listeners.add(listener);


    }

    public void removeListense(GameServerListener listener) {
        listeners.remove(listener);
    }

    void notifyListener(String msg,int msgType) {
        Iterator itr = listeners.iterator();
        GameServerEvent evt = new GameServerEvent(this, msg, msgType);
        while (itr.hasNext()) {
            ((GameServerListener) itr.next()).serverEvent(evt);
        }
    }


    public void start() throws RemoteException, MalformedURLException {
        java.rmi.Naming.rebind(BINDNAME, server);
        //notifyListener(STATEMSG[0]);
    }



}