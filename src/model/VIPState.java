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

    @Override
    public int getPointsToNextLevel(int currentPoints, int threshold) {
        // VIP is already the highest tier, so they need 0 points
        return 0; 
    }
}