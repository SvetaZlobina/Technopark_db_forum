package api.models;


public class Vote {
    public enum VoteStatus {VOTE, UNVOTE}

    private String nickname;
    private Integer voice;

    public VoteStatus getStatus() {
        if (voice.equals(1)) {
            return VoteStatus.VOTE;
        } else {
            return VoteStatus.UNVOTE;
        }
    }

    public Integer getVoice() {
        return voice;
    }

    public void setVoice(Integer voice) {
        this.voice = voice;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
