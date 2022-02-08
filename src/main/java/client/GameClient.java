package client;

import api.GameServer;
import api.Gamer;
import server.OpeMsg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.rmi.RemoteException;

public class GameClient extends JFrame {


    /**
     * 客户端信息
     */
    String my_name = "gamer";
    int my_gamerID = -1;
    String serverAddr;
    Gamer gamer;
    GameServer server;
    ConnectAction connectAction = new ConnectAction();
    ConnectDialog dlg = new ConnectDialog(this);

    /**
     * 硬币数
     */
    static int me_coin = 4;
    static int left_coin = 4;
    static int right_coin = 4;

    /**
     * id和名字
     */
    String left_name = "player1";
    int left_gamerID = -1;

    String right_name = "player2";
    int right_gamerID = -1;

    /**
     * 准备情况
     */
    int left_ready = 0;
    int right_ready = 0;
    int me_ready = 0;

    /**
     * 第几轮
     */
    int round = 0;

    /**
     * 当前手牌点数
     */
    int me_cardpoint = 0;
    int me_newPoint = 0;


    /**
     * 界面组件
     */
    JLabel[] roundLabel_ = new JLabel[7]; //第1....7回合
    JTextArea textArea;     //游戏规则
    JTextArea textHistory;  //历史记录
    JButton leftPicBtn;        //左侧玩家头像
    JButton rightPicBtn;    //右侧玩家头像
    JButton mePicBtn;        //我的头像
    JLabel leftLabel;        //左侧玩家名称
    JLabel rightLabel;        //右侧玩家名称
    JLabel meLabel;            //我的名称
    JLabel leftCoinLabel;    //左侧硬币数量
    JLabel rightCoinLabel;    //右侧硬币数量
    JLabel meCoinLabel;        //我的硬币数量
    JButton leftThrowBtn;    //左侧弃牌
    JButton rightThrowBtn;    //右侧弃牌
    JLabel leftStatusLabel;    //左侧状态（已准备/加入赌局/pass）
    JLabel rightStatusLabel;//右侧状态
    JButton btnDrawBtn;        //摸牌按钮
    JButton meThrowBtn;        //我的弃牌
    JButton btnHandCard2;    //手牌2
    JButton btnHandCard;    //手牌1
    JButton btnOmiReq;        //请求新小早川牌按钮
    JButton btnJoin;        //加入赌局按钮
    JButton btnPass;        //PASS按钮
    JButton omiCardBtn;        //中间的小早川牌
    JButton btnReadyCancel;    //取消准备按钮
    JButton btnReady;        //准备按钮
    JButton leftOpenCardBtn;//左侧开牌图案
    JButton rightOpenCardBtn;//右侧开牌图案
    JLabel opeLabel;        //要求玩家操作的文字


    /**
     * 界面代码
     */
    public GameClient() {

        super("OmiCard-客户端");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1012, 700);
        getContentPane().setLayout(null);

