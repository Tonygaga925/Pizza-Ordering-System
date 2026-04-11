package model.command;

import model.Member;
import service.OrderManager;
import service.MenuLoader;
import service.RecommendationService;
import java.util.Scanner;

public class GetRecommendationCommand implements Command {
    private Member member;
    private OrderManager orderManager;
    private MenuLoader menuLoader;
    private Scanner scanner;
    private RecommendationService.MainCallback callback;

    public GetRecommendationCommand(Member member, OrderManager orderManager, MenuLoader menuLoader, Scanner scanner, RecommendationService.MainCallback callback) {
        this.member = member;
        this.orderManager = orderManager;
        this.menuLoader = menuLoader;
        this.scanner = scanner;
        this.callback = callback;
    }

    @Override
    public void execute() {
        RecommendationService recommendationService = new RecommendationService(orderManager, menuLoader, scanner);
        recommendationService.setCallback(callback);
        recommendationService.getRecommendation(member, true);
    }

    @Override
    public void undo() {
        // Read-only
    }

    @Override
    public String getDescription() {
        return "Get Pizza Recommendation";
    }
}
