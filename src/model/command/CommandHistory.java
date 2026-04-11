package model.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CommandHistory {
    private Stack<Command> undoStack;
    private Stack<Command> redoStack;
    private List<String> selectedToppings;  // Track selected toppings to prevent duplicates
    
    public CommandHistory() {
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
        this.selectedToppings = new ArrayList<>();
    }
    
    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();  // Clear redo stack when new command is executed
        
        // Track selected topping
        if (command instanceof AddToppingCommand) {
            String toppingName = ((AddToppingCommand) command).getToppingName();
            selectedToppings.add(toppingName);
        }
    }
    
    public boolean undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
            
            // Remove from selected toppings
            if (command instanceof AddToppingCommand) {
                String toppingName = ((AddToppingCommand) command).getToppingName();
                selectedToppings.remove(toppingName);
            }
            return true;
        }
        return false;
    }
    
    public boolean redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);
            
            // Add back to selected toppings
            if (command instanceof AddToppingCommand) {
                String toppingName = ((AddToppingCommand) command).getToppingName();
                selectedToppings.add(toppingName);
            }
            return true;
        }
        return false;
    }
    
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
    
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public boolean isToppingSelected(String toppingName) {
        return selectedToppings.contains(toppingName);
    }
    

}