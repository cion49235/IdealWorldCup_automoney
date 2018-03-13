package ideal.worldcup.automoney.cms;

import java.io.Serializable;
import java.util.List;

public class ThemeContest extends BaseModel
                        implements Serializable{
    public int ct_seq;
    public String ct_type;
    public String ct_title;
    public int ct_cnt_target;
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
    public List<ThemeContestItem> list;
}
