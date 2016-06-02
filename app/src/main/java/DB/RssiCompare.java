package DB;

import java.util.Comparator;

/**
 * Created by 重书 on 2016/6/1.
 */
public class RssiCompare implements Comparator<MyBlueToothDevice> {
    @Override
    public int compare(MyBlueToothDevice lhs, MyBlueToothDevice rhs) {
        if(lhs.getRssi()>=rhs.getRssi()) return -1;
        else if(lhs.getRssi()<rhs.getRssi()) return 1;
        else return 0;
    }
}
