package www.tq.chatroom.net;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import androidx.annotation.NonNull;
import www.tq.chatroom.unit.ThreadUnit;

public class ServiceUnit {

    /* 使用volatile可以禁止JVM的指令重排,保证在多线程环境下也能正常运行.
    指令重排在单线程环境下不会出现问题,但是在多线程环境下会导致一个线程获得,
    还没有初始化的实例.*/
    private volatile static ServiceUnit serviceUnit = null;
    private int port = 5566;
    private OnServiceListener onServiceListener = null;
    private boolean isrevice = true;

    private ServiceUnit(){}

    public static ServiceUnit getInstance(){
    /*第一个if语句用来避免uniqueInstance已经被实例化之后的加锁操作*/
        if (serviceUnit==null){
            /*静态,同步用.class字节码文件*/
            synchronized (ServiceUnit.class){
                /*第二个if语句进行了加锁,所以只能有一个线程进入,
                就不会出现uniqueInstance == null时两个线程同时进行实例化操作.*/
                if (serviceUnit==null) {
                    serviceUnit = new ServiceUnit();
                }
            }
        }
        return  serviceUnit;
    }

    public void setPort(int port){
        this.port = port;
    }

    public void setOnServiceListener(OnServiceListener onServiceListener){
        this.onServiceListener = onServiceListener;
    }

    public void start(){

        isrevice = true;
        ThreadUnit.getInstance().getSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {

                try {

                   ServerSocket serverSocket = new ServerSocket(port);
                    Message message = new Message();
                    message.what = 0;
                    message.obj = "wait receive ... ";
                    handler.sendMessage(message);

                    while (isrevice){
                        Socket socket = serverSocket.accept();
                        System.out.println("----server---socket="+socket);

                        try {

                            System.out.println("---wait revice----");
                            InputStream inputStream = socket.getInputStream();
                            System.out.println("----revice get input---");

                            byte buffer1[] = new byte[1024*4];
                            int temp = 0;
                            // 从InputStream当中读取客户端所发送的数据
                            while ((temp = inputStream.read(buffer1)) != -1) {
                                String data = new String(buffer1, 0, temp);
                                System.out.println("----revice---data="+data);
                                System.out.println("----revice---temp="+temp);

                                Message message2 = new Message();
                                message2.obj = "receive: "+data;
                                message2.what = 0;
                                handler.sendMessage(message2);
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    Message message1 = new Message();
                    message1.what = 0;
                    message1.obj = "** receive ... over  ";
                    handler.sendMessage(message1);
                    //serverSocket.close();
                }catch (Exception e){
                    e.getMessage();
                    String error = "onReceive Fail: error["+e.getMessage()+"]";
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
                    onServiceListener.onReceive((String)msg.obj);
                    break;
                case 1:
                    onServiceListener.onFail((String)msg.obj);
                    break;
            }
        }
    };

    public void stop(){
        isrevice = false;
    }

    public interface  OnServiceListener{
        void onReceive(String msg);
        void onFail(String msg);
    }

    public void onDestroy(){
        stop();
        serviceUnit = null;
    }
}
