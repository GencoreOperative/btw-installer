package uk.co.gencoreoperative.btw.utils.os;

import com.apple.eawt.Application;
import uk.co.gencoreoperative.btw.utils.OSUtils;

import java.awt.*;

/**
 * MacOS specific user interface configuration and customisation.
 */
public class AppleUtils implements OSUtils {
    @Override
    public void setIcon(Image icn) {
        final Application application = Application.getApplication();
        application.setDockIconImage(icn);
    }
}
