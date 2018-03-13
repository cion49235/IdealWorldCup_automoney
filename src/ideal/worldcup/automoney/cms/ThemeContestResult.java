package ideal.worldcup.automoney.cms;


import java.io.Serializable;
import java.util.List;

public class ThemeContestResult extends BaseModel
                        implements Serializable{
    public int ct_seq;
    public String ct_type;
    public String ct_title;
    public int ct_cnt_target;
    public int ct_cnt_participate;
    public String ct_reward_txt;
    public int ct_reward_point;
    public String ct_hidden;
    public int ct_age_from;
    public int ct_age_to;
    public String ct_sex;
    public String end_type;
    public int end_cnt;
    public String end_text;
    public String pub_date;
    public String end_date;
    public String reg_date;
    public List<ThemeContestItem> list_item;
    public List<ThemeOtherContestList> list_other;
    public List<ThemeBoardList> list_reply;

    @Override
    public String toString() {
        return "ThemeContestResult{" +
                "ct_seq=" + ct_seq +
                ", ct_type='" + ct_type + '\'' +
                ", ct_title='" + ct_title + '\'' +
                ", ct_cnt_target=" + ct_cnt_target +
                ", ct_reward_txt='" + ct_reward_txt + '\'' +
                ", ct_reward_point=" + ct_reward_point +
                ", ct_hidden='" + ct_hidden + '\'' +
                ", ct_age_from=" + ct_age_from +
                ", ct_age_to=" + ct_age_to +
                ", ct_sex='" + ct_sex + '\'' +
                ", end_type='" + end_type + '\'' +
                ", end_cnt=" + end_cnt +
                ", end_text='" + end_text + '\'' +
                ", pub_date='" + pub_date + '\'' +
                ", end_date='" + end_date + '\'' +
                ", reg_date='" + reg_date + '\'' +
                ", list_item=" + list_item +
                ", list_other=" + list_other +
                ", list_reply=" + list_reply +
                '}';
    }
}
