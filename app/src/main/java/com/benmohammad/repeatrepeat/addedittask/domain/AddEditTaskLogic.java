package com.benmohammad.repeatrepeat.addedittask.domain;

import androidx.annotation.NonNull;

import com.benmohammad.repeatrepeat.addedittask.domain.AddEditTaskEvent.TaskDefinitionCompleted;
import com.benmohammad.repeatrepeat.data.Task;
import com.benmohammad.repeatrepeat.data.TaskDetails;
import com.spotify.mobius.Next;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.spotify.mobius.Effects.effects;

public class AddEditTaskLogic {

    @NonNull
    public static Next<AddEditTaskModel, AddEditTaskEffect> update(
            AddEditTaskModel model, AddEditTaskEvent event) {
        return event.map(
                taskDefinitionCompleted -> onTaskDefinitionCompleted(model, taskDefinitionCompleted),
                taskCreatedSuccessfully -> exitWithSuccess(),
                taskCreationFailed -> exitWithFailure(),
                taskUpdatedSuccessfully -> exitWithSuccess(),
                taskUpdateFailed -> exitWithFailure());

    }

    private static Next<AddEditTaskModel, AddEditTaskEffect> onTaskDefinitionCompleted(
            AddEditTaskModel model, TaskDefinitionCompleted definitionCompleted) {
        String title = definitionCompleted.title().trim();
        String description = definitionCompleted.description().trim();

        if(isNullOrEmpty(title) && isNullOrEmpty(description)) {
            return Next.dispatch(effects(AddEditTaskEffect.notifyEmptyTaskNotAllowed()));
        }

        TaskDetails details = model.details().toBuilder().title(title).description(description).build();
        AddEditTaskModel newModel = model.withDetails(details);

        return newModel
                .mode()
                .map(
                        create -> Next.next(newModel, effects(AddEditTaskEffect.createTask(newModel.details()))),
                        update -> Next.next(newModel, effects(AddEditTaskEffect.saveTask(Task.create(update.id(), newModel.details())))));
    }

    private static Next<AddEditTaskModel, AddEditTaskEffect> exitWithSuccess() {
        return Next.dispatch(effects(AddEditTaskEffect.exit(true)));
    }

    private static Next<AddEditTaskModel, AddEditTaskEffect> exitWithFailure() {
        return Next.dispatch(effects(AddEditTaskEffect.exit(false)));
    }
}
