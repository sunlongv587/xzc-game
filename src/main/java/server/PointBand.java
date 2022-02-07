package server;

public class PointBand {
    //计分版
    int winner;//本回合赢家
    boolean[] inorpass = new boolean[3];//参与与否
    int[] finalPoint = new int[3];//计入小早川后点数

    public PointBand(int winner,boolean[] inorpass,int[] finalPoint) {
        setWinner(winner);
        setInorpass(inorpass);
        setFinalPoint(finalPoint);
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public void setInorpass(boolean[] inorpass) {
        this.inorpass = inorpass;
    }
    public void setFinalPoint(int[] finalPoint) {
        this.finalPoint = finalPoint;
    }

    public int getWinner() {
        return winner;
    }

    public boolean getInorpass(int num) {
        return inorpass[num];
    }

    public int getFinalPoint(int num) {
        return finalPoint[num];
    }
}
