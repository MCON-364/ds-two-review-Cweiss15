package edu.touro.mcon364.finalreview.orderflowhandoff.exercises;

import edu.touro.mcon364.finalreview.model.Action;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Stack;

/**
 * In-class Exercise 1 — Action History
 *
 * A simple editor needs to remember actions so the user can undo and redo work.
 *
 * Requirements:
 * - perform(action) records a newly completed action.
 * - undo() removes and returns the action that should be undone next.
 * - redo() removes and returns the action that should be redone next.
 * - undo() returns Optional.empty() when there is nothing available to undo.
 * - redo() returns Optional.empty() when there is nothing available to redo.
 * - performing a new action after one or more undo operations makes the old redo path invalid.
 * - getUndoCount() returns how many actions are currently available to undo.
 * - getRedoCount() returns how many actions are currently available to redo.
 *
 * You may add private fields and private helper methods.
 * Do not change the public method signatures.
 * Before coding, decide:
 * - What information does this class need to remember?
 * - What is the appropriate data structure
 * - Which operation should be fastest?
 * - When an action is undone, where should it go so it can be redone later?
 * - What should happen to redo history after a brand-new action is performed?

 */
public class ActionHistory {
    Stack<Action> undo = new Stack<>();
    Stack<Action> redo = new Stack<>();

    public void perform(Action action) {
        undo.push(action);
        redo.clear();
    }

    public Optional<Action> undo() {
        if(!undo.isEmpty()) {
            Action toUndo = undo.pop();
            redo.push(toUndo);
            return Optional.of(toUndo);
        }
        return Optional.empty();
    }

    public Optional<Action> redo() {
        if(!redo.isEmpty()) {
            Action toRedo = redo.pop();
            undo.push(toRedo);
            return Optional.of(toRedo);
        }
        return Optional.empty();
    }

    public int getUndoCount() {
        return undo.size();
    }

    public int getRedoCount() {
        return redo.size();
    }
}
