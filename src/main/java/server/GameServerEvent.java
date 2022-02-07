package server;

import java.util.EventObject;

//服务器事件
public class GameServerEvent extends EventObject {
    int msgType;   //1=普通消息，2=登入消息，3=登出消息 4=准备消息 5=取消准备消息
    String message;

    public GameServerEvent(Object src, String message,int msgType) {
        super(src);
        setMessage(message);
        setMsgType(msgType);
    }

    public String getMessage() {
        return message;
    }


    public int getMsgType() {
        return msgType;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }



}
