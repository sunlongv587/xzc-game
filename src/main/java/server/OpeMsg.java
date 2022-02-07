package server;

public class OpeMsg {
    /**
     * */
    private static final long serialVersionUID = 1L;
    //操作的信息
    String textmsg;
    int opeGamerID;
    int opeType;	//加入赌局与否(1加入 2PASS)
    public OpeMsg(String textmsg,int opeGamerID,int opeType) {
        setTextmsg(textmsg);
        setOpeGamerID(opeGamerID);
        setOpeType(opeType);
    }

    public void setTextmsg(String textmsg) {
        this.textmsg = textmsg;
    }

    public void setOpeGamerID(int opeGamerID) {
        this.opeGamerID = opeGamerID;
    }
    public void setOpeType(int opeType) {
        this.opeType = opeType;
    }


    public String getTextmsg() {
        return textmsg;
    }

    public int getOpeGamerID() {
        return opeGamerID;
    }

    public int getOpeType() {
        return opeType;
    }

}
