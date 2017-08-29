package uk.co.gencoreoperative.btw;

import java.util.function.Function;
import java.util.function.Predicate;

import static java.text.MessageFormat.format;

public class ActionBuilder<T, R> {
    Then then = new Then();
    public ActionBuilder<T, R> description(String name) {
        then.name = name;
        return this;
    }

    public ActionBuilder<T, R> with(T item) {
        then.item = item;
        return this;
    }

    public Then action(Function<T, R> action) {
        then.action = action;
        return then;
    }

    public class Then {
        private String name;
        private T item;
        private Function<T, R> action;
        public void validate(Predicate<R> validate) {
            R result = action.apply(item);
            if (!validate.test(result)) {
                System.err.println(format("✗ {0} failed", name));
                System.exit(-1);
            }
            System.out.println(format("✓ {0}", name));
        }
    }
}
