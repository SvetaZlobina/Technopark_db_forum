package api.models;


public class VoteModel {
    public enum VoteState {VOTE, UNVOTE}

    private String nickname;
    private Integer voice;

    public VoteState getStatus() {
        if (voice.equals(1)) {
            return VoteState.VOTE;
        } else {
            return VoteState.UNVOTE;
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
