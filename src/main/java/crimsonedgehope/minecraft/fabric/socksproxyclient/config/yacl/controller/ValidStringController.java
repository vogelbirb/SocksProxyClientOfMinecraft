package crimsonedgehope.minecraft.fabric.socksproxyclient.config.yacl.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.controllers.string.IStringController;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.function.Predicate;

public class ValidStringController implements IStringController<String> {

    public static final Predicate<String> VALIDITY = s -> !(Objects.isNull(s) || s.isBlank() || s.isEmpty());

    private final Option<String> option;
    private final Predicate<String> validityPredication;

    public ValidStringController(Option<String> option, Predicate<String> validityPredication) {
        this.option = option;
        this.validityPredication = validityPredication;
    }

    @Override
    public Option<String> option() {
        return option;
    }

    @Override
    public String getString() {
        return option.pendingValue();
    }

    @Override
    public void setFromString(String value) {
        option.requestSet(value);
    }

    @Override
    public boolean isInputValid(String input) {
        return validityPredication.test(input);
    }

    @ApiStatus.Internal
    public static ValidStringController createInternal(Option<String> option, Predicate<String> validityPredication) {
        return new ValidStringController(option, validityPredication);
    }
}
