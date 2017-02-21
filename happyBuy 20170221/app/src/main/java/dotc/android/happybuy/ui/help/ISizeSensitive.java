package dotc.android.happybuy.ui.help;

import android.graphics.Point;

public interface ISizeSensitive {
    int getViewHeight();

    int getViewWidth();

    Point location(int type);
}
