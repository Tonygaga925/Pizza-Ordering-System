package model;

public interface MemberState {
    void addPoints(Member member, int points);
    double getDiscount();
    String getLevelName();
}