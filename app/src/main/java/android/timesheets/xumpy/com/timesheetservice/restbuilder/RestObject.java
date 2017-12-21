package android.timesheets.xumpy.com.timesheetservice.restbuilder;

import java.util.List;
import java.util.Map;

public interface RestObject {
    public String getUrl();
    public Object createObject(Map<String, List<String>> queryParams);
}
