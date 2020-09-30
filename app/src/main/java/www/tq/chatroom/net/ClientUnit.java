package www.tq.chatroom.net;

import android.os.Handler;
import android.os.Message;

import java.io.OutputStream;
import java.net.Socket;

import androidx.annotation.NonNull;
import www.tq.chatroom.unit.ThreadUnit;

public class ClientUnit {

    private volatile  static  ClientUnit clientUnit = null;
    private OnSendLinstener onSendLinstener = null;
    private  ClientUnit(){ }
    public static  ClientUnit getInstance(){

        if (clientUnit==null){
            synchronized (ClientUnit.class){
                if (clientUnit==null)
                    clientUnit = new ClientUnit();
            }
        }
        return  clientUnit;
    }

    public void  setOnSendLinstener(OnSendLinstener onSendLinstener){
        this.onSendLinstener = onSendLinstener;
    }

    private Socket socket = null;
    private OutputStream outputStream  = null;
    public void sendMsg(final String ip, final int port, final String msg){

        String data = "---start-send-"+msg;
        Message message = new Message();
        message.what = 0;
        message.obj = data;
        handler.sendMessage(message);

        ThreadUnit.getInstance().getFixedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                if (socket==null)
                    socket = new Socket(ip,port);
                    outputStream = socket.getOutputStream();
                    System.out.println("---start send : "+msg);
                    System.out.println("---client socket : "+socket);

                    outputStream.write((msg+ "\r\n").getBytes("utf-8"));
                    System.out.println("---send  over : "+msg);
                    //  outputStream.close();
                    String data = "send '"+msg+"' to "
                            +socket.getInetAddress().getHostAddress()+" success";
                    Message message = new Message();
                    message.what = 0;
                    message.obj = data;
                    handler.sendMessage(message);
                }catch (Exception e){
                    e.getMessage();
                    String error = "error["+e.getMessage()+"]";
                    Message message = new Message();
                    message.what = 1;
                    message.obj = error;
                    handler.sendMessage(message);
                }
            }
        });
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case  0:
                    onSendLinstener.onSuccess((String)msg.obj);
                    break;
                case 1:
                    onSendLinstener.onFail((String)msg.obj);
                    break;
            }
        }
    };


    public void onDestroy(){

        try {
            if (outputStream!=null)
                outputStream.close();
            if (socket!=null)
                socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        clientUnit = null;
    }

    public  interface  OnSendLinstener{
        void onSuccess(String msg);
        void onFail(String msg);
    }
}
