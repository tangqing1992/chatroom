package www.tq.chatroom;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import www.tq.chatroom.net.ClientUnit;
import www.tq.chatroom.net.ServiceUnit;
import www.tq.chatroom.unit.ConfigAddressUnit;
import www.tq.chatroom.unit.DataUnit;
import www.tq.chatroom.unit.NetWorkUnit;
import www.tq.chatroom.unit.ThreadUnit;

public class MainActivity extends AppCompatActivity {

    private TextView text_revice;
    private Button bnt_send;
    private  String string = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        text_revice = findViewById(R.id.text_revice);
        bnt_send = findViewById(R.id.bnt_send);
        string = "address = "+ NetWorkUnit.getIP(MainActivity.this);
        ServiceUnit.getInstance().setOnServiceListener(onServiceListener);
        ServiceUnit.getInstance().start();
        ConfigAddressUnit.getInstance();

        bnt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sendstr = " data ["+DataUnit.getInstance().getData("HH:mm:ss")+"]";
                ClientUnit.getInstance().setOnSendLinstener(onSendLinstener);
                ClientUnit.getInstance().sendMsg(ConfigAddressUnit.getInstance().getServiceip(),
                        ConfigAddressUnit.getInstance().getServiceport(),sendstr);
            }
        });

    }
    ClientUnit.OnSendLinstener onSendLinstener = new ClientUnit.OnSendLinstener() {
        @Override
        public void onSuccess(String msg) {
            string = string+"\n\n" +msg;
            Message message = new Message();
            message.what = 0;
            handler.sendMessage(message);
        }

        @Override
        public void onFail(String msg) {
            string = string+"\n\n" +" send Fail:"+msg;
            Message message = new Message();
            message.what = 0;
            handler.sendMessage(message);
        }
    };

    ServiceUnit.OnServiceListener onServiceListener = new ServiceUnit.OnServiceListener() {
        @Override
        public void onReceive(String msg) {
            string = string+"\n\n"  +msg;
            Message message = new Message();
            message.what = 0;
            handler.sendMessage(message);
        }

        @Override
        public void onFail(String msg) {
            string = string+"\n\n"+msg;
            Message message = new Message();
            message.what = 0;
            handler.sendMessage(message);
        }
    };

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    text_revice.setText(string);
                    break;
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ServiceUnit.getInstance().onDestroy();
        ThreadUnit.getInstance().shutdown();
        ConfigAddressUnit.getInstance().onDestroy();
    }
}
