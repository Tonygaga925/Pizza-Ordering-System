package model;

public class VIPState implements MemberState {
    @Override
    public void addPoints(Member member, int points) {
        member.setPoints(member.getPoints() + points);
    }

    @Override
    public double getDiscount() {
        return 0.1;
    }

    @Override
    public String getLevelName() {
        return "VIP";
    }
}