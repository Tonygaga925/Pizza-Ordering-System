package model;

public class NormalState implements MemberState {
    private static final int VIP_THRESHOLD = 1000;

    @Override
    public void addPoints(Member member, int points) {
        int newPoints = member.getPoints() + points;
        member.setPoints(newPoints);

        if (newPoints >= VIP_THRESHOLD) {
            member.setState(new VIPState());
            System.out.println("Congratulations! You have been upgraded to VIP member!");
        }
    }

    @Override
    public double getDiscount() {
        return 0.0;
    }

    @Override
    public String getLevelName() {
        return "NORMAL";
    }
}