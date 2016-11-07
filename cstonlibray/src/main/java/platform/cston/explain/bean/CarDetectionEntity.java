package platform.cston.explain.bean;

import java.util.HashMap;

import cston.cstonlibray.R;
import platform.cston.httplib.Cston;


/**
 * Created by daifei on 2016/7/6.
 */
public class CarDetectionEntity {

    public static final int DETECTION_IDLE = 0;

    public static final int DETECTION_ING = 1;

    public static final int DETECTION_NORMAL = 2;

    public static final int DETECTION_WARNING = 3;

    public static final int DETECTION_ERROR = 4;

    public static final int DETECTION_NO_SUPPORT = 5;

    // 车检测-车辆故障
    public static final String TYPE_DETECTION_CAR_FAULT = "fault";

    // 车检测-车辆故障
    public static final String TYPE_DETECTION_CAR_FAULT_EX = "faultex";

    // 车检测-车辆蓄电池
    public static final String TYPE_DETECTION_CAR_BATTERY = "battery";

    // 车检测-冷却液温度
    public static final String TYPE_DETECTION_CAR_TEMPERATURE = "coolant";

    public int drawable;

    public String pType;

    public String title;

    public int state;

    private static HashMap<String, String> titleMap;

    static {
        titleMap = new HashMap<>();
        titleMap.put(TYPE_DETECTION_CAR_FAULT, getString(R.string.cst_platform_detect_type_fault));
        titleMap.put(TYPE_DETECTION_CAR_FAULT_EX, getString(R.string.cst_platform_detect_type_fault));
        titleMap.put(TYPE_DETECTION_CAR_BATTERY, getString(R.string.cst_platform_detect_type_battery));
        titleMap.put(TYPE_DETECTION_CAR_TEMPERATURE, getString(R.string.cst_platform_data_item_title_enc));
    }

    public static String getTitle(String type) {
        String title = titleMap.get(type);
        if (title == null) {
            title = "";
        }
        return title;
    }
    private static String getString(int id){
        return Cston.app().getString(id);
    }
}
