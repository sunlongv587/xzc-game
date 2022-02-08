package server;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class ServerMain extends JFrame implements GameServerListener {


    GameServerImpl server = GameServerImpl.getInstance();
    JTextArea textArea;
    StarServerAction startAction = new StarServerAction();
    StopServerAction stopAction = new StopServerAction();
    JLabel serverStatusLabel;
    JLabel loginedLabel;
    JLabel readyedLabel;


    private JPanel contentPane;


    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ServerMain frame = new ServerMain();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public ServerMain() {

        try {
            LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


        setTitle("OmiCard\u670D\u52A1\u5668");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 442, 541);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JButton btnStartButton = new JButton("\u542F\u52A8\u670D\u52A1\u5668");
        btnStartButton.setBounds(15, 15, 186, 60);
        btnStartButton.addActionListener(new StarServerAction());
        contentPane.add(btnStartButton);

        JButton btnStopButton = new JButton("\u5173\u95ED\u670D\u52A1\u5668");
        btnStopButton.setBounds(216, 15, 186, 60);
        btnStopButton.addActionListener(stopAction);
        contentPane.add(btnStopButton);

        textArea = new JTextArea();
        textArea.setBounds(15, 90, 390, 364);
        contentPane.add(textArea);
        textArea.setColumns(10);

        serverStatusLabel = new JLabel("服务器状态:停止");
        serverStatusLabel.setBounds(15, 458, 134, 21);
        contentPane.add(serverStatusLabel);

        loginedLabel = new JLabel("已登入人数:0");
        loginedLabel.setBounds(163, 459, 134, 21);
        contentPane.add(loginedLabel);

        readyedLabel = new JLabel("已准备人数:0");
        readyedLabel.setBounds(308, 459, 134, 21);
        contentPane.add(readyedLabel);
    }

    private void exit() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);


    }

    public void serverEvent(GameServerEvent evt) {
        int type = evt.getMsgType();
        switch (type) { //1=普通消息，2=登入/登出消息，3=准备/取消准备消息
            case 1: {
                textArea.append(evt.getMessage() + "\n");
                break;
            }
            case 2: {
                loginedLabel.setText("已登入人数:" + evt.getMessage());
                break;
            }
            case 3: {
                readyedLabel.setText("已准备人数:" + evt.getMessage());
                break;
            }

        }


    }


    class StarServerAction extends AbstractAction {
//		public StartServerAction() {
//			super("启动");
//
//		}

        public void actionPerformed(ActionEvent evt) {
            try {
                server.addListener(ServerMain.this);
                textArea.setText("");
                server.start();
                stopAction.setEnabled(true);
                this.setEnabled(false);
                textArea.append("服务器启动成功\n");
                serverStatusLabel.setText("服务器状态:在线");
                System.out.println("服务器启动成功");
            } catch (Exception ex) {
                textArea.append("服务器启动错误\n");
                server.removeListense(ServerMain.this);
                ex.printStackTrace();
                return;
            }
        }

    }

    class StopServerAction extends AbstractAction {
        public StopServerAction() {
            super("停止");
            //putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("../images/stop.gif")));
            //putValue(Action.SHORT_DESCRIPTION, "停止聊天服务器");
            //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control O"));
            this.setEnabled(false);
        }

        public void actionPerformed(ActionEvent arg0) {
            try {
                server.stop();
                server.removeListense(ServerMain.this);
                startAction.setEnabled(true);
                textArea.append("服务器停止成功\n");
                serverStatusLabel.setText("服务器状态:停止");
                this.setEnabled(false);
            } catch (Exception e) {
                textArea.append("服务器停止错误\n");
                e.printStackTrace();
                return;
            }
        }
    }
}
