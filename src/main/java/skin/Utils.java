package skin;

import javafx.application.Platform;
import javafx.application.ConditionalFeature;
import javafx.scene.text.Font;
import com.sun.javafx.scene.text.TextLayout;
import com.sun.javafx.tk.Toolkit;

/**
 * Created by AliReza on 8/18/2016.
 */
public class Utils {
    static final TextLayout layout = Toolkit.getToolkit().getTextLayoutFactory().createLayout();
    static double computeTextWidth(Font font, String text, double wrappingWidth) {
        layout.setContent(text != null ? text : "", font.impl_getNativeFont());
        layout.setWrapWidth((float)wrappingWidth);
        return layout.getBounds().getWidth();
    }
    public static boolean isTwoLevelFocus() {
        return Platform.isSupported(ConditionalFeature.TWO_LEVEL_FOCUS);
    }
}
