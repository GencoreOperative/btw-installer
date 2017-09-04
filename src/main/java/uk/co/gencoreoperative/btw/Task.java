/*
 * Copyright 2017 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package uk.co.gencoreoperative.btw;

import java.util.function.Predicate; /**
 * A task performs some action and includes the validation step required to
 * validate that it was completed successfully.
 *
 * Once a task has completed processing, and was successful, then it can call
 * any chained tasks that are associated to it.
 */
public interface Task<T> {
    T action();

    default T thenValidate(Tasks item, Predicate<T> predicate) {
        T result = action();
        if (predicate.test(result)) {
            item.getTask().success();
        } else {
            item.getTask().failed();
            System.exit(-1); // TODO: Not this...
        }
        return result;
    }
}
