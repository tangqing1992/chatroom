package www.tq.chatroom.unit;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUnit {

    private volatile  static  DataUnit clientUnit = null;
    private  DataUnit(){ }
    public static  DataUnit getInstance(){

        if (clientUnit==null){
            synchronized (DataUnit.class){
                if (clientUnit==null)
                    clientUnit = new DataUnit();
            }
        }
        return  clientUnit;
    }

    public  String getData(String format){
        SimpleDateFormat formatter   =  new  SimpleDateFormat(format);
        Date curDate =  new Date(System.currentTimeMillis());
        String   str   =   formatter.format(curDate);
        return  str;
    }
}
