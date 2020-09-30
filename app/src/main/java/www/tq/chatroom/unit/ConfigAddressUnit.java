package www.tq.chatroom.unit;

public class ConfigAddressUnit {

    private volatile  static  ConfigAddressUnit configAddressUnit = null;
    private String serviceip = null;
    private int serviceport = 0;
    private  ConfigAddressUnit(){
        serviceip = "192.168.0.100";
        serviceport = 5566;
    }
    public String getServiceip(){
        return  serviceip;
    }
    public int getServiceport(){
        return  serviceport;
    }

    public static  ConfigAddressUnit getInstance(){

        if (configAddressUnit==null){
            synchronized (ConfigAddressUnit.class){
                if (configAddressUnit==null)
                    configAddressUnit = new ConfigAddressUnit();
            }
        }
        return  configAddressUnit;
    }

    public void onDestroy(){
        serviceport = 0;
        serviceip = null;
    }
}
