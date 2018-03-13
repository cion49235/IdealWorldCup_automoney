package ideal.worldcup.automoney.cms;

public class ThemeContestListItem extends BaseModel {
    public int ct_seq;
    public String ct_type;
    public String ct_title;
    public String ma_code;
    public String nickname;
    public String ct_reward_txt;
    public String ct_end_txt;
    public String end_text;
    public String ct_status;
    public String file_name_1;
    public String file_name_2;
    public String ct_hidden;
    public String ct_once_participate;
    public int ct_cnt_target;
    public int cnt_my_participate;
    public int ct_cnt_participate;
    public String reg_date;
    public int ct_reply_cnt;

    @Override
    public String toString() {
        return "ThemeContestListItem{" +
                "ct_seq=" + ct_seq +
                ", ct_type='" + ct_type + '\'' +
                ", ct_title='" + ct_title + '\'' +
                ", ma_code='" + ma_code + '\'' +
                ", nickname='" + nickname + '\'' +
                ", ct_reward_txt='" + ct_reward_txt + '\'' +
                ", ct_end_txt='" + ct_end_txt + '\'' +
                ", end_text='" + end_text + '\'' +
                ", ct_status='" + ct_status + '\'' +
                ", file_name_1='" + file_name_1 + '\'' +
                ", file_name_2='" + file_name_2 + '\'' +
                ", ct_hidden='" + ct_hidden + '\'' +
                ", ct_once_participate='" + ct_once_participate + '\'' +
                ", ct_cnt_target=" + ct_cnt_target +
                ", cnt_my_participate=" + cnt_my_participate +
                ", ct_cnt_participate=" + ct_cnt_participate +
                ", ct_reply_cnt=" + ct_reply_cnt +
                ", reg_date='" + reg_date + '\'' +
                "} " + super.toString();
    }
}