        setupMenu();//菜单
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });


        textArea = new JTextArea();
        textArea.setText("");
        /**
         * 每位玩家开局4枚硬币
         一共进行七局游戏，赌注为1枚硬币，最后一
         局赌注为2枚硬币
         最后拥有硬币最多者获胜
         1.翻开一张小早川牌并给每位玩家发一张底牌
         2.从起始玩家开始选择摸一张牌并保留其中一
         张或发开一张牌成为新的小早川牌
         3.从起始玩家开始决定是否用一枚硬币参与赌
         局
         4.开牌，点数最小的玩家可以加上小早川牌的
         点数然后比大小，点数最大的玩家拿去所有硬
         币并从银行拿去相应的硬币
         * */
        textArea.append("每位玩家开局4枚硬币\n");
        textArea.append("一共进行七局游戏，赌注为1枚硬币，最后一\n");
        textArea.append("局赌注为2枚硬币\n");
        textArea.append("最后拥有硬币最多者获胜\n");
        textArea.append("1.翻开一张小早川牌并给每位玩家发一张底牌\n");
        textArea.append("2.从起始玩家开始选择摸一张牌并保留其中一\n");
        textArea.append("张或发开一张牌成为新的小早川牌\n");
        textArea.append("3.从起始玩家开始决定是否用一枚硬币参与赌\n");
        textArea.append("局\n");
        textArea.append("4.开牌，点数最小的玩家可以加上小早川牌的\n");
        textArea.append("点数然后比大小，点数最大的玩家拿去所有硬\n");
        textArea.append("币并从银行拿去相应的硬币");

        textArea.setEditable(false);
        textArea.setBounds(761, 43, 218, 253);
        getContentPane().add(textArea);

        textHistory = new JTextArea();
        textHistory.setBounds(761, 347, 218, 269);
        getContentPane().add(textHistory);

        JLabel lblNewLabel = new JLabel("\u6E38\u620F\u89C4\u5219");
        lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 17));
        lblNewLabel.setBounds(834, 15, 89, 21);
        getContentPane().add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("\u8BB0\u5F55");
        lblNewLabel_1.setFont(new Font("宋体", Font.PLAIN, 17));
        lblNewLabel_1.setBounds(846, 311, 63, 21);
        getContentPane().add(lblNewLabel_1);

        JPanel panel = new JPanel();
        panel.setBounds(15, 15, 727, 601);
        getContentPane().add(panel);
        panel.setLayout(null);

        roundLabel_[0] = new JLabel("\u7B2C1\u56DE\u5408>>");
        roundLabel_[0].setFont(new Font("宋体", Font.PLAIN, 17));
        roundLabel_[0].setEnabled(false);
        roundLabel_[0].setBounds(15, 15, 81, 35);
        panel.add(roundLabel_[0]);

        roundLabel_[1] = new JLabel("\u7B2C2\u56DE\u5408>>");
        roundLabel_[1].setFont(new Font("宋体", Font.PLAIN, 17));
        roundLabel_[1].setEnabled(false);
        roundLabel_[1].setBounds(106, 15, 81, 35);
        panel.add(roundLabel_[1]);

        roundLabel_[2] = new JLabel("\u7B2C3\u56DE\u5408>>");
        roundLabel_[2].setFont(new Font("宋体", Font.PLAIN, 17));
        roundLabel_[2].setEnabled(false);
        roundLabel_[2].setBounds(198, 15, 81, 35);
        panel.add(roundLabel_[2]);

        roundLabel_[3] = new JLabel("\u7B2C4\u56DE\u5408>>");
        roundLabel_[3].setFont(new Font("宋体", Font.PLAIN, 17));
        roundLabel_[3].setEnabled(false);
        roundLabel_[3].setBounds(294, 15, 81, 35);
        panel.add(roundLabel_[3]);

        roundLabel_[4] = new JLabel("\u7B2C5\u56DE\u5408>>");
        roundLabel_[4].setFont(new Font("宋体", Font.PLAIN, 17));
        roundLabel_[4].setEnabled(false);
        roundLabel_[4].setBounds(390, 15, 81, 35);
        panel.add(roundLabel_[4]);

        roundLabel_[5] = new JLabel("\u7B2C6\u56DE\u5408>>");
        roundLabel_[5].setFont(new Font("宋体", Font.PLAIN, 17));
        roundLabel_[5].setEnabled(false);
        roundLabel_[5].setBounds(486, 15, 81, 35);
        panel.add(roundLabel_[5]);

        roundLabel_[6] = new JLabel("\u7B2C7\u56DE\u5408");
        roundLabel_[6].setFont(new Font("宋体", Font.PLAIN, 17));
        roundLabel_[6].setEnabled(false);
        roundLabel_[6].setBounds(577, 15, 81, 35);
        panel.add(roundLabel_[6]);

        leftPicBtn = new JButton("");
        leftPicBtn.setBounds(15, 95, 123, 123);
        leftPicBtn.setIcon(backAddress(20, 123, 123));
        panel.add(leftPicBtn);

        rightPicBtn = new JButton("");
        rightPicBtn.setBounds(589, 95, 123, 123);
        rightPicBtn.setIcon(backAddress(20, 123, 123));
        panel.add(rightPicBtn);

        mePicBtn = new JButton("");
        mePicBtn.setBounds(15, 463, 123, 123);
        mePicBtn.setIcon(backAddress(20, 123, 123));
        panel.add(mePicBtn);

        leftLabel = new JLabel("player2");
        leftLabel.setBounds(15, 233, 123, 29);
        panel.add(leftLabel);

        rightLabel = new JLabel("player3");
        rightLabel.setBounds(589, 237, 123, 29);
        panel.add(rightLabel);

        meLabel = new JLabel("player1");
        meLabel.setBounds(153, 464, 123, 29);
        panel.add(meLabel);

        leftCoinLabel = new JLabel("\u786C\u5E01: ");
        leftCoinLabel.setBounds(15, 277, 81, 21);
        panel.add(leftCoinLabel);

        rightCoinLabel = new JLabel("\u786C\u5E01: ");
        rightCoinLabel.setBounds(589, 277, 81, 21);
        panel.add(rightCoinLabel);

        meCoinLabel = new JLabel("\u786C\u5E01: ");
        meCoinLabel.setBounds(153, 496, 81, 21);
        panel.add(meCoinLabel);

        leftThrowBtn = new JButton("");
        leftThrowBtn.setBounds(153, 130, 81, 117);
        leftThrowBtn.setVisible(false);
        leftThrowBtn.addActionListener(new LeftThrowBtnListener());//？？？
        panel.add(leftThrowBtn);

        rightThrowBtn = new JButton("");
        rightThrowBtn.setBounds(486, 130, 81, 117);
        rightThrowBtn.setVisible(false);
        rightThrowBtn.addActionListener(new RightThrowBtnListener());//？？？
        panel.add(rightThrowBtn);

        leftStatusLabel = new JLabel("\u72B6\u6001");
        leftStatusLabel.setBounds(153, 94, 126, 21);
        panel.add(leftStatusLabel);

        rightStatusLabel = new JLabel("\u72B6\u6001");
        rightStatusLabel.setBounds(483, 95, 101, 21);
        panel.add(rightStatusLabel);

        btnDrawBtn = new JButton("\u6478\u724C");
        btnDrawBtn.setToolTipText("");
        btnDrawBtn.setBounds(419, 387, 123, 53);
        btnDrawBtn.setVisible(false);
        btnDrawBtn.addActionListener(new BtnDrawBtnListener());
        panel.add(btnDrawBtn);

        meThrowBtn = new JButton("");
        meThrowBtn.setBounds(106, 331, 81, 117);
        meThrowBtn.setVisible(false);
        panel.add(meThrowBtn);

        btnHandCard2 = new JButton("");
        btnHandCard2.setBounds(271, 444, 93, 142);
        btnHandCard2.addActionListener(new BtnHandCard2Listener());
        btnHandCard2.setVisible(false);
        panel.add(btnHandCard2);

        btnHandCard = new JButton("");
        btnHandCard.setBounds(390, 444, 93, 142);
        btnHandCard.addActionListener(new BtnHandCardListener());
        btnHandCard.setVisible(false);
        panel.add(btnHandCard);

        btnOmiReq = new JButton("\u65B0\u5C0F\u65E9\u5DDD\u724C");
        btnOmiReq.setToolTipText("");
        btnOmiReq.setBounds(262, 387, 123, 53);
        btnOmiReq.addActionListener(new BtnOmiReqListener());
        btnOmiReq.setVisible(false);
        panel.add(btnOmiReq);

        btnJoin = new JButton("\u52A0\u5165\u8D4C\u5C40");
        btnJoin.setToolTipText("");
        btnJoin.setBounds(252, 378, 123, 53);
        btnJoin.setVisible(false);
        btnJoin.addActionListener(new BtnJoinListener());
        panel.add(btnJoin);

        btnPass = new JButton("PASS");
        btnPass.setToolTipText("");
        btnPass.setBounds(399, 378, 123, 53);
        btnPass.setVisible(false);
        btnPass.addActionListener(new BtnPassListener());
        panel.add(btnPass);

        omiCardBtn = new JButton();
        omiCardBtn.setBounds(280, 151, 123, 163);


        omiCardBtn.setIcon(backAddress(0, 123, 163));
        panel.add(omiCardBtn);

        JLabel lblNewLabel_5 = new JLabel("\u5C0F\u65E9\u5DDD\u724C");
        lblNewLabel_5.setFont(new Font("华文隶书", Font.PLAIN, 16));
        lblNewLabel_5.setBounds(311, 130, 81, 21);
        panel.add(lblNewLabel_5);

        btnReadyCancel = new JButton("\u53D6\u6D88\u51C6\u5907");
        btnReadyCancel.setToolTipText("");
        btnReadyCancel.setBounds(238, 387, 123, 53);
        btnReadyCancel.addActionListener(new BtnReadyCancelListener());
        btnReadyCancel.setVisible(false);
        panel.add(btnReadyCancel);

        btnReady = new JButton("\u51C6\u5907");
        btnReady.setToolTipText("");
        btnReady.setBounds(390, 387, 123, 53);
        btnReady.addActionListener(new BtnReadyListener());
        btnReady.setVisible(false);
        panel.add(btnReady);

        leftOpenCardBtn = new JButton("");
        leftOpenCardBtn.setBounds(39, 72, 81, 117);
        leftOpenCardBtn.setVisible(false);
        panel.add(leftOpenCardBtn);

        rightOpenCardBtn = new JButton("");
        rightOpenCardBtn.setBounds(611, 72, 81, 117);
        rightOpenCardBtn.setVisible(false);
        panel.add(rightOpenCardBtn);

        opeLabel = new JLabel("请左上角点击连接到服务器");
        opeLabel.setFont(new Font("宋体", Font.ITALIC, 20));
        opeLabel.setBounds(224, 335, 318, 45);
        panel.add(opeLabel);
        //界面代码
        try {
            gamer = new GamerImpl(this);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }


    private void setupMenu() {
        // TODO Auto-generated method stub
        JMenuBar menuBar = new JMenuBar();
        JMenuItem conn = new JMenuItem(connectAction);
        JMenuItem exit = new JMenuItem("退出");
        exit.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                exit();
            }
        });
        JMenu file = new JMenu("客户端");
        file.add(conn);
        menuBar.add(file);
        setJMenuBar(menuBar);
    }

    private void exit() {
        destroy();
        System.exit(0);
    }

    public void destroy() {
        try {
            disconnect();
        } catch (java.rmi.RemoteException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * ActionListener
     */
    class LeftThrowBtnListener implements ActionListener {//左侧弃牌*

        @Override
        public void actionPerformed(ActionEvent e) {
        }
    }

    class RightThrowBtnListener implements ActionListener {//右侧弃牌*

        @Override
        public void actionPerformed(ActionEvent e) {
        }
    }

    class BtnDrawBtnListener implements ActionListener {//摸牌按钮*

        @Override
        public void actionPerformed(ActionEvent e) {
            btnOmiReq.setVisible(false);
            btnDrawBtn.setVisible(false);
            opeLabel.setText("请选择一张牌展示后弃置");
            try {
                server.drawCard(my_gamerID);
            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }
    }

    class BtnHandCard2Listener implements ActionListener {//手牌2*

        @Override
        public void actionPerformed(ActionEvent e) {
            btnHandCard2.setVisible(false);         //选择了手牌2 手牌1不可用 手牌2隐藏 是打算弃置手牌2

            btnHandCard.setEnabled(false);
            try {
                server.throwCard(my_gamerID, me_cardpoint, me_newPoint);
            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            int t = me_newPoint;
            //meThrowBtn.setText(""+t);
            meThrowBtn.setIcon(backAddress(t, 81, 117));
            meThrowBtn.setVisible(true);
            meThrowBtn.setEnabled(false);
            me_cardpoint = me_newPoint; // 更新点数
            opeLabel.setText("等待其他玩家...");

        }
    }

    class BtnHandCardListener implements ActionListener {    //手牌1*
        @Override
        public void actionPerformed(ActionEvent e) {
            btnHandCard2.setEnabled(false);     //选择了手牌1 手牌2不可用 手牌1隐藏是打算弃置手牌1
            btnHandCard.setVisible(false);
            try {
                server.throwCard(my_gamerID, me_newPoint, me_cardpoint);
            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            //meThrowBtn.setText(""+me_cardpoint);
            meThrowBtn.setIcon(backAddress(me_cardpoint, 81, 117));
            meThrowBtn.setVisible(true);
            meThrowBtn.setEnabled(false);
            opeLabel.setText("等待其他玩家...");
        }
    }

    class BtnOmiReqListener implements ActionListener {//请求新小早川牌按钮*

        @Override
        public void actionPerformed(ActionEvent e) {
            btnOmiReq.setVisible(false);
            btnDrawBtn.setVisible(false);
            opeLabel.setText("等待其他玩家...");
            try {
                server.turnCard(my_gamerID);
            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    class BtnJoinListener implements ActionListener {//加入赌局按钮*

        @Override
        public void actionPerformed(ActionEvent e) {
            opeLabel.setText("加入赌局...等待其他玩家...");
            me_coin--;
            refreshCoins();
            btnJoin.setVisible(false);
            btnPass.setVisible(false);
            try {
                server.joinGame(my_gamerID);
            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    class BtnPassListener implements ActionListener {//PASS按钮*

        @Override
        public void actionPerformed(ActionEvent e) {
            opeLabel.setText("PASS...等待其他玩家...");
            btnJoin.setVisible(false);
            btnPass.setVisible(false);
            try {
                server.pass(my_gamerID);
            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    class BtnReadyCancelListener implements ActionListener {//取消准备按钮*

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                server.ready(my_gamerID, false);
            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            btnReady.setEnabled(true);
            btnReadyCancel.setEnabled(false);
            opeLabel.setText("请准备开始游戏");
        }
    }

    class BtnReadyListener implements ActionListener {//准备按钮*

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                server.ready(my_gamerID, true);
            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            btnReady.setEnabled(false);
            btnReadyCancel.setEnabled(true);
            opeLabel.setText("等待其他玩家...");
        }
    }


    /**
     * 主函数
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    GameClient frame = new GameClient();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 服务器---->客户端
     */
    public void receiveEnter(String name, int gamerID) {
        System.out.println("now is receiveEnter() going");
        textHistory.append("玩家" + name + "进入房间,id为" + gamerID + "\n");
        // TODO Auto-generated method stub
        /**
         * 1   2        0   1       2   0
         *   0            2           1
         *   三种情况
         * */
        if (my_gamerID == 0) {
            if (gamerID == 1) {
                left_gamerID = gamerID;
                left_name = name;
                //textHistory.append("玩家"+name+"进入房间\n");
            } else if (gamerID == 2) {
                right_gamerID = gamerID;
                right_name = name;
                //textHistory.append("玩家"+name+"进入房间\n");
            } else {
                System.out.println("receiveEnter发送错误 at mygameID ==0");
            }

        } else if (my_gamerID == 2) {
            if (gamerID == 0) {
                left_gamerID = gamerID;
                left_name = name;
                //textHistory.append("玩家"+name+"进入房间\n");
            } else if (gamerID == 1) {
                right_gamerID = gamerID;
                right_name = name;
                //textHistory.append("玩家"+name+"进入房间\n");
            } else {
                System.out.println("receiveEnter发送错误 at mygameID ==2");
            }


        } else if (my_gamerID == 1) {
            if (gamerID == 2) {
                left_gamerID = gamerID;
                left_name = name;
            } else if (gamerID == 0) {
                right_gamerID = gamerID;
                right_name = name;
            } else {
                System.out.println("receiveEnter发送错误 at mygameID ==1");
            }


        } else {
            System.out.println("receiveEnter发送错误 at home");
        }


        refresh();

    }


    public void receiveExit(String name, int gamerID) {
        System.out.println("now is receiveexit() going");
        textHistory.append("玩家" + name + "离开房间\n");
        // TODO Auto-generated method stub
        if (left_gamerID == gamerID) {
            left_gamerID = -1;
            left_name = "player1";
            left_ready = 0;
        } else if (right_gamerID == gamerID) {
            right_gamerID = -1;
            right_name = "player2";
            right_ready = 0;
        }

        refresh();


    }

    public void reReady(int gamerID, boolean readyready) {
        System.out.println("now is reready() going");
        textHistory.append("第" + gamerID + "号玩家已准备\n");
        // TODO Auto-generated method stub
        if (left_gamerID == gamerID) {
            left_ready = readyready ? 1 : 0;
        } else if (right_gamerID == gamerID) {
            right_ready = readyready ? 1 : 0;
        }

        refresh();

    }

    public void keepGoing(int stage) {
        // TODO Auto-generated method stub
        /**
         * stage = 0 游戏开始 清空桌面 开始新的一轮 stage 1 一阶段 stage2 二阶段
         * */
        if (stage == 0) {

            if (round == 7) {
                round = 0;
                me_coin = 4;
                left_coin = 4;
                right_coin = 4;
                for (int i = 0; i < 7; i++) {
                    roundLabel_[i].setEnabled(false);
                }

            }

            textHistory.append("现在开始第" + (round + 1) + "回合\n");
            roundLabel_[round++].setEnabled(true);
            leftStatusLabel.setText("");
            rightStatusLabel.setText("");
            omiCardBtn.setIcon(backAddress(0, 123, 163)); // 小早川图案
            opeLabel.setText("等待其他玩家...");
            btnReadyCancel.setVisible(false);
            btnReady.setVisible(false);
            meThrowBtn.setVisible(false);
            leftThrowBtn.setVisible(false);
            rightThrowBtn.setVisible(false);
            btnHandCard2.setVisible(false);
            btnHandCard.setVisible(false);
            leftPicBtn.setVisible(true);
            rightPicBtn.setVisible(true);
            leftOpenCardBtn.setVisible(false);
            rightOpenCardBtn.setVisible(false);


        } else if (stage == 1) {
            btnOmiReq.setVisible(true);
            btnDrawBtn.setVisible(true);
            opeLabel.setText("到你了，请选择一项操作：");


        } else if (stage == 2) {
            btnJoin.setVisible(true);
            btnPass.setVisible(true);
            opeLabel.setText("到你了，请选择是否用1枚硬币加入赌局:");
            if (round == 7) opeLabel.setText("到你了，请选择是否用2枚硬币加入赌局:");
            if (me_coin > 0) {
                if (round != 7) {
                    //当非最终局时 且硬币数量不为0
                    btnJoin.setEnabled(true);
                } else if (me_coin >= 2) {
                    btnJoin.setEnabled(true);
                } else {
                    btnJoin.setEnabled(false);
                }
            } else {
                btnJoin.setEnabled(false);
            }


        }


    }

    public void reOpe1FromOther(OpeMsg msg) {
        // TODO Auto-generated method stub

    }

    public void reOpe2FromOther(int tgamerID, int inorout) {
        // TODO Auto-generated method stub
        //textHistory.append(msg.getTextmsg());
        //msg内容为谁加入了赌局
        //即谁扣除相应的硬币数
        //并加入状态“加入赌局/或PASS"
        int tid = tgamerID;
        int type = inorout;
        if (type == 1) {//1加入赌局
            if (tid == left_gamerID) {
                left_coin--;
                leftStatusLabel.setText("加入赌局");
                textHistory.append("第" + tgamerID + "号玩家加入赌局\n");
                if (round == 7) left_coin--;//第7回合扣两个币

            } else if (tid == right_gamerID) {
                right_coin--;
                textHistory.append("第" + tgamerID + "号玩家加入赌局\n");
                rightStatusLabel.setText("加入赌局");
                if (round == 7) right_coin--;

            }
            refreshCoins();
        } else if (type == 2) {//2PASS
            if (tid == left_gamerID) {
                leftStatusLabel.setText("PASS");
                textHistory.append("第" + tgamerID + "号玩家选择PASS\n");

            } else if (tid == right_gamerID) {
                rightStatusLabel.setText("PASS");
                textHistory.append("第" + tgamerID + "号玩家选择PASS\n");

            }
        }

    }

    public void theOmiCard(int cardPoints) {//更新小早川牌
        // TODO Auto-generated method stub
        //omiCardBtn.setText(""+cardPoints);
        omiCardBtn.setIcon(backAddress(cardPoints, 123, 163));
        textHistory.append("小早川牌为" + cardPoints + "\n");
        //history代码


    }

    public void reTheOriCard(int cardPoints) {//接受底牌
        // TODO Auto-generated method stub
        btnHandCard.setVisible(true);
        //btnHandCard.setText(""+cardPoints);
        btnHandCard.setIcon(backAddress(cardPoints, 93, 142));
        btnHandCard.setEnabled(false);
        me_cardpoint = cardPoints;
        textHistory.append("您收到的牌为" + cardPoints + "\n");

        //history代码
    }

    public void reTheNewCard(int cardPoints) {//接受新底牌
        // TODO Auto-generated method stub
        me_newPoint = cardPoints;
        btnHandCard2.setVisible(true);
        //btnHandCard2.setText(""+cardPoints);
        btnHandCard2.setIcon(backAddress(cardPoints, 93, 142));
        btnHandCard.setEnabled(true);
        btnHandCard2.setEnabled(true);
        textHistory.append("您收到的新牌为" + cardPoints + "\n");


    }

    public void reThrowCardFromOther(int gamerID, int cardPoints) {
        // TODO Auto-generated method stub
        //接受其他玩家的弃牌
        textHistory.append("第" + gamerID + "号玩家弃置了" + cardPoints + "\n");
        if (left_gamerID == gamerID) {
            leftThrowBtn.setVisible(true);
            leftThrowBtn.setEnabled(false);
            //leftThrowBtn.setText(""+cardPoints);
            leftThrowBtn.setIcon(backAddress(cardPoints, 81, 117));

        } else if (right_gamerID == gamerID) {
            rightThrowBtn.setVisible(true);
            rightThrowBtn.setEnabled(false);
            //rightThrowBtn.setText(""+cardPoints);
            rightThrowBtn.setIcon(backAddress(cardPoints, 81, 117));
        }


    }

    public void reOpenCardFromOther(int gamerID, int cardPoints) {
        // TODO Auto-generated method stub
        textHistory.append("开牌:第" + gamerID + "号玩家的底牌为" + cardPoints + "\n");
        if (left_gamerID == gamerID) {
            leftPicBtn.setVisible(false);
            leftOpenCardBtn.setVisible(true);
            //leftOpenCardBtn.setText(""+cardPoints);
            leftOpenCardBtn.setIcon(backAddress(cardPoints, 81, 117));
            opeLabel.setText("本回合游戏结束");
        } else if (right_gamerID == gamerID) {

            rightPicBtn.setVisible(false);
            rightOpenCardBtn.setVisible(true);
            //rightOpenCardBtn.setText(""+cardPoints);
            rightOpenCardBtn.setIcon(backAddress(cardPoints, 81, 117));
            opeLabel.setText("本回合游戏结束");
        }


    }

    public void rePointFromServer(int winnerid, int money) {
        // TODO Auto-generated method stub
        int winner = winnerid;
        String winName = "您";
        if (winner == left_gamerID) {
            winName = left_name;
            left_coin += money;
        } else if (winner == right_gamerID) {
            winName = right_name;
            right_coin += money;
        } else {
            me_coin += money;
        }
        refreshCoins();


        if (round < 7) {
            opeLabel.setText(winName + "获得了本轮游戏的胜利,准备进行下一回合");
            textHistory.append(winName + "获得了本轮游戏的胜利,准备进行下一回合");
        } else {
            String str = "";
            if (left_coin > right_coin) {
                //找出最大值
                if (left_coin > me_coin) {
                    str = left_name + "玩家拥有最多的硬币，获得了整局比赛的胜利！";
                } else {
                    str = "您拥有最多的硬币，获得了整局比赛的胜利！";
                }

            } else {

                if (right_coin > me_coin) {
                    str = right_name + "玩家拥有最多的硬币，获得了整局比赛的胜利！";
                } else {
                    str = "您拥有最多的硬币，获得了整局比赛的胜利！";
                }
            }
            textHistory.append(str + "\n");
        }

        left_ready = 0;
        right_ready = 0;
        me_ready = 0;


        btnReadyCancel.setVisible(true);
        btnReadyCancel.setEnabled(false);
        btnReady.setVisible(true);
        btnReady.setEnabled(true);


    }

    public void serverStop() {
        // TODO Auto-generated method stub
        JOptionPane.showMessageDialog(dlg, "与服务器失去连接");
        server = null;
        connectAction.setEnabled(false);

    }


    /**
     * 刷新面板
     */
    private void refresh() {
        // TODO Auto-generated method stub
        System.out.println("now is refresh() going");
        leftLabel.setText(left_name + " (" + left_gamerID + ")");
        rightLabel.setText(right_name + " (" + right_gamerID + ")");
        leftStatusLabel.setText((left_ready == 1) ? "已准备" : " ");
        rightStatusLabel.setText((right_ready == 1) ? "已准备" : " ");

        refreshCoins();

    }


    private void refreshCoins() {
        // TODO Auto-generated method stub
        meCoinLabel.setText("硬币：" + me_coin);
        leftCoinLabel.setText("硬币：" + left_coin);
        rightCoinLabel.setText("硬币：" + right_coin);

    }

    /**
     * 加载图片
     */
    public ImageIcon backAddress(int num, int w, int h) {
        URL resource = this.getClass().getResource("/image/" + num + ".png");
        String url;
        if (resource != null) {
            url = resource.getPath();
            url += "image/" + num + ".png";
        } else {
            url = System.getProperty("user.dir") + "/image/" + num + ".png";
        }
        ImageIcon icon1 = new ImageIcon(url);
        Image img = icon1.getImage();
        Image newimg = img.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH);

        return new ImageIcon(newimg);
    }


    /**
     * connect
     */
    public boolean connect() throws java.rmi.RemoteException, java.net.MalformedURLException, java.rmi.NotBoundException {
        server = (GameServer) java.rmi.Naming.lookup("//" + serverAddr + "/GameServer");
        boolean ans = server.login(my_name, gamer);
        return ans;
    }

    protected void disconnect() throws java.rmi.RemoteException {
        if (server != null)
            server.logout(my_name, my_gamerID);
    }


    /**
     * 客户端连接窗口
     */
    class ConnectAction extends AbstractAction {
        public ConnectAction() {
            super("连接");

        }

        public void actionPerformed(ActionEvent evt) {
            dlg.pack();
            dlg.setLocationRelativeTo(GameClient.this);
            dlg.setVisible(true);
            if (dlg.getValue() == JOptionPane.OK_OPTION) {
                try {
                    my_name = dlg.getUserName();
                    serverAddr = dlg.getServerAddr();
                    if (connect()) {
                        JOptionPane.showMessageDialog(dlg, "已连接");
                        System.out.println(my_name + " 已连接");
                        my_gamerID = server.getWhoId(my_name);            //获取gamerID
                        meLabel.setText(my_name + " （" + my_gamerID + ")");  //设置id
                        server.getOtherLogined(gamer);
                        opeLabel.setText("服务器已连接，请准备开始游戏");
                        btnReadyCancel.setVisible(true);
                        btnReady.setVisible(true);
                        btnReady.setEnabled(true);
                        btnReadyCancel.setEnabled(false);//出现准备和取消准备按钮
                        round = 0;
                    } else {
                        JOptionPane.showMessageDialog(dlg, "重复用户名/人数已达服务器上限");
                    }

                    //inputBox.setEditable(true);
                    //displayBox.setText("");

                    this.setEnabled(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(dlg, "连接失败");
                    System.out.println("不能连接到服务器");
                    return;
                }
            }
        }
    }

}
