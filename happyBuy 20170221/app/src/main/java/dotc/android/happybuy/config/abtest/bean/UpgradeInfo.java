package dotc.android.happybuy.config.abtest.bean;

import dotc.android.happybuy.config.abtest.core.IConfigBean;
import dotc.android.happybuy.config.abtest.core.MultiLang;

/**
 * Created by wangjun on 16/11/30.
 */

public class UpgradeInfo  {

    public String version;
    public String segment_id;
    public int version_code;
    public String version_name;
    public MultiLang title;
    public MultiLang message;
    public boolean forceupdate;
    public String download_url;
    public String gp_link;
    public String file_size;
    public String file_md5;
    public int notify_times_day_max;

    @Override
    public String toString() {
        return "UpgradeInfo{" +
                "version='" + version + '\'' +
                ", segment_id='" + segment_id + '\'' +
                ", version_code=" + version_code +
                ", version_name='" + version_name + '\'' +
                ", title=" + title +
                ", message=" + message +
                ", forceupdate=" + forceupdate +
                ", download_url='" + download_url + '\'' +
                ", gp_link='" + gp_link + '\'' +
                ", file_size='" + file_size + '\'' +
                ", file_md5='" + file_md5 + '\'' +
                ", notify_times_day_max='" + notify_times_day_max + '\'' +
                '}';
    }
}
